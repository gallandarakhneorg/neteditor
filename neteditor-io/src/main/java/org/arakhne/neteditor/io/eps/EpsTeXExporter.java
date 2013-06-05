/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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

package org.arakhne.neteditor.io.eps ;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.arakhne.afc.io.filefilter.EPSFileFilter;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.io.FileCollection;
import org.arakhne.neteditor.io.tex.TexGenerator;
import org.arakhne.vmutil.FileSystem;


/** This exporter permits to create a EPS file with
 * combined TeX macros, from a graphic context.
 * <p>
 * This graphic context supports the
 * <a href="http://www.adobe.com/products/postscript/pdfs/PLRM.pdf">Postscript Reference Document Third Edition</a>, and the
 * <a href="http://partners.adobe.com/public/developer/en/ps/5002.EPSF_Spec.pdf">EPS Reference Document 3.0</a>.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EpsTeXExporter extends EpsExporter {

	private boolean outputIsEPS = true;

	private PrintWriter texStream = null;
	private File originalEpsFile = null;
	private File tmpEpsFile = null;
	private File texFile = null;

	/**
	 */
	public EpsTeXExporter() {
		//
	}

	@SuppressWarnings("resource")
	@Override
	protected EpsOutputStream createStream(File currentFile, OutputStream stream) throws IOException {
		if (currentFile!=null) {
			FileCollection fileCollection = getFileCollection();
			EPSFileFilter filter = new EPSFileFilter(false);
			this.outputIsEPS = filter.accept(currentFile);
			if (!this.outputIsEPS) {
				this.texFile = currentFile;
				if (fileCollection!=null) {
					this.originalEpsFile = FileSystem.replaceExtension(fileCollection.getMainFile(), EPSFileFilter.EXTENSION);
					this.tmpEpsFile = fileCollection.createSubFile(this.originalEpsFile.getName());
				}
				else {
					this.originalEpsFile = this.tmpEpsFile = FileSystem.replaceExtension(currentFile, EPSFileFilter.EXTENSION);
				}
				this.texStream = new PrintWriter(stream);
				return new EpsOutputStream(new FileOutputStream(this.tmpEpsFile));
			}
			this.texFile = FileSystem.replaceExtension(currentFile, ".pstex_t"); //$NON-NLS-1$
			if (fileCollection!=null) {
				this.texFile = fileCollection.createSubFile(FileSystem.largeBasename(this.texFile));
				this.originalEpsFile = fileCollection.getMainFile();
				this.tmpEpsFile = fileCollection.getTemporaryMainFile();
			}
			else {
				this.originalEpsFile = this.tmpEpsFile = currentFile;
			}
			this.texStream = new PrintWriter(this.texFile);
		}
		else {
			this.texStream = null;
			this.originalEpsFile = this.tmpEpsFile = currentFile;
			this.texFile = null;
		}
		return super.createStream(this.tmpEpsFile, stream);
	}

	@Override
	protected EpsGraphics2D prepareExport(File currentFile,
			EpsOutputStream stream, Rectangle2f bounds) throws IOException {
		return new EpsTeXGraphics2D(bounds);
	}

	@Override
	protected void finalizeExport(File currentFile, EpsOutputStream stream,
			Rectangle2f bounds, EpsGraphics2D graphicContext)
					throws IOException {
		super.finalizeExport(currentFile, stream, bounds, graphicContext);
		if (this.texStream!=null && this.texFile!=null) {
			EpsTeXGraphics2D g = (EpsTeXGraphics2D)graphicContext;
			TexGenerator.writeTeXT(
					this.originalEpsFile,
					this.texStream,
					g.getGeneratedTeX(),
					bounds.getWidth(),
					bounds.getHeight());
			this.texStream.close();
			this.texStream = null;
		}
	}
}
