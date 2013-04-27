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
package org.arakhne.neteditor.fig.figure.edge.symbol;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.TreeMap;

import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.PropertyNames;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.AbstractPropertyTooler;
import org.arakhne.neteditor.formalism.Anchor;

/** This class is the base class for the symbols that will
 *  be paint at the ends of edges.
 *  <p>
 *  The symbol is associated to an edge and attached to
 *  a {@link Anchor} at a given point.
 *  In other words, the attach position is the point where
 *  the symbol will be connect to a port.
 *    
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class EdgeSymbol extends AbstractPropertyTooler implements PropertyNames {
	
	private static final long serialVersionUID = 3798013358094775638L;

	/** The instance of the 
	 *  {@link EdgeFigure}
	 *	that contains this symbol.
	 */
	private WeakReference<EdgeFigure<?>> edge = null;

	/** The angle with which the sumbol was painted.
	 */
	private float angle = Float.NaN;

	/** The position of the end of the edge.
	 */
	private float x;
	
	/** The position of the end of the edge.
	 */
	private float y;

	/** The bounds.
	 */
	private transient SoftReference<Rectangle2f> bounds = null;

	/** The shape.
	 */
	private transient SoftReference<Shape2f> shape = null;
	
	/** Construct a new Symbol.
	 *
	 * @param edge the edge that contains this arrow.
	 */
	public EdgeSymbol(EdgeFigure<?> edge) {
		setEdge( edge );
	}

	/** Construct a new Symbol.
	 */
	public EdgeSymbol() {
		//
	}

	/** Set the edge that contains this symbol.
	 *
	 * @param edge the edge that contains this symbol.
	 */
	public void setEdge(EdgeFigure<?> edge) {
		if (getEdge()!=edge) {
			if (edge==null)
				this.edge = null;
			else
				this.edge = new WeakReference<EdgeFigure<?>>(edge);
			this.bounds = null;
			this.shape = null;
		}
	}

	/** Set the edge that contains this symbol.
	 *
	 * @return the edge that contains this symbol.
	 */
	public EdgeFigure<?> getEdge() {
		return this.edge==null ? null : this.edge.get();
	}

	/** Return the rectangle in which the symbol is.
	 *
	 * @return a rectangle in which the symbol is.
	 */
	public Rectangle2f getBounds() {
		Rectangle2f b = (this.bounds==null) ? null : this.bounds.get();
		if (b==null) {
			b = getShape().toBoundingBox();
			this.bounds = new SoftReference<Rectangle2f>(b);
		}
		return b;
	}

	/** Change the attach point and the
	 * the rotating angle from the given 
	 *  edge coordinates.
	 *
	 * @param x_begin x coordinate from the edge point
	 *                where the symbol is.
	 * @param y_begin y coordinate from the edge point
	 *                where the symbol is.
	 * @param x_end x coordinate from the edge point
	 *              where the symbol is not.
	 * @param y_end y coordinate from the edge point
	 *              where the symbol is not.
	 */
	public final void setGeometry( float x_begin, float y_begin, 
			float x_end, float y_end ) {
		this.angle = MathUtil.angleOfVector(x_begin, y_begin, x_end, y_end);
		this.x = x_begin;
		this.y = y_begin;
		this.bounds = null;
		this.shape = null;
	}

	/** Reply <code>true</code> if the given Point
	 *  is in this symbol.
	 *
	 * @param x is the coordinate that may be contained in this symbol.
	 * @param y is the coordinate that may be contained in this symbol.
	 * @return <code>true</code> if <var>p</var> is in this symbol,
	 *         <code>false</code> otherwise.
	 * @see #hit(float, float, float)
	 */
	public boolean contains(float x, float y) {
		return getShape().contains(x, y);
	}

	/** Reply <code>true</code> if given coords may correspond
	 * to a position of the mouse cursor that permits to
	 * hit this figure.
	 * This function should take into account the shape of 
	 * the figure, and not only the bounds as for {@link #contains(float, float)}
	 *
	 * @param x horizontal coord.
	 * @param y vertical coord.
	 * @param epsilon is the allowed error when testing the hits.
	 * @return <code>true</code> if the point 
	 *         (<var>x</var>,<var>y</var>) is in this Fig.
	 * @see #contains(float, float)
	 */
	public boolean hit(float x, float y, float epsilon) {
		Shape2f shape = getShape();
		return shape.intersects(new Ellipse2f(x-epsilon, y-epsilon, 2f*epsilon, 2f*epsilon));
	}

	/** Paint the arrow.
	 *
	 * @param g the graphical context in which 
	 *          the arrow must be paint.
	 */
	public final void paint(ViewGraphics2D g) {
		EdgeFigure<?> edge = getEdge();
		if ( edge!=null && !Double.isNaN(this.angle)) {
			Shape2f shape = getShape();
			paintSymbol(g, shape);
		}
	}
	
	private Shape2f getShape() {
		Shape2f s = (this.shape==null) ? null : this.shape.get();
		if (s==null) {
			s = getSymbolShape(this.x, this.y, this.angle);
			this.shape = new SoftReference<Shape2f>(s);
		}
		return s;
	}
	
	/** Invoked to draw the property symbol.
	 * 
	 * @param g is the graphics context.
	 * @param shape is the symbol shape.
	 */
	protected abstract void paintSymbol(ViewGraphics2D g, Shape2f shape);
	
	/** Compute and replies the shape of the symbol.
	 * 
	 * @param x is the position of the symbol anchor.
	 * @param y is the position of the symbol anchor.
	 * @param angle is the angle of the symbol.
	 * @return the shape.
	 */
	protected abstract Shape2f getSymbolShape(float x, float y, float angle);

	/** Reply the color of lines.
	 *  By default this method take the color
	 *  from the edge.
	 *
	 * @return the color of the lines.
	 */
	public Color getLineColor() {
		EdgeFigure<?> edge = getEdge();
		if (edge!=null) return edge.getLineColor();
		return ViewComponentConstants.DEFAULT_LINE_COLOR;
	}

	/** Reply the color of filling area.
	 *  By default this method take the color
	 *  from the edge.
	 *
	 * @return the color of the filled areas.
	 */
	public Color getFillingColor() {
		EdgeFigure<?> edge = getEdge();
		if (edge!=null) return edge.getFillColor();
		return ViewComponentConstants.DEFAULT_FILL_COLOR;
	}

	/** Replies the properties of this component.
	 * 
	 * @return the properties, never <code>null</code>.
	 */
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = new TreeMap<String, Object>();
		properties.put(PROPERTY_ANGLE, this.angle); 
		properties.put(PROPERTY_X, this.x); 
		properties.put(PROPERTY_Y, this.y); 
		return properties;
	}
	
	/** Replies the names and the types of all the 
	 * properties that are understood by this component.
	 * 
	 * @return the properties' specification, never <code>null</code>.
	 */
	@SuppressWarnings("static-method")
	public Map<String,Class<?>> getSupportedProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_ANGLE, Float.class); 
		properties.put(PROPERTY_X, Float.class); 
		properties.put(PROPERTY_Y, Float.class); 
		return properties;
	}

	/** Set the properties of the model object, except the UUID.
	 * 
	 * @param properties are the properties of this model object, except the UUID.
	 * @throws IOException
	 */
	public void setProperties(Map<String,Object> properties) throws IOException {
		this.angle = propGetFloat(PROPERTY_ANGLE, this.angle, properties); 
		this.x = propGetFloat(PROPERTY_X, this.x, properties); 
		this.y = propGetFloat(PROPERTY_Y, this.y, properties); 
	}

}
