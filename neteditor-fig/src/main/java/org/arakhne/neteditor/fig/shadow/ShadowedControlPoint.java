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
package org.arakhne.neteditor.fig.shadow;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.UnmodifiablePoint2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Tuple2D;

/** This is the implementation of a control point.
 * It is a Point2D that can be modified only from a linear feature.
 *
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ShadowedControlPoint extends UnmodifiablePoint2f {

	private static final long serialVersionUID = -2766441369762844952L;
	
	private final float originalX;
	private final float originalY;
	
	/**
	 * @param x
	 * @param y
	 */
	public ShadowedControlPoint(float x, float y) {
		super(x, y);
		this.originalX = x;
		this.originalY = y;
	}
	
	/**
	 * @param p
	 */
	public ShadowedControlPoint(Point2D p) {
		this(p.getX(),p.getY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ShadowedControlPoint clone() {
		return (ShadowedControlPoint)super.clone();
	}
	
	/** Set this point with the coordinates translated
	 * by the speicified amount from the original coordinates.
	 * 
	 * @param dx
	 * @param dy
	 */
	public void translateFromOrigin(float dx, float dy) {
		set(this.originalX + dx, this.originalY + dy);
	}
	
	/** Replies the original position for the control point.
	 * 
	 * @return the original position for the control point.
	 */
	public Point2D getOriginalPoint() {
		return new Point2f(this.originalX, this.originalY);
	}

	@Override
	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void set(float[] t) {
		this.x = t[0];
		this.y = t[0];
	}
	
	@Override
	public void set(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public void set(int[] t) {
		this.x = t[0];
		this.y = t[1];
	}
	
	@Override
	public void set(Tuple2D<?> t1) {
		this.x = t1.getX();
		this.y = t1.getY();
	}
	
	@Override
	public void setX(float x) {
		this.x = x;
	}
	
	@Override
	public void setX(int x) {
		this.x = x;
	}
	
	@Override
	public void setY(float y) {
		this.y = y;
	}
	
	@Override
	public void setY(int y) {
		this.y = y;
	}
	

}
