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

package com.hellblazer.CoRE.meta;

import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeValue;
import com.hellblazer.CoRE.attribute.ClassifiedAttributeAuthorization;
import com.hellblazer.CoRE.network.Aspect;
import com.hellblazer.CoRE.network.Facet;
import com.hellblazer.CoRE.network.NetworkRuleform;
import com.hellblazer.CoRE.network.Relationship;

/**
 * 
 * The meta model for a networked ruleform.
 * 
 * @author hhildebrand
 * 
 */
public interface NetworkedModel<RuleForm extends ExistentialRuleform<RuleForm, Network>, Network extends NetworkRuleform<RuleForm>, AttributeAuthorization extends ClassifiedAttributeAuthorization<RuleForm>, AttributeType extends AttributeValue<RuleForm>> {

    /**
     * Create a new instance with the supplied aspects
     * 
     * @param name
     *            The name of the new instance
     * @param description
     *            the description of the new instance
     * @param aspects
     *            - the initial aspects of the instance
     * 
     * @return the new instance
     */
    public RuleForm create(String name, String description,
                           Aspect<RuleForm> aspect, @SuppressWarnings("unchecked") Aspect<RuleForm>... aspects);

    public RuleForm getSingleChild(RuleForm parent, Relationship r);

    /**
     * Answer the list of unlinked
     * 
     * @return
     */
    public List<RuleForm> getUnlinked();

    /**
     * Answer the list of relationships used in this ruleform's networks.
     * 
     * @return
     */
    public List<Relationship> getUsedRelationships();

    /**
     * Assign the attributes as authorized atrributes of the aspect
     * 
     * @param aspect
     * @param attributes
     */
    void authorize(Aspect<RuleForm> aspect, Attribute... attributes);

    /**
     * Create a new instance of the RuleForm based on the provided prototype
     * 
     * @param prototype
     *            - the model for the new instance
     * @return the new instance
     */
    RuleForm create(RuleForm prototype);
    
    /**
     * @param id
     * @return the ruleform with the specified id
     */
    RuleForm find(long id);

    /**
     * 
     * @return all existential ruleforms that exist for this model
     */
    List<RuleForm> findAll();

    /**
     * Answer the allowed values for an Attribute, classified by the supplied
     * aspect
     * 
     * @param attribute
     *            - the Attribute
     * @param groupingAgency
     *            - the grouping Agency
     * @return the List of allowed values for this attribute
     */
    <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                 Agency groupingAgency);

    /**
     * Answer the allowed values for an Attribute, classified by the supplied
     * aspect
     * 
     * @param attribute
     *            - the Attribute
     * @param aspect
     *            - the classifying aspect
     * @return the List of allowed values for this attribute
     */
    <ValueType> List<ValueType> getAllowedValues(Attribute attribute,
                                                 Aspect<RuleForm> aspect);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency
     * 
     * @param groupingAgency
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Agency groupingAgency);

    /**
     * Answer the list of attribute authorizations that are classified by the
     * grouping agency, defined for the particular attribute
     * 
     * @param groupingAgency
     * @param attribute
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Agency groupingAgency,
                                                            Attribute attribute);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect
     * 
     * @param aspect
     *            - the classifying aspect.
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Aspect<RuleForm> aspect);

    /**
     * Answer the list of attribute authorizations that are classified by an
     * aspect, defined for the particular attribute
     * 
     * @param aspect
     *            - the classifying aspect.
     * @param attribute
     * @return
     */
    List<AttributeAuthorization> getAttributeAuthorizations(Aspect<RuleForm> aspect,
                                                            Attribute attribute);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * authorized by the groupingAgency
     * 
     * @param ruleform
     *            - the instance
     * @param groupingAgency
     *            - the classifying agency
     * @return the list of existing attributes authorized by this classification
     */
    List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                  Agency groupingAgency);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * authorized by the classification relationship described by the aspect
     * 
     * @param ruleform
     *            - the instance
     * @param aspect
     *            - the classifying aspect
     * @return the list of existing attributes authorized by this classification
     */
    List<AttributeType> getAttributesClassifiedBy(RuleForm ruleform,
                                                  Aspect<RuleForm> aspect);

    /**
     * Answer the list of existing attributes for the ruleform instance that are
     * grouped by the given agency
     * 
     * @param ruleform
     *            - the instance
     * @param groupingAgency
     *            - the agency
     * @return the list of existing attributes for this instance that are
     *         grouped by the given agency
     */
    List<AttributeType> getAttributesGroupedBy(RuleForm ruleform,
                                               Agency groupingAgency);

    /**
     * 
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getChildren(RuleForm parent, Relationship relationship);

    /**
     * Answer the Facet of the ruleform instance containing the authorized
     * attributes as classified
     * 
     * @param ruleform
     *            - the instance
     * @param classifier
     *            - the parent ruleform
     * @param classification
     *            - the classifying relationship
     * @return
     */
    Facet<RuleForm, AttributeType> getFacet(RuleForm ruleform,
                                            Aspect<RuleForm> aspect);

    /**
     * 
     * @param parent
     * @return
     */
    Collection<Network> getImmediateNetworkEdges(RuleForm parent);

    /**
     * 
     * @param parent
     * @return
     */
    Collection<Relationship> getImmediateRelationships(RuleForm parent);

    /**
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getInGroup(RuleForm parent, Relationship relationship);

    /**
     * 
     * @param parent
     * @param relationship
     * @return
     */
    List<RuleForm> getNotInGroup(RuleForm parent, Relationship relationship);

    /**
     * 
     * @param parent
     * @return
     */
    Collection<Relationship> getTransitiveRelationships(RuleForm parent);

    /**
     * 
     * @param parent
     * @param parentRelationship
     * @param authorizingRelationship
     * @param child
     * @return
     */
    boolean isAccessible(RuleForm parent, Relationship parentRelationship,
                         Relationship authorizingRelationship,
                         ExistentialRuleform<?, ?> child,
                         Relationship childRelationship);

    /**
     * 
     * @param parent
     * @param r
     * @param child
     * @param updatedBy
     */
    void link(RuleForm parent, Relationship r, RuleForm child, Agency updatedBy);

    /**
     * Track the deleted network edge
     * 
     * @param parent
     * @param relationship
     */
    void networkEdgeDeleted(long parent, long relationship);

    /**
     * Propagate the network inferences based on the tracked additions,
     * deletions and modifications
     * 
     * @throws SQLException
     */
    void propagate();
}
