/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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
package org.arakhne.neteditor.swing.actionmode.creation ;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.PolylineFigure;

/** This class implements a Mode that permits to
 * create polygons.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PolygonDecorationCreationMode extends AbstractPolypointDecorationCreationMode {

        /** Construct a new PolygonDecorationCreationMode with the given parent.
         *
         * @param persistent indicates if the mode is persistent or not.
         * @param modeManager a reference to the ModeManager that
         *                    contains this Mode.
         */
        public PolygonDecorationCreationMode(boolean persistent, ActionModeManager<Figure,VirtualScreenGraphics2D,java.awt.Color> modeManager) { 
                super(persistent, modeManager);
        }

        /** Construct a new PolygonDecorationCreationMode. The 
         *  {@code ActionModeManager} should be
         *  set before using this object.
         *  
         * @param persistent indicates if the mode is persistent or not.
         */
        public PolygonDecorationCreationMode(boolean persistent) {
                super(persistent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected DecorationFigure createFigure(Path2D path) {
                PolylineFigure figure = new PolylineFigure(getModeManager().getViewID());
                List<Point2f> points = new ArrayList<Point2f>();
                float[] coords = new float[6];
                PathIterator iterator = path.getPathIterator(new AffineTransform());
                while (!iterator.isDone()) {
                        switch(iterator.currentSegment(coords)) {
                        case PathIterator.SEG_MOVETO:
                        case PathIterator.SEG_LINETO:
                                points.add(new Point2f(coords[0], coords[1]));
                                break;
                        default:
                                throw new IllegalStateException();
                        }
                        iterator.next();
                }
                figure.setCtrlPoints(points);
                figure.setFilled(true);
                figure.setFramed(true);
                figure.setClosed(true);
                return figure;
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected void paintShape(VirtualScreenGraphics2D g,
                        Path2D path, Point2D candidate,
                        Color borderColor, Color backgroundColor) {
                g.setColor(borderColor);
                g.draw(path);
                if (candidate!=null) {
                        Stroke old = g.getStroke();
                        g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                        1, new float[]{5, 5}, 0));
                        java.awt.geom.Point2D p0 = path.getCurrentPoint();
                        g.drawLine((float)p0.getX(), (float)p0.getY(), candidate.getX(), candidate.getY());
                        g.setStroke(old);
                }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected void paintShape(VirtualScreenGraphics2D g, Point2D lastPoint,
                        Point2D candidate, Color borderColor, Color backgroundColor) {
                assert(candidate!=null);
                assert(lastPoint!=null);
                g.setColor(borderColor);
                Stroke old = g.getStroke();
                g.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                1, new float[]{5, 5}, 0));
                g.drawLine(lastPoint.getX(), lastPoint.getY(), candidate.getX(), candidate.getY());
                g.setStroke(old);
        }

}