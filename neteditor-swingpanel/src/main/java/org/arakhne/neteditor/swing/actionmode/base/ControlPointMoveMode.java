/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.swing.actionmode.base ;

import javax.swing.undo.UndoableEdit;

import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.shadow.LinearFeatureShadowPainter;
import org.arakhne.neteditor.fig.view.LinearFeature;
import org.arakhne.neteditor.swing.graphics.TransparentViewGraphics2D;

/** This class implements a Mode that move the control
 * points of an edge.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class ControlPointMoveMode extends ActionMode<Figure,VirtualScreenGraphics2D,java.awt.Color> {

        private int movedCtrlPoint = -1;
        private Point2D hitPosition = null;
        private LinearFeatureShadowPainter shadowPainter = null;
        
        private Undoable undo;
        
        /** Construct a new ControlPointMoveMode. The 
         *  {@code ActionModeManager} should be
         *  set before using this object.
         *  
         *  @param undo
         */
        public ControlPointMoveMode(Undoable undo) {
                this.undo = undo;
        }
        
        /** {@inheritDoc}
         */
        @Override
        public void paint(VirtualScreenGraphics2D g) {
                if (this.shadowPainter!=null) {
                        TransparentViewGraphics2D sg = new TransparentViewGraphics2D(g);
                        this.shadowPainter.paint(sg);
                }
        }

        /** {@inheritDoc}
         */
        @Override
        public void cleanMode() { 
                setExclusive( false );
                this.hitPosition = null;
                if (this.shadowPainter!=null) {
                        this.shadowPainter.release();
                        this.shadowPainter = null;
                }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerPressed(ActionPointerEvent event) {
                if (event.getButton()==1) {
                        MouseCursor c = null;
                        Figure figure = getPointedFigure();
                        if (figure instanceof LinearFeature
                                && figure.isResizable() && !figure.isLocked()) {
                                LinearFeature linearFeature = (LinearFeature)figure;
                                
                                this.hitPosition = event.getPosition();
                                
                                this.movedCtrlPoint = linearFeature.hitCtrlPoint(
                                                this.hitPosition.getX(),
                                                this.hitPosition.getY(),
                                                getClickPrecision());
                                
                                if (this.movedCtrlPoint>=0
                                        && this.movedCtrlPoint<linearFeature.getCtrlPointCount()) {
                                        this.shadowPainter = linearFeature.getShadowPainter();
                                        c = MouseCursor.MOVE;
                                }
                                else {
                                        done();
                                }
                        }
                        else {
                                done();
                        }
                        setCursor(c);
                        event.consume();
                }
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerDragged(ActionPointerEvent event) {
                if (this.shadowPainter!=null) {
                        Point2D currentPosition = event.getPosition();
                        float dx = currentPosition.getX() - this.hitPosition.getX();
                        float dy = currentPosition.getY() - this.hitPosition.getY();
                        this.shadowPainter.moveControlPointTo(this.movedCtrlPoint, dx, dy);
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
                        if (this.shadowPainter!=null) {
                                Point2D currentPosition = event.getPosition();
                                float dx = currentPosition.getX() - this.hitPosition.getX();
                                float dy = currentPosition.getY() - this.hitPosition.getY();
                                if (dx!=0f || dy!=0f) {
                                        Figure figure = this.shadowPainter.getFigure();
                                        if (figure instanceof LinearFeature) {
                                                LinearFeature linearFeature = (LinearFeature)figure;
                                                Undo undoCmd = new Undo(linearFeature, this.movedCtrlPoint, dx, dy);
                                                undoCmd.doEdit();
                                                if (this.undo!=null) {
                                                        getModeManagerOwner().getUndoManager().add(this.undo);
                                                        this.undo = null;
                                                }
                                                getModeManagerOwner().getUndoManager().add(undoCmd);
                                        }
                                }
                                this.shadowPainter.release();
                                this.shadowPainter = null;
                                repaint();
                        }
                        if (isPersistent()) cleanMode();
                        else done();
                        event.consume();
                }
        }

        /**
         * @author $Author: galland$
         * @version $FullVersion$
         * @mavengroupid $GroupId$
         * @mavenartifactid $ArtifactId$
         */
        private static class Undo extends AbstractCallableUndoableEdit {

                private static final long serialVersionUID = 6650491264977712680L;
                
                private final LinearFeature figure;
                private final int index;
                private float dx;
                private float dy;
                private boolean pointRemoved;
                private float x;
                private float y;
                
                /**
                 * @param figure
                 * @param index
                 * @param dx
                 * @param dy
                 */
                public Undo(LinearFeature figure, int index, float dx, float dy) {
                        this.figure = figure;
                        this.index = index;
                        this.dx = dx;
                        this.dy = dy;
                }

                @Override
                public boolean replaceEdit(UndoableEdit anEdit) {
                        if (anEdit instanceof Undo) {
                                Undo u = (Undo)anEdit;
                                if (this.figure==u.figure && this.index==u.index) {
                                        this.dx += u.dx;
                                        this.dy += u.dy;
                                        return true;
                                }
                        }
                        return false;
                }       
                
                @Override
                public void doEdit() {
                        org.arakhne.afc.math.generic.Point2D p = this.figure.getCtrlPointAt(this.index);
                        this.x = p.getX();
                        this.y = p.getY();
                        this.figure.setCtrlPointAt(
                                        this.index,
                                        this.x + this.dx,
                                        this.y + this.dy);
                        // Try to remove this point if it does not contribute to the shape of the edge
                        this.pointRemoved = this.figure.flatteningAt(this.index);
                }
                
                @Override
                public void undoEdit() {
                        if (this.pointRemoved) {
                                this.figure.insertCtrlPointAt(this.index, this.x, this.y);
                        }
                        else {
                                this.figure.setCtrlPointAt(this.index, this.x, this.y);
                        }
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public String getPresentationName() {
                        return Locale.getString(ControlPointMoveMode.class, "UNDO_PRESENTATION_NAME"); //$NON-NLS-1$
                }
                
        }
        
}