/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.fsm.figures ;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Ellipse2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.figure.node.CircleNodeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fsm.constructs.FSMAnchor;
import org.arakhne.neteditor.fsm.constructs.FSMStartPoint;

/** Figure for the start points of the FSM.
 *  
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMStartPointFigure extends CircleNodeFigure<FSMStartPoint,FSMAnchor> {

	private static final long serialVersionUID = -4859773935364726017L;

	/**
	 * @param viewId
	 * @param x
	 * @param y
	 */
	public FSMStartPointFigure(UUID viewId, float x, float y) {
		super(viewId, x, y);
		setResizeDirections();
		setMinimalDimension(20, 20);
		setMaximalDimension(20, 20);
	}
	
	/**
	 * @param viewId
	 */
	public FSMStartPointFigure(UUID viewId) {
		this(viewId, 0, 0);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintNode(ViewGraphics2D g) {
		Rectangle2f figureBounds = g.getCurrentViewComponentBounds();
		Ellipse2f oval = new Ellipse2f(
				figureBounds.getMinX(),
				figureBounds.getMinY(),
				figureBounds.getWidth(),
				figureBounds.getHeight());
		g.setOutlineDrawn(false);
		g.setInteriorPainted(true);
		Color old = g.setFillColor(getLineColor());
		g.draw(oval);
		g.setFillColor(old);
	}
	
}
