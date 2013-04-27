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
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.shadow.ComposedShadowPainter;
import org.arakhne.neteditor.fig.shadow.ShadowPainter;
import org.arakhne.neteditor.android.R;

import android.content.Context;

/** This class implements a Mode that move the node figures
 * and the decoration figures.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class MoveMode extends ActionMode<Figure,DroidViewGraphics2D,Color> {

	private Point2D hitPosition = null;
	private final Rectangle2f damagedRectangle = new Rectangle2f();

	private ComposedShadowPainter shadowPainter = null;

	/** Construct a new ControlPointMoveMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public MoveMode() {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		pointerPressed(event, null);
	}

	/**
	 * Invoked to handle the POINTER_PRESSED event.
	 * 
	 * @param event is the description of the event.
	 * @param hitFigure is the hit figure or <code>null</code> if unknown. 
	 */
	void pointerPressed(ActionPointerEvent event, Figure hitFigure) {
		Figure hFigure = hitFigure==null ? getPointedFigure() : hitFigure;
		this.hitPosition = event.getPosition();
		this.shadowPainter = new ComposedShadowPainter();
		this.damagedRectangle.clear();
		Rectangle2f bb;
		for(Figure figure : (SelectionManager)getModeManagerOwner().getSelectionManager()) {
			if (figure.isMovable() && !figure.isLocked()) {
				this.shadowPainter.offers(figure, figure==hFigure);
				bb = figure.getDamagedBounds();
				if (bb!=null) {
					if (this.shadowPainter.size()==1) {
						this.damagedRectangle.set(bb);
					}
					else {
						Rectangle2f.union(this.damagedRectangle, this.damagedRectangle, bb);
					}
				}
			}
		}
		if (this.shadowPainter.getPainters().isEmpty()) {
			if (hFigure instanceof BlockFigure
					&& hFigure.isMovable() && !hFigure.isLocked()) {
				this.shadowPainter.offers(hFigure, true);
				this.damagedRectangle.inflate(
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE);
				repaint(this.damagedRectangle);
				event.consume();
			}
			else {
				this.shadowPainter = null;
				done();
			}
		}
		if (this.shadowPainter!=null && this.shadowPainter.getPainters().isEmpty()) {
			this.shadowPainter = null;
			done();
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
			this.shadowPainter.moveTo(dx, dy);
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
				Figure[] figures = new Figure[painters.size()];
				int i=0;
				for(ShadowPainter painter : painters) {
					figures[i] = painter.getFigure();
					++i;
				}

				String label = null;
				Context context = ((ActionModeOwner)getModeManagerOwner()).getContext();
				if (figures.length==1) {
					String txt = figures[0].getName();
					if (txt!=null && !txt.isEmpty()) {
						label = context.getString(R.string.actionmode_moving_1_figure);
					}
				}
				if (label==null) {
					label = context.getString(R.string.actionmode_moving_n_figures);
				}
				Undo undoCmd = new Undo(label, dx, dy, figures);
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

		private static final long serialVersionUID = -5863056949910287988L;

		private final String label;
		private float dx;
		private float dy;
		private final Figure[] figures;

		/**
		 * @param label
		 * @param dx
		 * @param dy
		 * @param figures
		 */
		public Undo(String label, float dx, float dy, Figure... figures) {
			this.label = label;
			this.dx = dx;
			this.dy = dy;
			this.figures = figures;
		}  

		@Override
		public void doEdit() {
			for(Figure figure : this.figures) {
				if (figure.isMovable() && !figure.isLocked()) {
					figure.translate(this.dx, this.dy);
				}
			}
		}

		@Override
		public void undoEdit() {
			for(Figure figure : this.figures) {
				if (figure.isMovable() && !figure.isLocked()) {
					figure.translate(-this.dx, -this.dy);
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