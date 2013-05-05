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

package org.arakhne.neteditor.io.ngr ;

/** Constants for the NGR tools.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public interface NGRConstants {

	/** Name of the inner file dedicated to GXL format.
	 */
	public static final String GXL_INNER_FILENAME = "graph.gxl"; //$NON-NLS-1$
		
	/** Name of the inner file dedicated to GraphML format.
	 */
	public static final String GRAPHML_INNER_FILENAME = "graph.graphml"; //$NON-NLS-1$

	/** Name of the inner file dedicated to GML format.
	 */
	public static final String GML_INNER_FILENAME = "graph.gml"; //$NON-NLS-1$

	/** Name of the directory that may contains the resources, with the terminal slash.
	 */
	public static final String RESOURCE_DIRNAME = "resources/"; //$NON-NLS-1$

}
