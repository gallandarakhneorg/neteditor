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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.arakhne.afc.io.stream.UnclosableInputStream;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.AbstractNetEditorReader;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.NetEditorReader;
import org.arakhne.neteditor.io.gml.GMLReader;
import org.arakhne.neteditor.io.graphml.GraphMLReader;
import org.arakhne.neteditor.io.gxl.GXLReader;
import org.arakhne.neteditor.io.resource.ResourceRepository;
import org.arakhne.vmutil.FileSystem;

/** This class permits to read the
 *  <strong>graph-model</strong> into the NGR format.
 *  <p>
 *  The NGR format (or Neteditor GRaph format) is a zip
 *  archive that contains a file describing the graph in
 *  XML format and any additional data pointed by the
 *  XML file.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see GXLReader
 * @see GraphMLReader
 * @see GMLReader
 */
public class NGRReader extends AbstractNetEditorReader implements NGRConstants {

	private boolean isDtdValidation = true;
	private boolean connectFigures = true;
	private NetEditorContentType type = null;
	
	/**
	 */
	public NGRReader() {
		//
	}
	
	/**
	 * Set the flag that permits to validate, or not, the DTD.
	 * 
	 * @param dtdValidation indicates if the DTD should be validated.
	 */
	public void setDTDValidation(boolean dtdValidation) {
		this.isDtdValidation = dtdValidation;
	}

	/**
	 * Replies if the DTD is validating when reading.
	 * 
	 * @return <code>true</code> if the DTD is validated; otherwise <code>false</code>.
	 */
	public boolean isDTDValidation() {
		return this.isDtdValidation;
	}

	/**
	 * Set the flag that permits to connect the model objects and
	 * the figures, or not.
	 * 
	 * @param connectFigures indicates if the figures should be linked to their model
	 * objects.
	 */
	public void setFigureConnection(boolean connectFigures) {
		this.connectFigures = connectFigures;
	}

	/**
	 * Replies the flag that permits to connect the model objects and
	 * the figures, or not.
	 * 
	 * @return <code>true</code> if the figures should be linked to their model
	 * objects; otherwise <code>false</code>.
	 */
	public boolean isFigureConnection() {
		return this.connectFigures;
	}

	/** {@inheritDoc}
	 */
	@Override
	public NetEditorContentType getContentType() {
		return this.type;
	}

	/** {@inheritDoc} 
	 */
	@SuppressWarnings("resource")
	@Override
	public <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			File inputFile,
			Map<UUID,List<ViewComponent>> figures) throws IOException {
		extractResources(
				inputFile.toURI().toURL(),
				getResourceRepository(),
				new JarInputStream(new FileInputStream(inputFile)));

		JarInputStream jis = new JarInputStream(new FileInputStream(inputFile));
		try {
			return read(type, 
					jis,
					figures);
		}
		finally {
			jis.close();
		}
	}
	
	/** {@inheritDoc} 
	 */
	@SuppressWarnings("resource")
	@Override
	public <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			URL inputURL,
			Map<UUID,List<ViewComponent>> figures) throws IOException {
		extractResources(
				inputURL,
				getResourceRepository(),
				new JarInputStream(inputURL.openStream()));
		
		JarInputStream jis = new JarInputStream(inputURL.openStream());
		try {
			return read(type,
					jis,
					figures);
		}
		finally {
			jis.close();
		}
	}
	
	/** {@inheritDoc} 
	 */
	@Override
	public <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			InputStream inputStream,
			Map<UUID,List<ViewComponent>> figures) throws IOException {
		JarInputStream jis = new JarInputStream(inputStream);
		try {
			return read(type,
					jis,
					figures);
		}
		finally {
			jis.close();
		}
	}

	private static void extractResources(URL url, ResourceRepository resourceRepository, JarInputStream jis) throws IOException {
		resourceRepository.setRoot(FileSystem.dirname(url));

		JarEntry je = jis.getNextJarEntry();
		while (je!=null) {
			if (!je.isDirectory() && 
				je.getName().startsWith(RESOURCE_DIRNAME)) {
				String name = je.getName().substring(10);
				resourceRepository.register(name,
						FileSystem.toJarURL(url, je.getName()));
			}
			je = jis.getNextJarEntry();
		}
		jis.close();
	}

	@SuppressWarnings("resource")
	private <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			JarInputStream jis,
			Map<UUID,List<ViewComponent>> figures) throws IOException {

		// Search for the right reader according to the name of the inner file.
		NetEditorReader reader = null;
		this.type = null;
		JarEntry je = jis.getNextJarEntry();
		while (reader==null && je!=null) {
			if (!je.isDirectory()) {
				if (GXL_INNER_FILENAME.equalsIgnoreCase(je.getName())) {
					GXLReader gxlReader = new GXLReader();
					reader = gxlReader;
					gxlReader.setDTDValidation(isDTDValidation());
					gxlReader.setFigureConnection(isFigureConnection());
					this.type = NetEditorContentType.GXL;
				}
				else if (GRAPHML_INNER_FILENAME.equalsIgnoreCase(je.getName())) {
					GraphMLReader gmlReader = new GraphMLReader();
					reader = gmlReader;
					gmlReader.setDTDValidation(isDTDValidation());
					gmlReader.setFigureConnection(isFigureConnection());
					this.type = NetEditorContentType.GRAPHML;
				}
				else if (GML_INNER_FILENAME.equalsIgnoreCase(je.getName())) {
					GMLReader gmlReader = new GMLReader();
					reader = gmlReader;
					this.type = NetEditorContentType.GML;
				}
			}
			if (reader==null) je = jis.getNextJarEntry();
		}

		G graph = null;
		
		if (reader!=null) {
			reader.setResourceRepository(getResourceRepository());
			reader.setProgression(getProgression());
			graph = reader.read(type, new UnclosableInputStream(jis), figures);			
		}
		
		if (graph==null) throw new IOException();
		return graph;
	}

}
