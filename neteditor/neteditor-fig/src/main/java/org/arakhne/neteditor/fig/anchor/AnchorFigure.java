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
package org.arakhne.neteditor.fig.anchor;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.subfigure.AbstractModelObjectSubFigure;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.AnchorLocation;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.Node;

/** This is the anchor that permit to connect {@link EdgeFigure}
 *  and {@link NodeFigure}.
 *  <p>
 *  Caution: The position of Anchor are relative to a Node.
 *
 * @param <A> is the type of the model anchor associated to this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AnchorFigure<A extends Anchor<?,?,?,?>>
extends AbstractModelObjectSubFigure<A> {

	private static final long serialVersionUID = -3451655934118279974L;

	/** Construct a new Anchor.
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
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 * @param width horizontal dimension of this Anchor within his {@link NodeFigure}.
	 * @param height vertical dimension of this Anchor within his {@link NodeFigure}.
	 */
	public AnchorFigure(UUID viewUUID, float x, float y, float width, float height) { 
		super(viewUUID, x, y, width, height) ;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateAssociatedGeometry() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Figure getParent() {
		A model = getModelObject();
		if (model!=null) {
			Node<?,?,?,?> parentNode = model.getNode();
			if (parentNode!=null) {
				return parentNode.getViewBinding().getView(getViewUUID(), Figure.class);
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateFromModel(ModelObjectEvent event) {
		//
	}

	/** Replies the absolute x position.
	 * <p>
	 * {@code absX = parentNode.x + this.x + delta}
	 * where {@delta} is a decreasing value that depends
	 * on the position of the anchor in the node.
	 * 
	 * @return the absolute x position.
	 */
	@Override
	public float getAbsoluteX() {
		Figure parent = getParent();
		float x = getRelativeX(parent);
		if (parent!=null) {
			x += parent.getX();
		}
		return x;
	}

	/** Replies the absolute x position.
	 * <p>
	 * {@code absY = parentNode.y + this.y + delta}
	 * where {@delta} is a decreasing value that depends
	 * on the position of the anchor in the node.
	 * 
	 * @return the absolute y position.
	 */
	@Override
	public float getAbsoluteY() {
		Figure parent = getParent();
		float y = getRelativeY(parent);
		if (parent!=null) {
			y += parent.getY();
		}
		return y;
	}

	private float getRelativeX(Figure parent) {
		float x = getY();
		if (parent!=null) {
			A anchor = getModelObject();
			if (anchor!=null) {
				AnchorLocation location = anchor.getLocation();
				Point2D anchorOrigin = getRelativeAnchorOriginPoint(parent, location);
				x += anchorOrigin.getX();
				switch (location) {
				case NORTH_WEST:
				case NORTH     : 
				case NORTH_EAST: break;
				case WEST      :
				case CENTER    :
				case EAST      : x -= getWidth() / 2f; break;
				case SOUTH_WEST:
				case SOUTH     : 
				case SOUTH_EAST: x -= getWidth(); break;
				default:
					throw new IllegalStateException();
				}
			}
		}
		return x;
	}

	/** Replies the x position relative to the parent node.
	 * <p>
	 * {@code relX = this.x + anchorLocation.origin.x + delta}
	 * where {@delta} is a decreasing value that depends
	 * on the position of the anchor in the node.
	 * <p>
	 * This function differs from {@link #getX}
	 * by the addition of the {@delta} value.
	 * 
	 * @return the relative x position.
	 */
	public float getRelativeX() {
		return getRelativeX(getParent());
	}

	private float getRelativeY(Figure parent) {
		float y = getY();
		if (parent!=null) {
			A anchor = getModelObject();
			if (anchor!=null) {
				AnchorLocation location = anchor.getLocation();
				Point2D anchorOrigin = getRelativeAnchorOriginPoint(parent, location);
				y += anchorOrigin.getY();
				switch (location) {
				case NORTH_WEST:
				case NORTH     : 
				case NORTH_EAST: break;
				case WEST      :
				case CENTER    :
				case EAST      : y -= getHeight() / 2f; break;
				case SOUTH_WEST:
				case SOUTH     : 
				case SOUTH_EAST: y -= getHeight(); break;
				default:
					throw new IllegalStateException();
				}
			}
		}
		return y;
	}

	/** Replies the y position relative to the parent node.
	 * <p>
	 * {@code relY = this.y + anchorLocation.origin.y + delta}
	 * where {@delta} is a decreasing value that depends
	 * on the position of the anchor in the node.
	 * <p>
	 * This function differs from {@link #getY}
	 * by the addition of the {@delta} value.
	 * 
	 * @return the relative y position.
	 */
	public float getRelativeY() {
		return getRelativeY(getParent());
	}

	/** Return the origin point in the given node from the given anchor location.
	 * <p>
	 * This method always create a new instanceof Point2D.
	 *
	 *  @param node is the origin node.
	 *  @param location the location of the anchor.
	 *  @return the origin point
	 */        
	public static Point2D getRelativeAnchorOriginPoint(Figure node, AnchorLocation location) {
		float x = 0;
		float y = 0;
		switch(location) {
		case NORTH_WEST:
			break;
		case WEST:
			y += node.getHeight()/2f;
			break;
		case SOUTH_WEST:
			y += node.getHeight();
			break;
		case NORTH:
			x += node.getWidth()/2f;
			break; 
		case CENTER:
			x += node.getWidth()/2f;
			y += node.getHeight()/2f;
			break; 
		case SOUTH:
			x += node.getWidth()/2f;
			y += node.getHeight();
			break; 
		case NORTH_EAST:
			x += node.getWidth();
			break; 
		case EAST:
			x += node.getWidth();
			y += node.getHeight()/2f;
			break; 
		case SOUTH_EAST:
			x += node.getWidth();
			y += node.getHeight();
			break; 
		default:
			throw new IllegalStateException();
		}
		// return the position;
		return new Point2f(x,y);
	}

	/** Replies the best connection point from the specified
	 * point to this anchor figure.
	 * 
	 * @param position is the position from which the connection
	 * may be computed.
	 * @return the connection point, never <code>null</code>.
	 */
	public final Point2D getConnectionPointFrom(Point2D position) {
		Point2D abs = new Point2f(getAbsoluteX(), getAbsoluteY());
		return getConnectionPointFrom(position, abs);
	}

	/** Replies the best connection point from the specified
	 * point to this anchor figure.
	 * 
	 * @param position is the position from which the connection
	 * may be computed.
	 * @param absoluteAnchorPosition is the absolute position to consider for this anchor
	 * (it may be different than the real absolute position).
	 * @return the connection point, never <code>null</code>.
	 */
	public final Point2D getConnectionPointFrom(Point2D position, Point2D absoluteAnchorPosition) {
		A anchor = getModelObject();
		if (anchor!=null) {
			return getConnectionPointFrom(position,absoluteAnchorPosition, anchor.getLocation());
		}
		return new Point2f(
				absoluteAnchorPosition.getX() + getWidth() / 2f,
				absoluteAnchorPosition.getY() + getHeight() / 2f);
	}

	/** Replies the best connection point from the specified
	 * point to this anchor figure.
	 * 
	 * @param position is the position from which the connection
	 * may be computed.
	 * @param absoluteAnchorPosition is the absolute position to consider for this anchor
	 * (it may be different than the real absolute position).
	 * @param location is the location of the anchor on the node.
	 * @return the connection point, never <code>null</code>.
	 */
	protected abstract Point2D getConnectionPointFrom(Point2D position, Point2D absoluteAnchorPosition, AnchorLocation location);
	
}
