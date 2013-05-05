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

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.vmutil.locale.Locale;

/** A decoration figure that is drawing a text.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TextFigure extends BlockDecorationFigure {

	private static final long serialVersionUID = -8727208834423982285L;

	private String text = null;

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
	public TextFigure(UUID viewUUID) {
		this(viewUUID, 0, 0);
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 */
	public TextFigure(UUID viewUUID, float x, float y) {
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
	public TextFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height);
	}

	/** Set the displayed text.
	 *
	 * @param text is the text to display.
	 */
	public void setText(String text) {
		if ((text==null && this.text!=null)
				||
				(text!=null && !text.equals(this.text))) {
			String old = this.text;
			this.text = text;
			firePropertyChange(PROPERTY_TEXT, old, this.text); 
			repaint(true);
		}
	}

	/** Replies the text.
	 *
	 * @return the text
	 */
	public String getText() {
		return this.text;
	}

	/** Replies the displayed text.
	 *
	 * @return the text or the empty-strign message
	 * if these is no associated text.
	 */
	public String getDisplayableText() {
		String s = getText();
		if (s==null || s.isEmpty()) {
			s = Locale.getString("EMPTY_TEXT"); //$NON-NLS-1$
		}
		return s;
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
		boolean isFramed = isFramed();
		boolean isFilled = isFilled();
		String text = getDisplayableText();
		if (isFramed || isFilled || (text!=null && !text.isEmpty())) {
			g.setOutlineDrawn(isFramed());
			g.setInteriorPainted(isFilled());
			g.setInteriorText(getDisplayableText());
			g.draw(g.getCurrentViewComponentBounds());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hit(float x, float y, float epsilon) {
		Rectangle2f bounds = getBounds();
		Rectangle2f rr = new Rectangle2f(
				bounds.getMinX() - epsilon,
				bounds.getMinY() - epsilon,
				bounds.getWidth() + 2*epsilon,
				bounds.getHeight() + 2*epsilon);
		return rr.contains(x,  y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		if (this.text==null || this.text.isEmpty())
			properties.put(PROPERTY_TEXT, null);
		else
			properties.put(PROPERTY_TEXT, this.text);
		properties.put(PROPERTY_FRAMED, this.framed); 
		properties.put(PROPERTY_FILLED, this.filled); 
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
		properties.put(PROPERTY_TEXT, String.class); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setText(propGetString(PROPERTY_TEXT, this.text, false, properties)); 
			setFramed(propGetBoolean(PROPERTY_FRAMED, this.framed, properties)); 
			setFilled(propGetBoolean(PROPERTY_FILLED, this.filled, properties)); 
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String n = getText();
		if (n!=null && !n.isEmpty()) return n;
		return super.toString();
	}

	/** {@inheritDoc}
	 */
	@Override
	public void fitToContent() {
		float w, h;
		String n = getText();
		if (n!=null && !n.isEmpty()) {
			Font font = VectorToolkit.font();
			Rectangle2f r = font.getStringBounds(n);
			w = r.getWidth();
			h = r.getHeight();
		}
		else {
			w = getMinimalWidth();
			h = getMinimalHeight();
		}
		setSize(w, h);
	}

}
