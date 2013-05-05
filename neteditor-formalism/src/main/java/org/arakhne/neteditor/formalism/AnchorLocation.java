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
package org.arakhne.neteditor.formalism;

/** This enumeration defines the slots available for
 * the anchors.
 *
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum AnchorLocation {
    
    /** Set the port in the northwest of the owner */
    NORTH_WEST,
    /** Set the port in the north of the owner */
    NORTH,
    /** Set the port in the west of the owner */
    NORTH_EAST,
    /** Set the port in the east of the owner */
    EAST,
    /** Set the port in the southesst of the owner */
    SOUTH_EAST,
    /** Set the port in the south of the owner */
    SOUTH,
    /** Set the port in the southeast of the owner */
    SOUTH_WEST,
    /** Set the port in the west of the owner */
    WEST,    
    /** Set the port in the middel to the owner*/
    CENTER;
        
}
