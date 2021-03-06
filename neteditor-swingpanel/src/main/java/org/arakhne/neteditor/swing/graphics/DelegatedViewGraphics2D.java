/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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
package org.arakhne.neteditor.swing.graphics;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.DelegatedVectorGraphics2D;
import org.arakhne.afc.ui.vector.Paint;
import org.arakhne.afc.ui.vector.Stroke;
import org.arakhne.afc.ui.vector.VectorGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** This is the implementation of a ViewGraphics2D which is
 * delegating to an existing {@link VectorGraphics2D}.
 * This implementation does not support zoom.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DelegatedViewGraphics2D extends DelegatedVectorGraphics2D<VectorGraphics2D> implements ViewGraphics2D {

	private final LinkedList<GContext> stack = new LinkedList<GContext>();

	/**
	 * @param context
	 */
	public DelegatedViewGraphics2D(VectorGraphics2D context) {
		super(context);
	}
	
	@Override
	public boolean isShadowDrawing() {
		return false;
	}

	@Override
	public float getShadowTranslationX() {
		return 0;
	}

	@Override
	public float getShadowTranslationY() {
		return 0;
	}
	
	/** Replies the top figure.
	 * 
	 * @return the top figure.
	 */
	protected Figure getTopFigure() {
		if (!this.stack.isEmpty()) {
			for(GContext c : this.stack) {
				if (c.component!=null) return c.component;
			}
		}
		return null;
	}

	@Override
	public boolean isLocked() {
		Figure fig = getTopFigure();
		if (fig==null)
			return false;
		return fig.isLocked();
	}

	@Override
	public Rectangle2f getCurrentViewComponentBounds() {
		if (!this.stack.isEmpty()) {
			for(GContext c : this.stack) {
				if (c.bounds!=null) return c.bounds;
			}
		}
		throw new NoSuchElementException();
	}

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

	@Override
	public void pushRenderingContext(Figure component, Transform2D localTransformation) {
		pushRenderingContext(component, null, null, null, null, localTransformation);
	}

	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds) {
		pushRenderingContext(component, viewShape, bounds, null, null, null);
	}

	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor) {
		pushRenderingContext(component, viewShape, bounds, fillColor, lineColor, null);
	}

	@Override
	public void pushRenderingContext(Figure component, Color fillColor,
			Color lineColor) {
		pushRenderingContext(component, null, null, fillColor, lineColor, null);
	}

	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor,
			Transform2D localTransformation) {
		Transform2D oldTrans = this.delegate.getTransform();
		
		if (localTransformation!=null) {
			this.delegate.setTransform(localTransformation);
		}

		Color ofc = getFillColor();
		Color fc = null;
		if (fillColor!=null) {
			fc = fillColor;
		}
		else if (component.isLocked()) {
			fc = component.getLockFillColor();
		}
		else {
			fc = component.getFillColor();
		}
		if (fc!=null) setFillColor(fc);

		Color olc = getOutlineColor();
		Color lc = null;
		if (lineColor!=null) {
			lc = lineColor;
		}
		else if (component.isLocked()) {
			lc = component.getLockOutlineColor();
		}
		else {
			lc = component.getLineColor();
		}
		if (lc!=null) setOutlineColor(lc);

		this.stack.push(new GContext(
				component, viewShape, bounds,
				ofc, olc,
				this.delegate.getStroke(),
				this.delegate.getPaint(),
				isInteriorPainted(),
				isOutlineDrawn(),
				oldTrans));
	}

	@Override
	public void popRenderingContext() {
		if (!this.stack.isEmpty()) {
			GContext c = this.stack.pop();
			assert(c!=null);
			if (c.fillColor!=null) {
				setFillColor(c.fillColor);
			}
			if (c.lineColor!=null) {
				setOutlineColor(c.lineColor);
			}
			if (c.stroke!=null) {
				this.delegate.setStroke(c.stroke);
			}
			if (c.paint!=null) {
				this.delegate.setPaint(c.paint);
			}
			if (c.transform!=null) {
				this.delegate.setTransform(c.transform);
			}
			setOutlineDrawn(c.isOutlined);
			setInteriorPainted(c.isFilled);
		}
		else {
			this.delegate.reset();
		}
	}

	@Override
	public void beginGroup() {
		//
	}

	@Override
	public void endGroup() {
		//
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class GContext {

		public final Figure component;
		public final Shape2f shape;
		public final Rectangle2f bounds;
		public final Color fillColor;
		public final Color lineColor;
		public final Stroke stroke;
		public final Paint paint;
		public final boolean isFilled;
		public final boolean isOutlined;
		public final Transform2D transform;

		/**
		 * @param component
		 * @param shape
		 * @param bounds
		 * @param fillColor
		 * @param lineColor
		 * @param stroke
		 * @param paint
		 * @param isFilled
		 * @param isOutlined
		 * @param transform
		 */
		public GContext(Figure component, Shape2f shape, Rectangle2f bounds,
				Color fillColor, Color lineColor,
				Stroke stroke, Paint paint,
				boolean isFilled, boolean isOutlined,
				Transform2D transform) {
			this.component = component;
			this.shape = shape;
			this.bounds = bounds;
			this.fillColor = fillColor;
			this.lineColor = lineColor;
			this.stroke = stroke;
			this.paint = paint;
			this.isFilled = isFilled;
			this.isOutlined = isOutlined;
			this.transform = transform;
		}

	}

}
