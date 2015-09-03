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
package com.chiralbehaviors.CoRE.relationship;

import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkAuthorization;

/**
 * The authorizations for attributes on entities.
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_attribute_authorization", schema = "ruleform")
public class RelationshipAttributeAuthorization
        extends AttributeAuthorization<Relationship, RelationshipNetwork> {
    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "network_authorization")
    private RelationshipNetworkAuthorization networkAuthorization;

    public RelationshipAttributeAuthorization() {
    }

    /**
     * @param updatedBy
     */
    public RelationshipAttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     * @param coreModel
     */
    public RelationshipAttributeAuthorization(Attribute attribute,
                                              Agency updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param id
     */
    public RelationshipAttributeAuthorization(UUID id) {
        super(id);
    }

    @Override
    public NetworkAuthorization<Relationship> getNetworkAuthorization() {
        return networkAuthorization;
    }

    @Override
    public void setNetworkAuthorization(NetworkAuthorization<Relationship> auth) {
        networkAuthorization = (RelationshipNetworkAuthorization) auth;
    }

}