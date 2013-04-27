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

import java.util.Set;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.undo.AbstractUndoable;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.actionmode.ActionModeManager;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.android.graphics.TransparentViewGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.shadow.ComposedShadowPainter;
import org.arakhne.neteditor.fig.shadow.ShadowPainter;
import org.arakhne.neteditor.android.R;

import android.content.Context;

/** This class implements a Mode that resize the node figures
 * and decoration figures.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class ResizeMode extends ActionMode<Figure,DroidViewGraphics2D,Color> {

	private final Rectangle2f damagedRectangle = new Rectangle2f();
	private Point2D hitPosition = null;
	private ComposedShadowPainter shadowPainter = null;
	private ResizeDirection direction = null;

	/** Construct a new ControlPointMoveMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public ResizeMode() {
		setPersistent(false);
	}

	@Override
	protected void onModeActivated() {
		// Ensure that only this mode is receiving the events
		setExclusive(true);
		requestFocus();
		super.onModeActivated();
	}

	/** Set the resizing direction.
	 * 
	 * @param direction
	 */
	public void setResizingDirection(ResizeDirection direction) {
		this.direction = direction;
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
		this.direction = null;
		if (this.shadowPainter!=null) {
			this.shadowPainter.release();
			this.shadowPainter = null;
		}
	}

	/**
	 * Invoked to handle the POINTER_PRESSED event.
	 * 
	 * @param event is the description of the event.
	 * @param hitFigure is the hit figure or <code>null</code> if unknown. 
	 */
	void pointerPressed(ActionPointerEvent event, Figure hitFigure) {
		this.damagedRectangle.clear();
		if (this.direction==null) {
			done();
		}
		else {
			Figure hFigure = hitFigure==null ? getPointedFigure() : hitFigure;
			this.hitPosition = event.getPosition();
			this.shadowPainter = new ComposedShadowPainter();
			for(Figure figure : (SelectionManager)getModeManagerOwner().getSelectionManager()) {
				if (figure.isResizable()
						&& !figure.isLocked()) {
					this.shadowPainter.offers(figure, figure==hFigure);
				}
			}
			if (this.shadowPainter!=null && this.shadowPainter.getPainters().isEmpty()) {
				this.shadowPainter = null;
			}
			if (this.shadowPainter==null) {
				done();
			}
			else {
				this.damagedRectangle.set(this.shadowPainter.getDamagedBounds());
				this.damagedRectangle.inflate(
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE);
				event.consume();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		pointerPressed(event, null);
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
			this.shadowPainter.resize(dx, dy, this.direction);
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
				Set<ShadowPainter> painters = this.shadowPainter.getPainters();
				Object[] figures = new Object[painters.size()*3];
				int i=0;
				for(ShadowPainter painter : painters) {
					figures[i] = painter.getFigure();
					figures[i+1] = painter.getShadowBounds().clone();
					figures[i+2] = painter.getFigure().getBounds();
					i+=3;
				}

				String label = null;
				Context context = ((ActionModeOwner)getModeManagerOwner()).getContext();
				if (figures.length==1) {
					String txt = ((Figure)figures[0]).getName();
					if (txt!=null && !txt.isEmpty()) {
						label = context.getString(R.string.actionmode_resizing_1_figure, txt);
					}
				}
				if (label==null) {
					label = context.getString(R.string.actionmode_resizing_n_figures);
				}

				Undo undoCmd = new Undo(label, figures);
				undoCmd.doEdit();
				getModeManagerOwner().getUndoManager().add(undoCmd);
				event.consume();
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

		private static final long serialVersionUID = -3179737022971633752L;

		private final String label;
		private final Object[] figures;

		/**
		 * @param label
		 * @param figures
		 */
		public Undo(String label, Object... figures) {
			this.label = label;
			this.figures = figures;
		}

		@Override
		public void doEdit() {
			Figure figure;
			Rectangle2f bounds;
			for(int i=0; i<this.figures.length; i+=3) {
				figure = (Figure)this.figures[i];
				bounds = (Rectangle2f)this.figures[i+1];
				if (figure.isResizable() && !figure.isLocked()) {
					figure.setBounds(bounds);
				}
			}
		}

		@Override
		public void undoEdit() {
			Figure figure;
			Rectangle2f bounds;
			for(int i=0; i<this.figures.length; i+=3) {
				figure = (Figure)this.figures[i];
				bounds = (Rectangle2f)this.figures[i+2];
				if (figure.isResizable() && !figure.isLocked()) {
					figure.setBounds(bounds);
				}
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