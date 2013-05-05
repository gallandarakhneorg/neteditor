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

package org.arakhne.neteditor.io ;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.arakhne.afc.progress.Progression;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.resource.ResourceRepository;

/** This interface represents a writer of NetEditor files.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public interface NetEditorWriter {

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

	/** Replies the version of the writer.
	 * 
	 * @return the version of the writer.
	 */
	public String getWriterVersion();
	
	/**
	 * Write the graph representation of the specified graph
	 * into the specified stream, but not the view objects.
	 * 
	 * @param os is the stream to write in.
	 * @param graph is the graph to export in the stream.
	 * @throws IOException
	 */
	public void write(OutputStream os, Graph<?,?,?,?> graph) throws IOException;

	/**
	 * Write the graph representation of the specified graph
	 * into the specified stream, and the view objects
	 * stored in the given container.
	 * 
	 * @param os is the stream to write in.
	 * @param graph is the graph to export in the stream.
	 * @param container is the figure container from which figures may be extracted.
	 * @throws IOException
	 */
	public <G extends Graph<?,?,?,?>> void write(OutputStream os, G graph, ViewComponentContainer<?,G> container) throws IOException;

	/**
	 * Write the graph representation of the specified graph
	 * into the specified stream, and the view objects
	 * stored in the given container.
	 * 
	 * @param os is the stream to write in.
	 * @param figures are the figures to output, and linked to the graph to output.
	 * The list is sorted from the front layer to the background layer.
	 * @throws IOException
	 */
	public void write(OutputStream os, Collection<? extends Figure> figures) throws IOException;

	
	/** Replies if this writer is outputing the anchors in addition
	 * to the graph structure composed of the nodes and the edges.
	 * 
	 * @return <code>true</code> if the anchors are output;
	 * <code>false</code> otherwise.
	 */
	public boolean isAnchorOutput();
	
	/** Set if this writer is outputing the anchors in addition
	 * to the graph structure composed of the nodes and the edges.
	 * 
	 * @param anchorOutput is <code>true</code> if the anchors are output;
	 * <code>false</code> otherwise.
	 */
	public void setAnchorOutput(boolean anchorOutput);
	
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
