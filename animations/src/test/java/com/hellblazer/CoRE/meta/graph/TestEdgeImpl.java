/**
 * Copyright (C) 2013 Halloran Parry. All rights reserved.
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
package com.hellblazer.CoRE.meta.graph;

/**
 * @author hparry
 *
 */
public class TestEdgeImpl implements IEdge {
	
	private INode<String> parent;
	private INode<String> child;
	
	public TestEdgeImpl(String parent, String child) {
		this.parent = new TestNodeImpl(parent);
		this.child = new TestNodeImpl(child);
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.IEdge#getParent()
	 */
	@Override
	public INode<String> getParent() {
		return this.parent;
	}

	/* (non-Javadoc)
	 * @see com.hellblazer.CoRE.meta.graph.IEdge#getChild()
	 */
	@Override
	public INode<String> getChild() {
		return this.child;
	}

}
