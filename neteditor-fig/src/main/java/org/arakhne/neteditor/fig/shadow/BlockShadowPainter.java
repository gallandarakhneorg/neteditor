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
package org.arakhne.neteditor.fig.shadow;

import org.arakhne.neteditor.fig.figure.ResizeDirection;

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
public interface BlockShadowPainter extends ShadowPainter {
	
	/** resize the shadowed objects in the specified direction
	 * of the specified amount.
	 * 
	 * @param dx is the amount of the resizing.
	 * @param dy is the amount of the resizing.
	 * @param direction is the direction of the resizing.
	 */
	public void resize(float dx, float dy, ResizeDirection direction);
	
}
