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

import javax.persistence.EntityManager;

import org.postgresql.pljava.TriggerData;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.jsp.JSP;
import com.hellblazer.CoRE.jsp.StoredProcedure;
import com.hellblazer.CoRE.kernel.Kernel;
import com.hellblazer.CoRE.kernel.KernelImpl;
import com.hellblazer.CoRE.meta.ResourceModel;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.resource.ResourceAttribute;
import com.hellblazer.CoRE.resource.ResourceAttributeAuthorization;
import com.hellblazer.CoRE.resource.ResourceNetwork;

/**
 * @author hhildebrand
 * 
 */
public class ResourceModelImpl
        extends
        AbstractNetworkedModel<Resource, ResourceAttributeAuthorization, ResourceAttribute>
        implements ResourceModel {

    private static interface Procedure<T> {
        T call(ResourceModelImpl productModel) throws Exception;
    }

    private static class Call<T> implements StoredProcedure<T> {
        private final Procedure<T> procedure;

        public Call(Procedure<T> procedure) {
            this.procedure = procedure;
        }

        @Override
        public T call(EntityManager em) throws Exception {
            return procedure.call(new ResourceModelImpl(em));
        }
    }

    private static <T> T execute(Procedure<T> procedure) throws SQLException {
        return JSP.call(new Call<T>(procedure));
    }

    private static final String RESOURCE_NETWORK_PROPAGATE = "ResourceNetwork.propagate";

    public static void propagate_deductions(final TriggerData data)
                                                                   throws Exception {
        if (!markPropagated(RESOURCE_NETWORK_PROPAGATE)) {
            return; // We be done
        }
        execute(new Procedure<Void>() {
            @Override
            public Void call(ResourceModelImpl resourceModel) throws Exception {
                resourceModel.propagate();
                return null;
            }
        });
    }

    public static void track_network_deleted(final TriggerData data)
                                                                    throws Exception {
        execute(new Procedure<Void>() {
            @Override
            public Void call(ResourceModelImpl resourceModel) throws Exception {
                resourceModel.networkEdgeDeleted(data.getOld().getLong("parent"),
                                                 data.getOld().getLong("relationship"));
                return null;
            }
        });
    }

    /**
     * @param em
     */
    public ResourceModelImpl(EntityManager em) {
        super(em, new KernelImpl(em));
    }

    /**
     * @param em
     */
    public ResourceModelImpl(EntityManager em, Kernel kernel) {
        super(em, kernel);
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#authorize(com.hellblazer.CoRE.meta.Aspect, com.hellblazer.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Resource> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            ResourceAttributeAuthorization authorization = new ResourceAttributeAuthorization(
                                                                                              aspect.getClassification(),
                                                                                              aspect.getClassifier(),
                                                                                              attribute,
                                                                                              kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.network.Networked)
     */
    @Override
    public Resource create(Resource prototype) {
        Resource copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (ResourceNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (ResourceAttribute attribute : prototype.getAttributes()) {
            ResourceAttribute clone = (ResourceAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setResource(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.meta.NetworkedModel#create(com.hellblazer.CoRE.meta.Aspect<RuleForm>[])
     */
    @Override
    public final Resource create(String name, String description,
                                 Aspect<Resource> aspect,
                                 Aspect<Resource>... aspects) {
        Resource resource = new Resource(name, description,
                                         kernel.getCoreModel());
        em.persist(resource);
        initialize(resource, aspect);
        if (aspects != null) {
            for (Aspect<Resource> a : aspects) {
                initialize(resource, a);
            }
        }
        return resource;
    }

    /**
     * @param resource
     * @param aspect
     */
    protected void initialize(Resource resource, Aspect<Resource> aspect) {
        resource.link(aspect.getClassification(), aspect.getClassifier(),
                      kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (ResourceAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            ResourceAttribute attribute = new ResourceAttribute(
                                                                authorization.getAuthorizedAttribute(),
                                                                kernel.getCoreModel());
            attribute.setResource(resource);
            defaultValue(attribute);
            em.persist(attribute);
        }
    }
}
