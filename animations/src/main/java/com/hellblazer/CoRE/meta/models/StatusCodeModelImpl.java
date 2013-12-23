/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeAttribute;
import com.hellblazer.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.hellblazer.CoRE.event.status.StatusCodeNetwork;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.meta.StatusCodeModel;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;

/**
 * @author hhildebrand
 * 
 */
public class StatusCodeModelImpl
        extends
        AbstractNetworkedModel<StatusCode, StatusCodeNetwork, StatusCodeAttributeAuthorization, StatusCodeAttribute>
        implements StatusCodeModel {

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new StatusCodeModelImpl(em));
        }
    }

    private static interface Procedure<T> {
        T call(StatusCodeModelImpl productModel) throws Exception;
    }

    private static final String STATUS_CODE_NETWORK_PROPAGATE = "statusCodeNetwork.propagate";

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (!markPropagated(STATUS_CODE_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        execute(new Procedure<Void>() {
            @Override
            public Void call(StatusCodeModelImpl agencyModel) throws Exception {
                agencyModel.propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(StatusCodeModelImpl agencyModel) throws Exception {
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
    public StatusCodeModelImpl(EntityManager em) {
        super(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public StatusCodeModelImpl(EntityManager em, Kernel kernel) {
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
    public void authorize(Aspect<StatusCode> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            StatusCodeAttributeAuthorization authorization = new StatusCodeAttributeAuthorization(
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
    public StatusCode create(StatusCode prototype) {
        StatusCode copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (StatusCodeNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (StatusCodeAttribute attribute : prototype.getAttributes()) {
            StatusCodeAttribute clone = (StatusCodeAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setStatusCode(copy);
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
    public final StatusCode create(String name, String description,
                                   Aspect<StatusCode> aspect,
                                   Aspect<StatusCode>... aspects) {
        StatusCode agency = new StatusCode(name, description,
                                           kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<StatusCode> a : aspects) {
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
    public List<StatusCode> findUnlinkedNodes() {
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
    public List<Relationship> getTransitiveRelationships(StatusCode parent) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @param agency
     * @param aspect
     */
    protected void initialize(StatusCode agency, Aspect<StatusCode> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (StatusCodeAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            StatusCodeAttribute attribute = new StatusCodeAttribute(
                                                                    authorization.getAuthorizedAttribute(),
                                                                    kernel.getCoreModel());
            attribute.setStatusCode(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.StatusCodeModel#getStatusCodes(com.hellblazer.CoRE.product.Product)
     */
    @Override
    public Collection<StatusCode> getStatusCodes(Product service) {
        Set<StatusCode> codes = new HashSet<StatusCode>();
        TypedQuery<StatusCode> query = em.createNamedQuery(StatusCodeSequencing.GET_PARENT_STATUS_CODES,
                                                           StatusCode.class);
        query.setParameter("service", service);
        codes.addAll(query.getResultList());
        query = em.createNamedQuery(StatusCodeSequencing.GET_CHILD_STATUS_CODES,
                                    StatusCode.class);
        query.setParameter("service", service);
        codes.addAll(query.getResultList());
        return codes;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.StatusCodeModel#getStatusCodeSequencing(com.hellblazer.CoRE.product.Product)
     */
    @Override
    public List<StatusCodeSequencing> getStatusCodeSequencing(Product service) {
        TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(StatusCodeSequencing.GET_ALL_STATUS_CODE_SEQUENCING,
                                                                     StatusCodeSequencing.class);
        query.setParameter("service", service);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.StatusCodeModel#getStatusCodeSequencingParent(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.event.status.StatusCode)
     */
    @Override
    public List<StatusCodeSequencing> getStatusCodeSequencingParent(Product service,
                                                                    StatusCode parent) {
        TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING,
                                                                     StatusCodeSequencing.class);
        query.setParameter("service", service);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.StatusCodeModel#getStatusCodeSequencingChild(com.hellblazer.CoRE.product.Product, com.hellblazer.CoRE.event.status.StatusCode)
     */
    @Override
    public List<StatusCodeSequencing> getStatusCodeSequencingChild(Product service,
                                                                   StatusCode child) {
        TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING,
                                                                     StatusCodeSequencing.class);
        query.setParameter("service", service);
        return query.getResultList();
    }
}
