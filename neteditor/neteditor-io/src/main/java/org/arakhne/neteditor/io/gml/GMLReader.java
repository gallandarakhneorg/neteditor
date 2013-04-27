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

package org.arakhne.neteditor.io.gml ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.arakhne.afc.progress.ProgressionInputStream;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.gml.parser.GMLParser;
import org.arakhne.neteditor.io.gml.readers.AbstractGMLReader;
import org.arakhne.neteditor.io.resource.ResourceRepository;
import org.arakhne.neteditor.io.xml.AbstractXMLToolReader;
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
public class GMLReader extends AbstractXMLToolReader implements GMLConstants {

	/** Construct a new GraphWriter.          
	 */
	public GMLReader() {    
		//
	}

	@Override
	public final NetEditorContentType getContentType() {
		return NetEditorContentType.GML;
	}

	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, File inputFile,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		ResourceRepository rr = getResourceRepository();
		if (rr!=null) rr.setRoot(FileSystem.dirname(inputFile));
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
		ResourceRepository rr = getResourceRepository();
		if (rr!=null) rr.setRoot(FileSystem.dirname(inputURL));
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
		try {
			ProgressionUtil.init(getProgression(), 0, 100000);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmldocument = builder.newDocument();

			GMLParser parser = new GMLParser(new ProgressionInputStream(
					is,	ProgressionUtil.sub(getProgression(), 50000)));
			Element root = parser.execute(xmldocument);
			
			String specVersion = AbstractGMLReader.extractSpecificationVersion(root, getResourceRepository());
			
			AbstractGMLReader backgroundReader = AbstractGMLReader.createGMLReader(specVersion);
			backgroundReader.setResourceRepository(getResourceRepository());
			
			ProgressionUtil.ensureNoSubTask(getProgression());

			G g = backgroundReader.readGraph(type, root, figures, ProgressionUtil.subToEnd(getProgression()));
			
			ProgressionUtil.end(getProgression());
			
			return g;
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	protected String extractType(Element node) throws IOException {
		Element typeN = extractNode(node, AbstractGMLReader.tag(K_TYPE));
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

}
