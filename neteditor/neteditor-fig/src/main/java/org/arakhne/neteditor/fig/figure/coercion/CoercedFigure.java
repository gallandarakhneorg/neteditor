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
package org.arakhne.neteditor.fig.figure.coercion ;

import org.arakhne.neteditor.fig.figure.Figure;

/** Figure that is associated to an anchor
 * and located according to the anchor position.
 * The anchored figures may be used as associated
 * figures with edges for example.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface CoercedFigure extends Figure {

	/** Change the location of this figure according to
	 * the new position of the anchor point.
	 * 
	 * @param x
	 * @param y
	 */
	public void setLocationFromAnchorPoint(float x, float y);
	
	/** Set the description of the anchor for this anchored figure.
	 * The description depends on the other figure that is
	 * computing the anchor point.
	 * 
	 * @param descriptor
	 */
	public void setAnchorDescriptor(Object descriptor);
	
	/** Replies the description of the anchor for this anchored figure.
	 * The description depends on the other figure that is
	 * computing the anchor point.
	 * 
	 * @return the description.
	 */
	public Object getAnchorDescriptor();

}
