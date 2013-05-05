/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * This program is free software; you can redistribute it and/or modify
 */
package org.arakhne.neteditor.formalism;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** This is the semantic object for graph models.
 *
 * @param <G> is the type of the graph.
 * @param <N> is the type of the node inside the graph.
 * @param <A> is the type of the anchors inside the graph.
 * @param <E> is the type of the node inside the graph.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Graph<G extends Graph<?,N,A,E>, N extends Node<G,?,A,E>, A extends Anchor<G,N,?,E>, E extends Edge<G,N,A,?>> extends ModelObject {
	
	/** Replies if this graph has no node nor edge inside.
	 *
	 * @return <code>true</code> if the graph has no node nor edge inside;
	 * otherwise <code>false</code>.
	 */
	public boolean isEmpty();

	/** Replies the number of nodes in the graph.
	 *
	 * @return the number of nodes in the graph.
	 */
	public int getNodeCount();

	/** Get the node's list of this Graph
	 *
	 * @return a list of {@link Node Nodes}.
	 */
	public Set<N> getNodes();

	/** Get the nodes that have the specified name.
	 *
	 * @param name
	 * @return a list of {@link Node Nodes}.
	 * @see #getNodeByName(String)
	 */
	public Set<N> getNodesByName(String name);

	/** Get the node that has the specified identifier.
	 *
	 * @param id
	 * @return the node or <code>null</code>
	 */
	public N getNodeByUUID(UUID id);

	/** Get the nodes classified by their names.
	 *  If two or
	 * more nodes have the same name, only one is replied.
	 *
	 * @return the map of the pairs name-node.
	 */
	public Map<String,N> getNodeByName();

	/** Get the nodes classified by their names.
	 *
	 * @return the map of the pairs name-nodes.
	 */
	public Map<String,Set<N>> getNodesByName();

	/** Replies the names of the nodes in a single collection.
	 *
	 * @return all the names of the nodes in this graph.
	 */
	public Set<String> getNodeNames();

	/** Get the first node that has the specified name.
	 *
	 * @param name
	 * @return a list of {@link Node Nodes}.
	 * @see #getNodesByName(String)
	 */
	public N getNodeByName(String name);

	/** Add a {@link Node node} in this Graph.
	 *
	 * @param node the new {@link Node Node}.
	 */
	public void addNode(N node);

	/** Remove a node attached to this Graph
	 * All the link to other objects of the graph
	 * are also broking.
	 *
	 * @param node the {@link Node Node} to remove.
	 */
	public void removeNode(N node);

	/** Replies the number of edges in the graph.
	 *
	 * @return the number of edges in the graph.
	 */
	public int getEdgeCount();

	/** Get the edge's list of this Graph.
	 *
	 * @return a list of {@link Edge Edges}.
	 */
	public Set<E> getEdges();

	/** Replies the names of the edges in a single collection.
	 *
	 * @return all the names of the edges in this graph.
	 */
	public Set<String> getEdgeNames();

	/** Get the edges that have the specified name.
	 *
	 * @param name
	 * @return a list of {@link Edge Edges}.
	 * @see #getEdgeByName(String)
	 */
	public Set<E> getEdgesByName(String name);

	/** Get the edge that has the specified identifier.
	 *
	 * @param id
	 * @return the edge or <code>null</code>
	 */
	public E getEdgeByUUID(UUID id);

	/** Get the first edge that has the specified name.
	 *
	 * @param name
	 * @return a list of {@link Edge Edges}.
	 * @see #getEdgesByName(String)
	 */
	public E getEdgeByName(String name);

	/** Get the edges classified by their names.
	 *  If two or
	 * more edges have the same name, only one is replied.
	 *
	 * @return the map of the pairs name-edge.
	 */
	public Map<String,E> getEdgeByName();

	/** Get the edges classified by their names.
	 *
	 * @return the map of the pairs name-edges.
	 */
	public Map<String,Set<E>> getEdgesByName();

	/** Add a {@link Edge} in this Graph
	 *
	 * @param edge the new {@link Edge}.
	 */
	public void addEdge(E edge);

	/** Remove a edge attached to this Graph.
	 * All the link to other objects of the graph
	 * are also broking.
	 *
	 * @param edge the {@link Edge} to remove.
	 */
	public void removeEdge(E edge);

	/** Clear the graph content.
	 * This function preserve the connections between the removed objects.
	 */
	public void clear();

	/** Move all the model objects from the specified graph to
	 * the current graph.
	 * This function preserve the connections between the moved objects.
	 * 
	 * @param graph is the graph from which the objects should be moved.
	 * @return the number of objects that were moved.
	 */
	public int moveFromGraph(G graph);

	/** Move all the model objects from the specified graph to
	 * the current graph.
	 * This function preserve the connections between the moved objects.
	 * 
	 * @param graph is the graph from which the objects should be moved.
	 * @param identifiersToExtract are the identifiers to extract and move
	 * into the current graph.
	 * @return the number of objects that were moved.
	 */
	public int moveFromGraph(G graph, Set<UUID> identifiersToExtract);
	
}
