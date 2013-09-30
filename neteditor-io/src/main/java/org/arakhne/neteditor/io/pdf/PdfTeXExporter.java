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

package org.arakhne.neteditor.io.pdf ;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.arakhne.afc.io.filefilter.PDFFileFilter;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.vmutil.FileSystem;
import org.arakhne.neteditor.io.FileCollection;
import org.arakhne.neteditor.io.tex.TexGenerator;


/** This exporter permits to create a PDF file with
 * combined TeX macros, from a graphic context.
 * <p>
 * This exporter supports the 
 * <a href="http://partners.adobe.com/public/developer/en/pdf/PDFReference.pdf">PDF 1.4 Reference Document</a>.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PdfTeXExporter extends PdfExporter {

	private boolean outputIsPDF = true;

	private PrintWriter texStream = null;
	private File originalPdfFile = null;
	private File tmpPdfFile = null;
	private File texFile = null;

	/**
	 */
	public PdfTeXExporter() {
		//
	}

	@SuppressWarnings("resource")
	@Override
	protected PdfOutputStream createStream(File currentFile, OutputStream stream) throws IOException {
		if (currentFile!=null) {
			FileCollection fileCollection = getFileCollection();
			PDFFileFilter filter = new PDFFileFilter(false);
			this.outputIsPDF = filter.accept(currentFile);
			if (!this.outputIsPDF) {
				this.texFile = currentFile;
				if (fileCollection!=null) {
					this.originalPdfFile = FileSystem.replaceExtension(fileCollection.getMainFile(), PDFFileFilter.EXTENSION);
					this.tmpPdfFile = fileCollection.createSubFile(this.originalPdfFile.getName());
				}
				else {
					this.originalPdfFile = this.tmpPdfFile = FileSystem.replaceExtension(currentFile, PDFFileFilter.EXTENSION);
				}
				this.texStream = new PrintWriter(stream);
				return new PdfOutputStream(new FileOutputStream(this.tmpPdfFile));
			}
			this.texFile = FileSystem.replaceExtension(currentFile, ".pdftex_t"); //$NON-NLS-1$
			if (fileCollection!=null) {
				this.texFile = fileCollection.createSubFile(FileSystem.largeBasename(this.texFile));
				this.originalPdfFile = fileCollection.getMainFile();
				this.tmpPdfFile = fileCollection.getTemporaryMainFile();
			}
			else {
				this.originalPdfFile = this.tmpPdfFile = currentFile;
			}
			this.texStream = new PrintWriter(this.texFile);
		}
		else {
			this.texStream = null;
			this.originalPdfFile = this.tmpPdfFile = currentFile;
			this.texFile = null;
		}
		return super.createStream(this.tmpPdfFile, stream);
	}

	@Override
	protected PdfGraphics2D prepareExport(File currentFile,
			PdfOutputStream stream, Rectangle2f bounds) throws IOException {
		return new PdfTeXGraphics2D(bounds);
	}

	@Override
	protected void finalizeExport(File currentFile, PdfOutputStream stream,
			Rectangle2f bounds, PdfGraphics2D graphicContext)
					throws IOException {
		super.finalizeExport(currentFile, stream, bounds, graphicContext);
		if (this.texStream!=null && this.texFile!=null) {
			PdfTeXGraphics2D g = (PdfTeXGraphics2D)graphicContext;
			TexGenerator.writeTeXT(
					this.originalPdfFile,
					this.texStream,
					g.getGeneratedTeX(),
					bounds.getWidth(),
					bounds.getHeight());
			this.texStream.close();
			this.texStream = null;
		}
	}
}
