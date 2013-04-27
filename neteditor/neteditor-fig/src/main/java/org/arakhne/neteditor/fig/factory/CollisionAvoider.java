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
package org.arakhne.neteditor.fig.factory ;

import java.util.Set;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.fig.view.ViewComponent;

/** This interface represents a collision avoider
 * that is used by the {@link FigureFactory}
 * to avoid collisions.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface CollisionAvoider {

	/** Replies if the given bounds is free of collision.
	 * 
	 * @param bounds
	 * @param exceptions are the components to exclude from the collision test.
	 * @return <code>true</code> if no collision was found;
	 * otherwise <code>false</code>
	 */
	public boolean isCollisionFree(Rectangle2f bounds, Set<? extends ViewComponent> exceptions);

	/** Replies the bounds of the figure that is
	 * intersecting the specified bounds.
	 * 
	 * @param bounds
	 * @param exceptions are the components to exclude from the collision test.
	 * @return the existing bounds in collision with the
	 * specified bounds; or <code>null</code> if there is
	 * no collision.
	 */
	public Rectangle2f detectCollision(Rectangle2f bounds, Set<? extends ViewComponent> exceptions);

}
