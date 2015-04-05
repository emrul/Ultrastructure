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
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttribute;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.meta.LocationModel;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 *
 */
public class LocationModelImpl
		extends
		AbstractNetworkedModel<Location, LocationNetwork, LocationAttributeAuthorization, LocationAttribute>
		implements LocationModel {

	/**
	 * @param em
	 */
	public LocationModelImpl(Model model) {
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
	public void authorize(Aspect<Location> aspect, Attribute... attributes) {
		for (Attribute attribute : attributes) {
			LocationAttributeAuthorization authorization = new LocationAttributeAuthorization(
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
	public void authorizeEnum(Aspect<Location> aspect, Attribute attribute,
			Attribute enumAttribute) {
		LocationAttributeAuthorization auth = new LocationAttributeAuthorization(
				aspect.getClassification(), aspect.getClassifier(), attribute,
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
	public Location create(Location prototype) {
		Location copy = prototype.clone();
		em.detach(copy);
		em.persist(copy);
		copy.setUpdatedBy(kernel.getCoreModel());
		for (LocationNetwork network : prototype.getNetworkByParent()) {
			network.getParent().link(network.getRelationship(), copy,
					kernel.getCoreModel(), kernel.getInverseSoftware(), em);
		}
		for (LocationAttribute attribute : prototype.getAttributes()) {
			LocationAttribute clone = (LocationAttribute) attribute.clone();
			em.detach(clone);
			em.persist(clone);
			clone.setLocation(copy);
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
	public Facet<Location, LocationAttribute> create(String name,
			String description, Aspect<Location> aspect) {
		Location location = new Location(name, description,
				kernel.getCoreModel());
		em.persist(location);
		return new Facet<Location, LocationAttribute>(aspect, location,
				initialize(location, aspect)) {
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
	public final Location create(String name, String description,
			Aspect<Location> aspect, Aspect<Location>... aspects) {
		Location location = new Location(name, description,
				kernel.getCoreModel());
		em.persist(location);
		initialize(location, aspect);
		if (aspects != null) {
			for (Aspect<Location> a : aspects) {
				initialize(location, a);
			}
		}
		return location;
	}

	@Override
	public List<LocationNetwork> getInterconnections(
			Collection<Location> parents,
			Collection<Relationship> relationships,
			Collection<Location> children) {
		TypedQuery<LocationNetwork> query = em.createNamedQuery(
				LocationNetwork.GET_NETWORKS, LocationNetwork.class);
		query.setParameter("parents", parents);
		query.setParameter("relationship", relationships);
		query.setParameter("children", children);
		return query.getResultList();
	}

	/**
	 * @param attributeValue
	 * @return
	 */
	private LocationAttributeAuthorization getValidatingAuthorization(
			LocationAttribute attributeValue) {
		String sql = "SELECT  p FROM LocationAttributeAuthorization p "
				+ "WHERE p.validatingAttribute IS NOT NULL "
				+ "AND p.authorizedAttribute = :attribute ";
		TypedQuery<LocationAttributeAuthorization> query = em.createQuery(sql,
				LocationAttributeAuthorization.class);
		query.setParameter("attribute", attributeValue.getAttribute());
		List<LocationAttributeAuthorization> auths = query.getResultList();
		TypedQuery<LocationNetwork> networkQuery = em.createNamedQuery(
				LocationNetwork.GET_NETWORKS, LocationNetwork.class);
		networkQuery.setParameter("parent", attributeValue.getLocation());
		for (LocationAttributeAuthorization auth : auths) {
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
	 * @param location
	 * @param aspect
	 */
	protected List<LocationAttribute> initialize(Location location,
			Aspect<Location> aspect) {
		location.link(aspect.getClassification(), aspect.getClassifier(),
				kernel.getCoreModel(), kernel.getInverseSoftware(), em);
		List<LocationAttribute> attributes = new ArrayList<>();
		for (LocationAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
			LocationAttribute attribute = new LocationAttribute(
					authorization.getAuthorizedAttribute(),
					kernel.getCoreModel());
			attributes.add(attribute);
			attribute.setLocation(location);
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
	public void setAttributeValue(LocationAttribute attributeValue) {
		LocationAttributeAuthorization auth = getValidatingAuthorization(attributeValue);
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
