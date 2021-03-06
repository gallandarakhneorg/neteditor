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
package org.arakhne.neteditor.fig.anchor;

import java.util.UUID;

import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;

/** This figure is for an anchor that is not drawn in its
 * inactive state, and the activated side of the anchor
 * is drawn along a square frame.
 *
 * @param <A> is the type of the model anchor associated to this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class InvisibleSquareAnchorFigure<A extends Anchor<?,?,?,?>>
extends AbstractRectangularAnchorFigure<A> {

	private static final long serialVersionUID = -3451655934118279974L;

	/** Construct a new Anchor.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified size and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified size and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified size and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified size and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 * @param size if the size of the borders of the square.
	 */
	public InvisibleSquareAnchorFigure(UUID viewUUID, float x, float y, float size) { 
		super(viewUUID, x, y, size, size) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 */
	public InvisibleSquareAnchorFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public InvisibleSquareAnchorFigure(UUID viewUUID) {
		this(viewUUID, 0, 0, DEFAULT_MINIMAL_SIZE) ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void paint(ViewGraphics2D g) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinimalDimension(float width, float height) {
		float s = Math.min(width, height);
		super.setMinimalDimension(s, s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaximalDimension(float width, float height) {
		float s = Math.max(width, height);
		super.setMaximalDimension(s, s);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		super.setHeight(width);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		super.setWidth(height);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBounds(float x, float y, float width, float height) {
		float s = Math.max(width, height);
		super.setBounds(x, y, s, s);
	}

}
