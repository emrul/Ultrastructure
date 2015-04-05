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
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.RelationshipModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.network.RelationshipAttribute;
import com.chiralbehaviors.CoRE.network.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.network.RelationshipNetwork;

/**
 * @author hhildebrand
 *
 */
public class RelationshipModelImpl
        extends
        AbstractNetworkedModel<Relationship, RelationshipNetwork, RelationshipAttributeAuthorization, RelationshipAttribute>
        implements RelationshipModel {

    /**
     * @param em
     */
    public RelationshipModelImpl(Model model) {
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
    public void authorize(Aspect<Relationship> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            RelationshipAttributeAuthorization authorization = new RelationshipAttributeAuthorization(
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
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors.CoRE.network
     * .Networked)
     */
    @Override
    public Relationship create(Relationship prototype) {
        Relationship copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (RelationshipNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (RelationshipAttribute attribute : prototype.getAttributes()) {
            RelationshipAttribute clone = (RelationshipAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setRelationship(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String, java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Relationship, RelationshipAttribute> create(String name,
                                                             String description,
                                                             Aspect<Relationship> aspect) {
        Relationship relationship = new Relationship(name, description,
                                                     kernel.getCoreModel());
        em.persist(relationship);
        return new Facet<Relationship, RelationshipAttribute>(
                                                              aspect,
                                                              relationship,
                                                              initialize(relationship,
                                                                         aspect)) {
        };
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
    public final Relationship create(String name, String description,
                                     Aspect<Relationship> aspect,
                                     Aspect<Relationship>... aspects) {
        Relationship relationship = new Relationship(name, description,
                                                     kernel.getCoreModel());
        em.persist(relationship);
        initialize(relationship, aspect);
        if (aspects != null) {
            for (Aspect<Relationship> a : aspects) {
                initialize(relationship, a);
            }
        }
        return relationship;
    }

    @Override
    public final Relationship create(String rel1Name, String rel1Description,
                                     String rel2Name, String rel2Description) {
        Relationship relationship = new Relationship(rel1Name, rel1Description,
                                                     kernel.getCoreModel());

        Relationship relationship2 = new Relationship(rel2Name,
                                                      rel2Description,
                                                      kernel.getCoreModel());

        relationship.setInverse(relationship2);
        relationship2.setInverse(relationship);
        em.persist(relationship);
        em.persist(relationship2);

        return relationship;
    }

    /* (non-Javadoc)
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java.util.List, java.util.List, java.util.List)
     */
    @Override
    public List<RelationshipNetwork> getInterconnections(Collection<Relationship> parents,
                                                         Collection<Relationship> relationships,
                                                         Collection<Relationship> children) {
        if (parents == null || parents.size() == 0 || relationships == null
            || relationships.size() == 0 || children == null
            || children.size() == 0) {
            return null;
        }
        TypedQuery<RelationshipNetwork> query = em.createNamedQuery(RelationshipNetwork.GET_NETWORKS,
                                                                    RelationshipNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param agency
     * @param aspect
     */
    protected List<RelationshipAttribute> initialize(Relationship agency,
                                                     Aspect<Relationship> aspect) {
        agency.link(aspect.getClassification(), aspect.getClassifier(),
                    kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<RelationshipAttribute> attributes = new ArrayList<>();
        for (RelationshipAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            RelationshipAttribute attribute = new RelationshipAttribute(
                                                                        authorization.getAuthorizedAttribute(),
                                                                        kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setRelationship(agency);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#setAttributeValue(com.
	 * chiralbehaviors.CoRE.ExistentialRuleform,
	 * com.chiralbehaviors.CoRE.attribute.Attribute, java.lang.Object)
	 */
	@Override
	public void setAttributeValue(RelationshipAttribute attributeValue) {
		RelationshipAttributeAuthorization auth = getValidatingAuthorization(attributeValue);
		if (auth != null) {
			Attribute validatingAttribute = auth.getValidatingAttribute();
			if (validatingAttribute.getValueType().equals(
					attributeValue.getAttribute().getValueType())) {
				TypedQuery<AttributeMetaAttribute> valueQuery = em
						.createNamedQuery(AttributeMetaAttribute.GET_ATTRIBUTE,
								AttributeMetaAttribute.class);
				valueQuery.setParameter("meta", attributeValue.getAttribute());
				valueQuery.setParameter("attr", validatingAttribute);
				List<AttributeMetaAttribute> values = valueQuery
						.getResultList();
				boolean valid = false;
				for (AttributeMetaAttribute value : values) {
					if (attributeValue.getTextValue().equals(
							value.getTextValue())) {
						valid = true;
						break;
					}
				}
				if (!valid) {
					throw new IllegalArgumentException(
							String.format(
									"%s is not a valid picklist value for attribute %s",
									attributeValue.getTextValue(),
									attributeValue.getAttribute().getName()));

				}
			}
		}

		em.persist(attributeValue);

	}

	/**
	 * @param attributeValue
	 * @return
	 */
	private RelationshipAttributeAuthorization getValidatingAuthorization(
			RelationshipAttribute attributeValue) {
		String sql = "SELECT  p FROM RelationshipAttributeAuthorization p "
				+ "WHERE p.validatingAttribute IS NOT NULL "
				+ "AND p.authorizedAttribute = :attribute ";
		TypedQuery<RelationshipAttributeAuthorization> query = em.createQuery(sql,
				RelationshipAttributeAuthorization.class);
		query.setParameter("attribute", attributeValue.getAttribute());
		List<RelationshipAttributeAuthorization> auths = query.getResultList();
		TypedQuery<RelationshipNetwork> networkQuery = em.createNamedQuery(
				RelationshipNetwork.GET_NETWORKS, RelationshipNetwork.class);
		networkQuery.setParameter("parent", attributeValue.getRelationship());
		for (RelationshipAttributeAuthorization auth : auths) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorizeEnum(com.
	 * chiralbehaviors.CoRE.network.Aspect,
	 * com.chiralbehaviors.CoRE.attribute.Attribute,
	 * com.chiralbehaviors.CoRE.attribute.Attribute)
	 */
	@Override
	public void authorizeEnum(Aspect<Relationship> aspect, Attribute attribute,
			Attribute enumAttribute) {
		RelationshipAttributeAuthorization auth = new RelationshipAttributeAuthorization(
				aspect.getClassification(), aspect.getClassifier(), attribute,
				kernel.getCoreAnimationSoftware());
		auth.setValidatingAttribute(enumAttribute);
		em.persist(auth);

	}
}
