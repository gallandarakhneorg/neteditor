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

package org.arakhne.neteditor.io.eps ;

import org.arakhne.afc.ui.vector.Stroke.LineJoin;

/** Line join for stroke in EPS files.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 15.0
 */
public enum EpsLineJoin {

	/** Joins path segments by extending their outside edges until
     * they meet.
     */
	MITER(LineJoin.MITER, 0),
	
	/** Joins path segments by rounding off the corner at a radius
     * of half the line width.
     */
	ROUND(LineJoin.ROUND, 1),
	
	/** Joins path segments by connecting the outer corners of their
     * wide outlines with a straight segment.
     */
	BEVEL(LineJoin.BEVEL, 2);
	
	private final LineJoin lineJoin;
	private final int eps;
	
	private EpsLineJoin(LineJoin lj, int eps) {
		this.lineJoin = lj;
		this.eps = eps;
	}
	
	/** Replies the value of the line join for the generic API.
	 * 
	 * @return the value of the line join for the generic API.
	 */
	public LineJoin getGenericType() {
		return this.lineJoin;
	}

	/** Replies the value of the line join for the EPS file.
	 * 
	 * @return the value of the line join for the EPS file.
	 */
	public int eps() {
		return this.eps;
	}
	
	/** Replies the line join from the generic line join value.
	 * 
	 * @param lineJoin the generic value
	 * @return the line join or <code>null</code> if the specified parameter does not corresponds to a line join.
	 */
	public static EpsLineJoin fromGenericType(LineJoin lineJoin) {
		for(EpsLineJoin caps : EpsLineJoin.values()) {
			if (caps.getGenericType() == lineJoin) return caps;
		}
		return null;
	}
	
}
