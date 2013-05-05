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
package org.arakhne.neteditor.fig.figure;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import java.util.prefs.Preferences;

import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.shadow.ShadowPainter;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.AbstractViewComponent;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.fig.view.ViewComponentLayoutListener;

/** This class is the base class for basic drawing objects such as
 *  rectangles, lines, text, circles, etc. 
 *
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractFigure extends AbstractViewComponent implements Figure {

	private static final long serialVersionUID = 8901037498478676771L;

	private final Set<ResizeDirection> resizeDirections = new TreeSet<ResizeDirection>();

	/** Shadow painter.
	 */
	private transient ShadowPainter shadowPainter = null;

	private Color lockingOutlineColor = null;
	private Color lockingFillColor = null;
	private Color lineColor = null;
	private Color fillingColor = null;

	private boolean isSelectable = true;
	private boolean isLocked = false;
	private boolean isAutoLockAssociatedFigures = true;

	/** Construct a new Fig.
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
	public AbstractFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height);
		this.resizeDirections.addAll(Arrays.asList(ResizeDirection.values()));
	}

	/** Move this figure to avoid collision with the other figures.
	 */
	protected void avoidCollision() {
		for (ViewComponentLayoutListener listener : getListeners(ViewComponentLayoutListener.class)) {
			listener.collisionAvoidance(this);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ResizeDirection> getResizeDirections() {
		return Collections.unmodifiableSet(this.resizeDirections);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResizeDirections(ResizeDirection... resizeDirections) {
		this.resizeDirections.clear();
		this.resizeDirections.addAll(Arrays.asList(resizeDirections));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setResizeAllDirections() {
		this.resizeDirections.addAll(Arrays.asList(ResizeDirection.values()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isResizable() {
		return !this.resizeDirections.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMovable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLockable() {
		return true;
	}

	/** {@inheritDoc}
	 */
	@Override
	public Color getLineColor() {
		if (this.lineColor!=null)
			return this.lineColor;
		return super.getLineColor();
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setLineColor(Color color) {
		if ((color==null && this.lineColor!=null)
				||(color!=null && !color.equals(this.lineColor))) {
			Color old = this.lineColor;
			this.lineColor = color;
			firePropertyChange(PROPERTY_LINECOLOR, old, this.lineColor); 
			repaint(false);
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public Color getFillColor() {
		if (this.fillingColor!=null)
			return this.fillingColor;
		return super.getFillColor();
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setFillColor(Color color) {
		if ((color==null && this.fillingColor!=null)
				||(color!=null && !color.equals(this.fillingColor))) {
			Color old = this.fillingColor;
			this.fillingColor = color;
			firePropertyChange(PROPERTY_FILLINGCOLOR, old, this.fillingColor); 
			repaint(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getLockOutlineColor() {
		if (this.lockingOutlineColor!=null)
			return this.lockingOutlineColor;
		Preferences prefs = Preferences.userNodeForPackage(AbstractFigure.class);
		int c = prefs.getInt("LOCKING_OUTLINE_COLOR", -1); //$NON-NLS-1$
		if (c>=0) return VectorToolkit.color(c, true);
		return DEFAULT_LOCKING_OUTLINE_COLOR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLockOutlineColor(Color color) {
		if ((color==null && this.lockingOutlineColor!=null)
				||(color!=null && !color.equals(this.lockingOutlineColor))) {
			Color old = this.lockingOutlineColor;
			this.lockingOutlineColor = color;
			firePropertyChange(PROPERTY_LOCKINGOUTLINECOLOR, old, this.lockingOutlineColor); 
			repaint(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getLockFillColor() {
		if (this.lockingFillColor!=null)
			return this.lockingFillColor;
		Preferences prefs = Preferences.userNodeForPackage(AbstractFigure.class);
		int c = prefs.getInt("LOCKING_FILL_COLOR", -1); //$NON-NLS-1$
		if (c>=0) return VectorToolkit.color(c, true);
		return DEFAULT_LOCKING_FILL_COLOR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLockFillColor(Color color) {
		if ((color==null && this.lockingFillColor!=null)
				||(color!=null && !color.equals(this.lockingFillColor))) {
			Color old = this.lockingFillColor;
			this.lockingFillColor = color;
			firePropertyChange(PROPERTY_LOCKINGFILLINGCOLOR, old, this.lockingFillColor); 
			repaint(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setSelectable(boolean select) {
		if (select!=this.isSelectable) {
			boolean old1 = this.isSelectable;
			this.isSelectable = select;
			firePropertyChange(PROPERTY_ISSELECTABLE, old1, this.isSelectable); 
			repaint(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSelectable() {
		return this.isSelectable;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocked(boolean lock) {
		if (this.isLocked!=lock) {
			boolean old = this.isLocked;
			this.isLocked = lock;
			firePropertyChange(PROPERTY_ISLOCKED, old, this.isLocked);
			repaint(false);
			if (isAssociatedFiguresAutoLocked()) {
				for(CoercedFigure coercedFigure : getAssociatedFiguresInView().values()) {
					coercedFigure.setLocked(lock);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isLocked() {
		return this.isLocked;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isAssociatedFiguresAutoLocked() {
		return this.isAutoLockAssociatedFigures;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void setAssociatedFiguresAutoLocked(boolean autoLock) {
		if (autoLock!=this.isAutoLockAssociatedFigures) {
			boolean old = this.isAutoLockAssociatedFigures;
			this.isAutoLockAssociatedFigures = autoLock;
			boolean isLocked = isLocked();
			for(CoercedFigure coercedFigure : getAssociatedFiguresInView().values()) {
				coercedFigure.setLocked(isLocked);
			}
			firePropertyChange(PROPERTY_ISAUTOLOCKASSOCIATEDFIGURES, old, this.isAutoLockAssociatedFigures);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized ShadowPainter getShadowPainter() {
		if (this.shadowPainter==null) {
			this.shadowPainter = createShadowPainter();
		}
		return this.shadowPainter;
	}

	/** Invoked by {@link #getShadowPainter()} when 
	 * an instance of shadow painter must be create.
	 * 
	 * @return the instance of shadow painter.
	 */
	protected abstract ShadowPainter createShadowPainter();

	/** Release any shadow painter associated to this component.
	 */
	protected synchronized final void releaseShadowPainter() {
		if (this.shadowPainter!=null) {
			ShadowPainter p = this.shadowPainter;
			this.shadowPainter = null;
			p.release();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		if (this.fillingColor!=null)
			properties.put(PROPERTY_FILLINGCOLOR, this.fillingColor.getRGB());
		else
			properties.put(PROPERTY_FILLINGCOLOR, null);
		if (this.lineColor!=null)
			properties.put(PROPERTY_LINECOLOR, this.lineColor.getRGB());
		else
			properties.put(PROPERTY_LINECOLOR, null);
		if (this.lockingOutlineColor!=null)
			properties.put(PROPERTY_LOCKINGOUTLINECOLOR, this.lockingOutlineColor.getRGB());
		else
			properties.put(PROPERTY_LOCKINGOUTLINECOLOR, null);
		if (this.lockingFillColor!=null)
			properties.put(PROPERTY_LOCKINGFILLINGCOLOR, this.lockingFillColor.getRGB());
		else
			properties.put(PROPERTY_LOCKINGFILLINGCOLOR, null);
		properties.put(PROPERTY_ISLOCKED, this.isLocked); 
		properties.put(PROPERTY_ISAUTOLOCKASSOCIATEDFIGURES, this.isAutoLockAssociatedFigures); 
		properties.put(PROPERTY_ISSELECTABLE, this.isSelectable); 
		properties.put(PROPERTY_RESIZEDIRECTIONS, this.resizeDirections); 
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String,Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setFillColor(propGetColor(PROPERTY_FILLINGCOLOR, this.fillingColor, false, properties)); 
			setLineColor(propGetColor(PROPERTY_LINECOLOR, this.lineColor, false, properties)); 
			setLockOutlineColor(propGetColor(PROPERTY_LOCKINGOUTLINECOLOR, this.lockingOutlineColor, false, properties)); 
			setLockFillColor(propGetColor(PROPERTY_LOCKINGFILLINGCOLOR, this.lockingFillColor, false, properties)); 
			setLocked(propGetBoolean(PROPERTY_ISLOCKED, this.isLocked, properties)); 
			setAssociatedFiguresAutoLocked(propGetBoolean(PROPERTY_ISAUTOLOCKASSOCIATEDFIGURES, this.isAutoLockAssociatedFigures, properties)); 
			setSelectable(propGetBoolean(PROPERTY_ISSELECTABLE, this.isSelectable, properties)); 
			Set<?> directions = propGet(Set.class, PROPERTY_RESIZEDIRECTIONS, this.resizeDirections, false, properties); 
			if (directions!=null) {
				this.resizeDirections.clear();
				for(Object o : directions) {
					if (o instanceof ResizeDirection) {
						this.resizeDirections.add((ResizeDirection)o);
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String,Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_ISLOCKED, Boolean.class);
		return properties;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMaximalDimension(float width, float height) {
		super.setMaximalDimension(width, height);
		for(SubFigure subFigure : getSubFigures()) {
			subFigure.setMaximalDimension(width, height);
		}
	}

}
