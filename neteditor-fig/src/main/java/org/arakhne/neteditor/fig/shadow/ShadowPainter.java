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
package org.arakhne.neteditor.fig.shadow;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** This interface represents any figure
 * that may be drawn in a shadow mode.
 * The shadow mode may be used when moving
 * or resizing the figure.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ShadowPainter {

	/** Replies the UUID of the associated figure.
	 * 
	 * @return the UUID of the associated figure.
	 */
	public UUID getUUID();
	
	/** Replies the associated figure.
	 * 
	 * @return the associated figure.
	 */
	public Figure getFigure();
	
	/** Replies the bounds of the shadow object.
	 * 
	 * @return the bounds of the shadow object.
	 */
	public Rectangle2f getShadowBounds();

	/** Replies the bounds of the area that must be redrawn
	 * according to this shadow object and all other related
	 * shadow object.
	 * 
	 * @return the bounds of the shadow object.
	 */
	public Rectangle2f getDamagedBounds();

	/** Method to paint the associated figure in shadow mode.
	 *
	 * @param g the graphic context.
	 */
	public void paint(ViewGraphics2D g);
	
	/** Release any resource associated to this painter.
	 */
	public void release();
	
	/**
	 * Recompute the decoration with the specified translation.
	 * This function does not change the original element.
	 * 
	 * @param dx is the translation along x
	 * @param dy is the translation along y
	 */
	public void moveTo(float dx, float dy);
	
}
