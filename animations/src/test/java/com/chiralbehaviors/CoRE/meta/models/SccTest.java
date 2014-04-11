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

package com.chiralbehaviors.CoRE.meta.models;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.chiralbehaviors.CoRE.event.status.StatusCode;
import com.chiralbehaviors.CoRE.meta.models.SCC;

/**
 * @author hhildebrand
 * 
 */
public class SccTest {

    @Test
    public void testScc() {
        Map<StatusCode, List<StatusCode>> graph = new HashMap<StatusCode, List<StatusCode>>();
        StatusCode[] codes = new StatusCode[] { new StatusCode(0L),
                new StatusCode(1L), new StatusCode(2L), new StatusCode(3L),
                new StatusCode(4L), new StatusCode(5L), new StatusCode(6L),
                new StatusCode(7L), new StatusCode(8L) };
        graph.put(codes[0], asList(codes[1]));
        graph.put(codes[1], asList(codes[2]));
        graph.put(codes[2], asList(codes[0], codes[6]));
        graph.put(codes[3], asList(codes[4]));
        graph.put(codes[4], asList(codes[5], codes[6]));
        graph.put(codes[5], asList(codes[3]));
        graph.put(codes[6], asList(codes[7]));
        graph.put(codes[7], asList(codes[8]));
        graph.put(codes[8], asList(codes[6]));
        List<StatusCode[]> sccs = new SCC(graph).getStronglyConnectedComponents();
        assertNotNull(sccs);
        assertEquals(3, sccs.size());
        int i = 0;
        for (StatusCode n : asList(codes[6], codes[7], codes[8])) {
            assertEquals(n, sccs.get(0)[i++]);
        }
        i = 0;
        for (StatusCode n : asList(codes[0], codes[1], codes[2])) {
            assertEquals(n, sccs.get(1)[i++]);
        }
        i = 0;
        for (StatusCode n : asList(codes[3], codes[4], codes[5])) {
            assertEquals(n, sccs.get(2)[i++]);
        }
    }
}