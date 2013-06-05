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

package org.arakhne.neteditor.io.tex ;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.FontMetrics;


/** Several TeX utilities used by exporters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TexGenerator {

	/** Write the complete TeX file for a PDF/TeX or PS/TeX export.
	 * 
	 * @param pdfFile is the path to the PDF file.
	 * @param output is the TeX file to write.
	 * @param content is the content of the TeX file (excluding the header
	 * and the end of the file).
	 * @param width is the width of the figure.
	 * @param height is the height of the figure.
	 * @throws IOException
	 * @see #buildTeXTString(float, float, String, String)
	 */
	public static void writeTeXT(File pdfFile, PrintWriter output, String content, float width, float height) throws IOException {
		String absPath = pdfFile.getAbsolutePath();
		output.println("%% Creator: NetEditor, www.arakhne.org"); //$NON-NLS-1$
		output.print("%% Generation the: "); //$NON-NLS-1$
		output.println(new Date());
		output.print("%% Accompanies image file '"); //$NON-NLS-1$
		output.print(absPath);
		output.println("' (pdf, eps, ps)"); //$NON-NLS-1$
		output.println("%%"); //$NON-NLS-1$
		output.println("%% To include the image in your LaTeX document, write"); //$NON-NLS-1$
		output.println("%%   \\input{<filename>.pdftex_t}"); //$NON-NLS-1$
		output.println("%%  instead of"); //$NON-NLS-1$
		output.println("%%   \\includegraphics{<filename>.pdf}"); //$NON-NLS-1$
		output.println("%% Some packages (autolatex, upmethodology...)"); //$NON-NLS-1$
		output.println("%% may provides the following macro to include:"); //$NON-NLS-1$
		output.println("%%   \\includegraphicswtex{<filename>.pdftex_t}"); //$NON-NLS-1$
		output.println("\\begingroup%"); //$NON-NLS-1$
		output.println("  \\makeatletter%"); //$NON-NLS-1$
		output.println("  \\providecommand\\color[2][]{%"); //$NON-NLS-1$
		output.println("    \\errmessage{(NetEditor) Color is used for the text in NetEditor, but the package 'color.sty' is not loaded}%"); //$NON-NLS-1$
		output.println("    \\renewcommand\\color[2][]{}%"); //$NON-NLS-1$
		output.println("  }%"); //$NON-NLS-1$
		output.println("  \\providecommand\\transparent[1]{%"); //$NON-NLS-1$
		output.println("    \\errmessage{(NetEditor) Transparency is used (non-zero) for the text in NetEditor, but the package 'transparent.sty' is not loaded}%"); //$NON-NLS-1$
		output.println("    \\renewcommand\\transparent[1]{}%"); //$NON-NLS-1$
		output.println("  }%"); //$NON-NLS-1$
		output.println("  \\providecommand\\rotatebox[2]{#2}%"); //$NON-NLS-1$
		output.println("  \\providecommand\\SetNeteditorFontForFigures[5]{%"); //$NON-NLS-1$
		output.println("    \\reset@font\\fontsize{#1}{#2pt}%"); //$NON-NLS-1$
		output.println("    \\fontfamily{#3}\\fontseries{#4}\\fontshape{#5}%"); //$NON-NLS-1$
		output.println("    \\selectfont%"); //$NON-NLS-1$
		output.println("  }%"); //$NON-NLS-1$
		output.println("  \\makeatother%"); //$NON-NLS-1$
		output.print("  \\begin{picture}("); //$NON-NLS-1$
		output.print(width);
		output.print(","); //$NON-NLS-1$
		output.print(height);
		output.println(")%"); //$NON-NLS-1$
		output.print("    \\put(0,0){\\includegraphics[width="); //$NON-NLS-1$
		output.print(width);
		output.print("\\unitlength]{"); //$NON-NLS-1$
		output.print(pdfFile.getAbsolutePath());
		output.println("}}%"); //$NON-NLS-1$

		output.print(content);

		output.println("  \\end{picture}%"); //$NON-NLS-1$
		output.println("\\endgroup%");  //$NON-NLS-1$
	}

	/** Build a TeX string for a text in a PDF/TeX or a PS/TeX export.
	 * 
	 * @param x is the position of the text.
	 * @param y is the position of the text.
	 * @param text is the text.
	 * @param fontDefinition is the definition of the font to use. May be <code>null</code>.
	 * @return the TeX macros.
	 * @see #writeTeXT(File, PrintWriter, String, float, float)
	 */
	public static String buildTeXTString(float x, float y, String text, String fontDefinition) {
		StringBuilder b = new StringBuilder();
		b.append("  \\put("); //$NON-NLS-1$
		b.append(x);
		b.append(","); //$NON-NLS-1$
		b.append(y);
		b.append("){\\makebox(0,0)[lb]{\\smash{"); //$NON-NLS-1$
		if (fontDefinition!=null && !fontDefinition.isEmpty()) {
			b.append(fontDefinition);
		}
		b.append(text);
		b.append("}}}%"); //$NON-NLS-1$
		return b.toString();
	}
	
	private static String detectFamily(Font font) {
		String family = font.getFamily();
		if ("serif".equalsIgnoreCase(family)) { //$NON-NLS-1$
			return "\\sfdefault"; //$NON-NLS-1$
		}
		if ("dialoginput".equalsIgnoreCase(family) //$NON-NLS-1$
			|| "monospaced".equalsIgnoreCase(family)) { //$NON-NLS-1$
			return "\\ttdefault"; //$NON-NLS-1$
		}
		return "\\rmdefault"; //$NON-NLS-1$
	}
	
	private static String detectSeries(Font font) {
		if (font.isBold()) return "\\bfdefault"; //$NON-NLS-1$
		return "\\mddefault"; //$NON-NLS-1$
	}

	private static String detectShape(Font font) {
		if (font.isItalic()) return "\\itshape"; //$NON-NLS-1$
		return "\\updefault"; //$NON-NLS-1$
	}

	/** Build a TeX string for a font selection in a PDF/TeX or a PS/TeX export.
	 * 
	 * @param font is the font to use.
	 * @param fontMetrics is the metrics of the <var>font</var>.
	 * @return the TeX macros.
	 */
	public static String buildFontString(Font font, FontMetrics fontMetrics) {
		float baselineskip = fontMetrics.getHeight();
		int size = (int)font.getSize();
		String family = detectFamily(font);
		String series = detectSeries(font);
		String shape = detectShape(font);
		return "\\SetNeteditorFontForFigures{" //$NON-NLS-1$
				+Integer.toString(size)
				+"}{" //$NON-NLS-1$
				+Float.toString(baselineskip)
				+"}{" //$NON-NLS-1$
				+family
				+"}{" //$NON-NLS-1$
				+series
				+"}{" //$NON-NLS-1$
				+shape
				+"}"; //$NON-NLS-1$
	}

	/** Translate the X-coordinate from the NetEditor to TeX.
	 * 
	 * @param x
	 * @param pictureBounds are the bounds of the picture.
	 * @return the x coordinate in TeX.
	 */
	public static float toTeXX(float x, Rectangle2f pictureBounds) {
		return x - pictureBounds.getMinX();
	}

	/** Translate the Y-coordinate from the NetEditor to TeX.
	 * 
	 * @param y
	 * @param pictureBounds are the bounds of the picture.
	 * @return the y coordinate in TeX.
	 */
	public static float toTeXY(float y, Rectangle2f pictureBounds) {
		return pictureBounds.getHeight() - (y - pictureBounds.getMinY());
	}

	/** Translate the rectangle from the NetEditor to TeX.
	 * 
	 * @param r
	 * @param pictureBounds are the bounds of the picture.
	 * @return the rectangle in TeX.
	 */
	public static Rectangle2f toTeX(Rectangle2f r, Rectangle2f pictureBounds) {
		return new Rectangle2f(
				r.getMinX() - pictureBounds.getMinX(),
				r.getMinY() - pictureBounds.getMinY(),
				r.getWidth(),
				r.getHeight());
	}

}
