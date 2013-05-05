/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.android.actionmode.creation ;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.undo.AbstractUndoable;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.actionmode.ActionModeManager;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.Node;

import android.util.Log;

/** This class implements a Mode that permits to
 * create nodes.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractNodeCreationMode extends AbstractAndroidCreationMode {

	private final int undoLabel;
	private final Rectangle2f bounds = new Rectangle2f();
	private final Rectangle2f damagedRectangle = new Rectangle2f();
	private Point2D hit = null;
	private Shape2f shape = null;
	private String undoLabelBuffer = null;

	/** Construct a new AbstractNodeCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 *  
	 *  @param undoLabel
	 */
	public AbstractNodeCreationMode(int undoLabel) {
		super(true);
		this.undoLabel = undoLabel;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onModeActivated() {
		// Ensure that only this mode is receiving the events
		setExclusive(true);
		requestFocus();
		super.onModeActivated();
	}
	
	@Override
	public void cleanMode() {
		super.cleanMode();
		this.hit = null;
		this.shape = null;
	}
	
	@Override
	public void paint(DroidViewGraphics2D g) {
		if (this.shape!=null) {
			Color border = getModeManagerOwner().getSelectionBackground();
			Color background = border.transparentColor();
			g.setFillColor(background);
			g.setOutlineColor(border);
			g.setInteriorPainted(true);
			g.setOutlineDrawn(true);
			g.draw(this.shape);
		}
	}
	
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		this.shape = null;
		if (!isPointerInFigureShape()) {
			this.hit = event.getPosition();
			this.bounds.setFromCorners(
					this.hit.getX(),
					this.hit.getY(),
					this.hit.getX(),
					this.hit.getY());
			this.damagedRectangle.set(this.bounds);
			this.damagedRectangle.inflate(
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE);
			event.consume();
		}
	}
	
	@Override
	public void pointerDragged(ActionPointerEvent event) {
		if (this.hit!=null) {
			Point2D p = event.getPosition();
			this.bounds.setFromCorners(
					this.hit.getX(),
					this.hit.getY(),
					p.getX(),
					p.getY());
			this.shape = getShape(this.bounds);
			Rectangle2f bb = this.bounds.createUnion(this.shape.toBoundingBox());
			repaint(this.damagedRectangle.createUnion(bb));
			this.damagedRectangle.set(bb);
			event.consume();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void pointerReleased(ActionPointerEvent event) {
		if (this.hit!=null) {
			float hitX = this.hit.getX();
			float hitY = this.hit.getY();
			ActionModeOwner container = getModeManagerOwner();
			Graph graph = container.getGraph();
			FigureFactory factory = container.getFigureFactory();
			if (graph!=null && factory!=null) {
				Node modelObject = createModelObject();
				if (modelObject!=null) {
					ModelObjectFigure figure = (ModelObjectFigure)factory.createFigureFor(
							getModeManager().getViewID(),
							graph,
							modelObject,
							hitX,
							hitY);
					if (figure!=null) {
						if (this.undoLabelBuffer==null) {
							this.undoLabelBuffer = container.getContext().getString(this.undoLabel);
						}
						Undo undoCmd = new Undo(
								graph, this.bounds,
								modelObject, figure,
								getModeManager().getViewID(),
								this.undoLabelBuffer);
						undoCmd.doEdit();
						container.getUndoManager().add(undoCmd);
					}
					else {
						Log.e(getClass().getName(), "No figure created when inserting a node"); //$NON-NLS-1$
					}
				}
			}
			else {
				Log.e(getClass().getName(), "No graph nor factory to create the node"); //$NON-NLS-1$
			}
			
			Rectangle2f bb;
			if (this.shape!=null) {
				bb = this.bounds.createUnion(this.shape.toBoundingBox());
			}
			else {
				bb = this.bounds;
			}
			this.damagedRectangle.setUnion(bb);
			this.damagedRectangle.inflate(
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE);
			repaint(this.damagedRectangle);
			event.consume();
		}
		finish();
	}
	
	/** Replies a shape that is representing the figure to create.
	 * 
	 * @param bounds
	 * @return the shape
	 */
	protected abstract Shape2f getShape(Rectangle2f bounds);
	
	/** Create the model object.
	 * 
	 * @return the model object.
	 */
	protected abstract Node<?,?,?,?> createModelObject();

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	private static class Undo extends AbstractUndoable {

		private static final long serialVersionUID = -5230069079023558150L;

		private final String label;
		private final Graph graph;
		private final Rectangle2f bounds;
		private final Node modelObject;
		private final UUID view;
		private final ModelObjectFigure figure;

		public Undo(Graph graph, Rectangle2f bounds,
				Node modelObject, ModelObjectFigure figure,
				UUID view, String label) {
			this.graph = graph;
			this.bounds = bounds.clone();
			this.modelObject = modelObject;
			this.figure = figure;
			this.view = view;
			this.label = label;
		}

		@Override
		public String getPresentationName() {
			return this.label;
		}

		@Override
		protected void doEdit() {
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
			this.figure.setModelObject(this.modelObject);
			this.graph.addNode(this.modelObject);
		}

		@Override
		protected void undoEdit() {
			this.graph.removeNode(this.modelObject);
		}

	}
	
}