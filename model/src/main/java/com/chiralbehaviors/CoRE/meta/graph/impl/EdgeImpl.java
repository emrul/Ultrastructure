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

import com.chiralbehaviors.CoRE.meta.graph.Edge;
import com.chiralbehaviors.CoRE.meta.graph.Node;

/**
 * @author hhildebrand
 * 
 */
public class EdgeImpl<T> implements Edge<T> {

	private final Node<?> child;
	private final T model;
	private final Node<?> parent;

	public EdgeImpl(Node<?> parent, T model, Node<?> child) {
		super();
		this.parent = parent;
		this.model = model;
		this.child = child;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.meta.graph.Edge#getChild()
	 */
	@Override
	public Node<?> getChild() {
		return child;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.meta.graph.Edge#getEdgeObject()
	 */
	@Override
	public T getEdgeObject() {
		return model;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.chiralbehaviors.CoRE.meta.graph.Edge#getParent()
	 */
	@Override
	public Node<?> getParent() {
		return parent;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof EdgeImpl) {
			Node<?> child = ((EdgeImpl<?>)obj).getChild();
			@SuppressWarnings("unchecked")
			T model = (T) ((EdgeImpl<?>)obj).getEdgeObject();
			Node<?> parent = ((EdgeImpl<?>)obj).getParent();
			
			if ((this.parent.equals(parent)) && (this.model.equals(model)) && (this.child.equals(child))) {
				return true;
			}
		}
		return false;
	}
}