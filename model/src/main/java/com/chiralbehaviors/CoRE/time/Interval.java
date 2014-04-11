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
package com.chiralbehaviors.CoRE.time;

import static com.chiralbehaviors.CoRE.Ruleform.NAME_SEARCH_SUFFIX;
import static com.chiralbehaviors.CoRE.time.Interval.FIND_BY_NAME;
import static com.chiralbehaviors.CoRE.time.Interval.FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.time.Interval.FIND_CLASSIFIED_ATTRIBUTE_VALUES;
import static com.chiralbehaviors.CoRE.time.Interval.FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS;
import static com.chiralbehaviors.CoRE.time.Interval.GET_ALL_PARENT_RELATIONSHIPS;
import static com.chiralbehaviors.CoRE.time.Interval.GET_CHILD;
import static com.chiralbehaviors.CoRE.time.Interval.GET_CHILD_RULES_BY_RELATIONSHIP;
import static com.chiralbehaviors.CoRE.time.Interval.ORDERED_ATTRIBUTES;
import static com.chiralbehaviors.CoRE.time.Interval.UNLINKED;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.ExistentialRuleform;
import com.chiralbehaviors.CoRE.NameSearchResult;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attributable;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * An interval in time.
 * 
 * @author hhildebrand
 * 
 */
@NamedNativeQueries({
                     @NamedNativeQuery(name = UNLINKED, query = "SELECT unlinked.* "
                                                                + "FROM interval AS unlinked "
                                                                + "JOIN ("
                                                                + "     SELECT id "
                                                                + "     FROM interval "
                                                                + "     EXCEPT ("
                                                                + "             SELECT distinct(net.child) "
                                                                + "             FROM interval_network as net "
                                                                + "             WHERE net.parent = interval_id('Agency') "
                                                                + "             AND relationship = relationship_id('includes') "
                                                                + "     )"
                                                                + ") AS linked ON unlinked.id = linked.id "
                                                                + "WHERE unlinked.id != interval_id('Agency');", resultClass = Agency.class),
                     // ?1 = :queryString, ?2 = :numberOfMatches
                     @NamedNativeQuery(name = "interval" + NAME_SEARCH_SUFFIX, query = "SELECT id, name, description FROM ruleform.existential_name_search('interval', :queryString, :numberOfMatches)", resultClass = NameSearchResult.class) })
@NamedQueries({
               @NamedQuery(name = ORDERED_ATTRIBUTES, query = "select ca from IntervalAttribute as ca where ca.interval = :interval"),
               @NamedQuery(name = FIND_BY_NAME, query = "select e from Agency e where e.name = :name"),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_VALUES, query = "SELECT "
                                                                            + "  attrValue "
                                                                            + "FROM "
                                                                            + "       IntervalAttribute attrValue, "
                                                                            + "       IntervalAttributeAuthorization auth, "
                                                                            + "       IntervalNetwork network "
                                                                            + "WHERE "
                                                                            + "        auth.authorizedAttribute = attrValue.attribute AND "
                                                                            + "        network.relationship = auth.classification AND "
                                                                            + "        network.child = auth.classifier AND"
                                                                            + "        attrValue.interval = :ruleform AND "
                                                                            + "        auth.classification = :classification AND "
                                                                            + "        auth.classifier = :classifier "),
               @NamedQuery(name = FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from IntervalAttributeAuthorization ra "
                                                                                    + "WHERE ra.classifier = :classification "
                                                                                    + "AND ra.classifier = :classifier"),
               @NamedQuery(name = FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS, query = "select ra from IntervalAttributeAuthorization ra "
                                                                                 + "WHERE ra.groupingAgency = :groupingAgency"),
               @NamedQuery(name = GET_CHILD, query = "SELECT n.child "
                                                     + "FROM IntervalNetwork n "
                                                     + "WHERE n.parent = :p "
                                                     + "AND n.relationship = :r"),
               @NamedQuery(name = GET_ALL_PARENT_RELATIONSHIPS, query = "SELECT n "
                                                                        + "FROM IntervalNetwork n "
                                                                        + "WHERE n.child = :c"),
               @NamedQuery(name = GET_CHILD_RULES_BY_RELATIONSHIP, query = "SELECT n FROM IntervalNetwork n "
                                                                           + "WHERE n.parent = :interval "
                                                                           + "AND n.relationship IN :relationships "
                                                                           + "ORDER by n.parent.name, n.relationship.name, n.child.name") })
@Entity
@Table(name = "interval", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "interval_id_seq", sequenceName = "interval_id_seq")
public class Interval extends ExistentialRuleform<Interval, IntervalNetwork>
        implements Attributable<IntervalAttribute> {

    public static final String     AGENCY_ATTRIBUTES_BY_CLASSIFICATION      = "interval.IntervalAttributesByClassification";

    public static final String     AUTHORIZED_AGENCY_ATTRIBUTES             = "interval.authorizedAttributes";
    public static final String     FIND_BY_NAME                             = "interval"
                                                                              + FIND_BY_NAME_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS = "interval"
                                                                              + FIND_CLASSIFIED_ATTRIBUTE_AUTHORIZATIONS_SUFFIX;
    public static final String     FIND_CLASSIFIED_ATTRIBUTE_VALUES         = "interval"
                                                                              + FIND_CLASSIFIED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String     FIND_GROUPED_ATTRIBUTE_AUTHORIZATIONS    = "interval"
                                                                              + FIND_GROUPED_ATTRIBUTE_VALUES_SUFFIX;
    public static final String     GET_ALL_PARENT_RELATIONSHIPS             = "interval"
                                                                              + GET_ALL_PARENT_RELATIONSHIPS_SUFFIX;
    public static final String     GET_CHILD                                = "interval"
                                                                              + GET_CHILDREN_SUFFIX;
    public static final String     GET_CHILD_RULES_BY_RELATIONSHIP          = "interval"
                                                                              + GET_CHILD_RULES_BY_RELATIONSHIP_SUFFIX;
    public static final String     ORDERED_ATTRIBUTES                       = "interval.orderedAttributes";
    public static final String     QUALIFIED_ENTITY_NETWORK_RULES           = "interval.qualifiedEntityNetworkRules";
    public static final String     UNLINKED                                 = "interval"
                                                                              + UNLINKED_SUFFIX;
    private static final long      serialVersionUID                         = 1L;

    // bi-directional many-to-one association to IntervalAttribute
    @OneToMany(mappedBy = "interval", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<IntervalAttribute> attributes;

    private BigDecimal             duration;

    @Id
    @GeneratedValue(generator = "interval_id_seq", strategy = GenerationType.SEQUENCE)
    private Long                   id;

    // bi-directional many-to-one association to IntervalNetwork
    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<IntervalNetwork>   networkByChild;

    // bi-directional many-to-one association to IntervalNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<IntervalNetwork>   networkByParent;

    private BigDecimal             start;

    public Interval() {
        super();
    }

    public Interval(Agency updatedBy) {
        super(updatedBy);
    }

    public Interval(BigDecimal start, BigDecimal duration, String name,
                    Agency updatedBy) {
        this(start, duration, name, null, updatedBy);
    }

    public Interval(BigDecimal start, BigDecimal duration, String name,
                    String description, Agency updatedBy) {
        this(name, description, updatedBy);
    }

    public Interval(Long id) {
        super(id);
    }

    public Interval(Long id, Agency updatedBy) {
        super(id, updatedBy);
    }

    public Interval(String name) {
        super(name);
    }

    public Interval(String name, Agency updatedBy) {
        super(name, updatedBy);
    }

    public Interval(String name, String description) {
        super(name, description);
    }

    public Interval(String name, String description, Agency updatedBy) {
        super(name, description, updatedBy);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.network.Networked#addChildRelationship(com.
     * chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addChildRelationship(IntervalNetwork relationship) {
        relationship.setChild(this);
        networkByChild.add(relationship);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#addParentRelationship(com.
     * chiralbehaviors .CoRE.network.NetworkRuleform)
     */
    @Override
    public void addParentRelationship(IntervalNetwork relationship) {
        relationship.setParent(this);
        networkByParent.add(relationship);
    }

    @Override
    public Interval clone() {
        Interval clone = (Interval) super.clone();
        clone.networkByChild = null;
        clone.networkByParent = null;
        clone.attributes = null;
        return clone;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.Attributable#getAttributes()
     */
    @Override
    public Set<IntervalAttribute> getAttributes() {
        return attributes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.Attributable#getAttributeType()
     */
    @Override
    public Class<IntervalAttribute> getAttributeType() {
        return IntervalAttribute.class;
    }

    /**
     * @return the duration
     */
    public BigDecimal getDuration() {
        return duration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Ruleform#getId()
     */
    @Override
    public Long getId() {
        return id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.network.Networked#getNetworkByChild()
     */
    @Override
    public Set<IntervalNetwork> getNetworkByChild() {
        if (networkByChild == null) {
            return Collections.emptySet();
        }
        return networkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.network.Networked#getNetworkByParent()
     */
    @Override
    public Set<IntervalNetwork> getNetworkByParent() {
        if (networkByParent == null) {
            return Collections.emptySet();
        }
        return networkByParent;
    }

    /**
     * @return the start
     */
    public BigDecimal getStart() {
        return start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#link(com.chiralbehaviors.CoRE
     * .network .Relationship, com.chiralbehaviors.CoRE.network.Networked,
     * com.chiralbehaviors.CoRE.agency.Agency,
     * com.chiralbehaviors.CoRE.agency.Agency, javax.persistence.EntityManager)
     */
    @Override
    public void link(Relationship r, Interval child, Agency updatedBy,
                     Agency inverseSoftware, EntityManager em) {
        assert r != null : "Relationship cannot be null";
        assert child != null;
        assert updatedBy != null;
        assert em != null;

        IntervalNetwork link = new IntervalNetwork(this, r, child, updatedBy);
        em.persist(link);
        IntervalNetwork inverse = new IntervalNetwork(child, r.getInverse(),
                                                      this, inverseSoftware);
        em.persist(inverse);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.attribute.Attributable#setAttributes(java.util
     * .Set)
     */
    @Override
    public void setAttributes(Set<IntervalAttribute> attributes) {
        this.attributes = attributes;
    }

    /**
     * @param duration
     *            the duration to set
     */
    public void setDuration(BigDecimal duration) {
        this.duration = duration;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.Ruleform#setId(java.lang.Long)
     */
    @Override
    public void setId(Long id) {
        this.id = id;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#setNetworkByChild(java.util
     * .Set)
     */
    @Override
    public void setNetworkByChild(Set<IntervalNetwork> networkByChild) {
        this.networkByChild = networkByChild;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.network.Networked#setNetworkByParent(java.util
     * .Set)
     */
    @Override
    public void setNetworkByParent(Set<IntervalNetwork> networkByParent) {
        this.networkByParent = networkByParent;
    }

    /**
     * @param start
     *            the start to set
     */
    public void setStart(BigDecimal start) {
        this.start = start;
    }
}