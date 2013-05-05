/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.swing.actionmode ;

import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.neteditor.fig.figure.ResizeDirection;

/** Utilities for Swing action modes.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ActionModeUtil {

	/** Replies the mouse cursor that is corresponding to the specified
	 * resizing direction.
	 * 
	 * @param direction
	 * @return the cursor or <code>null</code> to use the default.
	 */
	public static MouseCursor getResizingCursor(ResizeDirection direction) {
		MouseCursor c = null;
		if (direction!=null) {
			switch(direction) {
			case NORTH_WEST:
				c = MouseCursor.NW_RESIZE;
				break;
			case WEST:
				c = MouseCursor.W_RESIZE;
				break;
			case SOUTH_WEST:
				c = MouseCursor.SW_RESIZE;
				break;
			case NORTH:
				c = MouseCursor.N_RESIZE;
				break;
			case SOUTH:
				c = MouseCursor.S_RESIZE;
				break;
			case NORTH_EAST:
				c = MouseCursor.NE_RESIZE;
				break;
			case EAST:
				c = MouseCursor.E_RESIZE;
				break;
			case SOUTH_EAST:
				c = MouseCursor.SE_RESIZE;
				break;
			default:
				throw new IllegalStateException();
			}
		}
		return c;
	}
	
}
