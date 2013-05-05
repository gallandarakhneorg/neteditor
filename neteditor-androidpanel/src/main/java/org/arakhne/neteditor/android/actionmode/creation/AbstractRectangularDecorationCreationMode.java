/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.android.actionmode.creation ;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionModeManagerOwner;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.actionmode.ActionModeManager;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;

/** This class implements a Mode that permits to
 * create objects based on the drawing of
 * a rectangle.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractRectangularDecorationCreationMode extends AbstractAndroidCreationMode {

	private final Rectangle2f bounds = new Rectangle2f();
	private final Rectangle2f damagedRectangle = new Rectangle2f();
	private Point2D hit = null;
	private Shape2f shape = null;

	/** Construct a new AbstractRectangularDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public AbstractRectangularDecorationCreationMode() {
		super(true);
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanMode() {
		super.cleanMode();
		this.hit = null;
		this.shape = null;
	}

	/**
	 * {@inheritDoc}
	 */
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
	
	@Override
	public void pointerReleased(ActionPointerEvent event) {
		if (this.hit!=null) {
			DecorationFigure figure = createFigure(getModeManager().getViewID(), this.bounds);
			if (figure!=null) {
				ActionModeManagerOwner<Figure,?,?> container = getModeManagerOwner();
				Undoable undo = container.addFigure(figure);
				container.getUndoManager().add(undo);
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

	/** Create the object according to the given bounds.
	 * 
	 * @param viewId is the identifier of the view.
	 * @param bounds
	 * @return the decoration.
	 */
	protected abstract DecorationFigure createFigure(UUID viewId, Rectangle2f bounds);
	
}