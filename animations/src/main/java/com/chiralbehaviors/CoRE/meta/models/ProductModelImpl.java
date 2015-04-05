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

package com.chiralbehaviors.CoRE.meta.models;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import com.chiralbehaviors.CoRE.attribute.Attribute;
import com.chiralbehaviors.CoRE.attribute.AttributeMetaAttribute;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.ProductModel;
import com.chiralbehaviors.CoRE.network.Aspect;
import com.chiralbehaviors.CoRE.network.Facet;
import com.chiralbehaviors.CoRE.network.Relationship;
import com.chiralbehaviors.CoRE.product.Product;
import com.chiralbehaviors.CoRE.product.ProductAttribute;
import com.chiralbehaviors.CoRE.product.ProductAttributeAuthorization;
import com.chiralbehaviors.CoRE.product.ProductNetwork;

/**
 * @author hhildebrand
 *
 */
public class ProductModelImpl
extends
AbstractNetworkedModel<Product, ProductNetwork, ProductAttributeAuthorization, ProductAttribute>
implements ProductModel {

    /**
     * @param em
     */
    public ProductModelImpl(Model model) {
        super(model);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#authorize(com.chiralbehaviors
     * .CoRE .meta.Aspect, com.chiralbehaviors.CoRE.attribute.Attribute[])
     */
    @Override
    public void authorize(Aspect<Product> aspect, Attribute... attributes) {
        for (Attribute attribute : attributes) {
            ProductAttributeAuthorization authorization = new ProductAttributeAuthorization(
                                                                                            aspect.getClassification(),
                                                                                            aspect.getClassifier(),
                                                                                            attribute,
                                                                                            kernel.getCoreModel());
            em.persist(authorization);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#authorizeEnum(com.
     * chiralbehaviors.CoRE.network.Aspect,
     * com.chiralbehaviors.CoRE.attribute.Attribute,
     * com.chiralbehaviors.CoRE.attribute.Attribute)
     */
    @Override
    public void authorizeEnum(Aspect<Product> aspect, Attribute attribute,
                              Attribute enumAttribute) {
        ProductAttributeAuthorization auth = new ProductAttributeAuthorization(
                                                                               aspect.getClassification(),
                                                                               aspect.getClassifier(),
                                                                               attribute,
                                                                               kernel.getCoreAnimationSoftware());
        auth.setValidatingAttribute(enumAttribute);
        em.persist(auth);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors
     * .CoRE.network .Networked)
     */
    @Override
    public Product create(Product prototype) {
        Product copy = prototype.clone();
        em.detach(copy);
        em.persist(copy);
        copy.setUpdatedBy(kernel.getCoreModel());
        for (ProductNetwork network : prototype.getNetworkByParent()) {
            network.getParent().link(network.getRelationship(), copy,
                                     kernel.getCoreModel(),
                                     kernel.getInverseSoftware(), em);
        }
        for (ProductAttribute attribute : prototype.getAttributes()) {
            ProductAttribute clone = (ProductAttribute) attribute.clone();
            em.detach(clone);
            em.persist(clone);
            clone.setProduct(copy);
            clone.setUpdatedBy(kernel.getCoreModel());
        }
        return copy;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(java.lang.String,
     * java.lang.String, com.chiralbehaviors.CoRE.network.Aspect)
     */
    @Override
    public Facet<Product, ProductAttribute> create(String name,
                                                   String description,
                                                   Aspect<Product> aspect) {
        Product product = new Product(name, description, kernel.getCoreModel());
        em.persist(product);
        return new Facet<Product, ProductAttribute>(aspect, product,
                initialize(product, aspect)) {
        };
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.chiralbehaviors.CoRE.meta.NetworkedModel#create(com.chiralbehaviors
     * .CoRE.meta .Aspect<RuleForm>[])
     */
    @SafeVarargs
    @Override
    final public Product create(String name, String description,
                                Aspect<Product> aspect,
                                Aspect<Product>... aspects) {
        Product product = new Product(name, description, kernel.getCoreModel());
        em.persist(product);
        initialize(product, aspect);
        if (aspects != null) {
            for (Aspect<Product> a : aspects) {
                initialize(product, a);
            }
        }
        return product;
    }

    @Override
    public List<ProductNetwork> getInterconnections(Collection<Product> parents,
                                                    Collection<Relationship> relationships,
                                                    Collection<Product> children) {
        if (parents == null || parents.size() == 0 || relationships == null
                || relationships.size() == 0 || children == null
                || children.size() == 0) {
            return null;
        }
        TypedQuery<ProductNetwork> query = em.createNamedQuery(ProductNetwork.GET_NETWORKS,
                                                               ProductNetwork.class);
        query.setParameter("parents", parents);
        query.setParameter("relationships", relationships);
        query.setParameter("children", children);
        return query.getResultList();
    }

    /**
     * @param attributeValue
     * @return
     */
    private ProductAttributeAuthorization getValidatingAuthorization(ProductAttribute attributeValue) {
        String sql = "SELECT  p FROM ProductAttributeAuthorization p "
                + "WHERE p.validatingAttribute IS NOT NULL "
                + "AND p.authorizedAttribute = :attribute ";
        TypedQuery<ProductAttributeAuthorization> query = em.createQuery(sql,
                                                                         ProductAttributeAuthorization.class);
        query.setParameter("attribute", attributeValue.getAttribute());
        List<ProductAttributeAuthorization> auths = query.getResultList();
        TypedQuery<ProductNetwork> networkQuery = em.createNamedQuery(ProductNetwork.GET_NETWORKS,
                                                                      ProductNetwork.class);
        networkQuery.setParameter("parent", attributeValue.getProduct());
        for (ProductAttributeAuthorization auth : auths) {
            networkQuery.setParameter("relationship", auth.getClassification());
            networkQuery.setParameter("child", auth.getClassifier());
            try {
                if (networkQuery.getSingleResult() != null) {
                    return auth;
                }
            } catch (NoResultException e) {
                // keep going
            }
        }
        return null;
    }

    /**
     * @param product
     * @param aspect
     */
    protected List<ProductAttribute> initialize(Product product,
                                                Aspect<Product> aspect) {
        product.link(aspect.getClassification(), aspect.getClassifier(),
                     kernel.getCoreModel(), kernel.getInverseSoftware(), em);
        List<ProductAttribute> attributes = new ArrayList<>();
        for (ProductAttributeAuthorization authorization : getAttributeAuthorizations(aspect)) {
            ProductAttribute attribute = new ProductAttribute(
                                                              authorization.getAuthorizedAttribute(),
                                                              kernel.getCoreModel());
            attributes.add(attribute);
            attribute.setProduct(product);
            defaultValue(attribute);
            em.persist(attribute);
        }
        return attributes;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.chiralbehaviors.CoRE.meta.NetworkedModel#setAttributeValue(com.
     * chiralbehaviors.CoRE.ExistentialRuleform,
     * com.chiralbehaviors.CoRE.attribute.Attribute, java.lang.Object)
     */
    @Override
    public void setAttributeValue(ProductAttribute attributeValue) {
        ProductAttributeAuthorization auth = getValidatingAuthorization(attributeValue);
        if (auth != null) {
            Attribute validatingAttribute = auth.getValidatingAttribute();
            if (validatingAttribute.getValueType().equals(attributeValue.getAttribute().getValueType())) {
                TypedQuery<AttributeMetaAttribute> valueQuery = em.createNamedQuery(AttributeMetaAttribute.GET_ATTRIBUTE,
                                                                                    AttributeMetaAttribute.class);
                valueQuery.setParameter("meta", attributeValue.getAttribute());
                valueQuery.setParameter("attr", validatingAttribute);
                List<AttributeMetaAttribute> values = valueQuery.getResultList();
                boolean valid = false;
                for (AttributeMetaAttribute value : values) {
                    if (attributeValue.getTextValue().equals(value.getTextValue())) {
                        valid = true;
                        break;
                    }
                }
                if (!valid) {
                    throw new IllegalArgumentException(
                                                       String.format("%s is not a valid picklist value for attribute %s",
                                                                     attributeValue.getTextValue(),
                                                                     attributeValue.getAttribute().getName()));

                }
            }
        }

        em.persist(attributeValue);

    }
}
