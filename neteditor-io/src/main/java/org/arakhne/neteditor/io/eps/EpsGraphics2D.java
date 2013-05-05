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
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.Deque;
import java.util.LinkedList;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.PathElement2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.continous.object2d.Segment2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.ImageObserver;
import org.arakhne.afc.ui.vector.Stroke;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.io.AbstractVectorialExporterGraphics2D;
import org.arakhne.neteditor.io.VectorialExporterException;

/** This graphic context permits to create an Encapsulated Postscript file
 *  from a graphic context.
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
public class EpsGraphics2D extends AbstractVectorialExporterGraphics2D {

	private final Rectangle2f documentBounds;
	private final StringBuilder globalBuffer = new StringBuilder();

	private final Deque<EpsContext> context = new LinkedList<EpsContext>();
	
	/**
	 * @param documentBounds
	 */
	public EpsGraphics2D(Rectangle2f documentBounds) {
		this.documentBounds = documentBounds;
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}
	
	@Override
	public void reset() {
		super.reset();
		this.context.clear();
		this.globalBuffer.setLength(0);
	}
	
	private void gwrite(Transform2D trans) {
		float tx = EpsUtil.toEpsX(trans.getTranslationX());
		float ty = EpsUtil.toEpsY(trans.getTranslationY());
		if (tx!=0f || ty!=0f) {
			gwrite(Float.toString(tx));
			gwrite(" "); //$NON-NLS-1$
			gwrite(Float.toString(ty));
			gwriteln(" translate"); //$NON-NLS-1$
		}

		float sx = EpsUtil.toEpsX(trans.getScaleX());
		float sy = EpsUtil.toEpsY(trans.getScaleY());
		if (sx!=1f || sy!=1f) {
			gwrite(Float.toString(sx));
			gwrite(" "); //$NON-NLS-1$
			gwrite(Float.toString(sy));
			gwriteln(" scale"); //$NON-NLS-1$
		}

		float angle = EpsUtil.toEpsAngle(trans.getRotation());
		if (angle!=0f) {
			gwrite(Float.toString(angle));
			gwriteln(" rotate"); //$NON-NLS-1$
		}
	}
	
	@Override
	public void pushRenderingContext(Figure component, Shape2f viewShape,
			Rectangle2f bounds, Color fillColor, Color lineColor,
			Transform2D localTransformation) {
		super.pushRenderingContext(component, viewShape, bounds, fillColor, lineColor,
				localTransformation);
		gsave();
		if (!this.currentTransform.isIdentity()) {
			gwrite(this.currentTransform);
		}
	}
	
	@Override
	public void popRenderingContext() {
		grestore();
		super.popRenderingContext();
	}
	
	@Override
	public float getShadowTranslationX() {
		return ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_X;
	}

	@Override
	public float getShadowTranslationY() {
		return ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_Y;
	}

	private StringBuilder gwrite(String o) {
		this.globalBuffer.append(o);
		return this.globalBuffer;
	}

	private void gsave() {
		gwriteln("gsave"); //$NON-NLS-1$
		this.context.push(new EpsContext(this.context.peek()));
	}

	private void grestore() {
		this.context.pop();
		gwriteln("grestore"); //$NON-NLS-1$
	}

	private StringBuilder gwriteln(String o) {
		StringBuilder b = gwrite(o);
		if (b!=null) {
			b.append("\n"); //$NON-NLS-1$
		}
		return b;
	}
	
	@Override
	public void beginGroup() {
		gsave();
	}

	@Override
	public void endGroup() {
		grestore();
	}

	@Override
	public boolean drawImage(URL imageURL, Image img, float dx1, float dy1,
			float dx2, float dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		if (img==null) {
			return false;
		}
		
		preDrawing();

		Rectangle2f imageBounds = new Rectangle2f();
		imageBounds.setFromCorners(dx1, dy1, dx2, dy2);
		imageBounds = EpsUtil.toEps(imageBounds);

		int iw = Math.abs(sx2-sx1);
		int ih = Math.abs(sy2-sy1);
		float sx = Math.abs(dx2-dx1);
		float sy = Math.abs(dy2-dy1);
		
		// Get the pixels of the image
		String pixels;
		try {
			pixels = EpsUtil.toEps(img, sx1, sy1, sx2, sy2);
		}
		catch (IOException e) {
			throw new VectorialExporterException(e);
		}

		gsave();
		
		// Translate to put the lower corner at the right position.
		gwrite(Double.toString(imageBounds.getMinX()));
		gwrite(" "); //$NON-NLS-1$
		gwrite(Double.toString(imageBounds.getMinY()));
		gwriteln(" translate") ; //$NON-NLS-1$
		
		// Scale the image to fit the image's size
		gwrite(Float.toString(EpsUtil.toEpsX(sx)));
		gwrite(" "); //$NON-NLS-1$
		gwrite(Float.toString(EpsUtil.toEpsYInverted(sy)));
		gwriteln(" scale"); //$NON-NLS-1$
		
		// Select the RGB color space
		gwriteln("/DeviceRGB setcolorspace"); //$NON-NLS-1$
		
		// start an image dictionary of type 1 
		gwriteln("<<"); //$NON-NLS-1$
		gwriteln("  /ImageType 1"); //$NON-NLS-1$
		
		// Width of the bitmap
		gwrite("  /Width "); //$NON-NLS-1$
		gwriteln(Integer.toString(iw));
		
		// Height of the bitmap
		gwrite("  /Height "); //$NON-NLS-1$
		gwriteln(Integer.toString(ih));
		
		// Bits per color component
		gwriteln("  /BitsPerComponent 8"); //$NON-NLS-1$

		// Use the standard color decoding
		gwriteln("  /Decode [ 0 1 0 1 0 1 ]"); //$NON-NLS-1$

		// Set the matrix for the unit square of the bitmap 
		gwrite("  /ImageMatrix [ "); //$NON-NLS-1$
		gwrite(Integer.toString(iw));
		gwrite(" 0 0 "); //$NON-NLS-1$
		gwrite(Integer.toString(-ih));
		gwrite(" 0 "); //$NON-NLS-1$
		gwrite(Integer.toString(ih));
		gwriteln(" ]"); //$NON-NLS-1$

		// Read the image pixels from the current file
		gwrite("  /DataSource currentfile"); //$NON-NLS-1$
		// Use hexadecimal decoding algorithm
		gwriteln("  /ASCIIHexDecode filter"); //$NON-NLS-1$
		gwriteln(">>"); //$NON-NLS-1$
		gwriteln("image"); //$NON-NLS-1$
		
		// The bitmap data
		gwriteln(pixels);
		
		grestore();

		return true;
	}

	@Override
	public void drawString(String str, float x, float y) {
		drawString(str, x, y, null);
	}

	@Override
	public void drawString(String str, float x, float y, Shape2f clip) {
		preDrawing();
		
		EpsContext context = this.context.peek();

		gsave();

		if (clip!=null) {
			clip(clip);
		}

		Font font = getFont();
		if ((font==null && context.font!=null)
			||(font!=null && !font.equals(context.font))) {
			context.font = font;
			
			if (font!=null) {
				String psName = font.getPSName().replace('.', ',');
				float fontSize = getFont().getSize();
		
				gwrite("/"); //$NON-NLS-1$
				gwrite(psName);
				gwriteln(" findfont "); //$NON-NLS-1$
				gwrite(Float.toString(fontSize));
				gwriteln(" scalefont"); //$NON-NLS-1$
				gwriteln("setfont"); //$NON-NLS-1$
			}
		}

		setDrawingAttributes(true, true, false);

		// Move the text at the right position
		gwrite(Float.toString(EpsUtil.toEpsX(x)));
		gwrite(" "); //$NON-NLS-1$
		gwrite(Float.toString(EpsUtil.toEpsY(y)));
		gwriteln(" moveto"); //$NON-NLS-1$

		String text = EpsUtil.toEps(str.replaceAll("[\r\n]+", "")); //$NON-NLS-1$ //$NON-NLS-2$
		gwrite("("); //$NON-NLS-1$
		gwrite(text);
		gwriteln(") show"); //$NON-NLS-1$

		grestore();
		
		postDrawing();
	}

	@Override
	protected void drawPath(PathIterator2f pathIterator) {
		preDrawing();

		gwriteln("newpath"); //$NON-NLS-1$
		gwriteln(computeEpsPath(pathIterator));

		if (isInteriorPainted()) {
			gsave();
			setDrawingAttributes(false, true, false);
			gwriteln("fill"); //$NON-NLS-1$
			grestore();
		}
		if (isOutlineDrawn()) {
			gsave();
			setDrawingAttributes(true, false, false);
			gwriteln("stroke"); //$NON-NLS-1$
			grestore();
		}

		postDrawing();
	}

	@Override
	protected void drawEllipse(Ellipse2f ellipse) {
		drawPath(ellipse.getPathIterator());
	}

	@Override
	protected void drawRectangle(Rectangle2f rectangle) {
		drawPath(rectangle.getPathIterator());
	}

	@Override
	protected void drawRoundRectangle(RoundRectangle2f rectangle) {
		drawPath(rectangle.getPathIterator());
	}

	@Override
	protected void drawLine(Segment2f line) {
		drawPath(line.getPathIterator());
	}

	@Override
	protected void drawCircle(Circle2f circle) {
		drawPath(circle.getPathIterator());
	}

	@Override
	public void setClip(Shape2f clip) {
		super.setClip(clip);
		gclip(clip);
	}

	private void gclip(Shape2f clip) {
		EpsContext context = this.context.peek();
		if ((clip==null && context.clip!=null)
				||(clip!=null && !clip.equals(context.clip))) {
			context.clip = clip;
			gwriteln("initclip"); //$NON-NLS-1$
			if (clip!=null) {
				gwriteln("newpath"); //$NON-NLS-1$
				gwrite(computeEpsPath(clip.getPathIterator()));
				gwriteln(" clip"); //$NON-NLS-1$
			}
		}
	}

	@Override
	public void clip(Shape2f clip) {
		super.clip(clip);
		gclip(getClip());
	}

	@Override
	public void prolog() throws IOException {
		this.globalBuffer.setLength(0);
		this.context.push(new EpsContext());

		// Generates the Poscript header
		gwriteln("%!PS-Adobe-3.0 EPSF-3.0"); //$NON-NLS-1$
		gwrite("%%Creator: Arakhne.org NetEditor "); //$NON-NLS-1$
		gwriteln(getClass().getName());
		gwrite( "%%CreationDate: ");//$NON-NLS-1$
		gwriteln(new Date().toString());

		gwriteln("%%Pages: 1"); //$NON-NLS-1$

		Rectangle2f epsBounds = EpsUtil.toEps(this.documentBounds);

		gwrite("%%BoundingBox: "); //$NON-NLS-1$
		gwrite(Double.toString(epsBounds.getMinX()));
		gwrite(" "); //$NON-NLS-1$
		gwrite(Double.toString(epsBounds.getMinY()));
		gwrite(" "); //$NON-NLS-1$
		gwrite(Double.toString(epsBounds.getMaxX()));
		gwrite(" "); //$NON-NLS-1$
		gwriteln(Double.toString(epsBounds.getMaxY()));

		gwriteln("%%LanguageLevel: 2"); //$NON-NLS-1$

		gwriteln("%%BeginProlog"); //$NON-NLS-1$
		gwriteln("%%EndProlog"); //$NON-NLS-1$


		gwriteln("%%BeginSetup"); //$NON-NLS-1$
		gwriteln("%%EndSetup"); //$NON-NLS-1$

		gwriteln("/Helvetica findfont 11 scalefont setfont"); //$NON-NLS-1$
	}

	@Override
	public void epilog() throws IOException {
		gwriteln("showpage") ; //$NON-NLS-1$
		gwriteln("%%Trailer") ; //$NON-NLS-1$
		gwriteln("%%EOF") ; //$NON-NLS-1$
	}

	/** Replies the generated EPS instructions.
	 * 
	 * @return the generated EPS instructions.
	 */
	public String getGeneratedString() {
		return this.globalBuffer.toString();
	}

	private void setDrawingAttributes(boolean enableOutline, boolean enableFilling,
			boolean invertFillingOutlineColors) {
		EpsContext context = this.context.peek();

		if (isInteriorPainted() && enableFilling) {
			Color color = invertFillingOutlineColors ? getOutlineColor() : getFillColor();
			if (color==null) {
				color = invertFillingOutlineColors ?
						ViewComponentConstants.DEFAULT_LINE_COLOR :
							ViewComponentConstants.DEFAULT_FILL_COLOR;
			}

			if (!color.equals(context.color)) {
				context.color = color;

				double r = color.getRed() / 255.;
				double g = color.getGreen() / 255.;
				double b = color.getBlue() / 255.;

				gwrite(Double.toString(r));
				gwrite(" "); //$NON-NLS-1$
				gwrite(Double.toString(g));
				gwrite(" "); //$NON-NLS-1$
				gwrite(Double.toString(b));
				gwriteln(" setrgbcolor"); //$NON-NLS-1$
			}
		}

		if (isOutlineDrawn() && enableOutline) {
			Color color = invertFillingOutlineColors ? getFillColor() : getOutlineColor();
			if (color==null) {
				color = invertFillingOutlineColors ?
						ViewComponentConstants.DEFAULT_FILL_COLOR :
							ViewComponentConstants.DEFAULT_LINE_COLOR;
			}

			if (!color.equals(context.color)) {
				context.color = color;

				double r = color.getRed() / 255.;
				double g = color.getGreen() / 255.;
				double b = color.getBlue() / 255.;

				gwrite(Double.toString(r));
				gwrite(" "); //$NON-NLS-1$
				gwrite(Double.toString(g));
				gwrite(" "); //$NON-NLS-1$
				gwrite(Double.toString(b));
				gwriteln(" setrgbcolor"); //$NON-NLS-1$
			}

			Stroke stroke = getStroke();
			if (context.lineWidth!=stroke.getLineWidth()) {
				context.lineWidth = stroke.getLineWidth();
				gwrite(Float.toString(stroke.getLineWidth()));
				gwriteln(" setlinewidth"); //$NON-NLS-1$
			}

			float[] dashes = stroke.getDashArray();
			if (context.dashOffset!=stroke.getDashPhase()
					|| !Arrays.equals(dashes, context.dashes)) {
				context.dashes = dashes;
				context.dashOffset = stroke.getDashPhase();
				if (dashes==null) {
					gwrite("[ ] 0 "); //$NON-NLS-1$
				}
				else {
					gwrite("["); //$NON-NLS-1$
					for(float d : context.dashes) {
						gwrite(" "); //$NON-NLS-1$
						gwrite(Float.toString(d));
					}
					gwrite(" ] "); //$NON-NLS-1$
					gwrite(Float.toString(context.dashOffset));
				}
				gwriteln(" setdash"); //$NON-NLS-1$
			}

			EpsEndCaps endCap = EpsEndCaps.fromGenericType(stroke.getEndCap());
			if (context.endCap!=endCap) {
				context.endCap = endCap;
				gwrite(Integer.toString(endCap.eps()));
				gwriteln(" setlinecap"); //$NON-NLS-1$
			}

			EpsLineJoin lineJoin = EpsLineJoin.fromGenericType(stroke.getLineJoin());
			if (context.lineJoin!=lineJoin) {
				context.lineJoin = lineJoin;
				gwrite(Integer.toString(lineJoin.eps()));
				gwriteln(" setlinejoin"); //$NON-NLS-1$
			}

			if (context.miterLimit!=stroke.getMiterLimit()) {
				context.miterLimit = stroke.getMiterLimit();
				gwrite(Float.toString(stroke.getMiterLimit()));
				gwriteln(" setmiterlimit"); //$NON-NLS-1$
			}
		}
	}

	private static String computeEpsPath(PathIterator2f pathIterator) {
		StringBuilder path = new StringBuilder();

		PathElement2f element;

		while (pathIterator.hasNext()) {
			element = pathIterator.next();
			switch(element.type) {
			case MOVE_TO:
				path.append(EpsUtil.toEpsX(element.toX));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(element.toY));
				path.append(" moveto\n"); //$NON-NLS-1$
				break;
			case LINE_TO:
				path.append(EpsUtil.toEpsX(element.toX));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(element.toY));
				path.append(" lineto\n"); //$NON-NLS-1$
				break;
			case CURVE_TO:
				path.append(EpsUtil.toEpsX(element.ctrlX1));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(element.ctrlY1));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsX(element.ctrlX2));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(element.ctrlY2));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsX(element.toX));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(element.toY));
				path.append(" curveto\n"); //$NON-NLS-1$
				break;
			case QUAD_TO:
				float qx1 = element.fromX + 2f/3f*(element.ctrlX1 - element.fromX);
				float qy1 = element.fromY + 2f/3f*(element.ctrlY1 - element.fromY);
				float qx2 = element.ctrlX1 + 1f/3f*(element.toX - element.ctrlX1); 
				float qy2 = element.ctrlY1 + 1f/3f*(element.toY - element.ctrlY1); 
				float qx3 = element.toX; 
				float qy3 = element.toY; 
				path.append(EpsUtil.toEpsX(qx1));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(qy1));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsX(qx2));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(qy2));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsX(qx3));
				path.append(" "); //$NON-NLS-1$
				path.append(EpsUtil.toEpsY(qy3));
				path.append(" curveto\n"); //$NON-NLS-1$
				break;
			case CLOSE:
				path.append("closepath\n"); //$NON-NLS-1$
				break;
			default:
				throw new IllegalStateException();
			}
		}
		return path.toString();
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 15.0
	 */
	private static class EpsContext {

		public Shape2f clip = null;
		public Color color = null;
		public float lineWidth = Float.NaN;
		public float miterLimit = Float.NaN;
		public EpsEndCaps endCap = null;
		public EpsLineJoin lineJoin = null;
		public float dashOffset = Float.NaN;
		public float[] dashes = null;
		public Font font = null;

		public EpsContext() {
			//
		}

		public EpsContext(EpsContext parent) {
			if (parent!=null) {
				this.clip = parent.clip;
				this.color = parent.color;
				this.lineWidth = parent.lineWidth;
				this.miterLimit = parent.miterLimit;
				this.endCap = parent.endCap;
				this.lineJoin = parent.lineJoin;
				this.dashOffset = parent.dashOffset;
				this.dashes = parent.dashes;
				this.font = parent.font;
			}
		}

	}

}