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

import java.util.Set;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.shadow.ComposedShadowPainter;
import org.arakhne.neteditor.fig.shadow.ShadowPainter;
import org.arakhne.neteditor.swing.actionmode.ActionModeUtil;
import org.arakhne.neteditor.swing.graphics.TransparentViewGraphics2D;

/** This class implements a Mode that resize the node figures
 * and decoration figures.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class ResizeMode extends ActionMode<Figure,VirtualScreenGraphics2D,java.awt.Color> {

	private Point2D hitPosition = null;

	private ComposedShadowPainter shadowPainter = null;

	private ResizeDirection direction = null;

	/** Construct a new ControlPointMoveMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public ResizeMode() {
		//
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
		this.direction = null;
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
		MouseCursor c = null;
		if (event.getButton()==1) {
			if (this.direction==null) {
				done();
			}
			else {
				Figure hitFigure = getPointedFigure();
				this.hitPosition = event.getPosition();
				this.shadowPainter = new ComposedShadowPainter();
				for(Figure figure : (SelectionManager)getModeManagerOwner().getSelectionManager()) {
					if (figure.isResizable()
							&& !figure.isLocked()) {
						this.shadowPainter.offers(figure, figure==hitFigure);
					}
				}
				if (this.shadowPainter!=null && this.shadowPainter.getPainters().isEmpty()) {
					this.shadowPainter = null;
				}
				if (this.shadowPainter==null) {
					done();
				}
				else {
					c = ActionModeUtil.getResizingCursor(this.direction);
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
		if (this.shadowPainter!=null) {
			Point2D currentPosition = event.getPosition();
			float dx = currentPosition.getX() - this.hitPosition.getX();
			float dy = currentPosition.getY() - this.hitPosition.getY();
			this.shadowPainter.resize(dx, dy, this.direction);
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
					Set<ShadowPainter> painters = this.shadowPainter.getPainters();
					Object[] figures = new Object[painters.size()*3];
					int i=0;
					for(ShadowPainter painter : painters) {
						figures[i] = painter.getFigure();
						figures[i+1] = painter.getShadowBounds().clone();
						figures[i+2] = painter.getFigure().getBounds().clone();
						i+=3;
					}
					Undo undoCmd = new Undo(figures);
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
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Undo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = -3179737022971633752L;

		private final Object[] figures;

		/**
		 * @param figures
		 */
		 public Undo(Object... figures) {
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
			  if (this.figures.length==1) {
				  String txt = ((Figure)this.figures[0]).getName();
				  if (txt!=null && !txt.isEmpty()) {
					  return Locale.getString(ResizeMode.class, "UNDO_PRESENTATION_NAME_1", txt); //$NON-NLS-1$
				  }
			  }
			  return Locale.getString(ResizeMode.class, "UNDO_PRESENTATION_NAME_n"); //$NON-NLS-1$
		  }

	}

}