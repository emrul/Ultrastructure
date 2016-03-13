/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.meta.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.kernel.Kernel;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.WorkspaceModel;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceScope;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceSnapshot;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceModelImpl implements WorkspaceModel {

    private final EntityManager             em;
    private final Model                     model;
    private final Map<UUID, WorkspaceScope> scopes = new HashMap<>();

    public WorkspaceModelImpl(Model model) {
        this.model = model;
        em = model.getEntityManager();
    }

    @Override
    public WorkspaceScope createWorkspace(Product definingProduct,
                                          Agency updatedBy) {
        DatabaseBackedWorkspace workspace = new DatabaseBackedWorkspace(definingProduct,
                                                                        model);
        workspace.add(definingProduct);
        Kernel kernel = model.getKernel();
        Aspect<Product> aspect = new Aspect<Product>(kernel.getIsA(),
                                                     kernel.getWorkspace());
        model.getProductModel()
             .initialize(definingProduct, aspect, workspace);
        em.persist(definingProduct);
        WorkspaceScope scope = workspace.getScope();
        scopes.put(definingProduct.getId(), scope);
        return scope;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#flush()
     */
    @Override
    public void flush() {
        for (WorkspaceScope scope : scopes.values()) {
            scope.getWorkspace()
                 .flushCache();
        }
    }

    @Override
    public WorkspaceAuthorization get(Product definingProduct, String key) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_AUTHORIZATION,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        query.setParameter("key", key);
        return query.getSingleResult();
    }

    @Override
    public List<WorkspaceAuthorization> getByType(Product definingProduct,
                                                  String type) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_AUTHORIZATIONS_BY_TYPE,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        query.setParameter("type", type);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.WorkspaceModel#getScoped(java.util.UUID)
     */
    @Override
    public WorkspaceScope getScoped(Product definingProduct) {
        WorkspaceScope cached = scopes.get(definingProduct.getId());
        if (cached != null) {
            return cached;
        }
        WorkspaceScope scope = new DatabaseBackedWorkspace(definingProduct,
                                                           model).getScope();
        scopes.put(definingProduct.getId(), scope);
        return scope;
    }

    @Override
    public WorkspaceScope getScoped(UUID definingProduct) {
        Product product = em.find(Product.class, definingProduct);
        if (product == null) {
            throw new IllegalArgumentException(String.format("Defining Product %s does not exist",
                                                             definingProduct));
        }
        return getScoped(product);
    }

    @Override
    public List<WorkspaceAuthorization> getWorkspace(Product definingProduct) {
        TypedQuery<WorkspaceAuthorization> query = em.createNamedQuery(WorkspaceAuthorization.GET_WORKSPACE,
                                                                       WorkspaceAuthorization.class);
        query.setParameter("product", definingProduct);
        return query.getResultList();
    }

    @Override
    public void unload(Product definingProduct) {
        for (WorkspaceAuthorization auth : WorkspaceSnapshot.getAuthorizations(definingProduct,
                                                                               em)) {
            em.remove(auth.getEntity(em));
        }
    }
}
