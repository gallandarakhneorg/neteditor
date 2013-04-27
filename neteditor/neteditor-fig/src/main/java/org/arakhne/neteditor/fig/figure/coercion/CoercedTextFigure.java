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
package org.arakhne.neteditor.fig.figure.coercion ;

import java.util.Map;
import java.util.UUID;

import org.arakhne.neteditor.fig.figure.decoration.TextFigure;

/** Text Figure that is associated to an anchor
 * and located according to the anchor position.
 * The anchored figures may be used as associated
 * figures with edges for example.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CoercedTextFigure extends TextFigure implements CoercedFigure {

	private static final long serialVersionUID = -5796334447055459787L;
	
	private Object anchorDescription = null;
	private float anchorX;
	private float anchorY;
	private float dx = 0f;
	private float dy = 0f;

	private transient boolean update;

	/**
	 * @param viewId is the identifier of the view.
	 * @param text is the text to put in the object.
	 * @param anchorX is the coordinate of the point to be attahced to.
	 * @param anchorY is the coordinate of the point to be attahced to.
	 */
	public CoercedTextFigure(UUID viewId, String text, float anchorX, float anchorY) {
		super(viewId);
		this.update = false;
		setMinimalWidth(1);
		setMinimalHeight(1);
		setFilled(false);
		setFramed(false);
		setText(text);
		fitToContent();
		setLocationFromAnchorPoint(anchorX, anchorY);
		this.update = true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLockable() {
		// Avoid to individually lock a coerced figure
		return false;
	}

	/**
	 * @param viewId is the identifier of the view.
	 */
	public CoercedTextFigure(UUID viewId) {
		this(viewId, null, 0, 0);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setLocationFromAnchorPoint(float x, float y) {
		if (this.anchorX!=x || this.anchorY!=y) {
			float ox = this.anchorX;
			float oy = this.anchorY;
			this.anchorX = x;
			this.anchorY = y;
			this.update = false;
			try {
				setLocation(
						this.anchorX + this.dx - getWidth()/2f,
						this.anchorY + this.dy - getHeight()/2f);
			}
			finally {
				this.update = true;
				if (ox!=this.anchorX)
					firePropertyChange(PROPERTY_ANCHORX, ox, this.anchorX); 
				if (oy!=this.anchorY)
					firePropertyChange(PROPERTY_ANCHORY, oy, this.anchorY); 
			}
		}
	}

	private void updateLocalVector(double cx, double cy) {
		float ox = this.dx;
		float oy = this.dy;
		this.dx = (float)(cx - this.anchorX);
		this.dy = (float)(cy - this.anchorY);
		if (ox!=this.dx)
			firePropertyChange(PROPERTY_DX, ox, this.dx); 
		if (oy!=this.dy)
			firePropertyChange(PROPERTY_DY, oy, this.dy); 
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onSizeUpdated(
			float oldWidth, float newWidth,
			float oldHeight, float newHeight) {
		if (this.update) {
			updateLocalVector(
					getX() + newWidth/2f,
					getY() + newHeight/2f);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onPositionUpdated(float oldX, float newX, float oldY, float newY) {
		if (this.update) {
			updateLocalVector(
					newX + getWidth()/2f,
					newY + getHeight()/2f);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onBoundsUpdated(
			float oldX, float newX, float oldY, float newY,
			float oldWidth, float newWidth,
			float oldHeight, float newHeight) {
		if (this.update) {
			updateLocalVector(
					newX + newWidth/2f,
					newY + newHeight/2f);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAnchorDescriptor(Object descriptor) {
		if (this.anchorDescription!=descriptor) {
			Object old = this.anchorDescription;
			this.anchorDescription = descriptor;
			firePropertyChange(PROPERTY_ANCHORDESCRIPTION, old, this.anchorDescription); 
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getAnchorDescriptor() {
		return this.anchorDescription;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_DX, this.dx); 
		properties.put(PROPERTY_DY, this.dy); 
		properties.put(PROPERTY_ANCHORDESCRIPTION, this.anchorDescription); 
		properties.put(PROPERTY_ANCHORX, this.anchorX); 
		properties.put(PROPERTY_ANCHORY, this.anchorY); 
		return properties;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setAnchorDescriptor(propGet(Object.class, PROPERTY_ANCHORDESCRIPTION, null, false, properties)); 
			setD(
					propGetFloat(PROPERTY_DX, this.dx, properties), 
					propGetFloat(PROPERTY_DY, this.dy, properties)); 
			setLocationFromAnchorPoint(
					propGetFloat(PROPERTY_ANCHORX, this.anchorX, properties), 
					propGetFloat(PROPERTY_ANCHORY, this.anchorY, properties)); 
		}
	}
	
	private void setD(float x, float y) {
		if (this.dx!=x) {
			float o = this.dx;
			this.dx = x;
			firePropertyChange(PROPERTY_DX, o, this.dx); 
		}
		if (this.dy!=y) {
			float o = this.dy;
			this.dy = y;
			firePropertyChange(PROPERTY_DY, o, this.dy); 
		}
	}

}
