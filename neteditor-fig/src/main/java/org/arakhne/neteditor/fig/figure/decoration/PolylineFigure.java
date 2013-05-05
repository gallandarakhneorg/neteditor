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
package org.arakhne.neteditor.fig.figure.decoration;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.arakhne.afc.math.MathConstants;
import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.PathElement2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.continous.object2d.UnmodifiablePoint2f;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Vector2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.PathUtil;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.shadow.LinearFeatureShadowPainter;
import org.arakhne.neteditor.fig.shadow.ShadowedControlPoint;
import org.arakhne.neteditor.fig.view.DrawingMethod;
import org.arakhne.neteditor.fig.view.LinearFeature;

/** A decoration figure that is drawing a polyline.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PolylineFigure extends DecorationFigure implements LinearFeature {

	private static final long serialVersionUID = 3504513094459260798L;

	/** <code>true</code> if this area must be filled, 
	 *  <code>false</code> otherwise.
	 */
	private boolean filled = true;

	/** <code>true</code> if this area must be framed, 
	 *  <code>false</code> otherwise.
	 */
	private boolean framed = true;

	/** <code>true</code> if this area must be closed to form a polygon, 
	 *  <code>false</code> otherwise.
	 */
	private boolean closed = false;

	private DrawingMethod drawingMethod;

	/** This is the set of control points that composed
	 *  this edge.
	 */
	private final List<ControlPoint> points = new ArrayList<ControlPoint>() ;

	/** Buffered general path.
	 */
	private SoftReference<PathDetails> bufferedPath = null;

	/** Indicates the last segment that was successfully tested against
	 * a hit.
	 * This attribute is updated each time {@link #hit(float, float, float)}
	 * is invoked.
	 */
	protected int lastHitSegment = -1;

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public PolylineFigure(UUID viewUUID) {
		this(viewUUID, 0, 0);
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 */
	public PolylineFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE);
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 * @param width is the width of the figure.
	 * @param height is the height of the figure.
	 */
	public PolylineFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height);
	}

	@Override
	public void fitToContent() {
		//
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
		if (r.intersects(getBounds())) {
			return r.intersects(getPathIterator());
		}
		return false;
	}

	/** Change the closing flag.
	 *
	 * @param filled <code>true</code> if this figure
	 *               must be filled, <code>false</code> otherwise.
	 */
	public void setFilled(boolean filled) {
		if (filled!=this.filled) {
			boolean old = this.filled;
			this.filled = filled;
			firePropertyChange(PROPERTY_FILLED, old, this.filled); 
			repaint(false);
		}
	}

	/** Return the filling flag.
	 *
	 * @return <code>true</code> if this figure must
	 *         be filled, <code>false</code> otherwise.
	 */
	public boolean isFilled() {
		return this.filled;
	}

	/** Change the closing flag.
	 *
	 * @param closed <code>true</code> if this figure
	 *               must be closed, <code>false</code> otherwise.
	 */
	public void setClosed(boolean closed) {
		if (closed!=this.closed) {
			boolean old = this.closed;
			this.closed = closed;
			firePropertyChange(PROPERTY_CLOSED, old, this.closed); 
			repaint(true);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isClosed() {
		return this.closed;
	}
	
	/** Change the framing flag.
	 *
	 * @param framed <code>true</code> if this figure
	 *               must be framed, <code>false</code> otherwise.
	 */
	public void setFramed(boolean framed) {
		if (framed!=this.framed) {
			boolean old = this.framed;
			this.framed = framed;
			firePropertyChange(PROPERTY_FRAMED, old, this.framed); 
			repaint(false);
		}
	}

	/** Return the framed flag.
	 *
	 * @return <code>true</code> if this figure must
	 *         be framed, <code>false</code> otherwise.
	 */
	public boolean isFramed() {
		return this.framed;
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
	public void paint(ViewGraphics2D g) {
		paintSegments(g, getPath());
	}
	
	/**
	 * Paint the segments represented by the specified control points.
	 * 
	 * @param g is the graphical context.
	 * @param path is the path to draw.
	 */
	protected void paintSegments(ViewGraphics2D g, Path2f path) {
		g.setOutlineDrawn(isFramed());
		g.setInteriorPainted(isFilled());
		g.draw(path);
	}

	/** {@inheritDoc}
	 */
	@Override
	public int hitCtrlPoint(float x, float y, float epsilon) {
		ControlPoint p;
		for(int i=0; i<this.points.size(); ++i) { 
			p = this.points.get(i);
			if (MathUtil.distancePointToPoint(x, y, p.getX(), p.getY())<=epsilon) {
				return i;
			}
		}
		return -1;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public int hitSegment(float x, float y, float epsilon) {
		float distance = Float.POSITIVE_INFINITY;
		Point2D p1, p2;
		float d;
		int index=  -1;
		for(int i=0; i<this.points.size()-1; ++i) {
			p1 = this.getCtrlPointAt(i);
			p2 = this.getCtrlPointAt(i+1);
			d = MathUtil.distancePointToSegment(
					x, y,
					p1.getX(), p1.getY(),
					p2.getX(), p2.getY());
			if (!Float.isNaN(d) && d<=epsilon && d<distance) {
				distance = d;
				index = i;
			}
		}
		if (Float.isInfinite(distance) || Float.isNaN(distance)) {
			return -1;
		}
		return index;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hit(float x, float y, float epsilon) {
		if ( getDamagedBounds().contains(x,y) ) {
			if (isClosed()) {
				// Hit the polygon?
				Path2f path = getPath();
				assert(path!=null);
				this.lastHitSegment = -1;
				Rectangle2f mouse = new Rectangle2f();
				mouse.setFromCenter(x, y, x-epsilon, y-epsilon);
				return path.intersects(mouse);
			}
			
			// Hit a polyline?
			if (getDrawingMethod()==DrawingMethod.BEZIER_SPLINE
					|| getDrawingMethod()==DrawingMethod.QUADRATIC_SPLINE) {
				Path2f path = getPath();
				assert(path!=null);
				Iterator<PathElement2f> pathIterator = path.getPathIterator(MathConstants.SPLINE_APPROXIMATION_RATIO);
				int index = 0 ;
				
				PathElement2f pathElement;

				// Checks all segments
				while (pathIterator.hasNext() ) {
					pathElement = pathIterator.next();
					if ( ( index < 0 ) || ( index >= getCtrlPointCount() ) ) {
						index = -1 ;
					}
					else if( getCtrlPointAt(index+1).equals( 
							new Point2f( pathElement.toX, pathElement.toY ) ) ) {
						index ++ ;
					}
					switch(pathElement.type) {
					case MOVE_TO:
						break;
					case LINE_TO:
						if ( MathUtil.isPointClosedToSegment(
								pathElement.fromX, pathElement.fromY,
								pathElement.toX, pathElement.toY,
								x, y,
								epsilon ) ) {
							this.lastHitSegment = index ;
							return true ;
						}
						break;
					case CLOSE:
						if ( MathUtil.isPointClosedToSegment(
								pathElement.fromX, pathElement.fromY,
								pathElement.toX, pathElement.toY,
								x, y,
								epsilon) ) {
							this.lastHitSegment = index ;
							return true ;
						}
						break;
					default:
					}
				}
			}
			else {
				ControlPoint previous = null;
				this.lastHitSegment = 0;
				for(ControlPoint p : this.points) {
					if (previous!=null) {
						if (MathUtil.isPointClosedToSegment(
								previous.x(), previous.y(),
								p.x(), p.y(),
								x, y,
								epsilon)) {
							return true;
						}
						++this.lastHitSegment;
					}
					previous = p;
				}
			}
		}
		this.lastHitSegment = -1;
		return false;
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
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_FILLED, this.filled); 
		properties.put(PROPERTY_FRAMED, this.framed); 
		properties.put(PROPERTY_CLOSED, this.closed); 
		properties.put(PROPERTY_DRAWINGMETHOD, this.drawingMethod); 

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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_FILLINGCOLOR, Color.class); 
		properties.put(PROPERTY_LINECOLOR, Color.class); 
		properties.put(PROPERTY_FILLED, Boolean.class); 
		properties.put(PROPERTY_FRAMED, Boolean.class); 
		properties.put(PROPERTY_CLOSED, Boolean.class); 
		properties.put(PROPERTY_DRAWINGMETHOD, DrawingMethod.class); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		if (properties!=null) {
			setFilled(propGetBoolean(PROPERTY_FILLED, this.filled, properties)); 
			setFramed(propGetBoolean(PROPERTY_FRAMED, this.framed, properties)); 
			setClosed(propGetBoolean(PROPERTY_CLOSED, this.closed, properties)); 
			setDrawingMethod(propGet(DrawingMethod.class, PROPERTY_DRAWINGMETHOD, this.drawingMethod, true, properties)); 

			String ctrlpoints = propGetString(PROPERTY_CONTROLPOINTS, "", true, properties); //$NON-NLS-1$ 
			if (ctrlpoints!=null && !ctrlpoints.isEmpty()) {
				Pattern pattern = Pattern.compile("\\(([0-9+-.eE]+)\\|([0-9+-.eE]+)\\)"); //$NON-NLS-1$
				Matcher matcher = pattern.matcher(ctrlpoints);
				List<Point2D> points = new ArrayList<Point2D>();
				while (matcher.find()) {
					float x = Float.parseFloat(matcher.group(1));
					float y = Float.parseFloat(matcher.group(2));
					points.add(new Point2f(x,y));
				}
				setCtrlPoints(points);
			}
		}
		super.setProperties(properties);
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
	public void setCtrlPoints(Point2D... pathPoints) {
		this.points.clear();
		for(Point2D p : pathPoints) {
			this.points.add(new ControlPoint(p));
		}
		updateGeometry();
		repaint(true);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setCtrlPoints(Collection<? extends Point2D> pathPoints) {
		this.points.clear();
		for(Point2D p : pathPoints) {
			this.points.add(new ControlPoint(p));
		}
		updateGeometry();
		repaint(true);
	}

	/** Get the edge control points.
	 *
	 * @return control points of this edge.
	 */
	public List<? extends Point2D> getCtrlPoints() {
		return Collections.unmodifiableList(this.points) ;
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getCtrlPointCount() {
		return this.points.size() ;
	}

	/** {@inheritDoc}
	 */
	@Override
	public UnmodifiablePoint2f getCtrlPointAt( int index) {
		return this.points.get(index) ;
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

	/** {@inheritDoc}
	 */
	@Override
	public void setCtrlPointAt(int index, float x, float y) {
		ControlPoint p = this.points.get(index);
		if (x!=p.getX() || y!=p.getY()) {
			p.set(x, y);
			updateGeometry();
			repaint(true);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void insertCtrlPointAt(int index, float x, float y) {
		int idx = index;
		if (idx<0) idx = 0;
		if (idx>this.points.size()) idx = this.points.size();
		this.points.add(idx, new ControlPoint(x,y));
		updateGeometry();
		repaint(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanUp() {
		super.cleanUp();
		this.bufferedPath = null;
	}

	/** Create and replies the general path that is representing this edge figure.
	 * It is recommended to invoke {@link #getPath()} instead of this
	 * method.
	 * 
	 * @param controlPoints are the control points that are composing the path.
	 * @return the general path.
	 */
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

	/** {@inheritDoc}
	 */
	@Override
	public final Path2f getPath() {
		PathDetails p = (this.bufferedPath==null) ? null : this.bufferedPath.get();
		if (p==null) {
			p = computePath(this.points);
			this.bufferedPath = new SoftReference<PathDetails>(p);
		}
		Path2f path = p.path;
		if (isClosed()) {
			path = path.clone();
			path.closePath();
		}
		return path;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public final PathIterator2f getPathIterator() {
		return getPath().getPathIterator();
	}


	/** Calculate the bounds of the edge and its associated symbols.
	 * This function invokes:<ul>
	 * <li>{@link #cleanUp()}</li>
	 * <li>{@link #updateAssociatedGeometry()}</li>
	 * <li>{@link #setBounds(float, float, float, float)}</li>.
	 * </ul>
	 */
	protected void updateGeometry() {
		cleanUp();

		Path2f path = getPath();
		Rectangle2f bounds = path.toBoundingBox();

		if (bounds!=null)
			super.setBounds(
					bounds.getMinX(), bounds.getMinY(),
					bounds.getWidth(), bounds.getHeight());

		updateAssociatedGeometry();
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
			d = MathUtil.distancePointToSegment(
					x, y,
					p1.getX(), p1.getY(),
					p2.getX(), p2.getY());
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
			d = MathUtil.distancePointToSegment(
					x, y,
					p1.getX(), p1.getY(),
					p2.getX(), p2.getY(),
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLastHitSegment() {
		return this.lastHitSegment;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected LinearFeatureShadowPainter createShadowPainter() {
		return new SPainter();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized LinearFeatureShadowPainter getShadowPainter() {
		return (LinearFeatureShadowPainter)super.getShadowPainter();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void translate(float dx, float dy) {
		for(ControlPoint pts : this.points) {
			pts.translateCtrlPoint(dx, dy);
		}
		updateGeometry();
		repaint(true);
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

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class SPainter implements LinearFeatureShadowPainter {

		/** Controls points used to draw the moving shadow.
		 */
		private final List<ShadowedControlPoint> shadowPoints = new ArrayList<ShadowedControlPoint>();

		private PathDetails bufferedPainterPath = null;
		private Rectangle2f bufferedBounds = null;

		/**
		 */
		@SuppressWarnings("synthetic-access")
		public SPainter() {
			for(ControlPoint p : PolylineFigure.this.points) {
				this.shadowPoints.add(new ShadowedControlPoint(p));
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public UUID getUUID() {
			return PolylineFigure.this.getUUID();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Figure getFigure() {
			return PolylineFigure.this;
		}

		private Path2f getBufferedPath() {
			if (this.bufferedPainterPath==null) {
				this.bufferedPainterPath = computePath(this.shadowPoints);
			}
			Path2f path = this.bufferedPainterPath.path;
			if (isClosed()) {
				path = path.clone();
				path.closePath();
			}
			return path;
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
		
		/** 
		 * {@inheritDoc}
		 */
		@Override
		public Rectangle2f getDamagedBounds() {
			return getShadowBounds();
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
			g.pushRenderingContext(PolylineFigure.this, path, path.toBoundingBox());
			PolylineFigure.this.paintSegments(g, path);
			g.popRenderingContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public synchronized void release() {
			PolylineFigure.this.releaseShadowPainter();
			this.shadowPoints.clear();
			this.bufferedPainterPath = null;
			this.bufferedBounds = null;
		}

	}

}
