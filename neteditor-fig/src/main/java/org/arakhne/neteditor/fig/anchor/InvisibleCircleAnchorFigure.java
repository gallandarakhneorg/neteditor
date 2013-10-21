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
package org.arakhne.neteditor.fig.anchor;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;

/** This figure is for an anchor that is not drawn in its
 * inactive state, and the activated side of the anchor
 * is drawn along a circle frame.
 *
 * @param <A> is the type of the model anchor associated to this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class InvisibleCircleAnchorFigure<A extends Anchor<?,?,?,?>>
extends AbstractEllipsoidAnchorFigure<A> {

	private static final long serialVersionUID = 5458365251237864659L;

	/** Construct a new Anchor.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified 2*radius and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 * @param radius is the radius of the circle.
	 */
	public InvisibleCircleAnchorFigure(UUID viewUUID, float x, float y, float radius) { 
		super(viewUUID, x, y, radius*2f, radius*2f) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 */
	public InvisibleCircleAnchorFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE/2f) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public InvisibleCircleAnchorFigure(UUID viewUUID) {
		this(viewUUID, 0, 0, DEFAULT_MINIMAL_SIZE/2f) ;
	}

	/** Replies the radius of this circle.
	 * 
	 * @return the radius.
	 */
	public float getRadius() {
		return this.getWidth()/2f;
	}
	
	/** Replies the radius of this circle.
	 * 
	 * @param radius is the new radius.
	 */
	public void setRadius(float radius) {
		setSize(radius*2f, radius*2f);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(float x, float y) {
		float cx = getX() + getWidth()/2f;
		float cy = getY() + getHeight()/2f;
		float radius = getRadius();
		cx -= x;
		cy -= y;
		return cx*cx+cy*cy <= radius*radius;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Rectangle2f r) {
		float cx = getX() + getWidth()/2f;
		float cy = getY() + getHeight()/2f;
		float radius = getRadius();
		return Circle2f.containsCircleRectangle(
				cx, cy, radius,
				r.getMinX(), r.getMinY(),
				r.getWidth(), r.getHeight());
	}

	@Override
	public boolean intersects(Shape2f r) {
		float radius = getWidth()/2f;
		Circle2f circle = new Circle2f(getX() + radius, getY() + radius, radius);
		return r.intersects(circle);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void paint(ViewGraphics2D g) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinimalDimension(float width, float height) {
		float s = Math.min(width, height);
		super.setMinimalDimension(s, s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaximalDimension(float width, float height) {
		float s = Math.max(width, height);
		super.setMaximalDimension(s, s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		super.setHeight(width);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		super.setWidth(height);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBounds(float x, float y, float width, float height) {
		float s = Math.max(width, height);
		super.setBounds(x, y, s, s);
	}
	
}
