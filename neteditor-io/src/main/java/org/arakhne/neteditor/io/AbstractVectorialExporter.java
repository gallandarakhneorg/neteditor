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

package org.arakhne.neteditor.io ;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Graph;

/** Abstract implementation of a vector exporter.
 *
 * @param <D> is the type of the graphics 2D supported by this implementation.
 * @param <S> is the type of the supported streams.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractVectorialExporter<D extends AbstractVectorialExporterGraphics2D, S extends OutputStream> implements VectorialExporter {

	private boolean exportShadows = true;
	private File file = null;
	private Progression taskProgression = null;
	private FileCollection fileCollection = null;

	/**
	 */
	public AbstractVectorialExporter() {
		//
	}
	
	/** Replies the name of the file to output.
	 * 
	 * @return the name of the file or <code>null</code>.
	 */
	protected File getFile() {
		return this.file;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progression getProgression() {
		return this.taskProgression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProgression(Progression model) {
		Progression old = this.taskProgression;
		this.taskProgression = model;
		if (old!=null && this.taskProgression!=null) {
			this.taskProgression.setProperties(
					old.getValue(),
					old.getMinimum(),
					old.getMaximum(),
					old.isAdjusting(),
					old.getComment());
			this.taskProgression.setIndeterminate(old.isIndeterminate());
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isShadowExported() {
		return this.exportShadows;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setShadowExported(boolean export) {
		this.exportShadows = export;
	}	
	
	@Override
	public void setFileCollection(FileCollection c) {
		this.fileCollection = c;
	}
	
	@Override
	public FileCollection getFileCollection() {
		return this.fileCollection;
	}

	/** {@inheritDoc}
	 */
	@Override
	public <G extends Graph<?,?,?,?>> void write( File output, G graph, ViewComponentContainer<?,G> container) throws IOException {
		this.file = output;
		FileOutputStream fos = new FileOutputStream(output);
		try {
			write(fos, graph, container);
		}
		finally {
			fos.close();
			this.file = null;
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void write( File output, Collection<? extends Figure> figures) throws IOException {
		this.file = output;
		FileOutputStream fos = new FileOutputStream(output);
		try {
			write(fos, figures);
		}
		finally {
			fos.close();
			this.file = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(OutputStream output, Collection<? extends Figure> figures)
			throws IOException {
		ProgressionUtil.init(getProgression(), 0, figures.size()*2+3);
		Rectangle2f bounds = null;
		for(Figure figure : figures) {
			Rectangle2f fr = figure.getBounds();
			if (fr!=null) {
				if (bounds==null) bounds = fr.clone();
				else bounds = bounds.createUnion(fr);
			}
			ProgressionUtil.advance(getProgression());
		}
		if (bounds!=null && !bounds.isEmpty()) {
			Rectangle2f r;
			S stream = createStream(this.file, output);
			try {
				D g = prepareExport(this.file, stream, bounds);
				if (g==null) throw new IOException();
				g.prolog();
				g.pushRenderingContext(null, null, bounds);
				ProgressionUtil.advance(getProgression());
				for(Figure figure : figures) {
					r = figure.getBounds();
					g.pushRenderingContext(figure, figure.getClip(r), r);
					figure.paint(g);
					g.popRenderingContext();
					ProgressionUtil.advance(getProgression());
				}
				g.popRenderingContext();
				g.epilog();
				finalizeExport(this.file, stream, bounds, g);
			}
			finally {
				stream.close();
			}
		}
		ProgressionUtil.end(getProgression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <G extends Graph<?,?,?,?>> void write(OutputStream output, G graph, ViewComponentContainer<?,G> container) throws IOException {
		synchronized(container.getTreeLock()) {
			ProgressionUtil.init(getProgression(), 0, 1000);
			S stream = createStream(this.file, output);
			ProgressionUtil.advance(getProgression(), 10);
			try {
				D g = prepareExport(this.file, stream, container.getComponentBounds());
				if (g==null) throw new IOException();
				ProgressionUtil.advance(getProgression(), 100);
				g.prolog();
				g.pushRenderingContext(null, null, container.getComponentBounds());
				boolean oldShadow = container.isShadowDrawn();
				container.setShadowDrawn(isShadowExported() && isShadowSupported());
				ProgressionUtil.advance(getProgression(), 10);
				try {
					container.paintViewComponents(g);
				}
				finally {
					container.setShadowDrawn(oldShadow);
				}
				ProgressionUtil.advance(getProgression(), 780);
				g.popRenderingContext();
				g.epilog();
				finalizeExport(this.file, stream, container.getComponentBounds(), g);
			}
			finally {
				stream.close();
			}
			ProgressionUtil.end(getProgression());
		}
	}
	
	/** Wrap the specified stream to a stream that may 
	 * be properly used by the exporter.
	 * 
	 * @param currentFile is the name of the file currently under creation; may be <code>null</code>.
	 * @param stream is the stream to wrap.
	 * @return the wrapping stream.
	 * @throws IOException
	 */
	protected abstract S createStream(File currentFile, OutputStream stream) throws IOException;

	/** Create the graphics context to use to export the data.
	 * 
	 * @param currentFile is the name of the file currently under creation; may be <code>null</code>.
	 * @param stream is the output stream, never <code>null</code>.
	 * @param documentBounds are the bounds of the document.
	 * @return the graphics context to use to export the data.
	 * @throws IOException
	 */
	protected abstract D prepareExport(File currentFile, S stream, Rectangle2f documentBounds) throws IOException;

	/** Invoked to finalize the export.
	 * 
	 * @param currentFile is the name of the file currently under creation; may be <code>null</code>.
	 * @param stream is the output stream, never <code>null</code>.
	 * @param graphicContext is the grapihcal context used to generate the vector picture.
	 * @param documentBounds are the bounds of the document.
	 * @throws IOException
	 */
	protected abstract void finalizeExport(File currentFile, S stream, Rectangle2f documentBounds, D graphicContext) throws IOException;

}
