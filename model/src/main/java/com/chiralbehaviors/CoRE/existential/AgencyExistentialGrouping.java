/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.existential;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Any;
import org.hibernate.annotations.AnyMetaDef;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.MetaValue;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.existential.domain.Agency;
import com.chiralbehaviors.CoRE.existential.domain.Attribute;
import com.chiralbehaviors.CoRE.existential.domain.Interval;
import com.chiralbehaviors.CoRE.existential.domain.Location;
import com.chiralbehaviors.CoRE.existential.domain.Product;
import com.chiralbehaviors.CoRE.existential.domain.Relationship;
import com.chiralbehaviors.CoRE.existential.domain.StatusCode;
import com.chiralbehaviors.CoRE.existential.domain.Unit;
import com.fasterxml.jackson.annotation.JsonGetter;

/**
 * @author hhildebrand
 *
 */
@Entity
@Table(name = "agency_existential_grouping", schema = "ruleform")
public class AgencyExistentialGrouping<RuleForm extends ExistentialRuleform<RuleForm>>
        extends Ruleform {

    private static final long serialVersionUID = 1L;

    @Any(metaColumn = @Column(name = "domain"))
    @AnyMetaDef(idType = "pg-uuid", metaType = "char", metaValues = { @MetaValue(targetEntity = Agency.class, value = "A"),
                                                                      @MetaValue(targetEntity = Attribute.class, value = "T"),
                                                                      @MetaValue(targetEntity = Interval.class, value = "I"),
                                                                      @MetaValue(targetEntity = Location.class, value = "L"),
                                                                      @MetaValue(targetEntity = Product.class, value = "P"),
                                                                      @MetaValue(targetEntity = Relationship.class, value = "R"),
                                                                      @MetaValue(targetEntity = StatusCode.class, value = "S"),
                                                                      @MetaValue(targetEntity = Unit.class, value = "U") })
    @Cascade({ org.hibernate.annotations.CascadeType.ALL })
    @JoinColumn(name = "entity")
    private RuleForm          entity;

    @ManyToOne(cascade = { CascadeType.PERSIST,
                           CascadeType.DETACH }, fetch = FetchType.LAZY)
    @JoinColumn(name = "grouping_agency")
    private Agency            groupingAgency;

    @JsonGetter
    public RuleForm getEntity() {
        return entity;
    }

    @JsonGetter
    public Agency getGroupingAgency() {
        return groupingAgency;
    }

    public void setEntity(RuleForm entity) {
        this.entity = entity;
    }

    public void setGroupingAgency(Agency groupingAgency) {
        this.groupingAgency = groupingAgency;
    }

}
