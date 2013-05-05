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
package org.arakhne.neteditor.fig.graphics;

import java.net.URL;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.StringAnchor;
import org.arakhne.afc.ui.TextAlignment;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Composite;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.FontMetrics;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.ImageObserver;
import org.arakhne.afc.ui.vector.Paint;
import org.arakhne.afc.ui.vector.Stroke;
import org.arakhne.neteditor.fig.figure.Figure;

/** Implementation of a graphics context which is
 * delagating to another graphics context but
 * by forcing the shadow drawing parameters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ShadowViewGraphics2D implements ViewGraphics2D {

	private final ViewGraphics2D delegate;
	
	private final Color shadowColor;
	
	/** Create a Graphics that is drawing the shadows with
	 * a color darker than the current background}.
	 * 
	 * @param context
	 */
	public ShadowViewGraphics2D(ViewGraphics2D context) {
		this.delegate = context;
		Color c = getBackground();
		this.shadowColor = c.darkerColor();
		resetGraphics();
	}
	
	/** Create a Graphics that is drawing the shadows with
	 * the color specified by the parameter <var>shadowColor</var>.
	 * 
	 * @param context
	 * @param shadowColor is the color to use to draw the shadow.
	 */
	public ShadowViewGraphics2D(ViewGraphics2D context, Color shadowColor) {
		this.delegate = context;
		this.shadowColor = shadowColor;
		resetGraphics();
	}
	
	@Override
	public float getShadowTranslationX() {
		return 0;
	}
	
	@Override
	public float getShadowTranslationY() {
		return 0;
	}
	
	@Override
	public void dispose() {
		resetGraphics();
	}
	
	@Override
	public void reset() {
		resetGraphics();
	}
	
	private void resetGraphics() {
		this.delegate.setOutlineColor(this.shadowColor);
		this.delegate.setFillColor(this.shadowColor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Graphics2DLOD getLOD() {
		return Graphics2DLOD.LOW_LEVEL_OF_DETAIL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringAnchor getStringAnchor() {
		return this.delegate.getStringAnchor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drawPoint(float x, float y) {
		this.delegate.drawPoint(x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Font getFont() {
		return this.delegate.getFont();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Font getDefaultFont() {
		return this.delegate.getDefaultFont();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFont(Font font) {
		this.delegate.setFont(font);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FontMetrics getFontMetrics() {
		return this.delegate.getFontMetrics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FontMetrics getFontMetrics(Font f) {
		return this.delegate.getFontMetrics(f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Shape2f getClip() {
		return this.delegate.getClip();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClip(Shape2f clip) {
		this.delegate.setClip(clip);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clip(Shape2f clip) {
		this.delegate.clip(clip);
	}

	@Override
	public void drawDefaultImage(float dx1, float dy1, float dx2, float dy2) {
		Rectangle2f imageBounds = new Rectangle2f();
		imageBounds.setFromCorners(dx1, dy1, dx2, dy2);
		this.delegate.setInteriorPainted(true);
		this.delegate.draw(imageBounds);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean drawImage(URL imageURL, Image img, float dx1, float dy1,
			float dx2, float dy2, int sx1, int sy1, int sx2, int sy2) {
		Rectangle2f imageBounds = new Rectangle2f();
		imageBounds.setFromCorners(dx1, dy1, dx2, dy2);
		this.delegate.setInteriorPainted(true);
		this.delegate.draw(imageBounds);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean drawImage(URL imageURL, Image img, float dx1, float dy1,
			float dx2, float dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		Rectangle2f imageBounds = new Rectangle2f();
		imageBounds.setFromCorners(dx1, dy1, dx2, dy2);
		this.delegate.setInteriorPainted(true);
		this.delegate.draw(imageBounds);
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void draw(Shape2f s) {
		this.delegate.draw(s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drawString(String str, float x, float y) {
		this.delegate.drawString(str, x, y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drawString(String str, float x, float y, Shape2f clip) {
		this.delegate.drawString(str, x, y, clip);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void transform(Transform2D Tx) {
		this.delegate.transform(Tx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transform2D setTransform(Transform2D Tx) {
		return this.delegate.setTransform(Tx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transform2D getTransform() {
		return this.delegate.getTransform();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBackground(Color color) {
		this.delegate.setBackground(color);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getBackground() {
		return this.delegate.getBackground();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Paint setPaint(Paint paint) {
		return this.delegate.setPaint(paint);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color setFillColor(Color color) {
		return color;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color setOutlineColor(Color color) {
		return color;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Paint getPaint() {
		return this.delegate.getPaint();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComposite(Composite composite) {
		this.delegate.setComposite(composite);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Composite getComposite() {
		return this.delegate.getComposite();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStroke(Stroke stroke) {
		this.delegate.setStroke(stroke);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stroke getStroke() {
		return this.delegate.getStroke();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getOutlineColor() {
		return this.delegate.getOutlineColor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getFillColor() {
		return this.delegate.getFillColor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLocked() {
		return this.delegate.isLocked();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle2f getCurrentViewComponentBounds() {
		return this.delegate.getCurrentViewComponentBounds();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Shape2f getCurrentViewComponentShape() {
		return this.delegate.getCurrentViewComponentShape();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Transform2D localTransformation) {
		this.delegate.pushRenderingContext(component, localTransformation);
		resetGraphics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape, Rectangle2f bounds) {
		this.delegate.pushRenderingContext(component, viewShape, bounds);
		resetGraphics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape, Rectangle2f bounds, Color fillColor, Color lineColor, Transform2D localTransformation) {
		this.delegate.pushRenderingContext(component, viewShape, bounds, fillColor, lineColor, localTransformation);
		resetGraphics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor) {
		this.delegate.pushRenderingContext(component, viewShape, bounds, fillColor, lineColor);
		resetGraphics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Color fillColor,
			Color lineColor) {
		this.delegate.pushRenderingContext(component, fillColor, lineColor);
		resetGraphics();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void popRenderingContext() {
		this.delegate.popRenderingContext();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beginGroup() {
		this.delegate.beginGroup();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endGroup() {
		this.delegate.endGroup();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isInteriorPainted() {
		return this.delegate.isInteriorPainted();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInteriorPainted(boolean painted) {
		this.delegate.setInteriorPainted(painted);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isOutlineDrawn() {
		return this.delegate.isOutlineDrawn();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOutlineDrawn(boolean outlined) {
		this.delegate.setOutlineDrawn(outlined);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getInteriorText() {
		return this.delegate.getInteriorText();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setInteriorText(String interiorText) {
		this.delegate.setInteriorText(interiorText);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D computeTextPosition(String text, Rectangle2f bounds,
			TextAlignment halign, TextAlignment valign) {
		return this.delegate.computeTextPosition(text, bounds, halign, valign);
	}

	@Override
	public void clear(Shape2f s) {
		this.delegate.clear(s);
	}

	@Override
	public void translate(float tx, float ty) {
		this.delegate.translate(tx, ty);
	}

	@Override
	public void scale(float sx, float sy) {
		this.delegate.scale(sx, sy);
	}

	@Override
	public void rotate(float theta) {
		this.delegate.rotate(theta);
	}

	@Override
	public void shear(float shx, float shy) {
		this.delegate.shear(shx, shy);
	}
	
}
