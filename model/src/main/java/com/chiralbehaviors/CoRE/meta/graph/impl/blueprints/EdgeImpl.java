/** 
 * (C) Copyright 2014 Chiral Behaviors, LLC. All Rights Reserved
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
package com.chiralbehaviors.CoRE.meta.graph.impl.blueprints;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ExceptionFactory;
import com.tinkerpop.blueprints.util.StringFactory;

/**
 * @author hparry
 *
 */
public class EdgeImpl extends ElementImpl implements Edge {

    private final String label;
    private final Vertex inVertex;
    private final Vertex outVertex;

    protected EdgeImpl(final String id, final Vertex outVertex, final Vertex inVertex, final String label, final GraphImpl graph) {
        super(id, graph);
        this.label = label;
        this.outVertex = outVertex;
        this.inVertex = inVertex;
        this.graph.edgeKeyIndex.autoUpdate(StringFactory.LABEL, this.label, null, this);
    }

    public String getLabel() {
        return this.label;
    }

    public Vertex getVertex(final Direction direction) throws IllegalArgumentException {
        if (direction.equals(Direction.IN))
            return this.inVertex;
        else if (direction.equals(Direction.OUT))
            return this.outVertex;
        else
            throw ExceptionFactory.bothIsNotSupported();
    }

    public String toString() {
        return StringFactory.edgeString(this);
    }

}
