/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as 
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.hellblazer.CoRE.meta.models;

import java.sql.SQLException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.AgencyAttribute;
import com.hellblazer.CoRE.agency.AgencyAttributeAuthorization;
import com.hellblazer.CoRE.agency.AgencyLocationAccessAuthorization;
import com.hellblazer.CoRE.agency.AgencyNetwork;
import com.hellblazer.CoRE.agency.AgencyProductAccessAuthorization;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.meta.AgencyModel;
import com.hellblazer.CoRE.meta.graph.Graph;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public class AgencyModelImpl
        extends
        AbstractNetworkedModel<Agency, AgencyAttributeAuthorization, AgencyAttribute>
        implements AgencyModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new AgencyModelImpl(em));
        }
    }

    private static interface Procedure<T> {
        T call(AgencyModelImpl productModel) throws Exception;
    }

    private static final String AGENCY_NETWORK_PROPAGATE = "agencyNetwork.propagate";

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (!markPropagated(AGENCY_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        execute(new Procedure<Void>() {
            @Override
            public Void call(AgencyModelImpl agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(AgencyModelImpl agencyModel) throws Exception {
                agencyModel.networkEdgeDeleted(data.getOld().getLong("parent"),
                                               data.getOld().getLong("relationship"));
                return null;
            }
        });
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    /**
     * @param em
     */
    public AgencyModelImpl(EntityManager em) {
        super(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public AgencyModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#authorize(com.hellblazer.CoRE
     * .meta.Aspect, com.hellblazer.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Agency> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            AgencyAttributeAuthorization authorization = new AgencyAttributeAuthorization(
                                                                                          aspect.getClassification(),
                                                                                          aspect.getClassifier(),
                                                                                          attribute,
                                                                                          kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network
     * .Networked)
     */
    @Override
    public Agency create(Agency prototype) {
        Agency copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (AgencyNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (AgencyAttribute attribute : prototype.getAttributes()) {
            AgencyAttribute clone = (AgencyAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setAgency(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @Override
    public final Agency create(String name, String description,
                               Aspect<Agency> aspect, Aspect<Agency>... aspects) {
        Agency agency = new Agency(name, description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Agency> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.meta.NetworkedModel#findUnlinkedNodes()
     */
    @Override
    public List<Agency> findUnlinkedNodes() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getImmediateRelationships(com
     * .hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public List<Relationship> getImmediateRelationships(Agency parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getNetwork(com.hellblazer.CoRE
     * .ExistentialRuleform, com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public Graph getNetwork(Agency parent, Relationship relationship) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#getTransitiveRelationships(com
     * .hellblazer.CoRE.ExistentialRuleform)
     */
    @Override
    public List<Relationship> getTransitiveRelationships(Agency parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.hellblazer.CoRE.meta.NetworkedModel#isAccessible(com.hellblazer.CoRE
     * .ExistentialRuleform, com.hellblazer.CoRE.network.Relationship,
     * com.hellblazer.CoRE.network.Relationship,
     * com.hellblazer.CoRE.ExistentialRuleform,
     * com.hellblazer.CoRE.network.Relationship)
     */
    @Override
    public boolean isAccessible(Agency parent, Relationship parentRelationship,
                                Relationship authorizingRelationship,
                                ExistentialRuleform<?, ?> child,
                                Relationship childRelationship) {
        if (parent == null || child == null || authorizingRelationship == null) {
            throw new IllegalArgumentException(
                                               "parent, authorizingRelationship, and child cannot be null");
        }
        if (child instanceof Location) {

            return isLocationAccessible(parent, parentRelationship,
                                        authorizingRelationship,
                                        (Location) child, childRelationship);
        } else if (child instanceof Product) {
            return isProductAccessible(parent, parentRelationship,
                                       authorizingRelationship,
                                       (Product) child, childRelationship);
        } else {
            throw new IllegalArgumentException(
                                               "child type is not supported for this query");
        }

    }

    /**
     * @param parent
     * @param parentRelationship
     * @param authorizingRelationship
     * @param child
     * @param childRelationship
     * @return
     */
    private boolean isLocationAccessible(Agency parent,
                                         Relationship parentRelationship,
                                         Relationship authorizingRelationship,
                                         Location child,
                                         Relationship childRelationship) {
        Query query;

        if (parentRelationship == null && childRelationship == null) {
            query = em.createNamedQuery(AgencyLocationAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD);
            query.setParameter("parent", parent);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
        } else if (childRelationship == null) {
            query = em.createNamedQuery(AgencyLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
            query.setParameter("netRelationship", parentRelationship);
            query.setParameter("netChild", parent);

        } else if (parentRelationship == null) {
            query = em.createNamedQuery(AgencyLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parent", parent);
            query.setParameter("netRelationship", childRelationship);
            query.setParameter("netChild", child);

        } else {
            query = em.createNamedQuery(AgencyLocationAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parentNetRelationship", parentRelationship);
            query.setParameter("parentNetChild", parent);
            query.setParameter("childNetRelationship", childRelationship);
            query.setParameter("childNetChild", child);

        }
        List<?> results = query.getResultList();

        return results.size() > 0;

    }

    /**
     * @param parent
     * @param parentRelationship
     * @param authorizingRelationship
     * @param child
     * @param childRelationship
     * @return
     */
    private boolean isProductAccessible(Agency parent,
                                        Relationship parentRelationship,
                                        Relationship authorizingRelationship,
                                        Product child,
                                        Relationship childRelationship) {
        Query query;

        if (parentRelationship == null && childRelationship == null) {
            query = em.createNamedQuery(AgencyProductAccessAuthorization.FIND_ALL_AUTHS_FOR_PARENT_RELATIONSHIP_CHILD);
            query.setParameter("parent", parent);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
        } else if (childRelationship == null) {
            query = em.createNamedQuery(AgencyProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("child", child);
            query.setParameter("netRelationship", parentRelationship);
            query.setParameter("netChild", parent);

        } else if (parentRelationship == null) {
            query = em.createNamedQuery(AgencyProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parent", parent);
            query.setParameter("netRelationship", childRelationship);
            query.setParameter("netChild", child);

        } else {
            query = em.createNamedQuery(AgencyProductAccessAuthorization.FIND_AUTHS_FOR_INDIRECT_PARENT_AND_CHILD);
            query.setParameter("relationship", authorizingRelationship);
            query.setParameter("parentNetRelationship", parentRelationship);
            query.setParameter("parentNetChild", parent);
            query.setParameter("childNetRelationship", childRelationship);
            query.setParameter("childNetChild", child);

        }
        List<?> results = query.getResultList();

        return results.size() > 0;

    }

    /**
     * @param agency
     * @param aspect
     */
    protected void initialize(Agency agency, Aspect<Agency> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (AgencyAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            AgencyAttribute attribute = new AgencyAttribute(
                                                            authorization.getAuthorizedAttribute(),
                                                            kernel.getCoreModel());
            attribute.setAgency(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
}