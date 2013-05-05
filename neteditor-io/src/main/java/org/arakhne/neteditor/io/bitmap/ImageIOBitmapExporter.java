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

package org.arakhne.neteditor.io.bitmap ;

import java.io.IOException;
import java.io.OutputStream;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.graphics.ViewGraphicsUtil;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.io.BitmapExporter;

/** This class provides the base feature for all the bitmap exporters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ImageIOBitmapExporter implements BitmapExporter {

	private final ImageType type;
	private Progression taskProgression = null;
	private boolean isShadow = false;
	
	/**
	 * @param type
	 */
	public ImageIOBitmapExporter(ImageType type) {
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progression getProgression() {
		return this.taskProgression;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProgression(Progression model) {
		Progression old = this.taskProgression;
		this.taskProgression = model;
		if (old!=null && this.taskProgression!=null) {
			this.taskProgression.setProperties(
					old.getValue(),
					old.getMinimum(),
					old.getMaximum(),
					old.isAdjusting(),
					old.getComment());
			this.taskProgression.setIndeterminate(old.isIndeterminate());
		}
	}

	/** {@inheritDoc}
     */
	@Override
    public boolean write(OutputStream stream, ViewComponentContainer<?,?> container, float scale) 
	throws IOException {
		synchronized(container.getTreeLock()) {
			ProgressionUtil.init(getProgression(), 0, 0, 100, false, false);
			Image img = generateImage(container, scale,
					ProgressionUtil.sub(getProgression(), 50));
	    	writeImage(stream, img,
	    			ProgressionUtil.sub(getProgression(), 50));
			ProgressionUtil.end(getProgression());
	    	return true;
		}
    }
	
	/** Generate the image.
	 * 
	 * @param container is the component to render.
	 * @param scale is the scaling factor to apply to the image.
	 * @param progression is the progression model to use.
	 * @return the generated image.
	 */
	protected Image generateImage(ViewComponentContainer<?,?> container, float scale, Progression progression) {
		ProgressionUtil.init(progression, 0, 3);
    	Rectangle2f dim = container.getComponentBounds();
    	int iw = (int)(Math.ceil(dim.getWidth())*scale);
    	int ih = (int)(Math.ceil(dim.getHeight())*scale);
    	Image image = VectorToolkit.image(iw, ih, this.type.isAlphaSupported());
    	ViewGraphics2D vg = ViewGraphicsUtil.createViewGraphics(image.getVectorGraphics(), true, true, Graphics2DLOD.HIGH_LEVEL_OF_DETAIL);

		ProgressionUtil.advance(progression);
    	if (!this.type.isAlphaSupported()) {
    		vg.setBackground(container.getBackgroundColor());
    		vg.clear(new Rectangle2f(0f, 0f, iw, ih));
    	}
    	vg.scale(scale, scale);
    	vg.translate(-dim.getMinX(), -dim.getMinY());
    	
		ProgressionUtil.advance(progression);
    	boolean isShadow = container.isShadowDrawn();
    	container.setShadowDrawn(isShadowExported());
    	try {
    		container.paintViewComponents(vg);
    	}
    	finally {
    		container.setShadowDrawn(isShadow);
    	}
    	vg.dispose();
		ProgressionUtil.end(progression);
    	return image;
	}
	
	/** Write the given image into the specified stream.
	 * 
	 * @param stream
	 * @param image
	 * @param progression
	 * @throws IOException
	 */
	protected void writeImage(OutputStream stream, Image image, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, 1);
		VectorToolkit.writeImage(image, this.type.getImageIOName(), stream);
		ProgressionUtil.end(progression);
	}

	@Override
	public boolean isShadowExported() {
		return this.isShadow;
	}

	@Override
	public void setShadowExported(boolean export) {
		this.isShadow = export;
	}

}
