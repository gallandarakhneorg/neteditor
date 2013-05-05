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

import org.arakhne.afc.math.MathConstants;
import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.AnchorLocation;

/** This figure is for all the anchor figures based on ellipsoid.
 *
 * @param <A> is the type of the model anchor associated to this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEllipsoidAnchorFigure<A extends Anchor<?,?,?,?>> extends AnchorFigure<A> {

	private static final long serialVersionUID = 2190019498344342803L;

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
	public AbstractEllipsoidAnchorFigure(UUID viewUUID, float x, float y, float width, float height) { 
		super(viewUUID, x, y, width, height) ;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Shape2f getClip(Rectangle2f figureBounds) {
		return new Ellipse2f(
				figureBounds.getMinX(),
				figureBounds.getMinY(),
				figureBounds.getWidth(),
				figureBounds.getHeight());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(float x, float y) {
		return MathUtil.isPointInEllipse(
				x, y,
				getX(), getY(),
				getWidth(), getHeight());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(Rectangle2f r) {
		return Ellipse2f.containsEllipseRectangle(
				getX(), getY(),
				getWidth(), getHeight(),
				r.getMinX(), r.getMinY(),
				r.getWidth(), r.getHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hit(float x, float y, float epsilon) {
		float absx = getRelativeX();
		float absy = getRelativeY();
		Ellipse2f ellipse = new Ellipse2f(
				absx - epsilon, absy - epsilon,
				getWidth() + epsilon, getHeight() + epsilon) ;
		return ( ellipse.contains(x, y) );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point2D getConnectionPointFrom(Point2D position, Point2D absoluteAnchorPosition, AnchorLocation location) {
		float x = position.getX();
		float y = position.getY();
		float ax = absoluteAnchorPosition.getX();
		float ay = absoluteAnchorPosition.getY();
		float w = getWidth();
		float h = getHeight();
		float dw = w/2f;
		float dh = h/2f;
		float cx = 	ax + dw;
		float cy = ay + dh;
		
		float angle = MathUtil.angleOfVector(cx, cy, x, y);
		// angle in [-PI;PI]

		switch(location) {
		case NORTH_WEST:
			angle = MathUtil.clampAngle(angle, MathConstants.PI/4f, 5f*MathConstants.PI/4f);
			break;
		case WEST:
			angle = MathUtil.clampAngle(angle, MathConstants.PI/2f, 3f*MathConstants.PI/2f);
			break;
		case SOUTH_WEST:
			angle = MathUtil.clampAngle(angle, 5f*MathConstants.PI/4f, 7f*MathConstants.PI/4f);
			break;
		case NORTH:
			angle = MathUtil.clampAngle(angle, 0f, MathConstants.PI);
			break;
		case CENTER:
			break;
		case SOUTH:
			angle = MathUtil.clampAngle(angle, MathConstants.PI, 2f*MathConstants.PI);
			break;
		case NORTH_EAST:
			angle = MathUtil.clampAngle(angle, -MathConstants.PI/4f, 3f*MathConstants.PI/4f);
			break;
		case EAST:
			angle = MathUtil.clampAngle(angle, -MathConstants.PI/2f, MathConstants.PI/2f);
			break;
		case SOUTH_EAST:
			angle = MathUtil.clampAngle(angle, -3f*MathConstants.PI/4f, MathConstants.PI/4f);
			break;
		default:
			throw new IllegalStateException();
		}

		// x = u + a cos(t)
		// y = v + b sin(t)
		// where (u;v) is the center of the ellipse
		//       a is the bigger radius
		//       b is the smaller radius.
		// you may exchange a and b in the queations.

		dw *= (float)Math.cos(angle);
		dh *= (float)Math.sin(angle);
		
		float px = cx + dw;
		float py = cy + dh;

		return new Point2f(px, py);
	}

}
