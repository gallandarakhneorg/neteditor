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

import java.awt.Color;
import java.awt.geom.GeneralPath;
import java.awt.geom.Path2D;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.awt.AwtUtil;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.event.KeyEvent;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;

/** This class implements a Mode that permits to
 * create decorations based on a collection of points.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractPolypointDecorationCreationMode extends ActionMode<Figure,VirtualScreenGraphics2D,java.awt.Color> {

        private GeneralPath points = null;
        private Point2D candidate = null;
        private final Point2D hit = new Point2f();

        /** Construct a new AbstractDecorationCreationMode with the given parent.
         *
         * @param persistent indicates if the mode is persistent or not.
         * @param modeManager a reference to the ModeManager that
         *                    contains this Mode.
         */
        public AbstractPolypointDecorationCreationMode(boolean persistent, ActionModeManager<Figure,VirtualScreenGraphics2D,java.awt.Color> modeManager) { 
                super(modeManager);
                setPersistent(persistent);
        }

        /** Construct a new AbstractDecorationCreationMode. The 
         *  {@code ActionModeManager} should be
         *  set before using this object.
         *  
         * @param persistent indicates if the mode is persistent or not.
         */
        public AbstractPolypointDecorationCreationMode(boolean persistent) {
                setPersistent(persistent);
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void cleanMode() {
                setExclusive(false);
                this.points = null;
                this.candidate = null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void onModeActivated() {
                setExclusive(true);
                requestFocus();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void keyTyped(KeyEvent e) {
                if (java.awt.event.KeyEvent.VK_ESCAPE==e.getKeyChar()) {
                        cleanMode();
                        setCursor(null);
                        repaint();
                        done();
                        e.consume();
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerPressed(ActionPointerEvent event) {
                if (event.getButton()==1) {
                        if (isPointerInFigureShape()) {
                                done();
                        }
                        else {
                                this.candidate = event.getPosition();
                                this.hit.set(this.candidate);
                                repaint();
                        }
                        event.consume();
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerDragged(ActionPointerEvent event) {
                if (this.candidate!=null) {
                        this.candidate.set(event.getPosition());
                        repaint();
                }
                event.consume();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerReleased(ActionPointerEvent event) {
                if (event.getButton()==1) {
                        if (event.getClickCount()==2) {
                                DecorationFigure figure = createFigure(this.points);
                                if (figure!=null) {
                                        Undoable undo = getModeManagerOwner().addFigure(figure);
                                        getModeManagerOwner().getUndoManager().add(undo);
                                }
                                if (isPersistent()) cleanMode();
                                else done();
                        }
                        else if (this.candidate!=null) {
                                if (this.points==null) {
                                        this.points = new GeneralPath();
                                        if (this.candidate.equals(this.points)) {
                                                this.points.moveTo(event.getX(), event.getY());
                                        }
                                        else {
                                                this.points.moveTo(this.hit.getX(), this.hit.getY());
                                                this.points.lineTo(event.getX(), event.getY());
                                        }
                                }
                                else {
                                        this.points.lineTo(event.getX(), event.getY());
                                }
                                this.candidate = null;
                                repaint();
                        }
                        else if (isPersistent()) cleanMode();
                        else done();
                        event.consume();
                }
        }

        /** Invoked to create a new figure.
         * 
         * @param path is the path to follow.
         * @return the new figure.
         */
        protected abstract DecorationFigure createFigure(Path2D path);

        /**
         * {@inheritDoc}
         */
        @Override
        public void paint(VirtualScreenGraphics2D g) {
                if (this.points!=null) {
                        Color border = getModeManagerOwner().getSelectionBackground();
                        Color background = AwtUtil.makeTransparentColor(border);
                        paintShape(g, this.points, this.candidate, border, background);
                }
                else if (this.candidate!=null) {
                        Color border = getModeManagerOwner().getSelectionBackground();
                        Color background = AwtUtil.makeTransparentColor(border);
                        paintShape(g, this.hit, this.candidate, border, background);
                }
        }
        
        /** Paint the shape that will be created.
         * 
         * @param g
         * @param path is the current built path
         * @param candidate is the next point that may be added in the path.
         * @param borderColor
         * @param backgroundColor
         */
        protected abstract void paintShape(VirtualScreenGraphics2D g, Path2D path, Point2D candidate, Color borderColor, Color backgroundColor);

        /** Paint the shape that will be created.
         * 
         * @param g
         * @param lastPoint is the last point in the path.
         * @param candidate is the next point that may be added in the path.
         * @param borderColor
         * @param backgroundColor
         */
        protected abstract void paintShape(VirtualScreenGraphics2D g, Point2D lastPoint, Point2D candidate, Color borderColor, Color backgroundColor);

}