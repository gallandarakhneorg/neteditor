/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.android.actionmode ;

import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.neteditor.fig.figure.Figure;

/** This object contains all the data that are
 * corresponding to a mouse event for which
 * the treatment was differed.
 * 
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DifferedPointerEvent {

	/** x position of the mouse.
	 */
	public final float x;

	/** y position of the mouse.
	 */
	public final float y;

	/** Mouse pressed event.
	 */
	public final ActionPointerEvent pointerEvent;

	/** Figure under the mouse when the button was pressed.
	 */
	public final Figure pressedFigure;

	/**
	 * @param x
	 * @param y
	 * @param e
	 * @param f
	 */
	public DifferedPointerEvent(float x, float y, ActionPointerEvent e, Figure f) {
		this.x = x;
		this.y = y;
		this.pointerEvent = e;
		this.pressedFigure = f;
	}
}
