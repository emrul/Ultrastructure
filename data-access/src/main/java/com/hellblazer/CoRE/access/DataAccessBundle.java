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
package com.hellblazer.CoRE.access;

import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.apache.openjpa.persistence.OpenJPAEntityManagerFactory;

import com.hellblazer.CoRE.access.health.JpaHealthCheck;
import com.hellblazer.CoRE.access.resource.CrudGuiResource;
import com.hellblazer.CoRE.access.resource.CrudResource;
import com.hellblazer.CoRE.access.resource.DomainResource;
import com.hellblazer.CoRE.access.resource.TraversalResource;
import com.hellblazer.CoRE.configuration.CoREServiceConfiguration;
import com.hellblazer.CoRE.configuration.JpaConfiguration;
import com.yammer.dropwizard.ConfiguredBundle;
import com.yammer.dropwizard.config.Environment;

/**
 * @author hhildebrand
 * 
 */
public class DataAccessBundle implements
        ConfiguredBundle<CoREServiceConfiguration> {

    /* (non-Javadoc)
     * @see com.yammer.dropwizard.ConfiguredBundle#initialize(java.lang.Object, com.yammer.dropwizard.config.Environment)
     */
    @Override
    public void initialize(CoREServiceConfiguration configuration,
                           Environment environment) {

        JpaConfiguration jpaConfig = configuration.getCrudServiceConfiguration();

        String unit = jpaConfig.getPersistenceUnit();
        Map<String, String> properties = jpaConfig.getProperties();
        properties.put("openjpa.EntityManagerFactoryPool", "true");
        EntityManagerFactory emf = Persistence.createEntityManagerFactory(unit,
                                                                          properties);
        environment.addResource(new CrudResource(emf));
        environment.addResource(new CrudGuiResource(unit));
        environment.addResource(new DomainResource(
                                                   unit,
                                                   (OpenJPAEntityManagerFactory) emf));
        environment.addResource(new TraversalResource(emf));
        environment.addHealthCheck(new JpaHealthCheck(emf));
        
    }
}
