/**
 * (C) Copyright 2012 Chiral Behaviors, LLC. All Rights Reserved
 *

 * This file is part of Ultrastructure.
 *
 *  Ultrastructure is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 *  ULtrastructure is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Ultrastructure.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.chiralbehaviors.CoRE.utils;

/**
 * @author hhildebrand
 *
 */

public class TopologicalSort {

	public interface Edge {

	}

	public interface Node {

	}

	public static void main(String[] args) {
		/**
		 * //while S is non-empty do while (!S.isEmpty()) { //remove a node n
		 * from S Node n = S.iterator().next(); S.remove(n);
		 *
		 * //insert n into L L.add(n);
		 *
		 * //for each node m with an edge e from n to m do for (Iterator<Edge>
		 * it = n.outEdges.iterator(); it.hasNext();) { //remove edge e from the
		 * graph Edge e = it.next(); Node m = e.to; it.remove();//Remove edge
		 * from n m.inEdges.remove(e);//Remove edge from m
		 *
		 * //if m has no other incoming edges then insert m into S if
		 * (m.inEdges.isEmpty()) { S.add(m); } } } //Check to see if all edges
		 * are removed boolean cycle = false; for (Node n : allNodes) { if
		 * (!n.inEdges.isEmpty()) { cycle = true; break; } } if (cycle) {
		 * System.out.println("Cycle present, topological sort not possible"); }
		 * else { System.out.println("Topological Sort: " +
		 * Arrays.toString(L.toArray())); }
		 */
	}
}