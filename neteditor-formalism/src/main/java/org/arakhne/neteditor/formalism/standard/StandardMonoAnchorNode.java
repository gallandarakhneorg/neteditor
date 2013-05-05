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
import java.util.List;

import org.arakhne.neteditor.formalism.AnchorLocation;

/** Define an node with at max one central anchor
 * with the standard implementation.
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
public class StandardMonoAnchorNode<G extends StandardGraph<G,N,A,E>, N extends AbstractStandardNode<G,N,A,E>, A extends StandardAnchor<G,N,A,E>, E extends StandardEdge<G,N,A,E>> extends AbstractStandardNode<G,N,A,E> {

	private static final long serialVersionUID = -70828724020368342L;

	private A anchor;

	/**
	 * @param anchor
	 */
	@SuppressWarnings("unchecked")
	public StandardMonoAnchorNode(A anchor) {
		assert(anchor!=null);
		this.anchor = anchor;
		this.anchor.setNode((N)this);
		this.anchor.setLocation(AnchorLocation.CENTER);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<A> getAnchors() {
		return Collections.singletonList(this.anchor);
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public final void addAnchor(AnchorLocation location, A anchor) {
		if (anchor!=null && this.anchor!=anchor) {
			if (getGraph()==null)
				throw StandardGraphException.outsideGraph();
			if (location==null)
				throw StandardGraphException.noLocationSpecified();
			if (anchor.getNode()!=null)
				throw StandardGraphException.alreadyInsideNode();
			
			if (this.anchor!=null) {
				boolean old = this.anchor.isForwardEventsToNode();
				try {
					this.anchor.setForwardEventsToNode(false);
					this.anchor.removeAllEdges();
					this.anchor.setNode(null);
					this.anchor.setLocation(null);
				}
				finally {
					this.anchor.setForwardEventsToNode(old);
				}
				fireComponentRemoved(this.anchor);
			}
			
			this.anchor = anchor;
			
			anchor.setNode((N)this);
			anchor.setLocation(location);
			fireComponentAdded(anchor);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeAnchor(A anchor) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void removeAllAnchors() {
		//
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
		old = this.anchor.isForwardEventsToNode();
		try {
			this.anchor.setForwardEventsToNode(false);
			this.anchor.removeAllEdges();
		}
		finally {
			this.anchor.setForwardEventsToNode(old);
		}
		fireContentChanged(null);
	}
	
}
