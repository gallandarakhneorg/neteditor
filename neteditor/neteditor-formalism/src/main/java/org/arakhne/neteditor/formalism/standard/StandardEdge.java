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

import java.lang.ref.WeakReference;

import org.arakhne.neteditor.formalism.AbstractEdge;

/** Define an edge with the standard implementation.
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
public class StandardEdge<G extends StandardGraph<G,N,A,E>, N extends AbstractStandardNode<G,N,A,E>, A extends StandardAnchor<G,N,A,E>, E extends StandardEdge<G,N,A,E>> extends AbstractEdge<G,N,A,E> {

	private static final long serialVersionUID = 6097769283622406824L;
	
	private WeakReference<A> startAnchor = null;
	private WeakReference<A> endAnchor = null;
	
	/**
	 */
	public StandardEdge() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public A getEndAnchor() {
		return this.endAnchor==null ? null : this.endAnchor.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEndAnchor(A anchor) {
		setEndAnchor(anchor, true);
	}

	/**
	 * Connect the end of this edge to this specified anchor.
	 * This function is able to test or ignore the connection
	 * constraints on the anchor.
	 * 
	 * @param anchor
	 * @param testConnection is <code>true</code> to test the
	 * connection  constrains; <code>false</code> to ignore them.
	 */
	@SuppressWarnings("unchecked")
	final void setEndAnchor(A anchor, boolean testConnection) {
		G g = getGraph();
		if (g==null)
			throw StandardGraphException.outsideGraph();

		A old = getEndAnchor();
		if (old!=anchor) {
			
			if (anchor!=null) {
				if (anchor.getNode()==null)
					throw StandardGraphException.anchorOutsideNode();
				if (g!=anchor.getNode().getGraph())
					throw StandardGraphException.noSameGraph();
			}
			
			if (!testConnection
				|| anchor==null
				|| anchor.canConnectAsEndAnchor((E)this, getStartAnchor())) {
				if (old!=null) {
					this.endAnchor = null; // to avoid to come back here when the edge was detached 
					old.removeEdge((E)this);
				}
				this.endAnchor = (anchor==null) ? null : new WeakReference<A>(anchor);
				if (anchor!=null) {
					anchor.addEdge((E)this, !testConnection);
				}
				fireLinkChanged(old, anchor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public A getStartAnchor() {
		return this.startAnchor==null ? null : this.startAnchor.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStartAnchor(A anchor) {
		setStartAnchor(anchor, true);
	}

	/**
	 * Connect the beginning of this edge to this specified anchor.
	 * This function is able to test or ignore the connection
	 * constraints on the anchor.
	 * 
	 * @param anchor
	 * @param testConnection is <code>true</code> to test the
	 * connection  constrains; <code>false</code> to ignore them.
	 */
	@SuppressWarnings("unchecked")
	final void setStartAnchor(A anchor, boolean testConnection) {
		G g = getGraph();
		if (g==null)
			throw StandardGraphException.outsideGraph();

		A old = getStartAnchor();
		if (old!=anchor) {
			
			if (anchor!=null) {
				if (anchor.getNode()==null)
					throw StandardGraphException.anchorOutsideNode();
				if (g!=anchor.getNode().getGraph())
					throw StandardGraphException.noSameGraph();
			}
			
			if (!testConnection
				|| anchor==null
				|| anchor.canConnectAsStartAnchor((E)this, getEndAnchor())) {
				if (old!=null) {
					this.startAnchor = null; // to avoid to come back here when the edge was detached 
					old.removeEdge((E)this);
				}
				this.startAnchor = (anchor==null) ? null : new WeakReference<A>(anchor);
				if (anchor!=null) {
					anchor.addEdge((E)this, !testConnection);
				}
				fireLinkChanged(old, anchor);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void disconnect() {
		G g = getGraph();
		if (g==null)
			throw StandardGraphException.outsideGraph();
		A old = getStartAnchor();
		if (old!=null) {
			this.startAnchor = null;
			old.removeEdge((E)this);
			fireLinkChanged(old, null);
		}
		old = getEndAnchor();
		if (old!=null) {
			this.endAnchor = null;
			old.removeEdge((E)this);
			fireLinkChanged(old, null);
		}
	}
	
}
