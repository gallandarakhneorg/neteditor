/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.fig.figure.edge ;

import java.util.List;
import java.util.UUID;

import org.arakhne.afc.math.MathUtil;
import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Vector2D;
import org.arakhne.neteditor.formalism.Edge;

/** Edge that is drawn with a single segment.
 *
 * @param <E> is the type of the model edge supported by this figure.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SegmentEdgeFigure<E extends Edge<?,?,?,?>> extends EdgeFigure<E> {

	private static final long serialVersionUID = -5915583240483052005L;

	/** Contruct a new SegmentEdgeFigure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param xfrom horizontal position of the begin point for this FigEdge.
	 * @param yfrom vertical position of the begin point for this FigEdge.
	 * @param xto horizontal position of the end point for this FigEdge.
	 * @param yto vertical position of the end point for this FigEdge.
	 */
	public SegmentEdgeFigure(UUID viewUUID, float xfrom, float yfrom, float xto, float yto) { 
		super(viewUUID, xfrom, yfrom, xto, yto) ;
	}

	/** Contruct a new SegmentEdgeFigure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public SegmentEdgeFigure(UUID viewUUID) { 
		this(viewUUID, 0, 0, 0, 0) ;
	}
	
	@Override
	public void fitToContent() {
		//
	}

	/** {@inheritDoc}
	 */
	@Override
	public int getMaxAllowedCtrlPoints() {
		return 2 ;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected PathDetails computePath(List<? extends Point2D> controlPoints) {
		Path2f path = new Path2f();
		Vector2D startTangent = new Vector2f();
		Vector2D endTangent = new Vector2f();
		if (controlPoints.size()>=2) {
			Point2D p1 = controlPoints.get(0);
			Point2D p2 = controlPoints.get(controlPoints.size()-1);
			path.moveTo(p1.getX(), p1.getY());
			path.lineTo(p2.getX(), p2.getY());
			startTangent.sub(p2, p1);
			startTangent.normalize();
			endTangent.sub(p1, p2);
			endTangent.normalize();
		}
		return new PathDetails(
				path,
				startTangent,
				endTangent);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final boolean flatteningAt(int index) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected boolean isClosedToSegment(float x1, float y1, float x2, float y2,
			float x, float y, float hitDistance) {
		return MathUtil.isPointClosedToSegment(x1, y1, x2, y2, x, y, hitDistance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected float distanceToSegment(float x1, float y1, float x2, float y2,
			float x, float y, Point2D pts) {
		return MathUtil.distancePointToSegment(x, y, x1, y1, x2, y2, pts);
	}

}
