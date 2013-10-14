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

package org.arakhne.neteditor.io.pdf ;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.neteditor.io.AbstractVectorialExporter;

/** This exporter permits to create a PDF file
 *  from a graphic context.
 * <p>
 * This exporter supports the 
 * <a href="http://partners.adobe.com/public/developer/en/pdf/PDFReference.pdf">PDF 1.4 Reference Document</a>.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PdfExporter extends AbstractVectorialExporter<PdfGraphics2D, PdfOutputStream> {
	
	/** Character for the new line in the PDF file.
	 */
	protected static final char CR = 0x0A;
	
	/**
	 */
	public PdfExporter() {
		//
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public boolean isShadowSupported() {
		return true;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final boolean isSpecificationCompliant() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PdfOutputStream createStream(File currentFile, OutputStream stream) throws IOException {
		return new PdfOutputStream(stream);
	}
	
	/** Replies the size of the stream.
	 * 
	 * @param stream
	 * @return the size of the stream.
	 * @throws IOException
	 */
	protected static int size(PdfOutputStream stream) throws IOException {
		stream.flush();
		return stream.size();
	}
	
	/** Write the specified bytes in the PDF.
	 * 
	 * @param stream
	 * @param addNewLine indicates if a new line character must be added at the end.
	 * @param bytes
	 * @throws IOException
	 */
	protected static void writeRaw(PdfOutputStream stream, boolean addNewLine, byte... bytes) throws IOException {
		stream.write(bytes);
		if (addNewLine) {
			stream.write(CR);
		}
	}
	
	/**
	 * Write the specified objects as strings in the output stream.
	 * The passed objects are converted to strings.
	 * @param stream is the stream to write in.
	 * @param text are the Objects to be written to the document stream.
	 * @return <code>true</code> if something was written in the stream;
	 * otherwise <code>false</code>.
	 * @throws IOException
	 */
	protected static boolean writeln(PdfOutputStream stream, Object... text) throws IOException {
		boolean changed = write(stream, text);
		if (changed) {
			stream.write(CR);
		}
		return changed;	
	}
	
	/**
	 * Write the specified objects as strings in the output stream.
	 * The passed objects are converted to strings.
	 * @param stream is the stream to write in.
	 * @param text are the Objects to be written to the document stream.
	 * @return <code>true</code> if something was written in the stream;
	 * otherwise <code>false</code>.
	 * @throws IOException
	 */
	protected static boolean write(PdfOutputStream stream, Object... text) throws IOException {
		boolean changed = false;
		if (text!=null && text.length>0) {
			for(Object t : text) {
				if (t!=null) {
					String str = t.toString();
					if (str!=null && !str.isEmpty()) {
						stream.write(str);
						changed = true;
					}
				}
			}
		}
		return changed;
	}
	
	/**
	 * Write a collection of elements to the document stream as PDF object.
	 * The passed objects are converted to strings.
	 * @param id is the identifier of the object.
	 * @param stream is the stream to write in.
	 * @param strs Objects to be written to the document stream.
	 * @throws IOException
	 */
	protected static void writePdfObject(int id, PdfOutputStream stream, Object... strs) throws IOException {
		writeln(stream, id, " 0 obj"); //$NON-NLS-1$
		writePdfDictionary(stream, strs);
		writeln(stream, "endobj"); //$NON-NLS-1$
	}
	
	/**
	 * Write a PDF dictionary from the specified collection of objects.
	 * The passed objects are converted to strings. Every object with odd
	 * position is used as key, every object with even position is used
	 * as value.
	 * @param stream is the stream to write inside.
	 * @param strs Objects to be written to dictionary
	 * @throws IOException
	 */
	protected static void writePdfDictionary(PdfOutputStream stream, Object... strs) throws IOException {
		write(stream, "<< "); //$NON-NLS-1$
		for (int i = 0; i < strs.length; i += 2) {
			writeln(stream, "/", strs[i], " ", strs[i + 1]);  //$NON-NLS-1$//$NON-NLS-2$
		}
		writeln(stream, ">>"); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PdfGraphics2D prepareExport(File currentFile, PdfOutputStream stream, Rectangle2f bounds) throws IOException {
		return new PdfGraphics2D();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalizeExport(File currentFile, PdfOutputStream stream, Rectangle2f bounds, PdfGraphics2D graphicContext)
			throws IOException {
		byte[] generateBytes = graphicContext.getGeneratedString().getBytes();
		
		Rectangle2f pdfBounds = PdfUtil.toPdf(bounds);
		int x1 = (int) Math.floor(pdfBounds.getMinX());
		int y1 = (int) Math.floor(pdfBounds.getMinY());
		int x2 = (int) Math.ceil(pdfBounds.getMaxX());
		int y2 = (int) Math.ceil(pdfBounds.getMaxY());

		Map<Integer,Integer> objectMap = new TreeMap<Integer, Integer>();
		
		int idCatalogue = 1;
		int idOutlines = 2;
		int idPages = 3;
		int idMainPage = 4;
		int idContents = 5;
		int idResources = 6;

		writeln(stream, "%PDF-1.4"); //$NON-NLS-1$
		writeln(stream, "%% Creator: Arakhne.org NetEditor ", getClass().getName()); //$NON-NLS-1$
		writeln(stream, "%% CreationDate: ", new Date()); //$NON-NLS-1$
		// Object 0: Catalogue
		objectMap.put(idCatalogue, size(stream));
		writePdfObject(idCatalogue,  stream,
			"Type", "/Catalog", //$NON-NLS-1$ //$NON-NLS-2$
			"Outlines", idOutlines+" 0 R", //$NON-NLS-1$ //$NON-NLS-2$
			"Pages", idPages+" 0 R" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 1: Outlines
		objectMap.put(idOutlines, size(stream));
		writePdfObject( idOutlines, stream,
			"Type", "Outlines", //$NON-NLS-1$ //$NON-NLS-2$
			"Count", "0" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 2: Pages
		objectMap.put(idPages, size(stream));
		writePdfObject( idPages, stream,
			"Type", "/Pages", //$NON-NLS-1$ //$NON-NLS-2$
			"Kids", "["+idMainPage+" 0 R]", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"Count", "1" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 3: the single page
		objectMap.put(idMainPage, size(stream));
		writePdfObject( idMainPage, stream,
			"Type", "/Page", //$NON-NLS-1$ //$NON-NLS-2$
			"Parent", idPages+" 0 R", //$NON-NLS-1$ //$NON-NLS-2$
			"MediaBox", String.format("[%d %d %d %d]", x1, y1, x2, y2), //$NON-NLS-1$ //$NON-NLS-2$
			"Contents", idContents+" 0 R", //$NON-NLS-1$ //$NON-NLS-2$
			"Resources", idResources+" 0 R" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 4: the page content itself
		objectMap.put(idContents, size(stream));
		writeln(stream, idContents+" 0 obj"); //$NON-NLS-1$
		writePdfDictionary(stream, "Length", generateBytes.length); //$NON-NLS-1$
		writeln(stream, "stream"); //$NON-NLS-1$
		
		writeRaw(stream, true, generateBytes);
		
		// Footer
		writeln(stream, "endstream"); //$NON-NLS-1$
		writeln(stream, "endobj"); //$NON-NLS-1$

		// Object 5: the resources
		objectMap.put(idResources, size(stream));
		writeln(stream, idResources+" 0 obj"); //$NON-NLS-1$
		writeln(stream, "<<"); //$NON-NLS-1$
		writeln(stream, " /ProcSet [/PDF /Text /ImageB /ImageC /ImageI]"); //$NON-NLS-1$

		int objectId = idResources+1;
		Map<Integer,String> differedOutput = new TreeMap<Integer, String>();
		
		// Add resources for images
		Map<String,Image> images = graphicContext.getImageResources();
		if (!images.isEmpty()) {
			writeln(stream, " /XObject <<"); //$NON-NLS-1$

			for (Entry<String,Image> entry : images.entrySet()) {
				// Add image declaration
				String resourceId = entry.getKey();
				writeln(stream,
						"  /",  //$NON-NLS-1$
						resourceId, " ", //$NON-NLS-1$
						objectId, " 0 R"); //$NON-NLS-1$

				// Add data of images
				Image image = entry.getValue();
				String imageData = PdfUtil.toPdf(image);
				StringBuilder b = new StringBuilder();
				b.append(objectId);
				b.append(" 0 obj"); //$NON-NLS-1$
				b.append(CR);
				b.append("<<"); //$NON-NLS-1$
				b.append(CR);
				b.append("  /Type /XObject"); //$NON-NLS-1$
				b.append(CR);
				b.append("  /Subtype /Image"); //$NON-NLS-1$
				b.append(CR);
				b.append("  /Width "); //$NON-NLS-1$
				b.append(image.getWidth(null));
				b.append(CR);
				b.append("  /Height "); //$NON-NLS-1$
				b.append(image.getHeight(null));
				b.append(CR);
				b.append("  /ColorSpace /DeviceRGB"); //$NON-NLS-1$
				b.append(CR);
				b.append("  /BitsPerComponent 8"); //$NON-NLS-1$
				b.append(CR);
				b.append("  /Interpolate true"); //$NON-NLS-1$
				b.append(CR);
				b.append("  /Length "); //$NON-NLS-1$
				b.append(imageData.length());
				b.append(CR);
				b.append("  /Filter /ASCIIHexDecode"); //$NON-NLS-1$
				b.append(CR);
				b.append(">>"); //$NON-NLS-1$
				b.append(CR);
				b.append("stream"); //$NON-NLS-1$
				b.append(CR);
				b.append(imageData);
				b.append(CR);
				b.append("endstream"); //$NON-NLS-1$
				b.append(CR);
				b.append("endobj"); //$NON-NLS-1$
				b.append(CR);
				differedOutput.put(objectId, b.toString());
				++objectId;
			}
			writeln(stream, " >>"); //$NON-NLS-1$
		}

		// Add resources for transparency levels
		Map<Double,String> transparencies = graphicContext.getTransparencyResources();
		if (!transparencies.isEmpty()) {
			writeln(stream, " /ExtGState <<"); //$NON-NLS-1$
			for (Entry<Double,String> entry : transparencies.entrySet()) {
				double alpha = entry.getKey();
				String resourceId = entry.getValue();
				writeln(stream,
						"  /", //$NON-NLS-1$
						resourceId, " << /Type /ExtGState", //$NON-NLS-1$
						" /ca ", //$NON-NLS-1$
						alpha,
						" /CA ", //$NON-NLS-1$
						alpha, " >>"); //$NON-NLS-1$
			}
			writeln(stream, " >>"); //$NON-NLS-1$
		}

		writeln(stream, ">>"); //$NON-NLS-1$
		writeln(stream, "endobj"); //$NON-NLS-1$
		
		// Write the differed objects to output
		for(Entry<Integer,String> entry : differedOutput.entrySet()) {
			objectMap.put(entry.getKey(), size(stream));
			write(stream, entry.getValue());
		}

		// Write the XREFs
		int nObjects = objectMap.size();		
		int xrefPos = size(stream);
		writeln(stream, "xref"); //$NON-NLS-1$

		// lines of xref entries must be exactly 20 bytes long
		// (including line break) and thus end with <SPACE NEWLINE>

		writeln(stream, "0 ", nObjects+1); //$NON-NLS-1$

		writeln(stream, String.format("%010d %05d f ", 0, 0)); //$NON-NLS-1$

		for(int i=1; i<=nObjects; ++i) {
			Integer pos = objectMap.get(i);
			if (pos==null) throw new IOException("Position for PDF object not found: "+i); //$NON-NLS-1$
			writeln(stream, String.format("%010d %05d n ", pos.intValue(), 0)); //$NON-NLS-1$
		}

		writeln(stream, "trailer"); //$NON-NLS-1$
		writeln(stream, "<<"); //$NON-NLS-1$
		writeln(stream, "/Size ", nObjects); //$NON-NLS-1$
		writeln(stream, "/Root ", idCatalogue, " 0 R"); //$NON-NLS-1$ //$NON-NLS-2$
		/*writeln(stream, "/ID [ <", fileIdentifier, ">"); //$NON-NLS-1$ $NON-NLS-2$
		writeln(stream, "      <", fileIdentifier, ">"); //$NON-NLS-1$ $NON-NLS-2$
		writeln(stream, "      ]"); //$NON-NLS-1$*/
		writeln(stream, ">>"); //$NON-NLS-1$
		writeln(stream, "startxref"); //$NON-NLS-1$
		writeln(stream, xrefPos);

		writeln(stream, "%%EOF"); //$NON-NLS-1$
	}

}
