/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.CenteringTransform;
import org.arakhne.afc.ui.swing.zoom.ZoomableGraphics2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;

/**
 * This is the swing-based implementation of a ViewGraphics2D.
 * This implementation support zooms.
 *  
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SwingViewGraphics2D extends ZoomableGraphics2D implements ViewGraphics2D {

	private final LinkedList<GContext> stack = new LinkedList<GContext>();

	private transient Rectangle2f currentViewComponentBounds = null;
	private transient Shape2f currentViewComponentShape = null;
	private transient Figure topFigure = null;
	
	/**
	 * @param canvas
	 * @param scaleFactor is the scaling factor to apply.
	 * @param centeringTransform is the transformation used to draw the objects at the center of the view.
	 * @param background is the background color.
	 * @param isAntiAlias indicates if antialiasing is used when drawing.
	 * @param scalingSensitivity is the sensitivity of the scaling actions.
	 * @param focusX is the coordinate of the focus point.
	 * @param focusY is the coordinate of the focus point.
	 * @param minScaleFactor is the minimal allowed scaling factor.
	 * @param maxScaleFactor is the maximal allowed scaling factor.
	 */
	public SwingViewGraphics2D(
			Graphics2D canvas,
			float scaleFactor,
			CenteringTransform centeringTransform,
			Color background,
			boolean isAntiAlias,
			float scalingSensitivity,
			float focusX,
			float focusY,
			float minScaleFactor,
			float maxScaleFactor) {
		super(	canvas,
				ViewComponentConstants.DEFAULT_FILL_COLOR,
				ViewComponentConstants.DEFAULT_LINE_COLOR,
				scaleFactor,
				centeringTransform,
				background,
				isAntiAlias,
				scalingSensitivity,
				focusX,
				focusY,
				minScaleFactor,
				maxScaleFactor);
	}

	@Override
	public void dispose() {
		this.stack.clear();
		this.currentViewComponentBounds = null;
		this.currentViewComponentShape = null;
		this.topFigure = null;
		super.dispose();
	}

	@Override
	public void reset() {
		this.stack.clear();
		this.currentViewComponentBounds = null;
		this.currentViewComponentShape = null;
		this.topFigure = null;
		super.reset();
	}
	
	@Override
	public boolean isShadowDrawing() {
		return false;
	}

	@Override
	public float getShadowTranslationX() {
		return pixel2logical_size(ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_X);
	}

	@Override
	public float getShadowTranslationY() {
		return pixel2logical_size(ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_Y);
	}

	@Override
	public boolean isLocked() {
		Figure fig = getTopFigure();
		if (fig==null)
			return false;
		return fig.isLocked();
	}

	/** Replies the top figure.
	 * 
	 * @return the top figure.
	 */
	protected Figure getTopFigure() {
		if (this.topFigure==null)
			throw new NoSuchElementException();
		return this.topFigure;
	}

	@Override
	public Rectangle2f getCurrentViewComponentBounds() {
		if (this.currentViewComponentBounds==null)
			throw new NoSuchElementException();
		return this.currentViewComponentBounds;
	}

	@Override
	public Shape2f getCurrentViewComponentShape() {
		if (this.currentViewComponentShape==null)
			throw new NoSuchElementException();
		return this.currentViewComponentShape;
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
		AffineTransform oldTrans = this.canvas.getTransform();
		
		if (localTransformation!=null) {
			this.canvas.setTransform(convertTransformation(localTransformation, this.scale));
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
		
		Rectangle2f oldBounds = this.currentViewComponentBounds;
		if (bounds!=null)
			this.currentViewComponentBounds = bounds;
		
		Shape2f oldShape = this.currentViewComponentShape;
		if (viewShape!=null)
			this.currentViewComponentShape = viewShape;
		else if (bounds!=null)
			this.currentViewComponentShape = bounds;
		
		Figure oldFigure = this.topFigure;
		if (component!=null)
			this.topFigure = component;

		this.stack.push(new GContext(
				oldFigure, oldShape, oldBounds,
				ofc, olc,
				this.canvas.getStroke(),
				this.canvas.getPaint(),
				isInteriorPainted(),
				isOutlineDrawn(),
				oldTrans));
	}

	@Override
	public void popRenderingContext() {
		if (!this.stack.isEmpty()) {
			GContext closedContext = this.stack.pop();
			assert(closedContext!=null);
			if (closedContext.fillColor!=null) {
				setFillColor(closedContext.fillColor);
			}
			if (closedContext.lineColor!=null) {
				setOutlineColor(closedContext.lineColor);
			}
			if (closedContext.stroke!=null) {
				this.canvas.setStroke(closedContext.stroke);
			}
			if (closedContext.paint!=null) {
				this.canvas.setPaint(closedContext.paint);
			}
			if (closedContext.transform!=null) {
				this.canvas.setTransform(closedContext.transform);
			}
			setOutlineDrawn(closedContext.isOutlined);
			setInteriorPainted(closedContext.isFilled);
			this.currentViewComponentBounds = closedContext.bounds;
			this.currentViewComponentShape = closedContext.shape;
			this.topFigure = closedContext.component;
		}
		else {
			setFillColor(ViewComponentConstants.DEFAULT_FILL_COLOR);
			setOutlineColor(ViewComponentConstants.DEFAULT_LINE_COLOR);
			setOutlineDrawn(true);
			setInteriorPainted(true);
			resetPainters();
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
		public final java.awt.Stroke stroke;
		public final java.awt.Paint paint;
		public final boolean isFilled;
		public final boolean isOutlined;
		public final AffineTransform transform;

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
				java.awt.Stroke stroke, java.awt.Paint paint,
				boolean isFilled, boolean isOutlined,
				AffineTransform transform) {
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
