/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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
package org.arakhne.neteditor.fig.view;

import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Collections;
import java.util.EventListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.prefs.Preferences;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.references.WeakValueHashMap;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Colors;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.afc.util.ListenerCollection;
import org.arakhne.neteditor.fig.PropertyNames;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.formalism.AbstractPropertyTooler;

/** This class is the base class for all the drawable components. 
 *
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractViewComponent extends AbstractPropertyTooler implements ViewComponent, ViewComponentConstants, PropertyNames {

	private static final long serialVersionUID = -7365192025415626795L;

	/** Utility function that permits to extract a property image from a map.
	 * This function could reply <code>null</code> if the given map contains
	 * a property with <code>null</code> value. In this case the default
	 * value is ignored. The default value is replied only if the
	 * given map does not contains the <var>name</var>.
	 * The parameter <var>forceDefaultIfNull</var> overrides this behavior
	 * by forcing the use of the default value when the map contains
	 * a <code>null</code>.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @param forceDefaultIfNull indicates if the default value must be replied
	 * when a <code>null</code> value is found in the map.
	 * @return the property value
	 */
	public static Image propGetImage(String name, Image defaultValue, boolean forceDefaultIfNull, Map<String,Object> properties) {
		Object v = properties.get(name);
		Image img = null;
		try {
			if (v instanceof Image) {
				img = (Image)v;
			}
			else if (v instanceof URL) {
				img = VectorToolkit.image((URL)v);
			}
			if (img==null && v instanceof InputStream) {
				img = VectorToolkit.image((InputStream)v);
			}
			if (img==null) {
				img = VectorToolkit.image(v);
			}
		}
		catch(Throwable _) {
			img = null;
		}
		if (img==null && 
				(forceDefaultIfNull || !properties.containsKey(name))) img = defaultValue;
		return img;
	}

	/** Utility function that permits to extract a property color from a map.
	 * This function could reply <code>null</code> if the given map contains
	 * a property with <code>null</code> value. In this case the default
	 * value is ignored. The default value is replied only if the
	 * given map does not contains the <var>name</var>.
	 * The parameter <var>forceDefaultIfNull</var> overrides this behavior
	 * by forcing the use of the default value when the map contains
	 * a <code>null</code>.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @param forceDefaultIfNull indicates if the default value must be replied
	 * when a <code>null</code> value is found in the map.
	 * @return the property value
	 */
	public static Color propGetColor(String name, Color defaultValue, boolean forceDefaultIfNull, Map<String,Object> properties) {
		Object v = properties.get(name);
		Color color = null;
		try {
			if (v instanceof Number) {
				color = VectorToolkit.color(((Number)v).intValue());
			}
			else if (v instanceof Color) {
				color = (Color)v;
			}
			if (color==null) {
				color = VectorToolkit.color(v);
			}
		}
		catch(Throwable _) {
			color = null;
		}
		if (color==null && 
			(forceDefaultIfNull || !properties.containsKey(name))) color = defaultValue;
		return color;
	}

	/** Listener on view component events.
	 */
	private transient ListenerCollection<EventListener> listeners = null;

	private UUID viewUUID;
	private UUID uuid = UUID.randomUUID();

	private float width;
	private float height;
	private float x;
	private float y;
	private float minWidth;
	private float minHeight;
	private float maxWidth;
	private float maxHeight;

	private SoftReference<Point2D> bufferPosition = null;
	private SoftReference<Rectangle2f> bufferBounds = null;
	private SoftReference<Dimension> bufferDimension = null;

	private WeakReference<ViewComponentContainer<?,?>> container = null;

	private Map<String,CoercedFigure> associatedFigures = null;

	private Image icon = null;
	private String name = null;

	/** Construct a new view component.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Fig.
	 * @param y vertical position of this Fig.
	 * @param width width of this Fig.
	 * @param height height of this Fig.
	 */
	public AbstractViewComponent(UUID viewUUID, float x, float y, float width, float height) {
		this.viewUUID = viewUUID;
		this.x = x;
		this.y = y;
		this.width = Math.max(0, width);
		this.height = Math.max(0, height);
		this.minWidth = Math.min(DEFAULT_MINIMAL_SIZE, width);
		this.minHeight = Math.min(DEFAULT_MINIMAL_SIZE, height);
		this.maxWidth = Math.max(DEFAULT_MAXIMAL_SIZE, width);
		this.maxHeight = Math.max(DEFAULT_MAXIMAL_SIZE, width);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public URL getMetamodelSpecification() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int compareTo(ViewComponent o) {
		if (o==null) return Integer.MAX_VALUE;
		return getUUID().compareTo(o.getUUID());
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setViewComponentContainer(ViewComponentContainer<?,?> container) {
		ViewComponentContainer<?,?> old = getViewComponentContainer();
		if (old!=container) {
			this.container = container==null ? null : new WeakReference<ViewComponentContainer<?,?>>(container);
			if (container!=null) {
				setViewUUID(container.getUUID());
			}
		}
	}

	/** Replies the container of this component.
	 * 
	 * @return the container; may be <code>null</code> if the
	 * container was never set before.
	 */
	ViewComponentContainer<?,?> getViewComponentContainer() {
		return this.container==null ? null : this.container.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final UUID getViewUUID() {
		return this.viewUUID;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void setViewUUID(UUID id) {
		assert(id!=null);
		this.viewUUID = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UUID getUUID() {
		return this.uuid;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setUUID(UUID uuid) {
		if ((uuid==null && this.uuid!=null)
				||(uuid!=null && !uuid.equals(this.uuid))) {
			UUID old = this.uuid;
			this.uuid = uuid;
			firePropertyChange(PROPERTY_UUID, old, this.uuid); 
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addViewComponentPropertyChangeListener(ViewComponentPropertyChangeListener listener) {
		addListener(ViewComponentPropertyChangeListener.class, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeViewComponentPropertyChangeListener(ViewComponentPropertyChangeListener listener) {
		removeListener(ViewComponentPropertyChangeListener.class, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addViewComponentChangeListener(ViewComponentChangeListener listener) {
		addListener(ViewComponentChangeListener.class, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeViewComponentChangeListener(ViewComponentChangeListener listener) {
		removeListener(ViewComponentChangeListener.class, listener);
	}

	/** Add a listener.
	 * 
	 * @param type
	 * @param listener
	 */
	protected synchronized final <T extends EventListener> void addListener(Class<T> type, T listener) {
		if (this.listeners==null) {
			this.listeners = new ListenerCollection<EventListener>();
		}
		this.listeners.add(type, listener);
	}

	/** Remove a listener.
	 * 
	 * @param type
	 * @param listener
	 */
	protected synchronized final <T extends EventListener> void removeListener(Class<T> type, T listener) {
		if (this.listeners!=null) {
			this.listeners.remove(type, listener);
			if (this.listeners.isEmpty())
				this.listeners = null;
		}
	}

	/** Replies the listeners of the given type.
	 * 
	 * @param type
	 * @return the listeners, never <code>null</code>.
	 */
	@SuppressWarnings("unchecked")
	protected synchronized final <T extends EventListener> T[] getListeners(Class<T> type) {
		if (this.listeners==null)
			return (T[])Array.newInstance(type, 0);
		return this.listeners.getListeners(type);
	}

	/** Notifies listeners about changes.
	 * 
	 * @param propertyName is the name of the changed property.
	 * @param oldValue is the old value for the property.
	 * @param newValue is the new value for the property.
	 */
	public void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		ViewComponentPropertyChangeEvent event = new ViewComponentPropertyChangeEvent(
				this, propertyName, oldValue, newValue);
		for(ViewComponentPropertyChangeListener listener : getListeners(ViewComponentPropertyChangeListener.class)) {
			listener.propertyChange(event);
		}
	}
	
	/** Invoked to update the geometry of any associated object when the
	 * shape of this component has changed.
	 * This function may change the coerced figures.
	 */
	protected abstract void updateAssociatedGeometry();

	/** {@inheritDoc}
	 */
	@Override
	public CoercedFigure addAssociatedFigureIntoView(String figureId, CoercedFigure figure) {
		if (this.associatedFigures==null) {
			this.associatedFigures = new WeakValueHashMap<String,CoercedFigure>();
		}
		CoercedFigure previous = this.associatedFigures.put(figureId, figure);
		updateAssociatedGeometry();
		for(ViewComponentBindingListener listener : getListeners(ViewComponentBindingListener.class)) {
			if (previous!=null) {
				listener.componentRemoval(previous);
			}
			listener.componentAddition(figure);
		}
		return previous;
	}

	/** {@inheritDoc}
	 */
	@Override
	public CoercedFigure getAssociatedFigureInView(String figureId) {
		if (this.associatedFigures!=null) {
			return this.associatedFigures.get(figureId);
		}
		return null;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Map<String,CoercedFigure> getAssociatedFiguresInView() {
		if (this.associatedFigures==null)
			return Collections.emptyMap();
		return Collections.unmodifiableMap(this.associatedFigures);
	}

	/** {@inheritDoc}
	 */
	@Override
	public CoercedFigure removeAssociatedFigureFromView(String figureId) {
		if (this.associatedFigures!=null) {
			CoercedFigure figureToRemove = this.associatedFigures.remove(figureId);
			if (figureToRemove!=null) {
				if (this.associatedFigures.isEmpty())
					this.associatedFigures = null;
				updateAssociatedGeometry();
				for(ViewComponentBindingListener listener : getListeners(ViewComponentBindingListener.class)) {
					listener.componentRemoval(figureToRemove);
				}
				return figureToRemove;
			}
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setWidth(float width) {
		float rw = Math.min(Math.max(width, getMinimalWidth()), getMaximalWidth());
		if (rw!=this.width) {
			float old = this.width;
			this.width = rw;
			this.bufferBounds = null;
			this.bufferDimension = null;

			onSizeUpdated(old, this.width, this.height, this.height);

			firePropertyChange(PROPERTY_WIDTH, old, this.width); 

			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setHeight(float height) {
		float rh = Math.min(Math.max(height, getMinimalHeight()), getMaximalHeight());
		if (rh!=this.height) {
			float old = this.height;
			this.height = rh;
			this.bufferBounds = null;
			this.bufferDimension = null;

			onSizeUpdated(this.width, this.width, old, this.height);

			firePropertyChange(PROPERTY_HEIGHT, old, this.height); 

			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getMinimalHeight() {
		return this.minHeight;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getMinimalWidth() {
		return this.minWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension getMinimalDimension() {
		return VectorToolkit.dimension(this.minWidth, this.minHeight);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getMaximalHeight() {
		return this.maxHeight;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getMaximalWidth() {
		return this.maxWidth;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension getMaximalDimension() {
		return VectorToolkit.dimension(this.maxWidth, this.maxHeight);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setMaximalWidth(float width) {
		setMaximalDimension(width, this.maxHeight);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setMaximalHeight(float height) {
		setMaximalDimension(this.maxWidth, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setMinimalWidth(float width) {
		setMinimalDimension(width, this.minHeight);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setMinimalHeight(float height) {
		setMinimalDimension(this.minWidth, height);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setMinimalDimension(Dimension dimension) {
		setMinimalDimension(dimension.width(), dimension.height());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setMaximalDimension(Dimension dimension) {
		setMaximalDimension(dimension.width(), dimension.height());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMinimalDimension(float width, float height) {
		boolean boundsChanged = false;
		if (width>=0f && this.minWidth!=width) {
			float old1 = this.minWidth;
			float old2 = this.width;
			float old3 = this.maxWidth;

			this.minWidth = width;
			if (this.width<this.minWidth)
				this.width = this.minWidth;
			if (this.maxWidth<this.minWidth)
				this.maxWidth = this.minWidth;

			this.bufferBounds = null;
			this.bufferDimension = null;

			if (this.maxWidth!=old3) {
				firePropertyChange(PROPERTY_MAXWIDTH, old3, this.maxWidth); 
			}
			if (this.width!=old2) {
				onSizeUpdated(old2, this.width, this.height, this.height);
				boundsChanged = true;
				firePropertyChange(PROPERTY_WIDTH, old2, this.width); 
			}
			firePropertyChange(PROPERTY_MINWIDTH, old1, this.minWidth); 
		}
		if (height>=0f && this.minHeight!=height) {
			float old1 = this.minHeight;
			float old2 = this.height;
			float old3 = this.maxHeight;

			this.minHeight = height;
			if (this.height<this.minHeight)
				this.height = this.minHeight;
			if (this.maxHeight<this.minHeight)
				this.maxHeight = this.minHeight;

			this.bufferBounds = null;
			this.bufferDimension = null;

			if (this.maxHeight!=old3) {
				firePropertyChange(PROPERTY_MAXHEIGHT, old3, this.maxHeight); 
			}
			if (this.height!=old2) {
				onSizeUpdated(this.width, this.width, old2, this.height);
				boundsChanged = true;
				firePropertyChange(PROPERTY_HEIGHT, old2, this.height); 
			}
			firePropertyChange(PROPERTY_MINHEIGHT, old1, this.minHeight); 
		}
		repaint(boundsChanged);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaximalDimension(float width, float height) {
		boolean boundsChanged = false;
		if (width>=0f && this.maxWidth!=width) {
			float old1 = this.maxWidth;
			float old2 = this.width;
			float old3 = this.minWidth;

			this.maxWidth = width;
			if (this.width>this.maxWidth)
				this.width = this.maxWidth;
			if (this.minWidth>this.maxWidth)
				this.minWidth = this.maxWidth;

			this.bufferBounds = null;
			this.bufferDimension = null;

			if (this.minWidth!=old3) {
				firePropertyChange(PROPERTY_MINWIDTH, old3, this.minWidth); 
			}
			if (this.width!=old2) {
				onSizeUpdated(old2, this.width, this.height, this.height);
				boundsChanged = true;
				firePropertyChange(PROPERTY_WIDTH, old2, this.width); 
			}
			firePropertyChange(PROPERTY_MAXWIDTH, old1, this.maxWidth); 
		}
		if (height>=0f && this.maxHeight!=height) {
			float old1 = this.maxHeight;
			float old2 = this.height;
			float old3 = this.minHeight;

			this.maxHeight = height;
			if (this.height>this.maxHeight)
				this.height = this.maxHeight;
			if (this.minHeight>this.maxHeight)
				this.minHeight = this.maxHeight;

			this.bufferBounds = null;
			this.bufferDimension = null;

			if (this.minHeight!=old3) {
				firePropertyChange(PROPERTY_MINHEIGHT, old3, this.minHeight); 
			}
			if (this.height!=old2) {
				onSizeUpdated(this.width, this.width, old2, this.height);
				boundsChanged = true;
				firePropertyChange(PROPERTY_HEIGHT, old2, this.height); 
			}
			firePropertyChange(PROPERTY_MAXHEIGHT, old1, this.maxHeight); 
		}
		repaint(boundsChanged);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean contains(float x, float y) {
		return x>=this.x && x<=(this.x+this.width)
				&&y>=this.y && y<=(this.y+this.height);
	}
	
	@Override
	public boolean contains(Rectangle2f r) {
		return getBounds().contains(r);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean intersects(Shape2f r) {
		return r.intersects(getBounds());
	}

	/** This function compute the bounds.
	 * <p>
	 * <strong>This function is not expected to be directly
	 * called by you! So, never call it.</strong>
	 * This function is called by {@link #getBounds()}
	 * when the buffered damaged bounds is invalid.
	 * <p>
	 * This function does not change the bounds stored
	 * in this AbstractViewComponent (that is replied
	 * by {@link #getBounds()}.
	 * Its purpose is to provide an overridable implementation
	 * of the algorithm to compute the bounds.
	 * This algorithm may be specialized by the subclasses.
	 * 
	 * @return the bounds.
	 */
	protected Rectangle2f computeBounds() {
		return new Rectangle2f(this.x, this.y, this.width, this.height);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see #computeBounds()
	 */
	@Override
	public final Rectangle2f getBounds() {
		Rectangle2f bounds = this.bufferBounds==null ? null : this.bufferBounds.get();
		if (bounds==null) {
			bounds = computeBounds();
			this.bufferBounds = new SoftReference<Rectangle2f>(bounds);
		}
		return bounds;
	}

	/** Invoked each time the size (and not the position)
	 * of the component have changed.
	 * This function is declared to be overridden by subclasses
	 * to do something each time the size changed.
	 * By default this function does nothing.
	 * 
	 * @param oldWidth is the old value for the width.
	 * @param newWidth is the new value for the width.
	 * @param oldHeight is the old value for the height.
	 * @param newHeight is the new value for the height.
	 * @see #onSizeUpdated(float,float,float,float)
	 * @see #onPositionUpdated(float,float,float,float)
	 * @see #onBoundsUpdated(float,float,float,float,float,float,float,float)
	 */
	protected void onSizeUpdated(float oldWidth, float newWidth, float oldHeight, float newHeight) {
		//
	}

	/** Invoked each time the position (and not the size)
	 * of the component have changed.
	 * This function is declared to be overridden by subclasses
	 * to do something each time the position changed.
	 * By default this function does nothing.
	 * 
	 * @param oldX is the old value of the X position.
	 * @param newX is the new value of the X position.
	 * @param oldY is the old value of the Y position.
	 * @param newY is the new value of the Y position.
	 * @see #onSizeUpdated(float,float,float,float)
	 * @see #onPositionUpdated(float,float,float,float)
	 * @see #onBoundsUpdated(float,float,float,float,float,float,float,float)
	 */
	protected void onPositionUpdated(float oldX, float newX, float oldY, float newY) {
		//
	}

	/** Invoked each time the position and the size
	 * of the component have changed.
	 * This function is declared to be overridden by subclasses
	 * to do something each time the position changed.
	 * By default this function does nothing.
	 * 
	 * @param oldX is the old value of the X position.
	 * @param newX is the new value of the X position.
	 * @param oldY is the old value of the Y position.
	 * @param newY is the new value of the Y position.
	 * @param oldWidth is the old value for the width.
	 * @param newWidth is the new value for the width.
	 * @param oldHeight is the old value for the height.
	 * @param newHeight is the new value for the height.
	 * @see #onSizeUpdated(float,float,float,float)
	 * @see #onPositionUpdated(float,float,float,float)
	 * @see #onBoundsUpdated(float,float,float,float,float,float,float,float)
	 */
	protected void onBoundsUpdated(float oldX, float newX, float oldY, float newY, float oldWidth, float newWidth, float oldHeight, float newHeight) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setBounds(Rectangle2f bounds) {
		setBounds(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBounds(float x, float y, float width, float height) {
		float rw = Math.min(Math.max(width, getMinimalWidth()), getMaximalWidth());
		float rh = Math.min(Math.max(height, getMinimalHeight()), getMaximalHeight());
		if (this.x!=x || this.y!=y ||
				(this.width!=rw) ||
				(this.height!=rh)) {
			float old1 = this.x;
			float old2 = this.y;
			float old3 = this.width;
			float old4 = this.height;
			this.x = x;
			this.y = y;
			this.width = rw;
			this.height = rh;
			
			this.bufferBounds = null;
			this.bufferPosition = null;
			this.bufferDimension = null;

			if ((old1!=this.x || old2!=this.y)
					&&(old3!=this.width || old4!=this.height))
				onBoundsUpdated(old1, this.x, old2, this.y, old3, this.width, old4, this.height);
			else if (old1!=this.x || old2!=this.y)
				onPositionUpdated(old1, this.x, old2, this.y);
			else if (old3!=this.width || old4!=this.height)
				onSizeUpdated(old3, this.width, old4, this.height);

			if (old1!=this.x)
				firePropertyChange(PROPERTY_X, old1, this.x); 
			if (old2!=this.y)
				firePropertyChange(PROPERTY_Y, old2, this.y); 
			if (old3!=this.width)
				firePropertyChange(PROPERTY_WIDTH, old3, this.width); 
			if (old4!=this.height)
				firePropertyChange(PROPERTY_HEIGHT, old4, this.height); 

			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSize(float width, float height) {
		float rw = Math.min(Math.max(width, getMinimalWidth()), getMaximalWidth());
		float rh = Math.min(Math.max(height, getMinimalHeight()), getMaximalHeight());
		if (this.width!=rw || this.height!=rh) {
			float old3 = this.width;
			float old4 = this.height;
			this.width = rw;
			this.height = rh;
			
			this.bufferBounds = null;
			this.bufferDimension = null;

			onSizeUpdated(old3, this.width, old4, this.height);

			if (old3!=this.width)
				firePropertyChange(PROPERTY_WIDTH, old3, this.width); 
			if (old4!=this.height)
				firePropertyChange(PROPERTY_HEIGHT, old4, this.height); 

			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getWidth() {
		return this.width;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getHeight() {
		return this.height;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Dimension getSize() {
		Dimension dim = this.bufferDimension==null ? null : this.bufferDimension.get();
		if (dim==null) {
			dim = VectorToolkit.dimension(this.width, this.height);
			this.bufferDimension = new SoftReference<Dimension>(dim);
		}
		return dim;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void cleanUp() {
		this.bufferBounds = null;
		this.bufferPosition = null;
		this.bufferDimension = null;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Shape2f getClip(Rectangle2f figureBounds) {
		return null;
	}


	/** {@inheritDoc}
	 */
	@Override
	public void addViewComponentRepaintListener(ViewComponentLayoutListener listener) {
		addListener(ViewComponentLayoutListener.class, listener);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void removeViewComponentRepaintListener(ViewComponentLayoutListener listener) {
		removeListener(ViewComponentLayoutListener.class, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void repaint(boolean boundChanged) {
		for(ViewComponentLayoutListener listener : getListeners(ViewComponentLayoutListener.class)) {
			listener.componentRepaint(this, boundChanged);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public Color getLineColor() {
		Preferences prefs = Preferences.userNodeForPackage(AbstractViewComponent.class);
		int c = prefs.getInt("LINE_COLOR", -1); //$NON-NLS-1$
		if (c>=0) return VectorToolkit.color(c, true);
		return DEFAULT_LINE_COLOR;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Color getFillColor() {
		Preferences prefs = Preferences.userNodeForPackage(AbstractViewComponent.class);
		int c = prefs.getInt("FILL_COLOR", -1); //$NON-NLS-1$
		if (c>=0) return VectorToolkit.color(c, true);
		return DEFAULT_FILL_COLOR;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Color getShadowColor() {
		Preferences prefs = Preferences.userNodeForPackage(AbstractViewComponent.class);
		int c = prefs.getInt("SHADOW_COLOR", -1); //$NON-NLS-1$
		if (c>=0) return VectorToolkit.color(c, true);
		Color color = getBackgroundColor();
		return color.darkerColor();
	}

	/** {@inheritDoc}
	 */
	@Override
	public Color getBackgroundColor() {
		ViewComponentContainer<?,?> container = getViewComponentContainer();
		if (container!=null) {
			Color c = container.getBackgroundColor();
			if (c!=null) return c;
		}
		return Colors.WHITE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getForegroundSelectionColor() {
		ViewComponentContainer<?,?> container = getViewComponentContainer();
		if (container!=null) {
			Color c = container.getSelectionForegroundColor();
			if (c!=null) return c;
		}
		Preferences prefs = Preferences.userNodeForPackage(AbstractViewComponent.class);
		int c = prefs.getInt("FOREGROUND_SELECTION_COLOR", -1); //$NON-NLS-1$
		if (c>=0) return VectorToolkit.color(c, true);
		return DEFAULT_FOREGROUND_SELECTION_COLOR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getBackgroundSelectionColor() {
		ViewComponentContainer<?,?> container = getViewComponentContainer();
		if (container!=null) {
			Color c = container.getSelectionBackgroundColor();
			if (c!=null) return c;
		}
		Preferences prefs = Preferences.userNodeForPackage(AbstractViewComponent.class);
		int c = prefs.getInt("BACKGROUND_SELECTION_COLOR", -1); //$NON-NLS-1$
		if (c>=0) return VectorToolkit.color(c, true);
		return DEFAULT_BACKGROUND_SELECTION_COLOR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getX() {
		return this.x;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public float getY() {
		return this.y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point2D getLocation() {
		Point2D pos = this.bufferPosition==null ? null : this.bufferPosition.get();
		if (pos==null) {
			pos = new Point2f(this.x, this.y);
			this.bufferPosition = new SoftReference<Point2D>(pos);
		}
		return pos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(float x, float y) {
		if (x!=this.x || y!=this.y) {
			float old1 = this.x;
			float old2 = this.y;
			this.x = x;
			this.y = y;
			this.bufferBounds = null;
			this.bufferPosition = null;

			onPositionUpdated(old1, this.x, old2, this.y);

			if (this.x!=old1)
				firePropertyChange(PROPERTY_X, old1, this.x); 
			if (this.y!=old2)
				firePropertyChange(PROPERTY_Y, old2, this.y); 

			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void translate(float dx, float dy) {
		if (dx!=0f || dy!=0f) {
			float old1 = this.x;
			float old2 = this.y;
			this.x += dx;
			this.y += dy;
			this.bufferBounds = null;
			this.bufferPosition = null;

			onPositionUpdated(old1, this.x, old2, this.y);

			if (this.x!=old1)
				firePropertyChange(PROPERTY_X, old1, this.x); 
			if (this.y!=old2)
				firePropertyChange(PROPERTY_Y, old2, this.y); 

			repaint(true);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = new TreeMap<String, Object>();
		properties.put(PROPERTY_X, this.x); 
		properties.put(PROPERTY_Y, this.y); 
		properties.put(PROPERTY_WIDTH, this.width); 
		properties.put(PROPERTY_HEIGHT, this.height); 
		properties.put(PROPERTY_MINWIDTH, this.minWidth); 
		properties.put(PROPERTY_MINHEIGHT, this.minHeight); 
		properties.put(PROPERTY_MAXWIDTH, this.maxWidth); 
		properties.put(PROPERTY_MAXHEIGHT, this.maxHeight); 
		properties.put(PROPERTY_UUID, this.uuid); 
		properties.put(PROPERTY_VIEWUUID, this.viewUUID); 
		properties.put(PROPERTY_NAME, this.name); 
		properties.put(PROPERTY_ICON, this.icon); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) {
		if (properties!=null) {
			float x = propGetFloat(PROPERTY_X, this.x, properties); 
			float y = propGetFloat(PROPERTY_Y, this.y, properties); 
			float w = propGetFloat(PROPERTY_WIDTH, this.width, properties); 
			float h = propGetFloat(PROPERTY_HEIGHT, this.height, properties); 
			setBounds(x, y, w, h);
			setMinimalWidth(propGetFloat(PROPERTY_MINWIDTH, this.minWidth, properties)); 
			setMaximalWidth(propGetFloat(PROPERTY_MAXWIDTH, this.maxWidth, properties)); 
			setMinimalHeight(propGetFloat(PROPERTY_MINHEIGHT, this.minHeight, properties)); 
			setMaximalHeight(propGetFloat(PROPERTY_MAXHEIGHT, this.maxHeight, properties)); 
			setName(propGetString(PROPERTY_NAME, this.name, false, properties)); 
			setIcon(propGetImage(PROPERTY_ICON, this.icon, false, properties)); 
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		String n = getName();
		if (n!=null && !n.isEmpty()) return n;
		return getClass().getSimpleName()+"@"+ //$NON-NLS-1$
		Integer.toHexString(System.identityHashCode(this));
	}

	/** {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		if ((this.name==null && name!=null)
				||(this.name!=null && !this.name.equals(name))) {
			String old = this.name;
			this.name = name;
			firePropertyChange(PROPERTY_NAME, old, this.name);
			repaint(true);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public Image getIcon() {
		return this.icon;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setIcon(Image icon) {
		if (this.icon!=icon) {
			Image old = this.icon;
			this.icon = icon;
			firePropertyChange(PROPERTY_ICON, old, this.icon);
			repaint(true);
		}
	}

}