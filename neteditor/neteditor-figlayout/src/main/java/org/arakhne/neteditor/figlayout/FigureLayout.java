/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND
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

package org.arakhne.neteditor.figlayout;

import java.util.Collection;

import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.neteditor.fig.figure.Figure;

/** This interface represents all the algorithms that permits to
 * layout figures.
 * 
 * @author $Author: baumgartner$
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface FigureLayout {

	/** Run the laying-out algorithm for the given figures.
	 * 
	 * @param figures are the figures to laying out.
	 * @return the undoable edit that permits to revert the laying out; or
	 * <code>null</code> if the layout does not supports the undo/redo feature
	 * or it has nothing to undo.
	 */
	public Undoable layoutFigures(Collection<? extends Figure> figures);
	
}
