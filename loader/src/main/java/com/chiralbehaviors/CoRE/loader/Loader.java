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
package com.chiralbehaviors.CoRE.loader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.chiralbehaviors.CoRE.kernel.Bootstrap;
import com.hellblazer.utils.Utils;

/**
 * @author hhildebrand
 * 
 */
public class Loader {

    /**
     * 
     */
    private static final String CREATE_ROLES_SQL                                                    = "/create-roles.sql";
    /**
     * 
     */
    private static final String CREATE_DB_SQL                                                       = "/create-db.sql";
    private static final String ANIMATIONS_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_RULEFORM_ANIMATIONS_XML = "/animations/com/chiralbehaviors/CoRE/schema/ruleform/animations.xml";
    private static final String ANIMATIONS_JAR;
    private static final String ANIMATIONS_JAR_NAME;
    private static final Logger log                                                                 = LoggerFactory.getLogger(Loader.class);
    private static final String MODEL_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_CORE_XML                     = "/model/com/chiralbehaviors/CoRE/schema/core.xml";

    static {
        String version;
        try {
            version = Utils.getDocument(Loader.class.getResourceAsStream("/version"));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        ANIMATIONS_JAR = String.format("animations-%s.jar", version);
        ANIMATIONS_JAR_NAME = String.format("animations-%s",
                                            version.replace('.', '_'));
    }

    public static void main(String[] argv) throws Exception {
        Loader loader = new Loader(
                                   Configuration.fromYaml(Utils.resolveResource(Loader.class,
                                                                                argv[0])).getConnection());
        loader.bootstrap();
    }

    private final Connection connection;

    public Loader(Connection connection) throws Exception {
        this.connection = connection;
    }

    public void bootstrap() throws Exception {
        createDb();
        createRoles();
        loadAnimations();
        load(MODEL_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_CORE_XML);
        load(ANIMATIONS_COM_CHIRALBEHAVIORS_CO_RE_SCHEMA_RULEFORM_ANIMATIONS_XML);
        new Bootstrap(connection).bootstrap();
    }

    private void createDb() throws Exception, IOException {
        connection.setAutoCommit(false);
        execute(Utils.getDocument(getClass().getResourceAsStream(CREATE_DB_SQL)));
        connection.commit();
    }

    private void createRoles() throws Exception, IOException {
        connection.setAutoCommit(true);
        execute(Utils.getDocument(getClass().getResourceAsStream(CREATE_ROLES_SQL)));
        connection.commit();
    }

    private void drop(PreparedStatement drop, String name,
                      PreparedStatement validate) throws SQLException {
        validate.setString(1, name);
        ResultSet result = validate.executeQuery();
        if (result.next()) {
            log.info(String.format("dropping jar %s", name));
            drop.setString(1, name);
            drop.setBoolean(2, false);
            drop.execute();
            result = validate.executeQuery();
            if (result.next()) {
                throw new IllegalStateException(
                                                String.format("Did not actually drop %s",
                                                              name));
            }
        }
    }

    private void execute(String sqlFile) throws Exception {
        StringTokenizer tokes = new StringTokenizer(sqlFile, ";");
        while (tokes.hasMoreTokens()) {
            String line = tokes.nextToken();
            PreparedStatement exec = connection.prepareStatement(line);
            exec.execute();
            exec.close();
        }
    }

    private byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[16 * 1024];
            for (int read = is.read(buffer); read != -1; read = is.read(buffer)) {
                baos.write(buffer, 0, read);
            }
        } finally {
            is.close();
        }
        return baos.toByteArray();
    }

    private void load(PreparedStatement load) throws IOException, SQLException {
        byte[] bytes = getBytes(getClass().getResourceAsStream(ANIMATIONS_JAR));
        load.setBytes(1, bytes);
        load.setString(2, ANIMATIONS_JAR_NAME);
        load.setBoolean(3, true);
        load.execute();
    }

    private void load(String changeLog) throws Exception {
        Liquibase liquibase = null;
        try {
            Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(
                                                                                                                   connection));
            liquibase = new Liquibase(
                                      changeLog,
                                      new ClassLoaderResourceAccessor(
                                                                      getClass().getClassLoader()),
                                      database);
            liquibase.update(Integer.MAX_VALUE, "");

        } finally {
            if (liquibase != null) {
                liquibase.forceReleaseLocks();
            }
            try {
                connection.rollback();
                connection.close();
            } catch (SQLException e) {
                //nothing to do
            }
        }
    }

    private void loadAnimations() throws Exception {
        connection.setAutoCommit(true);
        PreparedStatement drop = connection.prepareStatement("SELECT sqlj.remove_jar(?, ?)");
        PreparedStatement load = connection.prepareStatement("SELECT sqlj.install_jar(?, ?, ?)");
        PreparedStatement validate = connection.prepareStatement("SELECT jarid from sqlj.jar_repository where jarname=?");
        drop(drop, ANIMATIONS_JAR_NAME, validate);
        log.info(String.format("loading artifact %s", ANIMATIONS_JAR));
        load(load);
        setClassPath();
    }

    private void setClassPath() throws SQLException {
        PreparedStatement statement = connection.prepareStatement(String.format("SELECT sqlj.set_classpath('ruleform', '%s')",
                                                                                ANIMATIONS_JAR_NAME));
        statement.execute();
    }
}
