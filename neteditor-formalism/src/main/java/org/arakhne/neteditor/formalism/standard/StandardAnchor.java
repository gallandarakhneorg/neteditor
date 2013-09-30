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

import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.arakhne.afc.references.WeakArrayList;
import org.arakhne.neteditor.formalism.AbstractAnchor;
import org.arakhne.neteditor.formalism.AnchorLocation;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.ModelObjectEvent;

/** Define an anchor with the standard implementation.
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
public class StandardAnchor<G extends StandardGraph<G,N,A,E>, N extends AbstractStandardNode<G,N,A,E>, A extends StandardAnchor<G,N,A,E>, E extends StandardEdge<G,N,A,E>> extends AbstractAnchor<G,N,A,E> {

	private static final long serialVersionUID = 3257058620685298161L;

	private final List<E> edges = new WeakArrayList<E>();
	
	private boolean forwardEventsToNode = true;

	/**
	 */
	public StandardAnchor() {
		//
	}

	/** Replies if this anchor is forwarding the events to its node.
	 * 
	 * @return if the events are forwarded; otherwise <code>false</code>. 
	 */
	boolean isForwardEventsToNode() {
		return this.forwardEventsToNode;
	}
	
	/** Set if this anchor is forwarding the events to its node.
	 * 
	 * @param forward is <code>true</code> if the events are forwarded; otherwise <code>false</code>. 
	 */
	void setForwardEventsToNode(boolean forward) {
		this.forwardEventsToNode = forward;
	}

	/** Replies the preferred location for this anchor.
	 * The subclasses may override this function.
	 * By default it replies, {@link AnchorLocation#CENTER}.
	 * 
	 * @return the preferred position of this anchor.
	 */
	@SuppressWarnings("static-method")
	public AnchorLocation getPreferredLocation() {
		return AnchorLocation.CENTER;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObjectEvent fireContainerChanged(ModelObject oldContainer,
			ModelObject newContainer) {
		ModelObjectEvent event = super.fireContainerChanged(oldContainer, newContainer);
		N node = getNode();
		if (node!=null) node.fireContentChanged(event);
		return event;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObjectEvent fireLinkChanged(ModelObject oldLinkedObject,
			ModelObject newLinkedObject) {
		ModelObjectEvent event = super.fireLinkChanged(oldLinkedObject, newLinkedObject);
		N node = getNode();
		if (node!=null) node.fireContentChanged(event);
		return event;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObjectEvent firePropertyChanged(String propertyName,
			Object oldValue, Object newValue) {
		ModelObjectEvent event = super.firePropertyChanged(propertyName, oldValue, newValue);
		N node = getNode();
		if (node!=null) node.fireContentChanged(event);
		return event;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObjectEvent fireComponentAdded(ModelObject component) {
		ModelObjectEvent event = super.fireComponentAdded(component);
		N node = getNode();
		if (node!=null) node.fireContentChanged(event);
		return event;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObjectEvent fireComponentRemoved(ModelObject component) {
		ModelObjectEvent event = super.fireComponentRemoved(component);
		N node = getNode();
		if (node!=null) node.fireContentChanged(event);
		return event;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObjectEvent fireContentChanged(ModelObjectEvent cause) {
		ModelObjectEvent event = super.fireContentChanged(cause);
		N node = getNode();
		if (node!=null) node.fireContentChanged(event);
		return event;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> getEdges() {
		return Collections.unmodifiableList(this.edges);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addEdge(E edge) {
		addEdge(edge, true);
	}
	
	/** Add the specified edge inside the list of the binded edges to this
	 * anchor. This function is able to test or ignore the
	 * connection constrains applied by {@link #canConnectAsEndAnchor(org.arakhne.neteditor.formalism.Edge, org.arakhne.neteditor.formalism.Anchor)}
	 * and {@link #canConnectAsStartAnchor(org.arakhne.neteditor.formalism.Edge, org.arakhne.neteditor.formalism.Anchor)}.
	 * 
	 * @param edge is the edge to insert.
	 * @param testConnection indicates if the connection constraints should be checked.
	 */
	@SuppressWarnings("unchecked")
	void addEdge(E edge, boolean testConnection) {
		if (edge!=null) {
			if (getNode()==null)
				throw StandardGraphException.anchorOutsideNode();
			Graph<?,?,?,?> g = getNode().getGraph();
			if (g==null)
				throw StandardGraphException.outsideGraph();
			if (g!=edge.getGraph())
				throw StandardGraphException.noSameGraph();
			
			A anchor = edge.getStartAnchor();
			if (anchor==null && (!testConnection
				|| canConnectAsStartAnchor(edge, (A)this))) {
				if (this.edges.add(edge)) {
					edge.setStartAnchor((A)this, !testConnection);
					fireLinkChanged(null, edge);
					return;
				}
			}
			if (anchor!=this) {
				anchor = edge.getEndAnchor();
				if (anchor==null) {
					if (!testConnection
							|| canConnectAsEndAnchor(edge, (A)this)) {
						if (this.edges.add(edge)) {
							edge.setEndAnchor((A)this, !testConnection);
							fireLinkChanged(null, edge);
							return;
						}
					}
				}
				else if (anchor!=this) {
					throw StandardGraphException.edgeAlreadyLinked();
				}
				else {
					this.edges.add(edge);
				}
			}
			else {
				this.edges.add(edge);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeEdge(E edge) {
		if (edge!=null) {
			if (getNode()==null)
				throw StandardGraphException.anchorOutsideNode();
			G g = getNode().getGraph();
			if (g==null)
				throw StandardGraphException.outsideGraph();
			if (g!=edge.getGraph())
				throw StandardGraphException.noSameGraph();

			if (this.edges.remove(edge)) {
				if (edge.getStartAnchor()==this) {
					edge.setStartAnchor(null);
				}
				else if (edge.getEndAnchor()==this) {
					edge.setEndAnchor(null);
				}
				fireLinkChanged(edge, null);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllEdges() {
		if (getNode()==null)
			throw StandardGraphException.anchorOutsideNode();
		Graph<?,?,?,?> g = getNode().getGraph();
		if (g==null)
			throw StandardGraphException.outsideGraph();

		if (!this.edges.isEmpty()) {
			Iterator<E> iterator = this.edges.iterator();
			E edge;
			while (iterator.hasNext()) {
				edge = iterator.next();
				iterator.remove();
				if (g!=edge.getGraph())
					throw StandardGraphException.noSameGraph();

				if (edge.getStartAnchor()==this) {
					edge.setStartAnchor(null);
				}
				else if (edge.getEndAnchor()==this) {
					edge.setEndAnchor(null);
				}
				fireLinkChanged(edge, null);
			}
		}
	}
	
}
