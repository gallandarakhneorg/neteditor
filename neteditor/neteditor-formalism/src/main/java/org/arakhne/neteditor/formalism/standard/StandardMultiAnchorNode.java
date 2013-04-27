/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.formalism.standard ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.arakhne.neteditor.formalism.AnchorLocation;

/** Define an node with the standard implementation.
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
public class StandardMultiAnchorNode<G extends StandardGraph<G,N,A,E>, N extends StandardMultiAnchorNode<G,N,A,E>, A extends StandardAnchor<G,N,A,E>, E extends StandardEdge<G,N,A,E>> extends AbstractStandardNode<G,N,A,E> {

	private static final long serialVersionUID = 7103859468025562617L;

	private final List<A> anchors = new ArrayList<A>();

	/**
	 */
	public StandardMultiAnchorNode() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<A> getAnchors() {
		return Collections.unmodifiableList(this.anchors);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void addAnchor(AnchorLocation location, A anchor) {
		if (anchor!=null) {
			if (getGraph()==null)
				throw StandardGraphException.outsideGraph();
			if (location==null)
				throw StandardGraphException.noLocationSpecified();
			if (anchor.getNode()!=null)
				throw StandardGraphException.alreadyInsideNode();
			
			if (this.anchors.add(anchor)) {
				anchor.setNode((N)this);
				anchor.setLocation(location);
				fireComponentAdded(anchor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAnchor(A anchor) {
		if (anchor!=null) {
			G g = getGraph();
			if (g==null)
				throw StandardGraphException.outsideGraph();
			if (anchor.getNode()!=this)
				throw StandardGraphException.anchorOutsideNode();
			
			if (this.anchors.remove(anchor)) {
				boolean old = anchor.isForwardEventsToNode();
				try {
					anchor.setForwardEventsToNode(false);
					anchor.removeAllEdges();
					anchor.setNode(null);
					anchor.setLocation(null);
				}
				finally {
					anchor.setForwardEventsToNode(old);
				}
				fireComponentRemoved(anchor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllAnchors() {
		G g = getGraph();
		if (g==null)
			throw StandardGraphException.outsideGraph();
		
		Iterator<A> iterator = this.anchors.iterator();
		A anchor;
		boolean old;
		
		while(iterator.hasNext()) {
			anchor = iterator.next();
			if (anchor.getNode()!=this)
				throw StandardGraphException.anchorOutsideNode();

			iterator.remove();
			old = anchor.isForwardEventsToNode();
			try {
				anchor.setForwardEventsToNode(false);
				anchor.removeAllEdges();
				anchor.setNode(null);
				anchor.setLocation(null);
			}
			finally {
				anchor.setForwardEventsToNode(old);
			}
			fireComponentRemoved(anchor);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void disconnect() {
		G g = getGraph();
		if (g==null)
			throw StandardGraphException.outsideGraph();
		boolean old;
		for(A anchor : this.anchors) {
			old = anchor.isForwardEventsToNode();
			try {
				anchor.setForwardEventsToNode(false);
				anchor.removeAllEdges();
			}
			finally {
				anchor.setForwardEventsToNode(old);
			}
		}
		fireContentChanged(null);
	}

}
