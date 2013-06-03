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
package org.arakhne.neteditor.android.activity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EventListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

import org.arakhne.afc.ui.android.filechooser.FileChooser;
import org.arakhne.afc.ui.android.zoom.ZoomableView;
import org.arakhne.afc.io.filefilter.FileFilter;
import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Vector2D;
import org.arakhne.afc.ui.CenteringTransform;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.afc.ui.ZoomableContext;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeListener;
import org.arakhne.afc.ui.actionmode.SelectableInteractionListener;
import org.arakhne.afc.ui.event.PointerEvent;
import org.arakhne.afc.ui.selection.SelectionEvent;
import org.arakhne.afc.ui.selection.SelectionListener;
import org.arakhne.afc.ui.undo.AbstractUndoable;
import org.arakhne.afc.ui.undo.DefaultUndoManager;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.undo.UndoableGroup;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.VectorGraphics2D;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.afc.util.ListenerCollection;
import org.arakhne.neteditor.android.R;
import org.arakhne.neteditor.android.actionmode.ActionModeManager;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.actionmode.FigureActionModeManager;
import org.arakhne.neteditor.android.actionmode.SelectionMode;
import org.arakhne.neteditor.android.actionmode.base.BaseMode;
import org.arakhne.neteditor.android.event.FigureEvent;
import org.arakhne.neteditor.android.event.FigureListener;
import org.arakhne.neteditor.android.filechooser.NetEditorFileChooserIconSelector;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.factory.CollisionAvoider;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.graphics.ViewGraphicsUtil;
import org.arakhne.neteditor.fig.graphics.ViewGraphicsUtil.Factory;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ModelObjectView;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.fig.view.ViewComponentBindingListener;
import org.arakhne.neteditor.fig.view.ViewComponentChangeEvent;
import org.arakhne.neteditor.fig.view.ViewComponentChangeListener;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.fig.view.ViewComponentLayoutListener;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeEvent;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeListener;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.ModelObjectListener;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.formalism.View;
import org.arakhne.neteditor.formalism.ViewBinding;
import org.arakhne.vmutil.locale.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;

/** This view permits to draw NetEditor figures on
 * an Droid view. 
 * 
 * @param <G> is the type of the graph supported by this editor.
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FigureView<G extends Graph<?,?,?,?>> extends ZoomableView  implements ViewComponentContainer<Figure,G> {

	private static final long serialVersionUID = 5915723431226736943L;

	/** Precision in pixels for the clicking action.
	 */
	public static final int CLICK_PRECISION = 10;

	static {
		ViewGraphicsUtil.setFactory(new ViewGraphicsFactory());
	}

	/** Lock resource that may be used for deletion actions.
	 */
	protected final ReentrantLock changeLock = new ReentrantLock();

	private final UUID viewID = UUID.randomUUID();

	private volatile LinkedList<Figure> figures = new LinkedList<Figure>();

	private SoftReference<Rectangle2f> documentBounds = null;

	private G graph; 

	private FigureFactory<G> figureFactory = null;

	private boolean isFigureAutoAdded = true;

	private float hitPrecision = CLICK_PRECISION;

	private final CollisionAvoider collisionAvoider = new CAvoider();
	private SelectionManager selectionManager = new SelectionManager();
	private UndoManager undoManager = new DefaultUndoManager();
	private ModeManagerWrapper mode = new ModeManagerWrapper();

	private boolean isShadowDraw = true;
	private boolean isEditable = true;
	private boolean isAlwaysRemovingModelObjects = true;
	private boolean skipFigureModelUnlink = false;
	private boolean isSelectionEnabled = true;

	/** List of listeners associated with this view.
	 */
	protected final ListenerCollection<EventListener> listenerList = new ListenerCollection<EventListener>();
	
	private int backgroundColor;
	private int backgroundSelectionColor;
	private int foregroundSelectionColor;
	
	/**
	 * @param context is the droid context in which the view is displayed.
	 */
	public FigureView(Context context) {
		this(context, null, 0);
	}

	/**
	 * @param context is the droid context in which the view is displayed.
	 * @param attrs are the attributes of the view.
	 */
	public FigureView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	/**
	 * @param context is the droid context in which the view is displayed.
	 * @param attrs are the attributes of the view.
	 * @param defStyle is the style of the view.
	 */
	public FigureView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.backgroundColor = context.getResources().getColor(android.R.color.background_light);
		this.backgroundSelectionColor = context.getResources().getColor(R.color.figureviewer_selection_background);
		this.foregroundSelectionColor = context.getResources().getColor(R.color.figureviewer_selection_foreground);
		this.selectionManager.addSelectionListener(getEventHandler(SelectionListener.class));
		setLongClickable(true);
	}
	
	/** Replies if the selection manager is enabled.
	 * 
	 * @return <code>true</code> if the selection manager is enabled.
	 */
	public boolean isSelectionEnabled() {
		return this.isSelectionEnabled;
	}
	
	/** Set if the selection manager is enabled.
	 * 
	 * @param enable is <code>true</code> if the selection manager is enabled.
	 */
	public void setSelectionEnabled(boolean enable) {
		if (enable!=this.isSelectionEnabled) {
			this.isSelectionEnabled = enable;
			if (!this.isSelectionEnabled)
				this.selectionManager.clear();
		}
	}

	/** Replies if the mode container want to remove the model
	 * objects when a figure is removed from the view.
	 * 
	 * @return <code>true</code> if both the figure and the
	 * model objects should be removed at the same time,
	 * <code>false</code> if only the figure should be removed
	 * (not the model object).
	 */
	public boolean isAlwaysRemovingModelObjects() {
		return this.isAlwaysRemovingModelObjects;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EventHandler createEventHandler() {
		return new EventHandler();
	}

	/** Replies the selection manager.
	 * 
	 * @return the selection manager.
	 */
	public SelectionManager getSelectionManager() {
		return this.selectionManager;
	}

	/** Replies the undo/redo manager.
	 * 
	 * @return the undo/redo manager.
	 */
	public UndoManager getUndoManager() {
		return this.undoManager;
	}

	/** Set the selection manager.
	 * 
	 * @param manager is the new selection manager.
	 */
	public void setSelectionManager(SelectionManager manager) {
		if (manager!=null && manager!=this.selectionManager) {
			this.selectionManager.removeSelectionListener(getEventHandler(SelectionListener.class));
			this.selectionManager = manager;
			this.selectionManager.addSelectionListener(getEventHandler(SelectionListener.class));
		}
	}

	/** Replies the UUId of the view.
	 * 
	 * @return the uuid of the view.
	 */
	@Override
	public final UUID getUUID() {
		return this.viewID;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected float getPreferredFocusX() {
		Rectangle2f r = getDocumentRect();
		if (r!=null) return r.getCenterX();
		return 0f;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected float getPreferredFocusY()  {
		Rectangle2f r = getDocumentRect();
		if (r!=null) return r.getCenterY();
		return 0f;
	}


	/** Replies the object that permits to avoid collision between a 
	 * figure to insert inside and the already present figures.
	 * @return the collision avoider.
	 */
	public CollisionAvoider getCollisionAvoider() {
		return this.collisionAvoider;
	}

	/** Replies if the figure are automatically added into the view
	 * when a model object was added in the graph.
	 * The figures are created with the figure factory
	 * replied by {@link #getFigureFactory()}.
	 * 
	 * @return <code>true</code> if the figures are automatically added;
	 * otherwise <code>false</code>.
	 */
	public boolean isFigureAutomaticallyAdded() {
		return this.isFigureAutoAdded;
	}

	/** Replies if the figure are automatically added into the view
	 * when a model object was added in the graph.
	 * The figures are created with the figure factory
	 * replied by {@link #getFigureFactory()}.
	 * 
	 * @param auto is <code>true</code> if the figures are automatically added;
	 * otherwise <code>false</code>.
	 */
	public void setFigureAutomaticallyAdded(boolean auto) {
		this.isFigureAutoAdded = auto;
	}

	/** {@inheritDoc}
	 */
	@Override
	public FigureFactory<G> getFigureFactory() {
		return this.figureFactory;
	}

	/** Set the figure factory used by this panel.
	 * 
	 * @param factory is the new figure factory.
	 */
	public void setFigureFactory(FigureFactory<G> factory) {
		if (factory!=null && this.figureFactory!=factory) {
			if (this.figureFactory!=null) {
				this.figureFactory.removeCollisionAvoider(this.collisionAvoider);
			}
			this.figureFactory = factory;
			if (this.figureFactory!=null) {
				this.figureFactory.addCollisionAvoider(this.collisionAvoider);
			}
		}
	}

	/**
	 * Replies the graph displayed by this view.
	 * 
	 * @return the graph.
	 */
	public G getGraph() {
		try {
			this.changeLock.lock();
			return this.graph;
		}
		finally {
			this.changeLock.unlock();
		}
	}

	/** Set the graph associated to this panel.
	 * 
	 * @param g is the graph.
	 */
	public void setGraph(G g) {
		try {
			this.changeLock.lock();
			if (g!=this.graph) {
				this.selectionManager.clear();
				ModelObjectListener eh = getEventHandler(ModelObjectListener.class);
				if (this.graph!=null) {
					this.graph.removeModelObjectListener(eh);
					for(ModelObjectListener l : this.listenerList.getListeners(ModelObjectListener.class)) {
						this.graph.removeModelObjectListener(l);
					}
				}
				Iterator<Figure> iterator = this.figures.iterator();
				Figure fig;
				while (iterator.hasNext()) {
					fig = iterator.next();
					fig.setViewComponentContainer(null);
					removeFigureListeners(fig);
					iterator.remove();
					fireFigureRemoved(fig);
				}
				this.graph = g;
				this.documentBounds = null;
				if (this.graph!=null) {
					this.graph.addModelObjectListener(eh);
					for(ModelObjectListener l : this.listenerList.getListeners(ModelObjectListener.class)) {
						this.graph.addModelObjectListener(l);
					}
				}
				repaint();
			}
		}
		finally {
			this.changeLock.unlock();
		}
	}

	/** Replies the figures in the container.
	 * 
	 * @return the figures.
	 */
	public Collection<? extends Figure> getFigures() {
		return Collections.unmodifiableList(this.figures);
	}


	/** Replies the figure that has its bounds with the specified
	 * point inside. If you want to test the point against
	 * the shape of the figures, please use
	 * {@link #getFigureAt(float, float)}.
	 * 
	 * @param x
	 * @param y
	 * @return the hit figure, or <code>null</code> if none.
	 */
	public Figure getFigureWithBoundsAt(float x, float y) {
		float precision = pixel2logical_size(getHitPrecision());
		Rectangle2f clickRect = new Rectangle2f();
		clickRect.setFromCorners(x-precision, y-precision, x+precision, y+precision);
		for(Figure figure : this.figures) {
			if (figure.intersects(clickRect)) {
				return figure;
			}
		}
		return null;
	}

	/** Replies the figures that are intersecting the specified bounds.
	 * 
	 * @param bounds
	 * @return the hit figures, never <code>null</code>.
	 */
	public Set<Figure> getFiguresOn(Shape2f bounds) {
		Set<Figure> figures = new TreeSet<Figure>();
		for(Figure figure : this.figures) {
			if (figure.intersects(bounds)) {
				figures.add(figure);
			}
		}
		return figures;
	}

	/** Replies the first figure that are intersecting the specified bounds.
	 * 
	 * @param bounds
	 * @return the hit figure, or <code>null</code>.
	 */
	public Figure getFigureOn(Shape2f bounds) {
		for(Figure figure : this.figures) {
			if (figure.intersects(bounds)) {
				return figure;
			}
		}
		return null;
	}

	/** Replies the figures that are inside the specified bounds.
	 * 
	 * @param bounds
	 * @return the hit figures, never <code>null</code>.
	 */
	public Set<Figure> getFiguresIn(Rectangle2f bounds) {
		Set<Figure> figures = new TreeSet<Figure>();
		Rectangle2f bb = new Rectangle2f(
				bounds.getMinX(),
				bounds.getMinY(),
				bounds.getWidth(),
				bounds.getHeight());
		for(Figure figure : this.figures) {
			if (figure.contains(bb)) {
				figures.add(figure);
			}
		}
		return figures;
	}

	/** Replies the first figure that are inside the specified bounds.
	 * 
	 * @param bounds
	 * @return the hit figures, never <code>null</code>.
	 */
	public Figure getFigureIn(Rectangle2f bounds) {
		for(Figure figure : this.figures) {
			if (figure.contains(bounds)) {
				return figure;
			}
		}
		return null;
	}

	/** Move the figure up in the layers used by this panel.
	 * 
	 * @param figure is the figure to move up.
	 */
	public void moveFigureUp(Figure figure) {
		int idx = indexOfFigure(figure);
		if (idx>0) {
			Figure o = this.figures.get(idx-1);
			this.figures.set(idx, o);
			this.figures.set(idx-1, figure);

			Rectangle2f r = figure.getDamagedBounds();
			r = o.getDamagedBounds().createUnion(r);

			repaint(r);
		}
	}

	/** Move the figure down in the layers used by this panel.
	 * 
	 * @param figure is the figure to move down.
	 */
	public void moveFigureDown(Figure figure) {
		int idx = indexOfFigure(figure);
		if (idx>=0 && idx<this.figures.size()-1) {
			Figure o = this.figures.get(idx+1);
			this.figures.set(idx, o);
			this.figures.set(idx+1, figure);

			Rectangle2f r = figure.getDamagedBounds();
			r = o.getDamagedBounds().createUnion(r);

			repaint(r);
		}
	}

	/** Move the figure at the front in the layers used by this panel.
	 * 
	 * @param figure is the figure to move to the front.
	 */
	public void moveFigureFront(Figure figure) {
		int idx = indexOfFigure(figure);
		if (idx>0) {
			this.figures.remove(idx);
			this.figures.add(0, figure);
			repaint(figure);
		}
	}

	/** Move the figure at the background in the layers used by this panel.
	 * 
	 * @param figure is the figure to move to the background.
	 */
	public void moveFigureBackground(Figure figure) {
		int idx = indexOfFigure(figure);
		if (idx>=0 && idx<this.figures.size()-1) {
			this.figures.remove(idx);
			this.figures.add(figure);
			repaint(figure);
		}
	}

	/** Move the figure at the specified position in the layers used by this panel.
	 * 
	 * @param figure is the figure to move to the front.
	 * @param index is the new position of the figure.
	 * @return the previous position of the figure; or {@code -1}
	 * if the figure was not found.
	 * @throws IllegalArgumentException
	 */
	public int moveFigureAt(Figure figure, int index) {
		if (index<0 && index>=this.figures.size())
			throw new IllegalArgumentException();
		int idx = indexOfFigure(figure);
		if (idx!=index && idx>=0) {
			this.figures.remove(idx);
			this.figures.add(index, figure);
			repaint(figure);
		}
		return idx;
	}

	/** Replies the index of the figure.
	 * This index is also the position of the
	 * from the background.
	 * 
	 * @param figure is the figure to search for.
	 * @return the position of the figure; {@code -1} if
	 * is was not found.
	 */
	public int indexOfFigure(Figure figure) {
		return this.figures.indexOf(figure);
	}

	private void removeFigureListeners(Figure figure) {
		figure.removeViewComponentChangeListener(getEventHandler(ViewComponentChangeListener.class));
		figure.removeViewComponentPropertyChangeListener(getEventHandler(ViewComponentPropertyChangeListener.class));
		figure.removeViewComponentRepaintListener(getEventHandler(ViewComponentLayoutListener.class));
		if (figure instanceof ModelObjectFigure<?>)
			((ModelObjectFigure<?>)figure).removeViewComponentBindingListener(
					getEventHandler(ViewComponentBindingListener.class));
	}

	private void addFigureListeners(Figure figure) {
		figure.addViewComponentChangeListener(getEventHandler(ViewComponentChangeListener.class));
		figure.addViewComponentPropertyChangeListener(getEventHandler(ViewComponentPropertyChangeListener.class));
		figure.addViewComponentRepaintListener(getEventHandler(ViewComponentLayoutListener.class));
		if (figure instanceof ModelObjectFigure<?>)
			((ModelObjectFigure<?>)figure).addViewComponentBindingListener(
					getEventHandler(ViewComponentBindingListener.class));
	}

	/**
	 * Replies the area covered by the displayed graph.
	 * This function invokes {@link #calcDocumentBounds()}
	 * to compute the bounds of the document when it is unknown.
	 * 
	 * @return the bounds of the document or <code>null</code> if
	 * there is no bounds (ie. no document).
	 */
	public Rectangle2f getDocumentRect() {
		Rectangle2f r = (this.documentBounds==null) ? null : this.documentBounds.get();
		if (r==null) {
			r = calcDocumentBounds();
			this.documentBounds = new SoftReference<Rectangle2f>(r);
		}
		if (r==null) return null;
		return r.clone();
	}

	/** Compute and reply the bounds of the entire document.
	 * 
	 * @return the bounds of the entire document.
	 */
	protected Rectangle2f calcDocumentBounds() {
		try {
			this.changeLock.lock();
			Rectangle2f r = null;
			Rectangle2f rr;
			for(Figure figure : this.figures) {
				rr = figure.getDamagedBounds();
				assert(rr!=null);
				if (r==null) r = rr.clone();
				else Rectangle2f.union(r, rr, r);
			}
			return r;
		}
		finally {
			this.changeLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Figure> iterator() {
		return this.figures.iterator();
	}

	/** Replies the precisions of the hits on the view.
	 * 
	 * @return the precisions of the hits.
	 */
	public float getHitPrecision() {
		return this.hitPrecision;
	}

	/** Replies the precision of the hits on the view.
	 * Byt default, the value of {@link #CLICK_PRECISION}
	 * is used.
	 * 
	 * @param precision is the precision of the hits, in pixels.
	 */
	public void setHitPrecision(float precision) {
		if (precision>=0f) {
			this.hitPrecision = precision;
		}
	}

	/** Replies if the data inside the view is editable.
	 * 
	 * @return <code>true</code> if the view enables edition;
	 * otherwise <code>false</code>.
	 */
	public boolean isEditable() {
		return this.isEditable;
	}

	/** Set if this viewer is interactively editable.
	 * 
	 * @param editable is <code>true</code> if this viewer is editable; otherwise <code>false</code>.
	 */
	public void setEditable(boolean editable) {
		this.isEditable = editable;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isShadowDrawn() {
		return this.isShadowDraw;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setShadowDrawn(boolean draw) {
		if (this.isShadowDraw!=draw) {
			this.isShadowDraw = draw;
			repaint();
		}
	}

	/** Refresh the drawing area covered by the given figure.
	 * <p>
	 * This function does nothing if {@link #isIgnoreRepaint()}
	 * replies <code>true</code>;
	 * 
	 * @param figure
	 */
	public void repaint(Figure figure) {
		if (!isIgnoreRepaint() && figure!=null) {
			Rectangle2f bb = figure.getBounds();
			if (bb!=null) repaint(bb);
		}
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	protected void onDrawView(Canvas canvas, float scaleFactor, CenteringTransform centeringTransform) {
		DroidViewGraphics2D viewG = new DroidViewGraphics2D(
				canvas,
				scaleFactor, centeringTransform,
				getBackgroundColor(),
				true,
				getScalingSensitivity(),
				getFocusX(),
				getFocusY(),
				getMinScalingFactor(),
				getMaxScalingFactor());
		VectorToolkit.prepareDrawing(viewG);
		paintViewComponents(viewG);
		if (isEnabled()) {
			viewG.reset();
			this.mode.paint(viewG);
		}
		VectorToolkit.finalizeDrawing(viewG);
		viewG.dispose();
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public void paintViewComponents(ViewGraphics2D g) {
		try {
			this.changeLock.lock();
			Rectangle2f bounds;
			
			/*if (isShadowDrawn() && g.getLOD().compareTo(Graphics2DLOD.NORMAL_LEVEL_OF_DETAIL)>=0) {
				Iterator<Figure> iterator = this.figures.descendingIterator();
				Figure figure;
				ViewGraphics2D sg = new ShadowViewGraphics2D(g);
				Transform2D trans = g.getTransform().clone();
				trans.translate(
						g.getShadowTranslationX(),
						g.getShadowTranslationY());
				while (iterator.hasNext()) {
					figure = iterator.next();
					bounds = figure.getBounds();
					sg.pushRenderingContext(
							figure, figure.getClip(bounds), bounds, null, null, trans);
					figure.paint(sg);
					sg.popRenderingContext();
				}
			}*/
			
			g.reset();
			
			Iterator<Figure> iterator = this.figures.descendingIterator();
			Figure figure;
			while (iterator.hasNext()) {
				figure = iterator.next();
				bounds = figure.getBounds();
				g.pushRenderingContext(
						figure,
						figure.getClip(bounds),
						bounds);
				figure.paint(g);
				g.popRenderingContext();
			}
		}
		finally {
			this.changeLock.unlock();
		}
	}
	
	@Override
	protected void onPointerPressed(PointerEvent e) {
		this.mode.getModeManager().pointerPressed(e);
	}
	
	@Override
	protected void onPointerDragged(PointerEvent e) {
		this.mode.getModeManager().pointerDragged(e);
	}
	
	@Override
	protected void onPointerReleased(PointerEvent e) {
		this.mode.getModeManager().pointerReleased(e);
	}
	
	@Override
	protected void onLongClick(PointerEvent e) {
		this.mode.getModeManager().pointerLongClicked(e);
	}
	
	@Override
	protected void onClick(PointerEvent e) {
		this.mode.getModeManager().pointerClicked(e);
	}

	/** Replies the object that permits to change
	 * the configuration of the action mode manager.
	 * 
	 * @return the configurator.
	 */
	public FigureActionModeManager getActionModeManager() {
		return this.mode;
	}
	
	/** Add Listener on changes in this object.
	 * 
	 * @param listener
	 */
	public void addModelObjectListener(ModelObjectListener listener) {
		this.listenerList.add(ModelObjectListener.class, listener);
		if (this.graph!=null) {
			this.graph.addModelObjectListener(listener);
		}
	}

	/** Add Listener on changes in this object.
	 * 
	 * @param listener
	 */
	public void removeModelObjectListener(ModelObjectListener listener) {
		if (this.graph!=null) {
			this.graph.removeModelObjectListener(listener);
		}
		this.listenerList.remove(ModelObjectListener.class, listener);
	}

	/** Add listener on figure events.
	 * 
	 * @param listener
	 */
	public void addFigureListener(FigureListener listener) {
		this.listenerList.add(FigureListener.class, listener);
	}

	/** Remove listener on figure events.
	 * 
	 * @param listener
	 */
	public void removeFigureListener(FigureListener listener) {
		this.listenerList.remove(FigureListener.class, listener);
	}

	/** Notifies the listeners about the removal of a figure.
	 * 
	 * @param removed
	 */
	protected void fireFigureRemoved(Figure removed) {
		FigureEvent event = new FigureEvent(this, removed, null, null);
		for(FigureListener listener : this.listenerList.getListeners(FigureListener.class)) {
			listener.figureRemoved(event);
		}
	}

	/** Notifies the listeners about the change of a figure.
	 * 
	 * @param changed
	 */
	protected void fireFigureChanged(Figure changed) {
		FigureEvent event = new FigureEvent(this, null, null, changed);
		for(FigureListener listener : this.listenerList.getListeners(FigureListener.class)) {
			listener.figureChanged(event);
		}
	}

	/** Notifies the listeners about the addition of a figure.
	 * 
	 * @param added
	 */
	protected void fireFigureAdded(Figure added) {
		FigureEvent event = new FigureEvent(this, null, added, null);
		for(FigureListener listener : this.listenerList.getListeners(FigureListener.class)) {
			listener.figureAdded(event);
		}
	}

	@Override
	public Object getTreeLock() {
		return this;
	}

	@Override
	public final Rectangle2f getComponentBounds() {
		return getDocumentRect();
	}

	@Override
	public int getFigureCount() {
		return this.figures.size();
	}

	/**
	 * Replies the figure at the given coordinate.
	 * 
	 * @param x
	 * @param y
	 * @return the figure at the given coordinate; or <code>null</code> if none.
	 */
	public Figure getFigureAt(float x, float y) {
		float precision = pixel2logical_size(getHitPrecision());
		Circle2f circle = new Circle2f(x, y, precision);
		for(Figure figure : this.figures) {
			if (figure.intersects(circle)) {
				return figure;
			}
		}
		return null;
	}

	@Override
	public Figure getFigureAt(int index) {
		return this.figures.get(index);
	}

	@Override
	public int addFigure(Figure component) {
		int position = -1;
		if (component!=null) {
			try {
				this.changeLock.lock();
				boolean firstComponent = this.figures.isEmpty();
				position = 0;
				this.figures.add(position,component);
				this.documentBounds = null;
				component.setViewComponentContainer(this);
				addFigureListeners(component);
				if (firstComponent) setScalingFactor(1);
				repaint();
			}
			finally {
				this.changeLock.unlock();
			}
			fireFigureAdded(component);
		}
		return position;
	}

	@Override
	public int removeFigure(Figure component) {
		return removeFigure(component, true);
	}

	/**
	 * Remove the specified component.
	 * 
	 * @param component is the component to remove.
	 * @param unlinkToModelObject indicates if the removed component should be
	 * unlink to its model object.
	 * @return position of the removed figure; or {@code -1} if not found. 
	 * @see #removeFigure(Figure)
	 */
	public int removeFigure(Figure component, boolean unlinkToModelObject) {
		int position = -1;
		if (component!=null) {
			try {
				this.changeLock.lock();
				position = this.figures.indexOf(component);
				if (position>=0) {
					this.figures.remove(position);
					component.setViewComponentContainer(null);
					removeFigureListeners(component);
					if (unlinkToModelObject &&
							component instanceof ModelObjectFigure<?>) {
						((ModelObjectFigure<?>)component).setModelObject(null);
					}
					this.documentBounds = null;
					this.selectionManager.remove(component);
					repaint();
					fireFigureRemoved(component);
				}
			}
			finally {
				this.changeLock.unlock();
			}
		}
		return position;
	}

	@Override
	public Figure removeFigureAt(int index) {
		try {
			this.changeLock.lock();
			Figure fig = this.figures.remove(index);
			if (fig!=null) {
				fig.setViewComponentContainer(null);
				removeFigureListeners(fig);
				this.documentBounds = null;
				this.selectionManager.remove(fig);
				repaint();
				fireFigureRemoved(fig);
			}
			return fig;
		}
		finally {
			this.changeLock.unlock();
		}
	}

	@Override
	public void removeAllFigures() {
		List<Figure> oldFigures = null;
		try {
			this.changeLock.lock();
			if (!this.figures.isEmpty()) {
				oldFigures = this.figures;
				this.figures = new LinkedList<Figure>();

				for(Figure fig : oldFigures) {
					fig.setViewComponentContainer(null);
					removeFigureListeners(fig);
				}

				this.documentBounds = null;
				this.selectionManager.clear();
				repaint();
			}
		}
		finally {
			this.changeLock.unlock();
		}

		if (oldFigures!=null) {
			for(Figure fig : oldFigures) {
				fireFigureRemoved(fig);
			}
		}
	}

	@Override
	public Color getSelectionForegroundColor() {
		return VectorToolkit.color(this.foregroundSelectionColor);
	}

	/** Set the background color of the selection.
	 * 
	 * @param color
	 */
	public void setSelectionForegroundColor(int color) {
		this.foregroundSelectionColor = color;
		repaint();
	}

	@Override
	public Color getSelectionBackgroundColor() {
		return VectorToolkit.color(this.backgroundSelectionColor);
	}
	
	/** Set the background color of the selection.
	 * 
	 * @param color
	 */
	public void setSelectionBackgroundColor(int color) {
		this.backgroundSelectionColor = color;
		repaint();
	}

	@Override
	public Color getBackgroundColor() {
		return VectorToolkit.color(this.backgroundColor);
	}
	
	@Override
	public void setBackgroundDrawable(Drawable d) {
		if (d instanceof ColorDrawable) {
			this.backgroundColor = ((ColorDrawable)d).getColor();
		}
		else {
			this.backgroundColor = getResources().getColor(android.R.color.background_light);
		}
		super.setBackgroundDrawable(d);
	}
	
	/** Import the graph and the specified figures into this editor.
	 * 
	 * @param graph is the graph to integrate.
	 * @param figures are the figures (classified per view id) to import, from front to background.
	 */
	public void importGraph(G graph, Map<UUID,List<ViewComponent>> figures) {
		try {
			this.changeLock.lock();
			setGraph(graph);

			UUID newViewId = getUUID();

			for(List<ViewComponent> theSet : figures.values()) {
				ViewComponent o;
				for(int i=theSet.size()-1; i>=0; --i) {
					o = theSet.get(i);
					if (o instanceof Figure) {
						Figure aFigure = (Figure)o;

						UUID oldViewId = aFigure.getViewUUID();

						// Ensure that the subfigures have the same view Id as the parent figure
						for(SubFigure subfigure : aFigure.getSubFigures()) {
							if (subfigure instanceof ModelObjectView<?>) {
								ModelObject mo = ((ModelObjectView<?>)subfigure).getModelObject();
								if (mo!=null) {
									ViewBinding binding = mo.getViewBinding();
									binding.replaceView(oldViewId, newViewId);
								}
							}
							subfigure.setViewUUID(newViewId);
						}

						if (aFigure instanceof ModelObjectFigure<?>) {
							ModelObjectFigure<?> mov = (ModelObjectFigure<?>)aFigure;
							ModelObject mo = mov.getModelObject();
							if (mo!=null) {
								ViewBinding binding = mo.getViewBinding();
								View old = binding.replaceView(oldViewId, newViewId);
								if (old instanceof Figure && old!=o) {
									FigureView.this.removeFigure((Figure)old);
								}
							}
						}
						FigureView.this.addFigure(aFigure);
					}
					else if (o instanceof SubFigure && o instanceof ModelObjectView<?>) {
						ModelObject mo = ((ModelObjectView<?>)o).getModelObject();
						if (mo!=null) {
							ViewBinding binding = mo.getViewBinding();
							binding.replaceView(
									o.getViewUUID(),
									newViewId);
						}
						o.setViewUUID(newViewId);
					}
				}
			}
		}
		finally {
			this.changeLock.unlock();
		}
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ViewGraphicsFactory implements Factory {

		/**
		 */
		public ViewGraphicsFactory() {
			//
		}

		@Override
		public ViewGraphics2D createViewGraphics(VectorGraphics2D g, boolean antialiasing, boolean isForPrinting) {
			return createViewGraphics(g, antialiasing, isForPrinting, g.getLOD());
		}

		@Override
		public ViewGraphics2D createViewGraphics(VectorGraphics2D g, boolean antialiasing, boolean isForPrinting, Graphics2DLOD lod) {
			assert(g instanceof ViewGraphics2D);
			return (ViewGraphics2D)g;
		}

	} // class ViewGraphicsFactory

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class CAvoider implements CollisionAvoider {

		/**
		 */
		public CAvoider() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isCollisionFree(Rectangle2f bounds, Set<? extends ViewComponent> exceptions) {
			Rectangle2f r;
			for(Figure figure : FigureView.this) {
				if (figure instanceof BlockFigure && !exceptions.contains(figure)) {
					r = figure.getDamagedBounds();
					if (r!=null && !r.isEmpty()
							&& r.intersects(bounds)) {
						return false;
					}
				}
			}
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Rectangle2f detectCollision(Rectangle2f bounds, Set<? extends ViewComponent> exceptions) {
			Rectangle2f r;
			for(Figure figure : FigureView.this) {
				if (figure instanceof BlockFigure && !exceptions.contains(figure)) {
					r = figure.getDamagedBounds();
					if (r!=null && !r.isEmpty()
							&& r.intersects(bounds)) {
						return r;
					}
				}
			}
			return null;
		}

	} // class CAvoider

	/**
	 * @author $Author: hannoun$
	 * @author $Author: galland$
	 * @author $Author: baumgartner$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class EventHandler 
	implements ModelObjectListener, ViewComponentChangeListener,
	ViewComponentPropertyChangeListener, ViewComponentLayoutListener,
	ViewComponentBindingListener, SelectionListener {

		/**
		 */
		public EventHandler() {
			//
		}

		//-------------------------------------
		// ModelObjectListener
		//-------------------------------------

		@Override
		public void modelPropertyChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		@Override
		public void modelContainerChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		@Override
		public void modelLinkChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		@Override
		public void modelContentChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		@Override
		public void modelComponentAdded(ModelObjectEvent event) {
			if (isFigureAutomaticallyAdded() && event.getSource()==getGraph()) {
				ModelObject addedObject = event.getAddedObject();
				if (addedObject!=null) {
					Figure figure = addedObject.getViewBinding().getView(getUUID(), Figure.class);
					if (figure==null) {
						FigureFactory<G> factory = getFigureFactory();
						Rectangle2f bb = getDocumentRect();
						figure = factory.createFigureFor(
								getUUID(),
								bb,
								getGraph(), addedObject);
					}
					if (figure!=null) addFigure(figure);
				}
			}
		}

		@Override
		public void modelComponentRemoved(ModelObjectEvent event) {
			if (event.getSource()==getGraph()) {
				Figure fig = event.getRemovedObject().getViewBinding().getView(getUUID(), Figure.class);
				if (fig!=null) {
					removeFigure(fig);
				}
			}
		}

		//-------------------------------------
		// ViewComponentChangeListener
		//-------------------------------------

		@Override
		public void componentChange(ViewComponentChangeEvent event) {
			ViewComponent vc = event.getSource();
			if (vc instanceof Figure) {
				fireFigureChanged((Figure)vc);
			}
		}

		//-------------------------------------
		// ViewComponentPropertyChangeListener
		//-------------------------------------

		@Override
		public void propertyChange(ViewComponentPropertyChangeEvent event) {
			ViewComponent vc = event.getSource();
			if (vc instanceof Figure) {
				fireFigureChanged((Figure)vc);
			}
		}

		//-------------------------------------
		// ViewComponentLayoutListener
		//-------------------------------------

		@SuppressWarnings("synthetic-access")
		@Override
		public void componentRepaint(ViewComponent component, boolean boundsChanged) {
			if (boundsChanged) {
				FigureView.this.documentBounds = null;
			}
			repaint();
		}

		@Override
		public void collisionAvoidance(ViewComponent component) {
			if (component instanceof Figure) {
				Rectangle2f componentBounds = component.getBounds().clone();
				CollisionAvoider avoider = getCollisionAvoider();
				Set<ViewComponent> myself = Collections.singleton(component);
				Rectangle2f collide = avoider.detectCollision(
						componentBounds, myself);
				Vector2D v = null;
				if (collide!=null) {
					do {
						v = componentBounds.avoidCollisionWith(collide, v);
						collide = avoider.detectCollision(
								componentBounds, myself);
					}
					while (collide!=null);

					component.setBounds(
							componentBounds.getMinX(),
							componentBounds.getMinY(),
							componentBounds.getWidth(),
							componentBounds.getHeight());
				}
			}
		}

		//-------------------------------------
		// ViewComponentBindingListener
		//-------------------------------------

		@Override
		public void componentCreation(Figure parent, ModelObject modelObject) {
			getFigureFactory().createSubFigureInside(
					getUUID(),
					getGraph(),
					parent,
					modelObject);
		}

		@Override
		public void componentRemoval(Figure parent, SubFigure subfigure) {
			getFigureFactory().removeSubFigureFrom(
					getUUID(),
					getGraph(),
					parent,
					subfigure);
		}

		@Override
		public void componentAddition(Figure figure) {
			assert(figure!=null);
			FigureView.this.addFigure(figure);
		}

		@Override
		public void componentRemoval(Figure figure) {
			assert(figure!=null);
			FigureView.this.removeFigure(figure);
		}

		//-------------------------------------
		// ModelObjectListener
		//-------------------------------------

		@Override
		public void selectionChanged(SelectionEvent event) {
			//FIXME: repaint a portion of the screen
			if (event.isLastEvent()) repaint();
		}

	} // class EventHandler

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class ModeManagerWrapper implements ActionModeOwner {

		private final ActionModeManager modeManager;

		/**
		 */
		public ModeManagerWrapper() {
			this.modeManager = new ActionModeManager(getUUID(), this);
			this.modeManager.setResetHitFigureWhenPointerReleased(true);
			// use the standard implementation of the modes
			this.modeManager.startMode(new BaseMode());
		}

		@Override
		public boolean isAlwaysRemovingModelObjects() {
			return FigureView.this.isAlwaysRemovingModelObjects();
		}

		@Override
		public Object getUIComponent() {
			return FigureView.this;
		}

		@Override
		public void requestFocus() {
			FigureView.this.requestFocus();
		}

		@Override
		public SelectionManager getSelectionManager() {
			return FigureView.this.getSelectionManager();
		}

		@Override
		public ActionModeManager getModeManager() {
			return this.modeManager;
		}

		@Override
		public UndoManager getUndoManager() {
			return FigureView.this.getUndoManager();
		}

		@Override
		public void setCursor(MouseCursor cursor) {
			//
		}

		@Override
		public void repaint(Rectangle2f bounds) {
			FigureView.this.repaint(
					bounds.getMinX(), bounds.getMinY(),
					bounds.getWidth(), bounds.getHeight());
		}

		@Override
		public void repaint() {
			FigureView.this.repaint();
		}
		
		/** Paint the elements from the modes.
		 * 
		 * @param g
		 */
		public void paint(DroidViewGraphics2D g) {
			this.modeManager.paint(g);
		}

		@Override
		public float getClickPrecision() {
			return FigureView.this.getHitPrecision();
		}

		@Override
		public ZoomableContext getZoomableContext() {
			return FigureView.this;
		}

		@Override
		public void cut() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void copy() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void paste() {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isEditable() {
			return FigureView.this.isEnabled() && FigureView.this.isEditable();
		}
		
		@Override
		public boolean isSelectionEnabled() {
			return FigureView.this.isEnabled() && FigureView.this.isSelectionEnabled();
		}

		@Override
		public void fireError(Throwable error) {
			Log.e(FigureView.this.getClass().getName(), error.getLocalizedMessage(), error);
		}

		@Override
		public int getFigureCount() {
			return FigureView.this.getFigureCount();
		}

		@Override
		public Collection<? extends Figure> getFigures() {
			return FigureView.this.getFigures();
		}

		@Override
		public Figure getFigureAt(int index) {
			return FigureView.this.getFigureAt(index);
		}

		@Override
		public Figure getFigureAt(float x, float y) {
			return FigureView.this.getFigureAt(x,y);
		}

		@Override
		public Figure getFigureWithBoundsAt(float x, float y) {
			return FigureView.this.getFigureWithBoundsAt(x, y);
		}

		@Override
		public Set<Figure> getFiguresOn(Shape2f bounds) {
			return FigureView.this.getFiguresOn(bounds);
		}

		@Override
		public Set<Figure> getFiguresIn(Rectangle2f bounds) {
			return FigureView.this.getFiguresIn(bounds);
		}

		@Override
		public Figure getFigureOn(Shape2f bounds) {
			return FigureView.this.getFigureOn(bounds);
		}

		@Override
		public Figure getFigureIn(Rectangle2f bounds) {
			return FigureView.this.getFigureIn(bounds);
		}

		@Override
		public Color getSelectionBackground() {
			return FigureView.this.getSelectionBackgroundColor();
		}

		@Override
		public Color getSelectionForeground() {
			return FigureView.this.getSelectionForegroundColor();
		}

		@Override
		public Undoable removeFigure(boolean deleteModel,
				boolean disconnectFigureAndModel, Figure figure) {
			if (!figure.isLocked() && FigureView.this.isEditable()) {
				if (deleteModel && figure instanceof ModelObjectFigure<?>) {
					ModelObject object = ((ModelObjectFigure<?>)figure).getModelObject();
					if (object!=null) {
						if (figure instanceof EdgeFigure<?>) {
							EdgeFigureRemovalUndo undo = new EdgeFigureRemovalUndo((EdgeFigure<?>)figure, disconnectFigureAndModel);
							undo.doEdit();
							return undo;
						}
						if (figure instanceof NodeFigure<?,?>) {
							NodeFigureRemovalUndo undo = new NodeFigureRemovalUndo((NodeFigure<?,?>)figure, disconnectFigureAndModel);
							undo.doEdit();
							return undo;
						}
					}
				}
				FigureRemovalUndo undo = new FigureRemovalUndo(figure);
				undo.doEdit();
				return undo;
			}
			return null;
		}

		@Override
		public Undoable removeFigures(boolean deleteModel,
				boolean disconnectFigureAndModel,
				Iterable<? extends Figure> figures) {
			assert(figures!=null);
			if (FigureView.this.isEditable()) {
				List<ModelObjectFigure<?>> removeModels = new ArrayList<ModelObjectFigure<?>>();
				List<Figure> removeFigures = new ArrayList<Figure>();
				Figure lastFigure = null;

				for(Figure figure : figures) {
					if (!figure.isLocked()) {
						if (figure instanceof ModelObjectFigure<?> && deleteModel) {
							if (getModeManager().fireFigureDeletionPerformed(figure, deleteModel)) {
								lastFigure = figure;
								removeModels.add((ModelObjectFigure<?>)figure);
							}
						}
						else if (getModeManager().fireFigureDeletionPerformed(figure, deleteModel)) {
							lastFigure = figure;
							removeFigures.add(figure);
						}
					}
				}

				String label;
				if ((removeModels.size()+removeFigures.size())>1) {
					label = Locale.getString("UNDO_PRESENTATION_FIGURE_REMOVAL_n"); //$NON-NLS-1$
				}
				else {
					String name = lastFigure==null ? null : lastFigure.toString();
					if (name!=null && !name.isEmpty()) {
						label = Locale.getString("UNDO_PRESENTATION_FIGURE_REMOVAL_1", name); //$NON-NLS-1$
					}
					else {
						label = Locale.getString("UNDO_PRESENTATION_FIGURE_REMOVAL_0"); //$NON-NLS-1$
					}
				}

				UndoableGroup group = new UndoableGroup(label);

				for(ModelObjectFigure<?> figure : removeModels) {
					group.add(removeFigure(deleteModel, disconnectFigureAndModel, figure));
				}

				for(Figure figure : removeFigures) {
					group.add(removeFigure(false, false, figure));
				}

				group.end();

				if (!group.isEmpty()) return group;
			}

			return null;
		}

		@Override
		public Undoable addFigure(Figure figure) {
			if (figure instanceof DecorationFigure && FigureView.this.isEditable()) {
				DecorationFigureAdditionUndo undo = new DecorationFigureAdditionUndo(
						figure.getBounds(), (DecorationFigure)figure);
				undo.doEdit();
				return undo;
			}
			return null;
		}

		@Override
		public G getGraph() {
			return FigureView.this.getGraph();
		}

		@Override
		public FigureFactory<G> getFigureFactory() {
			return FigureView.this.getFigureFactory();
		}

		@Override
		public Context getContext() {
			return FigureView.this.getContext();
		}

		@Override
		public void setSelectionMode(SelectionMode mode) {
			this.modeManager.setSelectionMode(mode);
		}
		
		@Override
		public SelectionMode getSelectionMode() {
			return this.modeManager.getSelectionMode();
		}

		@Override
		public void addModeListener(ActionModeListener listener) {
			this.modeManager.addModeListener(listener);
		}

		@Override
		public void removeModeListener(ActionModeListener listener) {
			this.modeManager.removeModeListener(listener);
		}

		@Override
		public void addSelectableInteractionListener(SelectableInteractionListener listener) {
			this.modeManager.addSelectableInteractionListener(listener);
		}

		@Override
		public void removeSelectableInteractionListener(SelectableInteractionListener listener) {
			this.modeManager.removeSelectableInteractionListener(listener);
		}

		@Override
		public void startMode(ActionMode<Figure, DroidViewGraphics2D, Color> mode) {
			this.modeManager.startMode(mode);
		}

		@Override
		public void runOnUIThread(Runnable action) {
			Context context = FigureView.this.getContext();
			if (context instanceof Activity) {
				((Activity)context).runOnUiThread(action);
			}
			else {
				action.run();
			}
		}

		@Override
		public android.view.ActionMode startActionBar(android.view.ActionMode.Callback listener) {
			Context context = FigureView.this.getContext();
			if (context instanceof Activity) {
				return ((Activity)context).startActionMode(listener);
			}
			return null;
		}

		@Override
		public void selectFile(
				String mimeType,
				Class<? extends FileFilter> fileFilter,
				org.arakhne.neteditor.android.actionmode.ActionModeOwner.Callback callback) {
			Context context = getContext();
			if (context instanceof AbstractEditorActivity<?>) {
				AbstractEditorActivity<?> activity = (AbstractEditorActivity<?>)context;
				activity.setFileSelectionCallback(callback);
				FileChooser.showOpenChooser(
						activity,
						ActivityResultIdentifiers.MODE_FILE_SELECTION_REQUEST_CODE,
						R.string.msg_loading,
						mimeType,
						FileChooser.createOptions(null, fileFilter, NetEditorFileChooserIconSelector.class));
			}
		}

	} // Class Mode

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private abstract class AbstractFigureRemovalUndo<F extends Figure> extends AbstractUndoable {

		private static final long serialVersionUID = -8094374124510096224L;

		/**
		 * the removed figure.
		 */
		protected final F figure;

		/**
		 * @param figure
		 */
		public AbstractFigureRemovalUndo(F figure) {
			this.figure = figure;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public final String getPresentationName() {
			if (this.figure!=null) {
				String txt = this.figure.toString();
				if (txt!=null && !txt.isEmpty()) {
					return Locale.getString(FigureView.class, "UNDO_PRESENTATION_FIGURE_REMOVAL_1", txt); //$NON-NLS-1$
				}
			}
			return Locale.getString(FigureView.class, "UNDO_PRESENTATION_FIGURE_REMOVAL_n"); //$NON-NLS-1$
		}


	} // class AbstractFigureRemovalUndo

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FigureRemovalUndo extends AbstractFigureRemovalUndo<Figure> {

		private static final long serialVersionUID = 73123844872054872L;

		/**
		 * @param figure
		 */
		public FigureRemovalUndo(Figure figure) {
			super(figure);
		}

		@Override
		public void doEdit() {
			removeFigure(this.figure, true);
		}

		@Override
		public void undoEdit() {
			addFigure(this.figure);
		}

	} // class FigureRemovalUndo

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	private class EdgeFigureRemovalUndo extends AbstractFigureRemovalUndo<EdgeFigure> {

		private static final long serialVersionUID = -1256611620514148919L;

		private final boolean disconnectFigureAndModel;
		private final Edge edge;
		private final Anchor startAnchor;
		private final Anchor endAnchor;
		private final Map<String,CoercedFigure> coercedFigures = new TreeMap<String,CoercedFigure>();

		/**
		 * @param figure is the figure to remove.
		 * @param disconnectFigureAndModel indicates if the figure and the model
		 * object may be disconnected or not.
		 */
		public EdgeFigureRemovalUndo(EdgeFigure<?> figure, boolean disconnectFigureAndModel) {
			super(figure);
			this.disconnectFigureAndModel = disconnectFigureAndModel;
			this.coercedFigures.putAll(figure.getAssociatedFiguresInView());
			this.edge = figure.getModelObject();
			this.startAnchor = this.edge.getStartAnchor();
			this.endAnchor = this.edge.getEndAnchor();
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void doEdit() {
			for(CoercedFigure f : this.coercedFigures.values()) {
				removeFigure(f);
			}
			boolean b = false;
			try {
				FigureView.this.changeLock.lock();
				b = FigureView.this.skipFigureModelUnlink;
				FigureView.this.skipFigureModelUnlink = !this.disconnectFigureAndModel;
				((Graph)getGraph()).removeEdge(this.edge);
			}
			finally {
				FigureView.this.skipFigureModelUnlink = b;
				FigureView.this.changeLock.unlock();
			}
		}

		@Override
		public void undoEdit() {
			this.figure.setViewUUID(getUUID());
			this.figure.setModelObject(this.edge);
			((Graph)getGraph()).addEdge(this.edge);

			this.edge.setStartAnchor(this.startAnchor);
			this.edge.setEndAnchor(this.endAnchor);

			for(Entry<String,CoercedFigure> entry : this.coercedFigures.entrySet()) {
				this.figure.addAssociatedFigureIntoView(entry.getKey(), entry.getValue());
			}
		}

	} // class EdgeFigureRemovalUndo

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@SuppressWarnings({"unchecked","rawtypes"})
	private class NodeFigureRemovalUndo extends AbstractFigureRemovalUndo<NodeFigure> {

		private static final long serialVersionUID = -1256611620514148919L;

		private final boolean disconnectFigureAndModel;
		private final Node<?,?,?,?> node;
		private final Collection<EdgeFigureRemovalUndo> linkedEdges = new ArrayList<EdgeFigureRemovalUndo>();
		private final Map<String,CoercedFigure> coercedFigures = new TreeMap<String,CoercedFigure>();

		/**
		 * @param figure is the figure to remove
		 * @param disconnectFigureAndModel indicates if the figure and the model
		 * object may be disconnected or not.
		 */
		public NodeFigureRemovalUndo(NodeFigure<?,?> figure, boolean disconnectFigureAndModel) {
			super(figure);
			this.disconnectFigureAndModel = disconnectFigureAndModel;
			this.coercedFigures.putAll(figure.getAssociatedFiguresInView());
			this.node = figure.getModelObject();
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void doEdit() {
			for(CoercedFigure f : this.coercedFigures.values()) {
				removeFigure(f);
			}

			boolean b = false;
			try {
				FigureView.this.changeLock.lock();
				b = FigureView.this.skipFigureModelUnlink;
				FigureView.this.skipFigureModelUnlink = !this.disconnectFigureAndModel;

				this.linkedEdges.clear();
				for(Edge edge : this.node.getEdges()) {
					EdgeFigure edgeFigure = edge.getViewBinding().getView(getUUID(), EdgeFigure.class);
					if (edgeFigure!=null) {
						EdgeFigureRemovalUndo undo = new EdgeFigureRemovalUndo(edgeFigure, this.disconnectFigureAndModel);
						undo.doEdit();
						this.linkedEdges.add(undo);
					}
				}

				((Graph)getGraph()).removeNode(this.node);
			}
			finally {
				FigureView.this.skipFigureModelUnlink = b;
				FigureView.this.changeLock.unlock();
			}
		}

		@Override
		public void undoEdit() {
			this.figure.setViewUUID(getUUID());
			this.figure.setModelObject(this.node);
			((Graph)getGraph()).addNode(this.node);

			for(EdgeFigureRemovalUndo undo : this.linkedEdges) {
				undo.undoEdit();
			}
			this.linkedEdges.clear();

			for(Entry<String,CoercedFigure> entry : this.coercedFigures.entrySet()) {
				this.figure.addAssociatedFigureIntoView(entry.getKey(), entry.getValue());
			}
		}

	} // class NodeFigureRemovalUndo

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationFigureAdditionUndo extends AbstractUndoable {

		private static final long serialVersionUID = 514552641013896102L;

		private final Rectangle2f bounds;
		private final DecorationFigure figure;

		/**
		 * @param bounds
		 * @param figure
		 */
		public DecorationFigureAdditionUndo(Rectangle2f bounds, DecorationFigure figure) {
			this.bounds = bounds;
			this.figure = figure;
		}

		@Override
		public void doEdit() {
			this.figure.setBounds(
					this.bounds.getMinX(),
					this.bounds.getMinY(),
					this.bounds.getWidth(),
					this.bounds.getHeight());
			addFigure(this.figure);
		}

		@Override
		public void undoEdit() {
			removeFigure(this.figure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			if (this.figure!=null) {
				String txt = this.figure.toString();
				if (txt!=null && !txt.isEmpty()) {
					return Locale.getString(FigureView.class, "UNDO_PRESENTATION_DECORATION_ADDITION_1", txt); //$NON-NLS-1$
				}
			}
			return Locale.getString(FigureView.class, "UNDO_PRESENTATION_DECORATION_ADDITION_n"); //$NON-NLS-1$
		}

	} // class DecorationFigureAdditionUndo

}
