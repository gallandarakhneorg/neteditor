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
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** This class represents the "arrow" symbol that will
 *  be paint in the ends of edges.
 *  <p>
 *  <center>
 *  	<img src="doc-files/ArrowEdgeSymbol.png"><br>
 *  	<i><u>Fig:</u>&nbsp;Example of a ArrowEdgeSymbol</i>
 *  </center>
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ArrowEdgeSymbol extends EdgeSymbol {

	private static final long serialVersionUID = 8312461474430015270L;
	
	/** This is the size of arrow's branches.
	 */
	protected float length;

	/** Construct a new Arrow.
	 */
	public ArrowEdgeSymbol() {
		super();
		this.length = 10;
	}

	/** Construct a new Arrow.
	 *
	 * @param length is the size of arrow when it was projected
	 * on the edge.
	 */
	public ArrowEdgeSymbol(float length) {
		super();
		this.length = length;
	}

	/** Reply the size of arrow when it was projected
	 * on the edge.
	 *
	 * @return the size of arrow when it was projected
	 * on the edge.
	 */
	protected float getProjectedSize() { 
		return this.length;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected void paintSymbol(ViewGraphics2D g, Shape2f shape) {
		g.setInteriorPainted(false);
		g.setOutlineDrawn(true);
		g.draw(shape);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Shape2f getSymbolShape(float x, float y, float angle) {
		float l = getProjectedSize();
		float dl = l/2f;
		Path2f path = new Path2f();
		path.moveTo(x+l, y-dl);
		path.lineTo(x, y);
		path.lineTo(x+l, y+dl);
		Transform2D t = new Transform2D();
		t.translate(x, y);
	    t.rotate(angle);
	    t.translate(-x, -y);
		path.createTransformedShape(t);
		return path;
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
