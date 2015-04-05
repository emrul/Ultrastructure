/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.meta.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.meta.AttributeModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class AttributeModelImpl
        extends
        AbstractNetworkedModel<Attribute, AttributeNetwork, AttributeMetaAttributeAuthorization, AttributeMetaAttribute>
        implements AttributeModel {

    /**
     * @param em
     */
    public AttributeModelImpl(Model model) {
        super(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors
     * .CoRE .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Attribute> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            AttributeMetaAttributeAuthorization authorization = new AttributeMetaAttributeAuthorization(
                                                                                                        aspect.getClassifier(),
                                                                                                        aspect.getClassification(),
                                                                                                        attribute,
                                                                                                        kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorizeEnum(com.
     * chiralbehaviors.CoRE.network.Aspect,
     * com.chiralbehaviors.CoRE.attribute.Attribute,
     * com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public void authorizeEnum(Aspect<Attribute> aspect, Attribute attribute,
                              Attribute enumAttribute) {
        AttributeMetaAttributeAuthorization auth = new AttributeMetaAttributeAuthorization(
                                                                                           aspect.getClassifier(),
                                                                                           aspect.getClassification(),
                                                                                           attribute,
                                                                                           kernel.getCoreAnimationSoftware());
        auth.setValidatingAttribute(enumAttribute);
        em.persist(auth);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors
     * .CoRE.network .Networked)
     */
    @Override
    public Attribute create(Attribute prototype) {
        Attribute copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (AttributeNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (AttributeMetaAttribute attribute : prototype.getAttributes()) {
            AttributeMetaAttribute clone = (AttributeMetaAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setAttribute(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String,
     * java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Attribute, AttributeMetaAttribute> create(String name,
                                                           String description,
                                                           Aspect<Attribute> aspect) {

        Attribute attribute = new Attribute(name, description,
                                            kernel.getCoreModel());
        em.persist(attribute);
        return new Facet<Attribute, AttributeMetaAttribute>(
                                                            aspect,
                                                            attribute,
                                                            initialize(attribute,
                                                                       aspect)) {
        };
    }

    @SafeVarargs
    @Override
    public final Attribute create(String name, String description,
                                  Aspect<Attribute> aspect,
                                  Aspect<Attribute>... aspects) {
        Attribute attribute = new Attribute(name, description,
                                            kernel.getCoreModel());
        em.persist(attribute);
        initialize(attribute, aspect);
        if (aspects != null) {
            for (Aspect<Attribute> a : aspects) {
                initialize(attribute, a);
            }
        }
        return attribute;
    }

    @Override
    public List<AttributeNetwork> getInterconnections(Collection<Attribute> parents,
                                                      Collection<Relationship> relationships,
                                                      Collection<Attribute> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<AttributeNetwork> query = em.createNamedQuery(AttributeNetwork.GET_NETWORKS,
                                                                 AttributeNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param attributeValue
     * @return
     */
    private AttributeMetaAttributeAuthorization getValidatingAuthorization(AttributeMetaAttribute attributeValue) {
        String sql = "SELECT  p FROM AttributeMetaAttributeAuthorization p "
                     + "WHERE p.validatingAttribute IS NOT NULL "
                     + "AND p.authorizedAttribute = :attribute ";
        TypedQuery<AttributeMetaAttributeAuthorization> query = em.createQuery(sql,
                                                                               AttributeMetaAttributeAuthorization.class);
        query.setParameter("attribute", attributeValue.getAttribute());
        List<AttributeMetaAttributeAuthorization> auths = query.getResultList();
        TypedQuery<AttributeNetwork> networkQuery = em.createNamedQuery(AttributeNetwork.GET_NETWORKS,
                                                                        AttributeNetwork.class);
        networkQuery.setParameter("parent", attributeValue.getMetaAttribute());
        for (AttributeMetaAttributeAuthorization auth : auths) {
            networkQuery.setParameter("relationship", auth.getClassification());
            networkQuery.setParameter("child", auth.getClassifier());
            try {
                if (networkQuery.getSingleResult() != null) {
                    return auth;
                }
            } catch (NoResultException e) {
                // keep going
            }
        }
        return null;
    }

    /**
     * @param attribute
     * @param aspect
     */
    protected List<AttributeMetaAttribute> initialize(Attribute attribute,
                                                      Aspect<Attribute> aspect) {
        List<AttributeMetaAttribute> attrs = new ArrayList<>();
        attribute.link(aspect.getClassification(), aspect.getClassifier(),
                       kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        for (AttributeMetaAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            AttributeMetaAttribute attr = new AttributeMetaAttribute(
                                                                     authorization.getAuthorizedAttribute(),
                                                                     kernel.getCoreModel());
            attrs.add(attr);
            attr.setAttribute(attribute);
            defaultValue(attr);
            em.persist(attr);
        }
        return attrs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#setAttributeValue(com.
     * chiralbehaviors.CoRE.ExistentialRuleform,
     * com.chiralbehaviors.CoRE.attribute.Attribute, java.lang.Object)
     */
    @Override
    public void setAttributeValue(AttributeMetaAttribute attributeValue) {
        AttributeMetaAttributeAuthorization auth = getValidatingAuthorization(attributeValue);
        if (auth != null) {
            Attribute validatingAttribute = auth.getValidatingAttribute();
            if (validatingAttribute.getValueType().equals(attributeValue.getAttribute().getValueType())) {
                TypedQuery<AttributeMetaAttribute> valueQuery = em.createNamedQuery(AttributeMetaAttribute.GET_ATTRIBUTE,
                                                                                    AttributeMetaAttribute.class);
                valueQuery.setParameter("meta", attributeValue.getAttribute());
                valueQuery.setParameter("attr", validatingAttribute);
                List<AttributeMetaAttribute> values = valueQuery.getResultList();
                boolean valid = false;
                for (AttributeMetaAttribute value : values) {
                    if (attributeValue.getTextValue().equals(value.getTextValue())) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    throw new IllegalArgumentException(
                                                       String.format("%s is not a valid picklist value for attribute %s",
                                                                     attributeValue.getTextValue(),
                                                                     attributeValue.getAttribute().getName()));

                }
            }
        }

        em.persist(attributeValue);

    }
}
