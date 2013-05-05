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

package org.arakhne.neteditor.io.xml ;

/** Constants for the XML tools.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public interface XMLConstants {

	/** URL of the generic specification for the model objects.
	 */
	public static final String SCHEMA_URL = "http://www.arakhne.org/neteditor/generic.gxl"; //$NON-NLS-1$

	/** &lt;int /&gt; */
	public static final String N_INT = "int"; //$NON-NLS-1$
	/** &lt;float /&gt; */
	public static final String N_FLOAT = "float"; //$NON-NLS-1$
	/** &lt;bool /&gt; */
	public static final String N_BOOL = "bool"; //$NON-NLS-1$
	/** &lt;enum /&gt; */
	public static final String N_ENUM = "enum"; //$NON-NLS-1$
	/** &lt;string /&gt; */
	public static final String N_STRING = "string"; //$NON-NLS-1$
	/** &lt;locator /&gt; */
	public static final String N_LOCATOR = "locator"; //$NON-NLS-1$
	/** &lt;set /&gt; */
	public static final String N_SET = "set"; //$NON-NLS-1$
	/** &lt;seq /&gt; */
	public static final String N_SEQ = "seq"; //$NON-NLS-1$
	/** &lt;bag /&gt; */
	public static final String N_BAG = "bag"; //$NON-NLS-1$
	/** &lt;attr /&gt; */
	public static final String N_ATTR = "attr"; //$NON-NLS-1$
		
	/** &lt; ... xlink:href="" ... /&gt; */
	public static final String A_XLINK_HREF = "xlink:href"; //$NON-NLS-1$
	/** &lt; ... name="" ... /&gt; */
	public static final String A_NAME = "name"; //$NON-NLS-1$

}
