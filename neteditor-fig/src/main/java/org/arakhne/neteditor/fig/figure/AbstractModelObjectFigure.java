/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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
package org.arakhne.neteditor.fig.figure;

import java.util.UUID;

import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ViewComponentBindingListener;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.fig.view.ViewComponentModelChangeEvent;
import org.arakhne.neteditor.fig.view.ViewComponentModelChangeListener;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.ModelObjectListener;

/** This class is the base class for graphical
 *  representation of model elements.
 *
 * @param <M> is the type of the model object supported by this figure.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractModelObjectFigure<M extends ModelObject> extends AbstractFigure implements ModelObjectFigure<M> {

	private static final long serialVersionUID = -4122014278369356364L;

	/** Owners are underlying objects that "own" the graphical Fig's 
	 *  that represent them.
	 */
	private M modelObject = null;

	private final ModelObjectListener listener = new ModelObjectListener() {
		@Override
		public void modelPropertyChanged(ModelObjectEvent event) {
			updateFromModel(event);
			fireModelChange(event);
			if (PROPERTY_NAME.equals(event.getPropertyName())) { 
				setName(event.getNewPropertyValue().toString());
			}
			repaint(false);
		}
		@Override
		public void modelContainerChanged(ModelObjectEvent event) {
			updateFromModel(event);
			fireModelChange(event);
			repaint(false);
		}
		@Override
		public void modelLinkChanged(ModelObjectEvent event) {
			updateFromModel(event);
			fireModelChange(event);
			repaint(false);
		}
		@Override
		public void modelContentChanged(ModelObjectEvent event) {
			updateFromModel(event);
			fireModelChange(event);
			repaint(false);
		}
		@Override
		public void modelComponentAdded(ModelObjectEvent event) {
			updateFromModel(event);
			fireModelChange(event);
			repaint(false);
		}
		@Override
		public void modelComponentRemoved(ModelObjectEvent event) {
			updateFromModel(event);
			fireModelChange(event);
			repaint(false);
		}
	};

	/** Construct a new ModelObjectFigure.
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
	public AbstractModelObjectFigure(UUID viewUUID, float x, float y, float width, float height) { 
		super(viewUUID, x, y, width, height );
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void setViewUUID(UUID id) {
		assert(id!=null);
		UUID oldId = getViewUUID();
		if (!id.equals(oldId)) {
			super.setViewUUID(id);
			M mo = getModelObject();
			if (mo!=null) {
				mo.getViewBinding().replaceView(oldId, id);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addViewComponentModelChangeListener(ViewComponentModelChangeListener listener) {
		addListener(ViewComponentModelChangeListener.class, listener);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeViewComponentModelChangeListener(ViewComponentModelChangeListener listener) {
		removeListener(ViewComponentModelChangeListener.class, listener);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void addViewComponentBindingListener(ViewComponentBindingListener listener) {
		addListener(ViewComponentBindingListener.class, listener);
	}

	/** {@inheritDoc}
	 */
	@Override
	public void removeViewComponentBindingListener(ViewComponentBindingListener listener) {
		removeListener(ViewComponentBindingListener.class, listener);
	}


	/** Notifies listeners about changes.
	 * 
	 * @param event
	 */
	protected void fireModelChange(ModelObjectEvent event) {
		ViewComponentModelChangeEvent figEvent = new ViewComponentModelChangeEvent(
				this, event);
		for(ViewComponentModelChangeListener listener : getListeners(ViewComponentModelChangeListener.class)) {
			listener.modelChange(figEvent);
		}
	}

	/** Notifies listeners about the request to create a graphical representation.
	 * 
	 * @param modelObject
	 * @see #unbindFigure(SubFigure)
	 */
	protected void createFigureFor(ModelObject modelObject) {
		for(ViewComponentBindingListener listener : getListeners(ViewComponentBindingListener.class)) {
			listener.componentCreation(this, modelObject);
		}
	}

	/** Notifies listeners about the request to remove a graphical representation.
	 * 
	 * @param subfigure
	 * @see #createFigureFor(ModelObject)
	 */
	protected void unbindFigure(SubFigure subfigure) {
		for(ViewComponentBindingListener listener : getListeners(ViewComponentBindingListener.class)) {
			listener.componentRemoval(this, subfigure);
		}
	}

	/** Invoked when the associated has model changed.
	 * 
	 * @param event is the model change event. If <code>null</code>
	 * if means that this figure was associated to a new model object.
	 */
	protected abstract void updateFromModel(ModelObjectEvent event);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public M getModelObject() {
		return this.modelObject;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModelObject(M modelObject) {
		if (modelObject!=this.modelObject) {
			M old = this.modelObject;
			if (this.modelObject!=null) {
				this.modelObject.removeModelObjectListener(this.listener);
				this.modelObject.getViewBinding().unbind(getViewUUID());
			}
			this.modelObject = modelObject;
			if (this.modelObject!=null) {
				this.modelObject.getViewBinding().bind(getViewUUID(), this);
				this.modelObject.addModelObjectListener(this.listener);
			}
			updateFromModel(null); // Note that null means the model object is a new one.
			firePropertyChange(PROPERTY_MODELOBJECT, old, this.modelObject); 
			setName(this.modelObject==null ? null : this.modelObject.getName());
			repaint(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		super.setName(name);
		ModelObject object = getModelObject();
		if (object!=null) {
			object.setName(name);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		M mo = getModelObject();
		if (mo!=null) return mo.toString();
		return super.toString();
	}

}
