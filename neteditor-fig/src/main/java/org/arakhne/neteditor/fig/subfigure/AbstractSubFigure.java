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
package org.arakhne.neteditor.fig.subfigure;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.AbstractViewComponent;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;

/** This class is the base class all the figures that are linked
 * to other figures such as the anchors inside the nodes.  
 * 
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractSubFigure extends AbstractViewComponent implements SubFigure {

	private static final long serialVersionUID = 8900490705847946093L;

	/** Construct a new view component.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Fig.
	 * @param y vertical position of this Fig.
	 * @param width width of this Fig.
	 * @param height height of this Fig.
	 */
	public AbstractSubFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height);
		setMinimalDimension(DEFAULT_MINIMAL_SUBFIGURE_SIZE, DEFAULT_MINIMAL_SUBFIGURE_SIZE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle2f computeDamagedBounds() {
		float h = ViewComponentConstants.DEFAULT_DAMAGING_EXTENTS;
		return new Rectangle2f(
				getAbsoluteX() - h - 1,
				getAbsoluteY() - h - 1,
				getWidth() + 2f*h + 2,
				getHeight() + 2f*h + 2);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getAbsoluteX() {
		Figure parent = getParent();
		float x = getX();
		if (parent!=null) {
			return x + parent.getX();
		}
		return x;
	}

	/** {@inheritDoc}
	 */
	@Override
	public float getAbsoluteY() {
		Figure parent = getParent();
		float y = getY();
		if (parent!=null) {
			return y + parent.getY();
		}
		return y;
	}
	
}
