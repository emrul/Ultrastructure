/**
 * Copyright (C) 2013 Hal Hildebrand. All rights reserved.
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

package com.hellblazer.CoRE.jsp;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.Callable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.openjpa.jdbc.sql.SQLExceptions;
import org.postgresql.pljava.Session;
import org.postgresql.pljava.SessionManager;
import org.postgresql.pljava.TransactionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hellblazer.CoRE.kernel.WellKnownObject;

/**
 * 
 * @author hhildebrand
 * 
 */
public class JSP {
    /**
     * 
     */
    private static final String        ABORTED = "jsp.aborted";
    private static final EntityManager EM;
    private static final Logger        log     = LoggerFactory.getLogger(JSP.class);
    private static Session             CURRENT_SESSION;

    static {
        try {
            Thread.currentThread().setContextClassLoader(JSP.class.getClassLoader());
            SQLExceptions.class.getCanonicalName();
            InputStream is = JSP.class.getResourceAsStream("jpa.properties");
            if (is == null) {
                log.error("Unable to read jpa.properties, resource is null");
                throw new IllegalStateException(
                                                "Unable to read jpa.properties, resource is null");
            }
            Properties properties = new Properties();
            try {
                properties.load(is);
            } catch (IOException e) {
                log.error("Unable to read jpa properties", e);
                throw new IllegalStateException(
                                                "Unable to read jpa.properties",
                                                e);
            }
            EntityManagerFactory emf = Persistence.createEntityManagerFactory(WellKnownObject.CORE,
                                                                              properties);
            EM = emf.createEntityManager();
            CURRENT_SESSION = SessionManager.current();
            CURRENT_SESSION.setAttribute(ABORTED, Boolean.FALSE);
            EM.getTransaction().begin();
            CURRENT_SESSION.addTransactionListener(new TransactionListener() {
                @Override
                public void onPrepare(Session session) throws SQLException {
                    EM.getTransaction().commit();
                    EM.getTransaction().begin();
                }

                @Override
                public void onCommit(Session session) throws SQLException {
                }

                @Override
                public void onAbort(Session session) throws SQLException {
                    CURRENT_SESSION.setAttribute(ABORTED, Boolean.TRUE);
                }
            });
            log.info("Entity manager created");
        } catch (RuntimeException e) {
            log.error("Unable to initialize Animations", e);
            throw e;
        } catch (SQLException e) {
            log.error("Unable to retreive current Session from SessionManager",
                      e);
            throw new IllegalStateException(
                                            "Unable to retreive current Session from SessionManager",
                                            e);
        }
    }

    public static EntityManager getEm() {
        return EM;
    }

    public static <T> T execute(Callable<T> call) throws SQLException {
        Thread.currentThread().setContextClassLoader(JSP.class.getClassLoader());

        if ((Boolean) CURRENT_SESSION.getAttribute(ABORTED)) {
            EM.getTransaction().rollback();
            CURRENT_SESSION.setAttribute(ABORTED, Boolean.FALSE);
        }
        try {
            return call.call();
        } catch (Throwable e) {
            StringWriter writer = new StringWriter();
            PrintWriter pWriter = new PrintWriter(writer);
            e.printStackTrace(pWriter);
            throw new SQLException(String.format("Stored procedure failed\n%s",
                                                 writer.toString()), e);
        }
    }
}