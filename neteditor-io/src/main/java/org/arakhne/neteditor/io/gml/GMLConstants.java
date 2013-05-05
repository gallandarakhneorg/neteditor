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

package org.arakhne.neteditor.io.gml ;

/** Constants for the GML format.
 *  <p>
 *  Graph Modelling Language (GML) is a hierarchical ASCII-based
 *  file format for describing graphs.
 *  It has been also named Graph Meta Language. 
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://en.wikipedia.org/wiki/Graph_Modelling_Language"
 * @since 16.0
 */
public interface GMLConstants {

	/** &lt;gml:gml /&gt; */
	public static final String N_GML = "gml:gml"; //$NON-NLS-1$
		
	/** */
	public static final String K_TRUE = "1"; //$NON-NLS-1$
	/** */
	public static final String K_FALSE = "0"; //$NON-NLS-1$
	/** */
	public static final String K_GRAPH = "graph"; //$NON-NLS-1$
	/** */
	public static final String K_NODE = "node"; //$NON-NLS-1$
	/** */
	public static final String K_EDGE = "edge"; //$NON-NLS-1$
	/** */
	public static final String K_ID = "id"; //$NON-NLS-1$
	/** */
	public static final String K_NAME = "name"; //$NON-NLS-1$
	/** */
	public static final String K_ATTRIBUTES = "attributes"; //$NON-NLS-1$
	/** */
	public static final String K_VERSION = "Version"; //$NON-NLS-1$
	/** */
	public static final String K_CREATOR = "Creator"; //$NON-NLS-1$
	/** */
	public static final String K_DIRECTED = "directed"; //$NON-NLS-1$
	/** */
	public static final String K_TYPE = "type"; //$NON-NLS-1$
	/** */
	public static final String K_VALUE = "value"; //$NON-NLS-1$
	/** */
	public static final String K_INTEGER = "integer"; //$NON-NLS-1$
	/** */
	public static final String K_FLOAT = "float"; //$NON-NLS-1$
	/** */
	public static final String K_BOOLEAN = "boolean"; //$NON-NLS-1$
	/** */
	public static final String K_STRING = "string"; //$NON-NLS-1$
	/** */
	public static final String K_UUID = "uuid"; //$NON-NLS-1$
	/** */
	public static final String K_MODELID = "modelid"; //$NON-NLS-1$
	/** */
	public static final String K_ENUM = "enum"; //$NON-NLS-1$
	/** */
	public static final String K_URL = "url"; //$NON-NLS-1$
	/** */
	public static final String K_SET = "set"; //$NON-NLS-1$
	/** */
	public static final String K_LIST = "list"; //$NON-NLS-1$
	/** */
	public static final String K_COLLECTION = "collection"; //$NON-NLS-1$
	/** */
	public static final String K_NS = "ns"; //$NON-NLS-1$
	/** */
	public static final String K_SERIAL = "serial"; //$NON-NLS-1$
	/** */
	public static final String K_EDGEANCHOR = "edgeAnchor"; //$NON-NLS-1$
	/** */
	public static final String K_SOURCE = "source"; //$NON-NLS-1$
	/** */
	public static final String K_TARGET = "target"; //$NON-NLS-1$
	/** */
	public static final String K_SOURCEPORT = "sourcePort"; //$NON-NLS-1$
	/** */
	public static final String K_TARGETPORT = "targetPort"; //$NON-NLS-1$
	/** */
	public static final String K_GRAPHICS = "graphics"; //$NON-NLS-1$
	/** */
	public static final String K_FIGURE = "Figure"; //$NON-NLS-1$
	/** */
	public static final String K_COERCIONID = "coercionid"; //$NON-NLS-1$
	/** */
	public static final String K_VIEWID = "viewid"; //$NON-NLS-1$

	/** URL of the generic specification for the model objects.
	 */
	public static final String SCHEMA_URL = "http://www.arakhne.org/neteditor/generic.gml"; //$NON-NLS-1$
	

}
