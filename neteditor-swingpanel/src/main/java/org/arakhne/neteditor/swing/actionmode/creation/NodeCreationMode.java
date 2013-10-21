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

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.event.KeyEvent;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ModelObjectView;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.swing.actionmode.ActionModeOwner;
import org.arakhne.neteditor.swing.graphics.SwingViewGraphics2D;

/** This class implements a Mode that permits to
 * create nodes
 *
 * @param <G> is the type of the graph supported by the mode container.
 * @param <N> is the type of the nodes supported by the mode container.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class NodeCreationMode<G extends Graph<?,N,?,?>, N extends Node<G,?,?,?>> extends ActionMode<Figure,SwingViewGraphics2D,Color> {

	private Point2D hit = null;
	private final Rectangle2f bounds = new Rectangle2f();

	/** Construct a new NodeCreationMode with the given parent.
	 *
	 * @param modeManager a reference to the ModeManager that
	 *                    contains this Mode.
	 */
	public NodeCreationMode(ActionModeManager<Figure,SwingViewGraphics2D,Color> modeManager) { 
		super(modeManager);
	}

	/** Construct a new NodeCreationMode. The 
	 *  {@link ActionModeManager ActionModeManager} should be
	 *  set before using this object.
	 */
	public NodeCreationMode() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanMode() {
		setExclusive(false);
		this.hit = null;
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
				this.hit = event.getPosition();
				this.bounds.setFromCorners(
						this.hit.getX(),
						this.hit.getY(),
						this.hit.getX(),
						this.hit.getY());
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
		if (this.hit!=null) {
			Point2D p = event.getPosition();
			this.bounds.setFromCorners(
					this.hit.getX(),
					this.hit.getY(),
					p.getX(),
					p.getY());
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
			if (this.hit!=null) {
				this.hit = null;
				createNodeAt(this.bounds);
				repaint();
			}
			if (isPersistent()) cleanMode();
			else done();
			event.consume();
		}
	}

	/** This method is invoked each time a node
	 * may be created.
	 * 
	 * @param bounds are the bounds to associated to the node.
	 */
	@SuppressWarnings("unchecked")
	protected void createNodeAt(Rectangle2f bounds) {
		ActionModeOwner<G> container = (ActionModeOwner<G>)getModeManagerOwner();
		G graph = container.getGraph();
		FigureFactory<G> factory = container.getFigureFactory();
		if (graph!=null && factory!=null) {
			N newNode = createModelObject();
			if (newNode!=null) {
				Figure figure = factory.createFigureFor(
						getModeManager().getViewID(),
						graph,
						newNode,
						bounds.getMinX(),
						bounds.getMinY());
				if (figure!=null) {
					Undo<G,N> undoCmd = new Undo<G,N>(graph, bounds, newNode, figure,
							getModeManager().getViewID());
					undoCmd.doEdit();
					container.getUndoManager().add(undoCmd);
					if (container.isSelectionEnabled())
						container.getSelectionManager().setSelection(figure);
				}
			}
		}
	}

	/** Invoked to create a new node.
	 * 
	 * @return the new node.
	 */
	protected abstract N createModelObject();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(SwingViewGraphics2D g) {
		if (this.hit!=null && !this.bounds.isEmpty()) {
			Color border = getModeManagerOwner().getSelectionBackground();
			Color background = border.transparentColor();
			g.setColors(background, border);
			g.draw(this.bounds);
		}
	}

	/** 
	 * @param <G> is the type of the graph supported by the mode container.
	 * @param <N> is the type of the nodes supported by the mode container.
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Undo<G extends Graph<?,N,?,?>, N extends Node<G,?,?,?>>
	extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = -7896116415216393426L;

		private final G graph;
		private final Rectangle2f bounds;
		private final N object;
		private final Figure figure;
		private final UUID view;

		/**
		 * @param graph
		 * @param bounds
		 * @param object
		 * @param figure
		 * @param view
		 */
		public Undo(G graph, Rectangle2f bounds, N object, Figure figure, UUID view) {
			this.graph = graph;
			this.bounds = bounds.clone();
			this.object = object;
			this.figure = figure;
			this.view = view;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public void doEdit() {
			float w, h;
			if (this.bounds.isEmpty()) {
				w = this.figure.getWidth();
				h = this.figure.getHeight();
			}
			else {
				w = this.bounds.getWidth();
				h = this.bounds.getHeight();
			}
			this.figure.setBounds(
					this.bounds.getMinX(),
					this.bounds.getMinY(),
					w, h);
			this.figure.setViewUUID(this.view);
			if (this.figure instanceof ModelObjectView<?>) {
				((ModelObjectView)this.figure).setModelObject(this.object);
			}
			this.graph.addNode(this.object);
		}

		@Override
		public void undoEdit() {
			this.graph.removeNode(this.object);
		}

		/**
		 * {@inheritDoc}
		 */
		 @Override
		 public String getPresentationName() {
			 if (this.object!=null) {
				 String txt = this.object.getName();
				 if (txt!=null && !txt.isEmpty()) {
					 return Locale.getString(NodeCreationMode.class, "UNDO_PRESENTATION_1", txt); //$NON-NLS-1$
				 }
			 }
			 return Locale.getString(NodeCreationMode.class, "UNDO_PRESENTATION_n"); //$NON-NLS-1$
		 }

	}

}