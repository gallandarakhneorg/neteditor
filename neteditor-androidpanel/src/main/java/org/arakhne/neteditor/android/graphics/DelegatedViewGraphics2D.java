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
package org.arakhne.neteditor.android.graphics;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.continous.object2d.Segment2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.ZoomableContext;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.android.DelegatedVectorGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** Implementation of a graphics context which is
 * delegating to another graphics context.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DelegatedViewGraphics2D extends DelegatedVectorGraphics2D<DroidViewGraphics2D> implements ViewGraphics2D, ZoomableContext {

	/**
	 * @param context
	 */
	public DelegatedViewGraphics2D(DroidViewGraphics2D context) {
		super(context);
	}
	
	@Override
	public boolean isShadowDrawing() {
		return this.delegate.isShadowDrawing();
	}

	@Override
	public float logical2pixel_size(float l) {
		return this.delegate.logical2pixel_size(l);
	}

	@Override
	public float logical2pixel_x(float l) {
		return this.delegate.logical2pixel_x(l);
	}

	@Override
	public float logical2pixel_y(float l) {
		return this.delegate.logical2pixel_y(l);
	}

	@Override
	public float pixel2logical_size(float l) {
		return this.delegate.pixel2logical_size(l);
	}

	@Override
	public float pixel2logical_x(float l) {
		return this.delegate.pixel2logical_x(l);
	}

	@Override
	public float pixel2logical_y(float l) {
		return this.delegate.pixel2logical_y(l);
	}

	@Override
	public float getScalingFactor() {
		return this.delegate.getScalingFactor();
	}

	@Override
	public float getMaxScalingFactor() {
		return this.delegate.getMaxScalingFactor();
	}

	@Override
	public float getMinScalingFactor() {
		return this.delegate.getMinScalingFactor();
	}

	@Override
	public float getScalingSensitivity() {
		return this.delegate.getScalingSensitivity();
	}

	@Override
	public float getFocusX() {
		return this.delegate.getFocusX();
	}

	@Override
	public float getFocusY() {
		return this.delegate.getFocusY();
	}

	@Override
	public float getShadowTranslationX() {
		return this.delegate.getShadowTranslationX();
	}

	@Override
	public float getShadowTranslationY() {
		return getShadowTranslationY();
	}

	@Override
	public boolean isLocked() {
		return this.delegate.isLocked();
	}

	@Override
	public Rectangle2f getCurrentViewComponentBounds() {
		return this.delegate.getCurrentViewComponentBounds();
	}

	@Override
	public Shape2f getCurrentViewComponentShape() {
		return this.delegate.getCurrentViewComponentShape();
	}

	@Override
	public void pushRenderingContext(Figure component,
			Transform2D localTransformation) {
		this.delegate.pushRenderingContext(component, localTransformation);
	}

	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds) {
		this.delegate.pushRenderingContext(component, viewShape, bounds);
	}

	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor) {
		this.delegate.pushRenderingContext(component, viewShape, bounds, fillColor, lineColor);
	}

	@Override
	public void pushRenderingContext(Figure component, Color fillColor,
			Color lineColor) {
		this.delegate.pushRenderingContext(component, fillColor, lineColor);
	}

	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor,
			Transform2D localTransformation) {
		this.delegate.pushRenderingContext(component, viewShape, bounds, fillColor, lineColor, localTransformation);
	}

	@Override
	public void popRenderingContext() {
		this.delegate.popRenderingContext();
	}

	@Override
	public void beginGroup() {
		this.delegate.beginGroup();
	}

	@Override
	public void endGroup() {
		this.delegate.endGroup();
	}

	@Override
	public boolean isXAxisInverted() {
		return this.delegate.isXAxisInverted();
	}

	@Override
	public boolean isYAxisInverted() {
		return this.delegate.isYAxisInverted();
	}

	@Override
	public PathIterator2f logical2pixel(PathIterator2f p) {
		return this.delegate.logical2pixel(p);
	}

	@Override
	public PathIterator2f pixel2logical(PathIterator2f p) {
		return this.delegate.pixel2logical(p);
	}

	@Override
	public void logical2pixel(Segment2f s) {
		this.delegate.logical2pixel(s);
	}

	@Override
	public void pixel2logical(Segment2f s) {
		this.delegate.pixel2logical(s);
	}

	@Override
	public void logical2pixel(RoundRectangle2f r) {
		this.delegate.logical2pixel(r);
	}

	@Override
	public void pixel2logical(RoundRectangle2f r) {
		this.delegate.pixel2logical(r);
	}

	@Override
	public void logical2pixel(Point2f p) {
		this.delegate.logical2pixel(p);
	}

	@Override
	public void pixel2logical(Point2f p) {
		this.delegate.pixel2logical(p);
	}

	@Override
	public void logical2pixel(Ellipse2f e) {
		this.delegate.logical2pixel(e);
	}

	@Override
	public void pixel2logical(Ellipse2f e) {
		this.delegate.pixel2logical(e);
	}

	@Override
	public void logical2pixel(Circle2f r) {
		this.delegate.logical2pixel(r);
	}

	@Override
	public void pixel2logical(Circle2f r) {
		this.delegate.pixel2logical(r);
	}

	@Override
	public void logical2pixel(Rectangle2f r) {
		this.delegate.logical2pixel(r);
	}

	@Override
	public void pixel2logical(Rectangle2f r) {
		this.delegate.pixel2logical(r);
	}
				
	@Override
	public Shape2f logical2pixel(Shape2f s) {
		return this.delegate.logical2pixel(s);
	}

	@Override
	public Shape2f pixel2logical(Shape2f s) {
		return this.delegate.pixel2logical(s);
	}


}
