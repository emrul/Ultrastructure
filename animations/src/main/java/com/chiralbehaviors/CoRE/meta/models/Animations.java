/**
 * (C) Copyright 2015 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chiralbehaviors.CoRE.meta.models;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.Triggers;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.JobModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.TriggerException;
import com.chiralbehaviors.CoRE.meta.models.openjpa.LifecycleListener;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 *         This class implements the animations logic for the Ultrastructure
 *         model. Abstractly, this logic is driven by state events of an
 *         Ultrastructure instance. Conceptually, this is equivalent to database
 *         triggers. This class models a simple state model of persist, update,
 *         delete style events. The animations model is conceptually simple and
 *         unchanging, thus we don't need a general mechanism of dynamically
 *         registering triggers n' such. We just inline the animation logic in
 *         the state methods, delegating to the appropriate model for
 *         implementation. What this means in practice is that this is the class
 *         that creates the high level logic around state change of an
 *         Ultrastructure instance. This is the high level, disambiguation logic
 *         of Ultrastructure animation.
 *
 *         This is the Rule Engine (tm).
 */
public class Animations implements Triggers {

    private static final int    MAX_JOB_PROCESSING = 10;

    private boolean             inferAgencyNetwork;
    private boolean             inferAttributeNetwork;
    private boolean             inferIntervalNetwork;
    private boolean             inferLocationNetwork;
    private boolean             inferProductNetwork;
    private boolean             inferRelationshipNetwork;
    private boolean             inferStatusCodeNetwork;
    private boolean             inferUnitNetwork;
    private final List<Job>     jobs               = new ArrayList<>();
    private final Model         model;
    private final Set<Product>  modifiedServices   = new HashSet<>();
    private final EntityManager em;

    public Animations(Model model, EntityManager em) {
        this.model = model;
        this.em = em;
        new LifecycleListener(this, em);
    }

    public void commit() throws TriggerException {
        flush();
        reset();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.agency.Agency)
     */
    @Override
    public void delete(Agency a) {
        inferAgencyNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.agency.AgencyNetwork)
     */
    @Override
    public void delete(AgencyNetwork a) {
        inferAgencyNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public void delete(Attribute a) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.AttributeNetwork)
     */
    @Override
    public void delete(AttributeNetwork a) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.time.Interval)
     */
    @Override
    public void delete(Interval i) {
        inferIntervalNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.time.IntervalNetwork)
     */
    @Override
    public void delete(IntervalNetwork i) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.location.Location)
     */
    @Override
    public void delete(Location l) {
        inferLocationNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.location.LocationNetwork)
     */
    @Override
    public void delete(LocationNetwork l) {
        inferLocationNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.network.NetworkInference)
     */
    @Override
    public void delete(NetworkInference inference) {
        propagateAll();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.product.Product)
     */
    @Override
    public void delete(Product p) {
        inferProductNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.product.ProductNetwork)
     */
    @Override
    public void delete(ProductNetwork p) {
        inferProductNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.network.Relationship)
     */
    @Override
    public void delete(Relationship r) {
        inferRelationshipNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.network.RelationshipNetwork)
     */
    @Override
    public void delete(RelationshipNetwork r) {
        inferRelationshipNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.event.status.StatusCode)
     */
    @Override
    public void delete(StatusCode s) {
        inferStatusCodeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork)
     */
    @Override
    public void delete(StatusCodeNetwork s) {
        inferStatusCodeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.unit.Unit)
     */
    @Override
    public void delete(Unit u) {
        inferUnitNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#delete(com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork)
     */
    @Override
    public void delete(UnitNetwork u) {
        inferUnitNetwork = true;
    }

    public void flush() {
        em.flush();
        try {
            model.getJobModel().validateStateGraph(modifiedServices);
        } catch (SQLException e) {
            throw new TriggerException(
                                       "StatusCodeSequencing validation failed",
                                       e);
        }
        propagate();
        int cycles = 0;
        while (!jobs.isEmpty()) {
            if (cycles > MAX_JOB_PROCESSING) {
                throw new IllegalStateException(
                                                "Processing more inserted job cycles than the maximum number of itterations allowed");
            }
            cycles++;
            List<Job> inserted = new ArrayList<>(jobs);
            jobs.clear();
            for (Job j : inserted) {
                process(j);
            }
        }
        em.flush();
    }

    public EntityManager getEm() {
        return model.getEntityManager();
    }

    /**
     * @return
     */
    public Model getModel() {
        return model;
    }

    public void log(StatusCodeSequencing scs) {
        modifiedServices.add(scs.getService());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.agency.AgencyNetwork)
     */
    @Override
    public void persist(AgencyNetwork a) {
        inferAgencyNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.attribute.AttributeNetwork)
     */
    @Override
    public void persist(AttributeNetwork a) {
        inferAttributeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.time.IntervalNetwork)
     */
    @Override
    public void persist(IntervalNetwork i) {
        inferIntervalNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.Job)
     */
    @Override
    public void persist(Job j) {
        TypedQuery<Integer> query = em.createNamedQuery(JobChronology.HIGHEST_SEQUENCE_FOR_JOB,
                                                        Integer.class);
        query.setParameter("job", j);
        if (query.getSingleResult() == null) {
            model.getJobModel().log(j, "Initial insertion of job");
        }
        jobs.add(j);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.location.LocationNetwork)
     */
    @Override
    public void persist(LocationNetwork l) {
        inferLocationNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization)
     */
    @Override
    public void persist(ProductChildSequencingAuthorization pcsa) {
        try {
            model.getJobModel().ensureValidServiceAndStatus(pcsa.getNextChild(),
                                                            pcsa.getNextChildStatus());
        } catch (SQLException e) {
            throw new TriggerException("Invalid sequence", e);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.product.ProductNetwork)
     */
    @Override
    public void persist(ProductNetwork p) {
        inferProductNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization)
     */
    @Override
    public void persist(ProductParentSequencingAuthorization ppsa) {
        try {
            model.getJobModel().ensureValidServiceAndStatus(ppsa.getParent(),
                                                            ppsa.getParentStatusToSet());
        } catch (SQLException e) {
            throw new TriggerException("Invalid sequence", e);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization)
     */
    @Override
    public void persist(ProductSelfSequencingAuthorization pssa) {
        try {
            model.getJobModel().ensureValidServiceAndStatus(pssa.getService(),
                                                            pssa.getStatusToSet());
        } catch (SQLException e) {
            throw new TriggerException("Invalid sequence", e);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization)
     */
    @Override
    public void persist(ProductSiblingSequencingAuthorization pssa) {
        try {
            model.getJobModel().ensureValidServiceAndStatus(pssa.getNextSibling(),
                                                            pssa.getNextSiblingStatus());
        } catch (SQLException e) {
            throw new TriggerException("Invalid sequence", e);
        }
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.network.RelationshipNetwork)
     */
    @Override
    public void persist(RelationshipNetwork r) {
        inferRelationshipNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork)
     */
    @Override
    public void persist(StatusCodeNetwork sc) {
        inferStatusCodeNetwork = true;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing)
     */
    @Override
    public void persist(StatusCodeSequencing scs) {
        modifiedServices.add(scs.getService());
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#persist(com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork)
     */
    @Override
    public void persist(UnitNetwork u) {
        inferUnitNetwork = true;
    }

    public void rollback() {
        reset();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.Triggers#update(com.chiralbehaviors.CoRE.event.Job)
     */
    @Override
    public void update(Job j) {
        jobs.add(j);
    }

    private void clearPropagation() {
        inferIntervalNetwork = false;
        inferRelationshipNetwork = false;
        inferStatusCodeNetwork = false;
        inferUnitNetwork = false;
        inferProductNetwork = false;
        inferLocationNetwork = false;
        inferAttributeNetwork = false;
        inferAgencyNetwork = false;
        inferRelationshipNetwork = false;
    }

    private void process(Job j) {
        JobModel jobModel = model.getJobModel();
        jobModel.generateImplicitJobsForExplicitJobs(j,
                                                     model.getKernel().getCoreAnimationSoftware());
        jobModel.processJobSequencing(j);
    }

    private void propagate() {
        if (inferAgencyNetwork) {
            model.getAgencyModel().propagate();
        }
        if (inferAttributeNetwork) {
            model.getAttributeModel().propagate();
        }
        if (inferIntervalNetwork) {
            model.getIntervalModel().propagate();
        }
        if (inferLocationNetwork) {
            model.getLocationModel().propagate();
        }
        if (inferProductNetwork) {
            model.getProductModel().propagate();
        }
        if (inferRelationshipNetwork) {
            model.getRelationshipModel().propagate();
        }
        if (inferStatusCodeNetwork) {
            model.getStatusCodeModel().propagate();
        }
        if (inferUnitNetwork) {
            model.getUnitModel().propagate();
        }
    }

    private void propagateAll() {
        inferIntervalNetwork = true;
        inferRelationshipNetwork = true;
        inferStatusCodeNetwork = true;
        inferUnitNetwork = true;
        inferProductNetwork = true;
        inferLocationNetwork = true;
        inferAttributeNetwork = true;
        inferAgencyNetwork = true;
        inferRelationshipNetwork = true;
    }

    private void reset() {
        clearPropagation();
        modifiedServices.clear();
        jobs.clear();
    }
}
