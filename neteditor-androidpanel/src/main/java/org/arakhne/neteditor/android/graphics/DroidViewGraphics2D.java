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
package org.arakhne.neteditor.android.graphics;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.arakhne.afc.ui.android.zoom.DroidZoomableGraphics2D;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.CenteringTransform;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;

import android.graphics.Canvas;
import android.graphics.Paint;

/**
 * This is the droid-based implementation of a ViewGraphics2D.
 *  
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DroidViewGraphics2D extends DroidZoomableGraphics2D implements ViewGraphics2D {

	private final LinkedList<GContext> stack = new LinkedList<GContext>();

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
	public DroidViewGraphics2D(
			Canvas canvas,
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
		super.dispose();
	}

	@Override
	public void reset() {
		this.stack.clear();
		super.reset();
	}

	@Override
	public float getShadowTranslationX() {
		return pixel2logical_size(ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_X);
	}

	@Override
	public float getShadowTranslationY() {
		return pixel2logical_size(ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_Y);
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
		this.canvas.save();

		if (localTransformation!=null) {
			this.canvas.setMatrix(convertTransformation(localTransformation, this.scale));
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
				new Paint(this.fillPainter),
				new Paint(this.linePainter),
				new Paint(this.fontPainter),
				isInteriorPainted(),
				isOutlineDrawn()));
	}

	@Override
	public void popRenderingContext() {
		if (!this.stack.isEmpty()) {
			this.canvas.restore();
			GContext c = this.stack.pop();
			assert(c!=null);
			if (c.fillColor!=null) {
				setFillColor(c.fillColor);
			}
			if (c.lineColor!=null) {
				setOutlineColor(c.lineColor);
			}
			if (c.fillPainter!=null) {
				this.fillPainter = c.fillPainter;
			}
			if (c.linePainter!=null) {
				this.linePainter = c.linePainter;
			}
			if (c.fontPainter!=null) {
				this.fontPainter = c.fontPainter;
			}
			setOutlineDrawn(c.isOutlined);
			setInteriorPainted(c.isFilled);
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
		public final Paint fillPainter;
		public final Paint linePainter;
		public final Paint fontPainter;
		public final boolean isFilled;
		public final boolean isOutlined;

		/**
		 * @param component
		 * @param shape
		 * @param bounds
		 * @param fillColor
		 * @param lineColor
		 * @param fillPainter
		 * @param linePainter
		 * @param fontPainter
		 * @param isFilled
		 * @param isOutlined
		 */
		public GContext(Figure component, Shape2f shape, Rectangle2f bounds,
				Color fillColor, Color lineColor,
				Paint fillPainter, Paint linePainter, Paint fontPainter,
				boolean isFilled, boolean isOutlined) {
			this.component = component;
			this.shape = shape;
			this.bounds = bounds;
			this.fillColor = fillColor;
			this.lineColor = lineColor;
			this.fillPainter = fillPainter;
			this.linePainter = linePainter;
			this.fontPainter = fontPainter;
			this.isFilled = isFilled;
			this.isOutlined = isOutlined;
		}

	}

}
