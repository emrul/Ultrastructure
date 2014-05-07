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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Element;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.ElementHelper;

/**
 * @author hparry
 *
 */
abstract class ElementImpl implements Element {

    protected Map<String, Object> properties = new HashMap<String, Object>();
    protected final String id;
    protected final GraphImpl graph;

    protected ElementImpl(final String id, final GraphImpl graph) {
        this.graph = graph;
        this.id = id;
    }

    public Set<String> getPropertyKeys() {
        return new HashSet<String>(this.properties.keySet());
    }

    public <T> T getProperty(final String key) {
        return (T) this.properties.get(key);
    }

    public void setProperty(final String key, final Object value) {
        ElementHelper.validateProperty(this, key, value);
        Object oldValue = this.properties.put(key, value);
        if (this instanceof VertexImpl)
            this.graph.vertexKeyIndex.autoUpdate(key, value, oldValue, (VertexImpl) this);
        else
            this.graph.edgeKeyIndex.autoUpdate(key, value, oldValue, (EdgeImpl) this);
    }

    public <T> T removeProperty(final String key) {
        Object oldValue = this.properties.remove(key);
        if (this instanceof VertexImpl)
            this.graph.vertexKeyIndex.autoRemove(key, oldValue, (VertexImpl) this);
        else
            this.graph.edgeKeyIndex.autoRemove(key, oldValue, (EdgeImpl) this);
        return (T) oldValue;
    }


    public int hashCode() {
        return this.id.hashCode();
    }

    public String getId() {
        return this.id;
    }

    public boolean equals(final Object object) {
        return ElementHelper.areEqual(this, object);
    }

    public void remove() {
        if (this instanceof Vertex)
            this.graph.removeVertex((Vertex) this);
        else
            this.graph.removeEdge((Edge) this);
    }

}
