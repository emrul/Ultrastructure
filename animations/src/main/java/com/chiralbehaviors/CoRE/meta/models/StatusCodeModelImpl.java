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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.StatusCodeModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;

/**
 * @author hhildebrand
 *
 */
public class StatusCodeModelImpl
		extends
		AbstractNetworkedModel<StatusCode, StatusCodeNetwork, StatusCodeAttributeAuthorization, StatusCodeAttribute>
		implements StatusCodeModel {

	/**
	 * @param em
	 */
	public StatusCodeModelImpl(Model model) {
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
	public void authorize(Aspect<StatusCode> aspect, Attribute... attributes) {
		for (Attribute attribute : attributes) {
			StatusCodeAttributeAuthorization authorization = new StatusCodeAttributeAuthorization(
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
	public void authorizeEnum(Aspect<StatusCode> aspect, Attribute attribute,
			Attribute enumAttribute) {
		StatusCodeAttributeAuthorization auth = new StatusCodeAttributeAuthorization(
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
	public StatusCode create(StatusCode prototype) {
		StatusCode copy = prototype.clone();
		em.detach(copy);
		em.persist(copy);
		copy.setUpdatedBy(kernel.getCoreModel());
		for (StatusCodeNetwork network : prototype.getNetworkByParent()) {
			network.getParent().link(network.getRelationship(), copy,
					kernel.getCoreModel(), kernel.getInverseSoftware(), em);
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
	 * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String,
	 * java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
	 */
	@Override
	public Facet<StatusCode, StatusCodeAttribute> create(String name,
			String description, Aspect<StatusCode> aspect) {
		StatusCode statusCode = new StatusCode(name, description,
				kernel.getCoreModel());
		em.persist(statusCode);
		return new Facet<StatusCode, StatusCodeAttribute>(aspect, statusCode,
				initialize(statusCode, aspect)) {
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
	public final StatusCode create(String name, String description,
			Aspect<StatusCode> aspect, Aspect<StatusCode>... aspects) {
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
	 * @see
	 * com.chiralbehaviors.CoRE.meta.NetworkedModel#getInterconnections(java
	 * .util.List, java.util.List, java.util.List)
	 */
	@Override
	public List<StatusCodeNetwork> getInterconnections(
			Collection<StatusCode> parents,
			Collection<Relationship> relationships,
			Collection<StatusCode> children) {
		TypedQuery<StatusCodeNetwork> query = em.createNamedQuery(
				StatusCodeNetwork.GET_NETWORKS, StatusCodeNetwork.class);
		query.setParameter("parents", parents);
		query.setParameter("relationship", relationships);
		query.setParameter("children", children);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodes(com.
	 * chiralbehaviors.CoRE.product.Product)
	 */
	@Override
	public Collection<StatusCode> getStatusCodes(Product service) {
		Set<StatusCode> codes = new HashSet<StatusCode>();
		TypedQuery<StatusCode> query = em.createNamedQuery(
				StatusCodeSequencing.GET_PARENT_STATUS_CODES_SERVICE,
				StatusCode.class);
		query.setParameter("service", service);
		codes.addAll(query.getResultList());
		query = em.createNamedQuery(
				StatusCodeSequencing.GET_CHILD_STATUS_CODES_SERVICE,
				StatusCode.class);
		query.setParameter("service", service);
		codes.addAll(query.getResultList());
		return codes;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencing
	 * (com.chiralbehaviors.CoRE.product.Product)
	 */
	@Override
	public List<StatusCodeSequencing> getStatusCodeSequencing(Product service) {
		TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(
				StatusCodeSequencing.GET_ALL_STATUS_CODE_SEQUENCING,
				StatusCodeSequencing.class);
		query.setParameter("service", service);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingChild
	 * (com.chiralbehaviors.CoRE.product.Product,
	 * com.chiralbehaviors.CoRE.event.status.StatusCode)
	 */
	@Override
	public List<StatusCodeSequencing> getStatusCodeSequencingChild(
			Product service, StatusCode child) {
		TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(
				StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING_SERVICE,
				StatusCodeSequencing.class);
		query.setParameter("service", service);
		query.setParameter("statusCode", child);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingChild
	 * (com.chiralbehaviors.CoRE.event.status.StatusCode)
	 */
	@Override
	public Collection<StatusCodeSequencing> getStatusCodeSequencingChild(
			StatusCode child) {
		TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(
				StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING,
				StatusCodeSequencing.class);
		query.setParameter("statusCode", child);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingParent
	 * (com.chiralbehaviors.CoRE.product.Product,
	 * com.chiralbehaviors.CoRE.event.status.StatusCode)
	 */
	@Override
	public List<StatusCodeSequencing> getStatusCodeSequencingParent(
			Product service, StatusCode parent) {
		TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(
				StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING_SERVICE,
				StatusCodeSequencing.class);
		query.setParameter("service", service);
		query.setParameter("statusCode", parent);
		return query.getResultList();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.chiralbehaviors.CoRE.meta.StatusCodeModel#getStatusCodeSequencingParent
	 * (com.chiralbehaviors.CoRE.event.status.StatusCode)
	 */
	@Override
	public List<StatusCodeSequencing> getStatusCodeSequencingParent(
			StatusCode parent) {
		TypedQuery<StatusCodeSequencing> query = em.createNamedQuery(
				StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING,
				StatusCodeSequencing.class);
		query.setParameter("statusCode", parent);
		return query.getResultList();
	}

	/**
	 * @param attributeValue
	 * @return
	 */
	private StatusCodeAttributeAuthorization getValidatingAuthorization(
			StatusCodeAttribute attributeValue) {
		String sql = "SELECT  p FROM StatusCodeAttributeAuthorization p "
				+ "WHERE p.validatingAttribute IS NOT NULL "
				+ "AND p.authorizedAttribute = :attribute ";
		TypedQuery<StatusCodeAttributeAuthorization> query = em.createQuery(
				sql, StatusCodeAttributeAuthorization.class);
		query.setParameter("attribute", attributeValue.getAttribute());
		List<StatusCodeAttributeAuthorization> auths = query.getResultList();
		TypedQuery<StatusCodeNetwork> networkQuery = em.createNamedQuery(
				StatusCodeNetwork.GET_NETWORKS, StatusCodeNetwork.class);
		networkQuery.setParameter("parent", attributeValue.getStatusCode());
		for (StatusCodeAttributeAuthorization auth : auths) {
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
	protected List<StatusCodeAttribute> initialize(StatusCode agency,
			Aspect<StatusCode> aspect) {
		agency.link(aspect.getClassification(), aspect.getClassifier(),
				kernel.getCoreModel(), kernel.getInverseSoftware(), em);
		List<StatusCodeAttribute> attributes = new ArrayList<>();
		for (StatusCodeAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
			StatusCodeAttribute attribute = new StatusCodeAttribute(
					authorization.getAuthorizedAttribute(),
					kernel.getCoreModel());
			attributes.add(attribute);
			attribute.setStatusCode(agency);
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
	public void setAttributeValue(StatusCodeAttribute attributeValue) {
		StatusCodeAttributeAuthorization auth = getValidatingAuthorization(attributeValue);
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
