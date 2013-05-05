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

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.actionmode.ActionModeManagerOwner;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.awt.AwtUtil;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.event.KeyEvent;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;

/** This class implements a Mode that permits to
 * create decorations based on the drawing of
 * a rectangle.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractRectangularDecorationCreationMode extends ActionMode<Figure,VirtualScreenGraphics2D,java.awt.Color> {

	private Point2D hit = null;
	private final Rectangle2D bounds = new Rectangle2D.Float();

	/** Construct a new AbstractDecorationCreationMode with the given parent.
	 *
	 * @param persistent indicates if the mode is persistent or not.
	 * @param modeManager a reference to the ModeManager that
	 *                    contains this Mode.
	 */
	public AbstractRectangularDecorationCreationMode(boolean persistent, ActionModeManager<Figure,VirtualScreenGraphics2D,java.awt.Color> modeManager) { 
		super(modeManager);
		setPersistent(persistent);
	}

	/** Construct a new AbstractDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 *  
	 * @param persistent indicates if the mode is persistent or not.
	 */
	public AbstractRectangularDecorationCreationMode(boolean persistent) {
		setPersistent(persistent);
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
				this.bounds.setFrameFromDiagonal(
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
			this.bounds.setFrameFromDiagonal(
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
	protected void createNodeAt(Rectangle2D bounds) {
		ActionModeManagerOwner<Figure,?,?> container = getModeManagerOwner();
		DecorationFigure newFigure = createFigure();
		if (newFigure!=null) {
			newFigure.setBounds(
					(float)bounds.getMinX(),
					(float)bounds.getMinY(),
					(float)bounds.getWidth(),
					(float)bounds.getHeight());
			Undoable undo = container.addFigure(newFigure);
			if (container.isSelectionEnabled())
				container.getSelectionManager().setSelection(newFigure);
			container.getUndoManager().add(undo);
		}
	}

	/** Invoked to create a new figure.
	 * 
	 * @return the new figure.
	 */
	protected abstract DecorationFigure createFigure();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(VirtualScreenGraphics2D g) {
		if (this.hit!=null && !this.bounds.isEmpty()) {
			Shape shp = getShape(this.bounds);
			if (shp!=null) {
				Color border = getModeManagerOwner().getSelectionBackground();
				Color background = AwtUtil.makeTransparentColor(border);
				g.setColor(background);
				g.fill(shp);
				g.setColor(border);
				g.draw(shp);
			}
		}
	}

	/** Replies a shape that is representing the figure to create.
	 * 
	 * @param bounds
	 * @return the shape
	 */
	protected abstract Shape getShape(Rectangle2D bounds);

}