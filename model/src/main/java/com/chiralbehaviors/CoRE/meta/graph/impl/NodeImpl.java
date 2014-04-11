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
package com.chiralbehaviors.CoRE.meta.graph.impl;

import com.chiralbehaviors.CoRE.meta.graph.Node;

/**
 * Wrapper class to turn a Ruleform into a node. Yes, it would be nice if the
 * Ruleform class just implemented the INode interface directly, but the
 * dependency management doesn't work out.
 * 
 * @author hparry
 * 
 */
public class NodeImpl<T> implements Node<T> {

    private T node;

    public NodeImpl(T node) {
        this.node = node;
    }

    @Override
    public T getNode() {
        return node;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return node.equals(obj);
	}
    
    

}