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
package com.chiralbehaviors.CoRE.product;

import java.math.BigDecimal;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.metamodel.SingularAttribute;

import com.chiralbehaviors.CoRE.Ruleform;
import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeValue;
import com.chiralbehaviors.CoRE.attribute.unit.Unit;

/**
 * The attribute value for product attributes
 * 
 * @author hhildebrand
 * 
 */
@Entity
@Table(name = "product_network_attribute", schema = "ruleform")
@SequenceGenerator(schema = "ruleform", name = "product_network_attribute_id_seq", sequenceName = "product_network_attribute_id_seq")
public class ProductNetworkAttribute extends AttributeValue<ProductNetwork> {
    private static final long serialVersionUID = 1L;

    // bi-directional many-to-one association to Agency
    @ManyToOne
    @JoinColumn(name = "agency")
    private Agency            agency;

    @Id
    @GeneratedValue(generator = "product_network_attribute_id_seq", strategy = GenerationType.SEQUENCE)
    private Long              id;

    // bi-directional many-to-one association to ProductNetwork
    @ManyToOne
    @JoinColumn(name = "network_rule")
    private ProductNetwork    productNetwork;

    // bi-directional many-to-one association to Product
    @ManyToOne
    @JoinColumn(name = "product_value")
    private Product           productValue;

    public ProductNetworkAttribute() {
    }

    /**
     * @param updatedBy
     */
    public ProductNetworkAttribute(Agency updatedBy) {
        super(updatedBy);
    }

    /**
     * @param attribute
     */
    public ProductNetworkAttribute(Attribute attribute) {
        super(attribute);
    }

    /**
     * @param attribute
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, Agency updatedBy) {
        super(attribute, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, BigDecimal value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, boolean value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, int value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param value
     * @param updatedBy
     */
    public ProductNetworkAttribute(Attribute attribute, String value,
                                   Agency updatedBy) {
        super(attribute, value, updatedBy);
    }

    /**
     * @param attribute
     * @param unit
     */
    public ProductNetworkAttribute(Attribute attribute, Unit unit) {
        super(attribute, unit);
    }

    /**
     * @param id
     */
    public ProductNetworkAttribute(Long id) {
        super(id);
    }

    public Agency getAgency() {
        return agency;
    }

    @Override
    public Long getId() {
        return id;
    }

    public ProductNetwork getProductNetwork() {
        return productNetwork;
    }

    public Product getProductValue() {
        return productValue;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformAttribute()
     */
    @Override
    public SingularAttribute<ProductNetworkAttribute, ProductNetwork> getRuleformAttribute() {
        return ProductNetworkAttribute_.productNetwork;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.chiralbehaviors.CoRE.attribute.AttributeValue#getRuleformClass()
     */
    @Override
    public Class<ProductNetwork> getRuleformClass() {
        return ProductNetwork.class;
    }

    public void setAgency(Agency agency2) {
        agency = agency2;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public void setProductNetwork(ProductNetwork productNetwork) {
        this.productNetwork = productNetwork;
    }

    public void setProductValue(Product product) {
        productValue = product;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.chiralbehaviors.CoRE.Ruleform#traverseForeignKeys(javax.persistence
     * .EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (productNetwork != null) {
            productNetwork = (ProductNetwork) productNetwork.manageEntity(em,
                                                                          knownObjects);
        }
        if (productValue != null) {
            productValue = (Product) productValue.manageEntity(em, knownObjects);
        }
        if (agency != null) {
            agency = (Agency) agency.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);

    }
}