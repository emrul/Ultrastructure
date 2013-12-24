/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.json;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.hellblazer.CoRE.agency.Agency;
import com.hellblazer.CoRE.agency.AgencyAttributeAuthorization;
import com.hellblazer.CoRE.agency.AgencyNetwork;
import com.hellblazer.CoRE.agency.AgencyNetworkAuthorization;
import com.hellblazer.CoRE.agency.access.AgencyAttribute;
import com.hellblazer.CoRE.agency.access.AgencyLocationAccessAuthorization;
import com.hellblazer.CoRE.agency.access.AgencyProductAccessAuthorization;
import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.attribute.AttributeMetaAttribute;
import com.hellblazer.CoRE.attribute.AttributeMetaAttributeAuthorization;
import com.hellblazer.CoRE.attribute.AttributeNetwork;
import com.hellblazer.CoRE.attribute.Transformation;
import com.hellblazer.CoRE.attribute.TransformationMetarule;
import com.hellblazer.CoRE.attribute.ValueType;
import com.hellblazer.CoRE.attribute.unit.Unit;
import com.hellblazer.CoRE.attribute.unit.UnitValue;
import com.hellblazer.CoRE.coordinate.Coordinate;
import com.hellblazer.CoRE.coordinate.CoordinateAttribute;
import com.hellblazer.CoRE.coordinate.CoordinateAttributeAuthorization;
import com.hellblazer.CoRE.coordinate.CoordinateNesting;
import com.hellblazer.CoRE.event.Job;
import com.hellblazer.CoRE.event.JobAttribute;
import com.hellblazer.CoRE.event.JobChronology;
import com.hellblazer.CoRE.event.MetaProtocol;
import com.hellblazer.CoRE.event.ProductChildSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductParentSequencingAuthorization;
import com.hellblazer.CoRE.event.ProductSiblingSequencingAuthorization;
import com.hellblazer.CoRE.event.Protocol;
import com.hellblazer.CoRE.event.ProtocolAttribute;
import com.hellblazer.CoRE.event.ProtocolAttributeAuthorization;
import com.hellblazer.CoRE.event.status.StatusCode;
import com.hellblazer.CoRE.event.status.StatusCodeSequencing;
import com.hellblazer.CoRE.location.Location;
import com.hellblazer.CoRE.location.LocationAttribute;
import com.hellblazer.CoRE.location.LocationAttributeAuthorization;
import com.hellblazer.CoRE.location.LocationNetwork;
import com.hellblazer.CoRE.location.LocationNetworkAuthorization;
import com.hellblazer.CoRE.location.LocationRelationship;
import com.hellblazer.CoRE.location.access.LocationAgencyAccessAuthorization;
import com.hellblazer.CoRE.location.access.LocationProductAccessAuthorization;
import com.hellblazer.CoRE.network.NetworkInference;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductAttributeAuthorization;
import com.hellblazer.CoRE.product.ProductLocation;
import com.hellblazer.CoRE.product.ProductLocationAttribute;
import com.hellblazer.CoRE.product.ProductLocationAttributeAuthorization;
import com.hellblazer.CoRE.product.ProductLocationNetwork;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.product.ProductNetworkAttribute;
import com.hellblazer.CoRE.product.ProductNetworkAuthorization;
import com.hellblazer.CoRE.product.access.ProductAgencyAccessAuthorization;
import com.hellblazer.CoRE.product.access.ProductLocationAccessAuthorization;

/**
 * A class for deserializing ruleforms. If you want your ruleform deserialized
 * polymorphically, it must be registered here. It MUST descend from Ruleform in
 * order to be correctly deserialized.
 * 
 * For now, we're only deserializing to concrete types, but if you need an
 * abstract type, just add it in.
 * 
 * @author hparry
 * 
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
               @Type(value = Attribute.class, name = "attribute"),
               @Type(value = AttributeMetaAttribute.class, name = "attributeMetaAttribute"),
               @Type(value = AttributeMetaAttributeAuthorization.class, name = "attributeMetaAttributeAuthorization"),
               @Type(value = AttributeNetwork.class, name = "attributeNetwork"),
               @Type(value = Transformation.class, name = "transformation"),
               @Type(value = TransformationMetarule.class, name = "transformationMetarule"),
               @Type(value = Unit.class, name = "unit"),
               @Type(value = UnitValue.class, name = "unitValue"),
               @Type(value = ValueType.class, name = "valueType"),

               @Type(value = Coordinate.class, name = "coordinate"),
               @Type(value = CoordinateAttribute.class, name = "coordinateAttribute"),
               @Type(value = CoordinateAttributeAuthorization.class, name = "coordinateAttributeAuthorization"),
               @Type(value = CoordinateNesting.class, name = "coordinateNesting"),

               @Type(value = Job.class, name = "job"),
               @Type(value = JobAttribute.class, name = "jobAttribute"),
               @Type(value = JobChronology.class, name = "jobChronology"),
               @Type(value = MetaProtocol.class, name = "metaProtocol"),
               @Type(value = ProductChildSequencingAuthorization.class, name = "productChildSequencingAuthorization"),
               @Type(value = ProductParentSequencingAuthorization.class, name = "productParentSequencingAuthorization"),
               @Type(value = ProductSiblingSequencingAuthorization.class, name = "productSiblingSequencingAuthorization"),
               @Type(value = Protocol.class, name = "protocol"),
               @Type(value = ProtocolAttribute.class, name = "protocolAttribute"),
               @Type(value = ProtocolAttributeAuthorization.class, name = "protocolAttributeAuthorization"),
               @Type(value = StatusCode.class, name = "statusCode"),
               @Type(value = StatusCodeSequencing.class, name = "statusCodeSequencing"),

               @Type(value = Location.class, name = "location"),
               @Type(value = LocationAttribute.class, name = "locationAttribute"),
               @Type(value = LocationAttributeAuthorization.class, name = "locationAttributeAuthorization"),
               @Type(value = LocationNetwork.class, name = "locationNetwork"),
               @Type(value = LocationNetworkAuthorization.class, name = "locationNetworkAuthorization"),
               @Type(value = LocationRelationship.class, name = "locationRelationship"),
               @Type(value = LocationProductAccessAuthorization.class, name = "locationProductAccessAuthorization"),
               @Type(value = LocationAgencyAccessAuthorization.class, name = "locationAgencyAccessAuthorization"),

               @Type(value = NetworkInference.class, name = "networkInference"),
               @Type(value = Relationship.class, name = "relationship"),

               @Type(value = Product.class, name = "product"),
               @Type(value = ProductAttribute.class, name = "productAttribute"),
               @Type(value = ProductAttributeAuthorization.class, name = "productAttributeAuthorization"),
               @Type(value = ProductLocation.class, name = "productLocation"),
               @Type(value = ProductLocationAttribute.class, name = "productLocationAttribute"),
               @Type(value = ProductLocationAttributeAuthorization.class, name = "productLocationAttributeAuthorization"),
               @Type(value = ProductLocationNetwork.class, name = "productLocationNetwork"),
               @Type(value = ProductNetwork.class, name = "productNetwork"),
               @Type(value = ProductNetworkAttribute.class, name = "productNetworkAttribute"),
               @Type(value = ProductNetworkAuthorization.class, name = "productNetworkAuthorization"),
               @Type(value = ProductLocationAccessAuthorization.class, name = "productLocationAccessAuthorization"),
               @Type(value = ProductAgencyAccessAuthorization.class, name = "productAgencyAccessAuthorization"),

               @Type(value = Agency.class, name = "agency"),
               @Type(value = AgencyAttribute.class, name = "agencyAttribute"),
               @Type(value = AgencyAttributeAuthorization.class, name = "agencyAttributeAuthorization"),
               @Type(value = AgencyNetwork.class, name = "agencyNetwork"),
               @Type(value = AgencyNetworkAuthorization.class, name = "agencyNetworkAuthorization"),
               @Type(value = AgencyProductAccessAuthorization.class, name = "agencyProductAccessAuthorization"),
               @Type(value = AgencyLocationAccessAuthorization.class, name = "agencyLocationAccessAuthorization") })
public abstract class PolymorphicRuleformMixin {

}