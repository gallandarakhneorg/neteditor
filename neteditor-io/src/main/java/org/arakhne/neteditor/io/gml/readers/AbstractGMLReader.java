/* 
 * $Id$
 * 
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

package org.arakhne.neteditor.io.gml.readers ;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionInputStream;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.gml.GMLConstants;
import org.arakhne.neteditor.io.gml.GMLException;
import org.arakhne.neteditor.io.gml.parser.GMLParser;
import org.arakhne.neteditor.io.resource.ResourceRepository;
import org.arakhne.neteditor.io.xml.AbstractXMLToolReader;
import org.arakhne.util.text.Base64Coder;
import org.arakhne.vmutil.FileSystem;
import org.arakhne.vmutil.locale.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/** This class permits to read the
 *  <strong>graph-model</strong> into the GML format.
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
public abstract class AbstractGMLReader extends AbstractXMLToolReader implements GMLConstants {

	/** Create a GML reader that is corresponding to the given NetEditor/GML specification version.
	 * 
	 * @param version is the NetEditor/GML specification version
	 * @return the reader.
	 */
	public static AbstractGMLReader createGMLReader(String version) {
		if (version==null || version.equals(GMLReader1.SPECIFICATION_VERSION)) {
			return new GMLReader1();
		}
		return new GMLReader2();
	}
	
	/** Parse an integer value but do not fail if <var>s</var> does not represent an integer value.
	 * 
	 * @param s
	 * @return the integer value or <code>null</code> if <var>s</var> does not represent an integer value.
	 */
	public static Long parseIntNoFail(String s) {
		try {
			return Long.valueOf(s);
		}
		catch(Throwable _) {
			return null;
		}
	}

	/** Parse an integer value but do not fail if <var>s</var> does not represent a floating-point value.
	 * 
	 * @param s
	 * @return the floating-point value or <code>null</code> if <var>s</var> does not represent a floating-point value.
	 */
	public static Double parseFloatNoFail(String s) {
		try {
			return Double.valueOf(s);
		}
		catch(Throwable _) {
			return null;
		}
	}

	/** Create and reply a GML tag with the given name.
	 * 
	 * @param name
	 * @return the GML tag
	 */
	public static String tag(String name) {
		return "gml:"+name; //$NON-NLS-1$
	}

	/** Reply the name of a GML tag.
	 * 
	 * @param tag
	 * @return the name of the GML tag.
	 * @throws IOException
	 */
	public static String untag(String tag) throws IOException {
		if (tag.startsWith("gml:")) { //$NON-NLS-1$
			return tag.substring(4);
		}
		throw new GMLException();
	}
	
	/** Check if the GML root element corresponds to the given NetEditor/GML specification version.
	 * 
	 * @param gmlRoot
	 * @param resourceRepository is the repository of the resources from which they could be extracted.
	 * @param specificationVersion
	 * @throws IOException
	 */
	protected static void assertSpecificationVersion(Element gmlRoot, String specificationVersion, ResourceRepository resourceRepository) throws IOException {
		String version = extractSpecificationVersion(gmlRoot, resourceRepository);
		if (version!=null && !version.isEmpty() &&
			!version.equalsIgnoreCase(specificationVersion)) {
			throw new GMLException(Locale.getString("INVALID_SPECIFICATION_VERSION", version, specificationVersion)); //$NON-NLS-1$
		}
	}

	/** Extract the NetEditor/GML specification version.
	 * 
	 * @param gmlRoot
	 * @param resourceRepository is the repository of the resources from which they could be extracted.
	 * @return the version or <code>null</code>
	 * @throws IOException
	 */
	public static String extractSpecificationVersion(Element gmlRoot, ResourceRepository resourceRepository) throws IOException {
		try {
			return extractValueFromTag(gmlRoot, K_VERSION, String.class, resourceRepository);
		}
		catch(Throwable _) {
			return null;
		}
	}

	/** Extract a value from a XML representation of the GML content.
	 * 
	 * @param parent is the XML element (the parent) from which the data must be extracted.
	 * @param child is the name of the data to extract.
	 * @param type is the expected type of the data.
	 * @param resourceRepository is the repository of the resources from which they could be extracted.
	 * @return the data.
	 * @throws IOException
	 */
	protected static <T> T extractValueFromTag(Element parent, String child, Class<T> type, ResourceRepository resourceRepository) throws IOException {
		Element element = extractNode(parent, tag(child));
		return extractValueFromTag(element, type, resourceRepository);
	}

	/** Extract a value from a XML representation of the GML content.
	 * 
	 * @param elementN is the XML element (the XML node itself) from which the data must be extracted.
	 * @param vtype is the expected type of the data.
	 * @param resourceRepository is the repository of the resources from which they could be extracted.
	 * @return the data.
	 * @throws IOException
	 */
	protected static <T> T extractValueFromTag(Element elementN, Class<T> vtype, ResourceRepository resourceRepository) throws IOException {
		String type = elementN.getAttribute(K_TYPE);
		if (type!=null) {
			type = type.trim();
			if (K_INTEGER.equals(type)) {
				String rawValue = elementN.getAttribute(K_VALUE);
				return vtype.cast(Long.valueOf(rawValue));
			}
			if (K_FLOAT.equals(type)) {
				String rawValue = elementN.getAttribute(K_VALUE);
				return vtype.cast(Double.valueOf(rawValue));
			}
			if (K_LIST.equals(type)) {
				return vtype.cast(extractValueFromTag(elementN, resourceRepository));
			}
		}
		// Base type is string
		String rawValue = elementN.getAttribute(K_VALUE);
		return vtype.cast(rawValue);
	}

	/** Extract a value from a XML representation of the GML content.
	 * 
	 * @param elementN is the XML element (the XML node itself) from which the data must be extracted.
	 * @param resourceRepository is the repository of the resources from which they could be extracted.
	 * @return the data.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected static Object extractValueFromTag(Element elementN, ResourceRepository resourceRepository) throws IOException {
		Element typeN = extractNodeNoFail(elementN, tag(K_TYPE));
		if (typeN!=null) {
			String typeName = extractValueFromTag(typeN, String.class, resourceRepository);
			if (K_BOOLEAN.equals(typeName))	{
				Number value = extractValueFromTag(elementN, K_VALUE, Number.class, resourceRepository);
				if (value!=null && value.intValue()!=0) {
					return Boolean.TRUE;
				}
				return Boolean.FALSE;
			}
			if (K_UUID.equals(typeName)) {
				return enforceUUID(extractValueFromTag(elementN, K_VALUE, String.class, resourceRepository));
			}
			if (K_ENUM.equals(typeName)) {
				String name = extractValueFromTag(elementN, K_NAME, String.class, resourceRepository);
				String value = extractValueFromTag(elementN, K_VALUE, String.class, resourceRepository);
				Class<?> type;
				try {
					type = Class.forName(name);
					if (type.isEnum()) {
						Class<? extends Enum<?>> enumType = (Class<? extends Enum<?>>)type;
						for(Enum<?> enumConstant : enumType.getEnumConstants()) {
							if (enumConstant.name().equals(value)) {
								return enumConstant;
							}
						}
					}
					throw new GMLException();
				}
				catch (ClassNotFoundException e) {
					throw new GMLException(e);
				}
			}
			if (K_URL.equals(typeName)) {
				String href = extractValueFromTag(elementN, K_VALUE, String.class, resourceRepository);
				href = href.trim();
				try {
					URL u = (resourceRepository==null) ? null : resourceRepository.getURL(href);
					if (u==null) {
						u = FileSystem.convertStringToURL(href, true);
					}
					return u;
				}
				catch (Exception _) {
					try {
						File file;
						if (FileSystem.isWindowsNativeFilename(href)) {
							file = FileSystem.normalizeWindowsNativeFilename(href);
						}
						else {
							file = new File(href);
						}
						if (resourceRepository!=null) {
							URL root = resourceRepository.getRoot();
							if (root!=null) {
								return FileSystem.makeAbsolute(file, root);
							}
						}
						return file.toURI().toURL();
					}
					catch (Throwable e) {
						throw new GMLException(e);
					}
				}
			}
			if (K_SET.equals(typeName)) {
				Set<Object> theSet = new HashSet<Object>();
				for(Element valueN : elements(elementN, tag(K_VALUE))) {
					Object value = extractValueFromTag(valueN, Object.class, resourceRepository);
					if (value!=null) theSet.add(value);
				}
				return theSet;
			}
			if (K_LIST.equals(typeName)) {
				List<Object> theList = new ArrayList<Object>();
				for(Element valueN : elements(elementN, tag(K_VALUE))) {
					Object value = extractValueFromTag(valueN, Object.class, resourceRepository);
					if (value!=null) theList.add(value);
				}
				return theList;
			}
			if (K_COLLECTION.equals(typeName)) {
				Collection<Object> theCollection = new ArrayList<Object>();
				for(Element valueN : elements(elementN, tag(K_VALUE))) {
					Object value = extractValueFromTag(valueN, Object.class, resourceRepository);
					if (value!=null) theCollection.add(value);
				}
				return theCollection;
			}
			if (K_SERIAL.equals(typeName)) {
				String value = extractValueFromTag(elementN, K_VALUE, String.class, resourceRepository);
				value = Base64Coder.decodeString(value);
				ByteArrayInputStream bais = new ByteArrayInputStream(value.getBytes());
				try {
					ObjectInputStream ois = new ObjectInputStream(bais);
					try {
						try {
							return ois.readObject();
						}
						catch (ClassNotFoundException e) {
							throw new GMLException(e);
						}
					}
					finally {
						ois.close();
					}
				}
				finally {
					bais.close();
				}
			}
			if (K_NS.equals(typeName)) {
				Element valueN = extractNode(elementN, tag(K_VALUE));
				Namespace ns = new Namespace(untag(elementN.getNodeName()));
				for(Element eN : elements(valueN)) {
					Object value = extractValueFromTag(eN, Object.class, resourceRepository);
					if (value!=null) {
						ns.put(untag(eN.getNodeName()), value);
					}
				}
				return ns;
			}
			
			throw new GMLException(typeN.getNodeName()+"=="+typeName); //$NON-NLS-1$
		}
		// Base type
		String textValue = elementN.getTextContent();
		if (textValue!=null) {
			Long intValue = parseIntNoFail(textValue);
			if (intValue!=null) {
				return intValue;
			}
			Double doubleValue = parseFloatNoFail(textValue);
			if (doubleValue!=null) {
				return doubleValue;
			}
			return textValue;
		}
		return null;
	}

	/** Construct a new GraphWriter.          
	 */
	public AbstractGMLReader() {    
		//
	}

	@Override
	public final NetEditorContentType getContentType() {
		return NetEditorContentType.GML;
	}

	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, File inputFile,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		FileInputStream fis = new FileInputStream(inputFile);
		try {
			return read(type, fis, figures);
		}
		finally {
			fis.close();
		}
	}

	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, URL inputURL,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		InputStream is = inputURL.openStream();
		try {
			return read(type, is, figures);
		}
		finally {
			is.close();
		}
	}
		
	@SuppressWarnings("resource")
	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, InputStream is,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		Progression progression = getProgression();
		try {
			ProgressionUtil.init(progression, 0, 100000);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmldocument = builder.newDocument();
			InputStream iis = is;
			if (progression!=null) {
				iis = new ProgressionInputStream(iis,
						ProgressionUtil.sub(progression, 50000));
			}
			GMLParser parser = new GMLParser(iis);
			Element root = parser.execute(xmldocument);
			ProgressionUtil.advance(progression, 50000);
			return readGraph(type, root, figures,
					ProgressionUtil.subToEnd(progression));
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		finally {
			ProgressionUtil.end(progression);
		}
	}
	
	/**
	 * Read the graph from the XML representation of the GML content.
	 * 
	 * @param type is the type of the graph to read.
	 * @param gmlRoot is the XML representation of the GML content.
	 * @param figures are the figures extracted from the file from the front to the background.
	 * @param progression is the progression indicator that will be used.
	 * @return the graph.
	 * @throws IOException
	 */
	public abstract <G extends Graph<?, ?, ?, ?>> G readGraph(
			Class<G> type,
			Element gmlRoot,
			Map<UUID, List<ViewComponent>> figures,
			Progression progression) throws IOException;

	@Override
	protected String extractType(Element node) throws IOException {
		Element typeN = extractNode(node, tag(K_TYPE));
		if (typeN!=null) {
			String value = typeN.getAttribute(K_VALUE);
			if (value!=null) {
				value = value.trim();
				if (value.startsWith(SCHEMA_URL+"#")) { //$NON-NLS-1$
					return value.substring(SCHEMA_URL.length()+1);
				}
			}
		}
		throw new GMLException(Locale.getString("UNSUPPORTED_XML_NODE", node.getNodeName())); //$NON-NLS-1$
	}

	/** Definition of a namespace in the GML file.
	 * 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	protected static class Namespace extends TreeMap<String,Object> {

		private static final long serialVersionUID = 7721656428144981665L;

		private final String name;

		/**
		 * @param name
		 */
		public Namespace(String name) {
			this.name = name;
		}

		/**
		 * Fill the properties with the values inside this namespace.
		 * 
		 * @param header is the name header of all the values in this namespace.
		 * @param properties are the properties to fill.
		 */
		public void fillProperties(String header, Map<String,Object> properties) {
			assert(header!=null);
			for(Entry<String,Object> value : entrySet()) {
				if (value.getValue() instanceof Namespace) {
					((Namespace)value.getValue()).fillProperties(header+this.name+".", properties); //$NON-NLS-1$
				}
				else {
					properties.put(header+this.name+"."+value.getKey(), value.getValue()); //$NON-NLS-1$
				}
			}
		}

	}

}
