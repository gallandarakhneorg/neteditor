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

package org.arakhne.neteditor.io ;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.arakhne.afc.progress.Progression;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Graph;

/** This interface represents exporters into vectorial
 *  formats such as xfig...
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface VectorialExporter {

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

	/** Replies if the shadows of the objects are exported or not.
	 * 
	 * @return <code>true</code> if the shadows of the objects are also exported.
	 */
	public boolean isShadowExported();

	/** Set if the shadows of the objects are exported or not.
	 * 
	 * @param export is <code>true</code> if the shadows of the objects are exported.
	 */
	public void setShadowExported(boolean export);

	/** Replies if this exporter is able to create
	 * a target file inside which all the specifications
	 * from <var>ViewGraphics2D</var> were supported.
	 * 
	 * @return <code>true</code> if the exporter completely support
	 * the <var>ViewGraphics2D</var> specifications;
	 * <code>false</code> if a part of the specifications are not supported.
	 */
	public boolean isSpecificationCompliant();
	
	/** Replies if this exporter is able to create
	 * shadows of the vectorials figures.
	 * 
	 * @return <code>true</code> if the exporter is able to
	 * create shadows; <code>false</code> if not.
	 */
	public boolean isShadowSupported();

	/** Replies the collection of files that is used by this exporter.
	 * <p>
	 * A collection of files may be used by the exporter to create
	 * files associated to the file to export.
	 * 
	 * @return the collection of files, or <code>null</code> if none.
	 */
	public FileCollection getFileCollection();

	/** Set the collection of files that is used by this exporter.
	 * <p>
	 * A collection of files may be used by the exporter to create
	 * files associated to the file to export.
	 * 
	 * @param c is the collection of files, or <code>null</code> if none.
	 */
	public void setFileCollection(FileCollection c);

	/** Exports the specified graph, its figures and all the other
	 * figures in the specified container into the given stream.
	 *
	 * @param output is the output file
	 * @param graph is the graph to write.
	 * @param container is the container of figures to write.
	 * @throws IOException
	 */
	public <G extends Graph<?,?,?,?>> void write( File output, G graph, ViewComponentContainer<?,G> container) throws IOException;

	/** Exports the specified figures and the associated model objects
	 * into a graph snipset in the given stream.
	 *
	 * @param output is the output file
	 * @param figures are the figures to export.
	 * @throws IOException
	 */
	public void write( File output, Collection<? extends Figure> figures) throws IOException;

	/** Exports the specified graph, its figures and all the other
	 * figures in the specified container into the given stream.
	 *
	 * @param output is the output stream.
	 * @param graph is the graph to write.
	 * @param container is the container of figures to write.
	 * @throws IOException
	 */
	public <G extends Graph<?,?,?,?>> void write( OutputStream output, G graph, ViewComponentContainer<?,G> container) throws IOException;

	/** Exports the specified figures and the associated model objects
	 * into a graph snipset in the given stream.
	 *
	 * @param output is the output stream.
	 * @param figures are the figures to export.
	 * @throws IOException
	 */
	public void write( OutputStream output, Collection<? extends Figure> figures) throws IOException;

}
