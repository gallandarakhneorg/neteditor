/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.android.event ;

import java.util.EventListener;

/** Listener on changes in the collection of figures in a FigureView.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface FigureListener extends EventListener {
	
	/** Invoked when a figure was added in the viewer.
	 * 
	 * @param event
	 */
	public void figureAdded(FigureEvent event);
	
	/** Invoked when a figure has changed in the viewer.
	 * 
	 * @param event
	 */
	public void figureChanged(FigureEvent event);

	/** Invoked when a figure was removed from the viewer.
	 * 
	 * @param event
	 */
	public void figureRemoved(FigureEvent event);

}
