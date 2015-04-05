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
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.UnitModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class UnitModelImpl
		extends
		AbstractNetworkedModel<Unit, UnitNetwork, UnitAttributeAuthorization, UnitAttribute>
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
	 * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors
	 * .CoRE .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
	 */
	@Override
	public void authorize(Aspect<Unit> aspect, Attribute... attributes) {
		for (Attribute attribute : attributes) {
			UnitAttributeAuthorization authorization = new UnitAttributeAuthorization(
					aspect.getClassification(), aspect.getClassifier(),
					attribute, kernel.getCoreModel());
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
	public void authorizeEnum(Aspect<Unit> aspect, Attribute attribute,
			Attribute enumAttribute) {
		UnitAttributeAuthorization auth = new UnitAttributeAuthorization(
				aspect.getClassification(), aspect.getClassifier(), attribute,
				kernel.getCoreAnimationSoftware());
		auth.setValidatingAttribute(enumAttribute);
		em.persist(auth);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String,
	 * java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
	 */
	@Override
	public Facet<Unit, UnitAttribute> create(String name, String description,
			Aspect<Unit> aspect) {
		Unit unit = new Unit(name, description, kernel.getCoreModel());
		em.persist(unit);
		return new Facet<Unit, UnitAttribute>(aspect, unit, initialize(unit,
				aspect)) {
		};
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
	public final Unit create(String name, String description,
			Aspect<Unit> aspect, Aspect<Unit>... aspects) {
		Unit agency = new Unit(name, description, kernel.getCoreModel());
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
	 * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors
	 * .CoRE.network .Networked)
	 */
	@Override
	public Unit create(Unit prototype) {
		Unit copy = prototype.clone();
		em.detach(copy);
		em.persist(copy);
		copy.setUpdatedBy(kernel.getCoreModel());
		for (UnitNetwork network : prototype.getNetworkByParent()) {
			network.getParent().link(network.getRelationship(), copy,
					kernel.getCoreModel(), kernel.getInverseSoftware(), em);
		}
		for (UnitAttribute attribute : prototype.getAttributes()) {
			UnitAttribute clone = (UnitAttribute) attribute.clone();
			em.detach(clone);
			em.persist(clone);
			clone.setUnit(copy);
			clone.setUpdatedBy(kernel.getCoreModel());
		}
		return copy;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java
	 * .util.List, java.util.List, java.util.List)
	 */
	@Override
	public List<UnitNetwork> getInterconnections(Collection<Unit> parents,
			Collection<Relationship> relationships, Collection<Unit> children) {
		if (parents == null || parents.size() == 0 || relationships == null
				|| relationships.size() == 0 || children == null
				|| children.size() == 0) {
			return null;
		}
		TypedQuery<UnitNetwork> query = em.createNamedQuery(
				UnitNetwork.GET_NETWORKS, UnitNetwork.class);
		query.setParameter("parents", parents);
		query.setParameter("relationships", relationships);
		query.setParameter("children", children);
		return query.getResultList();
	}

	/**
	 * @param attributeValue
	 * @return
	 */
	private UnitAttributeAuthorization getValidatingAuthorization(
			UnitAttribute attributeValue) {
		String sql = "SELECT  p FROM UnitAttributeAuthorization p "
				+ "WHERE p.validatingAttribute IS NOT NULL "
				+ "AND p.authorizedAttribute = :attribute ";
		TypedQuery<UnitAttributeAuthorization> query = em.createQuery(sql,
				UnitAttributeAuthorization.class);
		query.setParameter("attribute", attributeValue.getAttribute());
		List<UnitAttributeAuthorization> auths = query.getResultList();
		TypedQuery<UnitNetwork> networkQuery = em.createNamedQuery(
				UnitNetwork.GET_NETWORKS, UnitNetwork.class);
		networkQuery.setParameter("parent", attributeValue.getUnit());
		for (UnitAttributeAuthorization auth : auths) {
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
	protected List<UnitAttribute> initialize(Unit agency, Aspect<Unit> aspect) {
		agency.link(aspect.getClassification(), aspect.getClassifier(),
				kernel.getCoreModel(), kernel.getInverseSoftware(), em);
		List<UnitAttribute> attributes = new ArrayList<>();
		for (UnitAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
			UnitAttribute attribute = new UnitAttribute(
					authorization.getAuthorizedAttribute(),
					kernel.getCoreModel());
			attributes.add(attribute);
			attribute.setUnit(agency);
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
	public void setAttributeValue(UnitAttribute attributeValue) {
		UnitAttributeAuthorization auth = getValidatingAuthorization(attributeValue);
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
}
