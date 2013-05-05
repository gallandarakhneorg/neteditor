/* 
 * $Id$
 * 
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
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.PathElement2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.continous.object2d.Segment2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.StringAnchor;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.FontComparator;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.ImageObserver;
import org.arakhne.afc.ui.vector.Stroke;
import org.arakhne.afc.ui.vector.VectorGraphics2D;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.io.AbstractVectorialExporterGraphics2D;

/** This graphic context permits to create a PDF file
 *  from a graphic context.
 * <p>
 * This exporter supports the 
 * <a href="http://partners.adobe.com/public/developer/en/pdf/PDFReference.pdf">PDF 1.4 Reference Document</a>.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 15.0
 */
public class PdfGraphics2D extends AbstractVectorialExporterGraphics2D {

	/** Prefix string for PDF font resource ids. */
	protected static final String FONT_RESOURCE_PREFIX = "F"; //$NON-NLS-1$

	/** Prefix string for PDF image resource ids. */
	protected static final String IMAGE_RESOURCE_PREFIX = "Im"; //$NON-NLS-1$

	/** Prefix string for PDF transparency resource ids. */
	protected static final String TRANSPARENCY_RESOURCE_PREFIX = "T"; //$NON-NLS-1$
	
	private static String makeImageKey(Image image, int sx1, int sy1, int sx2, int sy2) {
		return IMAGE_RESOURCE_PREFIX +
				System.identityHashCode(image.toString()) +
				"_" + //$NON-NLS-1$
				sx1 + "x" + sy1 + //$NON-NLS-1$
				"_" + //$NON-NLS-1$
				sx2 + "x" + sy2; //$NON-NLS-1$
	}

	private final StringBuilder buffer = new StringBuilder();
	private final Map<Double,String> transparencyResources = new TreeMap<Double, String>();
	private final Map<Font,String> fontResources = new TreeMap<Font,String>(new FontComparator());
	private final Map<String,Image> imageResources = new TreeMap<String, Image>();

	private double currentTransparency = -1;
	private float currentLineWidth = Float.NaN;
	private float currentMiterLimit = Float.NaN;
	private float currentDashPhase = Float.NaN;
	private PdfEndCaps currentEndCap = null;
	private PdfLineJoin currentLineJoin = null;
	private float[] currentDashes = null;

	/** Construct a new PdfGraphics2D.
	 */
	public PdfGraphics2D() {
		//
	}
	
	@Override
	public float getShadowTranslationX() {
		return PdfUtil.SHADOW_DISTANCE;
	}
	
	@Override
	public float getShadowTranslationY() {
		return PdfUtil.SHADOW_DISTANCE;
	}

	@Override
	public void dispose() {
		super.dispose();
		this.buffer.setLength(0);
		this.transparencyResources.clear();
		this.fontResources.clear();
		this.imageResources.clear();

		this.currentTransparency = -1;
		this.currentLineWidth = Float.NaN;
		this.currentMiterLimit = Float.NaN;
		this.currentDashPhase = Float.NaN;
		this.currentEndCap = null;
		this.currentLineJoin = null;
		this.currentDashes = null;
	}
		
	@Override
	public void reset() {
		super.reset();
		this.buffer.setLength(0);
		this.transparencyResources.clear();
		this.fontResources.clear();
		this.imageResources.clear();

		this.currentTransparency = -1;
		this.currentLineWidth = Float.NaN;
		this.currentMiterLimit = Float.NaN;
		this.currentDashPhase = Float.NaN;
		this.currentEndCap = null;
		this.currentLineJoin = null;
		this.currentDashes = null;
	}

	@Override
	public void prolog() throws IOException {
		this.buffer.setLength(0);
		this.currentTransparency = -1;
		this.transparencyResources.clear();
		this.fontResources.clear();
		this.imageResources.clear();
		resetDrawingAttributes();

		writeln("q"); //$NON-NLS-1$
	}
	
	@Override
	public void epilog() throws IOException {
		if (getClip() != null) {
			writeln("Q"); //$NON-NLS-1$
		}
		writeln("Q"); //$NON-NLS-1$
		resetDrawingAttributes();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringAnchor getStringAnchor() {
		return StringAnchor.LOWER_LEFT;
	}

	private boolean writeln(Object... text) {
		boolean changed = write(text);
		if (changed) {
			this.buffer.append("\n"); //$NON-NLS-1$
		}
		return changed;	
	}

	private boolean write(Object... text) {
		boolean changed = false;
		if (text!=null && text.length>0) {
			for(Object t : text) {
				if (t!=null) {
					String str = t.toString();
					if (str!=null && !str.isEmpty()) {
						this.buffer.append(str);
						changed = true;
					}
				}
			}
		}
		return changed;
	}
	
	private void writeCurrentTransform() {
		if (!this.currentTransform.isIdentity()) {
			write( PdfUtil.toPdfParameters(this.currentTransform) );
			writeln(" cm"); //$NON-NLS-1$
		}
	}

	/**
	 * Returns the resource describing the specified transparency context.
	 * 
	 * @param alpha is the transparency value
	 * @return A new PDF object id.
	 */
	protected String getTransparencyResource(double alpha) {
		String name = this.transparencyResources.get(alpha);
		if (name==null) {
			name = TRANSPARENCY_RESOURCE_PREFIX + (this.transparencyResources.size() + 1);
			this.transparencyResources.put(alpha, name);
		}
		return name;
	}

	/** Replies all the fonts used to render into PDF.
	 * 
	 * @return the fonts in the PDF.
	 */
	public Map<Font,String> getFontResources() {
		return Collections.unmodifiableMap(this.fontResources);
	}

	/**
	 * Returns the resource describing the specified font.
	 * 
	 * @param font Font to be described.
	 * @return A new PDF object id.
	 */
	protected String getFontResource(Font font) {
		String name = this.fontResources.get(font);
		if (name==null) {
			name = FONT_RESOURCE_PREFIX + (this.fontResources.size() + 1);
			this.fontResources.put(font, name);
		}
		return name;
	}

	/** Replies all the transparency contexts used to render into PDF.
	 * 
	 * @return the transparency resources in the PDF.
	 */
	public Map<Double,String> getTransparencyResources() {
		return Collections.unmodifiableMap(this.transparencyResources);
	}

	/**
	 * Returns the resource describing the specified font.
	 * 
	 * @param image is the image for which the resource name may be retreive. 
	 * @param sx1 is the x coordinate of the first corner of the sub-image to consider.
	 * @param sy1 is the y coordinate of the first corner of the sub-image to consider.
	 * @param sx2 is the x coordinate of the second corner of the sub-image to consider.
	 * @param sy2 is the y coordinate of the second corner of the sub-image to consider.
	 * @return A new PDF object id.
	 */
	protected String getImageResource(Image image, int sx1, int sy1, int sx2, int sy2) {
		assert(image!=null);
		
		String desiredKey = makeImageKey(image, sx1, sy1, sx2, sy2);
		
		if (!this.imageResources.containsKey(desiredKey)) {
			Image subImage = image;
			if (sx1>0 || sy1>0 || sx2<image.getWidth(null)-1 || sy2<image.getHeight(null)-1) {
				Image bImg = VectorToolkit.image(sx2-sx1, sy2-sy1, true);
				VectorGraphics2D g = bImg.getVectorGraphics();
				g.drawImage(
						null, 
						image,
						0f, 0f,
						bImg.getWidth(null)-1f, bImg.getHeight(null)-1f,
						sx1, sy1, sx2, sy2, null);
				g.dispose();
				subImage = bImg;
			}
			this.imageResources.put(desiredKey, subImage);
		}
		
		return desiredKey;
	}

	/** Replies all the images used to render into PDF.
	 * 
	 * @return the images in the PDF.
	 */
	public Map<String,Image> getImageResources() {
		return Collections.unmodifiableMap(this.imageResources);
	}

	private String computeDrawOp(boolean enableOutline, boolean enableFilling) {
		boolean isFilling = false;
		boolean isOutlining = false;
		if (isInteriorPainted() && enableFilling) {
			isFilling = true;
		}

		if (isOutlineDrawn() && enableOutline) {
			isOutlining = true;
		}

		if (isFilling && isOutlining) {
			return "B"; //$NON-NLS-1$
		}
		if (isFilling) {
			return "f"; //$NON-NLS-1$
		}
		if (isOutlining) {
			return "S"; //$NON-NLS-1$
		}
		return null;
	}

	private String setDrawingAttributes(boolean enableOutline, boolean enableFilling,
			boolean invertFillingOutlineColors) {
		String drawOp = computeDrawOp(enableOutline, enableFilling);

		if (drawOp!=null) {
			
			if (isInteriorPainted() && enableFilling) {
				Color color = invertFillingOutlineColors ? getOutlineColor() : getFillColor();
				if (color==null) {
					color = invertFillingOutlineColors ?
							ViewComponentConstants.DEFAULT_LINE_COLOR :
								ViewComponentConstants.DEFAULT_FILL_COLOR;
				}
	
				double alpha = color.getAlpha() / 255.;
				if (alpha!=this.currentTransparency) {
					String resourceId = getTransparencyResource(alpha);
					writeln("/", resourceId, " gs");  //$NON-NLS-1$//$NON-NLS-2$
					this.currentTransparency = alpha;
				}

				double r = color.getRed() / 255.;
				double g = color.getGreen() / 255.;
				double b = color.getBlue() / 255.;
	
				writeln( r + " " + g + " " + b + " rg");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$
			}
	
			if (isOutlineDrawn() && enableOutline) {
				Color color = invertFillingOutlineColors ? getFillColor() : getOutlineColor();
				if (color==null) {
					color = invertFillingOutlineColors ?
							ViewComponentConstants.DEFAULT_FILL_COLOR :
							ViewComponentConstants.DEFAULT_LINE_COLOR;
				}
	
				double alpha = color.getAlpha() / 255.;
				if (alpha!=this.currentTransparency) {
					String resourceId = getTransparencyResource(alpha);
					writeln("/", resourceId, " gs");  //$NON-NLS-1$//$NON-NLS-2$
					this.currentTransparency = alpha;
				}

				double r = color.getRed() / 255.;
				double g = color.getGreen() / 255.;
				double b = color.getBlue() / 255.;
	
				writeln( r + " " + g + " " + b + " RG");  //$NON-NLS-1$ //$NON-NLS-2$//$NON-NLS-3$			
			}
			
			Stroke bStroke = getStroke();

			if (this.currentLineWidth!=bStroke.getLineWidth()) {
				this.currentLineWidth = bStroke.getLineWidth();
				writeln( this.currentLineWidth + " w"); //$NON-NLS-1$
			}
			PdfEndCaps endCap = PdfEndCaps.fromGenericType(bStroke.getEndCap());
			if (this.currentEndCap!=endCap) {
				this.currentEndCap = endCap;
				writeln( this.currentEndCap.pdf() + " J"); //$NON-NLS-1$
			}
			PdfLineJoin lineJoin = PdfLineJoin.fromGenericType(bStroke.getLineJoin());
			if (this.currentLineJoin!=lineJoin) {
				this.currentLineJoin = lineJoin;
				writeln( this.currentLineJoin.pdf() + " j"); //$NON-NLS-1$
			}
			if (this.currentMiterLimit!=bStroke.getMiterLimit()) {
				this.currentMiterLimit = bStroke.getMiterLimit();
				writeln( this.currentMiterLimit+ " M"); //$NON-NLS-1$
			}
			float[] dashes = bStroke.getDashArray();
			if (this.currentDashPhase!=bStroke.getDashPhase() ||
				!Arrays.equals(this.currentDashes, dashes)) {
				this.currentDashPhase = bStroke.getDashPhase();
				this.currentDashes = dashes;
				if (this.currentDashes!=null) {
					write("["); //$NON-NLS-1$
					for(float dash : this.currentDashes) {
						write(" "+dash); //$NON-NLS-1$
					}
					write("] "); //$NON-NLS-1$
					write(Float.toString(this.currentDashPhase));
					write(" d"); //$NON-NLS-1$
				}
			}
		}

		return drawOp;
	}
	
	private void resetDrawingAttributes() {
		this.currentLineWidth = Float.NaN;
		this.currentMiterLimit = Float.NaN;
		this.currentDashPhase = Float.NaN;
		this.currentEndCap = null;
		this.currentLineJoin = null;
		this.currentDashes = null;
	}
	
	@Override
	public void beginGroup() {
		writeln("q"); //$NON-NLS-1$
	}

	@Override
	public void endGroup() {
		writeln("Q"); //$NON-NLS-1$
		resetDrawingAttributes();
	}

	@Override
	public boolean drawImage(URL imageURL, Image img, float dx1, float dy1,
			float dx2, float dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		String resourceId = getImageResource(img, sx1, sy1, sx2, sy2);
		if (resourceId==null) return false;
		
		preDrawing();

		Rectangle2f imgBounds = new Rectangle2f();
		imgBounds.setFromCorners(dx1, dy1, dx2, dy2);
		imgBounds = PdfUtil.toPdf(imgBounds);
		
		float sx = imgBounds.getWidth();
		float sy = imgBounds.getHeight();
		float tx = imgBounds.getMinX();
		float ty = imgBounds.getMinY();
		
		// Save graphics state
		writeln("q"); //$NON-NLS-1$
		
		// Write the current transformation
		writeCurrentTransform();
		
		// Move image to correct position and scale it to (width, height)
		write(	sx,
				" 0 0 ", //$NON-NLS-1$
				sy,
				" ", //$NON-NLS-1$
				tx,
				" ",  //$NON-NLS-1$
				ty,
				" cm "); //$NON-NLS-1$
		// Draw image
		writeln("/", resourceId, " Do");  //$NON-NLS-1$//$NON-NLS-2$
		// Restore old graphics state
		writeln("Q"); //$NON-NLS-1$
		
		postDrawing();
		
		return true;
	}

	@Override
	public void drawString(String str, float x, float y) {
		drawString(str, x, y, null);
	}

	@Override
	public void drawString(String str, float x, float y, Shape2f clip) {
		Shape2f oldClip = getClip();

		preDrawing();

		if (clip!=null) {
			clip(clip);
		}
		
		setDrawingAttributes(true, true, true);

		float fontSize = getFont().getSize();

		// Start text and save current graphics state
		writeln("q BT"); //$NON-NLS-1$

		String fontResourceId = getFontResource(getFont());
		writeln("/", fontResourceId, " ", fontSize, " Tf");  //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$

		// Set leading
		//writeln(fontSize + leading, " TL");

		// Move the text at the right position
		writeln("1 0 0 1 ", //$NON-NLS-1$
				PdfUtil.toPdfX(x), " ", //$NON-NLS-1$
				PdfUtil.toPdfY(y), " cm"); //$NON-NLS-1$

		String text = PdfUtil.toPdf(str.replaceAll("[\r\n]+", "")); //$NON-NLS-1$ //$NON-NLS-2$
		writeln("(", text, ") Tj");  //$NON-NLS-1$//$NON-NLS-2$

		// End text and restore previous graphics state
		writeln("ET Q"); //$NON-NLS-1$

		if (clip!=null) {
			setClip(oldClip);
		}
		
		postDrawing();
	}

	private static String computePdfPath(Iterator<PathElement2f> iterator, Transform2D transform) {
		StringBuilder buffer = new StringBuilder();
		
		boolean insertSeparator = false;
		Point2D pt = new Point2f();
		PathElement2f element;
		
		while (iterator.hasNext()) {
			element = iterator.next();
			if (insertSeparator) {
				buffer.append(" "); //$NON-NLS-1$
			}
			else {
				insertSeparator = true;
			}
			switch(element.type) {
			case MOVE_TO:
				pt.set(element.toX, element.toY);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" m"); //$NON-NLS-1$
				break;
			case LINE_TO:
				pt.set(element.toX, element.toY);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" l"); //$NON-NLS-1$
				break;
			case CURVE_TO:
				pt.set(element.ctrlX1, element.ctrlY1);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" "); //$NON-NLS-1$
				pt.set(element.ctrlX2, element.ctrlY2);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" "); //$NON-NLS-1$
				pt.set(element.toX, element.toY);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" c"); //$NON-NLS-1$
				break;
			case QUAD_TO:
				float qx1 = element.fromX + 2f/3f*(element.ctrlX1 - element.fromX);
				float qy1 = element.fromY + 2f/3f*(element.ctrlY1 - element.fromY);
				float qx2 = element.ctrlX1 + 1f/3f*(element.toX - element.ctrlX1); 
				float qy2 = element.ctrlY1 + 1f/3f*(element.toY - element.ctrlY1); 
				float qx3 = element.toX; 
				float qy3 = element.toY; 
				pt.set(qx1, qy1);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" "); //$NON-NLS-1$
				pt.set(qx2, qy2);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" "); //$NON-NLS-1$
				pt.set(qx3, qy3);
				transform.transform(pt);
				buffer.append(PdfUtil.toPdfX(pt.getX()));
				buffer.append(" "); //$NON-NLS-1$
				buffer.append(PdfUtil.toPdfY(pt.getY()));
				buffer.append(" c"); //$NON-NLS-1$
				break;
			case CLOSE:
				buffer.append("h"); //$NON-NLS-1$
				break;
			default:
				throw new IllegalStateException();
			}
		}
		
		return buffer.toString();
	}
	
	@Override
	protected void drawPath(PathIterator2f pathIterator) {
		String drawOp = setDrawingAttributes(true, true, false);
		if (drawOp==null) return ;

		preDrawing();
		
		writeln(computePdfPath(pathIterator, this.currentTransform), " ", drawOp); //$NON-NLS-1$

		postDrawing();
	}

	@Override
	protected void drawLine(Segment2f line) {
		drawPath(line.getPathIterator());
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
	protected void drawEllipse(Ellipse2f ellipse) {
		drawPath(ellipse.getPathIterator());
	}

	@Override
	protected void drawCircle(Circle2f circle) {
		drawPath(circle.getPathIterator());
	}
	
	@Override
	public void setStroke(Stroke stroke) {
		Stroke prevStroke = getStroke();

		super.setStroke(stroke);

		if (stroke.getLineWidth() != prevStroke.getLineWidth()) {
			writeln(stroke.getLineWidth(), " w"); //$NON-NLS-1$
		}
		if (stroke.getLineJoin() != prevStroke.getLineJoin()) {
			PdfLineJoin lineJoin = PdfLineJoin.fromGenericType(stroke.getLineJoin());
			assert(lineJoin!=null);
			writeln(lineJoin.pdf(), " j"); //$NON-NLS-1$
		}
		if (stroke.getEndCap() != prevStroke.getEndCap()) {
			PdfEndCaps endCaps = PdfEndCaps.fromGenericType(stroke.getEndCap());
			assert(endCaps!=null);
			writeln(endCaps.pdf(), " J"); //$NON-NLS-1$
		}
		if ((!Arrays.equals(stroke.getDashArray(), prevStroke.getDashArray()))
				|| (stroke.getDashPhase() != prevStroke.getDashPhase())) {
			StringBuilder buffer = new StringBuilder();
			for(float dash : stroke.getDashArray()) {
				if (buffer.length()>0) buffer.append(" "); //$NON-NLS-1$
				buffer.append(dash);
			}
			writeln("[", buffer, "] ", stroke.getDashPhase(), " d");  //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	@Override
	public void setClip(Shape2f clip) {
		if (getClip() != null) {
			writeln("Q"); //$NON-NLS-1$
			resetDrawingAttributes();
		}
		super.setClip(clip);
		Shape2f clipShape = getClip();
		if (clipShape!=null) {
			writeln("q"); //$NON-NLS-1$
			String pathStr = computePdfPath(clipShape.getPathIterator(), this.currentTransform);
			writeln(pathStr, " W n"); //$NON-NLS-1$
		}
	}

	@Override
	public void clip(Shape2f clip) {
		if (getClip() != null) {
			writeln("Q"); //$NON-NLS-1$
			resetDrawingAttributes();
		}
		super.clip(clip);
		Shape2f clipShape = getClip();
		if (clipShape!=null) {
			writeln("q"); //$NON-NLS-1$
			String pathStr = computePdfPath(clipShape.getPathIterator(), this.currentTransform);
			writeln(pathStr, " W n"); //$NON-NLS-1$
		}
	}
	
	/** Replies the generated string.
	 * 
	 * @return the generated string.
	 */
	public String getGeneratedString() {
		return this.buffer.toString();
	}

}
