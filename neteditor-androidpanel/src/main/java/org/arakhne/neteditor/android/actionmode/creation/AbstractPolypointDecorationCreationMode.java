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

import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.actionmode.ActionModeManagerOwner;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.R;
import org.arakhne.neteditor.android.actionmode.ActionModeManager;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;

import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;


/** This class implements a Mode that permits to
 * create decorations based on a collection of points.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractPolypointDecorationCreationMode extends AbstractAndroidCreationMode {

	/** Constant that is used to detect click on coordinates
	 */
	public static final int CLICK_DISTANCE = 5;

	private final Rectangle2f damagedRectangle = new Rectangle2f();
	private Path2f points = null;
	private Path2f shape = null;
	private boolean enableLongClick = false;

	/** Construct a new AbstractPolypointDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public AbstractPolypointDecorationCreationMode() {
		super(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onModeActivated() {
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
		this.points = null;
		this.shape = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paint(DroidViewGraphics2D g) {
		if (this.shape!=null) {
			Color border = getModeManagerOwner().getSelectionBackground();
			g.setOutlineColor(border);
			g.setInteriorPainted(false);
			g.setOutlineDrawn(true);
			g.draw(this.shape);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		this.enableLongClick = true;
		if (this.points==null) {
			if (!isPointerInFigureShape()) {
				Point2D p = event.getPosition();

				this.points = new Path2f();
				this.points.moveTo(p.getX(), p.getY());

				this.shape = new Path2f();
				this.shape.moveTo(p.getX(), p.getY());
				this.shape.lineTo(p.getX(), p.getY());

				this.damagedRectangle.set(this.points.toBoundingBox());
				this.damagedRectangle.inflate(
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE,
						ActionModeManager.REPAINTING_INFLATING_SIZE);
				event.consume();
			}
		}
		else if (this.shape!=null) {
			Point2D p = event.getPosition();
			updatePolypointObject(p.getX(), p.getY());
			event.consume();
		}
	}
	
	private void updatePolypointObject(float x, float y) {
		this.shape.setLastPoint(x, y);

		getActionBar().invalidate();

		Rectangle2f newBounds = this.points.toBoundingBox().clone();
		newBounds.add(x, y);
		repaint(this.damagedRectangle.createUnion(newBounds));
		this.damagedRectangle.set(newBounds);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerDragged(ActionPointerEvent event) {
		this.enableLongClick = false;
		if (this.shape!=null) {
			Point2D p = event.getPosition();
			updatePolypointObject(p.getX(), p.getY());
			event.consume();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerReleased(ActionPointerEvent event) {
		this.enableLongClick = false;
		if (this.points!=null) {
			Point2D position = event.getPosition();

			this.points.lineTo(position.getX(), position.getY());

			this.shape.lineTo(position.getX(), position.getY());

			getActionBar().invalidate();

			Rectangle2f newBounds = this.points.toBoundingBox();
			repaint(this.damagedRectangle.createUnion(newBounds));
			this.damagedRectangle.inflate(
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE,
					ActionModeManager.REPAINTING_INFLATING_SIZE);
			event.consume();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerLongClicked(ActionPointerEvent event) {
		if (this.enableLongClick && this.points!=null) {
			this.enableLongClick = false;
			Point2D position = event.getPosition();
			this.points.lineTo(position.getX(), position.getY());

			finalizePolypointDrawing();
			event.consume();
		}
	}

	/** Finalize the polypoint object.
	 */
	protected void finalizePolypointDrawing() {
		Path2f polyline = this.points;
		this.points = null;
		this.shape = null;

		if (polyline!=null) {
			DecorationFigure figure = createFigure(getModeManager().getViewID(), polyline);
			if (figure!=null) {
				Undoable undo = getModeManagerOwner().addFigure(figure);
				getModeManagerOwner().getUndoManager().add(undo);
			}

			Rectangle2f newBounds = polyline.toBoundingBox();
			repaint(this.damagedRectangle.createUnion(newBounds));
		}

		getActionBar().invalidate();

		finish();
	}

	@Override
	protected ActionBar createActionBarListener() {
		return new ActionBar();
	}
	
	/** Invoked to create a new figure.
	 * 
	 * @param viewId is the id of the view inside which the figure will be inserted.
	 * @param path is the path to follow.
	 * @return the new figure.
	 */
	protected abstract DecorationFigure createFigure(UUID viewId, Path2f path);

	/** Action bar listener.
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class ActionBar extends AbstractAndroidCreationMode.ActionBar {

		/** Item that permits to finalize the polypoint object.
		 */
		protected MenuItem finalizationItem;

		/**
		 */
		public ActionBar() {
			//
		}

		/** Invoked when the action mode is created, ie. when
		 * {@code startActionMode()} was called.
		 * 
		 * @param mode is the new action mode.
		 * @param menu is the menu to populate with action buttons.
		 * @return <code>true</code> if the action mode should
		 * be created, <code>false</code> if entering this mode
		 * should be aborted.
		 */
		@Override
		public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
			ActionModeManagerOwner<?,?,?> container = getModeManagerOwner();
			UndoManager undoManager = container.getUndoManager();
			mode.getMenuInflater().inflate(R.menu.neteditor_polypointcreationmode, menu);
			this.finalizationItem = menu.findItem(R.id.menu_finalize_polypoint_object);
			this.undoItem = menu.findItem(R.id.menu_revert);
			this.redoItem = menu.findItem(R.id.menu_revert_revert);
			undoManager.addUndoListener(this);
			return true;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			super.onPrepareActionMode(mode, menu);
			this.finalizationItem.setEnabled(
					AbstractPolypointDecorationCreationMode.this.shape!=null &&
					!AbstractPolypointDecorationCreationMode.this.shape.isEmpty());
			return true;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			if (item.getItemId()==R.id.menu_finalize_polypoint_object) {
				finalizePolypointDrawing();
				return true;
			}
			return super.onActionItemClicked(mode, item);
		}

	}

}