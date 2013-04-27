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
package org.arakhne.neteditor.swing.graphics;

import java.awt.geom.AffineTransform;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.awt.LODGraphics2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.awt.DelegatedVectorGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;

/** Implementation of a graphics context which is
 * delagating to another graphics context.
 *
 * @param <D> is the type of the graphics context to delegate to.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DelegatedViewGraphics2D<D extends LODGraphics2D> extends DelegatedVectorGraphics2D<D> implements ViewGraphics2D {

	private final LinkedList<GContext> stack = new LinkedList<GContext>();
	
	/**
	 * @param context
	 */
	public DelegatedViewGraphics2D(D context) {
		super(context);
	}
	
	@Override
	public void dispose() {
		this.stack.clear();
		super.dispose();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.stack.clear();
	}
	
	@Override
	public float getShadowTranslationX() {
		return ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_X;
	}

	@Override
	public float getShadowTranslationY() {
		return ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_Y;
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getOutlineColor() {
		Color c = super.getOutlineColor();
		if (c!=null) return c;
		Figure fig = getTopFigure();
		if (fig==null)
			return ViewComponentConstants.DEFAULT_LINE_COLOR;
		return fig.getLineColor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getFillColor() {
		Color c = super.getFillColor();
		if (c!=null) return c;
		Figure fig = getTopFigure();
		if (fig==null)
			return ViewComponentConstants.DEFAULT_FILL_COLOR;
		return fig.getFillColor();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLocked() {
		Figure fig = getTopFigure();
		if (fig==null)
			return false;
		return fig.isLocked();
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
	public void pushRenderingContext(Figure component, Transform2D localTransformation) {
		pushRenderingContext(component, null, null, null, null, localTransformation);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds) {
		pushRenderingContext(component, viewShape, bounds, null, null, null);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor,
			Transform2D localTransformation) {
		AffineTransform trans = this.delegate.getTransform();
		if (localTransformation!=null) {
			AffineTransform at = new AffineTransform(
					localTransformation.m00,
					localTransformation.m10,
					localTransformation.m01,
					localTransformation.m11,
					localTransformation.m02,
					localTransformation.m12);
			this.delegate.setTransform(at);
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

		this.stack.push(new GContext(component, viewShape, bounds, trans, ofc, olc, getFont()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor) {
		pushRenderingContext(component, viewShape, bounds, fillColor, lineColor, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pushRenderingContext(Figure component, Color fillColor,
			Color lineColor) {
		pushRenderingContext(component, null, null, fillColor, lineColor, null);
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
				this.delegate.setTransform(c.transformation);
			}
			if (c.fillColor!=null) {
				setFillColor(c.fillColor);
			}
			if (c.lineColor!=null) {
				setOutlineColor(c.lineColor);
			}
			if (c.font!=null) {
				setFont(c.font);
			}
		}
		else {
			setFillColor(ViewComponentConstants.DEFAULT_FILL_COLOR);
			setOutlineColor(ViewComponentConstants.DEFAULT_LINE_COLOR);
			setFont(getDefaultFont());
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beginGroup() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
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
		public final AffineTransform transformation;
		public final Color fillColor;
		public final Color lineColor;
		public final Font font;
		
		/**
		 * @param component
		 * @param shape
		 * @param bounds
		 * @param transformation
		 * @param fillColor
		 * @param lineColor
		 * @param font
		 */
		public GContext(Figure component, Shape2f shape, Rectangle2f bounds, AffineTransform transformation, Color fillColor, Color lineColor, Font font) {
			this.component = component;
			this.shape = shape;
			this.bounds = bounds;
			this.transformation = transformation;
			this.fillColor = fillColor;
			this.lineColor = lineColor;
			this.font = font;
		}

	}

}
