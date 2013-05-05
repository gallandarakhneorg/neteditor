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

/** This class represents the "diamond" symbol that will
 *  be paint in the ends of edges.
 *  <p>
 *  <center>
 *  	<img src="doc-files/DiamondEdgeSymbol.png"><br>
 *  	<i><u>Fig:</u>&nbsp;Example of a DiamondEdgeSymbol, which is not filled</i>
 *  </center>
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DiamondEdgeSymbol extends AbstractFillableEdgeSymbol {

	private static final long serialVersionUID = -5581243771422101401L;
	
	/** This is the size of diamond branches.
	 */
	private float length;

	/** Construct a new diamond.
	 *
	 * @param length is the size of diamond when it was projected
	 * on the edge.
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public DiamondEdgeSymbol(float length, boolean filled) {
		super(filled);
		this.length = length;
	}

	/** Construct a new diamond.
	 */
	public DiamondEdgeSymbol() {
		this(10, true);
	}

	/** Construct a new diamond.
	 *
	 * @param length is the size of diamond when it was projected
	 * on the edge.
	 */
	public DiamondEdgeSymbol(float length) {
		this(length, true);
	}

	/** Construct a new diamond.
	 *
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public DiamondEdgeSymbol(boolean filled) {
		this(10, filled);
	}

	/** Reply the size of diamond when it was projected
	 * on the edge.
	 *
	 * @return the size of diamond when it was projected
	 * on the edge.
	 */
	protected float getProjectedSize() { 
		return this.length;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Shape2f getSymbolShape(float x, float y, float angle) {
		float l = getProjectedSize();
		float dl = l/2f;
		Path2f polygon = new Path2f();
		polygon.moveTo(x, y);
		polygon.lineTo(x+dl, y-dl);
		polygon.lineTo(x+l, y);
		polygon.lineTo(x+dl, y+dl);
		polygon.closePath();
		Transform2D t = new Transform2D();
		t.translate(x, y);
	    t.rotate(angle);
	    t.translate(-x, -y);
		polygon.createTransformedShape(t);
		return polygon;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_LENGTH, this.length); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Class<?>> getSupportedProperties() {
		Map<String,Class<?>> properties = super.getSupportedProperties();
		properties.put(PROPERTY_LENGTH, Float.class); 
		return properties;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) throws IOException {
		super.setProperties(properties);
		if (properties!=null) {
			this.length = propGetFloat(PROPERTY_LENGTH, this.length, properties); 
		}
	}

}
