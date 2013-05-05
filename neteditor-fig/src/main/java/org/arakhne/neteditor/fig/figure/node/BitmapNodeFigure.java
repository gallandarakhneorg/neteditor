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
package org.arakhne.neteditor.fig.figure.node;

import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.ImageObserver;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Node;

/** Node figure that is displaying a bitmap.
 *
 * @param <N> is the type of the model node supported by this figure.
 * @param <A> is the type of the model anchor supported by this figure.
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BitmapNodeFigure<N extends Node<?,? super N,? super A,?>,A extends Anchor<?,? super N,? super A,?>> extends NodeFigure<N,A> implements ImageObserver {

	private static final long serialVersionUID = 189962471845727935L;

	/** This is a reference to a bitmap.
	 */
	private Image bitmap = null;

	private SoftReference<Image> transparentBitmap = null;

	/** This is the filename of the icon.
	 * 
	 * @since 0.4
	 */
	private URL filename = null;

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public BitmapNodeFigure(UUID viewUUID) {
		this(viewUUID, 0, 0, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE );
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 */
	public BitmapNodeFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE );
	}

	/** Construct a new figure.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 * @param width is the width of the figure.
	 * @param height is the height of the figure.
	 */
	public BitmapNodeFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height );
	}

	/** Change the bitmap.
	 *
	 * @param name the name of the new bitmap.
	 * @throws IOException
	 */
	public void setImageURL(URL name) throws IOException {
		if ((name==null && this.filename!=null)
				||
				(name!=null && !name.equals(this.filename))) {
			URL old1 = this.filename;
			Image old2 = this.bitmap;
			this.filename = name;
			if (this.filename==null) {
				this.bitmap = null;
			}
			else {
				this.bitmap = VectorToolkit.image(this.filename);
			}
			this.transparentBitmap = null;
			firePropertyChange(PROPERTY_FILENAME, old1, this.filename); 
			if (old2!=this.bitmap)
				firePropertyChange(PROPERTY_IMAGE, old2, this.bitmap); 
			repaint(false);
		}
	}

	/** Replies the filename associated to the bitmap.
	 * 
	 * @return the filename associated to the bitmap.
	 */
	public URL getImageURL() {
		return this.filename;
	}

	/** Change the bitmap.
	 *
	 * @param image is the new bitmap.
	 */
	public void setImage(Image image) {
		if (this.bitmap!=image) {
			Image old1 = this.bitmap;
			URL old2 = this.filename;
			this.bitmap = image;
			this.transparentBitmap = null;
			this.filename = null;
			firePropertyChange(PROPERTY_IMAGE, old1, this.bitmap); 
			if (old2!=null)
				firePropertyChange(PROPERTY_FILENAME, old2, this.filename); 
			repaint(false);
		}
	}

	/** Replies the displayed bitmap.
	 * 
	 * @return the displayed bitmap.
	 */
	public Image getImage() {
		return this.bitmap;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintNode(ViewGraphics2D g) {
		if (this.bitmap!=null) {
			Rectangle2f figureBounds = g.getCurrentViewComponentBounds();
			Image img;
			if (g.getLOD()==Graphics2DLOD.SHADOW) {
				img = (this.transparentBitmap==null) ? null : this.transparentBitmap.get();
				if (img==null) {
					img = VectorToolkit.image(this.bitmap, -.5f);
					this.transparentBitmap = new SoftReference<Image>(img);
				}
			}
			else {
				img = this.bitmap;
			}

			int imgWidth = img.getWidth(this);
			if (imgWidth<0) return;
			int imgHeight = img.getHeight(this);
			if (imgHeight<0) return;

			g.setOutlineDrawn(true);
			g.drawImage(
					this.filename,
					img,
					figureBounds.getMinX(),
					figureBounds.getMinY(),
					figureBounds.getMaxX(),
					figureBounds.getMaxY(),
					0, 0, imgWidth-1, imgHeight-1, this);
		}
		else {
			paintEmptyFigure(g);
		}
	}
	
	/** Invoked when there is no image to draw.
	 * By default this function call {@link ViewGraphics2D#drawDefaultImage(float, float, float, float)}.
	 * 
	 * @param g
	 */
	@SuppressWarnings("static-method")
	protected void paintEmptyFigure(ViewGraphics2D g) {
		Rectangle2f bounds = g.getCurrentViewComponentBounds();
		g.setOutlineDrawn(true);
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
	public boolean imageUpdate(
			Image img, int x, int y,
			int width, int height) {
		repaint(false);
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_IMAGE, this.bitmap); 
		properties.put(PROPERTY_FILENAME, this.filename); 
		return properties;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_FILENAME, URL.class); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			URL imageUrl = propGetURL(PROPERTY_FILENAME, null, false, properties); 
			if (imageUrl!=null) {
				try {
					setImageURL(imageUrl);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
			else {
				setImage(propGetImage(PROPERTY_IMAGE, this.bitmap, false, properties)); 
			}
		}
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

}
