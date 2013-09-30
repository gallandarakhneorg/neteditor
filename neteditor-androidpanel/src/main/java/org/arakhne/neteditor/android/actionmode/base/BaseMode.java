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
package org.arakhne.neteditor.android.actionmode.base ;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.ZoomableContext;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.undo.AbstractUndoable;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.R;
import org.arakhne.neteditor.android.actionmode.ActionModeManager;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.actionmode.DifferedPointerEvent;
import org.arakhne.neteditor.android.actionmode.SelectionMode;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.view.LinearFeature;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;

/** This class implements a Mode that interpretes user inputs as
 *  selecting one or more figure. Clicking on a 
 *  figure will select it.
 *  
 *  <h3>Mouse Interaction</h3>
 *  <table border=1>
 *  <thead>
 *  <tr><th>Mouse Events</th><th>Action</th></tr>
 *  </thead>
 *  <tbody>
 *  <tr><td>Move on figures</td><td>Change the mouse cursor to indicates if they
 *  are resized by the user.</td></tr>
 *  <tr><td>Left click on figure</td><td>Select the figure. See below for details on the selection behavior.</td></tr>
 *  <tr><td>Left click on resizing handler</td><td>If selected objects, delegate to {@link ResizeMode}.</td></tr>
 *  <tr><td>Left click on block figure and drag</td><td>Select the block figures if not already, and delegate to {@link MoveMode}.</td></tr>
 *  <tr><td>Left click on edge control point and drag</td><td>Select the edge and delegate to {@link ControlPointMoveMode}.</td></tr>
 *  <tr><td>Left click on edge, outside control point, and drag</td><td>Create a control point and delegate to {@link ControlPointMoveMode}.</td></tr>
 *  <tr><td>Left click on background</td><td>Clear the selection</td></tr>
 *  <tr><td>Left click on background and drag</td><td>Draw the selection rectangle</td></tr>
 *  <tr><td>Left double-click on block figure</td><td>Fire action-performed event</td></tr>
 *  <tr><td>Right click everywhere</td><td>Fire popup-performed event</td></tr>
 *  </tbody>
 *  </table>
 *  
 *  <h3>Keyboard Interaction</h3>
 *  <table border=1>
 *  <thead>
 *  <tr><th>Keys</th><th>Action</th></tr>
 *  </thead>
 *  <tbody>
 *  </tbody>
 *  </table>
 *  
 *  <h3>Deletion Behavior</h3>
 *  <h4>Method 1: Conditional deletion of the figure and its model object</h4>
 *  The figure f is associated to the model object a.
 *  <p>
 *  If the parameter <var>isAlwaysRemovingModelObjects<var>, passed as 
 *  parameter of the constructors, is <code>true</code>; then the
 *  method 2 is run in place of the method 1.
 *  <p>
 *  If the parameter <var>isAlwaysRemovingModelObjects<var>, passed as 
 *  parameter of the constructors, is <code>false</code>; then only
 *  the figure f is removed from the viewer. The model object a remains
 *  inside the associated graph model.
 *   
 *  <h4>Method 2: Inconditional deletion of the figure and its model object</h4>
 *  The figure f is associated to the model object a.
 *  <p>
 *  The figure f is deleted from the viewer.
 *  The model object a is removed from the graph model.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BaseMode extends ActionMode<Figure,DroidViewGraphics2D,Color> {

	private DifferedPointerEvent onPressedEvent = null;
	private boolean isSelectedWhenPressed;
	private ResizeDirection initiatedResizingDirection = null;

	private Paint hatchPaint = null;
	private Paint foregroundPaint = null;
	private Paint backgroundPaint = null;

	private SelectionMode selectionMode = SelectionMode.SINGLE;
	
	/** Construct a new BaseMode with the given parent.
	 *
	 * @param modeManager a reference to the ModeManager that
	 *                    contains this Mode.
	 */
	public BaseMode(ActionModeManager modeManager) { 
		super(modeManager);
		setPersistent(true);
	}

	/** Construct a new BaseMode. The 
	 *  {@link ActionModeManager ModeManager} should be
	 *  set before using this object.
	 */
	public BaseMode() {
		setPersistent(true);
	}

	/** Change the mode of selection.
	 *
	 * @param mode
	 */
	public void setSelectionMode(SelectionMode mode) {
		if (mode!=null) {
			this.selectionMode = mode;
		}
	}

	/** Replies the current mode of selection.
	 *
	 * @return the mode of selection.
	 */
	public SelectionMode getSelectionMode() {
		return this.selectionMode;
	}

	private Paint getHatchPaint() {
		if (this.hatchPaint==null) {
			Context context = ((ActionModeOwner)getModeManagerOwner()).getContext();
			Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(),
					org.arakhne.afc.ui.android.R.drawable.hatchs);
			BitmapShader shader = new BitmapShader(bitmap,
					TileMode.REPEAT, TileMode.REPEAT);
			this.hatchPaint = new Paint();
			this.hatchPaint.setColor(android.graphics.Color.RED);
			this.hatchPaint.setStyle(Style.FILL);
			this.hatchPaint.setShader(shader);
			this.hatchPaint.setXfermode(new PorterDuffXfermode(Mode.SRC_OVER));
		}
		return this.hatchPaint;
	}

	private Paint getForegroundPaint() {
		if (this.foregroundPaint==null) {
			this.foregroundPaint = new Paint();
			this.foregroundPaint.setStyle(Style.STROKE);
			this.foregroundPaint.setColor(getModeManagerOwner().getSelectionForeground().getRGB());
		}
		return this.foregroundPaint;
	}

	private Paint getBackgroundPaint() {
		if (this.backgroundPaint==null) {
			this.backgroundPaint = new Paint();
			this.backgroundPaint.setStyle(Style.FILL);
			this.backgroundPaint.setColor(getModeManagerOwner().getSelectionBackground().getRGB());
		}
		return this.backgroundPaint;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onModeActivated() {
		requestFocus();
	}

	/** Always <code>false</code> because I never want
	 *  to get out of this mode.
	 *
	 * @return always <code>true</code> if this mode can be dropped
	 *         from the {@link ActionModeManager} stack,
	 *         otherwise <code>false</code>.
	 */
	@Override
	public boolean canExit() {
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void cleanMode() { 
		this.onPressedEvent = null;
		this.initiatedResizingDirection = null;
	}

	/** Draw a box for the selection.
	 * The box is composed of a frame with hatch pattern and by
	 * nine small boxes.
	 * 
	 * @param g is the Android canvas to draw inside
	 * @param bounds are the bounds of the selection box.
	 * @param hatchSize is the size of the hatchs
	 * @param hatchPainter is the painter to use for hatchs.
	 * @param foreground is tghe foreground color of the small boxes.
	 * @param background is tghe background color of the small boxes.
	 */
	private static void paintSelectionBox(
			Canvas g, 
			Rectangle2f bounds, 
			float frameSize, 
			Paint hatchPainter,
			Paint foreground,
			Paint background) {
		Path path = new Path();

		float x1 = bounds.getMinX();
		float x2 = bounds.getMaxX();
		float y1 = bounds.getMinY();
		float y2 = bounds.getMaxY();

		path.moveTo(x1, y1);
		path.lineTo(x2, y1);
		path.lineTo(x2, y2);
		path.lineTo(x1, y2);
		path.lineTo(x1, y1);

		float x1o = x1 - frameSize;
		float x2o = x2 + frameSize;
		float y1o = y1 - frameSize;
		float y2o = y2 + frameSize;
		float xc1 = (x1+x2-frameSize)/2;
		float yc1 = (y1+y2-frameSize)/2;
		float xc2 = xc1 + frameSize;
		float yc2 = yc1 + frameSize;

		path.lineTo(x1o, y1o);
		path.lineTo(x1o, y2o);
		path.lineTo(x2o, y2o);
		path.lineTo(x2o, y1o);
		path.lineTo(x1o, y1o);

		path.close();

		g.drawPath(path, hatchPainter);

		g.drawRect(x1o, y1o, x1, y1, background);
		g.drawRect(x1o, y1o, x1, y1, foreground);

		g.drawRect(x1o, y2, x1, y2o, background);
		g.drawRect(x1o, y2, x1, y2o, foreground);

		g.drawRect(x2, y1o, x2o, y1, background);
		g.drawRect(x2, y1o, x2o, y1, foreground);

		g.drawRect(x2, y2, x2o, y2o, background);
		g.drawRect(x2, y2, x2o, y2o, foreground);

		g.drawRect(xc1, y1o, xc2, y1, background);
		g.drawRect(xc1, y1o, xc2, y1, foreground);

		g.drawRect(xc1, y2, xc2, y2o, background);
		g.drawRect(xc1, y2, xc2, y2o, foreground);

		g.drawRect(x1o, yc1, x1, yc2, background);
		g.drawRect(x1o, yc1, x1, yc2, foreground);

		g.drawRect(x2, yc1, x2o, yc2, background);
		g.drawRect(x2, yc1, x2o, yc2, foreground);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void paint(DroidViewGraphics2D g) {
		SelectionManager manager = (SelectionManager)getModeManagerOwner().getSelectionManager();
		if (manager.containsNoLinearFeature()) {
			Rectangle2f bounds = manager.getBounds();
			if (bounds!=null && !bounds.isEmpty()) {
				Rectangle2f selectionBox = bounds.clone();
				g.logical2pixel(selectionBox);
				paintSelectionBox(
						g.getNativeGraphics2D(),
						selectionBox,
						ActionModeManager.SELECTION_FRAME_SIZE,
						getHatchPaint(),
						getForegroundPaint(),
						getBackgroundPaint());
			}
		}
		else {
			LinearFeature linearFeature;
			Canvas canvas = g.getNativeGraphics2D();
			Paint foreground = getForegroundPaint();
			Paint background = getBackgroundPaint();
			float dfs = ActionModeManager.SELECTION_FRAME_SIZE/2f;
			float x1, y1, x2, y2;
			org.arakhne.afc.math.generic.Point2D p;
			for(Figure figure : manager) {
				linearFeature = (LinearFeature)figure;
				int max = linearFeature.getCtrlPointCount();
				for(int i=0; i<max; ++i) {
					p = linearFeature.getCtrlPointAt(i);
					x1 = g.logical2pixel_x(p.getX()) - dfs;
					y1 = g.logical2pixel_y(p.getY()) - dfs;
					x2 = x1 + ActionModeManager.SELECTION_FRAME_SIZE;
					y2 = y1 + ActionModeManager.SELECTION_FRAME_SIZE;
					canvas.drawRect(x1, y1, x2, y2, background);
					canvas.drawRect(x1, y1, x2, y2, foreground);
				}
			}
		}
	}

	private void selectFigure(Figure figure, boolean enableToggleInMultiMode) {
		SelectionManager manager = (SelectionManager)getModeManagerOwner().getSelectionManager();
		if (getModeManagerOwner().isSelectionEnabled()) {
			switch(getSelectionMode()) {
			case SINGLE:
				// Force to select one figure
				manager.setSelection(figure);
				break;
			case MULTIPLE:
				// The user may select many figures.
				// The enable-toggle flag may permits to toggle or add the given figures. 
				if (enableToggleInMultiMode) {
					if (manager.toggle(figure)) {
						repaint();
					}
				}
				else {
					if (manager.add(figure)) {
						repaint();
					}
				}

				break;
			default:
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		this.initiatedResizingDirection = null;

		float x = event.getX();
		float y = event.getY();

		SelectionManager selectionManager = (SelectionManager)getModeManagerOwner().getSelectionManager();
		Figure figure = getPointedFigure();
		if (figure!=null) {
			this.isSelectedWhenPressed = selectionManager.contains(figure);
			selectFigure(figure, false);
			if (!selectionManager.isEmpty() && !figure.isLocked()) {
				this.onPressedEvent = new DifferedPointerEvent(x, y, event, figure);
				event.consume();
			}
			else 
				this.onPressedEvent = null;
		}

		if (getModeManagerOwner().isEditable() && selectionManager.containsNoLinearFeature()) {
			Rectangle2f selectionBounds = selectionManager.getBounds();
			if (selectionBounds!=null && !selectionBounds.isEmpty()) {
				float size;
				ZoomableContext zc = getModeManagerOwner().getZoomableContext();
				if (zc!=null) size = zc.pixel2logical_size(ActionModeManager.SELECTION_FRAME_SIZE);
				else size = ActionModeManager.SELECTION_FRAME_SIZE;
				ResizeDirection direction = null;
				for(int i=0; direction==null && i<event.getPointerCount(); ++i) {
					direction = ResizeDirection.findResizingDirection(
							event.getToolArea(i),
							selectionBounds,
							size);
				}
				if (direction!=null) {
					this.initiatedResizingDirection = direction;
					if (this.onPressedEvent==null)
						this.onPressedEvent = new DifferedPointerEvent(x, y, event, figure);
					event.consume();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerDragged(ActionPointerEvent event) {
		if (getModeManagerOwner().isEditable() && this.onPressedEvent!=null) {
			if (this.initiatedResizingDirection!=null) {
				// Initiate the resizing of the selected elements
				ResizeMode resizeMode = new ResizeMode();
				createDelegation(resizeMode);
				resizeMode.setResizingDirection(this.initiatedResizingDirection);
				forcePointerEvent(this.onPressedEvent.pointerEvent);
				resizeMode.pointerPressed(
						this.onPressedEvent.pointerEvent,
						this.onPressedEvent.pressedFigure);
				forcePointerEvent(event);
				resizeMode.pointerDragged(event);
				cleanMode();
				event.consume();
			}
			else if (!this.onPressedEvent.pressedFigure.isLocked()) {
				boolean isMovable = false;
				boolean isCtrlPointMovable = false;
				Integer segmentIndex = null;

				if (this.onPressedEvent.pressedFigure instanceof BlockFigure) {
					isMovable = this.onPressedEvent.pressedFigure.isMovable();
				}
				else if (this.onPressedEvent.pressedFigure instanceof LinearFeature) {
					LinearFeature linearFeature = (LinearFeature)this.onPressedEvent.pressedFigure;
					segmentIndex = linearFeature.hitSegment(
							this.onPressedEvent.x,
							this.onPressedEvent.y,
							getClickPrecision());
					if (this.onPressedEvent.pressedFigure.isMovable() &&
							(segmentIndex==null || segmentIndex==-1)) {
						isMovable = true;
					}
					else {
						isCtrlPointMovable = true;
					}
				}

				if (isMovable) {
					// Start the move mode
					MoveMode moveMode = new MoveMode();
					createDelegation(moveMode);
					forcePointerEvent(this.onPressedEvent.pointerEvent);
					// Simulate the POINTER_PRESSED event
					moveMode.pointerPressed(
							this.onPressedEvent.pointerEvent,
							this.onPressedEvent.pressedFigure);
					forcePointerEvent(event);
					moveMode.pointerDragged(event);
					cleanMode();
					event.consume();
				}
				else if (isCtrlPointMovable) {
					// The user want to creat eor move a control point
					LinearFeature linearFeature = (LinearFeature)this.onPressedEvent.pressedFigure;

					int ctrlPointIndex = linearFeature.hitCtrlPoint(
							this.onPressedEvent.x,
							this.onPressedEvent.y,
							getClickPrecision());

					Undoable undo = null;

					if (ctrlPointIndex<0) {
						// Create a new control point
						if (segmentIndex==null) {
							// Ensure that we have the index of the segment
							segmentIndex = linearFeature.hitSegment(
									this.onPressedEvent.x,
									this.onPressedEvent.y,
									getClickPrecision());
						}
						assert(segmentIndex!=null);
						if (segmentIndex>=0) {
							// Create the new control point, and move it
							Context context = ((ActionModeOwner)getModeManagerOwner()).getContext();
							ctrlPointIndex = segmentIndex+1;
							UndoCtrlPointInsertion undoCmd = new UndoCtrlPointInsertion(
									context.getString(R.string.actionmode_inserting_ctrlpts, (segmentIndex+1)),
									linearFeature,
									ctrlPointIndex,
									this.onPressedEvent.x,
									this.onPressedEvent.y);
							undoCmd.doEdit();
							undo = undoCmd;
						}
						else {
							return;
						}
					}

					// Start the control point change mode
					ControlPointMoveMode ctrlPtsMoveMode = new ControlPointMoveMode(undo);
					createDelegation(ctrlPtsMoveMode);
					forcePointerEvent(this.onPressedEvent.pointerEvent);
					// Simulate the POINTER_PRESSED event
					ctrlPtsMoveMode.pointerPressed(
							this.onPressedEvent.pointerEvent,
							ctrlPointIndex,
							this.onPressedEvent.pressedFigure);
					forcePointerEvent(event);
					ctrlPtsMoveMode.pointerDragged(event);
					cleanMode();
					event.consume();
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerReleased(ActionPointerEvent event) {
		// Toggle the hit figure because not submode was called by POINTER_DRAGGED.
		if (this.onPressedEvent!=null && this.onPressedEvent.pressedFigure!=null
				&& this.isSelectedWhenPressed
				&& getSelectionMode()==SelectionMode.MULTIPLE) {
			selectFigure(this.onPressedEvent.pressedFigure, true);
		}
		if (isPersistent()) cleanMode();
		else done();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerLongClicked(ActionPointerEvent event) {
		SelectionManager sm = (SelectionManager)getModeManagerOwner().getSelectionManager();
		Figure pointedFigure = getPointedFigure();
		if (pointedFigure!=null && !event.isContextualActionTriggered()) {
			if (!sm.contains(pointedFigure)) {
				sm.setSelection(pointedFigure);
			}
			getModeManager().fireActionPerformed(sm, event);
		}

		getModeManager().firePopupPerformed(event, pointedFigure);
	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class UndoCtrlPointInsertion extends AbstractUndoable {

		private static final long serialVersionUID = 264956281928033074L;

		private final String label;
		private final float x;
		private final float y;
		private final int index;
		private final LinearFeature figure;

		/**
		 * @param label
		 * @param figure
		 * @param pointIndex
		 * @param x
		 * @param y
		 */
		public UndoCtrlPointInsertion(String label, LinearFeature figure, int pointIndex, float x, float y) {
			this.label = label;
			this.figure = figure;
			this.index = pointIndex;
			this.x = x;
			this.y = y;
		}

		@Override
		public void doEdit() {
			this.figure.insertCtrlPointAt(
					this.index,
					this.x,
					this.y);
		}

		@Override
		public void undoEdit() {
			this.figure.removeCtrlPointAt(this.index);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return this.label;
		}

	} // class UndoCtrlPointInsertion

}