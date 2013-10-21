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

import java.util.Arrays;
import java.util.Set;

import javax.swing.undo.UndoableEdit;

import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.shadow.ComposedShadowPainter;
import org.arakhne.neteditor.fig.shadow.ShadowPainter;
import org.arakhne.neteditor.swing.graphics.SwingViewGraphics2D;
import org.arakhne.neteditor.swing.graphics.TransparentViewGraphics2D;

/** This class implements a Mode that move the node figures
 * and the decoration figures.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class MoveMode extends ActionMode<Figure,SwingViewGraphics2D,Color> {

	private Point2D hitPosition = null;

	private ComposedShadowPainter shadowPainter = null;

	/** Construct a new ControlPointMoveMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public MoveMode() {
		super.setPersistent(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPersistent(boolean persistent) {
		super.setPersistent(false);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void paint(SwingViewGraphics2D g) {
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
		Figure hitFigure = getPointedFigure();
		this.hitPosition = event.getPosition();
		this.shadowPainter = new ComposedShadowPainter();
		for(Figure figure : (SelectionManager)getModeManagerOwner().getSelectionManager()) {
			if (figure.isMovable()
					&& !figure.isLocked()) {
				this.shadowPainter.offers(figure, figure==hitFigure);
			}
		}
		if (this.shadowPainter.getPainters().isEmpty()) {
			Figure figure = getPointedFigure();
			if (figure instanceof BlockFigure
					&& figure.isMovable() && !figure.isLocked()) {
				this.shadowPainter.offers(figure, figure==hitFigure);
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
		else {
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
			this.shadowPainter.moveTo(dx, dy);
			repaint();
		}
		event.consume();
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
				Undo undoCmd = new Undo(dx, dy, figures);
				undoCmd.doEdit();
				getModeManagerOwner().getUndoManager().add(undoCmd);
			}
			this.shadowPainter.release();
			this.shadowPainter = null;
			repaint();
		}
		if (isPersistent()) cleanMode();
		else done();
		event.consume();
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Undo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = -5863056949910287988L;

		private float dx;
		private float dy;
		private final Figure[] figures;

		/**
		 * @param dx
		 * @param dy
		 * @param figures
		 */
		public Undo(float dx, float dy, Figure... figures) {
			this.dx = dx;
			this.dy = dy;
			this.figures = figures;
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof Undo) {
				Undo u = (Undo)anEdit;
				if (Arrays.equals(this.figures, u.figures)) {
					this.dx += u.dx;
					this.dy += u.dy;
					return true;
				}
			}
			return false;
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
			if (this.figures.length==1) {
				String txt = this.figures[0].getName();
				if (txt!=null && !txt.isEmpty()) {
					return Locale.getString(MoveMode.class, "UNDO_PRESENTATION_NAME_1", txt); //$NON-NLS-1$
				}
			}
			return Locale.getString(MoveMode.class, "UNDO_PRESENTATION_NAME_n"); //$NON-NLS-1$
		}

	}

}