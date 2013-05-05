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

import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.matrix.Transform2D;

/** This class represents the "circle" symbols that will
 *  be paint in the ends of edges.
 *  <p>
 *  <center>
 *  	<img src="doc-files/CircleEdgeSymbol.png"><br>
 *  	<i><u>Fig:</u>&nbsp;Example of a CircleEdgeSymbol, which is not filled</i>
 *  </center>
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class CircleEdgeSymbol extends AbstractFillableEdgeSymbol {

	private static final long serialVersionUID = -6205572453480237197L;
	
	/** This is the radius of the circle.
	 */
	private float radius;

	/** Construct a new circle.
	 *
	 * @param radius the radius if the circle.
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public CircleEdgeSymbol(float radius, boolean filled) {
		super(filled);
		this.radius = radius;
	}

	/** Construct a new circle.
	 */
	public CircleEdgeSymbol() {
		this(5, true);
	}

	/** Construct a new circle.
	 *
	 * @param radius the radius if the circle.
	 */
	public CircleEdgeSymbol(float radius) {
		this(radius, true);
	}

	/** Construct a new circle.
	 *
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public CircleEdgeSymbol(boolean filled) {
		this(5, filled);
	}

	/** Reply the radius of the circle.
	 *
	 * @return an integer that is the radius of the circle.
	 */
	public float getRadius() {
		return this.radius;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hit(float x, float y, float epsilon) {
		return (x*x+y*y)<=(epsilon*epsilon);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Shape2f getSymbolShape(float x, float y, float angle) {
		float s = 2f * this.radius;
		Transform2D t = new Transform2D();
		t.translate(x, y);
	    t.rotate(angle);
	    t.translate(-x, -y);
		Point2D center = new Point2f(x + this.radius, y);
		t.transform(center, center);
		return new Ellipse2f(
				center.getX() - this.radius,
				center.getY() - this.radius, s, s);
	}

	/** {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_RADIUS, this.radius); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Class<?>> getSupportedProperties() {
		Map<String,Class<?>> properties = super.getSupportedProperties();
		properties.put(PROPERTY_RADIUS, Float.class); 
		return properties;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) throws IOException {
		super.setProperties(properties);
		if (properties!=null) {
			this.radius = propGetFloat(PROPERTY_RADIUS, this.radius, properties); 
		}
	}

}
