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
package org.arakhne.neteditor.fig.view;

import java.util.Collection;

import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.neteditor.fig.shadow.LinearFeatureShadowPainter;

/** This interface represents all object from
 * the figure API that may be represented
 * bu a linear feature (line, spline...).
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface LinearFeature {

	/** Replies if the linear feature is closed.
	 * 
	 * @return <code>true</code> if the linear feature is closed;
	 * <code>false</code> if it is open.
	 */
	public boolean isClosed();

	/** Return the number of point in the path of the edge. 
	 *
	 * @return an integer that is the size of the path.
	 */
	public int getCtrlPointCount();

	/** return the point of the path point edge defined
	 *  by the parameter.
	 *
	 * @param index the <var>index</var> of the point from
	 *              the path point edge to get.
	 *              0 corresponds to the sourcePortPoint
	 *              and size-1 (where "size" denote the
	 *              size of the path point list)
	 *              the destination port point.
	 * @return coordinates of the control point.
	 */
	public Point2D getCtrlPointAt( int index);

	/** Reply <code>true</code> if given coords may correspond
	 * to a position of the mouse cursor that permits to
	 * hit this figure.
	 * This function should take into account the shape of 
	 * the figure, and not only the bounds as for {@link #contains(float, float)}
	 *
	 * @param x horizontal coord.
	 * @param y vertical coord.
	 * @param epsilon is the error amount allowed for the hitting test.
	 * @return <code>true</code> if the point 
	 *         (<var>x</var>,<var>y</var>) is in this Fig.
	 * @see #contains(float, float)
	 * @deprecated see {@link #contains(float, float)}
	 */
	@Deprecated
	public boolean hit(float x, float y, float epsilon) ;

	/** Reply <code>true</code> if the object intersects the given shape.
	 *
	 *  @param r a shape
	 *  @return <code>true</code> if the figure intersects
	 *          the given shape. otherwise <code>false</code>
	 */    
	public boolean intersects(Shape2f r) ;

	/** Reply <code>true</code> if given coords are in the bounds of this Fig.
	 * This function takes into account only the bounds of the figure.
	 * If you are interested in a click in the shape of the figure,
	 * see {@link #hit(float, float, float)}
	 *
	 * @param x horizontal coord.
	 * @param y vertical coord.
	 * @return <code>true</code> if the point 
	 *         (<var>x</var>,<var>y</var>) is in this Fig.
	 * @see #hit(float, float, float)
	 */
	public boolean contains(float x, float y) ;

	/** Replies the hit control point at the specified position.
	 * The control points at the ends of the edge cannot be hit.
	 * 
	 *  @param x is the position of the hit point.
	 *  @param y is the position of the hit point.
	 * @param epsilon is the error amount allowed for the hitting test.
	 * @return the index of the control point that is hit; otherwise
	 * {@code -1}.
	 * @see #hitSegment(float, float, float)
	 */
	public int hitCtrlPoint(float x, float y, float epsilon);

	/** Replies the index of the nearest control point to the specified
	 * point.
	 * 
	 * @param x
	 * @param y
	 * @return the index of the nearest control point on the edge.
	 * @see #getNearestSegmentTo(float, float)
	 */
	public int getNearestCtrlPointTo(float x, float y);

	/** Replies the index of the nearest segment to the specified
	 * point.
	 * 
	 * @param x
	 * @param y
	 * @return the index of the nearest segment on the edge.
	 * It is always in <code>[-1,{@link #getCtrlPointCount()}-2]</code>,
	 * where the value {@code -1} is when there is no segment in the
	 * edge.
	 * @see #getNearestCtrlPointTo(float, float)
	 */
	public int getNearestSegmentTo(float x, float y);
	
	/** Replies the point on the linear feature that
	 * is near to the given coordinates.
	 *
	 * @param x horizontal location of the point.
	 * @param y vertical location of the point.
	 * @return the nearest point on the linear feature, never <code>null</code>.
	 * @since 16.0
	 */
	public Point2D getNearestPointTo(float x, float y);

	/** Replies the index of the nearest segment to the specified
	 * point.
	 * 
	 * @param x
	 * @param y
	 * @param epsilon is the maximal distance from the point to the segment.
	 * @return the index of the nearest segment on the edge.
	 * It is always in <code>[-1,{@link #getCtrlPointCount()}-2]</code>,
	 * where the value {@code -1} is when there is no segment in the
	 * edge.
	 * @see #hitCtrlPoint(float, float, float)
	 */
	public int hitSegment(float x, float y, float epsilon);

	/** Replies the general path that is representing this edge figure.
	 * 
	 * @return the general path that is representing this edge figure.
	 */
	public Path2f getPath();

	/** Replies the last segment that was successfully tested against
	 * a hit.
	 * This value is updated each time {@link #hit(float, float, float)}
	 * is invoked.
	 * 
	 * @return the index of the last hit segment; if negative these is not
	 * last hit segment.
	 * @deprecated see {@link #hitSegment(float, float, float)}
	 */
	@Deprecated
	public int getLastHitSegment();

	/** Insert a point into the path defined by the given index.
	 *
	 * @param index the index of the control point in the path.
	 * @param x is the coordinate of the control point.
	 * @param y is the coordinate of the control point.
	 */
	public void insertCtrlPointAt(int index, float x, float y);

	/** Set the point of the path defined by the given index.
	 *
	 * @param index the index of the control point in the path.
	 * @param x is new coordinate of the control point.
	 * @param y is new coordinate of the control point.
	 */
	public void setCtrlPointAt(int index, float x, float y);

	/** Set the control points of the edge.
	 *
	 * @param pathPoints new control point set
	 */
	public void setCtrlPoints(Collection<? extends Point2D> pathPoints);

	/** Set the control points of the edge.
	 *
	 * @param pathPoints new control point set
	 */
	public void setCtrlPoints(Point2D... pathPoints);

	/** Remove a point at the given index from the path.
	 *
	 * @param index the index of the control point in the path.
	 */
	public void removeCtrlPointAt(int index);

	/** Replies the shadow painter for this linear feature.
	 * 
	 * @return the shadow painter for this linear feature.
	 */
	public LinearFeatureShadowPainter getShadowPainter();

	/**
	 * Try to remove the control point at the specified index
	 * if its removal does produce a large change in the polyline.
	 * This function is useful to rmeove the control points that
	 * do not contribute enough to the shape of the edge.
	 * 
	 * @param index
	 * @return <code>true</code> if the control point was removed;
	 * <code>false</code> if it was not removed.
	 */
	public boolean flatteningAt(int index);
	
	/** Replies the path iterator of the edge.
	 * 
	 * @return the path iterator of the edge.
	 */
	public PathIterator2f getPathIterator();

}
