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

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Attribute;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.existential.domain.Unit;
import com.chiralbehaviors.CoRE.existential.domain.UnitAttribute;
import com.chiralbehaviors.CoRE.existential.domain.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.existential.domain.UnitNetwork;
import com.chiralbehaviors.CoRE.existential.domain.UnitNetworkAuthorization;
import com.chiralbehaviors.CoRE.meta.Aspect;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.UnitModel;
import com.chiralbehaviors.CoRE.security.AgencyUnitGrouping;

/**
 * @author hhildebrand
 *
 */
public class UnitModelImpl extends
        ExistentialModelImpl<Unit, UnitNetwork, UnitAttributeAuthorization, UnitAttribute>
        implements UnitModel {

    /**
     * @param em
     */
    public UnitModelImpl(Model model) {
        super(model);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors.CoRE
     * .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Unit> aspect, Attribute... attributes) {
        UnitNetworkAuthorization auth = new UnitNetworkAuthorization(model.getCurrentPrincipal()
                                                                          .getPrincipal());
        auth.setClassifier(aspect.getClassifier());
        auth.setClassification(aspect.getClassification());
        em.persist(auth);
        for (Attribute attribute : attributes) {
            UnitAttributeAuthorization authorization = new UnitAttributeAuthorization(attribute,
                                                                                      model.getCurrentPrincipal()
                                                                                           .getPrincipal());
            em.persist(authorization);
        }
    }

    @Override
    public final Unit create(String name, String description) {
        Unit unit = new Unit(name, description, model.getCurrentPrincipal()
                                                     .getPrincipal());
        em.persist(unit);
        return unit;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.meta
     * .Aspect<RuleForm>[])
     */
    @SafeVarargs
    @Override
    public final Unit create(String name, String description,
                             Aspect<Unit> aspect, Agency updatedBy,
                             Aspect<Unit>... aspects) {
        Unit agency = new Unit(name, description, model.getCurrentPrincipal()
                                                       .getPrincipal());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Unit> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Unit create(Unit prototype) {
        Unit copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(model.getCurrentPrincipal()
                               .getPrincipal());
        for (UnitNetwork network : prototype.getNetworkByParent()) {
            network.getParent()
                   .link(network.getRelationship(), copy,
                         model.getCurrentPrincipal()
                              .getPrincipal(),
                         model.getCurrentPrincipal()
                              .getPrincipal(),
                         em);
        }
        for (UnitAttribute attribute : prototype.getAttributes()) {
            UnitAttribute clone = (UnitAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setUnit(copy);
            clone.setUpdatedBy(model.getCurrentPrincipal()
                                    .getPrincipal());
        }
        return copy;
    }

    @Override
    public UnitAttribute create(Unit ruleform, Attribute attribute,
                                Agency updatedBy) {
        return new UnitAttribute(ruleform, attribute, updatedBy);
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<UnitNetwork> getInterconnections(Collection<Unit> parents,
                                                 Collection<Relationship> relationships,
                                                 Collection<Unit> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<UnitNetwork> query = em.createNamedQuery(UnitNetwork.GET_NETWORKS,
                                                            UnitNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getAgencyGroupingClass()
     */
    @Override
    protected Class<?> getAgencyGroupingClass() {
        return AgencyUnitGrouping.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getAttributeAuthorizationClass()
     */
    @Override
    protected Class<?> getAttributeAuthorizationClass() {
        return UnitAttributeAuthorization.class;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.models.AbstractNetworkedModel#getNetworkAuthClass()
     */
    @Override
    protected Class<?> getNetworkAuthClass() {
        return UnitNetworkAuthorization.class;
    }
}
