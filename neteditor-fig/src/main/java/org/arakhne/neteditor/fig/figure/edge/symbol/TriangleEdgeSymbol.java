/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.fig.figure.edge.symbol;

import java.io.IOException;
import java.util.Map;

import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.matrix.Transform2D;

/** This class represents the "triangle" symbol that will
 *  be paint in the ends of edges.
 *  <p>
 *  <center>
 *  	<img src="doc-files/TriangleEdgeSymbol1.png">&nbsp;
 *  	<img src="doc-files/TriangleEdgeSymbol2.png"><br>
 *  	<i><u>Fig:</u>&nbsp;Examples of a TriangleEdgeSymbol, not inverted on the left and
 *  		inverted on the right</i>
 *  </center>
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TriangleEdgeSymbol extends AbstractFillableEdgeSymbol {

	private static final long serialVersionUID = 5568307003483504322L;

	/** This is the size of triangle branches.
	 */
	private float length;
	
	/** Indicates if the triangle direction is inverted.
	 */
	private boolean invert;

	/** Construct a new triangle.
	 *
	 * @param length is the size of triangle when it was projected
	 * on the edge.
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 * @param invert indicates if the direction of triangle
	 * is inverted. If <code>true</code> the triangle base
	 * is located at the end of the edge and the apex is
	 * located on the edge line. If <code>false</code>
	 * the triangle base is located on the edge line and
	 * the apex at the edge end.
	 */
	public TriangleEdgeSymbol(float length, boolean filled, boolean invert) {
		super(filled);
		this.length = length;
		this.invert = invert;
	}

	/** Construct a new triangle.
	 */
	public TriangleEdgeSymbol() {
		this(10, true, false);
	}

	/** Construct a new triangle.
	 *
	 * @param length is the size of triangle when it was projected
	 * on the edge.
	 */
	public TriangleEdgeSymbol(float length) {
		this(length, true, false);
	}

	/** Construct a new triangle.
	 *
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public TriangleEdgeSymbol(boolean filled) {
		this(10, filled, false);
	}

	/** Construct a new triangle.
	 *
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 * @param invert indicates if the direction of triangle
	 * is inverted. If <code>true</code> the triangle base
	 * is located at the end of the edge and the apex is
	 * located on the edge line. If <code>false</code>
	 * the triangle base is located on the edge line and
	 * the apex at the edge end.
	 */
	public TriangleEdgeSymbol(boolean filled, boolean invert) {
		this(10, filled, invert);
	}

	/** Reply the size of triangle when it was projected
	 * on the edge.
	 *
	 * @return the size of triangle when it was projected
	 * on the edge.
	 */
	protected float getProjectedSize() { 
		return this.length;
	}
	
	/** Replies if the direction of the triangle is inverted.
	 * 
	 * @return <code>true</code> if the triangle base
	 * is located at the end of the edge and the apex is
	 * located on the edge line. Returns <code>false</code>
	 * if the triangle base is located on the edge line and
	 * the apex at the edge end.
	 */
	public boolean isDirectionInverted() {
		return this.invert;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Shape2f getSymbolShape(float x, float y, float angle) {
		float l = getProjectedSize();
		float dl = l/2f;
		Path2f polygon = new Path2f();
		if (isDirectionInverted()) {
			polygon.moveTo(x, y-dl);
			polygon.lineTo(x, y+dl);
			polygon.lineTo(x+l, y);
		}
		else {
			polygon.moveTo(x, y);
			polygon.lineTo(x+l, y-dl);
			polygon.lineTo(x+l, y+dl);
		}
		polygon.closePath();
		Transform2D t = new Transform2D();
		t.translate(x, y);
	    t.rotate(angle);
	    t.translate(-x, -y);
		return polygon.createTransformedShape(t);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_LENGTH, this.length); 
		properties.put(PROPERTY_INVERT, this.invert); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Class<?>> getSupportedProperties() {
		Map<String,Class<?>> properties = super.getSupportedProperties();
		properties.put("width", Float.class); //$NON-NLS-1$
		properties.put(PROPERTY_INVERT, Boolean.class); 
		return properties;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) throws IOException {
		super.setProperties(properties);
		if (properties!=null) {
			this.length = propGetFloat(PROPERTY_LENGTH, this.length, properties); 
			this.invert = propGetBoolean(PROPERTY_INVERT, this.invert, properties); 
		}
	}

}
