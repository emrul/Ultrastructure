/**
 * Copyright (c) 2015 Chiral Behaviors, LLC, all rights reserved.
 * 
 
 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chiralbehaviors.CoRE.workspace;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Collections;

import org.junit.Test;

import com.chiralbehaviors.CoRE.domain.Agency;
import com.chiralbehaviors.CoRE.domain.Product;
import com.chiralbehaviors.CoRE.jooq.tables.records.WorkspaceLabelRecord;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.test.DatabaseTest;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author hhildebrand
 *
 */
public class WorkspaceSnapshotTest extends DatabaseTest {

    @Test
    public void testSerializeWorkspaceSnapshot() throws Exception {
        Product definingProduct = RECORDS.newProduct("zee product");
        definingProduct.setWorkspace(definingProduct.getId());
        definingProduct.insert();
        Agency pseudoScientist = RECORDS.newAgency("Behold the Pseudo Scientist!");
        pseudoScientist.setWorkspace(definingProduct.getId());
        pseudoScientist.insert();
        WorkspaceLabelRecord auth = RECORDS.newWorkspaceLabel("Su Su Sudio",
                                                              definingProduct,
                                                              pseudoScientist);
        auth.insert();

        WorkspaceSnapshot retrieved = new WorkspaceSnapshot(definingProduct,
                                                            create);
        assertEquals(2, retrieved.getRecords()
                                 .size());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        File temp = File.createTempFile("snaptest", "wsp");
        temp.deleteOnExit();
        try (FileOutputStream os = new FileOutputStream(temp)) {
            mapper.writeValue(os, retrieved);
        }
        WorkspaceSnapshot deserialized;
        try (FileInputStream is = new FileInputStream(temp)) {
            deserialized = mapper.readValue(is, WorkspaceSnapshot.class);
        }
        assertEquals(2, deserialized.getRecords()
                                    .size());

        assertTrue(deserialized.getRecords()
                               .stream()
                               .anyMatch(r -> pseudoScientist.equals(r)));
        assertFalse(deserialized.getRecords()
                                .stream()
                                .anyMatch(r -> definingProduct.equals(r)));
        create.configuration()
              .connectionProvider()
              .acquire()
              .rollback();
        WorkspaceSnapshot.load(create, temp.toURI()
                                           .toURL());
    }

    @Test
    public void testSerializeSnapshot() throws Exception {
        Agency pseudoScientist = RECORDS.newAgency("Behold the Pseudo Scientist!");
        pseudoScientist.insert();
        Product definingProduct = RECORDS.newProduct("zee product");
        definingProduct.insert();

        StateSnapshot retrieved = new StateSnapshot(RECORDS.create(),
                                                    Collections.emptyList());
        assertEquals(2, retrieved.getRecords()
                                 .size());
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        File temp = File.createTempFile("snaptest", "wsp");
        temp.deleteOnExit();
        try (FileOutputStream os = new FileOutputStream(temp)) {
            mapper.writeValue(os, retrieved);
        }
        StateSnapshot deserialized;
        try (FileInputStream is = new FileInputStream(temp)) {
            deserialized = mapper.readValue(is, StateSnapshot.class);
        }
        assertEquals(2, deserialized.getRecords()
                                    .size());

        assertTrue(deserialized.getRecords()
                               .stream()
                               .anyMatch(r -> pseudoScientist.equals(r)));
    }
}
