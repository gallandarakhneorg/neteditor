/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io ;

import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.continous.object2d.Segment2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Tuple2D;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.StringAnchor;
import org.arakhne.afc.ui.vector.AbstractVectorGraphics2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Colors;
import org.arakhne.afc.ui.vector.Composite;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.FontMetrics;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.Stroke;
import org.arakhne.afc.ui.vector.VectorGraphics2D;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;

/** Abstract implementation of a {@link VectorGraphics2D}
 * dedicated to vector exporters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractVectorialExporterGraphics2D extends AbstractVectorGraphics2D implements ViewGraphics2D {

	/** Is the current transformation.
	 */
	protected final Transform2D currentTransform = new Transform2D();
	
	private final LinkedList<GContext> stack = new LinkedList<GContext>();

	private Color background = Colors.WHITE;
	private Font font;
	private Stroke stroke = VectorToolkit.stroke(1f);
	private Composite composite = VectorToolkit.composite(1.0f);
	private Shape2f clip = null;
		
	/**
	 */
	public AbstractVectorialExporterGraphics2D() {
		this.font = VectorToolkit.font();
	}
	
	@Override
	public void dispose() {
		super.dispose();
		this.stack.clear();
		this.background = null;
		this.font = null;
		this.stroke = null;
		this.composite = null;
		this.clip = null;
	}
	
	@Override
	public void reset() {
		super.reset();
		this.stack.clear();
		this.currentTransform.setIdentity();
		this.background = Colors.WHITE;
		this.font = VectorToolkit.font();
		this.stroke = VectorToolkit.stroke(1f);
		this.composite = VectorToolkit.composite(1.0f);
		this.clip = null;
	}

	/** Replies the coordinates after the application of the current
	 * transformation on the given coordinates.
	 * 
	 * @param x
	 * @param y
	 * @return the transformed coordinates.
	 */
	protected Point2D applyTransform(float x, float y) {
		Point2D p = new Point2f(x, y);
		this.currentTransform.transform(p);
		return p;
	}
	
	/** Replies the coordinates after the application of the current
	 * transformation on the given coordinates.
	 * 
	 * @param t
	 */
	protected void applyTransform(Tuple2D<?> t) {
		this.currentTransform.transform(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getFillColor() {
		Color c = super.getFillColor();
		if (c==null) return ViewComponentConstants.DEFAULT_FILL_COLOR;
		return c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getOutlineColor() {
		Color c = super.getOutlineColor();
		if (c==null) return ViewComponentConstants.DEFAULT_LINE_COLOR;
		return c;
	}
	
	/** Invoked to export the prolog for the target format.
	 * This function should be overridden by subclasses
	 * to export a prolog into the target document.
	 * 
	 * @throws IOException
	 */
	public void prolog() throws IOException {
		//
	}

	/** Invoked to export the epilog for the target format.
	 * This function should be overridden by subclasses
	 * to export a epilog into the target document.
	 * @throws IOException
	 */
	public void epilog() throws IOException {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Graphics2DLOD getLOD() {
		return Graphics2DLOD.HIGH_LEVEL_OF_DETAIL;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringAnchor getStringAnchor() {
		return StringAnchor.LEFT_BASELINE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Font getFont() {
		return this.font;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFont(Font font) {
		Font f = (font==null) ? VectorToolkit.font() : font;
		if (!f.equals(this.font)) {
			this.font = f;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FontMetrics getFontMetrics() {
		return new VectorFontMetrics(this.font);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public FontMetrics getFontMetrics(Font f) {
		return new VectorFontMetrics(f);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Shape2f getClip() {
		return this.clip;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setClip(Shape2f clip) {
		this.clip = clip;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clip(Shape2f clip) {
		if (this.clip==null) { 
			this.clip = clip;
		}
		else {
			this.clip = clip.toBoundingBox().createUnion(this.clip.toBoundingBox());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void transform(Transform2D Tx) {
		this.currentTransform.mul(Tx);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void translate(float tx, float ty) {
		Transform2D t = new Transform2D();
		t.makeTranslationMatrix(tx, ty);
		transform(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void scale(float sx, float sy) {
		Transform2D t = new Transform2D();
		t.makeScaleMatrix(sx, sy);
		transform(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void rotate(float theta) {
		Transform2D t = new Transform2D();
		t.makeRotationMatrix(theta);
		transform(t);
	}

	/**
	 * {@inheritDoc}
	 */	
	@Override
	public final void shear(float shx, float shy) {
		Transform2D t = new Transform2D();
		t.makeShearMatrix(shx, shy);
		transform(t);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transform2D setTransform(Transform2D Tx) {
		Transform2D old = Tx.clone();
		this.currentTransform.set(Tx);
		return old;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transform2D getTransform() {
		return this.currentTransform.clone();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBackground(Color color) {
		this.background = color;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getBackground() {
		return this.background;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setComposite(Composite composite) {
		if (composite!=null)
			this.composite = composite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Composite getComposite() {
		return this.composite;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStroke(Stroke stroke) {
		if (stroke!=null) {
			this.stroke = stroke;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Stroke getStroke() {
		return this.stroke;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLocked() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle2f getCurrentViewComponentBounds() {
		if (!this.stack.isEmpty()) {
			for(GContext c : this.stack) {
				if (c.bounds!=null) return c.bounds;
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Shape2f getCurrentViewComponentShape() {
		if (!this.stack.isEmpty()) {
			for(GContext c : this.stack) {
				if (c.bounds!=null && c.shape==null) return c.bounds;
				if (c.shape!=null) return c.shape;
			}
		}
		throw new NoSuchElementException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void pushRenderingContext(Figure component, Transform2D localTransformation) {
		pushRenderingContext(component, null, null, null, null, localTransformation);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds) {
		pushRenderingContext(component, viewShape, bounds, null, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor) {
		pushRenderingContext(component, viewShape, bounds, fillColor, lineColor, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void pushRenderingContext(Figure component, Color fillColor,
			Color lineColor) {
		pushRenderingContext(component, null, null, null, null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor,
			Transform2D localTransformation) {
		Transform2D trans;
		if (localTransformation!=null) {
			trans = this.currentTransform.clone();
			setTransform(localTransformation);
		}
		else {
			trans = null;
		}
		
		Color ofc = getFillColor();
		Color fc = null;
		if (fillColor!=null) {
			fc = fillColor;
		}
		else if (component!=null) {
			if (component.isLocked()) {
				fc = component.getLockFillColor();
			}
			else {
				fc = component.getFillColor();
			}
		}
		if (fc!=null) setFillColor(fc);

		Color olc = getOutlineColor();
		Color lc = null;
		if (lineColor!=null) {
			lc = lineColor;
		}
		else if (component!=null) {
			if (component.isLocked()) {
				lc = component.getLockOutlineColor();
			}
			else {
				lc = component.getLineColor();
			}
		}
		if (lc!=null) setOutlineColor(lc);
		
		this.stack.push(new GContext(viewShape, bounds, trans, ofc, olc, getFont()));
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void popRenderingContext() {
		if (!this.stack.isEmpty()) {
			GContext c = this.stack.pop();
			assert(c!=null);
			if (c.transformation!=null) {
				setTransform(c.transformation);
			}
			if (c.fillColor!=null) {
				setFillColor(c.fillColor);
			}
			if (c.lineColor!=null) {
				setOutlineColor(c.lineColor);
			}
			Font font = c.font;
			if (font!=null) {
				setFont(font);
			}
		}
	}
	
	/** Replies the bounds in the root context.
	 * 
	 * @return the bounds in the root context.
	 */
	protected Rectangle2f getRootBounds() {
		if (!this.stack.isEmpty()) {
			GContext context = this.stack.getLast();
			if (context!=null) {
				return context.bounds;
			}
		}
		return null;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean drawImage(URL imageURL, Image img, float dx1, float dy1,
			float dx2, float dy2, int sx1, int sy1, int sx2, int sy2) {
		return drawImage(imageURL, img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, null);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drawPoint(float x, float y) {
		draw(new Rectangle2f(x-.5f, y-.5f, 1f, 1f));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void draw(Shape2f s) {
		if (s instanceof Rectangle2f) {
			drawRectangle((Rectangle2f)s);
		}
		else if (s instanceof RoundRectangle2f) {
			drawRoundRectangle((RoundRectangle2f)s);
		}
		else if (s instanceof Circle2f) {
			drawCircle((Circle2f)s);
		}
		else if (s instanceof Ellipse2f) {
			drawEllipse((Ellipse2f)s);
		}
		else if (s instanceof Segment2f) {
			drawLine((Segment2f)s);
		}
		else {
			drawPath(s.getPathIterator());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void clear(Shape2f s) {
		Color fillColor = getFillColor();
		boolean isFilled = isInteriorPainted();
		boolean isOutlined = isOutlineDrawn();
		setInteriorPainted(true);
		setOutlineDrawn(false);
		draw(s);
		setFillColor(fillColor);
		setInteriorPainted(isFilled);
		setOutlineDrawn(isOutlined);
	}

	/** Draw a path.
	 * 
	 * @param path
	 */
	protected abstract void drawPath(PathIterator2f path);

	/** Draw an ellipse.
	 * 
	 * @param ellipse
	 */
	protected abstract void drawEllipse(Ellipse2f ellipse);

	/** Draw a line.
	 * 
	 * @param line
	 */
	protected abstract void drawLine(Segment2f line);
	
	/** Draw a rectangle.
	 * 
	 * @param rectangle
	 */
	protected abstract void drawRectangle(Rectangle2f rectangle);

	/** Draw a round rectangle.
	 * 
	 * @param rectangle
	 */
	protected abstract void drawRoundRectangle(RoundRectangle2f rectangle);

	/** Draw a circle.
	 * 
	 * @param circle
	 */
	protected abstract void drawCircle(Circle2f circle);

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class VectorFontMetrics implements FontMetrics {

		private final FontMetrics fm;
		
		/**
		 * @param f
		 */
		public VectorFontMetrics(Font f) {
			this.fm = VectorToolkit.fontMetrics(f);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public float stringWidth(String str) {
			Rectangle2f r = getFont().getStringBounds(str);
			return r.getWidth();
		}

		@Override
		public Font getFont() {
			return this.fm.getFont();
		}

		@Override
		public float getLeading() {
			return this.fm.getLeading();
		}

		@Override
		public float getAscent() {
			return this.fm.getAscent();
		}

		@Override
		public float getDescent() {
			return this.fm.getDescent();
		}

		@Override
		public float getHeight() {
			return this.fm.getHeight();
		}

		@Override
		public float getMaxAscent() {
			return this.fm.getMaxAscent();
		}

		@Override
		public float getMaxDescent() {
			return this.fm.getMaxDescent();
		}

		@Override
		public float getMaxAdvance() {
			return this.fm.getMaxAdvance();
		}

		@Override
		public Rectangle2f getMaxCharBounds() {
			return this.fm.getMaxCharBounds();
		}
		
	}
	
	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class GContext {

		public final Shape2f shape;
		public final Rectangle2f bounds;
		public final Transform2D transformation;
		public final Color fillColor;
		public final Color lineColor;
		public final Font font; 
		
		/**
		 * @param shape
		 * @param bounds
		 * @param transformation
		 * @param fillColor
		 * @param lineColor
		 * @param font
		 */
		public GContext(Shape2f shape, Rectangle2f bounds, Transform2D transformation,
				Color fillColor, Color lineColor, Font font) {
			this.shape = shape;
			this.bounds = bounds;
			this.transformation = transformation;
			this.fillColor = fillColor;
			this.lineColor = lineColor;
			this.font = font;
		}

	}

}
