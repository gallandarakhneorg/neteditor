/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.fig.figure.node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.neteditor.fig.anchor.AnchorFigure;
import org.arakhne.neteditor.fig.figure.AbstractModelObjectFigure;
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeShadowPainter;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.Node;

/** Abstract class to present a node in a diagram.
 *
 * @param <N> is the type of the model node supported by this figure.
 * @param <A> is the type of the model anchor supported by this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class NodeFigure<N extends Node<?,? super N,? super A,?>,A extends Anchor<?,? super N,? super A,?>> extends AbstractModelObjectFigure<N> implements BlockFigure {

	private static final long serialVersionUID = -3748725681196675586L;

	/** Construct a new AbstractNodeFigure.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 * @param width is the width of the node.
	 * @param height is the height of the node.
	 */
	public NodeFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateAssociatedGeometry() {
		//
	}

	/** Return the list of anchor figure attached to the owner.
	 *
	 * @return list of anchor figures
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected List<? extends AnchorFigure<A>> getAnchorFigures() {
		List<AnchorFigure<A>> figures = new ArrayList<AnchorFigure<A>>();
		N modelObject = getModelObject();
		if (modelObject!=null) {
			for(Anchor<?,?,?,?> anchor : modelObject.getAnchors()) {
				AnchorFigure f = anchor.getViewBinding().getView(getViewUUID(), AnchorFigure.class);
				if (f!=null) figures.add(f);
			}
		}
		return figures;
	}

	/** {@inheritDoc} 
	 */
	@Override
	public Iterable<? extends SubFigure> getSubFigures() {
		return getAnchorFigures();
	}

	/** Return the list of edge figures attached to the owner.
	 *
	 * @return list of edge figures
	 */
	protected Iterable<? extends EdgeFigure<?>> getEdgeFigures() {
		return new EdgeFigureIterable();
	}

	/** Return the list of edges attached to the owner.
	 *
	 * @return list of edges
	 */
	protected Iterable<? extends Edge<?,?,?,?>> getEdges() {
		N modelObject = getModelObject();
		if (modelObject==null)
			return Collections.emptyList();
		return modelObject.getEdges();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(float x, float y) {
		if (super.contains(x, y)) return true;

		for(AnchorFigure<A> anchor : getAnchorFigures()) {
			if (anchor.contains(x, y)) {
				return true;
			}
		}

		return false;
	}

	/** Test if one of the anchors is hit by the given shape.
	 * 
	 * @param shape
	 * @return the anchor in intersection with the shape; or
	 * <code>null</code> if no anchor.
	 */
	public AnchorFigure<A> getAnchorOn(Shape2f shape) {
		// Translate the shape because the anchors coordinates
		// are relative to the node
		Shape2f trans = shape.clone();
		trans.translate(-getX(), -getY());
		for(AnchorFigure<A> anchor : getAnchorFigures()) {
			if (anchor.intersects(trans)) {
				return anchor;
			}
		}
		return null;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void paint(ViewGraphics2D g) {
		g.beginGroup();
		paintNode(g);
		paintAnchors(g);
		g.endGroup();
	}

	/** Paint the node content.
	 * 
	 * @param g the graphic context.
	 */
	protected abstract void paintNode(ViewGraphics2D g);

	/** Method to paint this
	 *  the ports associated to this node.
	 *
	 * @param g the graphic context.
	 */
	protected void paintAnchors(ViewGraphics2D g) {
		if (g.getLOD()!=Graphics2DLOD.SHADOW) {
			Transform2D trans = g.getTransform();
			Rectangle2f bounds = g.getCurrentViewComponentBounds();
			trans.translate(bounds.getMinX(), bounds.getMinY());
			g.pushRenderingContext(this, trans);
			for(AnchorFigure<A> figure : getAnchorFigures()) {
				figure.paint(g) ;
			}
			g.popRenderingContext();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateFromModel(ModelObjectEvent event) {
		if (event==null) {
			// The model object associated to this node is a new one
			// Try to refresh the anchors
			refreshAnchors();
			// Try to refresh the connections
			refreshConnections();
		}
		else {
			switch(event.getType()) {
			case COMPONENT_ADDITION:
				if (event.getSource()==getModelObject()
				&& event.getAddedObject() instanceof Anchor<?,?,?,?>) {
					createFigureFor(event.getSource());
				}
				break;
			case COMPONENT_REMOVE:
				if (event.getSource()==getModelObject()
				&& event.getRemovedObject() instanceof Anchor<?,?,?,?>) {
					SubFigure subfigure = event.getSource().getViewBinding().getView(getViewUUID(), SubFigure.class);
					if (subfigure!=null) {
						unbindFigure(subfigure);
					}
				}
				break;
			default:
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPositionUpdated(float oldX, float newX, float oldY, float newY) {
		// Try to refresh the anchors
		refreshAnchors();
		// Try to refresh the connections
		refreshConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSizeUpdated(float oldWidth, float newWidth, float oldHeight, float newHeight) {
		// Try to refresh the anchors
		refreshAnchors();
		// Try to refresh the connections
		refreshConnections();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onBoundsUpdated(
			float oldX, float newX, float oldY, float newY,
			float oldWidth, float newWidth, float oldHeight, float newHeight) {
		// Try to refresh the anchors
		refreshAnchors();
		// Try to refresh the connections
		refreshConnections();
	}

	private void refreshConnections() {
		for(EdgeFigure<?> edgeFigure : getEdgeFigures()) {
			edgeFigure.refreshConnectedCtrlPoints();
		}
	}

	private void refreshAnchors() {
		Rectangle2f r;
		float w = getWidth();
		float h = getHeight();
		Rectangle2f defaultRect = new Rectangle2f(0, 0, w, h);
		for(AnchorFigure<A> anchorFigure : getAnchorFigures()) {
			r = getPreferredBoundsForAnchor(anchorFigure);
			if (r==null) r = defaultRect;
			anchorFigure.setBounds(
					r.getMinX(),
					r.getMinY(),
					r.getWidth(),
					r.getHeight());
		}
		
	}
	
	/** Replies the preferred bounds for the specified anchor.
	 * <p>
	 * By default this function returns <code>null</code>.
	 * It should be overridden by a subclass to change this behavior.
	 * 
	 * @param figure
	 * @return the bounds relative to the bounds of this node figure;
	 * if <code>null</code> the anchor will be set to fit the bounds of
	 * node figure. 
	 */
	protected Rectangle2f getPreferredBoundsForAnchor(AnchorFigure<A> figure) {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected NodeShadowPainter createShadowPainter() {
		return new SPainter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized NodeShadowPainter getShadowPainter() {
		return (NodeShadowPainter)super.getShadowPainter();
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class SPainter implements NodeShadowPainter {

		/** Bounds of the shadow node.
		 */
		protected final Rectangle2f bounds = new Rectangle2f();
		
		/** Damaged bounds associated to this shadow node.
		 */
		protected Rectangle2f damagedBounds = null;
		
		private final List<EdgeShadowPainter> edgesConnectedByStart = new ArrayList<EdgeShadowPainter>();
		private final List<EdgeShadowPainter> edgesConnectedByEnd = new ArrayList<EdgeShadowPainter>();

		/**
		 */
		public SPainter() {
			Anchor<?,?,?,?> anchor;
			N node = getModelObject();
			for(Edge<?,?,?,?> edge : getEdges()) {
				anchor = edge.getStartAnchor();
				if (anchor!=null && anchor.getNode()==node) {
					this.edgesConnectedByStart.add(
							edge.getViewBinding().getView(getViewUUID(), EdgeFigure.class)
							.getShadowPainter());
				}
				else {
					anchor = edge.getEndAnchor();
					if (anchor!=null && anchor.getNode()==node) {
						this.edgesConnectedByEnd.add(
								edge.getViewBinding().getView(getViewUUID(), EdgeFigure.class)
								.getShadowPainter());
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UUID getUUID() {
			return NodeFigure.this.getUUID();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Figure getFigure() {
			return NodeFigure.this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Rectangle2f getShadowBounds() {
			return this.bounds;
		}
		
		@Override
		public Rectangle2f getDamagedBounds() {
			if (this.damagedBounds==null) {
				this.damagedBounds = this.bounds.clone();
				Rectangle2f bb;
				for(EdgeShadowPainter painter : this.edgesConnectedByStart) {
					bb = painter.getDamagedBounds();
					if (bb!=null) {
						this.damagedBounds.setUnion(bb);
					}
				}
				for(EdgeShadowPainter painter : this.edgesConnectedByEnd) {
					bb = painter.getDamagedBounds();
					if (bb!=null) {
						this.damagedBounds.setUnion(bb);
					}
				}
			}
			return this.damagedBounds;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void moveTo(float dx, float dy) {
			if (dx!=0f || dy!=0f) {
				this.bounds.set(
						getX()+dx,
						getY()+dy,
						getWidth(),
						getHeight());
				this.damagedBounds = this.bounds.clone();
				Rectangle2f bb;
				for(EdgeShadowPainter painter : this.edgesConnectedByStart) {
					painter.moveFirstAnchorTo(dx, dy);
					bb = painter.getDamagedBounds();
					if (bb!=null) {
						this.damagedBounds.setUnion(bb);
					}
				}
				for(EdgeShadowPainter painter : this.edgesConnectedByEnd) {
					painter.moveSecondAnchorTo(dx, dy);
					bb = painter.getDamagedBounds();
					if (bb!=null) {
						this.damagedBounds.setUnion(bb);
					}
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void resize(float dx, float dy, ResizeDirection direction) {
			if (dx!=0f || dy!=0f) {
				float nx1 = getX();
				float ny1 = getY();
				float nx2 = nx1 + getWidth();
				float ny2 = ny1 + getHeight();
	
				switch(direction) {
				case NORTH_WEST:
					nx1 += dx;
					ny1 += dy;
					break;
				case WEST:
					nx1 += dx;
					break;
				case SOUTH_WEST:
					nx1 += dx;
					ny2 += dy;
					break;
				case NORTH:
					ny1 += dy;
					break;
				case SOUTH:
					ny2 += dy;
					break;
				case NORTH_EAST:
					nx2 += dx;
					ny1 += dy;
					break;
				case EAST:
					nx2 += dx;
					break;
				case SOUTH_EAST:
					nx2 += dx;
					ny2 += dy;
					break;
				default:
					throw new IllegalStateException();
				}
				if (nx1>nx2) {
					float t = nx1;
					nx1 = nx2;
					nx2 = t;
				}
				if (ny1>ny2) {
					float t = ny1;
					ny1 = ny2;
					ny2 = t;
				}
				this.bounds.setFromCorners(nx1, ny1, nx2, ny2);
				this.damagedBounds = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void paint(ViewGraphics2D g) {
			g.pushRenderingContext(NodeFigure.this, getClip(this.bounds), this.bounds);
			NodeFigure.this.paintNode(g);
			g.popRenderingContext();
			for(EdgeShadowPainter painter : this.edgesConnectedByStart) {
				painter.paint(g);
			}
			for(EdgeShadowPainter painter : this.edgesConnectedByEnd) {
				painter.paint(g);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public synchronized void release() {
			NodeFigure.this.releaseShadowPainter();
			for(EdgeShadowPainter painter : this.edgesConnectedByStart) {
				painter.release();
			}
			this.edgesConnectedByStart.clear();
			for(EdgeShadowPainter painter : this.edgesConnectedByEnd) {
				painter.release();
			}
			this.edgesConnectedByEnd.clear();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class EdgeFigureIterable implements Iterable<EdgeFigure<?>> {

		/**
		 */
		public EdgeFigureIterable() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Iterator<EdgeFigure<?>> iterator() {
			return new EdgeFigureIterator();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class EdgeFigureIterator implements Iterator<EdgeFigure<?>> {

		private final Iterator<? extends Edge<?,?,?,?>> edges;
		private EdgeFigure<?> next = null;

		/**
		 */
		public EdgeFigureIterator() {
			N modelObject = getModelObject();
			if (modelObject==null) {
				this.edges = Collections.<Edge<?,?,?,?>>emptyList().iterator();
			}
			else {
				this.edges = modelObject.getEdges().iterator();
			}
			//figures.add();
			searchNext();
		}

		private void searchNext() {
			this.next = null;
			Edge<?,?,?,?> edge;
			while (this.next==null && this.edges.hasNext()) {
				edge = this.edges.next();
				this.next = edge.getViewBinding().getView(getViewUUID(), EdgeFigure.class);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.next!=null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public EdgeFigure<?> next() {
			EdgeFigure<?> n = this.next;
			if (n==null) throw new NoSuchElementException();
			searchNext();
			return n;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}

}
