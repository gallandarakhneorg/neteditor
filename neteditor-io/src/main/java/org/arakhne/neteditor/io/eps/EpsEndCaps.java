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

package org.arakhne.neteditor.io.eps ;

import org.arakhne.afc.ui.vector.Stroke.EndCap;

/** End Caps for stroke in EPS files.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 15.0
 */
public enum EpsEndCaps {

	/** Ends unclosed subpaths and dash segments with no added
     * decoration.
     */
	BUTT(EndCap.BUTT, 0),
	
	/** Ends unclosed subpaths and dash segments with a round
     * decoration that has a radius equal to half of the width
     * of the pen.
     */
	ROUND(EndCap.ROUND, 1),
	
	/** Ends unclosed subpaths and dash segments with a square
     * projection that extends beyond the end of the segment
     * to a distance equal to half of the line width.
     */
	SQUARE(EndCap.SQUARE, 2);
	
	private final EndCap endCap;
	private final int eps;
	
	private EpsEndCaps(EndCap endCap, int eps) {
		this.endCap = endCap;
		this.eps = eps;
	}
	
	/** Replies the value of the end caps for the generic API.
	 * 
	 * @return the value of the end caps for the generic API.
	 */
	public EndCap getGenericType() {
		return this.endCap;
	}

	/** Replies the value of the end caps for the EPS file.
	 * 
	 * @return the value of the end caps for the EPS file.
	 */
	public int eps() {
		return this.eps;
	}
	
	/** Replies the end caps from the generic end cap.
	 * 
	 * @param endCap
	 * @return the end caps or <code>null</code> if the specified parameter does not corresponds to an end caps.
	 */
	public static EpsEndCaps fromGenericType(EndCap endCap) {
		for(EpsEndCaps caps : EpsEndCaps.values()) {
			if (caps.getGenericType() == endCap) return caps;
		}
		return null;
	}
	
}
