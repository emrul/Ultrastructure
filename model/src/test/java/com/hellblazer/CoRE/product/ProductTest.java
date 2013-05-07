/**
 * Copyright (C) 2012 Hal Hildebrand. All rights reserved.
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
package com.hellblazer.CoRE.product;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.Set;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.attribute.Attribute;
import com.hellblazer.CoRE.network.Relationship;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.product.ProductAttribute;
import com.hellblazer.CoRE.product.ProductNetwork;
import com.hellblazer.CoRE.resource.Resource;
import com.hellblazer.CoRE.test.DatabaseTestContext;

/**
 * @author hhildebrand
 * 
 */

public class ProductTest extends DatabaseTestContext {

    private static final Logger LOG = LoggerFactory.getLogger(ProductTest.class);

    @Test
    public void createEntity() {
        beginTransaction();

        TypedQuery<Resource> query = em.createNamedQuery("resource.findByName",
                                                         Resource.class).setParameter("name",
                                                                                      "CoRE");
        Resource r = query.getSingleResult();

        LOG.debug(String.format("Resource: %s", r));

        assertNotNull("Resource was null!", r);
        assertEquals("CoRE", r.getName());

        Product b = new Product();

        String name = "New Product";
        b.setName(name);
        b.setDescription("An Product created solely for testing purposes");
        b.setUpdatedBy(r);
        b.setPinned(false);

        em.persist(b);
        commitTransaction();

        // Now check to see that the Product you just made actually got into 
        // the database. 

        em.clear();

        TypedQuery<Product> productQuery = em.createNamedQuery("product.findByName",
                                                             Product.class).setParameter("name",
                                                                                        name);

        Product b2 = productQuery.getSingleResult();

        assertNotNull("Retrieved Product was null!", b2);
        assertTrue(b != b2);
        assertEquals(b, b2);
    }

    @SuppressWarnings("boxing")
    @Test
    public void testAttributes() {
        beginTransaction();

        TypedQuery<Product> findProduct = em.createNamedQuery("product.findByName",
                                                            Product.class).setParameter("name",
                                                                                       "Peptide Foo");
        Product b = findProduct.getSingleResult();
        assertNotNull(b);
        assertEquals(Long.valueOf(1L), b.getId());
        assertEquals(b.getName(), "Peptide Foo");
        LOG.debug(String.format("Product is: %s", b));

        TypedQuery<Attribute> findAttribute = em.createNamedQuery("attribute.findByName",
                                                                  Attribute.class).setParameter("name",
                                                                                                "Length");

        Attribute a = findAttribute.getSingleResult();
        assertNotNull(a);
        assertEquals(Long.valueOf(1L), a.getId());
        assertEquals(a.getName(), "Length");
        LOG.debug(String.format("Attribute is: %s", a));

        Set<ProductAttribute> productAttributes = b.getAttributes();
        assertNotNull(productAttributes);
        assertEquals(1, productAttributes.size());

        Iterator<ProductAttribute> iter = productAttributes.iterator();
        ProductAttribute bea = iter.next();
        assertNotNull(bea);
        assertEquals(Long.valueOf(1L), bea.getId());
        assertEquals(b, bea.getProduct());
        assertEquals(a, bea.getAttribute());

        assertEquals(new BigDecimal("123"), bea.getNumericValue());

        commitTransaction();
    }

    public void testChildNetwork() {
        beginTransaction();
        Product b = em.find(Product.class, Long.valueOf(1L));

        assertNotNull(b);
        assertEquals("Peptide Foo", b.getName());
        LOG.debug(String.format("Product is: %s", b));

        Set<ProductNetwork> childRules = b.getNetworkByChild();
        assertNotNull(childRules);
        assertEquals(1, childRules.size());

        Iterator<ProductNetwork> iter = childRules.iterator();
        ProductNetwork ben = iter.next();
        assertNotNull(ben);
        assertEquals(Long.valueOf(1L), ben.getId());
        assertEquals(ben.getChild(), b);
        assertEquals("Top Node", ben.getParent().getName());
        assertEquals("includes", ben.getRelationship().getName());

        commitTransaction();
    }

    @Test
    public void testParentNetwork() {
        beginTransaction();

        Resource core = em.find(Resource.class, Long.valueOf(1L));
        assertEquals("CoRE", core.getName());

        Relationship includes = em.find(Relationship.class, Long.valueOf(2L));
        assertEquals("includes", includes.getName());

        Product b = em.find(Product.class, Long.valueOf(1L));
        assertEquals("Peptide Foo", b.getName());

        Product b2 = new Product();
        b2.setName("Great Horny Toads!");
        b2.setDescription("Foo!");
        b2.setUpdatedBy(core);
        em.persist(b2);

        ProductNetwork net = new ProductNetwork();
        net.setRelationship(includes);
        net.setChild(b2);
        net.setDistance(1);
        net.setUpdatedBy(core);

        // This should get persisted in the database automatically
        b.addParentRelationship(net);

        commitTransaction();

        em.clear();
        beginTransaction();

        // now check to see that things went in as expected 
        Query query = em.createNativeQuery("SELECT parent AS p, relationship AS rel, child AS c FROM ruleform.product_network WHERE parent = ?");
        query.setParameter(1, b.getId());
        Object[] result = (Object[]) query.getSingleResult();

        assertNotNull("The expected ProductNetwork record was not returned.  Are you sure the ProductNetwork collection is mapped with cascade=\"save-update\"?",
                      result);
        assertEquals(b.getId(), result[0]);
        assertEquals(includes.getId(), result[1]);
        assertEquals(b2.getId(), result[2]);

        commitTransaction();
    }

    /* (non-Javadoc)
     * @see com.hellblazer.CoRE.testing.HibernateDatabaseTestCase#prepareSettings()
     */
    @Override
    protected void prepareSettings() {
        LOG.trace("Entering prepareSettings");
        dataSetLocation = "ProductTest.xml";
        beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);

        LOG.trace("Exiting prepareSettings");
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.hellblazer.CoRE.testing.HibernateDatabaseTestCase#setSequences()
     */
    @Override
    protected void setSequences() throws Exception {
        LOG.trace("Entering setSequences");
        getConnection().getConnection().createStatement().execute("SELECT setval('resource_id_seq', 1)");
        getConnection().getConnection().createStatement().execute("SELECT setval('attribute_id_seq', 1)");
        getConnection().getConnection().createStatement().execute("SELECT setval('product_id_seq', 2)");
        getConnection().getConnection().createStatement().execute("SELECT setval('unit_id_seq', 1)");
        getConnection().getConnection().createStatement().execute("SELECT setval('product_attribute_id_seq', 1)");
        getConnection().getConnection().createStatement().execute("SELECT setval('relationship_id_seq', 2)");
        getConnection().getConnection().createStatement().execute("SELECT setval('research_id_seq', 5)");
        getConnection().getConnection().createStatement().execute("SELECT setval('product_network_id_seq', 1, false)");
        //getConnection().getConnection().createStatement().execute("SELECT setval('event_id_seq', 5)");
        LOG.trace("Exiting setSequences");
    }

}