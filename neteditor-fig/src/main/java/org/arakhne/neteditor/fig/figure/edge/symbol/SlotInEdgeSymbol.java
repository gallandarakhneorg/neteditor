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
package org.arakhne.neteditor.fig.figure.edge.symbol;

import java.io.IOException;
import java.util.Map;

import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;

/** This class represents the "slot-in" symbols that will
 *  be paint in the ends of edges.
 *  <p>
 *  <center>
 *  	<img src="doc-files/SlotInEdgeSymbol.png"><br>
 *  	<i><u>Fig:</u>&nbsp;Example of a SlotInEdgeSymbol, which is not filled</i>
 *  </center>
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SlotInEdgeSymbol extends AbstractFillableEdgeSymbol {

	private static final long serialVersionUID = 4087089993629404684L;

	/** This is the size of the rectangle perpendicularly
	 * to the edge vector.
	 */
	private float width;

	/** This is the size of the rectangle along
	 * the edge vector.
	 */
	private float height;

	/** Construct a new slot-in.
	 *
	 * @param width is the size of the rectangle perpendicularly to the edge vector.
	 * @param height is the size of the rectangle along the edge vector.
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public SlotInEdgeSymbol(float width, float height, boolean filled) {
		super(filled);
		this.width = width;
		this.height = height;
	}

	/** Construct a new slot-in.
	 */
	public SlotInEdgeSymbol() {
		this(5, 5, true);
	}

	/** Construct a new slot-in.
	 *
	 * @param width is the size of the rectangle perpendicularly to the edge vector.
	 * @param height is the size of the rectangle along the edge vector.
	 */
	public SlotInEdgeSymbol(float width, float height) {
		this(width, height, true);
	}

	/** Construct a new slot-in.
	 *
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public SlotInEdgeSymbol(boolean filled) {
		this(5, 5, filled);
	}

	/** Reply the size of the slot-in perpendicularly to the
	 * ege vector.
	 *
	 * @return the width of the slot-in.
	 */
	public float getWidth() {
		return this.width;
	}
	
	/** Reply the size of the slot-in along the
	 * ege vector.
	 *
	 * @return the height of the slot-in.
	 */
	public float getHeight() {
		return this.height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Shape2f getSymbolShape(float x, float y, float angle) {
		float w = getWidth();
		float dw = w-w/2f;
		float h = getHeight();
		float dh = h/2f;
		Path2f rectangle = new Path2f();
		rectangle.moveTo(x, y-dh);
		rectangle.lineTo(x+w, y-dh);
		rectangle.lineTo(x+dw, y);
		rectangle.lineTo(x+w, y+dh);
		rectangle.lineTo(x, y+dh);
		rectangle.closePath();
		Transform2D t = new Transform2D();
		t.translate(x, y);
	    t.rotate(angle);
	    t.translate(-x, -y);
		rectangle.createTransformedShape(t);
		return rectangle;
		
	}

	/** {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_WIDTH, this.width); 
		properties.put(PROPERTY_HEIGHT, this.height); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Class<?>> getSupportedProperties() {
		Map<String,Class<?>> properties = super.getSupportedProperties();
		properties.put(PROPERTY_WIDTH, Float.class); 
		properties.put(PROPERTY_HEIGHT, Float.class); 
		return properties;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) throws IOException {
		super.setProperties(properties);
		if (properties!=null) {
			this.width = propGetFloat(PROPERTY_WIDTH, this.width, properties); 
			this.height = propGetFloat(PROPERTY_HEIGHT, this.height, properties); 
		}
	}

}
