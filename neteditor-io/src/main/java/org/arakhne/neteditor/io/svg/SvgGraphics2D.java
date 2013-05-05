/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.svg ;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.PathElement2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.continous.object2d.Segment2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.StringAnchor;
import org.arakhne.afc.ui.TextAlignment;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.ImageObserver;
import org.arakhne.afc.ui.vector.Stroke;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.io.AbstractVectorialExporterGraphics2D;
import org.arakhne.neteditor.io.VectorialExporterException;
import org.arakhne.util.text.Base64Coder;
import org.arakhne.vmutil.FileSystem;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** This graphic context permits to create a SVG file
 *  from a graphic context.
 *  <p>
 *  The SVG format is specified in <a href="http://www.w3.org/Graphics/SVG/">W3C</a>.
 *  <p>
 *  The supported specifications are: <a href="http://www.w3.org/TR/SVG11/">1.1</a>.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 15.0
 */
public class SvgGraphics2D extends AbstractVectorialExporterGraphics2D {

	/** Version of the XML format.
	 */
	public static final String XML_VERSION = "1.0"; //$NON-NLS-1$

	/** Version of the SVG format.
	 */
	public static final String SVG_VERSION = "1.1"; //$NON-NLS-1$

	/** Namespace for SVG.
	 */
	public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg"; //$NON-NLS-1$

	/** Public name of the DTD.
	 */
	public static final String DTD_PUBLIC = "-//W3C//DTD SVG 1.1//EN"; //$NON-NLS-1$

	/** System name of the DTD.
	 */
	public static final String DTD_SYSTEM = "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"; //$NON-NLS-1$

	/** URI of the XLink name space.
	 */
	public static final String XLINK_NS_URI = "http://www.w3.org/1999/xlink"; //$NON-NLS-1$

	private static String toSVG(Color color) {
		return "rgb("+color.getRed() //$NON-NLS-1$
				+","+color.getGreen() //$NON-NLS-1$
				+","+color.getBlue() //$NON-NLS-1$
				+")"; //$NON-NLS-1$
	}
	
	private static String toSVG(Transform2D transform) {
		return "matrix( " //$NON-NLS-1$
				+transform.getM00()+"," //$NON-NLS-1$
				+transform.getM10()+"," //$NON-NLS-1$
				+transform.getM01()+"," //$NON-NLS-1$
				+transform.getM11()+"," //$NON-NLS-1$
				+transform.getM02()+"," //$NON-NLS-1$
				+transform.getM12()+")"; //$NON-NLS-1$
	}

	private static String toSVG(Shape2f shape) {
		StringBuilder b = new StringBuilder();
		Iterator<PathElement2f> pathIterator = shape.getPathIterator();
		PathElement2f pathElement;
		while (pathIterator.hasNext()) {
			pathElement = pathIterator.next();
			switch(pathElement.type) {
			case MOVE_TO:
				if (b.length()>0) b.append(" "); //$NON-NLS-1$
				b.append("M"+pathElement.toX+","+pathElement.toY);  //$NON-NLS-1$//$NON-NLS-2$
				break;
			case LINE_TO:
				if (b.length()>0) b.append(" "); //$NON-NLS-1$
				b.append("L"+pathElement.toX+","+pathElement.toY);  //$NON-NLS-1$//$NON-NLS-2$
				break;
			case CURVE_TO:
				if (b.length()>0) b.append(" "); //$NON-NLS-1$
				b.append("C"+pathElement.ctrlX1+","+pathElement.ctrlY1  //$NON-NLS-1$//$NON-NLS-2$
						+","+pathElement.ctrlX2+","+pathElement.ctrlY2  //$NON-NLS-1$//$NON-NLS-2$
						+","+pathElement.toX+","+pathElement.toY);//$NON-NLS-1$//$NON-NLS-2$
				break;
			case QUAD_TO:
				if (b.length()>0) b.append(" "); //$NON-NLS-1$
				b.append("Q"+pathElement.ctrlX1+","+pathElement.ctrlY1  //$NON-NLS-1$//$NON-NLS-2$
						+","+pathElement.toX+","+pathElement.toY);//$NON-NLS-1$//$NON-NLS-2$
				break;
			case CLOSE:
				if (b.length()>0) b.append(" "); //$NON-NLS-1$
				b.append("Z"); //$NON-NLS-1$
				break;
			default:
				throw new IllegalStateException();
			}
		}
		return b.toString();
	}

	/** XML document that contains SVG tags.
	 */
	Document xmldocument = null;

	/** Current node for the SVG.
	 */
	private Node svgCurrentNode = null;

	/** Root node for the SVG.
	 */
	private Node svgRootNode = null;
	
	/** Name space to append to all the generated SVG tags.
	 */
	private String namespace = null;

	////////////////////////////////////////////////////////////
	// Constructor

	/** Construct a new SvgGraphics2D.
	 * 
	 * @param bounds are the bounds of the document.
	 */
	public SvgGraphics2D(Rectangle2f bounds) {
		pushRenderingContext(null, null, bounds);
	}
	
	@Override
	public float getShadowTranslationX() {
		return ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_X*.5f;
	}
	
	@Override
	public float getShadowTranslationY() {
		return ViewComponentConstants.DEFAULT_SHADOW_PROJECTION_DISTANCE_Y*.5f;
	}

	@Override
	public void dispose() {
		super.dispose();
		this.xmldocument = null;
		this.svgCurrentNode = null;
		this.svgRootNode = null;
		this.namespace = null;
	}
	
	@Override
	public void reset() {
		Rectangle2f rootBounds = getRootBounds();
		super.reset();
		this.xmldocument = null;
		this.svgCurrentNode = null;
		this.svgRootNode = null;
		this.namespace = null;
		pushRenderingContext(null, null, rootBounds);
	}

	/** Replies the namespace to appand to all the generated SVG tags.
	 * 
	 * @return the namespapce to append to.
	 */
	public String getNamespace() {
		return this.namespace;
	}

	/** Set the namespace to appand to all the generated SVG tags.
	 * 
	 * @param ns is the namespapce to append to.
	 */
	public void setNamespace(String ns) {
		this.namespace = (ns==null || ns.isEmpty()) ? null : ns;
	}
	
	/** Build the name of a SVG tag.
	 * This function append the namespace replied by
	 * {@link #getNamespace()} to the given tag name.
	 * 
	 * @param tagName
	 * @return the complete tag name.
	 */
	protected String tag(String tagName) {
		String ns = getNamespace();
		if (ns!=null && !ns.isEmpty())
			return ns + ":" + tagName; //$NON-NLS-1$
		return tagName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void prolog() {
		if (this.xmldocument==null) {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder;
			try {
				builder = factory.newDocumentBuilder();
			}
			catch (ParserConfigurationException e) {
				throw new VectorialExporterException(e);
			}

			this.xmldocument = builder.newDocument();	

			Comment comment = this.xmldocument.createComment(
					"\n\tCreator: Arakhne.org NetEditor "+getClass().getName()//$NON-NLS-1$
					+" $Version$\n\tCreationDate: "//$NON-NLS-1$
					+(new Date())
					+"\n"); //$NON-NLS-1$
			this.xmldocument.appendChild(comment);
		}

		Rectangle2f bounds = getCurrentViewComponentBounds();
		Element root = this.xmldocument.createElement(tag("svg")); //$NON-NLS-1$;
		this.svgRootNode = root;
		root.setAttribute("xmlns:xlink", XLINK_NS_URI); //$NON-NLS-1$
		this.xmldocument.appendChild(this.svgRootNode);
		root.setAttribute("version", SVG_VERSION); //$NON-NLS-1$
		root.setAttribute("xmlns", SVG_NAMESPACE);  //$NON-NLS-1$
		root.setAttribute("viewBox",  //$NON-NLS-1$
				(int)Math.floor(bounds.getMinX())+" "  //$NON-NLS-1$
				+(int)Math.floor(bounds.getMinY())+" "  //$NON-NLS-1$
				+(int)Math.ceil(bounds.getWidth())+" "  //$NON-NLS-1$
				+(int)Math.ceil(bounds.getHeight()));
		this.svgCurrentNode = this.svgRootNode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void epilog() {
		this.xmldocument.setXmlVersion(XML_VERSION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StringAnchor getStringAnchor() {
		return StringAnchor.LOWER_LEFT;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void transform(Transform2D Tx) {
		super.transform(Tx);
		Element transform = this.xmldocument.createElement(tag("g")); //$NON-NLS-1$
		this.svgCurrentNode.appendChild(transform);
		this.svgCurrentNode = transform;
		transform.setAttribute("transform", toSVG(Tx)); //$NON-NLS-1$
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Transform2D setTransform(Transform2D Tx) {
		Transform2D old = super.setTransform(Tx);
		if (Tx.isIdentity()) {
			this.svgCurrentNode = this.svgRootNode;
		}
		else {
			Element transform = this.xmldocument.createElement(tag("g")); //$NON-NLS-1$
			this.svgRootNode.appendChild(transform);
			this.svgCurrentNode = transform;
			transform.setAttribute("transform", toSVG(Tx)); //$NON-NLS-1$
		}
		return old;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void beginGroup() {
		Element groupN = this.xmldocument.createElement(tag("g")); //$NON-NLS-1$
		this.svgCurrentNode.appendChild(groupN);
		this.svgCurrentNode = groupN;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endGroup() {
		if (this.svgCurrentNode!=this.svgRootNode) {
			this.svgCurrentNode = this.svgCurrentNode.getParentNode();
		}
	}
	
	private void setDrawingAttributes(Element node, boolean enableOutline, boolean enableFilling, boolean invertFillingOutlineColors) {
		if (isInteriorPainted() && enableFilling) {
			Color color = invertFillingOutlineColors ? getOutlineColor() : getFillColor();
			if (color==null) {
				color = invertFillingOutlineColors ?
							ViewComponentConstants.DEFAULT_LINE_COLOR :
								ViewComponentConstants.DEFAULT_FILL_COLOR;
			}
			node.setAttribute("fill", toSVG(color)); //$NON-NLS-1$
			node.setAttribute("fill-opacity", Float.toString(color.getAlpha()/255f)); //$NON-NLS-1$
		}
		else {
			node.setAttribute("fill", "none"); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (isOutlineDrawn() && enableOutline) {
			Stroke stroke = getStroke();
			float width = 1f;
			String linecap = "butt"; //$NON-NLS-1$
			String linejoin = "miter"; //$NON-NLS-1$
			float miterLimit = 4f;
			String dashArray = null;
			float dashOffset = 0f;
			Color color = invertFillingOutlineColors ? getFillColor() : getOutlineColor();
			if (color==null) {
				color = invertFillingOutlineColors ?
							ViewComponentConstants.DEFAULT_FILL_COLOR :
								ViewComponentConstants.DEFAULT_LINE_COLOR;
			}

			width = stroke.getLineWidth();
			miterLimit = stroke.getMiterLimit();
			dashOffset = stroke.getDashPhase();
			
			switch(stroke.getEndCap()) {
			case BUTT:
				linecap = "butt"; //$NON-NLS-1$
				break;
			case ROUND:
				linecap = "round"; //$NON-NLS-1$
				break;
			case SQUARE:
				linecap = "square"; //$NON-NLS-1$
				break;
			default:
				throw new IllegalStateException();
			}

			switch(stroke.getLineJoin()) {
			case MITER:
				linejoin = "miter"; //$NON-NLS-1$
				break;
			case BEVEL:
				linejoin = "bevel"; //$NON-NLS-1$
				break;
			case ROUND:
				linejoin = "round"; //$NON-NLS-1$
				break;
			default:
				throw new IllegalStateException();
			}
			
			StringBuilder b = new StringBuilder();
			float[] dashes = stroke.getDashArray();
			if (dashes!=null && dashes.length>0) {
				for(float f : dashes) {
					if (b.length()>0) b.append(","); //$NON-NLS-1$
					b.append(f);
				}
				dashArray = b.toString();
			}
			
			node.setAttribute("stroke", toSVG(color)); //$NON-NLS-1$
			node.setAttribute("stroke-width", Float.toString(width)); //$NON-NLS-1$
			node.setAttribute("stroke-linecap", linecap); //$NON-NLS-1$
			node.setAttribute("stroke-linejoin", linejoin); //$NON-NLS-1$
			node.setAttribute("stroke-miterlimit", Float.toString(miterLimit)); //$NON-NLS-1$
			if (dashArray!=null && !dashArray.isEmpty()) {
				node.setAttribute("stroke-dasharray", dashArray); //$NON-NLS-1$
				node.setAttribute("stroke-dashoffset", Float.toString(dashOffset)); //$NON-NLS-1$
			}
			node.setAttribute("stroke-opacity", Float.toString(color.getAlpha()/255f)); //$NON-NLS-1$
		}
		else {
			node.setAttribute("stroke", "none"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	private Element createInternalTextFor(String text) {
		Element textN = this.xmldocument.createElement(tag("text")); //$NON-NLS-1$
		textN.appendChild(this.xmldocument.createTextNode(text));
		
		Rectangle2f r = getCurrentViewComponentBounds();
		Point2D position = computeTextPosition(text, r, TextAlignment.CENTER_ALIGN, TextAlignment.CENTER_ALIGN);
		Font font = getFont();
		
		textN.setAttribute("x", Double.toString(position.getX()));  //$NON-NLS-1$
		textN.setAttribute("y", Double.toString(position.getY()));  //$NON-NLS-1$
		textN.setAttribute("font-family", font.getFamily());  //$NON-NLS-1$
		textN.setAttribute("font-size", Float.toString(font.getSize()));  //$NON-NLS-1$
		
		setDrawingAttributes(textN, false, true, true);
		
		return textN;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean drawImage(URL imageURL, Image img, float dx1, float dy1,
			float dx2, float dy2, int sx1, int sy1, int sx2, int sy2,
			ImageObserver observer) {
		if (imageURL==null && img==null)
			return false;
		
		assert(imageURL!=null || img!=null);
		preDrawing();
		
		Element imageN = this.xmldocument.createElement(tag("image")); //$NON-NLS-1$
		this.svgCurrentNode.appendChild(imageN);
		float x = Math.min(dx1,  dx2);
		float y = Math.min(dy1,  dy2);
		float width = Math.abs(dx2 - dx1);
		float height = Math.abs(dy2 - dy1);
		imageN.setAttribute("x", Float.toString(x)); //$NON-NLS-1$
		imageN.setAttribute("y", Float.toString(y)); //$NON-NLS-1$
		imageN.setAttribute("width", Float.toString(width)); //$NON-NLS-1$
		imageN.setAttribute("height", Float.toString(height)); //$NON-NLS-1$
		imageN.setAttribute("preserveAspectRatio", "none"); //$NON-NLS-1$ //$NON-NLS-2$
		String link;
		if (imageURL==null || FileSystem.isJarURL(imageURL)) {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				Image bimg = img;
				if (bimg==null) {
					assert(imageURL!=null);
					bimg = VectorToolkit.image(imageURL);
				}
				
				assert(bimg!=null);
				VectorToolkit.writeImage(bimg, "png", baos); //$NON-NLS-1$

				char[] encodedContent = Base64Coder.encode(baos.toByteArray());

				link = "data:image/png;base64," //$NON-NLS-1$
						+ new String(encodedContent);
			}
			catch(IOException e) {
				throw new VectorialExporterException("Cannot retreive the picture: "+imageURL, e); //$NON-NLS-1$
			}
		}
		else {
			link = imageURL.toExternalForm();
		}
		imageN.setAttribute("xlink:href", link); //$NON-NLS-1$
		
		postDrawing();
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drawString(String str, float x, float y) {
		preDrawing();
		
		Font font = getFont();
		Element textN = this.xmldocument.createElement(tag("text")); //$NON-NLS-1$
		this.svgCurrentNode.appendChild(textN);
		textN.setAttribute("x", Float.toString(x)); //$NON-NLS-1$
		textN.setAttribute("y", Float.toString(y)); //$NON-NLS-1$
		textN.setAttribute("font-size", Float.toString(font.getSize())); //$NON-NLS-1$
		textN.setAttribute("font-family", font.getFamily()); //$NON-NLS-1$

		textN.appendChild(this.xmldocument.createTextNode(str));
		
		setDrawingAttributes(textN, false, true, true);
		
		postDrawing();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void drawString(String str, float x, float y, Shape2f clip) {
		if (clip==null) {
			drawString(str, x, y);
		}
		else {
			preDrawing();
			
			Element gN = this.xmldocument.createElement(tag("g")); //$NON-NLS-1$
			this.svgCurrentNode.appendChild(gN);
			
			String clipPathId = UUID.randomUUID().toString();
			
			{
				Element clipPathN = this.xmldocument.createElement(tag("clipPath")); //$NON-NLS-1$
				gN.appendChild(clipPathN);
				clipPathN.setAttribute("id", clipPathId); //$NON-NLS-1$
				Element pathN = this.xmldocument.createElement(tag("path")); //$NON-NLS-1$
				clipPathN.appendChild(pathN);
				pathN.setAttribute("d", toSVG(clip)); //$NON-NLS-1$
			}
			
			Font font = getFont();
			Element textN = this.xmldocument.createElement(tag("text")); //$NON-NLS-1$
			gN.appendChild(textN);
			textN.setAttribute("x", Float.toString(x)); //$NON-NLS-1$
			textN.setAttribute("y", Float.toString(y)); //$NON-NLS-1$
			textN.setAttribute("font-size", Float.toString(font.getSize())); //$NON-NLS-1$
			textN.setAttribute("font-family", font.getFamily()); //$NON-NLS-1$
			textN.setAttribute("clip-path", "url(#"+clipPathId+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			
			textN.appendChild(this.xmldocument.createTextNode(str));
			
			setDrawingAttributes(textN, false, true, true);

			postDrawing();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawPath(PathIterator2f pathIterator) {
		preDrawing();
		
		PathElement2f pathElement;
		StringBuilder points = new StringBuilder();

		while (pathIterator.hasNext()) {
			pathElement = pathIterator.next();
			switch(pathElement.type) {
			case MOVE_TO:
				if (points.length()>0) points.append(" "); //$NON-NLS-1$
				points.append("M"+pathElement.toX+","+pathElement.toY);  //$NON-NLS-1$//$NON-NLS-2$
				break;
			case LINE_TO:
				if (points.length()>0) points.append(" "); //$NON-NLS-1$
				points.append("L"+pathElement.toX+","+pathElement.toY);  //$NON-NLS-1$//$NON-NLS-2$
				break;
			case CURVE_TO:
				if (points.length()>0) points.append(" "); //$NON-NLS-1$
				points.append("C" //$NON-NLS-1$
						+pathElement.ctrlX1+","+pathElement.ctrlY1+" " //$NON-NLS-1$ //$NON-NLS-2$
						+pathElement.ctrlX2+","+pathElement.ctrlY2+" " //$NON-NLS-1$ //$NON-NLS-2$
						+pathElement.toX+","+pathElement.toY); //$NON-NLS-1$
				break;
			case QUAD_TO:
				points.append("Q" //$NON-NLS-1$
						+pathElement.ctrlX1+","+pathElement.ctrlY1+" " //$NON-NLS-1$ //$NON-NLS-2$
						+pathElement.toX+","+pathElement.toY); //$NON-NLS-1$
				break;
			case CLOSE:
				if (points.length()>0) points.append(" "); //$NON-NLS-1$
				points.append("L"); //$NON-NLS-1$
				break;
			default:
				throw new IllegalStateException();
			}
		}

		Element pathN = this.xmldocument.createElement(tag("path")); //$NON-NLS-1$
		this.svgCurrentNode.appendChild(pathN);
		pathN.setAttribute("d", points.toString()); //$NON-NLS-1$
		setDrawingAttributes(pathN, true, true, false);
		
		postDrawing();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawEllipse(Ellipse2f ellipse) {
		preDrawing();
		
		Element ovalN = this.xmldocument.createElement(tag("ellipse")); //$NON-NLS-1$
		ovalN.setAttribute("cx", Double.toString(ellipse.getCenterX())); //$NON-NLS-1$
		ovalN.setAttribute("cy", Double.toString(ellipse.getCenterY())); //$NON-NLS-1$
		ovalN.setAttribute("rx", Double.toString(ellipse.getWidth()/2)); //$NON-NLS-1$
		ovalN.setAttribute("ry", Double.toString(ellipse.getHeight()/2)); //$NON-NLS-1$
		setDrawingAttributes(ovalN, true, true, false);
		exportInternalText(ovalN);
		
		postDrawing();
	}
	
	@Override
	protected void drawCircle(Circle2f circle) {
		preDrawing();
		
		Element ovalN = this.xmldocument.createElement(tag("circle")); //$NON-NLS-1$
		ovalN.setAttribute("cx", Double.toString(circle.getX())); //$NON-NLS-1$
		ovalN.setAttribute("cy", Double.toString(circle.getY())); //$NON-NLS-1$
		ovalN.setAttribute("r", Double.toString(circle.getRadius())); //$NON-NLS-1$
		setDrawingAttributes(ovalN, true, true, false);
		exportInternalText(ovalN);
		
		postDrawing();
	}

	private void exportInternalText(Element element) {
		String clipPathId = UUID.randomUUID().toString();
		String text = getInteriorText();
		Element gN = null;
		if (text!=null && !text.isEmpty()) {
			gN = this.xmldocument.createElement(tag("g")); //$NON-NLS-1$
			this.svgCurrentNode.appendChild(gN);
			{
				Element clipPathN = this.xmldocument.createElement(tag("clipPath")); //$NON-NLS-1$
				gN.appendChild(clipPathN);
				clipPathN.setAttribute("id", clipPathId); //$NON-NLS-1$
				Element pathN = this.xmldocument.createElement(tag("path")); //$NON-NLS-1$
				clipPathN.appendChild(pathN);
				pathN.setAttribute("d", toSVG(getCurrentViewComponentShape())); //$NON-NLS-1$
			}
			gN.appendChild(element);
		}
		else {
			this.svgCurrentNode.appendChild(element);
		}
		if (gN!=null && text!=null && !text.isEmpty()) {
			Element textN = createInternalTextFor(text);
			gN.appendChild(textN);
			textN.setAttribute("clip-path", "url(#"+clipPathId+")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawLine(Segment2f line) {
		preDrawing();
		
		Element ovalN = this.xmldocument.createElement(tag("line")); //$NON-NLS-1$
		ovalN.setAttribute("x1", Double.toString(line.getX1())); //$NON-NLS-1$
		ovalN.setAttribute("y1", Double.toString(line.getY1())); //$NON-NLS-1$
		ovalN.setAttribute("x2", Double.toString(line.getX2())); //$NON-NLS-1$
		ovalN.setAttribute("y2", Double.toString(line.getY2())); //$NON-NLS-1$
		setDrawingAttributes(ovalN, true, true, false);
		exportInternalText(ovalN);
		
		postDrawing();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawRectangle(Rectangle2f rectangle) {
		preDrawing();
		
		Element rectN = this.xmldocument.createElement(tag("rect")); //$NON-NLS-1$
		rectN.setAttribute("x", Double.toString(rectangle.getMinX())); //$NON-NLS-1$
		rectN.setAttribute("y", Double.toString(rectangle.getMinY())); //$NON-NLS-1$
		rectN.setAttribute("width", Double.toString(rectangle.getWidth())); //$NON-NLS-1$
		rectN.setAttribute("height", Double.toString(rectangle.getHeight())); //$NON-NLS-1$
		setDrawingAttributes(rectN, true, true, false);
		exportInternalText(rectN);
		
		postDrawing();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void drawRoundRectangle(RoundRectangle2f rectangle) {
		preDrawing();
		
		Element rectN = this.xmldocument.createElement(tag("rect")); //$NON-NLS-1$
		rectN.setAttribute("x", Double.toString(rectangle.getMinX())); //$NON-NLS-1$
		rectN.setAttribute("y", Double.toString(rectangle.getMinY())); //$NON-NLS-1$
		rectN.setAttribute("width", Double.toString(rectangle.getWidth())); //$NON-NLS-1$
		rectN.setAttribute("height", Double.toString(rectangle.getHeight())); //$NON-NLS-1$
		rectN.setAttribute("rx", Double.toString(rectangle.getArcWidth())); //$NON-NLS-1$
		rectN.setAttribute("ry", Double.toString(rectangle.getArcHeight())); //$NON-NLS-1$
		setDrawingAttributes(rectN, true, true, false);
		exportInternalText(rectN);
		
		postDrawing();
	}

}
