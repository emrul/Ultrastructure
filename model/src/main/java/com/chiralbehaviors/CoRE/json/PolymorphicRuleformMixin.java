/**
 * AUTOGENERATED! DO NOT EDIT BY HAND.
 * Generated by PolymorphicMixinGenerator.java
 *
 */
package com.chiralbehaviors.CoRE.json;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.agency.AgencyAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyAttributeAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyLocation;
import com.chiralbehaviors.CoRE.agency.AgencyLocationAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyNetwork;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAttribute;
import com.chiralbehaviors.CoRE.agency.AgencyNetworkAuthorization;
import com.chiralbehaviors.CoRE.agency.AgencyProduct;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.AttributeNetwork;
import com.chiralbehaviors.CoRE.attribute.AttributeNetworkAttribute;
import com.chiralbehaviors.CoRE.attribute.AttributeNetworkAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetworkAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetworkAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitValue;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSelfSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetworkAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetworkAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttribute;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.LocationNetworkAttribute;
import com.chiralbehaviors.CoRE.location.LocationNetworkAuthorization;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.product.ProductLocationAttribute;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetworkAttribute;
import com.chiralbehaviors.CoRE.product.ProductNetworkAuthorization;
import com.chiralbehaviors.CoRE.relationship.Relationship;
import com.chiralbehaviors.CoRE.relationship.RelationshipAttribute;
import com.chiralbehaviors.CoRE.relationship.RelationshipAttributeAuthorization;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetwork;
import com.chiralbehaviors.CoRE.relationship.RelationshipNetworkAuthorization;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttribute;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.time.IntervalNetworkAttribute;
import com.chiralbehaviors.CoRE.time.IntervalNetworkAuthorization;
import com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
               @Type(value = Job.class, name = "job"),
               @Type(value = UnitAttributeAuthorization.class, name = "unitattributeauthorization"),
               @Type(value = ProductSiblingSequencingAuthorization.class, name = "productsiblingsequencingauthorization"),
               @Type(value = IntervalAttribute.class, name = "intervalattribute"),
               @Type(value = ProductParentSequencingAuthorization.class, name = "productparentsequencingauthorization"),
               @Type(value = AgencyLocation.class, name = "agencylocation"),
               @Type(value = AgencyLocationAttribute.class, name = "agencylocationattribute"),
               @Type(value = ProductSelfSequencingAuthorization.class, name = "productselfsequencingauthorization"),
               @Type(value = ProductAttributeAuthorization.class, name = "productattributeauthorization"),
               @Type(value = StatusCodeNetworkAttribute.class, name = "statuscodenetworkattribute"),
               @Type(value = AttributeMetaAttribute.class, name = "attributemetaattribute"),
               @Type(value = StatusCodeNetwork.class, name = "statuscodenetwork"),
               @Type(value = Relationship.class, name = "relationship"),
               @Type(value = AgencyAttribute.class, name = "agencyattribute"),
               @Type(value = Attribute.class, name = "attribute"),
               @Type(value = RelationshipNetwork.class, name = "relationshipnetwork"),
               @Type(value = StatusCodeAttribute.class, name = "statuscodeattribute"),
               @Type(value = UnitNetwork.class, name = "unitnetwork"),
               @Type(value = StatusCode.class, name = "statuscode"),
               @Type(value = JobChronology.class, name = "jobchronology"),
               @Type(value = AttributeMetaAttributeAuthorization.class, name = "attributemetaattributeauthorization"),
               @Type(value = AgencyNetworkAttribute.class, name = "agencynetworkattribute"),
               @Type(value = UnitValue.class, name = "unitvalue"),
               @Type(value = Interval.class, name = "interval"),
               @Type(value = WorkspaceAuthorization.class, name = "workspaceauthorization"),
               @Type(value = AttributeNetwork.class, name = "attributenetwork"),
               @Type(value = AgencyAttributeAuthorization.class, name = "agencyattributeauthorization"),
               @Type(value = LocationAttributeAuthorization.class, name = "locationattributeauthorization"),
               @Type(value = NetworkInference.class, name = "networkinference"),
               @Type(value = ProductLocation.class, name = "productlocation"),
               @Type(value = MetaProtocol.class, name = "metaprotocol"),
               @Type(value = StatusCodeSequencing.class, name = "statuscodesequencing"),
               @Type(value = ProductNetworkAttribute.class, name = "productnetworkattribute"),
               @Type(value = Unit.class, name = "unit"),
               @Type(value = UnitAttribute.class, name = "unitattribute"),
               @Type(value = Agency.class, name = "agency"),
               @Type(value = AgencyNetworkAuthorization.class, name = "agencynetworkauthorization"),
               @Type(value = RelationshipAttributeAuthorization.class, name = "relationshipattributeauthorization"),
               @Type(value = ProductNetworkAuthorization.class, name = "productnetworkauthorization"),
               @Type(value = ProductLocationAttribute.class, name = "productlocationattribute"),
               @Type(value = RelationshipAttribute.class, name = "relationshipattribute"),
               @Type(value = ProductChildSequencingAuthorization.class, name = "productchildsequencingauthorization"),
               @Type(value = IntervalNetwork.class, name = "intervalnetwork"),
               @Type(value = LocationNetwork.class, name = "locationnetwork"),
               @Type(value = AgencyNetwork.class, name = "agencynetwork"),
               @Type(value = ProductAttribute.class, name = "productattribute"),
               @Type(value = LocationNetworkAuthorization.class, name = "locationnetworkauthorization"),
               @Type(value = Protocol.class, name = "protocol"),
               @Type(value = LocationAttribute.class, name = "locationattribute"),
               @Type(value = IntervalAttributeAuthorization.class, name = "intervalattributeauthorization"),
               @Type(value = Location.class, name = "location"),
               @Type(value = UnitNetworkAttribute.class, name = "unitnetworkattribute"),
               @Type(value = IntervalNetworkAttribute.class, name = "intervalnetworkattribute"),
               @Type(value = Product.class, name = "product"),
               @Type(value = AgencyProduct.class, name = "agencyproduct"),
               @Type(value = StatusCodeAttributeAuthorization.class, name = "statuscodeattributeauthorization"),
               @Type(value = AttributeNetworkAttribute.class, name = "attributenetworkattribute"),
               @Type(value = ProductNetwork.class, name = "productnetwork"),
               @Type(value = LocationNetworkAttribute.class, name = "locationnetworkattribute"),
               @Type(value = AttributeNetworkAuthorization.class, name = "attributenetworkauthorization"),
               @Type(value = IntervalNetworkAuthorization.class, name = "intervalnetworkauthorization"),
               @Type(value = RelationshipNetworkAuthorization.class, name = "relationshipnetworkauthorization"),
               @Type(value = StatusCodeNetworkAuthorization.class, name = "statuscodenetworkauthorization"),
               @Type(value = UnitNetworkAuthorization.class, name = "unitnetworkauthorization"),

})
public abstract class PolymorphicRuleformMixin {
}
