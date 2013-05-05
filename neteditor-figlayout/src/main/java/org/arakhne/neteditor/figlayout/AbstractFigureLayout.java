/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND
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

package org.arakhne.neteditor.figlayout;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.vector.Margins;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;

/** Abstract implementation of a laying out algorithm.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractFigureLayout implements FigureLayout, FigureLayoutConstants, ViewComponentConstants {

	/** The distances between two figures.
	 */
	private final Margins insets = VectorToolkit.margins(
			HALF_MINIMAL_VERTICAL_PADDING,
			HALF_MINIMAL_HORIZONTAL_PADDING,
			HALF_MINIMAL_VERTICAL_PADDING,
			HALF_MINIMAL_HORIZONTAL_PADDING);
	
	private final Point2D origin = new Point2f();

	/**
	 */
	public AbstractFigureLayout() {
		//
	}
	
	/** Replies the external padding of each figure.
	 * The padding is the minimum amount of space between the figures
	 * and the borders of its display area. The value is specified as 
	 * an Insets object. By default, all nodes has no external padding.
	 * 
	 * @return the external padding.
	 */
	public Margins getMargins() {
		return this.insets;
	}

	/** Set the external padding of each figure.
	 * The padding is the minimum amount of space between the figures
	 * and the borders of its display area. The value is specified as 
	 * an Insets object. By default, all nodes has no external padding.
	 * 
	 * @param insets is the external padding.
	 */
	public void setMargins(Margins insets) {
		if (insets==null) {
			this.insets.set(
					HALF_MINIMAL_VERTICAL_PADDING,
					HALF_MINIMAL_HORIZONTAL_PADDING,
					HALF_MINIMAL_VERTICAL_PADDING,
					HALF_MINIMAL_HORIZONTAL_PADDING);
		}
		else {
			this.insets.set(
					Math.max(insets.top(), HALF_MINIMAL_VERTICAL_PADDING),
					Math.max(insets.left(), HALF_MINIMAL_HORIZONTAL_PADDING),
					Math.max(insets.bottom(), HALF_MINIMAL_VERTICAL_PADDING),
					Math.max(insets.right(), HALF_MINIMAL_HORIZONTAL_PADDING));
		}
	}

	/** Replies the coordinates of the origin.
	 * 
	 * @return the coordinates of the origin.
	 */
	public Point2D getOrigin() {
		return this.origin; 
	}

	/** Set the coordinates of the origin.
	 * 
	 * @param point is the coordinates of the origin.
	 */
	public void setOrigin(Point2D point) {
		this.origin.set(point); 
	}

	/** Set the coordinates of the origin.
	 * 
	 * @param x
	 * @param y
	 */
	public void setOrigin(float x, float y) {
		this.origin.set(x, y); 
	}
	
}
