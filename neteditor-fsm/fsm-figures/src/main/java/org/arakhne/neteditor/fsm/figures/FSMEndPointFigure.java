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
package org.arakhne.neteditor.fsm.figures ;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.figure.node.CircleNodeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fsm.constructs.FSMAnchor;
import org.arakhne.neteditor.fsm.constructs.FSMEndPoint;

/** Figure for the end points of the FSM.
 *  
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMEndPointFigure extends CircleNodeFigure<FSMEndPoint,FSMAnchor> {

	private static final long serialVersionUID = 8369128131808614179L;

	/**
	 * @param viewId
	 * @param x
	 * @param y
	 */
	public FSMEndPointFigure(UUID viewId, float x, float y) {
		super(viewId, x, y);
		setResizeDirections();
		setMinimalDimension(20, 20);
		setMaximalDimension(20, 20);
	}
	
	/**
	 * @param viewId
	 */
	public FSMEndPointFigure(UUID viewId) {
		this(viewId, 0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintNode(ViewGraphics2D g) {
		g.beginGroup();
		super.paintNode(g);
		Rectangle2f figureBounds = g.getCurrentViewComponentBounds();
		Ellipse2f oval = new Ellipse2f(
				figureBounds.getMinX() + 5,
				figureBounds.getMinY() + 5,
				figureBounds.getWidth() - 10,
				figureBounds.getHeight() - 10);
		g.setInteriorPainted(true);
		g.setOutlineDrawn(false);
		Color old = g.setFillColor(g.getOutlineColor());
		g.draw(oval);
		g.setFillColor(old);
		g.endGroup();
	}

}
