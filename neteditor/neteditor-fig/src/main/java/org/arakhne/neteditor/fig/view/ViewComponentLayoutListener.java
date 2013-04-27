/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.fig.view;

import java.util.EventListener;

/** Listener on repaint requests.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ViewComponentLayoutListener extends EventListener {

	/** Invoked when a component wants to be repainted.
	 * 
	 * @param component is the component to repaint.
	 * @param boundsChanged indicates if the repaint is due to
	 * a change in bounds; or not.
	 */
	public void componentRepaint(ViewComponent component, boolean boundsChanged);
	
	/** Invoked when a component wants to be moved to avoid any
	 * collision with the other view components.
	 * 
	 * @param component is the component to move.
	 */
	public void collisionAvoidance(ViewComponent component);

}
