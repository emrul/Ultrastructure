/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.authorization;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * @author hparry
 * 
 */
@javax.persistence.Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Table(name = "access_authorizations", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "access_authorizations_id_seq", sequenceName = "access_authorizations_id_seq")
@DiscriminatorColumn(name = "authorization_type")
public abstract class AccessAuthorization extends Ruleform {
	
	//IMPORTANT: DON'T CHANGE THESE VALUES IF YOU HAVE DATA IN THE DATABASE
	public static final String RESOURCE_PRODUCT = "0";
	public static final String RESOURCE_LOCATION = "1";
	public static final String PRODUCT_RESOURCE = "2";
	public static final String PRODUCT_LOCATION = "3";
	public static final String LOCATION_RESOURCE = "4";
	public static final String LOCATION_PRODUCT = "5";
	

	private static final long serialVersionUID = 1L;

	@Column(name = "authorization_type")
	private String authorizationType;

	@Id
	@GeneratedValue(generator = "access_authorizations_id_seq", strategy = GenerationType.SEQUENCE)
	private Long id;

	// bi-directional many-to-one association to Relationship
	@ManyToOne
	@JoinColumn(name = "relationship")
	protected Relationship relationship;

	@ManyToOne
	@JoinColumn(name = "parent_transitive_relationship")
	protected Relationship parentTransitiveRelationship;

	@ManyToOne
	@JoinColumn(name = "child_transitive_relationship")
	protected Relationship childTransitiveRelationship;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.hellblazer.CoRE.Ruleform#setId(java.lang.Long)
	 */
	@Override
	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public Long getId() {
		return id;
	}

	/**
	 * @return the relationship
	 */
	public Relationship getRelationship() {
		return relationship;
	}

	/**
	 * @return the parentTransitiveRelationship
	 */
	public Relationship getParentTransitiveRelationship() {
		return parentTransitiveRelationship;
	}

	/**
	 * @return the childTransitiveRelationship
	 */
	public Relationship getChildTransitiveRelationship() {
		return childTransitiveRelationship;
	}

	/**
	 * @param relationship
	 *            the relationship to set
	 */
	public void setRelationship(Relationship relationship) {
		this.relationship = relationship;
	}

	/**
	 * @param parentTransitiveRelationship
	 *            the parentTransitiveRelationship to set
	 */
	public void setParentTransitiveRelationship(
			Relationship parentTransitiveRelationship) {
		this.parentTransitiveRelationship = parentTransitiveRelationship;
	}

	/**
	 * @param childTransitiveRelationship
	 *            the childTransitiveRelationship to set
	 */
	public void setChildTransitiveRelationship(
			Relationship childTransitiveRelationship) {
		this.childTransitiveRelationship = childTransitiveRelationship;
	}

	protected void setAuthorizationType(String type) {
		this.authorizationType = type;
	}
}