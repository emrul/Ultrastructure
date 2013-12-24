/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.event.status;

import static com.hellblazer.CoRE.event.status.StatusCodeSequencing.ENSURE_VALID_SERVICE_STATUS;
import static com.hellblazer.CoRE.event.status.StatusCodeSequencing.GET_ALL_STATUS_CODE_SEQUENCING;
import static com.hellblazer.CoRE.event.status.StatusCodeSequencing.GET_CHILD_STATUS_CODES;
import static com.hellblazer.CoRE.event.status.StatusCodeSequencing.GET_CHILD_STATUS_CODE_SEQUENCING;
import static com.hellblazer.CoRE.event.status.StatusCodeSequencing.GET_PARENT_STATUS_CODES;
import static com.hellblazer.CoRE.event.status.StatusCodeSequencing.GET_PARENT_STATUS_CODE_SEQUENCING;
import static com.hellblazer.CoRE.event.status.StatusCodeSequencing.IS_VALID_NEXT_STATUS;

import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * The persistent class for the status_code_sequencing database table.
 * 
 */
@NamedQueries({
               @NamedQuery(name = ENSURE_VALID_SERVICE_STATUS, query = "SELECT COUNT(scs.id) "
                                                                       + "FROM StatusCodeSequencing AS scs "
                                                                       + "WHERE scs.service = :service "
                                                                       + "  AND (scs.parentCode = :code "
                                                                       + "       OR scs.childCode = :code)"),
               @NamedQuery(name = IS_VALID_NEXT_STATUS, query = "SELECT COUNT(scs.id) "
                                                                + "FROM StatusCodeSequencing AS scs "
                                                                + "WHERE scs.service = :service "
                                                                + "  AND scs.parentCode = :parentCode "
                                                                + "  AND scs.childCode = :childCode"),
               @NamedQuery(name = GET_PARENT_STATUS_CODES, query = "SELECT DISTINCT(scs.parentCode) "
                                                                   + " FROM StatusCodeSequencing scs "
                                                                   + " WHERE scs.service = :service"),
               @NamedQuery(name = GET_CHILD_STATUS_CODES, query = "SELECT DISTINCT(scs.childCode) "
                                                                  + " FROM StatusCodeSequencing scs "
                                                                  + " WHERE scs.service = :service"),
               @NamedQuery(name = GET_CHILD_STATUS_CODE_SEQUENCING, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                            + " WHERE scs.service = :service"
                                                                            + "   AND scs.childCode = :statusCode"),
               @NamedQuery(name = GET_PARENT_STATUS_CODE_SEQUENCING, query = "SELECT scs FROM StatusCodeSequencing scs "
                                                                             + " WHERE scs.service = :service"
                                                                             + "   AND scs.parentCode = :statusCode"),
               @NamedQuery(name = GET_ALL_STATUS_CODE_SEQUENCING, query = "SELECT scs "
                                                                          + " FROM StatusCodeSequencing scs "
                                                                          + " WHERE scs.service = :service") })
@Entity
@Table(name = "status_code_sequencing", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "status_code_sequencing_id_seq", sequenceName = "status_code_sequencing_id_seq")
public class StatusCodeSequencing extends Ruleform {
    public static final String  ENSURE_VALID_SERVICE_STATUS       = "statusCodeSequencing.ensureValidServiceAndStatus";
    public static final String  GET_CHILD_STATUS_CODES            = "statusCodeSequencing.getChildStatusCodes";
    public static final String  GET_PARENT_STATUS_CODES           = "statusCodeSequencing.getParentStatusCodes";
    public static final String  GET_ALL_STATUS_CODE_SEQUENCING    = "statusCodeSequencing.getAllStatusCodeSequencing";
    public static final String  GET_CHILD_STATUS_CODE_SEQUENCING  = "statusCodeSequencing.getChildStatusCodeSequencing";
    public static final String  GET_PARENT_STATUS_CODE_SEQUENCING = "statusCodeSequencing.getParentStatusCodeSequencing";
    public static final String  IS_VALID_NEXT_STATUS              = "statusCodeSequencing.isValidNextStatus";

    private static final long   serialVersionUID                  = 1L;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "child_code")
    private StatusCode          childCode;

    @Id
    @GeneratedValue(generator = "status_code_sequencing_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                id;

    //bi-directional many-to-one association to StatusCode
    @ManyToOne
    @JoinColumn(name = "parent_code")
    private StatusCode          parentCode;

    @Column(name = "sequence_number")
    private Integer             sequenceNumber                    = 1;

    //bi-directional many-to-one association to Event
    @ManyToOne(optional = false)
    @JoinColumn(name = "service")
    private Product             service;

    //bi-directional many-to-one association to StatusCode
    @OneToMany(mappedBy = "child")
    @JsonIgnore
    private Set<ProductNetwork> statusCodeByChild;

    //bi-directional many-to-one association to StatusCode
    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    private Set<ProductNetwork> statusCodeByParent;

    public StatusCodeSequencing() {
    }

    /**
     * @param updatedBy
     */
    public StatusCodeSequencing(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param id
     */
    public StatusCodeSequencing(Long id) {
        super(id);
    }

    /**
     * @param id
     * @param updatedBy
     */
    public StatusCodeSequencing(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public StatusCodeSequencing(Product service, StatusCode parent,
                                StatusCode child, Agency updatedBy) {
        super(updatedBy);
        this.service = service;
        parentCode = parent;
        childCode = child;
    }

    public StatusCodeSequencing(Product service, StatusCode parent,
                                StatusCode child, int sequenceNumber,
                                Agency updatedBy) {
        this(service, parent, child, updatedBy);
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param notes
     */
    public StatusCodeSequencing(String notes) {
        super(notes);
    }

    /**
     * @param notes
     * @param updatedBy
     */
    public StatusCodeSequencing(String notes, Agency updatedBy) {
        super(notes, updatedBy);
    }

    public StatusCode getChildCode() {
        return childCode;
    }

    @Override
    public Long getId() {
        return id;
    }

    public StatusCode getParentCode() {
        return parentCode;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @return the service
     */
    public Product getService() {
        return service;
    }

    /**
     * @return the statusCodeByChild
     */
    public Set<ProductNetwork> getStatusCodeByChild() {
        return statusCodeByChild;
    }

    /**
     * @return the statusCodeByParent
     */
    public Set<ProductNetwork> getStatusCodeByParent() {
        return statusCodeByParent;
    }

    public void setChildCode(StatusCode statusCode1) {
        childCode = statusCode1;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setParentCode(StatusCode statusCode2) {
        parentCode = statusCode2;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    /**
     * @param service
     *            the service to set
     */
    public void setService(Product service) {
        this.service = service;
    }

    /**
     * @param statusCodeByChild
     *            the statusCodeByChild to set
     */
    public void setStatusCodeByChild(Set<ProductNetwork> statusCodeByChild) {
        this.statusCodeByChild = statusCodeByChild;
    }

    /**
     * @param statusCodeByParent
     *            the statusCodeByParent to set
     */
    public void setStatusCodeByParent(Set<ProductNetwork> statusCodeByParent) {
        this.statusCodeByParent = statusCodeByParent;
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (childCode != null) {
            childCode = (StatusCode) childCode.manageEntity(em, knownObjects);
        }
        if (parentCode != null) {
            parentCode = (StatusCode) parentCode.manageEntity(em, knownObjects);
        }
        if (service != null) {
            service = (Product) service.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}