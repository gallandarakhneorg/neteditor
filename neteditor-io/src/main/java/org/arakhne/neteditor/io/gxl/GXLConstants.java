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

package org.arakhne.neteditor.io.gxl ;

import org.arakhne.neteditor.io.xml.XMLConstants;

/** Constants for the GXL tools.
 *  <p>
 *  GXL (Graph eXchange Language) is designed to be a standard exchange
 *  format for graphs. GXL is an XML sublanguage and the syntax is 
 *  given by a XML DTD (Document Type Definition). This exchange format 
 *  offers an adaptable and flexible means to support interoperability 
 *  between graph-based tools.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://www.gupro.de/GXL/I"
 */
public interface GXLConstants extends XMLConstants {

	/** &lt;gxl /&gt; */
	public static final String N_GXL = "gxl"; //$NON-NLS-1$
	/** &lt;graph /&gt; */
	public static final String N_GRAPH = "graph"; //$NON-NLS-1$
	/** &lt;node /&gt; */
	public static final String N_NODE = "node"; //$NON-NLS-1$
	/** &lt;edge /&gt; */
	public static final String N_EDGE = "edge"; //$NON-NLS-1$
	/** &lt;rel /&gt; */
	public static final String N_REL = "rel"; //$NON-NLS-1$
	/** &lt;relend /&gt; */
	public static final String N_RELEND = "relend"; //$NON-NLS-1$
	/** &lt;type /&gt; */
	public static final String N_TYPE = "type"; //$NON-NLS-1$

	/** &lt; ... neteditor:spec="" ... /&gt; */
	public static final String A_SPECIFICATION_VERSION = "neteditor:specificationVersion"; //$NON-NLS-1$
	/** &lt; ... xmlns:link="" ... /&gt; */
	public static final String A_XMLNS_XLINK = "xmlns:xlink"; //$NON-NLS-1$
	/** &lt; ... xmlns:neteditor="" ... /&gt; */
	public static final String A_XMLNS_NETEDITOR = "xmlns:neteditor"; //$NON-NLS-1$
	/** &lt; ... xlink:type="" ... /&gt; */
	public static final String A_XLINK_TYPE = "xlink:type"; //$NON-NLS-1$
	/** &lt; ... id="" ... /&gt; */
	public static final String A_ID = "id"; //$NON-NLS-1$
	/** &lt; ... edgeids="" ... /&gt; */
	public static final String A_EDGEIDS = "edgeids"; //$NON-NLS-1$
	/** &lt; ... edgemode="" ... /&gt; */
	public static final String A_EDGEMODE = "edgemode"; //$NON-NLS-1$
	/** &lt; ... idref="" ... /&gt; */
	public static final String A_IDREF = "idref"; //$NON-NLS-1$
	/** &lt; ... direction="" ... /&gt; */
	public static final String A_DIRECTION = "direction"; //$NON-NLS-1$
	/** &lt; ... isdirected="" ... /&gt; */
	public static final String A_ISDIRECTED = "isdirected"; //$NON-NLS-1$
	/** &lt; ... from="" ... /&gt; */
	public static final String A_FROM = "from"; //$NON-NLS-1$
	/** &lt; ... to="" ... /&gt; */
	public static final String A_TO = "to"; //$NON-NLS-1$
	
	/** */
	public static final String C_XLINK_NS_URI = "http://www.w3.org/1999/xlink"; //$NON-NLS-1$
	/** */
	public static final String C_NETEDITOR_NS_URI = "http://www.arakhne.org/neteditor/ns"; //$NON-NLS-1$
	/** */
	public static final String C_XLINK_SIMPLE = "simple"; //$NON-NLS-1$
	/** */
	public static final String C_GXL_DTD_URL = "http://www.gupro.de/GXL/gxl-1.0.dtd"; //$NON-NLS-1$
	/** */
	public static final String C_GXL_DTD_FILENAME = "gxl-1.0.dtd"; //$NON-NLS-1$
	/** */
	public static final String C_GXL_REL_IN = "in"; //$NON-NLS-1$
	/** */
	public static final String C_GXL_REL_OUT = "out"; //$NON-NLS-1$
	/** */
	public static final String C_GXL_NONE = "none"; //$NON-NLS-1$
	/** */
	public static final String C_GXL_EDGE_DEFAULTDIRECTED = "defaultdirected"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_LABEL = "label"; //$NON-NLS-1$
	/** */
	public static final String C_INTERNAL_VIEW_TYPE = SCHEMA_URL+"#__internal_view__"; //$NON-NLS-1$
	/** */
	public static final String C_INTERNAL_VIEW_RELATION_TYPE = SCHEMA_URL+"#__internal_view_relation__"; //$NON-NLS-1$
	/** */
	public static final String C_INTERNAL_COERCION_RELATION_TYPE = SCHEMA_URL+"#__internal_coercion_relation__"; //$NON-NLS-1$
	/** */
	public static final String C_INTERNAL_NODE_ANCHOR_RELATION_TYPE = SCHEMA_URL+"#__internal_node_anchor_relation__"; //$NON-NLS-1$
		
}
