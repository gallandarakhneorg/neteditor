/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.android.actionmode.base ;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.undo.AbstractUndoable;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.undo.UndoableGroup;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.actionmode.ActionModeManager;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.android.graphics.TransparentViewGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.shadow.LinearFeatureShadowPainter;
import org.arakhne.neteditor.fig.view.LinearFeature;
import org.arakhne.neteditor.android.R;

import android.content.Context;

/** This class implements a Mode that move the control
 * points of an edge.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class ControlPointMoveMode extends ActionMode<Figure,DroidViewGraphics2D,Color> {

	private final Rectangle2f damagedRectangle = new Rectangle2f();
	private final Undoable undo;
	private int movedCtrlPoint = -1;
	private Point2D hitPosition = null;
	private LinearFeatureShadowPainter shadowPainter = null;

	/** Construct a new ControlPointMoveMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 *  
	 *  @param undo
	 */
	public ControlPointMoveMode(Undoable undo) {
		this.undo = undo;
		setPersistent(false);
	}
	
	@Override
	protected void onModeActivated() {
		// Ensure that only this mode is receiving the events
		setExclusive(true);
		requestFocus();
		super.onModeActivated();
	}

	/** {@inheritDoc}
	 */
	@Override
	public void paint(DroidViewGraphics2D g) {
		if (this.shadowPainter!=null) {
			g.reset(); // ensure that the graphical context is not influence by any other mode.
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

	/** Invoked to handle the POINTER_PRESSED event.
	 * 
	 * @param event is the description of the event.
	 * @param ctrlPointIndex is the hit control point; or
	 * {@code -1} if the control point is unknown.
	 * @param hitFigure is the hit figure or <code>null</code> if unknown. 
	 */
	void pointerPressed(ActionPointerEvent event, int ctrlPointIndex, Figure hitFigure) {
		Figure figure = hitFigure==null ? getPointedFigure() : hitFigure;
		this.damagedRectangle.clear();
		if (figure instanceof LinearFeature
				&& figure.isResizable() && !figure.isLocked()) {
			LinearFeature linearFeature = (LinearFeature)figure;

			this.hitPosition = event.getPosition();

			if (ctrlPointIndex<0) {
				this.movedCtrlPoint = linearFeature.hitCtrlPoint(
						this.hitPosition.getX(),
						this.hitPosition.getY(),
						getClickPrecision());
			}
			else {
				this.movedCtrlPoint = ctrlPointIndex;
			}

			if (this.movedCtrlPoint>=0
					&& this.movedCtrlPoint<linearFeature.getCtrlPointCount()) {
				this.shadowPainter = linearFeature.getShadowPainter();
				this.damagedRectangle.set(this.shadowPainter.getDamagedBounds());
				this.damagedRectangle.inflate(
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE);
				event.consume();
			}
			else {
				done();
			}
		}
		else {
			done();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		pointerPressed(event, -1, null);
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
			Rectangle2f newBounds = this.shadowPainter.getDamagedBounds(); 
			repaint(this.damagedRectangle.createUnion(newBounds));
			this.damagedRectangle.set(newBounds);
			event.consume();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerReleased(ActionPointerEvent event) {
		if (this.shadowPainter!=null) {
			Point2D currentPosition = event.getPosition();
			float dx = currentPosition.getX() - this.hitPosition.getX();
			float dy = currentPosition.getY() - this.hitPosition.getY();
			if (dx!=0f || dy!=0f) {
				Figure figure = this.shadowPainter.getFigure();
				if (figure instanceof LinearFeature) {
					UndoManager undoManager = getModeManagerOwner().getUndoManager();
					LinearFeature linearFeature = (LinearFeature)figure;
					Context context = ((ActionModeOwner)getModeManagerOwner()).getContext();
					Undo undoCmd = new Undo(
							context.getString(R.string.actionmode_moving_ctrlpts),
							linearFeature, this.movedCtrlPoint, dx, dy);
					undoCmd.doEdit();
					if (this.undo!=null) {
						UndoableGroup group = new UndoableGroup(
								context.getString(R.string.actionmode_inserting_moving_ctrlpts));
						group.add(this.undo);
						group.add(undoCmd);
						undoManager.add(group);
					}
					else {
						undoManager.add(undoCmd);
					}
					event.consume();
				}
			}
			Rectangle2f newBounds = this.shadowPainter.getShadowBounds();
			this.damagedRectangle.setUnion(newBounds);
			this.damagedRectangle.inflate(
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE);

			this.shadowPainter.release();
			this.shadowPainter = null;
			
			repaint(this.damagedRectangle);
		}
		if (isPersistent()) cleanMode();
		else done();
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Undo extends AbstractUndoable {

		private static final long serialVersionUID = 6650491264977712680L;

		private final String label;
		private final LinearFeature figure;
		private final int index;
		private float dx;
		private float dy;
		private boolean pointRemoved;
		private float x;
		private float y;

		/**
		 * @param label
		 * @param figure
		 * @param index
		 * @param dx
		 * @param dy
		 */
		 public Undo(String label, LinearFeature figure, int index, float dx, float dy) {
			 this.label = label;
			 this.figure = figure;
			 this.index = index;
			 this.dx = dx;
			 this.dy = dy;
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
			  return this.label;
		  }

	}

}