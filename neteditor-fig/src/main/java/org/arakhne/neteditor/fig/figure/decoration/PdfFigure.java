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
package org.arakhne.neteditor.fig.figure.decoration;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.ImageObserver;
import org.arakhne.afc.ui.vector.Pdf;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** A decoration figure that is drawing a bitmap.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public class PdfFigure extends BlockDecorationFigure implements ImageObserver {

	private static final long serialVersionUID = 3357210009571869767L;

	/** <code>true</code> if this text area must be framed, 
	 *  <code>false</code> otherwise.
	 */
	private boolean framed = true;

	/** This is the filename of the icon.
	 */
	private URL filename = null;

	/** Permits to manipulate the entire PDF file.
	 */
	private Pdf pdfFile = null;

	/** Image that is used to render the PDF page in a transparent way.
	 */
	private transient SoftReference<Image> transparentImage = null;

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public PdfFigure(UUID viewUUID) {
		this(viewUUID, 0, 0);
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 */
	public PdfFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE);
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 * @param width is the width of the figure.
	 * @param height is the height of the figure.
	 */
	public PdfFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height);
	}

	/** Change the framing flag.
	 *
	 * @param framed <code>true</code> if this FigText
	 *               must be framed, <code>false</code> otherwise.
	 */
	public void setFramed(boolean framed) {
		if (framed!=this.framed) {
			boolean old = this.framed;
			this.framed = framed;
			firePropertyChange(PROPERTY_FRAMED, old, this.framed); 
			repaint(false);
		}
	}

	/** Return the framed flag.
	 *
	 * @return <code>true</code> if this FigText must
	 *         be framed, <code>false</code> otherwise.
	 */
	public boolean isFramed() {
		return this.framed;
	}

	/** Change the PDF document.
	 *
	 * @param name the name of the new PDF document.
	 * @throws IOException
	 */
	public void setPdfURL(URL name) throws IOException {
		if ((name==null && this.filename!=null)
				||
				(name!=null && !name.equals(this.filename))) {
			URL old1 = this.filename;
			int old2 = getPdfPage();

			if (name!=null) {
				this.pdfFile = VectorToolkit.pdf(name);
				this.pdfFile.setViewerSize(getSize());
			}
			else {
				this.pdfFile = null;
			}
			this.filename = name;
			this.transparentImage = null;

			if (old2!=getPdfPage())
				firePropertyChange(PROPERTY_PDFPAGE, old2, getPdfPage()); 
			firePropertyChange(PROPERTY_PDFFILE, old1, this.filename);

			repaint(false);

			setPdfPage(1);
		}
	}

	/** Replies the filename associated to the PDF document.
	 * 
	 * @return the filename associated to the PDF document.
	 */
	public URL getPdfURL() {
		return this.filename;
	}

	/** Replies the image of the current page.
	 * 
	 * @return the image of the current page.
	 */
	public Image getImage() {
		return this.pdfFile!=null ? this.pdfFile.getImage() : null;
	}

	/** Replies the number of pages in the document.
	 * 
	 * @return the number of pages in the document.
	 */
	public int getNumberOfPages() {
		return (this.pdfFile!=null) ? this.pdfFile.getPageCount() : 0;
	}

	/** Replies the number of the page currently displayed.
	 * 
	 * @return the page number or {@code -1} if no page is displayed.
	 */
	public int getPdfPage() {
		return (this.pdfFile!=null) ? this.pdfFile.getPageNumber() : -1;
	}

	/** Set the page to display.
	 * 
	 * @param pageno is the number of the page to display in [1;numPages]
	 */
	public void setPdfPage(int pageno) {
		if (this.pdfFile!=null && getPdfPage()!=pageno
				&& pageno>0 && pageno<=getNumberOfPages()) {
			int oldPageno = getPdfPage();
			if (this.pdfFile.setPageNumber(pageno, this)) {
				firePropertyChange(
						PROPERTY_PDFPAGE, 
						oldPageno,
						pageno);
				repaint(false);
			}
		}
	}

	@Override
	protected void onBoundsUpdated(float oldX, float newX, float oldY,
			float newY, float oldWidth, float newWidth, float oldHeight,
			float newHeight) {
		super.onBoundsUpdated(oldX, newX, oldY, newY, oldWidth, newWidth, oldHeight,
				newHeight);
		if (this.pdfFile!=null) this.pdfFile.setViewerSize(getSize());
	}

	@Override
	protected void onSizeUpdated(float oldWidth, float newWidth,
			float oldHeight, float newHeight) {
		super.onSizeUpdated(oldWidth, newWidth, oldHeight, newHeight);
		if (this.pdfFile!=null) this.pdfFile.setViewerSize(getSize());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void paint(ViewGraphics2D g) {
		if (this.pdfFile!=null) {
			Image image = this.pdfFile.getImage();
			if (image!=null) {
				Rectangle2f bounds = g.getCurrentViewComponentBounds();
				if (g.getLOD()==Graphics2DLOD.SHADOW) {
					Image transImg = (this.transparentImage==null) ? null : this.transparentImage.get();
					if (transImg==null) {
						image = VectorToolkit.image(image, -.5f);
						this.transparentImage = new SoftReference<Image>(image);
					}
					else {
						image = transImg;
					}
				}

				int imgWidth = image.getWidth(this);
				if (imgWidth<0) return;
				int imgHeight = image.getHeight(this);
				if (imgHeight<0) return;

				g.setOutlineDrawn(isFramed());
				g.drawImage(
						this.filename,
						image,
						bounds.getMinX(),
						bounds.getMinY(),
						(bounds.getMinX()+bounds.getWidth()),
						(bounds.getMinY()+bounds.getHeight()),
						0, 0, imgWidth-1, imgHeight-1, this);
			}
			else {
				paintEmptyFigure(g);
			}
		}
		else {
			paintEmptyFigure(g);
		}
	}

	/** Invoked when there is no PDF document to draw.
	 * By default this function call {@link ViewGraphics2D#drawDefaultImage(float, float, float, float)}.
	 * 
	 * @param g
	 */
	@SuppressWarnings("static-method")
	protected void paintEmptyFigure(ViewGraphics2D g) {
		Rectangle2f bounds = g.getCurrentViewComponentBounds();
		g.drawDefaultImage(
				bounds.getMinX(),
				bounds.getMinY(),
				bounds.getMaxX(),
				bounds.getMaxY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Deprecated
	public boolean hit(float x, float y, float epsilon) {
		Rectangle2f bounds = getBounds();
		Rectangle2f rr = new Rectangle2f(
				bounds.getMinX() - epsilon,
				bounds.getMinY() - epsilon,
				bounds.getWidth() + 2*epsilon,
				bounds.getHeight() + 2*epsilon);
		return rr.contains(x,  y);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_PDFFILE, this.filename); 
		properties.put(PROPERTY_PDFPAGE, getPdfPage()); 
		properties.put(PROPERTY_FRAMED, this.framed); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_LINECOLOR, Color.class);
		properties.put(PROPERTY_FRAMED, Boolean.class); 
		properties.put(PROPERTY_PDFFILE, URL.class); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		if (properties!=null) {
			URL pdfUrl = propGetURL(PROPERTY_PDFFILE, null, false, properties); 
			if (pdfUrl!=null) {
				try {
					setPdfURL(pdfUrl);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			setPdfPage(propGetInt(PROPERTY_PDFPAGE, getPdfPage(), properties)); 
			setFramed(propGetBoolean(PROPERTY_FRAMED, this.framed, properties)); 
		}
		super.setProperties(properties);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void fitToContent() {
		Image i = getImage();
		if (i!=null) {
			setSize(i.getWidth(null), i.getHeight(null));
		}
	}

	@Override
	public boolean imageUpdate(Image img, int x, int y, int width, int height) {
		this.transparentImage = new SoftReference<Image>(VectorToolkit.image(img, -.5f));
		repaint(false);
		return false;
	}
	
	@Override
	protected void finalize() throws Throwable {
		if (this.pdfFile!=null) {
			this.pdfFile.release();
			this.pdfFile = null;
		}
	}

}
