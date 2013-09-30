/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.gxl.readers ;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.gxl.GXLConstants;
import org.arakhne.neteditor.io.gxl.GXLException;
import org.arakhne.neteditor.io.gxl.GXLReader;
import org.arakhne.neteditor.io.xml.AbstractXMLReader;
import org.w3c.dom.Element;

/** This abstract class permits to read a
 *  <strong>graph-model</strong> from the GXL format.
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
 * @see "http://www.gupro.de/GXL/"
 */
public abstract class AbstractGXLReader extends AbstractXMLReader implements GXLConstants {

	/** Create a GXL reader that is corresponding to the given NetEditor/GXL specification version.
	 * 
	 * @param version is the NetEditor/GXL specification version
	 * @return the reader.
	 */
	public static AbstractGXLReader createGXLReader(String version) {
		if (version==null || version.equals(GXLReader1.SPECIFICATION_VERSION)) {
			return new GXLReader1();
		}
		return new GXLReader2();
	}

	/**
	 */
	public AbstractGXLReader() {
		//
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public final NetEditorContentType getContentType() {
		return NetEditorContentType.GXL;
	}

	@Override
	protected final URL getPublicDTD() {
		try {
			return new URL(GXLReader.getPublicDTD());
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	protected final URL getSystemDTD() {
		return GXLReader.getSystemDTD();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	protected Map<String,Object> extractAttributes(Element node, Progression progression) throws IOException {
		Map<String,Object> extractedProperties = new TreeMap<String,Object>();
		for(Element attrN : elements(node, N_ATTR, progression)) {
			String name = attrN.getAttribute(A_NAME);
			if (name!=null && !name.isEmpty()) {
				List<Object> values = extractAttributeValue(attrN);
				if (!values.isEmpty()) { 
					extractedProperties.put(name, values.get(0));
				}
			}
		}
		return extractedProperties;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected String extractType(Element node) throws IOException {
		Element typeN = extractNode(node, N_TYPE);
		String value = typeN.getAttribute(A_XLINK_HREF);
		if (value!=null && !value.isEmpty()) {
			if (value.startsWith(SCHEMA_URL+"#")) { //$NON-NLS-1$
				value = value.substring(SCHEMA_URL.length()+1);
				if (!value.startsWith("__internal_")) { //$NON-NLS-1$
					return value;
				}
			}
		}
		throw new GXLException(Locale.getString("UNSUPPORTED_XML_NODE", node.getNodeName())); //$NON-NLS-1$
	}

	/** Extract the type of the specified node as a string, assuming that it is an
	 * internal type of NetEditor.
	 * 
	 * @param node is the node to explore
	 * @return the type of the node, or <code>null</code>.
	 * @throws IOException
	 */
	protected static String extractInternalType(Element node) throws IOException {
		Element typeN = extractNode(node, N_TYPE);
		String ovalue = typeN.getAttribute(A_XLINK_HREF);
		String value = ovalue;
		if (value!=null && !value.isEmpty()) {
			if (value.startsWith(SCHEMA_URL+"#")) { //$NON-NLS-1$
				value = value.substring(SCHEMA_URL.length()+1);
				if (value.startsWith("__internal_")) { //$NON-NLS-1$
					return ovalue;
				}
			}
		}
		return null;
	}

	/** Replies if the specified node is the node for a graph model.
	 * 
	 * @param node is the node to test.
	 * @return <code>true</code> if the specified node contains a graph model;
	 * otherwise <code>false</code>.
	 * @throws IOException
	 */
	protected static boolean isFigureModel(Element node) throws IOException {
		try {
			String type = extractInternalType(node);
			if (C_INTERNAL_VIEW_TYPE.equals(type)) {
				return true;
			}
		}
		catch(Throwable _) {
			//
		}
		return false;
	}

}
