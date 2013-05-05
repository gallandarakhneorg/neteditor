/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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

package org.arakhne.neteditor.io.ngr ;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.arakhne.afc.io.stream.UnclosableOutputStream;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.afc.util.Pair;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.AbstractNetEditorWriter;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.NetEditorWriter;
import org.arakhne.neteditor.io.gml.GMLWriter;
import org.arakhne.neteditor.io.graphml.GraphMLWriter;
import org.arakhne.neteditor.io.gxl.GXLWriter;
import org.arakhne.neteditor.io.resource.ResourceRepository;


/** This class permits to export the
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
 * @see GXLWriter
 * @see GraphMLWriter
 * @see GMLWriter
 */
public class NGRWriter extends AbstractNetEditorWriter implements NGRConstants {
	
	private NetEditorContentType type = NetEditorContentType.GRAPHML;

	/**
	 */
	public NGRWriter() {
		//
	}
	
	/** Replies the type of the content that will be written by this NGRWriter.
	 * 
	 * @return the type, never <code>null</code>.
	 * @since 16.0
	 */
	public NetEditorContentType getContentType() {
		return this.type;
	}

	/** Set the type of the content that will be written by this NGRWriter.
	 * 
	 * @param type
	 * @since 16.0
	 */
	public void setContentType(NetEditorContentType type) {
		if (type!=null) {
			this.type = type;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("resource")
	@Override
	public void write(OutputStream outputStream, Graph<?, ?, ?, ?> graph)
			throws IOException {
		ProgressionUtil.init(getProgression(), 0, 300000);
		// Create temp file to be sure that there is not conflict between the reader and the writer of the resources
		File tempFile = File.createTempFile("neteditor", ".dta");  //$NON-NLS-1$//$NON-NLS-2$
		try {
			JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile));
			try {
				NetEditorWriter xmlWriter;
				String innerFilename;
				switch(getContentType()) {
				case GXL:
				{
					xmlWriter = new GXLWriter();
					innerFilename = GXL_INNER_FILENAME;
					break;
				}
				case GRAPHML:
				{
					GraphMLWriter graphmlWriter = new GraphMLWriter();
					xmlWriter = graphmlWriter;
					innerFilename = GRAPHML_INNER_FILENAME;
					graphmlWriter.setWriteSVGDrawings(false);
					break;
				}
				case GML:
				{
					GMLWriter gmlWriter = new GMLWriter();
					xmlWriter = gmlWriter;
					innerFilename = GML_INNER_FILENAME;
					break;
				}
				default:
					throw new IllegalStateException();
				}
				
				ResourceRepository rr = getResourceRepository();
				xmlWriter.setResourceRepository(rr);
				xmlWriter.setAnchorOutput(isAnchorOutput());
				xmlWriter.setProgression(ProgressionUtil.sub(getProgression(), 100000));
				jos.putNextEntry(new JarEntry(innerFilename));
				xmlWriter.write(new UnclosableOutputStream(jos), graph);
				jos.closeEntry();
				jos.putNextEntry(new JarEntry(RESOURCE_DIRNAME));
				jos.closeEntry();
				writeResources(jos, rr, ProgressionUtil.sub(getProgression(), 100000));
				jos.finish();
			}
			finally {
				jos.close();
			}
			copyToStream(outputStream, tempFile, ProgressionUtil.sub(getProgression(), 100000));
		}
		finally {
			if (tempFile.exists()) tempFile.delete();
			ProgressionUtil.end(getProgression());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("resource")
	@Override
	public <G extends Graph<?, ?, ?, ?>> void write(OutputStream outputStream, G graph,
			ViewComponentContainer<?, G> container) throws IOException {
		ProgressionUtil.init(getProgression(), 0, 300000);
		// Create temp file to be sure that there is not conflict between the reader and the writer of the resources
		File tempFile = File.createTempFile("neteditor", ".dta");  //$NON-NLS-1$//$NON-NLS-2$
		try {
			JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile));
			try {
				NetEditorWriter xmlWriter;
				String innerFilename;
				switch(getContentType()) {
				case GXL:
				{
					xmlWriter = new GXLWriter();
					innerFilename = GXL_INNER_FILENAME;
					break;
				}
				case GRAPHML:
				{
					GraphMLWriter graphmlWriter = new GraphMLWriter();
					xmlWriter = graphmlWriter;
					innerFilename = GRAPHML_INNER_FILENAME;
					graphmlWriter.setWriteSVGDrawings(false);
					break;
				}
				case GML:
				{
					GMLWriter gmlWriter = new GMLWriter();
					xmlWriter = gmlWriter;
					innerFilename = GML_INNER_FILENAME;
					break;
				}
				default:
					throw new IllegalStateException();
				}
	
				ResourceRepository rr = getResourceRepository();
				xmlWriter.setResourceRepository(rr);
				xmlWriter.setAnchorOutput(isAnchorOutput());
				xmlWriter.setProgression(ProgressionUtil.sub(getProgression(), 100000));
				jos.putNextEntry(new JarEntry(innerFilename));
				xmlWriter.write(new UnclosableOutputStream(jos), graph, container);
				jos.closeEntry();
				jos.putNextEntry(new JarEntry(RESOURCE_DIRNAME));
				jos.closeEntry();
				writeResources(jos, rr, ProgressionUtil.sub(getProgression(), 100000));
				jos.finish();
			}
			finally {
				jos.close();
			}
			copyToStream(outputStream, tempFile, ProgressionUtil.sub(getProgression(), 100000));
		}
		finally {
			if (tempFile.exists()) tempFile.delete();
			ProgressionUtil.end(getProgression());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("resource")
	@Override
	public void write(OutputStream outputStream, Collection<? extends Figure> figures)
			throws IOException {
		ProgressionUtil.init(getProgression(), 0, figures.size()*3000);
		
		// Create temp file to be sure that there is not conflict between the reader and the writer of the resources
		File tempFile = File.createTempFile("neteditor", ".dta");  //$NON-NLS-1$//$NON-NLS-2$
		try {
			JarOutputStream jos = new JarOutputStream(new FileOutputStream(tempFile));
			try {
				NetEditorWriter xmlWriter;
				String innerFilename;
				switch(getContentType()) {
				case GXL:
				{
					GXLWriter gxlWriter = new GXLWriter();
					xmlWriter = gxlWriter;
					innerFilename = GXL_INNER_FILENAME;
					break;
				}
				case GRAPHML:
					{
						GraphMLWriter graphmlWriter = new GraphMLWriter();
						xmlWriter = graphmlWriter;
						innerFilename = GRAPHML_INNER_FILENAME;
						graphmlWriter.setWriteSVGDrawings(false);
						break;
					}
				case GML:
				{
					GMLWriter gmlWriter = new GMLWriter();
					xmlWriter = gmlWriter;
					innerFilename = GML_INNER_FILENAME;
					break;
				}
				default:
					throw new IllegalStateException();
				}
				
				ResourceRepository rr = getResourceRepository();
				xmlWriter.setResourceRepository(rr);
				xmlWriter.setAnchorOutput(isAnchorOutput());
				xmlWriter.setProgression(ProgressionUtil.sub(getProgression(), figures.size()*1000));
				jos.putNextEntry(new JarEntry(innerFilename));
				xmlWriter.write(new UnclosableOutputStream(jos), figures);
				jos.closeEntry();
				jos.putNextEntry(new JarEntry(RESOURCE_DIRNAME));
				jos.closeEntry();
				writeResources(jos, rr, ProgressionUtil.sub(getProgression(), figures.size()*1000));
				jos.finish();
			}
			finally {
				jos.close();
			}
			copyToStream(outputStream, tempFile, ProgressionUtil.sub(getProgression(), figures.size()*1000));
		}
		finally {
			if (tempFile.exists()) tempFile.delete();
			 ProgressionUtil.end(getProgression());
		}
	}

	/** Write the content of the resource index.
	 * 
	 * @param stream is the stream inside which the indexes must be put.
	 * @param repository is the repository from which the index should be extracted.*
	 * @param progression notfies on the progression of the writing.
	 * @throws IOException
	 */
	@SuppressWarnings("static-method")
	private void writeResources(JarOutputStream stream, ResourceRepository repository, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, repository.getURLEntries().size()*2);
		byte[] buffer = new byte[512];
		int len;
		for(Entry<URL,String> entry : repository.getURLEntries()) {
			stream.putNextEntry(new JarEntry(RESOURCE_DIRNAME+entry.getValue()));
			InputStream is = entry.getKey().openStream();
			try {
				len = is.read(buffer);
				while (len>0) {
					stream.write(buffer, 0, len);
					len = is.read(buffer);
				}
			}
			finally {
				is.close();
			}
			stream.closeEntry();
			ProgressionUtil.advance(progression);
		}
		for(Pair<String,Image> pair : repository.getImages()) {
			stream.putNextEntry(new JarEntry(RESOURCE_DIRNAME+pair.getA()));
			VectorToolkit.writeImage(pair.getB(), "png", stream); //$NON-NLS-1$
			stream.closeEntry();
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	private static void copyToStream(OutputStream stream, File file, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, (int)file.length());
		FileInputStream fis = new FileInputStream(file);
		try {
			byte[] buffer = new byte[512];
			int n = fis.read(buffer);
			while (n>0) {
				stream.write(buffer, 0, n);
				ProgressionUtil.advance(progression, n);
				n = fis.read(buffer);
			}
			stream.flush();
		}
		finally {
			fis.close();
		}
		ProgressionUtil.end(progression);
	}
	
}
