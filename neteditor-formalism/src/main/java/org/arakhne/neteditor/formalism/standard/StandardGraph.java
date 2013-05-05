/* 
 * $Id$
 * 
 * Copyright (C) 2012 Stephane GALLAND.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
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
package org.arakhne.neteditor.formalism.standard ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;

import org.arakhne.neteditor.formalism.AbstractModelObject;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.ModelObjectListener;

/** Define a graph with the standard implementation.
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
public class StandardGraph<G extends StandardGraph<G,N,A,E>, N extends AbstractStandardNode<G,N,A,E>, A extends StandardAnchor<G,N,A,E>, E extends StandardEdge<G,N,A,E>>
extends AbstractModelObject implements Graph<G,N,A,E> {

	private static final long serialVersionUID = -1864076783546754533L;

	private final Set<N> nodes = new TreeSet<N>();
	private final Set<E> edges = new TreeSet<E>();
	
	private final Listener listener = new Listener();
	
	/**
	 */
	public StandardGraph() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isEmpty() {
		return this.nodes.isEmpty() && this.edges.isEmpty();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNodeCount() {
		return this.nodes.size();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getEdgeCount() {
		return this.edges.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObject findModelObject(UUID id) {
		if (getUUID().equals(id)) return this;
		for(N node : this.nodes) {
			ModelObject obj = node.findModelObject(id);
			if (obj!=null) return obj;
		}
		for(E edge : this.edges) {
			ModelObject obj = edge.findModelObject(id);
			if (obj!=null) return obj;
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<N> getNodes() {
		return Collections.unmodifiableSet(this.nodes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<N> getNodesByName(String name) {
		Set<N> collection = new TreeSet<N>();
		String n;
		for(N node : this.nodes) {
			n = node.getName();
			if ((name==null && n==null)
				||(name!=null && name.equals(n))) {
				collection.add(node);
			}
		}
		return collection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public N getNodeByName(String name) {
		String n;
		for(N node : this.nodes) {
			n = node.getName();
			if ((name==null && n==null)
				||(name!=null && name.equals(n))) {
				return node;
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public N getNodeByUUID(UUID id) {
		for(N node : this.nodes) {
			if (node.getUUID().equals(id)) {
				return node;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Set<N>> getNodesByName() {
		Map<String,Set<N>> map = new TreeMap<String,Set<N>>();
		Set<N> set;
		String n;
		for(N node : this.nodes) {
			n = node.getName();
			set = map.get(n);
			if (set==null) {
				set = new TreeSet<N>();
				map.put(n, set);
			}
			set.add(node);
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getNodeNames() {
		Set<String> names = new TreeSet<String>();
		String n;
		for(N node : this.nodes) {
			n = node.getName();
			if (n!=null) {
				names.add(n);
			}
		}
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,N> getNodeByName() {
		Map<String,N> map = new TreeMap<String,N>();
		String n;
		for(N node : this.nodes) {
			n = node.getName();
			map.put(n, node);
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addNode(N node) {
		if (node!=null) {
			if (node.getGraph()!=null)
				StandardGraphException.alreadyInsideGraph();
			
			if (this.nodes.add(node)) {
				node.setGraph((G)this);
				node.addModelObjectListener(this.listener);
				fireComponentAdded(node);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeNode(N node) {
		if (node!=null) {
			if (node.getGraph()!=this)
				StandardGraphException.outsideGraph();
			
			List<E> edges = node.getEdges();
			
			if (this.nodes.remove(node)) {
				node.removeModelObjectListener(this.listener);
				node.disconnect();
				node.setGraph(null);
				fireComponentRemoved(node);
				
				for(E edge : edges) {
					removeEdge(edge);
				}
			}
		}
	}

	/** Remove the specified node with no notification
	 * to the outside of the graph.
	 * 
	 * @param node is the node to remove.
	 */
	void removeNodeSilently(N node) {
		if (node!=null) {
			if (node.getGraph()!=this)
				StandardGraphException.outsideGraph();
			
			if (this.nodes.remove(node)) {
				node.removeModelObjectListener(this.listener);
				node.setGraph(null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> getEdges() {
		return Collections.unmodifiableSet(this.edges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<String> getEdgeNames() {
		Set<String> names = new TreeSet<String>();
		String n;
		for(E edge : this.edges) {
			n = edge.getName();
			if (n!=null) {
				names.add(n);
			}
		}
		return names;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<E> getEdgesByName(String name) {
		Set<E> collection = new TreeSet<E>();
		String n;
		for(E edge : this.edges) {
			n = edge.getName();
			if ((name==null && n==null)
				||(name!=null && name.equals(n))) {
				collection.add(edge);
			}
		}
		return collection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public E getEdgeByName(String name) {
		String n;
		for(E edge : this.edges) {
			n = edge.getName();
			if ((name==null && n==null)
				||(name!=null && name.equals(n))) {
				return edge;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Set<E>> getEdgesByName() {
		Map<String,Set<E>> map = new TreeMap<String,Set<E>>();
		Set<E> set;
		String n;
		for(E edge : this.edges) {
			n = edge.getName();
			set = map.get(n);
			if (set==null) {
				set = new TreeSet<E>();
				map.put(n, set);
			}
			set.add(edge);
		}
		return map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,E> getEdgeByName() {
		Map<String,E> map = new TreeMap<String,E>();
		String n;
		for(E edge : this.edges) {
			n = edge.getName();
			map.put(n, edge);
		}
		return map;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public E getEdgeByUUID(UUID id) {
		for(E edge : this.edges) {
			if (edge.getUUID().equals(id)) {
				return edge;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addEdge(E edge) {
		if (edge!=null) {
			if (edge.getGraph()!=null)
				StandardGraphException.alreadyInsideGraph();
			
			if (this.edges.add(edge)) {
				edge.setGraph((G)this);
				edge.addModelObjectListener(this.listener);
				fireComponentAdded(edge);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeEdge(E edge) {
		if (edge!=null) {
			if (edge.getGraph()!=this)
				StandardGraphException.outsideGraph();
			
			if (this.edges.remove(edge)) {
				edge.removeModelObjectListener(this.listener);
				edge.disconnect();
				edge.setGraph(null);
				fireComponentRemoved(edge);
			}
		}
	}
	
	/** Remove the specified edge with no notification
	 * to the outside of the graph.
	 * 
	 * @param edge is the edge to remove.
	 */
	void removeEdgeSilently(E edge) {
		if (edge!=null) {
			if (edge.getGraph()!=this)
				StandardGraphException.outsideGraph();
			
			if (this.edges.remove(edge)) {
				edge.removeModelObjectListener(this.listener);
				edge.setGraph(null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int moveFromGraph(G graph) {
		int moved = 0;
		if (graph!=null) {
			List<N> addedNodes = new ArrayList<N>();
			List<E> addedEdges = new ArrayList<E>();
			for(N node : graph.getNodes()) {
				if (this.nodes.add(node)) {
					addedNodes.add(node);
					++moved;
				}
			}
			for(E edge : graph.getEdges()) {
				if (this.edges.add(edge)) {
					addedEdges.add(edge);
					++moved;
				}
			}
			graph.clear(); // Remove the nodes and the edges, but do not break the links
			for(N node : addedNodes) {
				node.setGraph((G)this);
				node.addModelObjectListener(this.listener);
				fireComponentAdded(node);
			}
			for(E edge : addedEdges) {
				edge.setGraph((G)this);
				edge.addModelObjectListener(this.listener);
				fireComponentAdded(edge);
			}
		}
		return moved;
	}

	private boolean isExtractable(E edge, Set<UUID> identifiers) {
		assert(edge!=null);
		A anchor;
		N node;
		anchor = edge.getStartAnchor();
		if (anchor==null) return false;
		node = anchor.getNode();
		if (node==null || !identifiers.contains(node.getUUID())) return false;

		anchor = edge.getEndAnchor();
		if (anchor==null) return false;
		node = anchor.getNode();
		if (node==null || !identifiers.contains(node.getUUID())) return false;
		
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public int moveFromGraph(G graph, Set<UUID> identifiersToExtract) {
		int moved = 0;
		if (graph!=null && identifiersToExtract!=null && !identifiersToExtract.isEmpty()) {
			List<N> addedNodes = new ArrayList<N>();
			List<E> addedEdges = new ArrayList<E>();
			List<E> removedEdges = new ArrayList<E>();
			
			{
				E edge;
				N node;
				for(UUID id : identifiersToExtract) {
					edge = graph.getEdgeByUUID(id);
					if (edge!=null) {
						if (isExtractable(edge, identifiersToExtract)) {
							addedEdges.add(edge);
							++moved;
						}
					}
					else {
						node = graph.getNodeByUUID(id);
						if (node!=null) {
							addedNodes.add(node);
							++moved;
							for(E e : node.getEdges()) {
								if (!isExtractable(e, identifiersToExtract)) {
									removedEdges.add(e);
								}
							}
						}
					}
				}
			}
			
			for(E rEdge : removedEdges) {
				graph.removeEdge(rEdge);
			}

			for(N node : addedNodes) {
				graph.removeNodeSilently(node);
				this.nodes.add(node);
				node.setGraph((G)this);
				node.addModelObjectListener(this.listener);
				fireComponentAdded(node);
			}
			
			for(E edge : addedEdges) {
				graph.removeEdgeSilently(edge);
				this.edges.add(edge);
				edge.setGraph((G)this);
				edge.addModelObjectListener(this.listener);
				fireComponentAdded(edge);
			}
		}
		return moved;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear() {
		Iterator<N> nodeIterator = this.nodes.iterator();
		N node;
		while (nodeIterator.hasNext()) {
			node = nodeIterator.next();
			nodeIterator.remove();
			node.removeModelObjectListener(this.listener);
			node.setGraph(null);
			fireComponentRemoved(node);
		}
		Iterator<E> edgeIterator = this.edges.iterator();
		E edge;
		while (edgeIterator.hasNext()) {
			edge = edgeIterator.next();
			edgeIterator.remove();
			edge.removeModelObjectListener(this.listener);
			edge.setGraph(null);
			fireComponentRemoved(edge);
		}
	}
	
	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Listener implements ModelObjectListener {

		/**
		 */
		public Listener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelPropertyChanged(ModelObjectEvent event) {
			fireContentChanged(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelContainerChanged(ModelObjectEvent event) {
			fireContentChanged(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelLinkChanged(ModelObjectEvent event) {
			fireContentChanged(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelContentChanged(ModelObjectEvent event) {
			fireContentChanged(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelComponentAdded(ModelObjectEvent event) {
			fireContentChanged(event);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelComponentRemoved(ModelObjectEvent event) {
			fireContentChanged(event);
		}
	
	}
	
}
