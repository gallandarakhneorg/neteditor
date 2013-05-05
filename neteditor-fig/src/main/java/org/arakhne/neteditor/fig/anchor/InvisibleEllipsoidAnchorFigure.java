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
 * is drawn along a ellipsoid frame.
 *
 * @param <A> is the type of the model anchor associated to this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class InvisibleEllipsoidAnchorFigure<A extends Anchor<?,?,?,?>>
extends AbstractEllipsoidAnchorFigure<A> {

	private static final long serialVersionUID = 8290027498644284967L;

	/** Construct a new Anchor.
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
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 * @param width horizontal dimension of this Anchor within his {@link NodeFigure}.
	 * @param height vertical dimension of this Anchor within his {@link NodeFigure}.
	 */
	public InvisibleEllipsoidAnchorFigure(UUID viewUUID, float x, float y, float width, float height) { 
		super(viewUUID, x, y, width, height) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 */
	public InvisibleEllipsoidAnchorFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public InvisibleEllipsoidAnchorFigure(UUID viewUUID) {
		this(viewUUID, 0, 0, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE) ;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void paint(ViewGraphics2D g) {
		//
	}

}
