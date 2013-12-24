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
package com.hellblazer.CoRE.product.access;

import static com.hellblazer.CoRE.product.access.ProductAccessAuthorization.FIND_AUTHORIZATION;
import static com.hellblazer.CoRE.product.access.ProductAccessAuthorization.GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.hellblazer.CoRE.ExistentialRuleform;
import com.hellblazer.CoRE.Ruleform;
import com.hellblazer.CoRE.authorization.AccessAuthorization;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductNetwork;

/**
 * @author hparry
 * 
 */
@NamedQueries({

               @NamedQuery(name = GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP, query = "SELECT auth "
                                                                                              + "FROM ProductAccessAuthorization auth "
                                                                                              + "WHERE auth.relationship = :r "
                                                                                              + "AND auth.parent = :rf "),
               @NamedQuery(name = FIND_AUTHORIZATION, query = "SELECT auth "
                                                              + "FROM ProductAccessAuthorization auth "
                                                              + "WHERE auth.parent = :parent "
                                                              + "AND auth.relationship = :relationship ") })
@Entity
public abstract class ProductAccessAuthorization<Child extends ExistentialRuleform<Child, ?>>
        extends AccessAuthorization<Product, Child> {
    public static final String  PRODUCT_ACCESS_AUTHORIZATION_PREFIX                = "productAccessAuthorization";

    public static final String  FIND_AUTHORIZATION                                 = PRODUCT_ACCESS_AUTHORIZATION_PREFIX
                                                                                     + FIND_AUTHORIZATION_SUFFIX;
    public static final String  GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP = PRODUCT_ACCESS_AUTHORIZATION_PREFIX
                                                                                     + GET_ALL_AUTHORIZATIONS_FOR_PARENT_AND_RELATIONSHIP_SUFFIX;

    private static final long   serialVersionUID                                   = 1L;

    //bi-directional many-to-one association to ProductNetwork
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<ProductNetwork> networkByParent;

    @ManyToOne
    @JoinColumn(name = "product1")
    private Product             parent;

    /**
     * @return the networkByParent
     */
    public Set<ProductNetwork> getNetworkByParent() {
        return networkByParent;
    }

    /**
     * @return the parent
     */
    @Override
    public Product getParent() {
        return parent;
    }

    /**
     * @param networkByParent
     *            the networkByParent to set
     */
    public void setNetworkByParent(Set<ProductNetwork> networkByParent) {
        this.networkByParent = networkByParent;
    }

    /**
     * @param parent
     *            the parent to set
     */
    public void setParent(Product parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.Ruleform#traverseForeignKeys(javax.persistence.
     * EntityManager, java.util.Map)
     */
    @Override
    public void traverseForeignKeys(EntityManager em,
                                    Map<Ruleform, Ruleform> knownObjects) {
        if (parent != null) {
            parent = (Product) parent.manageEntity(em, knownObjects);
        }
        super.traverseForeignKeys(em, knownObjects);
    }

}