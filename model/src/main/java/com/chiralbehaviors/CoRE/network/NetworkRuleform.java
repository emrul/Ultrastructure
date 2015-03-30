/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chiralbehaviors.CoRE.network;

import java.util.UUID;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.SqlResultSetMapping;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An existential ruleform that can form directed graphs.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@Access(AccessType.FIELD)
@SqlResultSetMapping(name = "Edge", classes = { @ConstructorResult(targetClass = Edge.class, columns = {
                                                                                                        @ColumnResult(name = "parent"),
                                                                                                        @ColumnResult(name = "relationship"),
                                                                                                        @ColumnResult(name = "child"),
                                                                                                        @ColumnResult(name = "inference"),
                                                                                                        @ColumnResult(name = "premise1"),
                                                                                                        @ColumnResult(name = "premise2") }) })
abstract public class NetworkRuleform<E extends ExistentialRuleform<?, ?>>
        extends Ruleform {
    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "inference", insertable = false)
    private NetworkInference  inference;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "relationship")
    private Relationship      relationship;

    public NetworkRuleform() {
        super();
    }

    /**
     * @param updatedBy
     */
    public NetworkRuleform(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param relationship
     * @param updatedBy
     */
    public NetworkRuleform(Relationship relationship, Agency updatedBy) {
        super(updatedBy);
        this.relationship = relationship;
    }

    /**
     * @param id
     */
    public NetworkRuleform(UUID id) {
        super(id);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        @SuppressWarnings("unchecked")
        NetworkRuleform<E> other = (NetworkRuleform<E>) obj;
        return getParent().equals(other.getParent())
               && getRelationship().equals(other.getRelationship())
               && getChild().equals(other.getChild());
    }

    abstract public E getChild();

    /**
     * @return the inference
     */
    @JsonGetter
    public NetworkInference getInference() {
        return inference;
    }

    @JsonGetter
    abstract public E getParent();

    @JsonGetter
    abstract public NetworkRuleform<E> getPremise1();

    @JsonGetter
    abstract public NetworkRuleform<E> getPremise2();

    /**
     * @return the relationship
     */
    public Relationship getRelationship() {
        return relationship;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result
                 + (getParent() == null ? 0 : getParent().hashCode());
        result = prime * result
                 + (relationship == null ? 0 : relationship.hashCode());
        result = prime * result
                 + (getChild() == null ? 0 : getChild().hashCode());
        return result;
    }

    @JsonIgnore
    public boolean isInferred() {
        if (inference == null) {
            return false;
        }
        return !ZERO.equals(inference.getId());
    }

    abstract public void setChild(E child);

    abstract public void setParent(E parent);

    /**
     * @param relationship
     *            the relationship to set
     */
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    @Override
    public String toString() {
        return String.format("%s[%s] %s >> %s >> %s: %s",
                             this.getClass().getSimpleName(), getId(),
                             getParent().getName(),
                             getRelationship().getName(), getChild().getName(),
                             isInferred());
    }
}
