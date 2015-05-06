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

package com.chiralbehaviors.CoRE.kernel;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.persistence.EntityManager;

import org.hibernate.internal.SessionImpl;

import com.chiralbehaviors.CoRE.WellKnownObject.WellKnownProduct;
import com.chiralbehaviors.CoRE.json.CoREModule;
import com.chiralbehaviors.CoRE.meta.Model;
import com.chiralbehaviors.CoRE.meta.workspace.DatabaseBackedWorkspace;
import com.chiralbehaviors.CoRE.meta.workspace.Workspace;
import com.chiralbehaviors.CoRE.meta.workspace.WorkspaceSnapshot;
import com.chiralbehaviors.CoRE.product.Product;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;

/**
 * Repository of immutable kernal rules
 *
 * This used to be the standard. Now we use workspaces. However, kernel is a
 * fundamental workspace, and it's needed a lot. Consequently, because of the
 * way we do Java stored procedures, reentrancy requires a new image of the
 * kernel workspace in the context of the entity manager. Sucks to be us.
 *
 * Utilities for the Kernel
 *
 * @author hhildebrand
 *
 */
public class KernelUtil {

    public static final String KERNEL_WORKSPACE_RESOURCE = "/kernel-workspace.json";

    public static final String SELECT_TABLE              = "SELECT table_schema || '.' || table_name AS name FROM information_schema.tables WHERE table_schema='ruleform' AND table_type='BASE TABLE' ORDER BY table_name";

    public static void clear(EntityManager em) throws SQLException {
        em.getTransaction().begin();
        boolean committed = false;
        Connection connection = em.unwrap(SessionImpl.class).connection();
        try {
            connection.setAutoCommit(false);
            alterTriggers(connection, false);
            ResultSet r = connection.createStatement().executeQuery(KernelUtil.SELECT_TABLE);
            while (r.next()) {
                String table = r.getString("name");
                String query = String.format("DELETE FROM %s", table);
                connection.createStatement().execute(query);
            }
            r.close();
            alterTriggers(connection, true);
            em.getTransaction().commit();
            committed = true;
        } finally {
            if (!committed) {
                connection.rollback();
                em.getTransaction().rollback();
            }
        }
    }

    public static void clearAndLoadKernel(EntityManager em)
                                                           throws SQLException,
                                                           IOException {
        clear(em);
        loadKernel(em);
    }

    public static Workspace getKernelWorkspace(Model model) {
        return new DatabaseBackedWorkspace(
                                           model.getEntityManager().find(Product.class,
                                                                         WellKnownProduct.KERNEL_WORKSPACE.id()),
                                           model);
    }

    public static void loadKernel(EntityManager em) throws IOException {
        loadKernel(em,
                   KernelUtil.class.getResourceAsStream(KernelUtil.KERNEL_WORKSPACE_RESOURCE));
    }

    public static void loadKernel(EntityManager em, InputStream is)
                                                                   throws IOException {
        em.getTransaction().begin();
        WorkspaceSnapshot workspace = rehydrateKernel(is);
        workspace.retarget(em);
        em.getTransaction().commit();
    }

    private static WorkspaceSnapshot readKernel(InputStream is)
                                                               throws IOException,
                                                               JsonParseException,
                                                               JsonMappingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new CoREModule());
        mapper.registerModule(new Hibernate4Module());
        WorkspaceSnapshot workspace = mapper.readValue(is,
                                                       WorkspaceSnapshot.class);
        return workspace;
    }

    private static WorkspaceSnapshot rehydrateKernel(InputStream is)
                                                                    throws IOException,
                                                                    JsonParseException,
                                                                    JsonMappingException {
        return readKernel(is);
    }

    static void alterTriggers(Connection connection, boolean enable)
                                                                    throws SQLException {
        for (String table : new String[] { "ruleform.agency",
                "ruleform.product", "ruleform.location" }) {
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        ResultSet r = connection.createStatement().executeQuery(KernelUtil.SELECT_TABLE);
        while (r.next()) {
            String table = r.getString("name");
            String query = String.format("ALTER TABLE %s %s TRIGGER ALL",
                                         table, enable ? "ENABLE" : "DISABLE");
            connection.createStatement().execute(query);
        }
        r.close();
    }
}
