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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.meta.IntervalModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttribute;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;

/**
 * @author hhildebrand
 *
 */
public class IntervalModelImpl
        extends
        AbstractNetworkedModel<Interval, IntervalNetwork, IntervalAttributeAuthorization, IntervalAttribute>
        implements IntervalModel {

    /**
     * @param em
     */
    public IntervalModelImpl(Model model) {
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
    public void authorize(Aspect<Interval> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            IntervalAttributeAuthorization authorization = new IntervalAttributeAuthorization(
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
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorizeEnum(com.
     * chiralbehaviors.CoRE.network.Aspect,
     * com.chiralbehaviors.CoRE.attribute.Attribute,
     * com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public void authorizeEnum(Aspect<Interval> aspect, Attribute attribute,
                              Attribute enumAttribute) {
        IntervalAttributeAuthorization auth = new IntervalAttributeAuthorization(
                                                                                 aspect.getClassification(),
                                                                                 aspect.getClassifier(),
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
    public Interval create(Interval prototype) {
        Interval copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (IntervalNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (IntervalAttribute attribute : prototype.getAttributes()) {
            IntervalAttribute clone = (IntervalAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setInterval(copy);
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
    @SuppressWarnings("unchecked")
    @Override
    public Facet<Interval, IntervalAttribute> create(String name,
                                                     String description,
                                                     Aspect<Interval> aspect) {
        return create(name, description, null, kernel.getNotApplicableUnit(),
                      aspect);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors
     * .CoRE.meta .Aspect<RuleForm>[])
     */
    @SafeVarargs
    @Override
    public final Interval create(String name, String description,
                                 Aspect<Interval> aspect,
                                 Aspect<Interval>... aspects) {
        Interval agency = new Interval(name, BigDecimal.valueOf(0),
                                       kernel.getNotApplicableUnit(),
                                       BigDecimal.valueOf(0),
                                       kernel.getNotApplicableUnit(),
                                       description, kernel.getCoreModel());
        em.persist(agency);
        initialize(agency, aspect);
        if (aspects != null) {
            for (Aspect<Interval> a : aspects) {
                initialize(agency, a);
            }
        }
        return agency;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.IntervalModel#create(java.lang.String,
     * java.lang.String, java.math.BigDecimal,
     * com.chiralbehaviors.CoRE.attribute.unit.Unit,
     * com.chiralbehaviors.CoRE.network.Aspect,
     * com.chiralbehaviors.CoRE.network.Aspect[])
     */
    @Override
    public Facet<Interval, IntervalAttribute> create(String name,
                                                     String description,
                                                     BigDecimal start,
                                                     Unit startUnit,
                                                     Aspect<Interval> aspect,
                                                     @SuppressWarnings("unchecked") Aspect<Interval>... aspects) {
        return create(name, description, start, startUnit, null,
                      kernel.getNotApplicableUnit(), aspect, aspects);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.IntervalModel#create(java.lang.String,
     * java.lang.String, java.math.BigDecimal,
     * com.chiralbehaviors.CoRE.attribute.unit.Unit, java.math.BigDecimal,
     * com.chiralbehaviors.CoRE.attribute.unit.Unit,
     * com.chiralbehaviors.CoRE.network.Aspect,
     * com.chiralbehaviors.CoRE.network.Aspect[])
     */
    @Override
    public Facet<Interval, IntervalAttribute> create(String name,
                                                     String description,
                                                     BigDecimal start,
                                                     Unit startUnit,
                                                     BigDecimal duration,
                                                     Unit durationUnit,
                                                     Aspect<Interval> aspect,
                                                     @SuppressWarnings("unchecked") Aspect<Interval>... aspects) {
        Interval interval = new Interval(name, start, startUnit, duration,
                                         durationUnit, description,
                                         kernel.getCoreModel());
        em.persist(interval);
        return new Facet<Interval, IntervalAttribute>(aspect, interval,
                                                      initialize(interval,
                                                                 aspect)) {
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java
     * .util.List, java.util.List, java.util.List)
     */
    @Override
    public List<IntervalNetwork> getInterconnections(Collection<Interval> parents,
                                                     Collection<Relationship> relationships,
                                                     Collection<Interval> children) {
        throw new UnsupportedOperationException();
    }

    /**
     * @param attributeValue
     * @return
     */
    private IntervalAttributeAuthorization getValidatingAuthorization(IntervalAttribute attributeValue) {
        String sql = "SELECT  p FROM IntervalAttributeAuthorization p "
                     + "WHERE p.validatingAttribute IS NOT NULL "
                     + "AND p.authorizedAttribute = :attribute ";
        TypedQuery<IntervalAttributeAuthorization> query = em.createQuery(sql,
                                                                          IntervalAttributeAuthorization.class);
        query.setParameter("attribute", attributeValue.getAttribute());
        List<IntervalAttributeAuthorization> auths = query.getResultList();
        TypedQuery<IntervalNetwork> networkQuery = em.createNamedQuery(IntervalNetwork.GET_NETWORKS,
                                                                       IntervalNetwork.class);
        networkQuery.setParameter("parent", attributeValue.getInterval());
        for (IntervalAttributeAuthorization auth : auths) {
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
     * @param agency
     * @param aspect
     */
    protected List<IntervalAttribute> initialize(Interval agency,
                                                 Aspect<Interval> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<IntervalAttribute> attributes = new ArrayList<>();
        for (IntervalAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            IntervalAttribute attribute = new IntervalAttribute(
                                                                authorization.getAuthorizedAttribute(),
                                                                kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setInterval(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.IntervalModel#create(java.lang.String,
     * java.lang.String, java.math.BigDecimal,
     * com.chiralbehaviors.CoRE.attribute.unit.Unit, java.math.BigDecimal,
     * com.chiralbehaviors.CoRE.attribute.unit.Unit)
     */
    @Override
    public Interval newDefaultInterval(String name, String description) {
        Interval interval = new Interval(name, BigDecimal.valueOf(0),
                                         kernel.getNotApplicableUnit(),
                                         BigDecimal.valueOf(0),
                                         kernel.getNotApplicableUnit(),
                                         description, kernel.getCoreModel());
        em.persist(interval);
        return interval;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#setAttributeValue(com.
     * chiralbehaviors.CoRE.ExistentialRuleform,
     * com.chiralbehaviors.CoRE.attribute.Attribute, java.lang.Object)
     */
    @Override
    public void setAttributeValue(IntervalAttribute attributeValue) {
        IntervalAttributeAuthorization auth = getValidatingAuthorization(attributeValue);
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
