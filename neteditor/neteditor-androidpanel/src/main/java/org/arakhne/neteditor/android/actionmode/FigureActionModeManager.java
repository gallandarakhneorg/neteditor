/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.android.actionmode;

import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeListener;
import org.arakhne.afc.ui.actionmode.SelectableInteractionListener;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;

/** Public interface that permits to access to
 * the configuration services of a mode manager on
 * Android platforms.
 *  
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface FigureActionModeManager {
	
	/** Change the mode of selection.
	 *
	 * @param mode
	 */
	public void setSelectionMode(SelectionMode mode);
	
	/** Replies the current mode of selection.
	 *
	 * @return the mode of selection.
	 */
	public SelectionMode getSelectionMode();

	/** Add listener on the mode events.
	 * 
	 * @param listener
	 */
	public void addModeListener(ActionModeListener listener);
	
	/** Remove listener on the mode events.
	 * 
	 * @param listener
	 */
	public void removeModeListener(ActionModeListener listener);
	
	/** Add listener on the figure interaction events.
	 * 
	 * @param listener
	 */
	public void addSelectableInteractionListener(SelectableInteractionListener listener);
	
	/** Remove listener on the figure interaction events.
	 * 
	 * @param listener
	 */
	public void removeSelectableInteractionListener(SelectableInteractionListener listener);

	/** Change the mode of selection.
	 *
	 * @param mode
	 */
	public void startMode(ActionMode<Figure,DroidViewGraphics2D,Color> mode);

}

