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

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.VectorGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;

/** This interface represents a Graphics for view components.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ViewGraphics2D extends VectorGraphics2D {

	/** Replies the translation to apply along the X axis to draw
	 * the shadow of a figure.
	 * 
	 * @return the translation along X for the shadow from the figures.
	 */
	public float getShadowTranslationX();
	
	/** Replies the translation to apply along the Y axis to draw
	 * the shadow of a figure.
	 * 
	 * @return the translation along Y for the shadow from the figures.
	 */
	public float getShadowTranslationY();

	/** Indicates if the associated component is locked.
	 * 
	 * @return <code>true</code> if the associated component is locked,
	 * <code>false</code> otherwise.
	 */
	public boolean isLocked();
		
	/** Indicates if the drawings are for shadows or not.
	 * 
	 * @return <code>true</code> if the drawings are for shadows,
	 * <code>false</code> otherwise.
	 */
	public boolean isShadowDrawing();

	/** Replies the bounds of the current view. This bounds may
	 * changed dynamically.
	 * 
	 * @return the bounds of the associated view.
	 */
	public Rectangle2f getCurrentViewComponentBounds();

	/** Replies the shape of the current view. This bounds may
	 * changed dynamically.
	 * 
	 * @return the shape of the associated view.
	 */
	public Shape2f getCurrentViewComponentShape();

	/** Start a rendering context for a sub-component but with
	 * the specified local transformation.
	 * 
	 * @param component is the component to paint for.
	 * @param localTransformation is the local transformation to apply to.
	 */
	public void pushRenderingContext(Figure component, Transform2D localTransformation);
	
	/** Start a rendering context for a component.
	 * 
	 * @param component is the component to paint for.
	 * @param viewShape is the shape of the component. It could
	 * be <code>null</code> to use the specified <var>bounds</var>.
	 * @param bounds are the bounds of the component, never <code>null</code>.
	 */
	public void pushRenderingContext(Figure component, Shape2f viewShape, Rectangle2f bounds);

	/** Start a rendering context for a component.
	 * 
	 * @param component is the component to paint for.
	 * @param viewShape is the shape of the component. It could
	 * be <code>null</code> to use the specified <var>bounds</var>.
	 * @param bounds are the bounds of the component, never <code>null</code>.
	 * @param fillColor is the color to use to fill the objects.
	 * @param lineColor is the color to use to draw the outlines of the objects.
	 */
	public void pushRenderingContext(Figure component, Shape2f viewShape, Rectangle2f bounds, Color fillColor, Color lineColor);

	/** Start a rendering context for a sub-component but this
	 * the specified local transformation.
	 * 
	 * @param component is the component to paint for.
	 * @param fillColor is the color to use to fill the objects.
	 * @param lineColor is the color to use to draw the outlines of the objects.
	 */
	public void pushRenderingContext(Figure component, Color fillColor, Color lineColor);

	/** Start a rendering context for a component.
	 * 
	 * @param component is the component to paint for.
	 * @param viewShape is the shape of the component. It could
	 * be <code>null</code> to use the specified <var>bounds</var>.
	 * @param bounds are the bounds of the component, never <code>null</code>.
	 * @param fillColor is the color to use to fill the objects.
	 * @param lineColor is the color to use to draw the outlines of the objects.
	 * @param localTransformation is the local transformation to apply to.
	 */
	public void pushRenderingContext(Figure component, Shape2f viewShape, Rectangle2f bounds, Color fillColor, Color lineColor, Transform2D localTransformation);

	/** Stop a rendering context for a component.
	 */
	public void popRenderingContext();

	/** Start a group of vector objects.
	 */
	public void beginGroup();

	/** End a group of vector objects.
	 */
	public void endGroup();

}
