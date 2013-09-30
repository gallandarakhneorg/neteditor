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
package org.arakhne.neteditor.swing.actionmode.creation ;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.event.KeyEvent;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.anchor.AnchorFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.swing.actionmode.ActionModeOwner;

/** This class implements a Mode that permits to
 * create edges between anchors.
 *
 * @param <G> is the type of the graph supported by the mode container.
 * @param <A> is the type of the anchors supported by the mode container.
 * @param <E> is the type of the edges supported by the mode container.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class EdgeCreationMode<G extends Graph<G,?,A,E>, A extends Anchor<G,?,A,E>, E extends Edge<G,?,A,E>> extends ActionMode<Figure,VirtualScreenGraphics2D,java.awt.Color> {

        private final List<Point2D> points = new ArrayList<Point2D>();
        private AnchorFigure<A> startAnchor = null;
        private E edge = null;
        private Point2D candidate = null;

        /** Construct a new EdgeCreationMode with the given parent.
         *
         * @param modeManager a reference to the ModeManager that
         *                    contains this Mode.
         */
        public EdgeCreationMode(ActionModeManager<Figure,VirtualScreenGraphics2D,java.awt.Color> modeManager) { 
                super(modeManager);
        }

        /** Construct a new EdgeCreationMode. The 
         *  {@link ActionModeManager} should be
         *  set before using this object.
         */
        public EdgeCreationMode() {
                //
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void cleanMode() {
                setExclusive(false);
                this.points.clear();
                this.startAnchor = null;
                this.candidate = null;
        }

        @SuppressWarnings("unchecked")
        private AnchorFigure<A> getPointedAnchor() {
                if (isPointerInFigureShape()) {
                        Figure fig = getPointedFigure();
                        if (fig instanceof NodeFigure<?,?>) {
                                NodeFigure<?,?> nodeFigure = (NodeFigure<?,?>)fig;
                                Shape2f hitArea = getModeManager().getFigureHitArea();
                                if (hitArea!=null) {
	                                return (AnchorFigure<A>)nodeFigure.getAnchorOn(hitArea);
                                }
                        }
                }
                return null;
        }

        private static Point2D getCenter(AnchorFigure<?> anchor) {
                float x = anchor.getAbsoluteX();
                float y = anchor.getAbsoluteY();
                return new Point2f(
                                x + anchor.getWidth() / 2f,
                                y + anchor.getHeight() / 2f);
        }

        private E getNewEdge() {
                if (this.edge==null) {
                        this.edge = createEdge();
                }
                return this.edge;
        }

        private boolean canArriveTo(AnchorFigure<A> figure) {
                if (figure!=null) {
                        A anchor = figure.getModelObject();
                        assert(anchor!=null);
                        E newEdge = getNewEdge();
                        assert(newEdge!=null);
                        assert(this.startAnchor!=null);
                        A otherSide = this.startAnchor.getModelObject();
                        assert(otherSide!=null);
                        return anchor.canConnectAsEndAnchor(newEdge, otherSide);
                }
                return false;
        }

        private boolean canStartFrom(AnchorFigure<A> figure) {
                if (figure!=null) {
                        A anchor = figure.getModelObject();
                        assert(anchor!=null);
                        E newEdge = getNewEdge();
                        assert(newEdge!=null);
                        assert(this.startAnchor==null);
                        return anchor.canConnectAsStartAnchor(newEdge, null);
                }
                return false;
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
                        MouseCursor c = null;
                        if (this.startAnchor==null) {
                                AnchorFigure<A> figure = getPointedAnchor();
                                if (figure!=null && canStartFrom(figure)) {
                                        this.startAnchor = figure;
                                        this.points.clear();
                                        this.points.add(getCenter(this.startAnchor));
                                        this.candidate = event.getPosition();
                                        c = MouseCursor.CROSSHAIR;
                                        repaint();
                                }
                        }
                        else {
                                this.candidate = event.getPosition();
                                c = MouseCursor.CROSSHAIR;
                        }
                        event.consume();
                        setCursor(c);
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerMoved(ActionPointerEvent event) {
                MouseCursor c = null;
                AnchorFigure<A> figure = getPointedAnchor();
                if (this.startAnchor==null) {
                        if (figure!=null) {
                                if (canStartFrom(figure)) {
                                        c = MouseCursor.CROSSHAIR;
                                }
                                else {
                                        c = MouseCursor.INVALID;
                                }
                        }
                }
                else {
                        if (figure!=null) {
                                if (canArriveTo(figure)) {
                                        c = MouseCursor.CROSSHAIR;
                                }
                                else {
                                        c = MouseCursor.INVALID;
                                }
                        }
                }
                event.consume();
                setCursor(c);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerDragged(ActionPointerEvent event) {
                MouseCursor c = null;
                if (this.startAnchor!=null) {
                        this.candidate = event.getPosition();
                        AnchorFigure<A> figure = getPointedAnchor();
                        if (figure!=null) {
                                if (canArriveTo(figure)) {
                                        c = MouseCursor.CROSSHAIR;
                                }
                                else {
                                        c = MouseCursor.INVALID;
                                }
                        }
                        repaint();
                }
                event.consume();
                setCursor(c);
        }

        /** Replies the maximal number of control points that is
         * allowed for the creation.
         *
         * @return the maximal number of control points.
         */
        protected abstract int getMaximalControlPointCount();

        /**
         * {@inheritDoc}
         */
        @Override
        public void pointerReleased(ActionPointerEvent event) {
                if (event.getButton()==1) {
                        MouseCursor c = null;
                        if (this.startAnchor!=null) {
                                AnchorFigure<A> figure = getPointedAnchor();
                                if (figure!=null) {
                                        // Try to connect to the anchor
                                        if ((this.startAnchor!=figure || this.points.size()>=2)
                                                        &&canArriveTo(figure)) {
                                                this.points.add(event.getPosition());
                                                addEdge(getNewEdge(),
                                                                this.startAnchor.getModelObject(),
                                                                figure.getModelObject(),
                                                                this.points);
                                        }
                                        else {
                                                c = MouseCursor.CROSSHAIR;
                                        }
                                        repaint();
                                        if (isPersistent()) cleanMode();
                                        else done();
                                }
                                else {
                                        // Put a control point
                                        c = MouseCursor.CROSSHAIR;
                                        if (this.points.size()<getMaximalControlPointCount()) {
                                                Point2D position = event.getPosition();
                                                this.points.add(position);
                                                repaint();
                                        }
                                }
                        }
                        else {
                                done();
                        }
                        event.consume();
                        setCursor(c);
                }
        }

        /** Invoked to create an edge instance that is not already
         * connected to anchors.
         * 
         * @return the edge instance.
         */
        protected abstract E createEdge();

        /** Invoked to connect the specified edge to the specified
         * anchors and with the specified path.
         * 
         * @param edge is the new edge
         * @param startAnchor is the start anchor.
         * @param endAnchor is the end anchor.
         * @param controlPoints are the edge control point, including the
         * points that may be connected to the anchors.
         */
        @SuppressWarnings("unchecked")
		protected void addEdge(E edge, A startAnchor, A endAnchor, List<Point2D> controlPoints) {
                ActionModeOwner<G> container = (ActionModeOwner<G>)getModeManagerOwner();
                G graph = container.getGraph();
                if (graph!=null && edge!=null && startAnchor!=null && endAnchor!=null) {
                        Undo<G,A,E> undoCmd = new Undo<G,A,E>(
                                        getModeManager().getViewID(),
                                        graph, edge, startAnchor, endAnchor, controlPoints);
                        undoCmd.doEdit();
                        container.getUndoManager().add(undoCmd);
                        Figure figure = edge.getViewBinding().getView(getModeManager().getViewID(), Figure.class);
                        if (figure!=null && container.isSelectionEnabled())
                                container.getSelectionManager().setSelection(figure);
                        this.edge = null;
                }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void paint(VirtualScreenGraphics2D g) {
                if (this.startAnchor!=null && !this.points.isEmpty()) {
                        Point2D p;
                        GeneralPath path = new GeneralPath();
                        p = this.points.get(0);
                        path.moveTo(p.getX(), p.getY());
                        for(int i=1; i<this.points.size(); ++i) {
                                p = this.points.get(i);
                                path.lineTo(p.getX(), p.getY());
                        }

                        if (this.candidate!=null) {
                                path.lineTo(this.candidate.getX(), this.candidate.getY());
                        }

                        g.setColor(getModeManagerOwner().getSelectionBackground());
                        g.draw(path);
                }
        }

        /**
         * @param <G> is the type of the graph supported by the mode container.
         * @param <A> is the type of the anchors supported by the mode container.
         * @param <E> is the type of the edges supported by the mode container.
         * @author $Author: galland$
         * @version $Name$ $Revision$ $Date$
         * @mavengroupid $GroupId$
         * @mavenartifactid $ArtifactId$
         */
        private static class Undo<G extends Graph<G,?,A,E>, A extends Anchor<G,?,A,E>, E extends Edge<G,?,A,E>>
        extends AbstractCallableUndoableEdit {

                private static final long serialVersionUID = -3604234058112925898L;
                
                private final UUID viewId;
                private final G graph;
                private final E edge;
                private final A startAnchor;
                private final A endAnchor;
                private final List<Point2D> controlPoints = new ArrayList<Point2D>();

                /**
                 * @param viewId
                 * @param graph
                 * @param edge
                 * @param startAnchor
                 * @param endAnchor
                 * @param controlPoints
                 */
                public Undo(UUID viewId, G graph, E edge, A startAnchor, A endAnchor, List<Point2D> controlPoints) {
                        this.viewId = viewId;
                        this.graph = graph;
                        this.edge = edge;
                        this.startAnchor = startAnchor;
                        this.endAnchor = endAnchor;
                        this.controlPoints.addAll(controlPoints);
                }

                @Override
                public void doEdit() {
                        this.graph.addEdge(this.edge);
                        EdgeFigure<?> figure = this.edge.getViewBinding().getView(this.viewId, EdgeFigure.class);
                        Point2D p;
                        for(int i=this.controlPoints.size()-2; i>0; --i) {
                                p = this.controlPoints.get(i);
                                figure.insertCtrlPointAt(1, p.getX(), p.getY());
                        }
                        this.edge.setStartAnchor(this.startAnchor);
                        this.edge.setEndAnchor(this.endAnchor); 
                }
                
                @Override
                public void undoEdit() {
                        this.graph.removeEdge(this.edge);
                }
                
                /**
                 * {@inheritDoc}
                 */
                @Override
                public String getPresentationName() {
                        if (this.edge!=null) {
                                String txt = this.edge.getName();
                                if (txt!=null && !txt.isEmpty()) {
                                        return Locale.getString(EdgeCreationMode.class, "UNDO_PRESENTATION_1", txt); //$NON-NLS-1$
                                }
                        }
                        return Locale.getString(EdgeCreationMode.class, "UNDO_PRESENTATION_n"); //$NON-NLS-1$
                }

        }

}