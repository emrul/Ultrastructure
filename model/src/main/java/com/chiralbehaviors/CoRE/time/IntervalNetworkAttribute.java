/**
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.time;

import java.math.BigDecimal;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization_;
import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * The attribute value for product attributes
 *
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "interval_network_attribute", schema = "ruleform")
public class IntervalNetworkAttribute extends AttributeValue<IntervalNetwork> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "agency")
    private Agency            agency;

    // bi-directional many-to-one association to Attribute
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "attribute")
    private Attribute         attribute;

    // bi-directional many-to-one association to IntervalNetwork
    @ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.DETACH })
    @JoinColumn(name = "network_rule")
    private IntervalNetwork   IntervalNetwork;

    public IntervalNetworkAttribute() {
    }

    /**
     * @param updatedBy
     */
    public IntervalNetworkAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     */
    public IntervalNetworkAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public IntervalNetworkAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public IntervalNetworkAttribute(Attribute attribute, BigDecimal value,
                                    Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public IntervalNetworkAttribute(Attribute attribute, boolean value,
                                    Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public IntervalNetworkAttribute(Attribute attribute, int value,
                                    Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public IntervalNetworkAttribute(Attribute attribute, String value,
                                    Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public IntervalNetworkAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public IntervalNetworkAttribute(UUID id) {
        super(id);
    }

    @JsonGetter
    public Agency getAgency() {
        return agency;
    }

    @Override
    public Attribute getAttribute() {
        return attribute;
    }

    @JsonGetter
    public IntervalNetwork getIntervalNetwork() {
        return IntervalNetwork;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<IntervalNetworkAttribute, IntervalNetwork> getRuleformAttribute() {
        return IntervalNetworkAttribute_.IntervalNetwork;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<IntervalNetwork> getRuleformClass() {
        return IntervalNetwork.class;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Ruleform#getWorkspaceAuthAttribute()
     */
    @Override
    @JsonIgnore
    public SingularAttribute<WorkspaceAuthorization, IntervalNetworkAttribute> getWorkspaceAuthAttribute() {
        return WorkspaceAuthorization_.intervalNetworkAttribute;
    }

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    @Override
    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public void setIntervalNetwork(IntervalNetwork IntervalNetwork) {
        this.IntervalNetwork = IntervalNetwork;
    }
}