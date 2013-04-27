/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.graphml.readers ;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.graphml.GraphMLConstants;
import org.arakhne.neteditor.io.graphml.GraphMLException;
import org.arakhne.neteditor.io.graphml.GraphMLReader;
import org.arakhne.neteditor.io.xml.AbstractXMLReader;
import org.arakhne.vmutil.locale.Locale;
import org.w3c.dom.Element;

/** This abstract class permits to read a
 *  <strong>graph-model</strong> from the GraphML format.
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
public abstract class AbstractGraphMLReader extends AbstractXMLReader implements GraphMLConstants {

	/** Create a GraphML reader that is corresponding to the given NetEditor/GraphML specification version.
	 * 
	 * @param version is the NetEditor/GraphML specification version
	 * @return the reader.
	 */
	public static AbstractGraphMLReader createGraphMLReader(String version) {
		if (version==null || version.equals(GraphMLReader1.SPECIFICATION_VERSION)) {
			return new GraphMLReader1();
		}
		return new GraphMLReader2();
	}

	private final Map<String,Element> keys = new TreeMap<String,Element>();
	
	/**
	 */
	public AbstractGraphMLReader() {
		//
	}

	/** {@inheritDoc}
	 */
	@Override
	public final NetEditorContentType getContentType() {
		return NetEditorContentType.GRAPHML;
	}

	@Override
	protected final URL getPublicDTD() {
		try {
			return new URL(GraphMLReader.getPublicDTD());
		}
		catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	protected final URL getSystemDTD() {
		return GraphMLReader.getSystemDTD();
	}

	/** Extract tha data with the given key.
	 * 
	 * @param parent is the parent node
	 * @param key is the key od the data to extract
	 * @return the node that is describing the data.
	 */
	public static Element extractData(Element parent, String key) {
		for(Element element : elements(parent, N_DATA)) {
			String ekey = element.getAttribute(A_KEY);
			if (ekey!=null && ekey.equals(key)) {
				return element;
			}
		}
		return null;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected final String extractType(Element node) throws IOException {
		Element typeN = extractData(node, C_KEY_NETEDITOR_TYPE);
		if (typeN!=null) {
			String value = typeN.getTextContent();
			if (value!=null) {
				value = value.trim();
				if (value.trim().startsWith(SCHEMA_URL+"#")) { //$NON-NLS-1$
					return value.substring(SCHEMA_URL.length()+1);
				}
			}
		}
		throw new GraphMLException(Locale.getString("UNSUPPORTED_XML_NODE", node.getNodeName())); //$NON-NLS-1$
	}
	
	/** Extract the keys from the GraphML.
	 * 
	 * @param root
	 * @param progression
	 * @throws IOException
	 */
	protected final void extractKeys(Element root, Progression progression) throws IOException {
		this.keys.clear();
		for(Element keyN : elements(root, N_KEY, progression)) {
			String id = keyN.getAttribute(A_ID);
			if (id!=null && !id.isEmpty()) {
				this.keys.put(id, keyN);
			}
		}
	}
	
	/** Clear the collection of the keys extracted from the GraphML.
	 */
	protected final void clearKeys() {
		this.keys.clear();
	}
	
	/** Replies the element the key with the given id.
	 * 
	 * @param keyId
	 * @return the element or <code>null</code>.
	 */
	protected final Element getKeyElement(String keyId) {
		return this.keys.get(keyId);
	}

	/** Replies the value of the attribute of the key with the given id.
	 * 
	 * @param keyId
	 * @param attributeName
	 * @return the value or <code>null</code>.
	 */
	private String getKeyAttribute(String keyId, String attributeName) {
		Element keyN = this.keys.get(keyId);
		if (keyN!=null) {
			return keyN.getAttribute(attributeName);
		}
		return null;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	protected final Map<String,Object> extractAttributes(Element node, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, node.getChildNodes().getLength()*2);
		boolean foundProprietaryAttrs = false;
		Map<String,Object> extractedProperties = new TreeMap<String,Object>();
		Progression subTask = ProgressionUtil.sub(progression, node.getChildNodes().getLength());
		for(Element dataN : elements(node, N_DATA, subTask)) {
			String dkey = dataN.getAttribute(A_KEY);
			if (dkey!=null && !dkey.isEmpty()) {
				if (dkey.startsWith(C_KEY_ATTR_PREFIX)) {
					// Embedded attribute
					String attrName = getKeyAttribute(dkey, A_ATTR_NAME);
					if (attrName!=null && !attrName.isEmpty()) {
						String attrType = getKeyAttribute(dkey, A_ATTR_TYPE);
						if (attrType!=null && !attrType.isEmpty()) {
							String value = dataN.getTextContent();
							if (value!=null) value = value.trim();
							if (value==null || value.isEmpty()) {
								// Try to get the default value for the key
								Element keyContent = getKeyElement(dkey);
								if (keyContent!=null) {
									Element defaultNode = extractNodeNoFail(keyContent, N_DEFAULT);
									if (defaultNode!=null) {
										value = defaultNode.getTextContent();
										if (value!=null) value = value.trim();
									}
								}
							}
							if (value!=null && !value.isEmpty()) {
								if (C_ATTR_TYPE_BOOLEAN.equals(attrType)) {
									extractedProperties.put(
											attrName,
											Boolean.valueOf(value.trim()));
								}
								else if (C_ATTR_TYPE_DOUBLE.equals(attrType)
										||C_ATTR_TYPE_FLOAT.equals(attrType)) {
									extractedProperties.put(
											attrName,
											Double.valueOf(value.trim()));
								}
								else if (C_ATTR_TYPE_INT.equals(attrType)
										||C_ATTR_TYPE_LONG.equals(attrType)) {
									extractedProperties.put(
											attrName,
											Long.valueOf(value.trim()));
								}
								else if (C_ATTR_TYPE_STRING.equals(attrType)) {
									extractedProperties.put(
											attrName,
											value.toString());
								}
							}
						}
					}
				}
				else if (dkey.equals(C_KEY_NETEDITOR_ATTRIBUTES)) {
					if (extractProprietaryAttributes(dataN, extractedProperties,
							ProgressionUtil.sub(subTask, 1))) {
						foundProprietaryAttrs = true;
					}
					ProgressionUtil.ensureNoSubTask(subTask);
				}
			}
		}
		
		if (!foundProprietaryAttrs) {
			// Use the default definition if it exists.
			Element keyContent = getKeyElement(C_KEY_NETEDITOR_ATTRIBUTES);
			if (keyContent!=null) {
				Element defaultNode = extractNodeNoFail(keyContent, N_DEFAULT);
				if (defaultNode!=null) {
					extractProprietaryAttributes(defaultNode, extractedProperties,
							ProgressionUtil.sub(progression, node.getChildNodes().getLength()));
				}
			}
		}
		
		ProgressionUtil.end(progression);
		
		return extractedProperties;
	}
	
	/** Extract any attribute dedicated to the NetEditor API.
	 * 
	 * @param dataN is the node of the data to parse.
	 * @param extractedProperties are the extracted properties.
	 * @param progression is the indicator that is used to notify on the progression of the extraction.
	 * @return <code>true</code> if a data was extracted, <code>false</code> otherwise.
	 * @throws IOException
	 */
	protected final boolean extractProprietaryAttributes(Element dataN, Map<String,Object> extractedProperties, Progression progression) throws IOException {
		boolean found = false;
		// Additional attributes
		for(Element attrN : elements(dataN, N_ATTR, progression)) {
			found = true;
			String name = attrN.getAttribute(A_NAME);
			if (name!=null && !name.isEmpty()) {
				List<Object> values = extractAttributeValue(attrN);
				if (!values.isEmpty()) { 
					extractedProperties.put(name, values.get(0));
				}
			}
		}
		return found;
	}
	
}
