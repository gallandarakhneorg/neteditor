/* 
 * $Id$
 * 
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

package org.arakhne.neteditor.io.graphml ;

import org.arakhne.neteditor.io.xml.XMLConstants;

/** Constants for the GraphML tools.
 *  <p>
 *  GraphML is a comprehensive and easy-to-use file format for graphs.
 *  It consists of a language core to describe the structural properties
 *  of a graph and a flexible extension mechanism to add
 *  application-specific data. 
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://graphml.graphdrawing.org/"
 * @since 16.0
 */
public interface GraphMLConstants extends XMLConstants {

	/** &lt;graphml /&gt; */
	public static final String N_GRAPHML = "graphml"; //$NON-NLS-1$
	/** &lt;graph /&gt; */
	public static final String N_GRAPH = "graph"; //$NON-NLS-1$
	/** &lt;key /&gt; */
	public static final String N_KEY = "key"; //$NON-NLS-1$
	/** &lt;data /&gt; */
	public static final String N_DATA = "data"; //$NON-NLS-1$
	/** &lt;node /&gt; */
	public static final String N_NODE = "node"; //$NON-NLS-1$
	/** &lt;port /&gt; */
	public static final String N_PORT = "port"; //$NON-NLS-1$
	/** &lt;edge /&gt; */
	public static final String N_EDGE = "edge"; //$NON-NLS-1$
	/** &lt;default /&gt; */
	public static final String N_DEFAULT = "default"; //$NON-NLS-1$
	/** &lt;neteditor:view /&gt; */
	public static final String N_NETEDITOR_VIEW = "neteditor:view"; //$NON-NLS-1$
	/** &lt;neteditor:viewcomponent /&gt; */
	public static final String N_NETEDITOR_VIEWCOMPONENT = "neteditor:viewcomponent"; //$NON-NLS-1$
	/** &lt;neteditor:viewcomponent /&gt; */
	public static final String N_NETEDITOR_COERCEDFIGURE = "neteditor:coercedfigure"; //$NON-NLS-1$
	/** &lt;neteditor:subfigure /&gt; */
	public static final String N_NETEDITOR_SUBFIGURE = "neteditor:subfigure"; //$NON-NLS-1$

	/** &lt; ... xmlns="" ... /&gt; */
	public static final String A_XMLNS = "xmlns"; //$NON-NLS-1$
	/** &lt; ... xmlns:link="" ... /&gt; */
	public static final String A_XMLNS_XLINK = "xmlns:xlink"; //$NON-NLS-1$
	/** &lt; ... xlink:type="" ... /&gt; */
	public static final String A_XLINK_TYPE = "xlink:type"; //$NON-NLS-1$
	/** &lt; ... xmlns:svg="" ... /&gt; */
	public static final String A_XMLNS_SVG = "xmlns:svg"; //$NON-NLS-1$
	/** &lt; ... xmlns:neteditor="" ... /&gt; */
	public static final String A_XMLNS_NETEDITOR = "xmlns:neteditor"; //$NON-NLS-1$
	/** &lt; ... xmlns:xsi="" ... /&gt; */
	public static final String A_XMLNS_XSI = "xmlns:xsi"; //$NON-NLS-1$
	/** &lt; ... xsi:schemaLocation="" ... /&gt; */
	public static final String A_XSI_SCHEMALOCATION = "xsi:schemaLocation"; //$NON-NLS-1$
	/** &lt; ... GraphMLSpecificationVersion="" ... /&gt; */
	public static final String A_GRAPHML_SPECIFICATION_VERSION = "GraphMLSpecificationVersion"; //$NON-NLS-1$
	/** &lt; ... id="" ... /&gt; */
	public static final String A_ID = "id"; //$NON-NLS-1$
	/** &lt; ... for="" ... /&gt; */
	public static final String A_FOR = "for"; //$NON-NLS-1$
	/** &lt; ... key="" ... /&gt; */
	public static final String A_KEY = "key"; //$NON-NLS-1$
	/** &lt; ... type="" ... /&gt; */
	public static final String A_TYPE = "type"; //$NON-NLS-1$
	/** &lt; ... source="" ... /&gt; */
	public static final String A_SOURCE = "source"; //$NON-NLS-1$
	/** &lt; ... target="" ... /&gt; */
	public static final String A_TARGET = "target"; //$NON-NLS-1$
	/** &lt; ... sourceport="" ... /&gt; */
	public static final String A_SOURCEPORT = "sourceport"; //$NON-NLS-1$
	/** &lt; ... targetport="" ... /&gt; */
	public static final String A_TARGETPORT = "targetport"; //$NON-NLS-1$
	/** &lt; ... edgedefault="" ... /&gt; */
	public static final String A_EDGEDEFAULT = "edgedefault"; //$NON-NLS-1$
	/** &lt; ... viewid="" ... /&gt; */
	public static final String A_VIEWID = "viewid"; //$NON-NLS-1$
	/** &lt; ... modelid="" ... /&gt; */
	public static final String A_MODELID = "modelid"; //$NON-NLS-1$
	/** &lt; ... coercedid="" ... /&gt; */
	public static final String A_COERCEDID = "coercedid"; //$NON-NLS-1$
	/** &lt; ... attr.name="" ... /&gt; */
	public static final String A_ATTR_NAME = "attr.name"; //$NON-NLS-1$
	/** &lt; ... attr.type="" ... /&gt; */
	public static final String A_ATTR_TYPE = "attr.type"; //$NON-NLS-1$
	
	/** */
	public static final String C_XML_VERSION = "1.0"; //$NON-NLS-1$
	/** URL of the generic specification for the model objects.
	 */
	public static final String C_GRAPHML_DTD_URL = "http://graphml.graphdrawing.org/dtds/graphml.dtd"; //$NON-NLS-1$
	/** Local filename of the generic specification for the model objects.
	 */
	public static final String C_GRAPHML_DTD_FILENAME = "graphml.dtd"; //$NON-NLS-1$
	/** URI of the xlink namespace.
	 */
	public static final String C_XLINK_NS_URI = "http://www.w3.org/1999/xlink"; //$NON-NLS-1$
	/** Type of xlink: simple.
	 */
	public static final String C_XLINK_TYPE_SIMPLE = "simple"; //$NON-NLS-1$
	/** */
	public static final String C_XMLNS_URI = "http://graphml.graphdrawing.org/xmlns"; //$NON-NLS-1$
	/** */
	public static final String C_XMLNS_SVG_URI = "http://www.w3.org/2000/svg"; //$NON-NLS-1$
	/** */
	public static final String C_XMLNS_NETEDITOR_URI = "http://www.arakhne.org/neteditor"; //$NON-NLS-1$
	/** */
	public static final String C_XMLNS_XSI_URI = "http://www.w3.org/2001/XMLSchema-instance"; //$NON-NLS-1$
	/** */
	public static final String C_XSI_SCHEMALOCATION_URI = "http://graphml.graphdrawing.org/xmlns graphml+svg.xsd"; //$NON-NLS-1$
	/** */
	public static final String C_FOR_ALL = "all"; //$NON-NLS-1$
	/** */
	public static final String C_FOR_NODE = "node"; //$NON-NLS-1$
	/** */
	public static final String C_FOR_EDGE = "edge"; //$NON-NLS-1$
	/** */
	public static final String C_DIRECTED = "directed"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_LABEL = "label"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_TYPE_INT = "int"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_TYPE_LONG = "long"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_TYPE_FLOAT = "float"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_TYPE_DOUBLE = "double"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_TYPE_BOOLEAN = "boolean"; //$NON-NLS-1$
	/** */
	public static final String C_ATTR_TYPE_STRING = "string"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_ATTR_PREFIX = "neteditor:attr:"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_NETEDITOR_TYPE = "neteditor:type"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_NETEDITOR_ATTRIBUTES = "neteditor:attrs"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_NETEDITOR_VIEWS = "neteditor:views"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_NETEDITOR_MODELID = "neteditor:modelId"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_NETEDITOR_COERCEDFIGURES = "neteditor:coercedFigures"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_NETEDITOR_SUBFIGURES = "neteditor:subFigures"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_SVG_NODE = "neteditor:svg:node"; //$NON-NLS-1$
	/** */
	public static final String C_KEY_SVG_EDGE = "neteditor:svg:edge"; //$NON-NLS-1$
	/** */
	public static final String C_NS_SVG = "svg"; //$NON-NLS-1$

}
