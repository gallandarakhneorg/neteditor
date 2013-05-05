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

import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** This class represents a edge symbol that is able
 * to fill its interior part.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractFillableEdgeSymbol extends EdgeSymbol {

	private static final long serialVersionUID = -2126493322237125528L;
	
	/** Indicates if the symbol is filled with the 
	 * filling color replied by {@link #getFillingColor()} or not.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	private boolean isFilled; 

	/** Construct a new triangle.
	 *
	 * @param filled indicates how the symbol is filled.
	 * If <code>true</code> the color replied by
	 * {@link #getFillingColor()} is used.
	 * If <code>false</code> the color replied by
	 * {@link #getLineColor()} is used.
	 */
	public AbstractFillableEdgeSymbol(boolean filled) {
		super();
		this.isFilled = filled;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected void paintSymbol(ViewGraphics2D g, Shape2f shape) {
		Color c;
		if (this.isFilled) {
			c = g.getOutlineColor();
		}
		else {
			c = g.getFillColor();
		}
		g.setInteriorPainted(true);
		g.setOutlineDrawn(true);
	  	Color old = g.setFillColor(c);
	  	g.draw(shape);
	  	g.setFillColor(old);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_FILLED, this.isFilled); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Class<?>> getSupportedProperties() {
		Map<String,Class<?>> properties = super.getSupportedProperties();
		properties.put(PROPERTY_FILLED, Boolean.class); 
		return properties;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) throws IOException {
		super.setProperties(properties);
		if (properties!=null) {
			this.isFilled = propGetBoolean(PROPERTY_FILLED, this.isFilled, properties); 
		}
	}

}
