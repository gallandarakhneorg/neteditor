/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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
package org.arakhne.neteditor.swing.actionmode.creation ;

import java.util.ArrayList;
import java.util.List;

import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.PathElement2f;
import org.arakhne.afc.math.continous.object2d.PathIterator2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Segment2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Stroke;
import org.arakhne.afc.ui.vector.Stroke.EndCap;
import org.arakhne.afc.ui.vector.Stroke.LineJoin;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.PolylineFigure;
import org.arakhne.neteditor.swing.graphics.SwingViewGraphics2D;

/** This class implements a Mode that permits to
 * create polylines.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PolylineDecorationCreationMode extends AbstractPolypointDecorationCreationMode {

	/** Construct a new PolylineDecorationCreationMode with the given parent.
	 *
	 * @param persistent indicates if the mode is persistent or not.
	 * @param modeManager a reference to the ModeManager that
	 *                    contains this Mode.
	 */
	public PolylineDecorationCreationMode(boolean persistent, ActionModeManager<Figure,SwingViewGraphics2D,Color> modeManager) { 
		super(persistent, modeManager);
	}

	/** Construct a new PolylineDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 *  
	 * @param persistent indicates if the mode is persistent or not.
	 */
	public PolylineDecorationCreationMode(boolean persistent) {
		super(persistent);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected DecorationFigure createFigure(Path2f path) {
		PolylineFigure figure = new PolylineFigure(getModeManager().getViewID());
		List<Point2f> points = new ArrayList<Point2f>();
		PathIterator2f iterator = path.getPathIterator();
		PathElement2f element;
		while (iterator.hasNext()) {
			element = iterator.next();
			switch(element.type) {
			case MOVE_TO:
			case LINE_TO:
				points.add(new Point2f(element.toX, element.toX));
				break;
			default:
				throw new IllegalStateException();
			}
			iterator.next();
		}
		figure.setCtrlPoints(points);
		figure.setFilled(false);
		figure.setFramed(true);
		figure.setClosed(false);
		return figure;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintShape(SwingViewGraphics2D g,
			Path2f path, Point2D candidate,
			Color borderColor, Color backgroundColor) {
		g.setColors(null, borderColor);
		g.draw(path);
		if (candidate!=null) {
			Stroke old = g.getStroke();
			g.setStroke(VectorToolkit.stroke(1f, LineJoin.ROUND, EndCap.ROUND,
					1, new float[]{5, 5}, 0));
			Point2D p0 = path.getCurrentPoint();
			g.draw(new Segment2f(p0.getX(), p0.getY(), candidate.getX(), candidate.getY()));
			g.setStroke(old);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintShape(SwingViewGraphics2D g, Point2D lastPoint,
			Point2D candidate, Color borderColor, Color backgroundColor) {
		assert(candidate!=null);
		assert(lastPoint!=null);
		g.setColors(null, borderColor);
		Stroke old = g.getStroke();
		g.setStroke(VectorToolkit.stroke(1f, LineJoin.ROUND, EndCap.ROUND,
				1, new float[]{5, 5}, 0));
		g.draw(new Segment2f(lastPoint.getX(), lastPoint.getY(), candidate.getX(), candidate.getY()));
		g.setStroke(old);
	}

}