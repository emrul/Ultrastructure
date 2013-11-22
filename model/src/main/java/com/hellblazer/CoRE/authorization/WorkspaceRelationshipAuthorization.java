/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

import java.util.Map;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.network.Relationship;

/**
 * @author hhildebrand
 * 
 */
@Entity
@DiscriminatorValue(WorkspaceAuthorization.PRODUCT_AGENCY)
public class WorkspaceRelationshipAuthorization extends WorkspaceAuthorization {

    private static final long serialVersionUID = 1L;

    {
        setAuthorizationType(WorkspaceAuthorization.PRODUCT_AGENCY);
    }

    @ManyToOne
    @JoinColumn(name = "relationship")
    protected Relationship    relationship;

    public WorkspaceRelationshipAuthorization() {
        super();
    }

    public WorkspaceRelationshipAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public WorkspaceRelationshipAuthorization(Long id) {
        super(id);
    }

    public WorkspaceRelationshipAuthorization(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public WorkspaceRelationshipAuthorization(String notes) {
        super(notes);
    }

    public WorkspaceRelationshipAuthorization(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    public Relationship getRelationship() {
        return relationship;
    }

    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {

        if (relationship != null) {
            relationship = (Relationship) relationship.manageEntity(em,
                                                                    knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }
}
