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
package org.arakhne.neteditor.fig.figure.decoration;

import org.arakhne.afc.math.continous.object2d.UnmodifiablePoint2f;
import org.arakhne.afc.math.generic.Point2D;

/** This is the implementation of a control point.
 * It is a Point2D that can be modified only from a linear feature.
 *
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ControlPoint extends UnmodifiablePoint2f {

	private static final long serialVersionUID = -1732426829237935328L;

	/**
	 */
	public ControlPoint() {
		super();
	}

	/**
	 * @param x
	 * @param y
	 */
	public ControlPoint(float x, float y) {
		super(x,y);
	}

	/**
	 * @param p
	 */
	public ControlPoint(Point2D p) {
		super(p.getX(),p.getY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ControlPoint clone() {
		return (ControlPoint)super.clone();
	}

	/** Set the coordinates.
	 * 
	 * @param x
	 * @param y
	 */
	void setCtrlPoint(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	/** Set the coordinates.
	 * 
	 * @param p
	 */
	void setCtrlPoint(Point2D p) {
		this.x = p.getX();
		this.y = p.getY();
	}

	/** Set the coordinates.
	 * 
	 * @param x
	 */
	void setCtrlPointX(float x) {
		this.x = x;
	}

	/** Set the coordinates.
	 * 
	 * @param y
	 */
	void setCtrlPointY(float y) {
		this.y = y;
	}

	/** Translate the point.
	 * 
	 * @param dx
	 * @param dy
	 */
	void translateCtrlPoint(float dx, float dy) {
		this.x += dx;
		this.y += dy;
	}

}
