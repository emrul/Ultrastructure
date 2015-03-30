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
package com.chiralbehaviors.CoRE;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The superclass of all rule forms.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@JsonIdentityInfo(generator = RuleformIdGenerator.class, property = "@id")
@JsonAutoDetect(fieldVisibility = Visibility.PUBLIC_ONLY)
abstract public class Ruleform implements Serializable, Cloneable {
    public static final Integer FALSE                 = Integer.valueOf((byte) 0);

    public static final String  FIND_ALL_SUFFIX       = ".findAll";

    public static final String  FIND_BY_ID_SUFFIX     = ".findById";
    public static final String  FIND_BY_NAME_SUFFIX   = ".findByName";
    public static final String  FIND_FLAGGED_SUFFIX   = ".findFlagged";
    public static final String  GET_UPDATED_BY_SUFFIX = ".getUpdatedBy";
    public static final Integer TRUE                  = Integer.valueOf((byte) 1);
    public static final String  ZERO                  = UuidGenerator.toBase64(new UUID(
                                                                                        0,
                                                                                        0));
    private static final long   serialVersionUID      = 1L;

    public static Boolean toBoolean(Integer value) {
        if (value == null) {
            return null;
        }
        return value.equals(Integer.valueOf(0)) ? Boolean.FALSE : Boolean.TRUE;
    }

    public static Integer toInteger(Boolean value) {
        if (value == null) {
            return null;
        }
        return value ? TRUE : FALSE;
    }

    @Id
    private String   id = UuidGenerator.nextId();

    private String   notes;

    @Version
    @Column(name = "version")
    private int      version;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "updated_by")
    protected Agency updatedBy;

    public Ruleform() {
    }

    public Ruleform(Agency updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Ruleform(String notes) {
        this.notes = notes;
    }

    public Ruleform(String notes, Agency updatedBy) {
        this.notes = notes;
        this.updatedBy = updatedBy;
    }

    public Ruleform(UUID id) {
        this();
        setId(id);
    }

    public Ruleform(UUID id, Agency updatedBy) {
        this(id);
        this.updatedBy = updatedBy;
    }

    @Override
    public Ruleform clone() {
        Ruleform clone;
        try {
            clone = (Ruleform) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException("Unable to clone");
        }
        clone.setId((String) null);
        return clone;
    }

    public void delete(Triggers triggers) {
        // default is to do nothing;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Ruleform other = (Ruleform) obj;
        String id = getPrimaryKey();
        if (id == null) {
            if (other.getPrimaryKey() != null) {
                return false;
            }
        } else if (!id.equals(other.getPrimaryKey())) {
            return false;
        }
        return true;
    }

    @JsonGetter
    public final String getId() {
        return getPrimaryKey();
    }

    /**
     * @return the notes
     */
    @JsonGetter
    public String getNotes() {
        return notes;
    }

    @JsonIgnore
    public final String getPrimaryKey() {
        return id;
    }

    /**
     * @return the updatedBy
     */
    @JsonGetter
    public Agency getUpdatedBy() {
        return updatedBy;
    }

    @JsonIgnore
    public final UUID getUUID() {
        String primaryKey = getPrimaryKey();
        return primaryKey == null ? null : UuidGenerator.fromBase64(primaryKey);
    }

    /**
     * @return the version
     */
    @JsonIgnore
    public int getVersion() {
        return version;
    }

    @JsonIgnore
    abstract public SingularAttribute<WorkspaceAuthorization, ? extends Ruleform> getWorkspaceAuthAttribute();

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        if (getPrimaryKey() == null) {
            return 31;
        }
        return getPrimaryKey().hashCode();
    }

    public void persist(Triggers triggers) {
        // default is to do nothing
    }

    @JsonProperty
    public void setId(String id) {
        setPrimaryKey(id);
    }

    @JsonIgnore
    public void setId(UUID id) {
        setPrimaryKey(UuidGenerator.toBase64(id));
    }

    /**
     * @param notes
     *            the notes to set
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * @param updatedBy
     *            the updatedBy to set
     */
    public void setUpdatedBy(Agency updatedBy) {
        this.updatedBy = updatedBy;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return String.format("%s[%s]", getClass(), getId());
    }

    public void update(Triggers triggers) {
        // default is to do nothing;
    }

    protected final void setPrimaryKey(String id) {
        this.id = id;
    }
}
