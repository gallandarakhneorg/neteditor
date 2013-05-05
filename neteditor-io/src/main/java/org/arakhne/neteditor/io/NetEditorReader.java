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

package org.arakhne.neteditor.io ;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.resource.ResourceRepository;

/** This interface represents a reader of NetEditor files.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public interface NetEditorReader {
	
	/** Replies the task progression model used by
	 * this reader.
	 * 
	 * @return the task progression model.
	 */
	public Progression getProgression();

	/** Set the task progression model used by
	 * this reader.
	 * 
	 * @param model is the task progression model.
	 */
	public void setProgression(Progression model);

	/** Replies the version of the reader.
	 * 
	 * @return the version of the reader.
	 */
	public String getReaderVersion();

	/** Replies the type of the last read content by this reader.
	 * 
	 * @return the type, or <code>null</code> if unknown.
	 */
	public NetEditorContentType getContentType();

	/** Read the specified input stream as a XML file,
	 * and extract a graph model and its associated figures.
	 * 
	 * @param <G> is the expected type of the graph to read from the stream.
	 * @param type is the expected type of the graph to read from the stream.
	 * @param inputFile is the file to read.
	 * @param figures is the collection of figures that were read from the input stream, from the front to the background.
	 * @return the graph read from the input stream.
	 * @throws IOException 
	 */
	public <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			File inputFile,
			Map<UUID,List<ViewComponent>> figures) throws IOException;
	
	/** Read the specified input stream as a XML file,
	 * and extract a graph model and its associated figures.
	 * 
	 * @param <G> is the expected type of the graph to read from the stream.
	 * @param type is the expected type of the graph to read from the stream.
	 * @param inputURL is the URL to read.
	 * @param figures is the collection of figures that were read from the input stream, from the front to the background.
	 * @return the graph read from the input stream.
	 * @throws IOException 
	 */
	public <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			URL inputURL,
			Map<UUID,List<ViewComponent>> figures) throws IOException;

	/** Read the specified input stream as a GXL file,
	 * and extract a graph model and its associated figures.
	 * 
	 * @param <G> is the expected type of the graph to read from the stream.
	 * @param type is the expected type of the graph to read from the stream.
	 * @param is is the input stream.
	 * @param figures is the collection of figures that were read from the input stream, from the front to the background.
	 * @return the graph read from the input stream.
	 * @throws IOException 
	 */
	public <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			InputStream is,
			Map<UUID,List<ViewComponent>> figures) throws IOException;
	
	/** Replies the resource repository used by this writer.
	 * 
	 * @return the resource repository.
	 */
	public ResourceRepository getResourceRepository();

	/** Replies the resource repository used by this writer.
	 * 
	 * @param repos the resource repository.
	 */
	public void setResourceRepository(ResourceRepository repos);

}
