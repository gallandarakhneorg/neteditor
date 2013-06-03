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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.AnchorLocation;

/** This figure is for all the rectangular or squared
 * anchor figures.
 *
 * @param <A> is the type of the model anchor associated to this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractRectangularAnchorFigure<A extends Anchor<?,?,?,?>> extends AnchorFigure<A> {

	private static final long serialVersionUID = 7542505123525257546L;

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
	public AbstractRectangularAnchorFigure(UUID viewUUID, float x, float y, float width, float height) { 
		super(viewUUID, x, y, width, height) ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public boolean hit(float x, float y, float epsilon) {
		float ax = getRelativeX();
		float ay = getRelativeX();
		return (x>=(ax-epsilon) && x<=(ax+getWidth()+epsilon))
				&&
				(y>=(ay-epsilon) && y<=(ay+getHeight()+epsilon));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point2D getConnectionPointFrom(Point2D position, Point2D absoluteAnchorPosition, AnchorLocation location) {
		assert(location!=null);
		assert(position!=null);
		
		float x = position.getX();
		float y = position.getY();
		
		float ax1 = absoluteAnchorPosition.getX();
		float ay1 = absoluteAnchorPosition.getY();
		float ax2 = ax1 + getWidth();
		float ay2 = ay1 + getHeight();
		
		Segment sightSegment = new Segment(
				ax1 + getWidth() / 2f, ay1 + getHeight() / 2f,
				x, y);
		
		List<Segment> segments = new ArrayList<Segment>(4);
		
		switch(location) {
		case NORTH_WEST:
			segments.add(new Segment(ax1, ay1, ax1, ay2));
			segments.add(new Segment(ax1, ay1, ax2, ay1));
			break;
		case WEST:
			segments.add(new Segment(ax1, ay1, ax1, ay2));
			break;
		case SOUTH_WEST:
			segments.add(new Segment(ax1, ay1, ax1, ay2));
			segments.add(new Segment(ax1, ay2, ax2, ay2));
			break;
		case NORTH:
			segments.add(new Segment(ax1, ay1, ax2, ay1));
			break;
		case CENTER:
			segments.add(new Segment(ax1, ay1, ax1, ay2));
			segments.add(new Segment(ax1, ay2, ax2, ay2));
			segments.add(new Segment(ax2, ay2, ax2, ay1));
			segments.add(new Segment(ax2, ay1, ax1, ay1));
			break;
		case SOUTH:
			segments.add(new Segment(ax1, ay2, ax2, ay2));
			break;
		case NORTH_EAST:
			segments.add(new Segment(ax1, ay1, ax2, ay1));
			segments.add(new Segment(ax2, ay1, ax2, ay2));
			break;
		case EAST:
			segments.add(new Segment(ax2, ay1, ax2, ay2));
			break;
		case SOUTH_EAST:
			segments.add(new Segment(ax2, ay1, ax2, ay2));
			segments.add(new Segment(ax2, ay2, ax1, ay2));
			break;
		default:
			throw new IllegalStateException();
		}
		
		Point2D i = null;
		Iterator<Segment> iterator = segments.iterator();
		Segment s;
		while (i==null && iterator.hasNext()) {
			s = iterator.next();
			i = s.getIntersection(sightSegment);
		}
		
		if (i==null) {
			switch(location) {
			case NORTH_WEST:
				if (x<=ax1) {
					x = ax1;
					y = MathUtil.clamp(y, ay1, ay2);
				}
				else {
					x = MathUtil.clamp(x, ax1, ax2);
					y = ay1;
				}
				break;
			case WEST:
				x = ax1;
				y = MathUtil.clamp(y, ay1, ay2);
				break;
			case SOUTH_WEST:
				if (x<=ax1) {
					x = ax1;
					y = MathUtil.clamp(y, ay1, ay2);
				}
				else {
					x = MathUtil.clamp(x, ax1, ax2);
					y = ay2;
				}
				break;
			case NORTH:
				x = MathUtil.clamp(x, ax1, ax2);
				y = ay1;
				break;
			case CENTER:
				x = MathUtil.clamp(x, ax1, ax2);
				y = MathUtil.clamp(y, ay1, ay2);
				break;
			case SOUTH:
				x = MathUtil.clamp(x, ax1, ax2);
				y = ay2;
				break;
			case NORTH_EAST:
				if (x>=ax2) {
					x = ax2;
					y = MathUtil.clamp(y, ay1, ay2);
				}
				else {
					x = MathUtil.clamp(x, ax1, ax2);
					y = ay1;
				}
				break;
			case EAST:
				x = ax2;
				y = MathUtil.clamp(y, ay1, ay2);
				break;
			case SOUTH_EAST:
				if (x>=ax2) {
					x = ax2;
					y = MathUtil.clamp(y, ay1, ay2);
				}
				else {
					x = MathUtil.clamp(x, ax1, ax2);
					y = ay2;
				}
				break;
			default:
				throw new IllegalStateException();
			}
			
			return new Point2f(x,y);
		}
		
		return i;
	}
	
	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Segment {
		
		public final float x1;
		public final float y1;
		public final float x2;
		public final float y2;
		
		/**
		 * @param x1
		 * @param y1
		 * @param x2
		 * @param y2
		 */
		public Segment(float x1, float y1, float x2, float y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
		
		/**
		 * Compute the intersection between this segment and the specified one.
		 * 
		 * @param s
		 * @return the intersection point or <code>null</code> if none.
		 */
		public Point2D getIntersection(Segment s) {
			return MathUtil.getSegmentSegmentIntersectionPoint(
					this.x1, this.y1, this.x2, this.y2,
					s.x1, s.y1, s.x2, s.y2);
		}
		
	}
	
}
