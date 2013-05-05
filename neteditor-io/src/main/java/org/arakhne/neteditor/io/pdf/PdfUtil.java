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

package org.arakhne.neteditor.io.pdf ;

import java.io.IOException;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.vector.Image;


/** Utilities for the PDF exporter.
 * <p>
 * This graphic context is exporting a PDF 1.4 according to the
 * given <a href="http://partners.adobe.com/public/developer/en/pdf/PDFReference.pdf">PDF 1.4 Reference Document</a>.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 15.0
 */
class PdfUtil {
	
	/** Shifting distance for the shadows in PDF documents.
	 */
	public static final float SHADOW_DISTANCE = 1f;

	/** Convert the given coordinate along the x axis
	 * from the standard vectorial coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param x
	 * @return the x in the PDF user space.
	 */
	public static float toPdfX(float x) {
		return x;
	}

	/** Convert the given coordinate along the y axis
	 * from the standard vectorial coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param y
	 * @return the y in the PDF user space.
	 */
	public static float toPdfY(float y) {
		return -y;
	}

	/** Convert the given rectangle from the standard vectorial coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param bounds are the bounds to translate.
	 * @return the bounds in the PDF user space.
	 */
	public static Rectangle2f toPdf(Rectangle2f bounds) {
		return new Rectangle2f(
				bounds.getMinX(), -bounds.getMaxY(),
				bounds.getWidth(), bounds.getHeight());
	}

	/** Convert the given point from the standard vectorial coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param point is the point to translate.
	 * @return the point in the PDF user space.
	 */
	public static Point2D toPdf(Point2D point) {
		return new Point2f(point.getX(), -point.getY());
	}

	/** Convert the given point from the standard vectorial coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param x is the x coordinate of the point to translate.
	 * @param y is the y coordinate of the point to translate.
	 * @return the point in the PDF user space.
	 */
	public static Point2D toPdf(double x, double y) {
		return new Point2f((float)x, (float)-y);
	}

	/** Replies the transformation matrix that permits to
	 * convert any operation in the standard coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @return the transformation matrix without the brackets.
	 */
	public static String getPdfTransformationMatrix() {
		return "1 0 0 -1 0 0"; //$NON-NLS-1$
	}

	/** Convert the given image into a PDF sequence of bytes.
	 * 
	 * @param image
	 * @return the sequence of bytes to put in the PDF document.
	 * @throws IOException
	 */
	public static String toPdf(Image image) throws IOException {
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		int bands = image.getNumBands();
		StringBuffer str = new StringBuffer(width*height*bands*2);
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				int pixel = image.getRGB(x, y) & 0xffffff;
				if (bands >= 3) {
					String hex = String.format("%06x", pixel); //$NON-NLS-1$
					str.append(hex);
				}
				else if (bands == 2) {
					str.append(String.format("%04x", pixel)); //$NON-NLS-1$
				}
				else if (bands == 1) {
					str.append(String.format("%02x", pixel)); //$NON-NLS-1$
				}
			}
		}
		return str.toString();
	}

	/** Convert a string into a PDF text by escaping special charaters.
	 *
	 * @param text is the text to convert.
	 * @return the PDF text.
	 */
	public static String toPdf(String text) {
		// Escape string
		return text.replaceAll("\\\\", "\\\\\\\\") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\t", "\\\\t") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\b", "\\\\b") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\f", "\\\\f") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\\(", "\\\\(") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\\)", "\\\\)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/** Convert the given matrix from the standard vectorial coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param matrix
	 * @return the matrix in the PDF user space.
	 */
	public static String toPdf(Transform2D matrix) {
		return "[ " //$NON-NLS-1$
				+toPdfParameters(matrix)
				+" ]"; //$NON-NLS-1$
	}

	/** Convert the given matrix from the standard vectorial coordinate system
	 * to the PDF user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The PDF user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param matrix
	 * @return the matrix in the PDF user space.
	 */
	public static String toPdfParameters(Transform2D matrix) {
		return +matrix.getScaleX()
				+" " //$NON-NLS-1$
				+-matrix.getShearX()
				+" " //$NON-NLS-1$
				+matrix.getShearY()
				+" " //$NON-NLS-1$
				+-matrix.getScaleY()
				+" " //$NON-NLS-1$
				+matrix.getTranslationX()
				+" " //$NON-NLS-1$
				+-matrix.getTranslationY();
	}

}
