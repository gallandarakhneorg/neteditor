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

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.continous.object2d.UnmodifiablePoint2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Vector2D;
import org.arakhne.afc.ui.vector.PathUtil;
import org.arakhne.neteditor.fig.anchor.AnchorFigure;
import org.arakhne.neteditor.fig.figure.AbstractModelObjectFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.edge.symbol.EdgeSymbol;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.shadow.ShadowedControlPoint;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.LinearFeature;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.Node;

/** Abstract {@link Figure} classe for representing edges between 
 *  nodes' ports.
 *
 * @param <E> is the type of the model edge supported by this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class EdgeFigure<E extends Edge<?,?,?,?>> extends AbstractModelObjectFigure<E> implements LinearFeature {

	private static final long serialVersionUID = -1067666543925554497L;

	/** This is the set of control points that composed
	 *  this edge.
	 */
	private final List<ControlPoint> points = new ArrayList<ControlPoint>() ;

	/** This is the end-symbol connected to the source port.
	 */
	private EdgeSymbol startSymbol = null;

	/** This is the end-symbol connected to the target port.
	 */    
	private EdgeSymbol endSymbol = null;

	/** Indicates the last segment that was successfully tested against
	 * a hit.
	 * This attribute is updated each time {@link #hit(float, float, float)}
	 * is invoked.
	 */
	protected int lastHitSegment = -1;

	/** Buffered general path.
	 */
	private SoftReference<PathDetails> bufferedPath = null;

	private transient SoftReference<float[]> bufferedSegmentLengths = null;

	/** Contruct a new AbstractEdgeFigure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param xfrom horizontal position of the begin point for this AbstractEdgeFigure.
	 * @param yfrom vertical position of the begin point for this AbstractEdgeFigure.
	 * @param xto horizontal position of the end point for this AbstractEdgeFigure.
	 * @param yto vertical position of the end point for this AbstractEdgeFigure.
	 */
	public EdgeFigure(UUID viewUUID, float xfrom, float yfrom, float xto, float yto) { 
		super(viewUUID,
				Math.min(xfrom, xto),
				Math.min(yfrom, yto),
				DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE) ;
		setMinimalWidth(0f);
		setMinimalHeight(0f);
		this.points.add(new ControlPoint(xfrom,yfrom)) ;
		this.points.add(new ControlPoint(xto,yto)) ;
		updateGeometry() ;
	}
	
	@Override
	public boolean contains(float x, float y) {
		return Path2f.contains(getPathIterator(), x, y);
	}
	
	@Override
	public boolean contains(Rectangle2f r) {
		return Path2f.contains(getPathIterator(), r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
	}
	
	@Override
	public boolean intersects(Shape2f r) {
		return r.intersects(getBounds()) && r.intersects(getPath());
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean isClosed() {
		return false;
	}
	
	/** {@inheritDoc} 
	 */
	@Override
	public Iterable<? extends SubFigure> getSubFigures() {
		return Collections.emptyList();
	}

	/** {@inheritDoc}
	 */
	@Override
	public final Path2f getPath() {
		PathDetails p = getPathDetails();
		return p.path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PathIterator2f getPathIterator() {
		return getPath().getPathIterator();
	}

	/** Replies the tangent at the first point.
	 * 
	 * @return the tangent at the first point.
	 */
	public final Vector2D getTangentAtFirstPoint() {
		PathDetails p = getPathDetails();
		return p.startPointTangent;
	}
	
	/** Replies the tangent at the last point.
	 * 
	 * @return the tangent at the last point.
	 */
	public final Vector2D getTangentAtLastPoint() {
		PathDetails p = getPathDetails();
		return p.lastPointTangent;
	}

	private float[] getSegmentLengths() {
		float[] lengths = this.bufferedSegmentLengths==null ? null : this.bufferedSegmentLengths.get();
		if (lengths==null) {
			lengths = new float[getCtrlPointCount()-1];
			computeSegmentLengths(lengths);
			this.bufferedSegmentLengths = new SoftReference<float[]>(lengths);
		}
		return lengths;
	}

	/** Compute the lengths of all the segments and put these lengths
	 * in the specified array.
	 * The size of the specified array must be the same as 
	 * the number of control points
	 *
	 * @param lengths are the computed lengths.
	 */
	protected void computeSegmentLengths(float[] lengths) {
		assert(this.points.size()==lengths.length);
		Point2D p;
		Point2D previous = this.points.get(0);
		for(int i=1; i<this.points.size(); ++i) {
			p = this.points.get(i);
			lengths[i-1] = MathUtil.distancePointToPoint(
					previous.getX(), previous.getY(),
					p.getX(), p.getY());
			previous = p;
		}
		lengths[lengths.length-1] = 0;
	}

	/** Replies the distance of the first point of the segment at 
	 * the specified index from the beginning of the edge.
	 *
	 * @param index is the index of the segment in the edge.
	 * @return the distance from the beginning of the edge to the
	 * first point of the segment.
	 */
	public float getDistanceToSegmentAt(int index) {
		float dist = 0f;
		float[] lengths = getSegmentLengths();
		for(int i=0; i<index; ++i) {
			dist += lengths[i];
		}
		return dist;
	}

	/** Replies the size of the segment at the specified index.
	 * 
	 * @param index is the index of the segment to consider.
	 * @return the size of the segment.
	 */
	public float getSegmentLength(int index) {
		return getSegmentLengths()[index];
	}

	/** Replies the size of the edge.
	 * 
	 * @return the length of the entire edge.
	 */
	public float getLength() {
		float length = 0f;
		for(float d : getSegmentLengths()) {
			length += d;
		}
		return length;
	}
	
	private PathDetails getPathDetails() {
		PathDetails p = (this.bufferedPath==null) ? null : this.bufferedPath.get();
		if (p==null) {
			p = computePath(this.points);
			this.bufferedPath = new SoftReference<PathDetails>(p);
		}
		return p;
	}

	/** Create and replies the general path that is representing this edge figure.
	 * It is recommended to invoke {@link #getPath()} instead of this
	 * method.
	 * 
	 * @param controlPoints are the control points that are composing the path.
	 * @return the general path.
	 */
	protected abstract PathDetails computePath(List<? extends Point2D> controlPoints);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanUp() {
		super.cleanUp();
		this.bufferedPath = null;
		this.bufferedSegmentLengths = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ResizeDirection> getResizeDirections() {
		return Collections.emptySet();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResizeAllDirections() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResizeDirections(ResizeDirection... resizeDirections) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EdgeShadowPainter createShadowPainter() {
		return new SPainter();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized EdgeShadowPainter getShadowPainter() {
		return (EdgeShadowPainter)super.getShadowPainter();
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setBounds(float x, float y, float width, float height) { 
		setLocation( x, y );
	}

	/** {@inheritDoc}
	 */
	@Override
	public final void setCtrlPoints(Point2D... pathPoints) {
		setCtrlPoints(Arrays.asList(pathPoints));
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setCtrlPoints(Collection<? extends Point2D> pathPoints) {
		cleanUp();
		this.points.clear();
		for(Point2D p : pathPoints) {
			this.points.add(new ControlPoint(p));
		}
		refreshConnectedCtrlPoints();
		repaint(true);
	}
	

	/** Get the edge control points.
	 *
	 * @return control points of this edge.
	 */
	public List<? extends Point2D> getCtrlPoints() {
		return Collections.unmodifiableList(this.points) ;
	}

	/** Set the source port point.
	 *
	 * @param x is the coordinate of the first control point.
	 * @param y is the coordinate of the first control point.
	 */
	public void setFirstCtrlPoint( float x, float y ) {
		ControlPoint p = this.points.get(0);
		if (x!=p.getX() || y!=p.getY()) {
			p.setCtrlPoint(x, y);
			updateGeometry();
			repaint(true);
		}
	}

	/** Get The source port point.
	 *
	 * @return the first control point.
	 */
	public UnmodifiablePoint2f getFirstCtrlPoint() {
		return this.points.get(0) ;
	}

	/** Set the destination port point.
	 *
	 * @param x is the coordinate of the last control point.
	 * @param y is the coordinate of the last control point.
	 */
	public void setLastCtrlPoint( float x, float y) {
		int size = this.points.size() ; 
		ControlPoint p = this.points.get(size-1);
		if (x!=p.getX() || y!=p.getY()) {
			p.setCtrlPoint(x, y);
			updateGeometry();
			repaint(true);
		}
	}

	/** Get the destination port point.
	 *
	 * @return the last control point.
	 */
	public UnmodifiablePoint2f getLastCtrlPoint() {
		return this.points.get(this.points.size()-1);
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getCtrlPointCount() {
		return this.points.size() ;
	}

	/** Return the max number of control point for the edge. Usually two
	 *  for a linear edge, and <code>Interger.MAX_VALUE</code>
	 *  for a poly edge.
	 * 
	 * @return an integer that is the max count of control points.
	 */
	public abstract int getMaxAllowedCtrlPoints() ;

	/** {@inheritDoc}
	 */
	@Override
	public Point2f getCtrlPointAt( int index) {
		return this.points.get(index) ;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setCtrlPointAt(int index, float x, float y) {
		ControlPoint p = this.points.get(index);
		if (x!=p.getX() || y!=p.getY()) {
			p.setCtrlPoint(x, y);

			// Update the position of the ends if necessary.
			if (index<=1 || index>=this.points.size()-2)
				refreshConnectedCtrlPoints();
			else
				updateGeometry();

			repaint(true);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void insertCtrlPointAt(int index, float x, float y) {
		if (this.points.size()<getMaxAllowedCtrlPoints()) {
			int idx = index;
			if (idx<0) idx = 0;
			if (idx>this.points.size()) idx = this.points.size();
			this.points.add(idx, new ControlPoint(x,y));
			
			// Update the position of the ends if necessary.
			if (index<=1 || index>=this.points.size()-2)
				refreshConnectedCtrlPoints();
			else
				updateGeometry();
			
			repaint(true);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getNearestSegmentTo(float x, float y) {
		float distance = Float.POSITIVE_INFINITY;
		Point2D p1, p2;
		float d;
		int index=  -1;
		for(int i=0; i<this.points.size()-1; ++i) {
			p1 = this.getCtrlPointAt(i);
			p2 = this.getCtrlPointAt(i+1);
			d = distanceToSegment(
					p1.getX(), p1.getY(),
					p2.getX(), p2.getY(),
					x, y,
					null);
			if (!Float.isNaN(d) && d<distance) {
				distance = d;
				index = i;
			}
		}
		if (Float.isInfinite(distance) || Float.isNaN(distance)) {
			return -1;
		}
		return index;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Point2D getNearestPointTo(float x, float y) {
		Point2D pts = new Point2f();
		Point2D ppts = new Point2f();
		float distance = Float.POSITIVE_INFINITY;
		Point2D p1, p2;
		float d;
		for(int i=0; i<this.points.size()-1; ++i) {
			p1 = this.getCtrlPointAt(i);
			p2 = this.getCtrlPointAt(i+1);
			d = distanceToSegment(
					p1.getX(), p1.getY(),
					p2.getX(), p2.getY(),
					x, y,
					ppts);
			if (!Float.isNaN(d) && d<distance) {
				distance = d;
				pts.set(ppts);
			}
		}
		return pts;
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getNearestCtrlPointTo(float x, float y) {
		float distance = Float.POSITIVE_INFINITY;
		float d;
		int index=  -1;
		int i=0;
		for(Point2D p : this.points) {
			d = MathUtil.distancePointToPoint(
					x, y,
					p.getX(), p.getY());
			if (!Float.isNaN(d) && d<distance) {
				distance = d;
				index = i;
			}
			++i;
		}
		if (Float.isInfinite(distance) || Float.isNaN(distance)) {
			return -1;
		}
		return index;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void removeCtrlPointAt(int index) {
		if (this.points.size()>2) {
			this.points.remove(index) ;
			updateGeometry();
			repaint(true);
		}
	}

	/** Calculate the bounds of the edge and its associated symbols.
	 * This function invokes:<ul>
	 * <li>{@link #cleanUp()}</li>
	 * <li>{@link #computeEdgeBounds()}</li>
	 * <li>{@link #updateSymbolGeometry(EdgeSymbol, int, Point2D, Vector2D)} on the both symbols</li>
	 * <li>{@link #updateAssociatedGeometry()}</li>
	 * <li>{@link #setBounds(float, float, float, float)}</li>.
	 * </ul>
	 */
	protected void updateGeometry() {
		cleanUp();

		Rectangle2f bounds = computeEdgeBounds();

		EdgeSymbol s;
		s = getStartSymbol();
		if (s!=null) {
			Point2D p0 = getCtrlPointAt(0);
			updateSymbolGeometry(s, 0, p0, getTangentAtFirstPoint());
			Rectangle2f r = s.getBounds();
			if (bounds==null) bounds = r.clone();
			else bounds = bounds.createUnion(r);
		}
		s = getEndSymbol();
		if (s!=null) {
			Point2D p0 = getCtrlPointAt(getCtrlPointCount()-1);
			updateSymbolGeometry(s, getCtrlPointCount()-1, p0, getTangentAtLastPoint());
			Rectangle2f r = s.getBounds();
			if (bounds==null) bounds = r.clone();
			else bounds = bounds.createUnion(r);
		}

		if (bounds!=null)
			super.setBounds(
					bounds.getMinX(), bounds.getMinY(),
					bounds.getWidth(), bounds.getHeight());

		updateAssociatedGeometry();
	}

	/** Invoked to refine the computation of the bounds of the edge's segments.
	 * By default this function compute the bounds of the control points themselves.
	 * It is defined to be overridden by the subclasses.
	 * 
	 * @return the bounds of the edge's control points; or <code>null</code>
	 * if the bounds cannot be computed.
	 */
	protected Rectangle2f computeEdgeBounds() {
		Path2f path = getPath();
		return path.toBoundingBox();
	}

	/** Invoked to update the geometry of the given end-symbol.
	 * By default this function call {@link EdgeSymbol#setGeometry(float, float, float, float)}.
	 * It is defined to be overridden by the subclasses when
	 * they need to update the symbol is a specifical way.
	 * 
	 * @param symbol is the end-symbol to be updated.
	 * @param indexOfAttachPoint is the index of the point passed in <var>attachPoint</var>.
	 * @param attachPoint is the point where the symbol is attached.
	 * @param tangent is the tangent at the attach point. 
	 */
	@SuppressWarnings("static-method")
	protected void updateSymbolGeometry(
			EdgeSymbol symbol, 
			int indexOfAttachPoint,
			Point2D attachPoint,
			Vector2D tangent) {
		symbol.setGeometry(
				attachPoint.x(), attachPoint.y(),
				attachPoint.x()+tangent.getX(),
				attachPoint.y()+tangent.getY());
	}

	/** {@inheritDoc}
	 * 
	 * This function changes the anchors of
	 * the associated coerced figures if, and only if, their
	 * description is a number that is corresponding to the position
	 * of the anchor on the edge.
	 * It is defined to be overridden by the subclasses.
	 */
	@Override
	protected void updateAssociatedGeometry() {
		for(Figure figure : getAssociatedFiguresInView().values()) {
			if (figure instanceof CoercedFigure) {
				CoercedFigure f = (CoercedFigure)figure;
				Object d = f.getAnchorDescriptor();
				if (d instanceof Number) {
					Point2D anchor = PathUtil.interpolate(getPath(), ((Number)d).floatValue());
					f.setLocationFromAnchorPoint(
							anchor.getX(), anchor.getY());
				}
			}
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setLocation(float x, float y) {
		Point2D loc = getLocation();
		if (x!=loc.getX() || y!=loc.getY()) {
			if (!this.points.isEmpty()) {
				float dx = (x - loc.getX());
				float dy = (y - loc.getY());
				for(int i=1; i<this.points.size()-1; ++i) {
					this.points.get(i).translateCtrlPoint(dx, dy);
				}
			}
			updateGeometry();
			repaint(true);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void translate(float dx, float dy) {
		if (dx!=0f || dy!=0f) {
			if (!this.points.isEmpty()) {
				for(int i=1; i<this.points.size()-1; ++i) {
					this.points.get(i).translateCtrlPoint(dx, dy);
				}
			}
			updateGeometry();
			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public abstract boolean flatteningAt(int index);

	/** Compute and replies the position of the end of the edge that
	 * is connected to the anchor "<var>from</var>" and the other side
	 * of the edge is connected to the anchor "<var>to</var>".
	 * 
	 * @param from is the anchor connected to
	 * @param to is the other side of the edge.
	 * @param isFromStart indicates of the from is the start anchor of the edge.
	 * This parameter is useful when the "from" and "to" is the same anchor.
	 * @return the point of connection to the anchor "<var>from</var>". 
	 */
	Point2D findConnectingPointFromTo(Anchor<?,?,?,?> from, Anchor<?,?,?,?> to, boolean isFromStart) {
		Point2D p;
		float x,y;
		int outsideIndex = -1;
		assert(from!=null);
		Node<?,?,?,?> node = from.getNode();
		Figure figure = null;
		if (node!=null) {
			figure = node.getViewBinding().getView(getViewUUID(), Figure.class);
		}
		if (isFromStart) {
			for(int i=1;
					outsideIndex==-1 && i<getCtrlPointCount()-1;
					++i) {
				p = getCtrlPointAt(i);
				x = p.getX();
				y = p.getY();
				if (figure==null || !figure.contains(x, y)) {
					outsideIndex = i;
				}
			}
		}
		else {
			for(int i=getCtrlPointCount()-2;
					outsideIndex==-1 && i>0;
					--i) {
				p = getCtrlPointAt(i);
				x = p.getX();
				y = p.getY();
				if (figure==null || !figure.contains(x, y)) {
					outsideIndex = i;
				}
			}
		}
		if (outsideIndex!=-1) {
			return getCtrlPointAt(outsideIndex);
		}

		// The point is the center of the other anchor, or the control point
		// at the other end of the edge.
		if (to!=null) {
			AnchorFigure<?> aFigure = to.getViewBinding().getView(getViewUUID(), AnchorFigure.class);
			if (aFigure!=null) {
				return new Point2f(
						aFigure.getAbsoluteX() + aFigure.getWidth()/2f,
						aFigure.getAbsoluteY() + aFigure.getHeight()/2f);
			}
		}
		return isFromStart ? getLastCtrlPoint() : getFirstCtrlPoint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateFromModel(ModelObjectEvent event) {
		if (event==null) {
			// The associated model object is a new one.
			refreshConnectedCtrlPoints();
		}
		else {
			switch(event.getType()) {
			case LINK_CHANGE:
				//
				// Force the position of the end's control points to
				// the best positions on the anchors.
				//
				ModelObject obj = event.getNewLinkedObject();
				if (obj instanceof Anchor<?,?,?,?>) {
					Anchor<?,?,?,?> anchor = (Anchor<?,?,?,?>)obj;
					Anchor<?,?,?,?> otherAnchor = getModelObject().getOtherSideFrom(anchor);
					boolean isFromFirstAnchor = anchor==getModelObject().getStartAnchor();

					// Search the point that is the other side of the connecting segment
					Point2D pts = findConnectingPointFromTo(anchor, otherAnchor, isFromFirstAnchor);

					AnchorFigure<?> figure = anchor.getViewBinding().getView(getViewUUID(), AnchorFigure.class);
					assert(figure!=null);
					pts = figure.getConnectionPointFrom(pts);
					if (isFromFirstAnchor)
						setFirstCtrlPoint(pts.getX(), pts.getY());
					else
						setLastCtrlPoint(pts.getX(), pts.getY());

					// Try to initialize the other-side anchor
					if (otherAnchor!=null) {
						figure = otherAnchor.getViewBinding().getView(getViewUUID(), AnchorFigure.class);
						if (figure!=null) {
							pts = findConnectingPointFromTo(otherAnchor, anchor, !isFromFirstAnchor);
							pts = figure.getConnectionPointFrom(pts);
							if (isFromFirstAnchor)
								setLastCtrlPoint(pts.getX(), pts.getY());
							else
								setFirstCtrlPoint(pts.getX(), pts.getY());
						}
					}

					updateGeometry();
				}
				break;
			default:
			}
		}
	}

	/** Refresh the positions of the connected control points.
	 */
	public void refreshConnectedCtrlPoints() {
		E edge = getModelObject();
		if (edge!=null) {
			Anchor<?,?,?,?> anchor1, anchor2;
			AnchorFigure<?> figure;

			anchor1 = edge.getStartAnchor();
			anchor2 = edge.getEndAnchor();

			if (anchor1!=null) {
				Point2D pts = findConnectingPointFromTo(anchor1, anchor2, true);
				figure = anchor1.getViewBinding().getView(getViewUUID(), AnchorFigure.class);
				if (figure!=null) {
					pts = figure.getConnectionPointFrom(pts);
					setFirstCtrlPoint(pts.getX(), pts.getY());
				}
			}

			if (anchor2!=null) {
				Point2D pts = findConnectingPointFromTo(anchor2, anchor1, false);
				figure = anchor2.getViewBinding().getView(getViewUUID(), AnchorFigure.class);
				if (figure!=null) {
					pts = figure.getConnectionPointFrom(pts);
					setLastCtrlPoint(pts.getX(), pts.getY());
				}
			}

			updateGeometry();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hitSegment(float x, float y, float epsilon) {
		if ( getDamagedBounds().contains(x,y) ) {
			ControlPoint previous = null;
			this.lastHitSegment = 0;
			for(ControlPoint p : this.points) {
				if (previous!=null) {
					if (isClosedToSegment(
							previous.x(), previous.y(),
							p.x(), p.y(),
							x, y,
							epsilon)) {
						return this.lastHitSegment;
					}
					++this.lastHitSegment;
				}
				previous = p;
			}
		}
		this.lastHitSegment = -1;
		return this.lastHitSegment;
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getLastHitSegment() {
		return this.lastHitSegment;
	}

	/** Replies if a point is closed to a segment.
	 * By default this function calls
	 * {@link MathUtil#isPointClosedToSegment(float, float, float, float, float, float, float)}.
	 * This function may be overridden by the subclasses to 
	 * influence the {@link #hitSegment(float, float, float)}'s behavior.
	 *
	 * @param x1 horizontal location of the first-segment begining.
	 * @param y1 vertical location of the first-segment ending.
	 * @param x2 horizontal location of the second-segment begining.
	 * @param y2 vertical location of the second-segment ending.
	 * @param x horizontal location of the point.
	 * @param y vertical location of the point.
	 * @param hitDistance is the maximal hitting distance.
	 * @return <code>true</code> if the point and the
	 *         line have closed locations.
	 */
	protected abstract boolean isClosedToSegment(float x1, float y1,  float x2, float y2, 
			float x, float y, float hitDistance);

	/** Replies if a point is closed to a segment.
	 * By default this function calls
	 * {@link MathUtil#distancePointToSegment(float, float, float, float, float, float)}.
	 * This function may be overridden by the subclasses to 
	 * influence the {@link #getNearestSegmentTo(float, float)}'s behavior.
	 *
	 * @param x1 horizontal location of the first-segment begining.
	 * @param y1 vertical location of the first-segment ending.
	 * @param x2 horizontal location of the second-segment begining.
	 * @param y2 vertical location of the second-segment ending.
	 * @param x horizontal location of the point.
	 * @param y vertical location of the point.
	 * @param pts is set with the coordinates of the nearest point, if not <code>null</code>.
	 * @return <code>true</code> if the point and the
	 *         line have closed locations.
	 */
	protected abstract float distanceToSegment(float x1, float y1,  float x2, float y2, 
			float x, float y, Point2D pts);

	/** {@inheritDoc}
	 */
	@Override
	public final boolean hit(float x, float y, float epsilon) {
		// Tests if hitting the edges
		boolean hitted = hitSegment( x, y, epsilon ) != -1;
		// Tests the hitting of end-symbols
		if (!hitted) {
			EdgeSymbol s;
			s = getStartSymbol();
			if (s!=null) hitted = s.hit(x, y, epsilon);
			if (!hitted) {
				s = getEndSymbol();
				if (s!=null) hitted = s.hit(x, y, epsilon);
			}
		}
		return hitted ;
	}

	/** {@inheritDoc}
	 */
	@Override
	public int hitCtrlPoint(float x, float y, float epsilon) {
		ControlPoint p;
		for(int i=1; i<this.points.size()-1; ++i) { 
			p = this.points.get(i);
			if (MathUtil.distancePointToPoint(x, y, p.getX(), p.getY())<=epsilon) {
				return i;
			}
		}
		return -1;
	}

	/** Method to paint end-symbols of this Fig.
	 *
	 * @param g the graphic context
	 * @since 0.9
	 */
	protected void paintEdgeSymbols(ViewGraphics2D g) {
		if ( this.startSymbol != null ) {
			this.startSymbol.paint(g);
		}
		if ( this.endSymbol != null ) {
			this.endSymbol.paint(g);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void paint(ViewGraphics2D g) {
		g.beginGroup();
		paintSegments(g, getPath());
		paintEdgeSymbols(g);
		g.endGroup();
	}

	/**
	 * Paint the segments represented by the specified control points.
	 * 
	 * @param g is the graphical context.
	 * @param path is the path to draw.
	 */
	@SuppressWarnings("static-method")
	protected void paintSegments(ViewGraphics2D g, Path2f path) {
		g.setOutlineDrawn(true);
		g.setInteriorPainted(false);
		g.draw(path);
	}

	///////////////////////////////////////////////////////////
	// End-symbol API

	/** Reply the end-symbol connected to the source port.
	 *
	 * @return the start end-symbol or <code>null</code> if none.
	 */
	public EdgeSymbol getStartSymbol() {
		return this.startSymbol;
	}

	/** Change the end-symbol connected to the source port.
	 *
	 * @param symbol the new end-symbol.
	 */
	public void setStartSymbol(EdgeSymbol symbol) {
		if (symbol!=this.startSymbol && (this.endSymbol==null || (this.endSymbol!=symbol))) {
			EdgeSymbol old = this.startSymbol;
			if (this.startSymbol!=null)
				this.startSymbol.setEdge(null);
			this.startSymbol = symbol;
			if (this.startSymbol!=null) {
				Point2D p0 = getCtrlPointAt(0);
				this.startSymbol.setEdge(this);
				updateSymbolGeometry(this.startSymbol, 0, p0, getTangentAtFirstPoint());
			}
			firePropertyChange(PROPERTY_STARTSYMBOL, old, this.startSymbol); 
			repaint(true);
		}
	}

	/** Reply the end-symbol connected to the target port.
	 *
	 * @return the end end-symbol or <code>null</code> if none.
	 */
	public EdgeSymbol getEndSymbol() {
		return this.endSymbol;
	}

	/** Change the end-symbol connected to the target port.
	 *
	 * @param symbol is the new end-symbol.
	 */
	public void setEndSymbol(EdgeSymbol symbol) {
		if (symbol!=this.endSymbol && (this.startSymbol==null || (this.startSymbol!=symbol))) {
			EdgeSymbol old = this.endSymbol;
			if (this.endSymbol!=null)
				this.endSymbol.setEdge(null);
			this.endSymbol = symbol;
			if (this.endSymbol!=null) {
				Point2D p0 = getCtrlPointAt(getCtrlPointCount()-1);
				this.endSymbol.setEdge(this);
				updateSymbolGeometry(this.endSymbol, getCtrlPointCount()-1, p0, getTangentAtLastPoint());
			}
			firePropertyChange(PROPERTY_ENDSYMBOL, old, this.startSymbol); 
			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		if (this.startSymbol!=null) {
			Map<String,Object> props = this.startSymbol.getProperties();
			for(Entry<String,Object> entry : props.entrySet()) {
				properties.put(PROPERTY_STARTSYMBOL+"."+entry.getKey(), entry.getValue()); //$NON-NLS-1$
			}
		}
		if (this.endSymbol!=null) {
			Map<String,Object> props = this.endSymbol.getProperties();
			for(Entry<String,Object> entry : props.entrySet()) {
				properties.put(PROPERTY_ENDSYMBOL+"."+entry.getKey(), entry.getValue()); //$NON-NLS-1$
			}
		}
		StringBuilder b = new StringBuilder();
		for(Point2D p : this.points) {
			b.append("("); //$NON-NLS-1$
			b.append(p.getX());
			b.append("|"); //$NON-NLS-1$
			b.append(p.getY());
			b.append(")"); //$NON-NLS-1$
		}
		properties.put(PROPERTY_CONTROLPOINTS, b.toString()); 
		return properties;
	}

	private static EdgeSymbol setSymbolProperties(EdgeSymbol symbol, Map<String,Object> properties) {
		Object sType = properties.get(PROPERTY_TYPE);
		if (sType!=null) {
			try {
				Class<?> type = Class.forName(sType.toString());
				if (EdgeSymbol.class.isAssignableFrom(type)) {
					if (type.isInstance(symbol)) {
						symbol.setProperties(properties);
						return symbol;
					}
					EdgeSymbol newSymbol = (EdgeSymbol)type.newInstance();
					newSymbol.setProperties(properties);
					return newSymbol;
				}
			}
			catch (Exception e) {
				//
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			String ctrlpoints = propGetString(PROPERTY_CONTROLPOINTS, "", true, properties); //$NON-NLS-1$ 
			if (ctrlpoints!=null && !ctrlpoints.isEmpty()) {
				Pattern pattern = Pattern.compile("\\(([0-9+-.eE]+)\\|([0-9+-.eE]+)\\)"); //$NON-NLS-1$
				Matcher matcher = pattern.matcher(ctrlpoints);

				cleanUp();
				this.points.clear();
				
				while (matcher.find()) {
					float x = Float.parseFloat(matcher.group(1));
					float y = Float.parseFloat(matcher.group(2));
					this.points.add(new ControlPoint(x,y));
				}
				refreshConnectedCtrlPoints();
				repaint(true);
			}

			Map<String,Object> startSymbolProps = new TreeMap<String, Object>();
			Map<String,Object> endSymbolProps = new TreeMap<String, Object>();
			for(Entry<String,Object> entry : properties.entrySet()) {
				if (entry.getKey().startsWith(PROPERTY_STARTSYMBOL+".")) { //$NON-NLS-1$
					startSymbolProps.put(entry.getKey().substring(12), entry.getValue());
				}
				else if (entry.getKey().startsWith(PROPERTY_ENDSYMBOL+".")) { //$NON-NLS-1$
					endSymbolProps.put(entry.getKey().substring(10), entry.getValue());
				}
			}

			setStartSymbol(setSymbolProperties(getStartSymbol(), startSymbolProps));
			setEndSymbol(setSymbolProperties(getEndSymbol(), endSymbolProps));
		}
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class SPainter implements EdgeShadowPainter {

		/** Controls points used to draw the moving shadow.
		 */
		private final List<ShadowedControlPoint> shadowPoints = new ArrayList<ShadowedControlPoint>();

		private PathDetails bufferedPainterPath = null;
		private Rectangle2f bufferedBounds = null;

		/**
		 */
		@SuppressWarnings("synthetic-access")
		public SPainter() {
			for(ControlPoint p : EdgeFigure.this.points) {
				this.shadowPoints.add(new ShadowedControlPoint(p));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UUID getUUID() {
			return EdgeFigure.this.getUUID();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Figure getFigure() {
			return EdgeFigure.this;
		}

		private Path2f getBufferedPath() {
			if (this.bufferedPainterPath==null) {
				this.bufferedPainterPath = computePath(this.shadowPoints);
			}
			return this.bufferedPainterPath.path;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Rectangle2f getShadowBounds() {
			if (this.bufferedBounds==null) {
				this.bufferedBounds = new Rectangle2f();
				boolean init = false;
				for(ShadowedControlPoint p : this.shadowPoints) {
					if (init) this.bufferedBounds.add(p);
					else {
						init = true;
						this.bufferedBounds.setFromCorners(p, p);
					}
				}
			}
			return this.bufferedBounds;
		}
		
		@Override
		public Rectangle2f getDamagedBounds() {
			return getShadowBounds();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void moveFirstAnchorTo(float dx, float dy) {
			moveControlPointTo(0, dx, dy);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void moveSecondAnchorTo(float dx, float dy) {
			moveControlPointTo(this.shadowPoints.size()-1, dx, dy);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void moveControlPointTo(int index, float dx, float dy) {
			if (dx!=0f || dy!=0f) {
				ShadowedControlPoint p = this.shadowPoints.get(index);
				p.translateFromOrigin(dx, dy);
				this.bufferedPainterPath = null;
				this.bufferedBounds = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void moveTo(float dx, float dy) {
			if (dx!=0f || dy!=0f) {
				this.bufferedBounds = new Rectangle2f();
				boolean init = false;
				for(ShadowedControlPoint pts : this.shadowPoints) {
					pts.translateFromOrigin(dx, dy);
					if (init) this.bufferedBounds.add(pts);
					else {
						init = true;
						this.bufferedBounds.setFromCorners(pts, pts);
					}
				}
				this.bufferedPainterPath = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void paint(ViewGraphics2D g) {
			Path2f path = getBufferedPath();
			g.pushRenderingContext(EdgeFigure.this, path, path.toBoundingBox());
			EdgeFigure.this.paintSegments(g, path);
			g.popRenderingContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public synchronized void release() {
			EdgeFigure.this.releaseShadowPainter();
			this.shadowPoints.clear();
			this.bufferedPainterPath = null;
			this.bufferedBounds = null;
		}

	}

	/** A container for a path and the tangents
	 * of the path ends.
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected static class PathDetails {

		/** Path.
		 */
		public final Path2f path;
		
		/** Tangent to the first point.
		 */
		public final Vector2D startPointTangent;
		
		/** Tangent to the last point.
		 */
		public final Vector2D lastPointTangent;

		/** Outside path.
		 */
		public final Path2f outsidePath;

		/** Inside path.
		 */
		public final Path2f insidePath;

		/**
		 * @param path
		 * @param startTangent
		 * @param endTangent
		 */
		public PathDetails(Path2f path, Vector2D startTangent, Vector2D endTangent) {
			super();
			this.path = path;
			this.startPointTangent = startTangent;
			this.lastPointTangent = endTangent;
			this.outsidePath = this.insidePath = null;
		}

		/**
		 * @param path
		 * @param startTangent
		 * @param endTangent
		 * @param outsidePath
		 * @param insidePath
		 */
		public PathDetails(Path2f path, Vector2D startTangent, Vector2D endTangent, Path2f outsidePath, Path2f insidePath) {
			super();
			this.path = path;
			this.startPointTangent = startTangent;
			this.lastPointTangent = endTangent;
			this.outsidePath = outsidePath;
			this.insidePath = insidePath;
		}

	}
	
}
