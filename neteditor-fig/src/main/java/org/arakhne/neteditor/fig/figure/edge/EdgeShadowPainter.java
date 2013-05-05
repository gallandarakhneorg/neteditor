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
package org.arakhne.neteditor.fig.figure.edge;

import org.arakhne.neteditor.fig.shadow.LinearFeatureShadowPainter;

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
public interface EdgeShadowPainter extends LinearFeatureShadowPainter {

	/**
	 * Recompute the edge path with the specified translation
	 * of the first port.
	 * This function does not change the original element.
	 * 
	 * @param dx is the translation along x
	 * @param dy is the translation along y
	 */
	public void moveFirstAnchorTo(float dx, float dy);

	 /** Recompute the edge path with the specified translation
	 * of the second port.
	 * 
	 * @param dx is the translation along x
	 * @param dy is the translation along y
	 */
	public void moveSecondAnchorTo(float dx, float dy);
	
}
