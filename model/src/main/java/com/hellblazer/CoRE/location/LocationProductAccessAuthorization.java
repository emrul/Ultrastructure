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
package com.hellblazer.CoRE.location;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.resource.Resource;

/**
 * @author hparry
 * 
 */
@Entity
@DiscriminatorValue(AccessAuthorization.LOCATION_PRODUCT)
public class LocationProductAccessAuthorization extends
		LocationAccessAuthorization {

	@ManyToOne
	@JoinColumn(name = "product2")
	private Product child;

	private static final long serialVersionUID = 1L;

	public LocationProductAccessAuthorization() {
		super();
		setAuthorizationType(AccessAuthorization.LOCATION_PRODUCT);
	}

	/**
	 * @param Resource
	 * @param Relationship
	 * @param Product
	 * @param updatedBy
	 */
	public LocationProductAccessAuthorization(Location parent,
			Relationship relationship, Product child, Resource updatedBy) {
		this();
		setParent(parent);
		setRelationship(relationship);
		setChild(child);
		setUpdatedBy(updatedBy);
	}

	/**
	 * @return the child
	 */
	public Product getChild() {
		return child;
	}

	/**
	 * @param child
	 *            the child to set
	 */
	public void setChild(Product child) {
		this.child = child;
	}
}