/**
 * AUTOGENERATED! DO NOT EDIT BY HAND.
 * Generated by WorkspaceGenerator.java
 *
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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

package com.chiralbehaviors.CoRE.workspace;

import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.GET_AUTHORIZATION;
import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.GET_AUTHORIZATIONS_BY_TYPE;
import static com.chiralbehaviors.CoRE.workspace.WorkspaceAuthorization.GET_WORKSPACE;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import com.chiralbehaviors.CoRE.Ruleform;
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
import com.chiralbehaviors.CoRE.attribute.unit.Unit;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.UnitAttributeAuthorization;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetwork;
import com.chiralbehaviors.CoRE.attribute.unit.UnitNetworkAttribute;
import com.chiralbehaviors.CoRE.attribute.unit.UnitValue;
import com.chiralbehaviors.CoRE.event.Job;
import com.chiralbehaviors.CoRE.event.JobChronology;
import com.chiralbehaviors.CoRE.event.MetaProtocol;
import com.chiralbehaviors.CoRE.event.ProductChildSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductParentSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.ProductSiblingSequencingAuthorization;
import com.chiralbehaviors.CoRE.event.Protocol;
import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCodeAttributeAuthorization;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetwork;
import com.chiralbehaviors.CoRE.event.status.StatusCodeNetworkAttribute;
import com.chiralbehaviors.CoRE.event.status.StatusCodeSequencing;
import com.chiralbehaviors.CoRE.location.Location;
import com.chiralbehaviors.CoRE.location.LocationAttribute;
import com.chiralbehaviors.CoRE.location.LocationAttributeAuthorization;
import com.chiralbehaviors.CoRE.location.LocationNetwork;
import com.chiralbehaviors.CoRE.location.LocationNetworkAttribute;
import com.chiralbehaviors.CoRE.location.LocationNetworkAuthorization;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductLocation;
import com.chiralbehaviors.CoRE.product.ProductLocationAttribute;
import com.chiralbehaviors.CoRE.product.ProductNetwork;
import com.chiralbehaviors.CoRE.product.ProductNetworkAttribute;
import com.chiralbehaviors.CoRE.time.Interval;
import com.chiralbehaviors.CoRE.time.IntervalAttribute;
import com.chiralbehaviors.CoRE.time.IntervalAttributeAuthorization;
import com.chiralbehaviors.CoRE.time.IntervalNetwork;
import com.chiralbehaviors.CoRE.time.IntervalNetworkAttribute;
import com.fasterxml.jackson.annotation.JsonIgnore;

@NamedQueries({
               @NamedQuery(name = GET_WORKSPACE, query = "SELECT auth FROM WorkspaceAuthorization auth WHERE auth.definingProduct = :product"),
               @NamedQuery(name = GET_AUTHORIZATION, query = "SELECT auth FROM WorkspaceAuthorization auth "
                                                             + "WHERE auth.definingProduct = :product "
                                                             + "AND auth.key = :key"),
               @NamedQuery(name = GET_AUTHORIZATIONS_BY_TYPE, query = "SELECT auth FROM WorkspaceAuthorization auth "
                                                                      + "WHERE auth.definingProduct = :product "
                                                                      + "AND auth.type= :type"), })
@Entity
@Table(name = "workspace_authorization", schema = "ruleform")
public class WorkspaceAuthorization extends Ruleform {
    public static final String                    GET_WORKSPACE                            = "workspaceAuthorization.getWorkspace";
    public static final String                    GET_AUTHORIZATION                        = "workspaceAuthorization.getAuthorization";
    public static final String                    GET_AUTHORIZATIONS_BY_TYPE               = "workspaceAuthorization.getAuthorizationByType";
    public static final String                    AGENCY                                   = "Agency";
    public static final String                    AGENCY_ATTRIBUTE                         = "AgencyAttribute";
    public static final String                    AGENCY_ATTRIBUTE_AUTHORIZATION           = "AgencyAttributeAuthorization";
    public static final String                    AGENCY_LOCATION                          = "AgencyLocation";
    public static final String                    AGENCY_LOCATION_ATTRIBUTE                = "AgencyLocationAttribute";
    public static final String                    AGENCY_NETWORK                           = "AgencyNetwork";
    public static final String                    AGENCY_NETWORK_ATTRIBUTE                 = "AgencyNetworkAttribute";
    public static final String                    AGENCY_NETWORK_AUTHORIZATION             = "AgencyNetworkAuthorization";
    public static final String                    AGENCY_PRODUCT                           = "AgencyProduct";
    public static final String                    ATTRIBUTE                                = "Attribute";
    public static final String                    ATTRIBUTE_META_ATTRIBUTE                 = "AttributeMetaAttribute";
    public static final String                    ATTRIBUTE_META_ATTRIBUTE_AUTHORIZATION   = "AttributeMetaAttributeAuthorization";
    public static final String                    ATTRIBUTE_NETWORK                        = "AttributeNetwork";
    public static final String                    ATTRIBUTE_NETWORK_ATTRIBUTE              = "AttributeNetworkAttribute";
    public static final String                    INTERVAL                                 = "Interval";
    public static final String                    INTERVAL_ATTRIBUTE                       = "IntervalAttribute";
    public static final String                    INTERVAL_NETWORK                         = "IntervalNetwork";
    public static final String                    INTERVAL_NETWORK_ATTRIBUTE               = "IntervalNetworkAttribute";
    public static final String                    JOB                                      = "Job";
    public static final String                    JOB_CHRONOLOGY                           = "JobChronology";
    public static final String                    LOCATION                                 = "Location";
    public static final String                    LOCATION_ATTRIBUTE                       = "LocationAttribute";
    public static final String                    LOCATION_ATTRIBUTE_AUTHORIZATION         = "LocationAttributeAuthorization";
    public static final String                    LOCATION_NETWORK                         = "LocationNetwork";
    public static final String                    LOCATION_NETWORK_ATTRIBUTE               = "LocationNetworkAttribute";
    public static final String                    LOCATION_NETWORK_AUTHORIZATION           = "LocationNetworkAuthorization";
    public static final String                    META_PROTOCOL                            = "MetaProtocol";
    public static final String                    PRODUCT_ATTRIBUTE                        = "ProductAttribute";
    public static final String                    PRODUCT_ATTRIBUTE_AUTHORIZATION          = "ProductAttributeAuthorization";
    public static final String                    PRODUCT_CHILD_SEQUENCING_AUTHORIZATION   = "ProductChildSequencingAuthorization";
    public static final String                    PRODUCT_LOCATION                         = "ProductLocation";
    public static final String                    PRODUCT_LOCATION_ATTRIBUTE               = "ProductLocationAttribute";
    public static final String                    PRODUCT_NETWORK                          = "ProductNetwork";
    public static final String                    PRODUCT_NETWORK_ATTRIBUTE                = "ProductNetworkAttribute";
    public static final String                    PRODUCT_PARENT_SEQUENCING_AUTHORIZATION  = "ProductParentSequencingAuthorization";
    public static final String                    PRODUCT_SIBLING_SEQUENCING_AUTHORIZATION = "ProductSiblingSequencingAuthorization";
    public static final String                    PRODUCT2                                 = "Product";
    public static final String                    PROTOCOL                                 = "Protocol";
    public static final String                    STATUS_CODE                              = "StatusCode";
    public static final String                    STATUS_CODE_ATTRIBUTE                    = "StatusCodeAttribute";
    public static final String                    STATUS_CODE_ATTRIBUTE_AUTHORIZATION      = "StatusCodeAttributeAuthorization";
    public static final String                    STATUS_CODE_NETWORK                      = "StatusCodeNetwork";
    public static final String                    STATUS_CODE_NETWORK_ATTRIBUTE            = "StatusCodeNetworkAttribute";
    public static final String                    STATUS_CODE_SEQUENCING                   = "StatusCodeSequencing";
    public static final String                    UNIT                                     = "Unit";
    public static final String                    UNIT_ATTRIBUTE                           = "UnitAttribute";
    public static final String                    UNIT_ATTRIBUTE_AUTHORIZATION             = "UnitAttributeAuthorization";
    public static final String                    UNIT_NETWORK                             = "UnitNetwork";
    public static final String                    UNIT_NETWORK_ATTRIBUTE                   = "UnitNetworkAttribute";
    public static final String                    UNIT_VALUE                               = "UnitValue";

    private static final long                     serialVersionUID                         = 1L;

    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency                                agency;

    @ManyToOne
    @JoinColumn(name = "agency_attribute")
    private AgencyAttribute                       agencyAttribute;

    @ManyToOne
    @JoinColumn(name = "agency_attribute_authorization")
    private AgencyAttributeAuthorization          agencyAttributeAuthorization;

    @ManyToOne
    @JoinColumn(name = "agency_location")
    private AgencyLocation                        agencyLocation;

    @ManyToOne
    @JoinColumn(name = "agency_location_attribute")
    private AgencyLocationAttribute               agencyLocationAttribute;

    @ManyToOne
    @JoinColumn(name = "agency_network")
    private AgencyNetwork                         agencyNetwork;

    @ManyToOne
    @JoinColumn(name = "agency_network_attribute")
    private AgencyNetworkAttribute                agencyNetworkAttribute;

    @ManyToOne
    @JoinColumn(name = "agency_network_authorization")
    private AgencyNetworkAuthorization            agencyNetworkAuthorization;

    @ManyToOne
    @JoinColumn(name = "agency_product")
    private AgencyProduct                         agencyProduct;

    @ManyToOne
    @JoinColumn(name = "attribute")
    private Attribute                             attribute;

    @ManyToOne
    @JoinColumn(name = "attribute_meta_attribute")
    private AttributeMetaAttribute                attributeMetaAttribute;

    @ManyToOne
    @JoinColumn(name = "attr_meta_attr_auth")
    private AttributeMetaAttributeAuthorization   attributeMetaAttributeAuthorization;

    @ManyToOne
    @JoinColumn(name = "attribute_network")
    private AttributeNetwork                      attributeNetwork;

    @ManyToOne
    @JoinColumn(name = "attribute_network_attribute")
    private AttributeNetworkAttribute             attributeNetworkAttribute;

    @ManyToOne
    @JoinColumn(name = "defining_product")
    private Product                               definingProduct;

    @ManyToOne
    @JoinColumn(name = "interval")
    private Interval                              interval;

    @ManyToOne
    @JoinColumn(name = "interval_attribute")
    private IntervalAttribute                     intervalAttribute;

    @ManyToOne
    @JoinColumn(name = "interval_attribute_authorization")
    private IntervalAttributeAuthorization        intervalAttributeAuthorization;

    @ManyToOne
    @JoinColumn(name = "interval_network")
    private IntervalNetwork                       intervalNetwork;

    @ManyToOne
    @JoinColumn(name = "interval_network_attribute")
    private IntervalNetworkAttribute              intervalNetworkAttribute;

    @ManyToOne
    @JoinColumn(name = "job")
    private Job                                   job;

    @ManyToOne
    @JoinColumn(name = "job_chronology")
    private JobChronology                         jobChronology;

    private String                                key;

    @ManyToOne
    @JoinColumn(name = "location")
    private Location                              location;

    @ManyToOne
    @JoinColumn(name = "location_attribute")
    private LocationAttribute                     locationAttribute;

    @ManyToOne
    @JoinColumn(name = "location_attribute_authorization")
    private LocationAttributeAuthorization        locationAttributeAuthorization;

    @ManyToOne
    @JoinColumn(name = "location_network")
    private LocationNetwork                       locationNetwork;

    @ManyToOne
    @JoinColumn(name = "location_network_attribute")
    private LocationNetworkAttribute              locationNetworkAttribute;

    @ManyToOne
    @JoinColumn(name = "location_network_authorization")
    private LocationNetworkAuthorization          locationNetworkAuthorization;

    @ManyToOne
    @JoinColumn(name = "meta_protocol")
    private MetaProtocol                          metaProtocol;

    @ManyToOne
    @JoinColumn(name = "product")
    private Product                               product;

    @ManyToOne
    @JoinColumn(name = "product_attribute")
    private ProductAttribute                      productAttribute;

    @ManyToOne
    @JoinColumn(name = "product_attribute_authorization")
    private ProductAttributeAuthorization         productAttributeAuthorization;

    @ManyToOne
    @JoinColumn(name = "product_child_sequencing_authorization")
    private ProductChildSequencingAuthorization   productChildSequencingAuthorization;

    @ManyToOne
    @JoinColumn(name = "product_location")
    private ProductLocation                       productLocation;

    @ManyToOne
    @JoinColumn(name = "product_location_attribute")
    private ProductLocationAttribute              productLocationAttribute;

    @ManyToOne
    @JoinColumn(name = "product_network")
    private ProductNetwork                        productNetwork;

    @ManyToOne
    @JoinColumn(name = "product_network_attribute")
    private ProductNetworkAttribute               productNetworkAttribute;

    @ManyToOne
    @JoinColumn(name = "product_parent_sequencing_authorization")
    private ProductParentSequencingAuthorization  productParentSequencingAuthorization;

    @ManyToOne
    @JoinColumn(name = "product_sibling_sequencing_authorization")
    private ProductSiblingSequencingAuthorization productSiblingSequencingAuthorization;

    @ManyToOne
    @JoinColumn(name = "protocol")
    private Protocol                              protocol;

    @ManyToOne
    @JoinColumn(name = "status_code")
    private StatusCode                            statusCode;

    @ManyToOne
    @JoinColumn(name = "status_code_attribute")
    private StatusCodeAttribute                   statusCodeAttribute;

    @ManyToOne
    @JoinColumn(name = "status_code_attribute_authorization")
    private StatusCodeAttributeAuthorization      statusCodeAttributeAuthorization;

    @ManyToOne
    @JoinColumn(name = "status_code_network")
    private StatusCodeNetwork                     statusCodeNetwork;

    @ManyToOne
    @JoinColumn(name = "status_code_network_attribute")
    private StatusCodeNetworkAttribute            statusCodeNetworkAttribute;

    @ManyToOne
    @JoinColumn(name = "status_code_sequencing")
    private StatusCodeSequencing                  statusCodeSequencing;

    @Column(name = "type")
    private String                                type;

    @ManyToOne
    @JoinColumn(name = "unit")
    private Unit                                  unit;

    @ManyToOne
    @JoinColumn(name = "unit_attribute")
    private UnitAttribute                         unitAttribute;

    @ManyToOne
    @JoinColumn(name = "unit_attribute_authorization")
    private UnitAttributeAuthorization            unitAttributeAuthorization;

    @ManyToOne
    @JoinColumn(name = "unit_network")
    private UnitNetwork                           unitNetwork;

    @ManyToOne
    @JoinColumn(name = "unit_network_attribute")
    private UnitNetworkAttribute                  unitNetworkAttribute;

    @ManyToOne
    @JoinColumn(name = "unit_value")
    private UnitValue                             unitValue;

    public Agency getAgency() {
        return agency;
    }

    public AgencyAttribute getAgencyAttribute() {
        return agencyAttribute;
    }

    public AgencyAttributeAuthorization getAgencyAttributeAuthorization() {
        return agencyAttributeAuthorization;
    }

    public AgencyLocation getAgencyLocation() {
        return agencyLocation;
    }

    public AgencyLocationAttribute getAgencyLocationAttribute() {
        return agencyLocationAttribute;
    }

    public AgencyNetwork getAgencyNetwork() {
        return agencyNetwork;
    }

    public AgencyNetworkAttribute getAgencyNetworkAttribute() {
        return agencyNetworkAttribute;
    }

    public AgencyNetworkAuthorization getAgencyNetworkAuthorization() {
        return agencyNetworkAuthorization;
    }

    public AgencyProduct getAgencyProduct() {
        return agencyProduct;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public AttributeMetaAttribute getAttributeMetaAttribute() {
        return attributeMetaAttribute;
    }

    public AttributeMetaAttributeAuthorization getAttributeMetaAttributeAuthorization() {
        return attributeMetaAttributeAuthorization;
    }

    public AttributeNetwork getAttributeNetwork() {
        return attributeNetwork;
    }

    public AttributeNetworkAttribute getAttributeNetworkAttribute() {
        return attributeNetworkAttribute;
    }

    public Product getDefiningProduct() {
        return definingProduct;
    }

    @SuppressWarnings("unchecked")
    @JsonIgnore
    public <T extends Ruleform> T getEntity() {
        switch (type) {
            case AGENCY:
                return (T) getAgency();
            case AGENCY_ATTRIBUTE:
                return (T) getAgencyAttribute();
            case AGENCY_ATTRIBUTE_AUTHORIZATION:
                return (T) getAgencyAttributeAuthorization();
            case AGENCY_LOCATION:
                return (T) getAgencyLocation();
            case AGENCY_LOCATION_ATTRIBUTE:
                return (T) getAgencyLocationAttribute();
            case AGENCY_NETWORK:
                return (T) getAgencyNetwork();
            case AGENCY_NETWORK_ATTRIBUTE:
                return (T) getAgencyNetworkAttribute();
            case AGENCY_NETWORK_AUTHORIZATION:
                return (T) getAgencyNetworkAuthorization();
            case AGENCY_PRODUCT:
                return (T) getAgencyProduct();
            case ATTRIBUTE:
                return (T) getAttribute();
            case ATTRIBUTE_META_ATTRIBUTE:
                return (T) getAttributeMetaAttribute();
            case ATTRIBUTE_META_ATTRIBUTE_AUTHORIZATION:
                return (T) getAttributeMetaAttributeAuthorization();
            case ATTRIBUTE_NETWORK:
                return (T) getAttributeNetwork();
            case ATTRIBUTE_NETWORK_ATTRIBUTE:
                return (T) getAttributeNetworkAttribute();
            case UNIT:
                return (T) getUnit();
            case UNIT_ATTRIBUTE:
                return (T) getUnitAttribute();
            case UNIT_ATTRIBUTE_AUTHORIZATION:
                return (T) getUnitAttributeAuthorization();
            case UNIT_NETWORK:
                return (T) getUnitNetwork();
            case UNIT_NETWORK_ATTRIBUTE:
                return (T) getUnitNetworkAttribute();
            case UNIT_VALUE:
                return (T) getUnitValue();
            case LOCATION:
                return (T) getLocation();
            case LOCATION_ATTRIBUTE:
                return (T) getLocationAttribute();
            case LOCATION_ATTRIBUTE_AUTHORIZATION:
                return (T) getLocationAttributeAuthorization();
            case LOCATION_NETWORK:
                return (T) getLocationNetwork();
            case LOCATION_NETWORK_ATTRIBUTE:
                return (T) getLocationNetworkAttribute();
            case LOCATION_NETWORK_AUTHORIZATION:
                return (T) getLocationNetworkAuthorization();
            case PRODUCT2:
                return (T) getProduct();
            case PRODUCT_ATTRIBUTE:
                return (T) getProductAttribute();
            case PRODUCT_ATTRIBUTE_AUTHORIZATION:
                return (T) getProductAttributeAuthorization();
            case PRODUCT_NETWORK:
                return (T) getProductNetwork();
            case PRODUCT_NETWORK_ATTRIBUTE:
                return (T) getProductNetworkAttribute();
            case PRODUCT_LOCATION:
                return (T) getProductLocation();
            case PRODUCT_LOCATION_ATTRIBUTE:
                return (T) getProductLocationAttribute();
            case INTERVAL:
                return (T) getInterval();
            case INTERVAL_ATTRIBUTE:
                return (T) getIntervalAttribute();
            case "IntervalAttributeAuthorization":
                return (T) getIntervalAttributeAuthorization();
            case INTERVAL_NETWORK:
                return (T) getIntervalNetwork();
            case INTERVAL_NETWORK_ATTRIBUTE:
                return (T) getIntervalNetworkAttribute();
            case STATUS_CODE:
                return (T) getStatusCode();
            case STATUS_CODE_ATTRIBUTE:
                return (T) getStatusCodeAttribute();
            case STATUS_CODE_ATTRIBUTE_AUTHORIZATION:
                return (T) getStatusCodeAttributeAuthorization();
            case STATUS_CODE_NETWORK:
                return (T) getStatusCodeNetwork();
            case STATUS_CODE_NETWORK_ATTRIBUTE:
                return (T) getStatusCodeNetworkAttribute();
            case STATUS_CODE_SEQUENCING:
                return (T) getStatusCodeSequencing();
            case JOB:
                return (T) getJob();
            case JOB_CHRONOLOGY:
                return (T) getJobChronology();
            case META_PROTOCOL:
                return (T) getMetaProtocol();
            case PRODUCT_CHILD_SEQUENCING_AUTHORIZATION:
                return (T) getProductChildSequencingAuthorization();
            case PRODUCT_PARENT_SEQUENCING_AUTHORIZATION:
                return (T) getProductParentSequencingAuthorization();
            case PRODUCT_SIBLING_SEQUENCING_AUTHORIZATION:
                return (T) getProductSiblingSequencingAuthorization();
            case PROTOCOL:
                return (T) getProtocol();

            default:
                throw new IllegalStateException(
                                                String.format("Invalid type: %s",
                                                              type));
        }
    }

    public Interval getInterval() {
        return interval;
    }

    public IntervalAttribute getIntervalAttribute() {
        return intervalAttribute;
    }

    public IntervalAttributeAuthorization getIntervalAttributeAuthorization() {
        return intervalAttributeAuthorization;
    }

    public IntervalNetwork getIntervalNetwork() {
        return intervalNetwork;
    }

    public IntervalNetworkAttribute getIntervalNetworkAttribute() {
        return intervalNetworkAttribute;
    }

    public Job getJob() {
        return job;
    }

    public JobChronology getJobChronology() {
        return jobChronology;
    }

    public String getKey() {
        return key;
    }

    public Location getLocation() {
        return location;
    }

    public LocationAttribute getLocationAttribute() {
        return locationAttribute;
    }

    public LocationAttributeAuthorization getLocationAttributeAuthorization() {
        return locationAttributeAuthorization;
    }

    public LocationNetwork getLocationNetwork() {
        return locationNetwork;
    }

    public LocationNetworkAttribute getLocationNetworkAttribute() {
        return locationNetworkAttribute;
    }

    public LocationNetworkAuthorization getLocationNetworkAuthorization() {
        return locationNetworkAuthorization;
    }

    public MetaProtocol getMetaProtocol() {
        return metaProtocol;
    }

    public Product getProduct() {
        return product;
    }

    public ProductAttribute getProductAttribute() {
        return productAttribute;
    }

    public ProductAttributeAuthorization getProductAttributeAuthorization() {
        return productAttributeAuthorization;
    }

    public ProductChildSequencingAuthorization getProductChildSequencingAuthorization() {
        return productChildSequencingAuthorization;
    }

    public ProductLocation getProductLocation() {
        return productLocation;
    }

    public ProductLocationAttribute getProductLocationAttribute() {
        return productLocationAttribute;
    }

    public ProductNetwork getProductNetwork() {
        return productNetwork;
    }

    public ProductNetworkAttribute getProductNetworkAttribute() {
        return productNetworkAttribute;
    }

    public ProductParentSequencingAuthorization getProductParentSequencingAuthorization() {
        return productParentSequencingAuthorization;
    }

    public ProductSiblingSequencingAuthorization getProductSiblingSequencingAuthorization() {
        return productSiblingSequencingAuthorization;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public StatusCodeAttribute getStatusCodeAttribute() {
        return statusCodeAttribute;
    }

    public StatusCodeAttributeAuthorization getStatusCodeAttributeAuthorization() {
        return statusCodeAttributeAuthorization;
    }

    public StatusCodeNetwork getStatusCodeNetwork() {
        return statusCodeNetwork;
    }

    public StatusCodeNetworkAttribute getStatusCodeNetworkAttribute() {
        return statusCodeNetworkAttribute;
    }

    public StatusCodeSequencing getStatusCodeSequencing() {
        return statusCodeSequencing;
    }

    public String getType() {
        return type;
    }

    public Unit getUnit() {
        return unit;
    }

    public UnitAttribute getUnitAttribute() {
        return unitAttribute;
    }

    public UnitAttributeAuthorization getUnitAttributeAuthorization() {
        return unitAttributeAuthorization;
    }

    public UnitNetwork getUnitNetwork() {
        return unitNetwork;
    }

    public UnitNetworkAttribute getUnitNetworkAttribute() {
        return unitNetworkAttribute;
    }

    public UnitValue getUnitValue() {
        return unitValue;
    }

    public void setAgency(Agency agency) {
        type = AGENCY;
        this.agency = agency;
    }

    public void setAgencyAttribute(AgencyAttribute agencyAttribute) {
        type = AGENCY_ATTRIBUTE;
        this.agencyAttribute = agencyAttribute;
    }

    public void setAgencyAttributeAuthorization(AgencyAttributeAuthorization agencyAttributeAuthorization) {
        type = AGENCY_ATTRIBUTE_AUTHORIZATION;
        this.agencyAttributeAuthorization = agencyAttributeAuthorization;
    }

    public void setAgencyLocation(AgencyLocation agencyLocation) {
        type = AGENCY_LOCATION;
        this.agencyLocation = agencyLocation;
    }

    public void setAgencyLocationAttribute(AgencyLocationAttribute agencyLocationAttribute) {
        type = AGENCY_LOCATION_ATTRIBUTE;
        this.agencyLocationAttribute = agencyLocationAttribute;
    }

    public void setAgencyNetwork(AgencyNetwork agencyNetwork) {
        type = AGENCY_NETWORK;
        this.agencyNetwork = agencyNetwork;
    }

    public void setAgencyNetworkAttribute(AgencyNetworkAttribute agencyNetworkAttribute) {
        type = AGENCY_NETWORK_ATTRIBUTE;
        this.agencyNetworkAttribute = agencyNetworkAttribute;
    }

    public void setAgencyNetworkAuthorization(AgencyNetworkAuthorization agencyNetworkAuthorization) {
        type = AGENCY_NETWORK_AUTHORIZATION;
        this.agencyNetworkAuthorization = agencyNetworkAuthorization;
    }

    public void setAgencyProduct(AgencyProduct agencyProduct) {
        type = AGENCY_PRODUCT;
        this.agencyProduct = agencyProduct;
    }

    public void setAttribute(Attribute attribute) {
        type = ATTRIBUTE;
        this.attribute = attribute;
    }

    public void setAttributeMetaAttribute(AttributeMetaAttribute attributeMetaAttribute) {
        type = ATTRIBUTE_META_ATTRIBUTE;
        this.attributeMetaAttribute = attributeMetaAttribute;
    }

    public void setAttributeMetaAttributeAuthorization(AttributeMetaAttributeAuthorization attributeMetaAttributeAuthorization) {
        type = ATTRIBUTE_META_ATTRIBUTE_AUTHORIZATION;
        this.attributeMetaAttributeAuthorization = attributeMetaAttributeAuthorization;
    }

    public void setAttributeNetwork(AttributeNetwork attributeNetwork) {
        type = ATTRIBUTE_NETWORK;
        this.attributeNetwork = attributeNetwork;
    }

    public void setAttributeNetworkAttribute(AttributeNetworkAttribute attributeNetworkAttribute) {
        type = ATTRIBUTE_NETWORK_ATTRIBUTE;
        this.attributeNetworkAttribute = attributeNetworkAttribute;
    }

    public void setDefiningProduct(Product definingProduct) {
        this.definingProduct = definingProduct;
    }

    public void setInterval(Interval interval) {
        type = INTERVAL;
        this.interval = interval;
    }

    public void setIntervalAttribute(IntervalAttribute intervalAttribute) {
        type = INTERVAL_ATTRIBUTE;
        this.intervalAttribute = intervalAttribute;
    }

    public void setIntervalAttributeAuthorization(IntervalAttributeAuthorization intervalAttributeAuthorization) {
        type = "IntervalAttributeAuthorization";
        this.intervalAttributeAuthorization = intervalAttributeAuthorization;
    }

    public void setIntervalNetwork(IntervalNetwork intervalNetwork) {
        type = INTERVAL_NETWORK;
        this.intervalNetwork = intervalNetwork;
    }

    public void setIntervalNetworkAttribute(IntervalNetworkAttribute intervalNetworkAttribute) {
        type = INTERVAL_NETWORK_ATTRIBUTE;
        this.intervalNetworkAttribute = intervalNetworkAttribute;
    }

    public void setJob(Job job) {
        type = JOB;
        this.job = job;
    }

    public void setJobChronology(JobChronology jobChronology) {
        type = JOB_CHRONOLOGY;
        this.jobChronology = jobChronology;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLocation(Location location) {
        type = LOCATION;
        this.location = location;
    }

    public void setLocationAttribute(LocationAttribute locationAttribute) {
        type = LOCATION_ATTRIBUTE;
        this.locationAttribute = locationAttribute;
    }

    public void setLocationAttributeAuthorization(LocationAttributeAuthorization locationAttributeAuthorization) {
        type = LOCATION_ATTRIBUTE_AUTHORIZATION;
        this.locationAttributeAuthorization = locationAttributeAuthorization;
    }

    public void setLocationNetwork(LocationNetwork locationNetwork) {
        type = LOCATION_NETWORK;
        this.locationNetwork = locationNetwork;
    }

    public void setLocationNetworkAttribute(LocationNetworkAttribute locationNetworkAttribute) {
        type = LOCATION_NETWORK_ATTRIBUTE;
        this.locationNetworkAttribute = locationNetworkAttribute;
    }

    public void setLocationNetworkAuthorization(LocationNetworkAuthorization locationNetworkAuthorization) {
        type = LOCATION_NETWORK_AUTHORIZATION;
        this.locationNetworkAuthorization = locationNetworkAuthorization;
    }

    public void setMetaProtocol(MetaProtocol metaProtocol) {
        type = META_PROTOCOL;
        this.metaProtocol = metaProtocol;
    }

    public void setProduct(Product product) {
        type = PRODUCT2;
        this.product = product;
    }

    public void setProductAttribute(ProductAttribute productAttribute) {
        type = PRODUCT_ATTRIBUTE;
        this.productAttribute = productAttribute;
    }

    public void setProductAttributeAuthorization(ProductAttributeAuthorization productAttributeAuthorization) {
        type = PRODUCT_ATTRIBUTE_AUTHORIZATION;
        this.productAttributeAuthorization = productAttributeAuthorization;
    }

    public void setProductChildSequencingAuthorization(ProductChildSequencingAuthorization productChildSequencingAuthorization) {
        type = PRODUCT_CHILD_SEQUENCING_AUTHORIZATION;
        this.productChildSequencingAuthorization = productChildSequencingAuthorization;
    }

    public void setProductLocation(ProductLocation productLocation) {
        type = PRODUCT_LOCATION;
        this.productLocation = productLocation;
    }

    public void setProductLocationAttribute(ProductLocationAttribute productLocationAttribute) {
        type = PRODUCT_LOCATION_ATTRIBUTE;
        this.productLocationAttribute = productLocationAttribute;
    }

    public void setProductNetwork(ProductNetwork productNetwork) {
        type = PRODUCT_NETWORK;
        this.productNetwork = productNetwork;
    }

    public void setProductNetworkAttribute(ProductNetworkAttribute productNetworkAttribute) {
        type = PRODUCT_NETWORK_ATTRIBUTE;
        this.productNetworkAttribute = productNetworkAttribute;
    }

    public void setProductParentSequencingAuthorization(ProductParentSequencingAuthorization productParentSequencingAuthorization) {
        type = PRODUCT_PARENT_SEQUENCING_AUTHORIZATION;
        this.productParentSequencingAuthorization = productParentSequencingAuthorization;
    }

    public void setProductSiblingSequencingAuthorization(ProductSiblingSequencingAuthorization productSiblingSequencingAuthorization) {
        type = PRODUCT_SIBLING_SEQUENCING_AUTHORIZATION;
        this.productSiblingSequencingAuthorization = productSiblingSequencingAuthorization;
    }

    public void setProtocol(Protocol protocol) {
        type = PROTOCOL;
        this.protocol = protocol;
    }

    public void setStatusCode(StatusCode statusCode) {
        type = STATUS_CODE;
        this.statusCode = statusCode;
    }

    public void setStatusCodeAttribute(StatusCodeAttribute statusCodeAttribute) {
        type = STATUS_CODE_ATTRIBUTE;
        this.statusCodeAttribute = statusCodeAttribute;
    }

    public void setStatusCodeAttributeAuthorization(StatusCodeAttributeAuthorization statusCodeAttributeAuthorization) {
        type = STATUS_CODE_ATTRIBUTE_AUTHORIZATION;
        this.statusCodeAttributeAuthorization = statusCodeAttributeAuthorization;
    }

    public void setStatusCodeNetwork(StatusCodeNetwork statusCodeNetwork) {
        type = STATUS_CODE_NETWORK;
        this.statusCodeNetwork = statusCodeNetwork;
    }

    public void setStatusCodeNetworkAttribute(StatusCodeNetworkAttribute statusCodeNetworkAttribute) {
        type = STATUS_CODE_NETWORK_ATTRIBUTE;
        this.statusCodeNetworkAttribute = statusCodeNetworkAttribute;
    }

    public void setStatusCodeSequencing(StatusCodeSequencing statusCodeSequencing) {
        type = STATUS_CODE_SEQUENCING;
        this.statusCodeSequencing = statusCodeSequencing;
    }

    public void setUnit(Unit unit) {
        type = UNIT;
        this.unit = unit;
    }

    public void setUnitAttribute(UnitAttribute unitAttribute) {
        type = UNIT_ATTRIBUTE;
        this.unitAttribute = unitAttribute;
    }

    public void setUnitAttributeAuthorization(UnitAttributeAuthorization unitAttributeAuthorization) {
        type = UNIT_ATTRIBUTE_AUTHORIZATION;
        this.unitAttributeAuthorization = unitAttributeAuthorization;
    }

    public void setUnitNetwork(UnitNetwork unitNetwork) {
        type = UNIT_NETWORK;
        this.unitNetwork = unitNetwork;
    }

    public void setUnitNetworkAttribute(UnitNetworkAttribute unitNetworkAttribute) {
        type = UNIT_NETWORK_ATTRIBUTE;
        this.unitNetworkAttribute = unitNetworkAttribute;
    }

    public void setUnitValue(UnitValue unitValue) {
        type = UNIT_VALUE;
        this.unitValue = unitValue;
    }

}