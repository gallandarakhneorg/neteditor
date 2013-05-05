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

package org.arakhne.neteditor.io.eps ;

import java.io.IOException;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.Raster;


/** Utilities for the EPS exporter.
 * <p>
 * This graphic context supports the
 * <a href="http://www.adobe.com/products/postscript/pdfs/PLRM.pdf">Postscript Reference Document Third Edition</a>, and the
 * <a href="http://partners.adobe.com/public/developer/en/ps/5002.EPSF_Spec.pdf">EPS Reference Document 3.0</a>.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
class EpsUtil {

	/** Convert the given coordinate along the x axis
	 * from the standard vectorial coordinate system
	 * to the EPS user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The EPS user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param x
	 * @return the x in the EPS user space.
	 */
	public static float toEpsX(float x) {
		return x;
	}

	/** Convert the given coordinate along the y axis
	 * from the standard vectorial coordinate system
	 * to the EPS user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The EPS user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param y
	 * @return the y in the EPS user space.
	 */
	public static float toEpsY(float y) {
		return -y;
	}

	/** Convert the given coordinate along the y axis
	 * from the standard vectorial coordinate system
	 * to the EPS user space <code>-y</code>.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The EPS user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param y
	 * @return the y in the EPS user space.
	 */
	public static float toEpsYInverted(float y) {
		return y;
	}

	/** Convert the given rectangle from the standard vectorial coordinate system
	 * to the EPS user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The EPS user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param bounds are the bounds to translate.
	 * @return the bounds in the EPS user space.
	 */
	public static Rectangle2f toEps(Rectangle2f bounds) {
		return new Rectangle2f(
				bounds.getMinX(), -bounds.getMaxY(),
				bounds.getWidth(), bounds.getHeight());
	}

	/** Convert the given point from the standard vectorial coordinate system
	 * to the EPS user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The EPS user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param point is the point to translate.
	 * @return the point in the EPS user space.
	 */
	public static Point2D toEps(Point2D point) {
		return new Point2f(point.getX(), -point.getY());
	}

	/** Convert the given point from the standard vectorial coordinate system
	 * to the EPS user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The EPS user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param x is the x coordinate of the point to translate.
	 * @param y is the y coordinate of the point to translate.
	 * @return the point in the EPS user space.
	 */
	public static Point2D toEps(float x, float y) {
		return new Point2f(x, -y);
	}

	/** Convert a string into a EPS text by escaping special charaters.
	 *
	 * @param text is the text to convert.
	 * @return the EPS text.
	 */
	public static String toEps(String text) {
		// Escape string
		return text.replaceAll("\\\\", "\\\\\\\\") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\t", "\\\\t") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\b", "\\\\b") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\f", "\\\\f") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\\(", "\\\\(") //$NON-NLS-1$ //$NON-NLS-2$
				.replaceAll("\\)", "\\\\)"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	/** Convert the given image into a PDF sequence of bytes.
	 * 
	 * @param image
	 * @param sx1 is the x coordinate of the first corner of the subimage to extract.
	 * @param sy1 is the y coordinate of the first corner of the subimage to extract.
	 * @param sx2 is the x coordinate of the second corner of the subimage to extract.
	 * @param sy2 is the y coordinate of the second corner of the subimage to extract.
	 * @return the sequence of bytes to put in the EPS document.
	 * @throws IOException
	 */
	public static String toEps(Image image, int sx1, int sy1, int sx2, int sy2) throws IOException {
		int ix = Math.min(sx1,  sx2);
		int iy = Math.min(sy1,  sy2);
		int iw = Math.abs(sx2 - sx1);
		int ih = Math.abs(sy2 - sy1);
		
		Raster raster = image.getData(new Rectangle2f(ix, iy, iw, ih));
		
		int pixel;
		int bands = raster.getNumBands();
		StringBuilder str = new StringBuilder();
		int[] samples = new int[bands];
		
		for (int y=0; y<ih; ++y) {
			for (int x=0; x<iw; ++x) {
				raster.getPixel(x, y, samples);
				if (bands >= 3) {
					pixel = ((samples[0] & 0xFF) << 16) |
			                ((samples[1] & 0xFF) << 8)  |
			                ((samples[2] & 0xFF));
					str.append(String.format("%06x", pixel)); //$NON-NLS-1$
				}
				else if (bands == 2) {
					pixel = ((samples[0] & 0xFF) << 8)  |
			                ((samples[1] & 0xFF));
					str.append(String.format("%04x", pixel)); //$NON-NLS-1$
				}
				else if (bands == 1) {
					pixel = (samples[0] & 0xFF);
					str.append(String.format("%02x", pixel)); //$NON-NLS-1$
				}
			}
		}
		return str.toString();
	}

	/** Convert the given angle from the standard vectorial coordinate system
	 * to the EPS user space.
	 * <p>
	 * The standard coordinate system has the x axis positive to the right and
	 * the y axis positive downward.
	 * The EPS user space has the x axis positive to the right and
	 * the y axis positive upward.
	 * 
	 * @param a is the angle to translate.
	 * @return the angle in the EPS user space.
	 */
	public static float toEpsAngle(float a) {
		return -a;
	}

}
