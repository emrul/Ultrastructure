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
package com.hellblazer.CoRE.meta.models;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.chiralbehaviors.CoRE.agency.Agency;
import com.chiralbehaviors.CoRE.coordinate.Coordinate;
import com.chiralbehaviors.CoRE.coordinate.CoordinateNetwork;
import com.chiralbehaviors.CoRE.network.NetworkInference;
import com.chiralbehaviors.CoRE.network.Relationship;

/**
 * @author hhildebrand
 * 
 */
public class CoordinateModelTest extends AbstractModelTest {

    @Test
    public void testSimpleNetworkPropagation() {
        Agency core = model.getKernel().getCore();
        Relationship equals = model.getKernel().getEquals();

        em.getTransaction().begin();

        Relationship equals2 = new Relationship("equals 2",
                                                "an alias for equals", core);
        equals2.setInverse(equals2);
        em.persist(equals2);
        NetworkInference aEqualsA = new NetworkInference(equals, equals2,
                                                         equals, core);
        em.persist(aEqualsA);
        Coordinate a = new Coordinate("A", core);
        em.persist(a);
        Coordinate b = new Coordinate("B", core);
        em.persist(b);
        Coordinate c = new Coordinate("C", core);
        em.persist(c);
        CoordinateNetwork edgeA = new CoordinateNetwork(a, equals, b, core);
        em.persist(edgeA);
        CoordinateNetwork edgeB = new CoordinateNetwork(b, equals2, c, core);
        em.persist(edgeB);

        em.getTransaction().commit();
        em.clear();

        List<CoordinateNetwork> edges = em.createQuery("SELECT edge FROM CoordinateNetwork edge WHERE edge.inferred = 1",
                                                       CoordinateNetwork.class).getResultList();
        assertEquals(2, edges.size());
    }
}
