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
package com.chiralbehaviors.CoRE.attribute;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 *
 * The abstract authorization for attributes on entities.
 *
 * @author hhildebrand
 *
 */
@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
abstract public class AttributeAuthorization extends Ruleform {

    private static final long serialVersionUID = 1L;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "authorized_attribute")
    private Attribute         authorizedAttribute;

    @Column(name = "binary_value")
    private byte[]            binaryValue;

    @Column(name = "boolean_value")
    private Integer           booleanValue;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "grouping_agency")
    private Agency            groupingAgency;

    @Column(name = "integer_value")
    private Integer           integerValue;

    @Column(name = "numeric_value")
    private BigDecimal        numericValue;

    @Column(name = "sequence_number")
    private Integer           sequenceNumber   = 1;

    @Column(name = "text_value")
    private String            textValue;

    @Column(name = "timestamp_value")
    private Timestamp         timestampValue;

    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "validating_attribute")
    private Attribute         validatingAttribute;

    public AttributeAuthorization() {
        super();
    }

    /**
     * @param updatedBy
     */
    public AttributeAuthorization(Agency updatedBy) {
        super(updatedBy);
    }

    public AttributeAuthorization(Attribute authorized, Agency updatedBy) {
        super(updatedBy);
        authorizedAttribute = authorized;
    }

    /**
     * @param id
     */
    public AttributeAuthorization(UUID id) {
        super(id);
    }

    /**
     * @param updatedBy
     */
    public AttributeAuthorization(UUID id, Agency updatedBy) {
        super(id, updatedBy);
    }

    @JsonGetter
    public Attribute getAuthorizedAttribute() {
        return authorizedAttribute;
    }

    /**
     * @return the binaryValue
     */
    public byte[] getBinaryValue() {
        return binaryValue;
    }

    /**
     * @return the booleanValue
     */
    public Integer getBooleanValue() {
        return booleanValue;
    }

    @JsonGetter
    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public BigDecimal getNumericValue() {
        return numericValue;
    }

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public String getTextValue() {
        return textValue;
    }

    public Timestamp getTimestampValue() {
        return timestampValue;
    }

    public Attribute getValidatingAttribute() {
        return validatingAttribute;
    }

    public void setAuthorizedAttribute(Attribute productAttributeType3) {
        authorizedAttribute = productAttributeType3;
    }

    /**
     * @param binaryValue
     *            the binaryValue to set
     */
    public void setBinaryValue(byte[] binaryValue) {
        this.binaryValue = binaryValue;
    }

    /**
     * @param booleanValue
     *            the booleanValue to set
     */
    public void setBooleanValue(Integer booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setGroupingAgency(Agency agency) {
        groupingAgency = agency;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }

    public void setNumericValue(BigDecimal numericValue) {
        this.numericValue = numericValue;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setTextValue(String textValue) {
        this.textValue = textValue;
    }

    /**
     * @param timestampValue
     *            the timestampValue to set
     */
    public void setTimestampValue(Timestamp timestampValue) {
        this.timestampValue = timestampValue;
    }

    public void setValidatingAttribute(Attribute validatingAttribute) {
        this.validatingAttribute = validatingAttribute;
    }
}
