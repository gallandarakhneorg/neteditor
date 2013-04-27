/* 
 * $Id$
 * 
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

package org.arakhne.neteditor.io.pdf ;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.FontMetrics;
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
	protected PdfOutputStream createStream(OutputStream stream) {
		return new PdfOutputStream(stream);
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
			stream.write("\n"); //$NON-NLS-1$
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
		
		writeln(stream, "%PDF-1.4"); //$NON-NLS-1$
		writeln(stream, "%% Creator: Arakhne.org NetEditor ", getClass().getName()); //$NON-NLS-1$
		writeln(stream, "%% CreationDate: ", new Date()); //$NON-NLS-1$
		// Object 1: Catalogue
		objectMap.put(1, stream.size());
		writePdfObject(1,  stream,
			"Type", "/Catalog", //$NON-NLS-1$ //$NON-NLS-2$
			"Outlines", "2 0 R", //$NON-NLS-1$ //$NON-NLS-2$
			"Pages", "3 0 R" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 2: Outlines
		objectMap.put(2, stream.size());
		writePdfObject( 2, stream,
			"Type", "Outlines", //$NON-NLS-1$ //$NON-NLS-2$
			"Count", "0" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 3: Pages
		objectMap.put(3, stream.size());
		writePdfObject( 3, stream,
			"Type", "/Pages", //$NON-NLS-1$ //$NON-NLS-2$
			"Kids", "[4 0 R]", //$NON-NLS-1$ //$NON-NLS-2$
			"Count", "1" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 4: the single page
		objectMap.put(4, stream.size());
		writePdfObject( 4, stream,
			"Type", "/Page", //$NON-NLS-1$ //$NON-NLS-2$
			"Parent", "3 0 R", //$NON-NLS-1$ //$NON-NLS-2$
			"MediaBox", String.format("[%d %d %d %d]", x1, y1, x2, y2), //$NON-NLS-1$ //$NON-NLS-2$
			"Contents", "5 0 R", //$NON-NLS-1$ //$NON-NLS-2$
			"Resources", "6 0 R" //$NON-NLS-1$ //$NON-NLS-2$
		);
		// Object 5: the page content itself
		objectMap.put(5, stream.size());
		writeln(stream, "5 0 obj"); //$NON-NLS-1$
		writePdfDictionary(stream, "Length", generateBytes.length); //$NON-NLS-1$
		writeln(stream, "stream"); //$NON-NLS-1$
		
		stream.write(generateBytes);
		
		// Footer
		stream.write("\n"); //$NON-NLS-1$
		stream.writeln("endstream"); //$NON-NLS-1$
		stream.writeln("endobj"); //$NON-NLS-1$

		// Object 6: the resources
		objectMap.put(6, stream.size());
		stream.writeln("6 0 obj"); //$NON-NLS-1$
		stream.writeln("<<"); //$NON-NLS-1$
		stream.writeln(" /ProcSet [/PDF /Text /ImageB /ImageC /ImageI]"); //$NON-NLS-1$

		int objectId = 7;
		Map<Integer,String> differedOutput = new TreeMap<Integer, String>();
		
		// Add resources for fonts
		Map<Font,String> fonts = graphicContext.getFontResources();
		if (!fonts.isEmpty()) {
			stream.writeln(" /Font <<"); //$NON-NLS-1$
			for (Entry<Font,String> entry : fonts.entrySet()) {
				Font font = entry.getKey();
				String resourceId = entry.getValue();
				String psName = font.getPSName().replace('.', ',');
				FontMetrics fm = graphicContext.getFontMetrics(font);
				Rectangle2f fontBounds = fm.getMaxCharBounds();
				int fontFlags = 0;
				if (font.isItalic()) {
					fontFlags |= 1<<6;
				}
				if (font.isBold()) {
					fontFlags |= 1<<18;
				}
				
				stream.write("  /"); //$NON-NLS-1$
				stream.write(resourceId);
				stream.writeln(" << /Type /Font"); //$NON-NLS-1$
				stream.writeln(" /Subtype /TrueType"); //$NON-NLS-1$
				stream.write(" /BaseFont /"); //$NON-NLS-1$
				stream.writeln(psName);
				stream.write(" /FontDescriptor "); //$NON-NLS-1$
				stream.write(Integer.toString(objectId));
				stream.writeln(" 0 R"); //$NON-NLS-1$
				stream.writeln(" >>"); //$NON-NLS-1$
				
				differedOutput.put(objectId,
						objectId + " 0 obj\n<<\n" //$NON-NLS-1$
						+ "/Type /FontDescriptor\n" //$NON-NLS-1$
						+ "/FontName " //$NON-NLS-1$
						+ psName
						+ "\n/Flags " //$NON-NLS-1$
						+ fontFlags
						+ "\n/FontBBox [0 0 " //$NON-NLS-1$
						+ fontBounds.getWidth()
						+ " " //$NON-NLS-1$
						+ fontBounds.getHeight()
						+ "]\n/ItalicAngle " //$NON-NLS-1$
						+ font.getItalicAngle()
						+ "\n/Ascent " //$NON-NLS-1$
						+ fm.getAscent()
						+ "\n/Descent " //$NON-NLS-1$
						+ -fm.getDescent()
						+ "\n/Leading " //$NON-NLS-1$
						+ fm.getLeading()
						+ "\n/CapHeight " //$NON-NLS-1$
						+ fm.getMaxAscent()
						+ "\n>>\nendobj\n"); //$NON-NLS-1$
				++objectId;
			}
			stream.writeln(" >>"); //$NON-NLS-1$
		}

		// Add resources for images
		Map<String,Image> images = graphicContext.getImageResources();
		if (!images.isEmpty()) {
			stream.writeln(" /XObject <<"); //$NON-NLS-1$

			for (Entry<String,Image> entry : images.entrySet()) {
				// Add image declaration
				String resourceId = entry.getKey();
				stream.write("  /"); //$NON-NLS-1$
				stream.write(resourceId);
				stream.write(" "); //$NON-NLS-1$
				stream.write(Integer.toString(objectId));
				stream.writeln(" 0 R"); //$NON-NLS-1$

				// Add data of images
				Image image = entry.getValue();
				String imageData = PdfUtil.toPdf(image);
				StringBuilder b = new StringBuilder();
				b.append(Integer.toString(objectId));
				b.append(" 0 obj\n"); //$NON-NLS-1$
				b.append("<<\n"); //$NON-NLS-1$
				b.append("  /Type /XObject\n"); //$NON-NLS-1$
				b.append("  /Subtype /Image\n"); //$NON-NLS-1$
				b.append("  /Width "); //$NON-NLS-1$
				b.append(image.getWidth(null));
				b.append("\n  /Height "); //$NON-NLS-1$
				b.append(image.getHeight(null));
				b.append("\n  /ColorSpace /DeviceRGB\n"); //$NON-NLS-1$
				b.append("  /BitsPerComponent 8\n"); //$NON-NLS-1$
				b.append("  /Interpolate true\n"); //$NON-NLS-1$
				b.append("  /Length "); //$NON-NLS-1$
				b.append(imageData.length());
				b.append("\n  /Filter /ASCIIHexDecode\n"); //$NON-NLS-1$
				b.append(">>\n"); //$NON-NLS-1$
				b.append("stream\n"); //$NON-NLS-1$
				b.append(imageData);
				b.append("\nendstream\n"); //$NON-NLS-1$
				b.append("endobj\n"); //$NON-NLS-1$
				differedOutput.put(objectId, b.toString());
				++objectId;
			}
			stream.writeln(" >>"); //$NON-NLS-1$
		}

		// Add resources for transparency levels
		Map<Double,String> transparencies = graphicContext.getTransparencyResources();
		if (!transparencies.isEmpty()) {
			stream.writeln(" /ExtGState <<"); //$NON-NLS-1$
			for (Entry<Double,String> entry : transparencies.entrySet()) {
				double alpha = entry.getKey();
				String resourceId = entry.getValue();
				stream.write("  /"); //$NON-NLS-1$
				stream.write(resourceId);
				stream.write(" << /Type /ExtGState"); //$NON-NLS-1$
				stream.write(" /ca "); //$NON-NLS-1$
				stream.write(Double.toString(alpha));
				stream.write(" /CA "); //$NON-NLS-1$
				stream.write(Double.toString(alpha));
				stream.writeln(" >>"); //$NON-NLS-1$
			}
			stream.writeln(" >>"); //$NON-NLS-1$
		}

		stream.writeln(">>"); //$NON-NLS-1$
		stream.writeln("endobj"); //$NON-NLS-1$
		
		// Write the differed objects to output
		for(Entry<Integer,String> entry : differedOutput.entrySet()) {
			objectMap.put(entry.getKey(), stream.size());
			stream.write(entry.getValue());
		}

		int nObjects = objectMap.size();
		
		stream.write("\n"); //$NON-NLS-1$
		int xrefPos = stream.size();
		stream.writeln("xref"); //$NON-NLS-1$
		stream.write("0 "); //$NON-NLS-1$
		stream.writeln(Integer.toString(nObjects));

		// lines of xref entries must must be exactly 20 bytes long
		// (including line break) and thus end with <SPACE NEWLINE>
		stream.write(String.format("%010d %05d", 0, 65535)); //$NON-NLS-1$
		stream.writeln(" f"); //$NON-NLS-1$
		for(int i=1; i<=nObjects; ++i) {
			Integer pos = objectMap.get(i);
			if (pos==null) throw new IOException("Position for PDF object not found: "+i); //$NON-NLS-1$
			stream.write(String.format("%010d %05d", pos.intValue(), 0)); //$NON-NLS-1$
			stream.writeln(" n"); //$NON-NLS-1$
		}

		stream.writeln("\ntrailer"); //$NON-NLS-1$
		stream.writeln("<<"); //$NON-NLS-1$
		stream.write("/Size "); //$NON-NLS-1$
		stream.writeln(Integer.toString(nObjects));
		stream.writeln("/Root 1 0 R"); //$NON-NLS-1$
		stream.writeln(">>"); //$NON-NLS-1$
		stream.writeln("startxref"); //$NON-NLS-1$
		stream.writeln(Integer.toString(xrefPos));

		stream.writeln("%%EOF"); //$NON-NLS-1$
	}

}
