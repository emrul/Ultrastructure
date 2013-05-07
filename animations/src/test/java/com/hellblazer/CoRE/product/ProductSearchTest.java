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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.dbunit.operation.DatabaseOperation;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.meta.Model;
import com.hellblazer.CoRE.meta.models.ModelImpl;
import com.hellblazer.CoRE.product.Product;
import com.hellblazer.CoRE.test.DatabaseTestContext;

/**
 * @author hhildebrand
 * 
 */
public class ProductSearchTest extends DatabaseTestContext {

    private static final Logger LOG = LoggerFactory.getLogger(ProductSearchTest.class);

    @Test()
    public void findByIdTest() {
        beginTransaction();

        Model model = new ModelImpl(em, null);
        Product b = model.find(new Long(1), Product.class);
        assertNotNull(b);
        assertEquals("Mass List 1", b.getName());

        commitTransaction();
    }

    @Test()
    public void findByNameTest() {
        beginTransaction();
        Product b = new ModelImpl(em, null).find("Mass List 1", Product.class);
        assertNotNull(b);
        assertEquals("Mass List 1", b.getName());
        assertEquals(Long.valueOf(1L), b.getId());

        commitTransaction();
    }

    // @Test
    public void getFlaggedTest() {
        beginTransaction();

        List<Product> l = new ModelImpl(em, null).findFlagged(Product.class);

        assertNotNull(l);
        for (Product b : l) {
            LOG.debug(String.format("Product: %s", b));
        }
        assertFalse(l.isEmpty());
        assertEquals(l.size(), 1);
        assertEquals(l.get(0).getName(), "Genomic Region");

        commitTransaction();
    }

    @Override
    protected void prepareSettings() {
        dataSetLocation = "ProductTestSeedData.xml";
        beforeTestOperations.add(DatabaseOperation.CLEAN_INSERT);
    }

    @Override
    protected void setSequences() throws Exception {
        //        setSequenceWithLastCalled("resource_id_seq", 1);
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('resource_id_seq', 1)");
        //        setSequenceWithLastCalled("attribute_id_seq", 4);
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('attribute_id_seq', 4)");
        //        setSequenceWithLastCalled("product_id_seq", 1);
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('product_id_seq', 1)");
        //        setSequenceWithLastCalled("unit_id_seq", 1);
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('unit_id_seq', 1)");
        //        setSequenceWithLastCalled("product_attribute_id_seq", 2);
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('product_attribute_id_seq', 2)");
        //        setSequenceWithLastCalled("relationship_id_seq", 2);
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('relationship_id_seq', 2)");        
        //        setSequenceWithLastCalled("product_network_id_seq", 1);
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('product_network_id_seq', 1)");
        //        restartSequence("sequences.genomic_region_seq");
        //        //getConnection().getConnection().createStatement().execute("SELECT setval('sequences.genomic_region_seq', 1, false)");
    }
}