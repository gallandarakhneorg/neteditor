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

import java.awt.Color;
import java.awt.Paint;
import java.awt.TexturePaint;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.ImageIO;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.afc.ui.ZoomableContext;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.actionmode.ActionModeManagerOwner;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.awt.AwtUtil;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.event.KeyEvent;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.vmutil.Resources;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.view.LinearFeature;
import org.arakhne.neteditor.swing.actionmode.ActionModeUtil;
import org.arakhne.neteditor.swing.actionmode.DifferedMouseEvent;

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
 *  <tr><td>{@link java.awt.event.KeyEvent#VK_ESCAPE ESCAPE}</td><td>Close all the additional 
 *  mode and restore this base mode in its standard behavior.</td></tr>
 *  <tr><td>{@link java.awt.event.KeyEvent#VK_DELETE DELETE}</td><td>Delete the selected
 *  objects according to method 1.
 *  See below for details on the deletion behavior.</td></tr>
 *  <tr><td>{@link java.awt.event.KeyEvent#VK_CONTROL CTRL}+{@link java.awt.event.KeyEvent#VK_DELETE DELETE}</td>
 *  <td>Delete the selected objects according to method 2.
 *  See below for details on the deletion behavior.</td></tr>
 *  <tr><td>{@link java.awt.event.KeyEvent#VK_CONTROL CTRL}+{@link java.awt.event.KeyEvent#VK_D D}</td>
 *  <td>Delete the selected objects according to method 2.
 *  See below for details on the deletion behavior.</td></tr>
 *  <tr><td>{@link java.awt.event.KeyEvent#VK_TAB TAB}</td>
 *  <td>Select the next figure in the layers.</td></tr>
 *  <tr><td>{@link java.awt.event.KeyEvent#VK_TAB SHIFT}+{@link java.awt.event.KeyEvent#VK_TAB TAB}</td>
 *  <td>Select the previous figure in the layers.</td></tr>
 *  <tr><td>{@link java.awt.event.KeyEvent#VK_CONTROL CTRL}+{@link java.awt.event.KeyEvent#VK_A A}</td>
 *  <td>Select all the figures.</td></tr>
 *  </tbody>
 *  </table>
 *  
 *  <h3>Deletion Behavior</h3>
 *  Behavior of the objects selection may differ depending on the addition key you have
 *  pressed in parallel to the mouse action:<ul>
 *  <li><strong>No additional key</strong>: the previous selection is cleared, and then the object is selected.</li>
 *  <li><strong>{@link java.awt.event.KeyEvent#VK_CONTROL CONTROL}</strong>: the object is toggled in the current selection.</li>
 *  <li><strong>{@link java.awt.event.KeyEvent#VK_SHIFT SHIFT}</strong>: the object is added in the current selection, ie the previous selection remains unchanged.</li>
 *  </ul> 
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
public class BaseMode extends ActionMode<Figure,VirtualScreenGraphics2D,java.awt.Color> {

	private static final int SELECTION_FRAME_SIZE = 8;
	private static final TexturePaint SELECTION_PAINT;

	static {
		try {
			URL url = Resources.getResource(BaseMode.class, "hatchs.png"); //$NON-NLS-1$
			BufferedImage image = ImageIO.read(url);
			int w = image.getWidth();
			if (w<0) w = SELECTION_FRAME_SIZE;
			int h = image.getHeight();
			if (h<0) h = SELECTION_FRAME_SIZE;
			SELECTION_PAINT = new TexturePaint(image,
					new Rectangle2D.Float(0, 0, w, h));
		}
		catch(Throwable e) {
			throw new Error(e);
		}
	}

	private volatile Rectangle2D selectRect = null;
	private volatile Point2D selectRectAnchor = null;
	private volatile DifferedMouseEvent onPressedEvent = null;
	private volatile ResizeDirection initiatedResizing = null;

	private int figureFocus = -1;

	/** Construct a new BaseMode with the given parent.
	 *
	 * @param modeManager a reference to the ModeManager that
	 *                    contains this Mode.
	 */
	public BaseMode(ActionModeManager<Figure,VirtualScreenGraphics2D,java.awt.Color> modeManager) { 
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
	public void paint(VirtualScreenGraphics2D g) {
		if (this.selectRect!=null && !this.selectRect.isEmpty()) {
			Color border = getModeManagerOwner().getSelectionBackground();
			Color background = AwtUtil.makeTransparentColor(border);
			g.setColor(background);
			g.fill(this.selectRect);
			g.setColor(border);
			g.draw(this.selectRect);
		}

		SelectionManager manager = (SelectionManager)getModeManagerOwner().getSelectionManager();
		if (manager.containsNoLinearFeature()) {
			Rectangle2f bounds = manager.getBounds();
			if (bounds!=null && !bounds.isEmpty()) {
				float size = g.pixel2logical_size(SELECTION_FRAME_SIZE);
				paintSelectionFrame(g, bounds, size);
				paintSelectionBlocks(g, bounds, size);
			}
		}
		else if (manager.containsLinearFeature()) {
			float size = g.pixel2logical_size(SELECTION_FRAME_SIZE);
			for(Figure figure : manager) {
				paintSelectedControlPoints(g, (LinearFeature)figure, size);
			}
		}
	}

	private void paintSelectedControlPoints(VirtualScreenGraphics2D g, LinearFeature figure, float frameSize) {
		float x, y;
		Color background = getModeManagerOwner().getSelectionBackground();
		Color foreground = getModeManagerOwner().getSelectionForeground();
		org.arakhne.afc.math.generic.Point2D p;
		int max = figure.getCtrlPointCount();
		for(int i=0; i<max; ++i) {
			p = figure.getCtrlPointAt(i);
			x = p.getX() - frameSize/2;
			y = p.getY() - frameSize/2;
			paintSelectionBlock(g, x, y, frameSize, foreground, background);
		}
	}

	private static void paintSelectionFrame(VirtualScreenGraphics2D g, Rectangle2f bounds, double frameSize) {
		GeneralPath path = new GeneralPath();

		double x1 = bounds.getMinX();
		double x2 = bounds.getMaxX();
		double y1 = bounds.getMinY();
		double y2 = bounds.getMaxY();

		path.moveTo(x1, y1);
		path.lineTo(x2, y1);
		path.lineTo(x2, y2);
		path.lineTo(x1, y2);
		path.lineTo(x1, y1);

		double x1o = x1 - frameSize;
		double x2o = x2 + frameSize;
		double y1o = y1 - frameSize;
		double y2o = y2 + frameSize;

		path.lineTo(x1o, y1o);
		path.lineTo(x1o, y2o);
		path.lineTo(x2o, y2o);
		path.lineTo(x2o, y1o);
		path.lineTo(x1o, y1o);

		path.closePath();

		Paint oldPaint = g.getPaint();
		g.setPaint(SELECTION_PAINT);
		g.fill(path);
		g.setPaint(oldPaint);
	}

	private void paintSelectionBlocks(VirtualScreenGraphics2D g, Rectangle2f bounds, float frameSize) {
		float x1 = bounds.getMinX();
		float x2 = bounds.getMaxX();
		float y1 = bounds.getMinY();
		float y2 = bounds.getMaxY();
		float x1o = x1 - frameSize;
		float y1o = y1 - frameSize;
		float xc = (x1+x2-frameSize)/2;
		float yc = (y1+y2-frameSize)/2;

		Color background = getModeManagerOwner().getSelectionBackground();
		Color foreground = getModeManagerOwner().getSelectionForeground();

		paintSelectionBlock(g, x1o, y1o, frameSize, foreground, background);
		paintSelectionBlock(g, x1o, y2, frameSize, foreground, background);
		paintSelectionBlock(g, x2, y1o, frameSize, foreground, background);
		paintSelectionBlock(g, x2, y2, frameSize, foreground, background);

		paintSelectionBlock(g, xc, y1o, frameSize, foreground, background);
		paintSelectionBlock(g, xc, y2, frameSize, foreground, background);

		paintSelectionBlock(g, x1o, yc, frameSize, foreground, background);
		paintSelectionBlock(g, x2, yc, frameSize, foreground, background);
	}

	private static void paintSelectionBlock(VirtualScreenGraphics2D g, float x, float y, float frameSize, Color foreground, Color background) {
		g.setColor(background);
		g.fillRect(x, y, frameSize, frameSize);
		g.setColor(foreground);
		g.drawRect(x, y, frameSize, frameSize);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void cleanMode() { 
		setExclusive( false );
		this.selectRect = null;
		this.selectRectAnchor = null;
		this.initiatedResizing = null;
		this.onPressedEvent = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerPressed(ActionPointerEvent event) {
		if (event.getButton()==1) {
			float x = event.getX();
			float y = event.getY();

			SelectionManager manager = (SelectionManager)getModeManagerOwner().getSelectionManager();

			Figure figure = getPointedFigure();
			if (figure!=null) {
				this.selectRect = null;
				this.selectRectAnchor = null;
				this.initiatedResizing = null;
				selectFigure(event, figure);
				if (!figure.isLocked())
					this.onPressedEvent = new DifferedMouseEvent(x, y, event, figure);
				else
					this.onPressedEvent = null;
			}
			else {
				this.initiatedResizing = null;
				if (getModeManagerOwner().isEditable() && manager.containsNoLinearFeature()) {
					Rectangle2f selectionBounds = manager.getBounds();
					if (selectionBounds!=null && !selectionBounds.isEmpty()) {
						float size;
						ZoomableContext zc = getModeManagerOwner().getZoomableContext();
						if (zc!=null) size = zc.pixel2logical_size(SELECTION_FRAME_SIZE);
						else size = SELECTION_FRAME_SIZE;
						ResizeDirection direction = ResizeDirection.findResizingDirection(
								event.getX(),
								event.getY(),
								selectionBounds,
								size);
						if (direction!=null) {
							this.initiatedResizing = direction;
							this.selectRect = null;
							this.selectRectAnchor = null;
							this.onPressedEvent = new DifferedMouseEvent(x, y, event, figure);
						}
					}
				}
				if (this.initiatedResizing==null) {
					this.selectRect = new Rectangle2D.Float(x,y,0f,0f);
					this.selectRectAnchor = new Point2D.Float(x, y);
					this.onPressedEvent = null;
					this.initiatedResizing = null;
				}
			}

		}
		// PopupTriggering may occurs during "pressed", "release", or "click" events
		// depending on the operating system behavior.
		else if (event.isContextualActionTriggered()) {
			getModeManager().firePopupPerformed(event, getPointedFigure());
		}
		event.consume();
	}

	private void selectFigure(ActionPointerEvent event, Figure figure) {
		boolean changed = false;
		SelectionManager manager = (SelectionManager)getModeManagerOwner().getSelectionManager();
		if (getModeManagerOwner().isSelectionEnabled()) {
			if (event.isShiftDown()) {
				changed = manager.add(figure);
			}
			else if (event.isControlDown()) {
				if (manager.containsNoLinearFeature()
						||(!(figure instanceof LinearFeature))
						||figure.isLocked()
						||!figure.isMovable()) {
					changed = manager.toggle(figure);
				}
			}
			else if (!manager.contains(figure)) {
				changed = manager.setSelection(figure);
			}
		}
		if (changed) repaint();
	}

	private void updateVisualIndicators(float x, float y, boolean isCtrlDown) {
		MouseCursor c = null;

		if (getModeManagerOwner().isEditable()) {
			//
			// Try to detect the overlapping of the mouse cursor
			// on a resizing handle for a selected figure
			//
			ResizeDirection resizingDirection = null;
			SelectionManager manager = (SelectionManager)getModeManagerOwner().getSelectionManager();
			if (manager.containsNoLinearFeature()) {
				Rectangle2f selectionBounds = manager.getBounds();
				if (selectionBounds!=null && !selectionBounds.isEmpty()) {
					float size;
					ZoomableContext zc = getModeManagerOwner().getZoomableContext();
					if (zc!=null) size = zc.pixel2logical_size(SELECTION_FRAME_SIZE);
					else size = SELECTION_FRAME_SIZE;
					resizingDirection = ResizeDirection.findResizingDirection(
							x,
							y,
							selectionBounds,
							size);
				}
			}
			else if (isCtrlDown) {
				Iterator<Figure> figureIterator = manager.iterator();
				Circle2f circle = new Circle2f(x, y, getClickPrecision());
				LinearFeature linearFeature;
				while(c==null && figureIterator.hasNext()) {
					linearFeature = (LinearFeature)figureIterator.next();
					if (linearFeature.intersects(circle)) {
						c = MouseCursor.MOVE;
					}
				}
			}
			else {
				Iterator<Figure> figureIterator = manager.iterator();
				LinearFeature linearFeature;
				while(c==null && figureIterator.hasNext()) {
					linearFeature = (LinearFeature)figureIterator.next();
					int index = linearFeature.hitCtrlPoint(x, y, getClickPrecision());
					if (index>=0) {
						c = MouseCursor.MOVE;
					}
				}
			}

			if (resizingDirection!=null) {
				c = ActionModeUtil.getResizingCursor(resizingDirection);
			}
		}

		setCursor(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerMoved(ActionPointerEvent event) {
		updateVisualIndicators(event.getX(), event.getY(), event.isControlDown());
		event.consume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerDragged(ActionPointerEvent event) {
		if (this.selectRect!=null) {
			assert(this.selectRectAnchor!=null);
			float x = event.getX();
			float y = event.getY();
			this.selectRect.setFrameFromDiagonal(
					x, y, 
					this.selectRectAnchor.getX(),
					this.selectRectAnchor.getY());
			repaint();
		}
		else if (getModeManagerOwner().isEditable() && this.onPressedEvent!=null) {
			if (this.initiatedResizing!=null) {
				// Initiate the resizing of the selected elements
				ResizeMode resizeMode = new ResizeMode();
				createDelegation(resizeMode);
				resizeMode.setResizingDirection(this.initiatedResizing);
				forcePointerEvent(this.onPressedEvent.pointerEvent);
				resizeMode.pointerPressed(this.onPressedEvent.pointerEvent);
				forcePointerEvent(event);
				resizeMode.pointerDragged(event);
				cleanMode();
			}
			else if (!this.onPressedEvent.pressedFigure.isLocked()) {
				boolean isMovable = false;
				boolean isCtrlMovable = false;
				Integer segmentIndex = null;

				if (this.onPressedEvent.pressedFigure instanceof BlockFigure) {
					isMovable = this.onPressedEvent.pressedFigure.isMovable();
				}
				else if (this.onPressedEvent.pressedFigure instanceof LinearFeature) {
					LinearFeature linearFeature = (LinearFeature)this.onPressedEvent.pressedFigure;
					if (!event.isControlDown()) {
						segmentIndex = linearFeature.hitSegment(
								this.onPressedEvent.x,
								this.onPressedEvent.y,
								getClickPrecision());
					}
					if (this.onPressedEvent.pressedFigure.isMovable() &&
							(segmentIndex==null || segmentIndex==-1)) {
						isMovable = true;
					}
					else {
						isCtrlMovable = true;
					}
				}

				if (isMovable) {
					// Start the move mode
					MoveMode moveMode = new MoveMode();
					createDelegation(moveMode);
					forcePointerEvent(this.onPressedEvent.pointerEvent);
					moveMode.pointerPressed(this.onPressedEvent.pointerEvent);
					forcePointerEvent(event);
					moveMode.pointerDragged(event);
					cleanMode();
				}
				else if (isCtrlMovable) {
					LinearFeature linearFeature = (LinearFeature)this.onPressedEvent.pressedFigure;

					int ctrlPointIndex = linearFeature.hitCtrlPoint(
							this.onPressedEvent.x,
							this.onPressedEvent.y,
							getClickPrecision());

					Undoable undo = null;

					if (ctrlPointIndex<0) {
						// Create a new control point
						if (segmentIndex==null) {
							segmentIndex = linearFeature.hitSegment(
									this.onPressedEvent.x,
									this.onPressedEvent.y,
									getClickPrecision());
						}
						assert(segmentIndex!=null);
						if (segmentIndex>=0) {
							UndoCtrlPointInsertion undoCmd = new UndoCtrlPointInsertion(
									linearFeature,
									segmentIndex+1,
									this.onPressedEvent.x,
									this.onPressedEvent.y);
							undoCmd.doEdit();
							undo = undoCmd;
						}
						else {
							event.consume();
							return;
						}
					}

					// Start the control point change mode
					ControlPointMoveMode moveMode = new ControlPointMoveMode(undo);
					createDelegation(moveMode);
					forcePointerEvent(this.onPressedEvent.pointerEvent);
					moveMode.pointerPressed(this.onPressedEvent.pointerEvent);
					forcePointerEvent(event);
					moveMode.pointerDragged(event);
					cleanMode();
				}
			}
		}
		event.consume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerReleased(ActionPointerEvent event) {
		if (event.getButton()==1) {
			this.onPressedEvent = null;
			this.initiatedResizing = null;
			if (this.selectRect!=null) {
				Rectangle2D bounds = this.selectRect;
				Point2D anchor = this.selectRectAnchor;
				this.selectRect = null;
				this.selectRectAnchor = null;
				assert(anchor!=null);
				float x = event.getX();
				float y = event.getY();
				bounds.setFrameFromDiagonal(
						x, y, 
						anchor.getX(), anchor.getY());

				ActionModeManagerOwner<Figure,VirtualScreenGraphics2D,java.awt.Color> mc = getModeManagerOwner();
				assert(mc!=null);
				SelectionManager sm = (SelectionManager)mc.getSelectionManager();
				if (!bounds.isEmpty() && mc.isSelectionEnabled()) {
					Set<Figure> inBounds = mc.getFiguresIn(new Rectangle2f(
							(float)bounds.getMinX(), (float)bounds.getMinY(),
							(float)bounds.getWidth(), (float)bounds.getHeight()));
					if (!inBounds.isEmpty()) {
						if (event.isShiftDown()) {
							sm.addAll(inBounds);
						}
						else if (event.isControlDown()) {
							sm.toggle(inBounds);
						}
						else {
							sm.setSelection(inBounds);
						}
					}
					repaint();
				}
				else {
					sm.clear();
					repaint();
				}
			}
		}
		// PopupTriggering may occurs during "pressed", "release", or "click" events
		// depending on the operating system behavior.
		else if (event.isContextualActionTriggered()) {
			getModeManager().firePopupPerformed(event, getPointedFigure());
		}
		if (isPersistent()) cleanMode();
		else done();
		event.consume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void pointerClicked(ActionPointerEvent event) {
		if (event.getButton()==1
				&& event.getClickCount()==2) {
			ActionModeManager<Figure,?,?> mm = getModeManager();
			SelectionManager sm = (SelectionManager)getModeManagerOwner().getSelectionManager();
			if (!sm.isEmpty()) {
				mm.fireActionPerformed(sm, event);
			}
		}
		// PopupTriggering may occurs during "pressed", "release", or "click" events
		// depending on the operating system behavior.
		if (event.isContextualActionTriggered()) {
			getModeManager().firePopupPerformed(event, getPointedFigure());
		}
		event.consume();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		if (java.awt.event.KeyEvent.VK_CONTROL==e.getKeyCode() ||
				java.awt.event.KeyEvent.VK_SHIFT==e.getKeyCode() ||
				java.awt.event.KeyEvent.VK_ALT==e.getKeyCode() ||
				java.awt.event.KeyEvent.VK_META==e.getKeyCode()) {
			ActionPointerEvent pEvent = getModeManager().getLastPointerEvent();
			if (pEvent!=null) {
				updateVisualIndicators(pEvent.getX(), pEvent.getY(), e.isControlDown());
				e.consume();
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void keyPressed(KeyEvent e) {
		if (java.awt.event.KeyEvent.VK_CONTROL==e.getKeyCode() ||
				java.awt.event.KeyEvent.VK_SHIFT==e.getKeyCode() ||
				java.awt.event.KeyEvent.VK_ALT==e.getKeyCode() ||
				java.awt.event.KeyEvent.VK_META==e.getKeyCode()) {
			ActionPointerEvent pEvent = getModeManager().getLastPointerEvent();
			if (pEvent!=null) {
				updateVisualIndicators(pEvent.getX(), pEvent.getY(), e.isControlDown());
				e.consume();
			}
		}
		else if (java.awt.event.KeyEvent.VK_ESCAPE==e.getKeyCode()) {
			getModeManager().resetModes();
			e.consume();
		}
		else if (java.awt.event.KeyEvent.VK_CUT==e.getKeyCode() ||
				(java.awt.event.KeyEvent.VK_X==e.getKeyCode() && e.isControlDown())) {
			getModeManagerOwner().cut();
			e.consume();
		}
		else if (java.awt.event.KeyEvent.VK_COPY==e.getKeyCode() ||
				(java.awt.event.KeyEvent.VK_C==e.getKeyCode() && e.isControlDown())) {
			getModeManagerOwner().copy();
			e.consume();
		}
		else if (java.awt.event.KeyEvent.VK_PASTE==e.getKeyCode() ||
				(java.awt.event.KeyEvent.VK_V==e.getKeyCode() && e.isControlDown())) {
			getModeManagerOwner().paste();
			e.consume();
		}
		else if (java.awt.event.KeyEvent.VK_TAB==e.getKeyCode()) {
			if (getModeManagerOwner().isSelectionEnabled()) {
				if (this.figureFocus<0) {
					this.figureFocus = getModeManagerOwner().getFigureCount()-1;
				}
				else if (e.isShiftDown()) {
					++this.figureFocus;
					if (this.figureFocus>=getModeManagerOwner().getFigureCount())
						this.figureFocus = 0;
				}
				else {
					--this.figureFocus;
					if (this.figureFocus<0)
						this.figureFocus = getModeManagerOwner().getFigureCount() - 1;
				}
				Figure f = getModeManagerOwner().getFigureAt(this.figureFocus);
				getModeManagerOwner().getSelectionManager().setSelection(f);
				e.consume();
			}
		}
		else if (java.awt.event.KeyEvent.VK_A==e.getKeyCode() && e.isControlDown()) {
			ActionModeManagerOwner<Figure,?,?> container = getModeManagerOwner();
			if (container.isSelectionEnabled()) {
				container.getSelectionManager().setSelection(container.getFigures());
				e.consume();
			}
		}
		else if (getModeManagerOwner().isEditable()) {
			boolean isModelDeletion, isFigureDeletion;

			if (getModeManagerOwner().isAlwaysRemovingModelObjects()) {
				isModelDeletion = (java.awt.event.KeyEvent.VK_DELETE==e.getKeyCode())
						||
						(e.isControlDown() && java.awt.event.KeyEvent.VK_D==e.getKeyCode());
				isFigureDeletion = false;
			}
			else {
				isModelDeletion = (e.isControlDown() &&
						((java.awt.event.KeyEvent.VK_DELETE==e.getKeyCode())
								||
								(java.awt.event.KeyEvent.VK_D==e.getKeyCode())));
				isFigureDeletion = !isModelDeletion && (java.awt.event.KeyEvent.VK_DELETE==e.getKeyCode());
			}

			if (isModelDeletion || isFigureDeletion) {
				Undoable edit = getModeManagerOwner().removeFigures(isModelDeletion, true, (SelectionManager)getModeManagerOwner().getSelectionManager());
				getModeManagerOwner().getUndoManager().add(edit);
				e.consume();
			}
		}
	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class UndoCtrlPointInsertion extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = 264956281928033074L;

		private final float x;
		private final float y;
		private final int index;
		private final LinearFeature figure;

		/**
		 * @param figure
		 * @param pointIndex
		 * @param x
		 * @param y
		 */
		public UndoCtrlPointInsertion(LinearFeature figure, int pointIndex, float x, float y) {
			this.figure = figure;
			this.index = pointIndex;
			this.x = x;
			this.y = y;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSignificant() {
			return false;
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
			return Locale.getString(BaseMode.class, "CTRL_POINT_INSERTION_PRESENTATION_NAME", (this.index+1)); //$NON-NLS-1$
		}

	} // class UndoCtrlPointInsertion

}