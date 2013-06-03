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

import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** A decoration figure that is drawing an ellipse.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EllipseFigure extends BlockDecorationFigure {

	private static final long serialVersionUID = -8727208834423982285L;

	/** <code>true</code> if this text area must be filled, 
	 *  <code>false</code> otherwise.
	 */
	private boolean filled = true;

	/** <code>true</code> if this text area must be framed, 
	 *  <code>false</code> otherwise.
	 */
	private boolean framed = true;

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public EllipseFigure(UUID viewUUID) {
		this(viewUUID, 0, 0);
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 */
	public EllipseFigure(UUID viewUUID, float x, float y) {
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
	public EllipseFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height);
	}
	
	@Override
	public void fitToContent() {
		Dimension prefs = getMinimalDimension();
		if (prefs.width()!=getMinimalWidth() || prefs.height()!=getMinimalHeight()) {
			setBounds(
					getX(),
					getY(),
					prefs.width(),
					prefs.height());
			avoidCollision();
		}
	}
	
	@Override
	public boolean contains(float x, float y) {
		return MathUtil.isPointInEllipse(
				x, y,
				getX(), getY(), getWidth(), getHeight());
	}
	
	@Override
	public boolean contains(Rectangle2f r) {
		return Ellipse2f.containsEllipseRectangle(
				getX(), getY(), getWidth(), getHeight(),
				r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
	}
	
	@Override
	public boolean intersects(Shape2f r) {
		return r.intersects(new Ellipse2f(getX(), getY(), getWidth(), getHeight()));
	}
	
	/** Change the filling flag.
	 *
	 * @param filled <code>true</code> if this FigText
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

	/** Return the filling flag.
	 *
	 * @return <code>true</code> if this FigText must
	 *         be filled, <code>false</code> otherwise.
	 */
	public boolean isFilled() {
		return this.filled;
	}

	/** Change the framing flag.
	 *
	 * @param framed <code>true</code> if this FigText
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
	 * @return <code>true</code> if this FigText must
	 *         be framed, <code>false</code> otherwise.
	 */
	public boolean isFramed() {
		return this.framed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(ViewGraphics2D g) {
		Rectangle2f figureBounds = g.getCurrentViewComponentBounds();
		Ellipse2f oval = new Ellipse2f(
				figureBounds.getMinX(),
				figureBounds.getMinY(),
				figureBounds.getWidth(),
				figureBounds.getHeight());
		g.setOutlineDrawn(isFramed());
		g.setInteriorPainted(isFilled());
		g.draw(oval);
	}

	/**
	 * {@inheritDoc}
	 */
	@Deprecated
	@Override
	public boolean hit(float x, float y, float epsilon) {
		Ellipse2f oval = new Ellipse2f(
				getX() - epsilon,
				getY() - epsilon,
				getWidth() + 2*epsilon,
				getHeight() + 2*epsilon);
		return oval.contains(x,  y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_FILLED, this.filled); 
		properties.put(PROPERTY_FRAMED, this.framed); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_LINECOLOR, Color.class); 
		properties.put(PROPERTY_FRAMED, Boolean.class); 
		properties.put(PROPERTY_FILLINGCOLOR, Color.class); 
		properties.put(PROPERTY_FILLED, Boolean.class); 
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
		}
		super.setProperties(properties);
	}

}
