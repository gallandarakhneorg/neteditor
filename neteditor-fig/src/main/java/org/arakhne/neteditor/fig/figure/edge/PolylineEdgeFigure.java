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
package org.arakhne.neteditor.fig.figure.edge ;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.math.MathConstants;
import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.PathElement2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Vector2D;
import org.arakhne.afc.ui.vector.PathUtil;
import org.arakhne.neteditor.fig.view.DrawingMethod;
import org.arakhne.neteditor.formalism.Edge;

/** Edge that is drawn with a polyline.
 *
 * @param <E> is the type of the model edge supported by this figure.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PolylineEdgeFigure<E extends Edge<?,?,?,?>> extends EdgeFigure<E> {

	private static final long serialVersionUID = 7682602662572852032L;

	private DrawingMethod drawingMethod;
	
	/** Contruct a new SegmentEdgeFigure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param xfrom horizontal position of the begin point for this FigEdge.
	 * @param yfrom vertical position of the begin point for this FigEdge.
	 * @param xto horizontal position of the end point for this FigEdge.
	 * @param yto vertical position of the end point for this FigEdge.
	 */
	public PolylineEdgeFigure(UUID viewUUID, float xfrom, float yfrom, float xto, float yto) { 
		super(viewUUID, xfrom, yfrom, xto, yto) ;
	}

	/** Contruct a new SegmentEdgeFigure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public PolylineEdgeFigure(UUID viewUUID) { 
		this(viewUUID, 0, 0, 0, 0) ;
	}
	
	@Override
	public void fitToContent() {
		//
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getMaxAllowedCtrlPoints() {
		return Integer.MAX_VALUE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PathDetails computePath(List<? extends Point2D> controlPoints) {
		Path2f path = new Path2f();
		Vector2D startTangent = new Vector2f();
		Vector2D endTangent = new Vector2f();
		switch(getDrawingMethod()) {
		case SEGMENTS:
			PathUtil.createSegments(path, startTangent, endTangent, controlPoints);
			break;
		case BEZIER_SPLINE:
			PathUtil.createCubicSpline(path, startTangent, endTangent, null, null, controlPoints);
			break;
		case QUADRATIC_SPLINE:
			PathUtil.createQuadraticSpline(path, startTangent, endTangent, null, null, controlPoints);
			break;
		default:
			throw new IllegalStateException();
		}
		return new PathDetails(path, startTangent, endTangent, null, null);
	}

	/** Set the method how the polyline edge is drawn.
	 * 
	 * @param method is the type of the drawning method.
	 */
	public void setDrawingMethod(DrawingMethod method) {
		if (method!=null && this.drawingMethod!=method) {
			DrawingMethod old = this.drawingMethod;
			this.drawingMethod = method;
			updateGeometry();
			firePropertyChange(PROPERTY_DRAWINGMETHOD, old, this.drawingMethod);
			repaint(true);
		}
	}

	/** Replies the method how the polyline edge is drawn.
	 * 
	 * @return the method used to draw this edge.
	 */
	public DrawingMethod getDrawingMethod() {
		if (this.drawingMethod==null)
			this.drawingMethod = DrawingMethod.SEGMENTS; // to avoid null pointer exception in constructor
		return this.drawingMethod;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_DRAWINGMETHOD, this.drawingMethod); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_DRAWINGMETHOD, DrawingMethod.class); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setDrawingMethod(propGet(DrawingMethod.class, PROPERTY_DRAWINGMETHOD, this.drawingMethod, true, properties)); 
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void computeSegmentLengths(float[] lengths) {
		switch(getDrawingMethod()) {
		case SEGMENTS:
			super.computeSegmentLengths(lengths);
			break;
		case BEZIER_SPLINE:
		case QUADRATIC_SPLINE:
			PathUtil.computeSegmentLengths(getPath(), lengths, getCtrlPoints());
			break;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hitSegment(float x, float y, float epsilon) {
		if (getDrawingMethod()==DrawingMethod.BEZIER_SPLINE
				|| getDrawingMethod()==DrawingMethod.QUADRATIC_SPLINE) {
			if ( getDamagedBounds().contains(x,y) ) {
				Path2f path = getPath();
				assert(path!=null);
				Iterator<PathElement2f> pathIterator = path.getPathIterator(MathConstants.SPLINE_APPROXIMATION_RATIO);
				PathElement2f pathElement;
				int index = 0 ;

				// Checks all segments
				while (pathIterator.hasNext()) {
					pathElement = pathIterator.next();
					if ( ( index < 0 ) || ( index >= getCtrlPointCount() ) ) {
						index = -1 ;
					}
					else if( getCtrlPointAt(index+1).equals( 
							new Point2f( pathElement.toX, pathElement.toY ) ) ) {
						index ++ ;
					}
					switch( pathElement.type) {
					case MOVE_TO:
						break;
					case LINE_TO:
					case CLOSE:
						if ( isClosedToSegment(
								pathElement.fromX, pathElement.fromY,
								pathElement.toX, pathElement.toY,
								x, y,
								epsilon ) ) {
							return index;
						}
						break;
					default:
						throw new IllegalStateException();
					}		
				}
			}
			return -1;
		}
		return super.hitSegment(x, y, epsilon);
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getNearestSegmentTo(float x, float y) {
		if (getDrawingMethod()==DrawingMethod.BEZIER_SPLINE
				|| getDrawingMethod()==DrawingMethod.QUADRATIC_SPLINE) {
			Path2f path = getPath();
			assert(path!=null);
			Iterator<PathElement2f> pathIterator = path.getPathIterator(MathConstants.SPLINE_APPROXIMATION_RATIO);
			PathElement2f pathElement;
			int index = 0 ;
			int nearestSegment = -1;
			float minDistance = Float.POSITIVE_INFINITY;
			float d;

			// Checks all segments
			while (pathIterator.hasNext()) {
				pathElement = pathIterator.next();
				if ( ( index < 0 ) || ( index >= getCtrlPointCount() ) ) {
					index = -1 ;
				}
				else if( getCtrlPointAt(index+1).equals( 
						new Point2f( pathElement.toX, pathElement.toY ) ) ) {
					index ++ ;
				}
				switch( pathElement.type ) {
				case MOVE_TO:
					break;
				case LINE_TO:
				case CLOSE:
					d = distanceToSegment(
							pathElement.fromX, pathElement.fromY,
							pathElement.toX, pathElement.toY,
							x, y,
							null);
					if (d<minDistance) {
						minDistance = d;
						nearestSegment = index;
					}
					break;
				default:
					throw new IllegalStateException();
				}		
			}
			
			return nearestSegment;
		}
		return super.getNearestSegmentTo(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean flatteningAt(int index) {
		if (index>0 && index<getCtrlPointCount()-1) {
			boolean flattenable = false;
			switch(getDrawingMethod()) {
			case BEZIER_SPLINE:
			case QUADRATIC_SPLINE:
			case SEGMENTS:
				flattenable = isFlattenable1(index);
				break;
			default:
				throw new IllegalStateException();
			}
			if (flattenable) {
				removeCtrlPointAt(index);
				return true;
			}
		}
		return false;
	}

	private boolean isFlattenable1(int index) {
		Point2D p0 = getCtrlPointAt(index-1);
		Point2D p1 = getCtrlPointAt(index);
		Point2D p2 = getCtrlPointAt(index+1);

		double x1 = p0.getX() - p1.getX();
		double y1 = p0.getY() - p1.getY();
		double x2 = p2.getX() - p1.getX();
		double y2 = p2.getY() - p1.getY();
		
		if ((x1 == 0. && y1 == 0.) || (x2 == 0. && y2 == 0))
			return true;
		double num = (x1*x2 + y1*y2);
		double denum = (x1*x1+y1*y1)*(x2*x2+y2*y2);
		if (Double.isInfinite(num)||Double.isInfinite(denum))
			return false; // Capacity exceeded
		double dotProduct = num / Math.sqrt(denum);
		dotProduct = 1. - Math.abs(dotProduct);
		return ((dotProduct >= -FLATTENING_PRECISION) && (dotProduct <= FLATTENING_PRECISION));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isClosedToSegment(float x1, float y1, float x2, float y2,
			float x, float y, float hitDistance) {
		return MathUtil.isPointClosedToSegment(x1, y1, x2, y2, x, y, hitDistance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float distanceToSegment(float x1, float y1, float x2, float y2,
			float x, float y, Point2D pts) {
		return MathUtil.distancePointToSegment(x, y, x1, y1, x2, y2, pts);
	}
	
}
