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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.VertexQuery;
import com.tinkerpop.blueprints.util.DefaultVertexQuery;
import com.tinkerpop.blueprints.util.MultiIterable;
import com.tinkerpop.blueprints.util.StringFactory;
import com.tinkerpop.blueprints.util.VerticesFromEdgesIterable;

/**
 * @author hparry
 *
 */
public class VertexImpl extends ElementImpl implements Vertex {
    
    

    protected Map<String, Set<Edge>> outEdges = new HashMap<String, Set<Edge>>();
    protected Map<String, Set<Edge>> inEdges = new HashMap<String, Set<Edge>>();

    protected VertexImpl(final String id, final GraphImpl graph) {
        super(id, graph);
    }

    public Iterable<Edge> getEdges(final Direction direction, final String... labels) {
        if (direction.equals(Direction.OUT)) {
            return this.getOutEdges(labels);
        } else if (direction.equals(Direction.IN))
            return this.getInEdges(labels);
        else {
            return new MultiIterable<Edge>(Arrays.asList(this.getInEdges(labels), this.getOutEdges(labels)));
        }
    }

    public Iterable<Vertex> getVertices(final Direction direction, final String... labels) {
        return new VerticesFromEdgesIterable(this, direction, labels);
    }

    private Iterable<Edge> getInEdges(final String... labels) {
        if (labels.length == 0) {
            final List<Edge> totalEdges = new ArrayList<Edge>();
            for (final Collection<Edge> edges : this.inEdges.values()) {
                totalEdges.addAll(edges);
            }
            return totalEdges;
        } else if (labels.length == 1) {
            final Set<Edge> edges = this.inEdges.get(labels[0]);
            if (null == edges) {
                return Collections.emptyList();
            } else {
                return new ArrayList<Edge>(edges);
            }
        } else {
            final List<Edge> totalEdges = new ArrayList<Edge>();
            for (final String label : labels) {
                final Set<Edge> edges = this.inEdges.get(label);
                if (null != edges) {
                    totalEdges.addAll(edges);
                }
            }
            return totalEdges;
        }
    }

    private Iterable<Edge> getOutEdges(final String... labels) {
        if (labels.length == 0) {
            final List<Edge> totalEdges = new ArrayList<Edge>();
            for (final Collection<Edge> edges : this.outEdges.values()) {
                totalEdges.addAll(edges);
            }
            return totalEdges;
        } else if (labels.length == 1) {
            final Set<Edge> edges = this.outEdges.get(labels[0]);
            if (null == edges) {
                return Collections.emptyList();
            } else {
                return new ArrayList<Edge>(edges);
            }
        } else {
            final List<Edge> totalEdges = new ArrayList<Edge>();
            for (final String label : labels) {
                final Set<Edge> edges = this.outEdges.get(label);
                if (null != edges) {
                    totalEdges.addAll(edges);
                }
            }
            return totalEdges;
        }
    }

    public VertexQuery query() {
        return new DefaultVertexQuery(this);
    }

    public String toString() {
        return StringFactory.vertexString(this);
    }

    public Edge addEdge(final String label, final Vertex vertex) {
        return this.graph.addEdge(null, this, vertex, label);
    }

    protected void addOutEdge(final String label, final Edge edge) {
        Set<Edge> edges = this.outEdges.get(label);
        if (null == edges) {
            edges = new HashSet<Edge>();
            this.outEdges.put(label, edges);
        }
        edges.add(edge);
    }

    protected void addInEdge(final String label, final Edge edge) {
        Set<Edge> edges = this.inEdges.get(label);
        if (null == edges) {
            edges = new HashSet<Edge>();
            this.inEdges.put(label, edges);
        }
        edges.add(edge);
    }

}
