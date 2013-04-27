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

import java.util.Iterator;

import org.arakhne.neteditor.formalism.AbstractNode;

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
public abstract class AbstractStandardNode<G extends StandardGraph<G,N,A,E>, N extends AbstractStandardNode<G,N,A,E>, A extends StandardAnchor<G,N,A,E>, E extends StandardEdge<G,N,A,E>> extends AbstractNode<G,N,A,E> {

	private static final long serialVersionUID = 7103859468025562617L;

	/**
	 */
	public AbstractStandardNode() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void addAnchor(A anchor) {
		if (anchor!=null) {
			addAnchor(anchor.getPreferredLocation(), anchor);
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
		for(A anchor : getAnchors()) {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasIncomingEdges() {
		for(A anchor : getAnchors()) {
			for(E edge : anchor.getEdges()) {
				if (edge.getEndAnchor()==anchor) return true;
			}
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasOutgoingEdges() {
		for(A anchor : getAnchors()) {
			for(E edge : anchor.getEdges()) {
				if (edge.getStartAnchor()==anchor) return true;
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<E> getIncomingEdges() {
		return new InOutEdgeIterable(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterable<E> getOutgoingEdges() {
		return new InOutEdgeIterable(false);
	}
	
	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private class InOutEdgeIterable implements Iterable<E> {
		
		private final boolean incoming;
		
		/**
		 * @param in
		 */
		public InOutEdgeIterable(boolean in) {
			this.incoming = in;
		}
		
		/**
		 *  {@inheritDoc}
		 */
		@Override
		public Iterator<E> iterator() {
			if (this.incoming)
				return new IncomingEdgeIterator();
			return new OutgoingEdgeIterator();
		}
		
	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private abstract class AbstractInOutEdgeIterator implements Iterator<E> {
		
		private final Iterator<A> anchorIterator;
		private Iterator<E> edgeIterator;
		private A anchor = null;
		private E next = null;
		private boolean searched = false;
		
		/**
		 */
		public AbstractInOutEdgeIterator() {
			this.anchorIterator = getAnchors().iterator();
		}
		
		private void searchNext() {
			if (!this.searched) {
				this.searched = true;
				this.next = null;
				do {
					while (this.anchorIterator.hasNext() &&
						(this.edgeIterator==null || !this.edgeIterator.hasNext())) {
						this.anchor = this.anchorIterator.next();
						this.edgeIterator = this.anchor.getEdges().iterator();
					}
					if (this.edgeIterator!=null) {
						E edge;
						while (this.edgeIterator.hasNext()) {
							edge = this.edgeIterator.next();
							if (isEdge(this.anchor, edge)) {
								this.next = edge;
								return ;
							}
						}
					}
				}
				while (this.anchorIterator.hasNext());
			}
		}
		
		/** Test if the edge is valid against the given anchor.
		 * 
		 * @param anchor
		 * @param edge
		 * @return <code>true</code> if the given edge is connected
		 *  to this node by the given anchor; otherwise <code>false</code>.
		 */
		protected abstract boolean isEdge(A anchor, E edge);

		@Override
		public boolean hasNext() {
			searchNext();
			return this.next!=null;
		}

		@Override
		public E next() {
			searchNext();
			E n = this.next;
			this.next = null;
			this.searched = false;
			return n;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private class IncomingEdgeIterator extends AbstractInOutEdgeIterator {
		
		/**
		 */
		public IncomingEdgeIterator() {
			//
		}

		@Override
		protected boolean isEdge(A anchor, E edge) {
			return edge.getEndAnchor()==anchor;
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private class OutgoingEdgeIterator extends AbstractInOutEdgeIterator {
		
		/**
		 */
		public OutgoingEdgeIterator() {
			//
		}

		@Override
		protected boolean isEdge(A anchor, E edge) {
			return edge.getStartAnchor()==anchor;
		}

	}

}
