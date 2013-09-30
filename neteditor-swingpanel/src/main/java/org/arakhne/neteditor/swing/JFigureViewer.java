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
package org.arakhne.neteditor.swing ;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
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

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.TransferHandler;
import javax.swing.UIManager;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Vector2D;
import org.arakhne.afc.math.matrix.Transform2D;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.afc.ui.ZoomableContext;
import org.arakhne.afc.ui.actionmode.SelectableInteractionEvent;
import org.arakhne.afc.ui.actionmode.SelectableInteractionListener;
import org.arakhne.afc.ui.awt.AwtUtil;
import org.arakhne.afc.ui.awt.DefaultLODGraphics2D;
import org.arakhne.afc.ui.awt.ExceptionListener;
import org.arakhne.afc.ui.awt.LODGraphics2D;
import org.arakhne.afc.ui.event.PointerEvent;
import org.arakhne.afc.ui.selection.Selectable;
import org.arakhne.afc.ui.selection.SelectionEvent;
import org.arakhne.afc.ui.selection.SelectionListener;
import org.arakhne.afc.ui.selection.SelectionManager;
import org.arakhne.afc.ui.swing.JPopupTextField;
import org.arakhne.afc.ui.swing.event.KeyEventSwing;
import org.arakhne.afc.ui.swing.event.PointerEventSwing;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.swing.undo.UndoManagerSwing;
import org.arakhne.afc.ui.swing.undo.UndoableGroupSwing;
import org.arakhne.afc.ui.swing.zoompanel.ZoomableGraphics2D;
import org.arakhne.afc.ui.swing.zoompanel.ZoomablePanel;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.undo.UndoableGroup;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Colors;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.afc.ui.vector.VectorGraphics2D;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.afc.ui.vector.awt.DelegatedVectorGraphics2D;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.factory.CollisionAvoider;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.TextFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.graphics.ShadowViewGraphics2D;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.graphics.ViewGraphicsUtil;
import org.arakhne.neteditor.fig.graphics.ViewGraphicsUtil.Factory;
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
import org.arakhne.neteditor.swing.actionmode.ActionModeManager;
import org.arakhne.neteditor.swing.actionmode.ActionModeOwner;
import org.arakhne.neteditor.swing.actionmode.base.BaseMode;
import org.arakhne.neteditor.swing.dnd.FigureTransferHandler;
import org.arakhne.neteditor.swing.event.FigureEvent;
import org.arakhne.neteditor.swing.event.FigureListener;
import org.arakhne.neteditor.swing.graphics.DelegatedViewGraphics2D;
import org.arakhne.neteditor.swing.selection.JSelectionManager;

/** This class provides a viewer for manipulating graphical network
 *  The editor (JFigurePanel) is the central class of the graph editing
 *  framework.  All the net-level models, graphical objects, layers,
 *  editor modes, editor commands, and supporting dialogs and frames are
 *  implemented in their own classes.
 *  <p>
 *  
 * @param <G> is the type of the graph supported by this panel.
 * @author $Author: hannoun$
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JFigureViewer<G extends Graph<?,?,?,?>> extends ZoomablePanel
implements ViewComponentContainer<Figure, G> {

	private static final long serialVersionUID = 2277117769809745385L;

	/** Precision in pixels for the clicking action (in pixels).
	 */
	public static final int CLICK_PRECISION = 5;

	static {
		ViewGraphicsUtil.setFactory(new ViewGraphicsFactory());
	}


	private final UUID viewID = UUID.randomUUID();

	private volatile LinkedList<Figure> figures = new LinkedList<Figure>();

	private SoftReference<Rectangle2f> documentBounds = null;

	private G graph; 

	private FigureFactory<G> figureFactory = null;

	private boolean isFigureAutoAdded = true;

	private float hitPrecision = CLICK_PRECISION;

	private final CollisionAvoider collisionAvoider = new CAvoider();

	private boolean isAxisDraw = false;
	private boolean isShadowDraw = true;
	private boolean isOutsideGrayed = false;

	private final Class<G> supportedGraphType;

	/**
	 * TransferHandler used if one hasn't been supplied by the UI.
	 */
	private static FigureTransferHandler defaultTransferHandler;

	private final JSelectionManager selectionManager;
	private final UndoManagerSwing undoManager;
	private final ModeManagerOwner mode;

	private EmbeddedTextFigureEditor popupField = null;

	private boolean isEditable = true;
	private boolean isSelectionEnabled = true;
	private final boolean isAlwaysRemovingModelObjects;

	private boolean skipFigureModelUnlink = false;

	/** Lock resource that may be used for deletion actions.
	 */
	protected final ReentrantLock deletionLock = new ReentrantLock();

	/** Construct a new JFigurePanel.
	 *
	 * @param supportedGraphType is the type supported by this viewer.
	 * @param g is the graph to display.
	 * @param figureFactory is the factory for figures.
	 * @param isAlwaysRemovingModelObjects is <code>true</code> to allow this
	 * base mode to remove the model objects each time an associated figure
	 * is removed; <code>false</code> to permits to the user to select
	 * the figure or the model object to be removed.
	 */
	public JFigureViewer(Class<G> supportedGraphType, G g, FigureFactory<G> figureFactory, boolean isAlwaysRemovingModelObjects) {
		super();
		assert(supportedGraphType!=null);
		assert(g!=null);
		assert(figureFactory!=null);
		this.isAlwaysRemovingModelObjects = isAlwaysRemovingModelObjects;
		this.supportedGraphType = supportedGraphType;
		this.graph = g;
		this.figureFactory = figureFactory;
		this.figureFactory.addCollisionAvoider(this.collisionAvoider);
		this.graph.addModelObjectListener(getEventHandler(ModelObjectListener.class));
		setAntiAliased(true);

		this.selectionManager = new JSelectionManager();
		this.undoManager = new UndoManagerSwing();
		this.mode = new ModeManagerOwner();

		this.selectionManager.addSelectionListener(getEventHandler(EventHandler.class));
		addFigureListener(getEventHandler(EventHandler.class));
		addKeyListener(getEventHandler(KeyListener.class));
		this.mode.addSelectableInteractionListener(getEventHandler(SelectableInteractionListener.class));

		setFocusTraversalKeysEnabled(false);
		setFocusable(true);
		requestFocus();

		installDefaultTransferHandlerIfNecessary();
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

	/** Replies the type of graph supported by this viewer.
	 * 
	 * @return the type of graph supported by this viewer.
	 */
	public final Class<G> getSupportedGraphType() {
		return this.supportedGraphType;
	}

	/** Replies the object that permits to avoid collision between a 
	 * figure to insert inside and the already present figures.
	 * @return the collision avoider.
	 */
	public CollisionAvoider getCollisionAvoider() {
		return this.collisionAvoider;
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

	/** Replies if this viewer is interactively editable.
	 * 
	 * @return <code>true</code> if this viewer is editable; otherwise <code>false</code>.
	 */
	public boolean isEditable() {
		return this.isEditable;
	}
	
	/** Replies if the selection manager is enabled
	 * 
	 * @return <code>true</code> if the selection manager is enabled.
	 */
	public boolean isSelectionEnabled() {
		return this.isSelectionEnabled;
	}
	
	/** Set if the selection manager is enabled
	 * 
	 * @param enable
	 */
	public void setSelectionManager(boolean enable) {
		if (enable!=this.isSelectionEnabled) {
			this.isSelectionEnabled = enable;
			if (!this.isSelectionEnabled)
				this.selectionManager.clear();
		}
	}
	

	/** Set if this viewer is interactively editable.
	 * 
	 * @param editable is <code>true</code> if this viewer is editable; otherwise <code>false</code>.
	 */
	public void setEditable(boolean editable) {
		if (editable!=this.isEditable) {
			this.isEditable = editable;
			firePropertyChange("isEditable", !editable, editable); //$NON-NLS-1$
		}
	}

	/** Replies the background color used for the selection of objects.
	 * 
	 * @return the selection color.
	 */
	@SuppressWarnings("static-method")
	public java.awt.Color getSelectionBackground() {
		return UIManager.getColor("EditorPane.selectionBackground"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Color getSelectionBackgroundColor() {
		java.awt.Color c = getSelectionBackground();
		return VectorToolkit.color(c.getRGB(), true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getBackgroundColor() {
		return Colors.WHITE;
	}

	/** Replies the foreground color used for the selection of objects.
	 * 
	 * @return the selection color.
	 */
	@SuppressWarnings("static-method")
	public java.awt.Color getSelectionForeground() {
		return UIManager.getColor("EditorPane.selectionForeground"); //$NON-NLS-1$
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Color getSelectionForegroundColor() {
		java.awt.Color c = getSelectionForeground();
		return VectorToolkit.color(c.getRGB(), true);
	}

	/** Replies the precision of the clicks (in pixels).
	 * 
	 * @return the precision of the clicks (in pixels).
	 */
	public float getClickPrecision() {
		return this.hitPrecision;
	}

	/** Set the precision of the clicks (in pixels).
	 * Byt default, the value of {@link #CLICK_PRECISION}
	 * is used.
	 * 
	 * @param precision is the precision of the clicks, in pixels.
	 */
	public void setClickPrecision(float precision) {
		if (precision>=0 && precision!=this.hitPrecision) {
			float old = this.hitPrecision;
			this.hitPrecision = precision;
			firePropertyChange("hitPrecision", old, this.hitPrecision); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EventListener createEventHandler() {
		return new EventHandler();
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
	 * {@inheritDoc}
	 */
	@Override
	public final UUID getUUID() {
		return this.viewID;
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
		if (auto!=this.isFigureAutoAdded) {
			boolean old = this.isFigureAutoAdded;
			this.isFigureAutoAdded = auto;
			firePropertyChange("isfigureautoadded", old, this.isFigureAutoAdded); //$NON-NLS-1$
		}
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
			FigureFactory<? extends G> old = this.figureFactory;
			this.figureFactory.removeCollisionAvoider(this.collisionAvoider);
			this.figureFactory = factory;
			this.figureFactory.addCollisionAvoider(this.collisionAvoider);
			firePropertyChange("figurefactory", old, this.figureFactory); //$NON-NLS-1$
		}
	}

	/**
	 * Replies the graph displayed in this viewer.
	 * 
	 * @return the graph.
	 */
	public synchronized G getGraph() {
		return this.graph;
	}

	/** set the graph associated to this panel.
	 * 
	 * @param g is the graph.
	 */
	public synchronized void setGraph(G g) {
		if (g!=null && g!=this.graph) {
			ModelObjectListener eh = getEventHandler(ModelObjectListener.class);
			G old = this.graph;
			if (this.graph!=null) 
				this.graph.removeModelObjectListener(eh);
			this.graph = g;
			Iterator<Figure> iterator = this.figures.iterator();
			Figure fig;
			while (iterator.hasNext()) {
				fig = iterator.next();
				fig.setViewComponentContainer(this);
				removeFigureListeners(fig);
				iterator.remove();
			}
			this.documentBounds = null;
			this.graph.addModelObjectListener(eh);
			firePropertyChange("graph", old, this.graph); //$NON-NLS-1$
			refreshWorkspaceSize();
			repaint();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Iterator<Figure> iterator() {
		return this.figures.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getFigureCount() {
		return this.figures.size();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Figure getFigureAt(int index) {
		return this.figures.get(index);
	}

	/**
	 * {@inheritDoc}
	 * @see #importFigure(DecorationFigure)
	 * @see #importGraph(String, Graph, Map)
	 */
	@Override
	public int addFigure(Figure component) {
		int position = -1;
		if (component!=null) {
			boolean firstComponent = this.figures.isEmpty();
			position = 0;
			this.figures.add(position,component);
			this.documentBounds = null;
			component.setViewComponentContainer(this);
			addFigureListeners(component);
			refreshWorkspaceSize();
			if (firstComponent) setZoomFactorForPixelRatio(1);
			fireFigureAdded(component);
		}
		return position;
	}

	/**
	 * {@inheritDoc}
	 * This function also unlink the model object associated to the figure.
	 * @see #removeFigure(Figure, boolean)
	 */
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
		try {
			int position = -1;
			this.deletionLock.lock();
			if (component!=null) {
				position = this.figures.indexOf(component);
				if (position>=0) {
					this.figures.remove(position);
					component.setViewComponentContainer(null);
					removeFigureListeners(component);
					if (!this.skipFigureModelUnlink && 
							unlinkToModelObject &&
							component instanceof ModelObjectFigure<?>) {
						((ModelObjectFigure<?>)component).setModelObject(null);
					}
					this.documentBounds = null;
					refreshWorkspaceSize();
					fireFigureRemoved(component);
					repaint();
				}
			}
			return position;
		}
		finally {
			this.deletionLock.unlock();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Figure removeFigureAt(int index) {
		Figure fig = this.figures.remove(index);
		if (fig!=null) {
			fig.setViewComponentContainer(null);
			removeFigureListeners(fig);
			this.documentBounds = null;
			refreshWorkspaceSize();
			fireFigureRemoved(fig);
		}
		return fig;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeAllFigures() {
		if (!this.figures.isEmpty()) {
			List<Figure> oldFigures = this.figures;
			this.figures = new LinkedList<Figure>();

			for(Figure fig : oldFigures) {
				fig.setViewComponentContainer(null);
				removeFigureListeners(fig);
			}

			this.documentBounds = null;
			refreshWorkspaceSize();

			for(Figure fig : oldFigures) {
				fireFigureRemoved(fig);
			}
		}
	}

	/** Replies if the axis are drawn.
	 * 
	 * @return <code>true</code> if the axis are drawn;
	 * otherwise <code>false</code>.
	 */
	public boolean isAxisDrawn() {
		return this.isAxisDraw;
	}

	/** Set if the axis are drawn.
	 * 
	 * @param draw is <code>true</code> if the axis are drawn;
	 * otherwise <code>false</code>.
	 */
	public void setAxisDrawn(boolean draw) {
		if (this.isAxisDraw!=draw) {
			this.isAxisDraw = draw;
			firePropertyChange("isAxisDraw", !draw, draw); //$NON-NLS-1$
			repaint();
		}
	}

	/** Replies if the area outside the workspace is grayed.
	 * 
	 * @return <code>true</code> if the outside area is grayed;
	 * otherwise <code>false</code>.
	 */
	public boolean isOutsideGrayed() {
		return this.isOutsideGrayed;
	}

	/** Set if the area outside the workspace is grayed.
	 * 
	 * @param gray is <code>true</code> if the outside area is grayed;
	 * otherwise <code>false</code>.
	 */
	public void setOutsideGrayed(boolean gray) {
		if (this.isOutsideGrayed!=gray) {
			this.isOutsideGrayed = gray;
			firePropertyChange("isOutsideGrayed", !gray, gray); //$NON-NLS-1$
			repaint();
		}
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
			firePropertyChange("isShadowDraw", !draw, draw); //$NON-NLS-1$
			repaint();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintViewComponents(ViewGraphics2D g) {
		Rectangle2f bounds;

		VectorToolkit.prepareDrawing(g);

		if (isShadowDrawn() && g.getLOD().compareTo(Graphics2DLOD.NORMAL_LEVEL_OF_DETAIL)>=0) {
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
		}
		
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

		VectorToolkit.finalizeDrawing(g);		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintPanel(ZoomableGraphics2D gzoom) {
		if (isOutsideGrayed() && !gzoom.isPrinting()) {
			paintOutsideGrayed(gzoom);
		}
		if (isAxisDrawn()) paintAxis(gzoom);
		paintViewComponents(new DelegatedViewGraphics2D<ZoomableGraphics2D>(gzoom));
		getModeManager().paint(gzoom);
	}

	/** Paint the outside of the workspace with gray.
	 * 
	 * @param g
	 */
	protected void paintOutsideGrayed(ZoomableGraphics2D g) {
		java.awt.Color c = getBackground();
		java.awt.Color lightLightGray = c.darker();
		java.awt.Dimension dim = getSize(); 
		g.setColor(lightLightGray);
		g.fill(new Rectangle2D.Double(
				g.pixel2logical_x(0),
				g.pixel2logical_y(0),
				g.pixel2logical_size((float)dim.getWidth()),
				g.pixel2logical_size((float)dim.getHeight())));
		Rectangle2D docBounds = getDocumentRect();
		if (docBounds!=null && !docBounds.isEmpty()) {
			g.clearRect(
					(float)docBounds.getX(), (float)docBounds.getY(),
					(float)docBounds.getWidth(), (float)docBounds.getHeight());
		}
	}

	/** Paint the axis.
	 * 
	 * @param g
	 */
	protected void paintAxis(ZoomableGraphics2D g) {
		GeneralPath path = new GeneralPath();

		path.moveTo(5, 15);
		path.lineTo(5, 5);
		path.lineTo(15, 5);

		path.moveTo(12, 2);
		path.lineTo(15, 5);
		path.lineTo(12, 8);

		path.moveTo(2, 12);
		path.lineTo(5, 15);
		path.lineTo(8, 12);

		float x0 = g.logical2pixel_x(0);
		float y0 = g.logical2pixel_y(0);

		Graphics2D rg = g.getRenderingGraphics();
		rg.setColor(java.awt.Color.DARK_GRAY);
		rg.draw(path);
		rg.drawString("x", 15, 5); //$NON-NLS-1$
		rg.drawString("y", 5, 15); //$NON-NLS-1$

		rg.setColor(java.awt.Color.LIGHT_GRAY);
		rg.draw(new Line2D.Float(x0, 0, x0, getHeight()));
		rg.draw(new Line2D.Float(0, y0, getWidth(), y0));
	}

	/** Repaint the graphical area around the specified figure.
	 * If the specified object is <code>null</code>
	 * the entire area is repainted.
	 * 
	 * @param figure
	 */
	public void repaint(Figure figure) {
		if (figure==null) repaint();
		else repaint(figure.getDamagedBounds());
	}

	/** Repaint the graphical area around the figures associated to
	 * the specified object. If the specified object is <code>null</code>
	 * the entire area is repainted.
	 * 
	 * @param obj
	 */
	public void repaint(ModelObject obj) {
		if (obj!=null) {
			ViewComponent vc = obj.getViewBinding().getView(getUUID(), ViewComponent.class);
			if (vc!=null) {
				repaint(vc.getDamagedBounds());
				return ;
			}
		}
		repaint();
	}

	/** Repaint the area of the component that is corresponding
	 * to the given bounds.
	 * 
	 * @param bounds
	 */
	public void repaint(Rectangle2f bounds) {
		if (bounds==null) repaint();
		else repaint(
				(int)logical2pixel_x(bounds.getMinX()) - 2,
				(int)logical2pixel_y(bounds.getMinY()) - 2,
				(int)logical2pixel_size(bounds.getWidth()) + 4,
				(int)logical2pixel_size(bounds.getHeight()) + 4);
	}

	/** Compute and reply the bounds of the entire document.
	 * 
	 * @return the bounds of the entire document.
	 */
	protected Rectangle2f calcDocumentBounds() {
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

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Rectangle2f getComponentBounds() {
		Rectangle2f r = (this.documentBounds==null) ? null : this.documentBounds.get();
		if (r==null) {
			r = calcDocumentBounds();
			this.documentBounds = new SoftReference<Rectangle2f>(r);
		}
		return r;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle2D getDocumentRect() {
		Rectangle2f bb = getComponentBounds();
		if (bb==null) return null;
		return new Rectangle2D.Float(
				bb.getMinX(),
				bb.getMinY(),
				bb.getWidth(),
				bb.getHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle2D getVisibleDocumentRect() {
		// All the view components are visible, ie displayed.
		return getDocumentRect();
	}

	/** Replies the figures in the container.
	 * 
	 * @return the figures.
	 */
	public Collection<? extends Figure> getFigures() {
		return Collections.unmodifiableList(this.figures);
	}


	/** Replies the figure that has the specified point in
	 * its shape. If you want to test the point against
	 * the bounds of the figures, please use
	 * {@link #getFigureWithBoundsAt(float, float)}.
	 * 
	 * @param x
	 * @param y
	 * @return the hit figure, or <code>null</code> if none.
	 * @see #getFigureWithBoundsAt(float, float)
	 */
	public Figure getFigureAt(float x, float y) {
		float precision = pixel2logical_size(getClickPrecision());
		Circle2f circle = new Circle2f(x, y, precision);
		for(Figure figure : this.figures) {
			if (figure.intersects(circle)) {
				return figure;
			}
		}
		return null;
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
		float precision = pixel2logical_size(getClickPrecision());
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

	/** Replies the figures that are inside the specified bounds.
	 * 
	 * @param bounds
	 * @return the hit figures, never <code>null</code>.
	 */
	public Set<Figure> getFiguresIn(Rectangle2f bounds) {
		Set<Figure> figures = new TreeSet<Figure>();
		for(Figure figure : this.figures) {
			if (figure.contains(bounds)) {
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

	/** Replies the first figure that are inside the specified bounds.
	 * 
	 * @param bounds
	 * @return the hit figure, or <code>null</code>.
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

	/** Import the graph and the specified figures into this editor.
	 * 
	 * @param undoLabel is the label of the undoable edit to reply.
	 * @param graph is the graph to integrate.
	 * @param figures are the figures (classified per view id) to import, from front to background.
	 * @return the undoable edit that permits to undo the import.
	 */
	public Undoable importGraph(String undoLabel, G graph, Map<UUID,List<ViewComponent>> figures) {
		MajorImportUndo cEdit = new MajorImportUndo(undoLabel);

		G original = getGraph();
		GraphImportUndo gUndo = new GraphImportUndo(original, graph, false);
		gUndo.doEdit();
		cEdit.addEdit(gUndo);

		for(List<ViewComponent> theSet : figures.values()) {
			ViewComponent o;
			for(int i=theSet.size()-1; i>=0; --i) {
				o = theSet.get(i);
				FigureImportUndo undo = new FigureImportUndo(o, false);
				undo.doEdit();
				cEdit.addEdit(undo);
			}
		}

		cEdit.end();
		return cEdit;
	}

	/** Import the graph and the specified figures into this editor.
	 * 
	 * @param undoLabel is the label of the undoable edit to reply.
	 * @param graph is the graph to integrate.
	 * @param figures are the figures (classified per view id) to import, from front to background.
	 * @return the undoable edit that permits to undo the import.
	 */
	public Undoable importGraph(String undoLabel, G graph, List<ViewComponent> figures) {
		MajorImportUndo cEdit = new MajorImportUndo(undoLabel);

		G original = getGraph();
		GraphImportUndo gUndo = new GraphImportUndo(original, graph, false);
		gUndo.doEdit();
		cEdit.addEdit(gUndo);

		ViewComponent o;
		for(int i=figures.size()-1; i>=0; --i) {
			o = figures.get(i);
			FigureImportUndo undo = new FigureImportUndo(o, false);
			undo.doEdit();
			cEdit.addEdit(undo);
		}

		cEdit.end();
		return cEdit;
	}

	/** Import the specified figure into this editor and
	 * replies an undoable edit.
	 * 
	 * @param figure is the figure to import.
	 * @return the undoable edit that permits to undo the import.
	 */
	public Undoable importFigure(DecorationFigure figure) {
		FigureImportUndo undo = new FigureImportUndo(figure, true);
		undo.doEdit();
		return undo;
	}

	/** Notifies the listeners about an error that occurs in one
	 * of the JFigureEditor components.
	 * 
	 * @param error
	 */
	public void fireError(Throwable error) {
		boolean treated = false;
		for (ExceptionListener listener : this.listenerList.getListeners(ExceptionListener.class)) {
			if (listener.exceptionThrown(error)) {
				treated = true;
			}
		}
		if (!treated) {
			throw new RuntimeException(error);
		}
	}

	/** Add listener on exceptions.
	 * 
	 * @param listener
	 */
	public void addExceptionListener(ExceptionListener listener) {
		this.listenerList.add(ExceptionListener.class, listener);
	}

	/** Remove listener on exceptions.
	 * If there is no listener on exceptions or all the listeners
	 * are replying <code>false</code>, the default
	 * catching behavior is executed: the exception is forwaded to the VM.
	 * 
	 * @param listener
	 */
	public void removeExceptionListener(ExceptionListener listener) {
		this.listenerList.remove(ExceptionListener.class, listener);
	}

	/** Replies the selection manager.
	 * 
	 * @return the selection manager.
	 */
	public JSelectionManager getSelectionManager() {
		return this.selectionManager;
	}

	/**
	 * Replies the manager of undoable actions.
	 * 
	 * @return the undo manager.
	 */
	public UndoManagerSwing getUndoManager() {
		return this.undoManager;
	}

	/**
	 * Replies the action mode manager used by this viewser.
	 * 
	 * @return the manager.
	 */
	public ActionModeManager getModeManager() {
		return this.mode.getManager();
	}

	/**
	 * Transfers the currently selected figures
	 * to the system clipboard, removing the contents
	 * from the model.  The current selection is reset.  Does nothing
	 * for <code>null</code> selections.
	 */
	public void cut() {
		if (isEnabled()) {
			if (isEditable())
				invokeAction("cut", TransferHandler.getCutAction()); //$NON-NLS-1$
			else
				invokeAction("copy", TransferHandler.getCopyAction()); //$NON-NLS-1$
		}
	}

	/**
	 * Transfers the currently selected figures
	 * to the system clipboard, leaving the contents
	 * in the text model.  The current selection remains intact.
	 * Does nothing for <code>null</code> selections.
	 */
	public void copy() {
		if (isEnabled()) {
			invokeAction("copy", TransferHandler.getCopyAction()); //$NON-NLS-1$
		}
	}

	/**
	 * Transfers the contents of the system clipboard into the
	 * associated graph.  If the clipboard is empty, does nothing.
	 */
	public void paste() {
		if (isEnabled() && isEditable()) {
			invokeAction("paste", TransferHandler.getPasteAction()); //$NON-NLS-1$
		}
	}

	/**
	 * This is a conveniance method that is only useful for
	 * <code>cut</code>, <code>copy</code> and <code>paste</code>.  If
	 * an <code>Action</code> with the name <code>name</code> does not
	 * exist in the <code>ActionMap</code>, this will attemp to install a
	 * <code>TransferHandler</code> and then use <code>altAction</code>.
	 */
	private void invokeAction(String name, Action altAction) {
		ActionMap map = getActionMap();
		Action action = null;

		if (map != null) {
			action = map.get(name);
		}
		if (action == null) {
			installDefaultTransferHandlerIfNecessary();
			action = altAction;
		}
		action.actionPerformed(new ActionEvent(this,
				ActionEvent.ACTION_PERFORMED, (String)action.
				getValue(Action.NAME),
				EventQueue.getMostRecentEventTime(),
				getCurrentEventModifiers()));
	}

	private static int getCurrentEventModifiers() {
		int modifiers = 0;
		AWTEvent currentEvent = EventQueue.getCurrentEvent();
		if (currentEvent instanceof InputEvent) {
			modifiers = ((InputEvent)currentEvent).getModifiers();
		}
		else if (currentEvent instanceof ActionEvent) {
			modifiers = ((ActionEvent)currentEvent).getModifiers();
		}
		return modifiers;
	}

	/**
	 * If the current <code>TransferHandler</code> is null, this will
	 * install a new one.
	 */
	private void installDefaultTransferHandlerIfNecessary() {
		if (getTransferHandler() == null) {
			if (defaultTransferHandler == null) {
				defaultTransferHandler = new FigureTransferHandler();
			}
			setTransferHandler(defaultTransferHandler);
		}
	}

	/** Open the popup editor for the specified figure.
	 * 
	 * @param figure
	 * @param mousePosition is the position of the mouse when opening the
	 * editor; may be <code>null</code> if the mouse position is unknown.
	 */
	void openPopupEditor(TextFigure figure, Point2D mousePosition) {
		if (this.popupField!=null) {
			this.popupField.setVisible(false);
		}
		this.popupField = new EmbeddedTextFigureEditor(this, figure, mousePosition);
		this.popupField.setVisible(true);
		repaint();
	}

	/** Cancel and close the popup editor.
	 */
	void closePopupEditor() {
		EmbeddedTextFigureEditor e = this.popupField;
		this.popupField = null;
		if (e!=null) {
			e.setVisible(false);
			repaint();
		}
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class EmbeddedTextFigureEditor extends JPopupTextField
	implements ComponentListener, PropertyChangeListener {

		private static final long serialVersionUID = 7710377866565725226L;

		private final WeakReference<TextFigure> figure;

		/**
		 * @param component
		 * @param figure
		 * @param mousePosition
		 */
		public EmbeddedTextFigureEditor(
				JFigureViewer<?> component,
				TextFigure figure,
				Point2D mousePosition) {
			super(component,
					null,
					VectorToolkit.nativeUIObject(java.awt.Color.class, figure.getFillColor()));

			this.figure = new WeakReference<TextFigure>(figure);

			Dimension d = figure.getSize();
			setPreferredSize(new java.awt.Dimension(
					(int)Math.ceil(component.logical2pixel_size(d.width())),
					(int)Math.ceil(component.logical2pixel_size(d.height()))));
			setPreferredLocation(
					component.logical2pixel_x(figure.getX()),
					component.logical2pixel_y(figure.getY()));

			Font font = component.getFont();
			font = component.logical2pixel(font);
			getTextField().setFont(font);
			getTextField().setBorder(null);

			String text = figure.getText();
			setText(text);

			getTextField().setSelectionStart(0);
			getTextField().setSelectionEnd(text.length());

			addComponentListener(this);
			component.addPropertyChangeListener(this);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentResized(ComponentEvent e) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentMoved(ComponentEvent e) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentShown(ComponentEvent e) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPopupFieldClosed() {
			((JFigureViewer<?>)getOwner()).closePopupEditor();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onValidationAction() {
			TextFigure fig = this.figure.get();
			if (fig!=null) {
				String field = getText();
				fig.setText(field);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentHidden(ComponentEvent e) {
			((JFigureViewer<?>)getOwner()).closePopupEditor();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if ("zoomFactor".equals(evt.getPropertyName()) //$NON-NLS-1$
					||"targetPoint".equals(evt.getPropertyName())) { //$NON-NLS-1$
				((JFigureViewer<?>)getOwner()).closePopupEditor();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void setVisible(boolean aFlag) {
			if (!aFlag) {
				removeComponentListener(this);
				removePropertyChangeListener(this);
			}
			super.setVisible(aFlag);
		}

	} // class EmbeddedTextFigureEditor

	/**
	 * @author $Author: hannoun$
	 * @author $Author: galland$
	 * @author $Author: baumgartner$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class EventHandler extends ZoomablePanel.EventHandler
	implements ViewComponentLayoutListener, ModelObjectListener,
	ViewComponentBindingListener, SelectionListener, FigureListener, KeyListener,
	SelectableInteractionListener, ViewComponentChangeListener, ViewComponentPropertyChangeListener {

		/**
		 */
		public EventHandler() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void selectionChanged(SelectionEvent event) {
			repaint();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void figureAdded(FigureEvent event) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void figureRemoved(FigureEvent event) {
			getSelectionManager().remove(event.getRemovedFigure());
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void figureChanged(FigureEvent event) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseClicked(MouseEvent event) {
			super.mouseClicked(event);
			getModeManager().pointerClicked(new PointerEventSwing(event));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseDragged(MouseEvent event) {
			super.mouseDragged(event);
			getModeManager().pointerDragged(new PointerEventSwing(event));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseEntered(MouseEvent e) {
			super.mouseEntered(e);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseExited(MouseEvent e) {
			super.mouseExited(e);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseMoved(MouseEvent event) {
			super.mouseMoved(event);
			getModeManager().pointerMoved(new PointerEventSwing(event));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mousePressed(MouseEvent event) {
			super.mousePressed(event);
			getModeManager().pointerPressed(new PointerEventSwing(event));
			requestFocusInWindow();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void mouseReleased(MouseEvent event) {
			super.mouseReleased(event);
			getModeManager().pointerReleased(new PointerEventSwing(event));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void keyTyped(KeyEvent e) {
			getModeManager().keyTyped(new KeyEventSwing(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void keyPressed(KeyEvent e) {
			getModeManager().keyPressed(new KeyEventSwing(e));
			requestFocusInWindow();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void keyReleased(KeyEvent e) {
			getModeManager().keyReleased(new KeyEventSwing(e));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(SelectableInteractionEvent event) {
			if (!event.isConsumed() && event.getSource() instanceof TextFigure) {
				Point2D p = null;
				if (event.getPointerEvent()!=null) {
					PointerEvent evt = event.getPointerEvent();
					p = new Point2f(evt.getX(), evt.getY());
				}
				openPopupEditor(
						(TextFigure)event.getSource(),
						p);
				event.consume();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void popupPerformed(SelectableInteractionEvent event) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean figureDeletionPerformed(Selectable figure, boolean deleteModel) {
			return true;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void componentRepaint(ViewComponent component, boolean boundsChanged) {
			if (boundsChanged) {
				JFigureViewer.this.documentBounds = null;
				refreshWorkspaceSize();
			}
			Rectangle2f bb = component.getDamagedBounds();
			repaint(bb);
		}

		/**
		 * {@inheritDoc}
		 */
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

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentCreation(Figure parent, ModelObject modelObject) {
			getFigureFactory().createSubFigureInside(
					getUUID(),
					getGraph(),
					parent,
					modelObject);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentRemoval(Figure parent, SubFigure subfigure) {
			getFigureFactory().removeSubFigureFrom(
					getUUID(),
					getGraph(),
					parent,
					subfigure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentAddition(Figure figure) {
			assert(figure!=null);
			JFigureViewer.this.addFigure(figure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentRemoval(Figure figure) {
			assert(figure!=null);
			JFigureViewer.this.removeFigure(figure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelPropertyChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelContainerChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelLinkChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelContentChanged(ModelObjectEvent event) {
			// Nothing to repaint, because an repainting event
			// will arrive from the figure itself
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelComponentAdded(ModelObjectEvent event) {
			if (isFigureAutomaticallyAdded() && event.getSource()==getGraph()) {
				ModelObject addedObject = event.getAddedObject();
				if (addedObject!=null) {
					Figure figure = addedObject.getViewBinding().getView(getUUID(), Figure.class);
					if (figure==null) {
						FigureFactory<G> factory = getFigureFactory();
						Rectangle2D bb = getVisibleDocumentRect();
						figure = factory.createFigureFor(
								getUUID(),
								new Rectangle2f(
										(float)bb.getMinX(),
										(float)bb.getMinY(),
										(float)bb.getWidth(),
										(float)bb.getHeight()),
										getGraph(), addedObject);
					}
					if (figure!=null) addFigure(figure);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelComponentRemoved(ModelObjectEvent event) {
			if (event.getSource()==getGraph()) {
				Figure fig = event.getRemovedObject().getViewBinding().getView(getUUID(), Figure.class);
				if (fig!=null) {
					removeFigure(fig);
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void propertyChange(ViewComponentPropertyChangeEvent event) {
			ViewComponent vc = event.getSource();
			if (vc instanceof Figure) {
				fireFigureChanged((Figure)vc);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void componentChange(ViewComponentChangeEvent event) {
			ViewComponent vc = event.getSource();
			if (vc instanceof Figure) {
				fireFigureChanged((Figure)vc);
			}
		}

	} // class EventHandler

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
			for(Figure figure : JFigureViewer.this) {
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
			for(Figure figure : JFigureViewer.this) {
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
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FigureImportUndo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = 1120366072427468090L;

		private final ViewComponent addedFigure;
		private final ModelObject modelObject;
		private boolean isSignificant;

		public FigureImportUndo(ViewComponent addedFigure, boolean isSignificant) {
			this.isSignificant = isSignificant;
			this.addedFigure = addedFigure;
			if (addedFigure instanceof ModelObjectView<?>) {
				this.modelObject = ((ModelObjectView<?>)addedFigure).getModelObject();
			}
			else {
				this.modelObject = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSignificant() {
			return this.isSignificant;
		}

		@Override
		public void doEdit() {
			UUID newViewId = JFigureViewer.this.getUUID();

			if (this.addedFigure instanceof Figure) {
				Figure aFigure = (Figure)this.addedFigure;

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

				if (this.modelObject!=null) {
					ViewBinding binding = this.modelObject.getViewBinding();
					View old = binding.replaceView(oldViewId, newViewId);
					if (old instanceof Figure && old!=this.addedFigure) {
						JFigureViewer.this.removeFigure((Figure)old);
					}
				}
				JFigureViewer.this.addFigure(aFigure);
			}
			else if (this.addedFigure instanceof SubFigure) {
				if (this.modelObject!=null) {
					UUID oldViewId = this.addedFigure.getViewUUID();
					ViewBinding binding = this.modelObject.getViewBinding();
					binding.replaceView(
							oldViewId,
							newViewId);
				}
				this.addedFigure.setViewUUID(newViewId);
			}
		}

		@Override
		public void undoEdit() {
			if (this.addedFigure instanceof Figure) {
				Figure aFigure = (Figure)this.addedFigure;
				JFigureViewer.this.removeFigure(aFigure, false);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(JFigureViewer.class, "UNDO_PRESENTATION_ADD_FIGURE", this.addedFigure.getName()); //$NON-NLS-1$
		}

	} // class FigureUndo

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	@SuppressWarnings("rawtypes")
	private class GraphImportUndo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = 8418338290213631479L;

		private final Graph target;
		private final Graph source;
		private final Set<UUID> ids = new TreeSet<UUID>();
		private boolean isSignificant;

		public GraphImportUndo(Graph targetGraph, Graph<?,?,?,?> sourceGraph, boolean isSignificant) {
			this.isSignificant = isSignificant;
			this.target = targetGraph;
			this.source = sourceGraph;
			for(Edge edge : sourceGraph.getEdges()) {
				this.ids.add(edge.getUUID());
			}
			for(Node node : sourceGraph.getNodes()) {
				this.ids.add(node.getUUID());
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean isSignificant() {
			return this.isSignificant;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void doEdit() {
			boolean isAuto = JFigureViewer.this.isFigureAutomaticallyAdded();
			JFigureViewer.this.setFigureAutomaticallyAdded(false);
			try {
				this.target.moveFromGraph(this.source);
			}
			finally {
				JFigureViewer.this.setFigureAutomaticallyAdded(isAuto);
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		public void undoEdit() {
			boolean isAuto = JFigureViewer.this.isFigureAutomaticallyAdded();
			JFigureViewer.this.setFigureAutomaticallyAdded(false);
			try {
				this.source.moveFromGraph(this.target, this.ids);
			}
			finally {
				JFigureViewer.this.setFigureAutomaticallyAdded(isAuto);
			}
		}

	} // class GraphUndo

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class MajorImportUndo extends UndoableGroupSwing {

		private static final long serialVersionUID = -3408101108910149014L;

		/**
		 * @param label
		 */
		public MajorImportUndo(String label) {
			super(label);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			String label = super.getPresentationName();
			return Locale.getString(JFigureViewer.class, "UNDO_PRESENTATION_ADD", label); //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getRedoPresentationName() {
			return UIManager.getString("AbstractUndoableEdit.redoText")+" "+getPresentationName(); //$NON-NLS-1$ //$NON-NLS-2$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getUndoPresentationName() {
			return UIManager.getString("AbstractUndoableEdit.undoText")+" "+getPresentationName();  //$NON-NLS-1$//$NON-NLS-2$
		}

	} // class MajorUndo

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationFigureAdditionUndo extends AbstractCallableUndoableEdit {

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
					return Locale.getString(JFigureViewer.class, "UNDO_PRESENTATION_DECORATION_ADDITION_1", txt); //$NON-NLS-1$
				}
			}
			return Locale.getString(JFigureViewer.class, "UNDO_PRESENTATION_DECORATION_ADDITION_n"); //$NON-NLS-1$
		}

	} // class DecorationFigureAdditionUndo

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private abstract class AbstractFigureRemovalUndo<F extends Figure> extends AbstractCallableUndoableEdit {

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
					return Locale.getString(JFigureViewer.class, "UNDO_PRESENTATION_FIGURE_REMOVAL_1", txt); //$NON-NLS-1$
				}
			}
			return Locale.getString(JFigureViewer.class, "UNDO_PRESENTATION_FIGURE_REMOVAL_n"); //$NON-NLS-1$
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
				JFigureViewer.this.deletionLock.lock();
				b = JFigureViewer.this.skipFigureModelUnlink;
				JFigureViewer.this.skipFigureModelUnlink = !this.disconnectFigureAndModel;
				((Graph)getGraph()).removeEdge(this.edge);
			}
			finally {
				JFigureViewer.this.skipFigureModelUnlink = b;
				JFigureViewer.this.deletionLock.unlock();
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
				JFigureViewer.this.deletionLock.lock();
				b = JFigureViewer.this.skipFigureModelUnlink;
				JFigureViewer.this.skipFigureModelUnlink = !this.disconnectFigureAndModel;

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
				JFigureViewer.this.skipFigureModelUnlink = b;
				JFigureViewer.this.deletionLock.unlock();
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

		@SuppressWarnings("unchecked")
		@Override
		public ViewGraphics2D createViewGraphics(VectorGraphics2D g, boolean antialiasing, boolean isForPrinting, Graphics2DLOD lod) {
			if (g instanceof ViewGraphics2D) return (ViewGraphics2D)g;
			if (g instanceof DelegatedVectorGraphics2D<?>) {
				DelegatedVectorGraphics2D<? extends LODGraphics2D> dvtg = (DelegatedVectorGraphics2D<?  extends LODGraphics2D>)g;
				LODGraphics2D g2d = dvtg.getNativeGraphics2D();
				if (g2d instanceof ViewGraphics2D) return (ViewGraphics2D)g2d;
				DelegatedViewGraphics2D<LODGraphics2D> vg = new DelegatedViewGraphics2D<LODGraphics2D>(g2d);
				return vg;
			}
			if (g instanceof Graphics2D) {
				Graphics2D g2d = (Graphics2D)g;
				DefaultLODGraphics2D lodg = new DefaultLODGraphics2D(
						g2d, null, antialiasing, isForPrinting, lod);
				DelegatedViewGraphics2D<DefaultLODGraphics2D> vg = new DelegatedViewGraphics2D<DefaultLODGraphics2D>(lodg);
				return vg;
			}
			throw new IllegalArgumentException("cannot embed the given VectorGraphics2D into a ViewGraphics2D"); //$NON-NLS-1$
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class ModeManagerOwner implements ActionModeOwner<G> {

		private final ActionModeManager modeManager;

		/**
		 */
		public ModeManagerOwner() {
			this.modeManager = new ActionModeManager(getUUID(), this);
			// use the standard implementation of the modes
			this.modeManager.beginMode(new BaseMode());
		}

		public ActionModeManager getManager() {
			return this.modeManager;
		}

		public void addSelectableInteractionListener(SelectableInteractionListener listener) {
			this.modeManager.addSelectableInteractionListener(listener);
		}

		@Override
		public boolean isAlwaysRemovingModelObjects() {
			return JFigureViewer.this.isAlwaysRemovingModelObjects();
		}

		@Override
		public Object getUIComponent() {
			return JFigureViewer.this;
		}

		@Override
		public void requestFocus() {
			JFigureViewer.this.requestFocus();
		}

		@Override
		public SelectionManager<? super Figure> getSelectionManager() {
			return JFigureViewer.this.getSelectionManager();
		}

		@Override
		public ActionModeManager getModeManager() {
			return this.modeManager;
		}

		@Override
		public UndoManager getUndoManager() {
			return JFigureViewer.this.getUndoManager();
		}

		@Override
		public void setCursor(MouseCursor cursor) {
			JFigureViewer.this.setCursor(AwtUtil.getCursor(cursor));
		}

		@Override
		public void repaint(Rectangle2f bounds) {
			JFigureViewer.this.repaint(bounds);
		}

		@Override
		public void repaint() {
			JFigureViewer.this.repaint();
		}

		@Override
		public float getClickPrecision() {
			return JFigureViewer.this.getClickPrecision();
		}

		@Override
		public ZoomableContext getZoomableContext() {
			return JFigureViewer.this.getZoomableContext();
		}

		@Override
		public void cut() {
			JFigureViewer.this.cut();
		}

		@Override
		public void copy() {
			JFigureViewer.this.copy();
		}

		@Override
		public void paste() {
			JFigureViewer.this.paste();
		}

		@Override
		public boolean isEditable() {
			return JFigureViewer.this.isEnabled() && JFigureViewer.this.isEditable();
		}

		@Override
		public boolean isSelectionEnabled() {
			return JFigureViewer.this.isEnabled() && JFigureViewer.this.isSelectionEnabled();
		}

		@Override
		public void fireError(Throwable error) {
			JFigureViewer.this.fireError(error);
		}

		@Override
		public int getFigureCount() {
			return JFigureViewer.this.getFigureCount();
		}

		@Override
		public Collection<? extends Figure> getFigures() {
			return JFigureViewer.this.getFigures();
		}

		@Override
		public Figure getFigureAt(int index) {
			return JFigureViewer.this.getFigureAt(index);
		}

		@Override
		public Figure getFigureAt(float x, float y) {
			return JFigureViewer.this.getFigureAt(x,y);
		}

		@Override
		public Figure getFigureWithBoundsAt(float x, float y) {
			return JFigureViewer.this.getFigureWithBoundsAt(x, y);
		}

		@Override
		public Set<Figure> getFiguresOn(Shape2f bounds) {
			return JFigureViewer.this.getFiguresOn(bounds);
		}

		@Override
		public Set<Figure> getFiguresIn(Rectangle2f bounds) {
			return JFigureViewer.this.getFiguresIn(bounds);
		}
		
		@Override
		public Figure getFigureIn(Rectangle2f area) {
			return JFigureViewer.this.getFigureIn(area);
		}
		
		@Override
		public Figure getFigureOn(Shape2f area) {
			return JFigureViewer.this.getFigureOn(area);
		}

		@Override
		public java.awt.Color getSelectionBackground() {
			return JFigureViewer.this.getSelectionBackground();
		}

		@Override
		public java.awt.Color getSelectionForeground() {
			return JFigureViewer.this.getSelectionForeground();
		}

		@Override
		public Undoable removeFigure(boolean deleteModel,
				boolean disconnectFigureAndModel, Figure figure) {
			if (!figure.isLocked() && JFigureViewer.this.isEditable()) {
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
			if (JFigureViewer.this.isEditable()) {
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
			if (figure instanceof DecorationFigure && JFigureViewer.this.isEditable()) {
				DecorationFigureAdditionUndo undo = new DecorationFigureAdditionUndo(
						figure.getBounds(), (DecorationFigure)figure);
				undo.doEdit();
				return undo;
			}
			return null;
		}

		@Override
		public G getGraph() {
			return JFigureViewer.this.getGraph();
		}

		@Override
		public FigureFactory<G> getFigureFactory() {
			return JFigureViewer.this.getFigureFactory();
		}

	}

}
