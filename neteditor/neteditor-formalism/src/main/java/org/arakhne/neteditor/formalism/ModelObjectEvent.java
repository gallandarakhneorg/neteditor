/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.formalism;

import java.util.EventObject;

/** Event in the model.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ModelObjectEvent extends EventObject {
    
	private static final long serialVersionUID = 1747373936875815410L;
	
	private final Type type;
	private final String propertyName;
	private final Object oldValue;
	private final Object newValue;
	private final ModelObjectEvent cause;
	
	/**
	 * @param source
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	public ModelObjectEvent(ModelObject source, String propertyName, Object oldValue, Object newValue) {
		super(source);
		this.type = Type.PROPERTY_CHANGE;
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.cause = null;
	}
	
	/**
	 * @param source
	 * @param cause
	 */
	public ModelObjectEvent(ModelObject source, ModelObjectEvent cause) {
		super(source);
		this.type = Type.CONTENT_CHANGE;
		this.propertyName = null;
		this.oldValue = null;
		this.newValue = null;
		this.cause = cause;
	}

	/**
	 * @param source
	 */
	public ModelObjectEvent(ModelObject source) {
		super(source);
		this.type = Type.CONTENT_CHANGE;
		this.propertyName = null;
		this.oldValue = null;
		this.newValue = null;
		this.cause = null;
	}

	/**
	 * @param source
	 * @param component
	 * @param type
	 */
	public ModelObjectEvent(ModelObject source, ModelObject component, Type type) {
		super(source);
		assert(type==Type.COMPONENT_ADDITION || type==Type.COMPONENT_REMOVE);
		this.type = type;
		this.propertyName = null;
		if (type==Type.COMPONENT_ADDITION) {
			this.newValue = component;
			this.oldValue = null;
		}
		else {
			this.newValue = null;
			this.oldValue = component;
		}
		this.cause = null;
	}

	/**
	 * @param source
	 * @param oldContainer
	 * @param newContainer
	 * @param type
	 */
	public ModelObjectEvent(ModelObject source, ModelObject oldContainer, ModelObject newContainer, Type type) {
		super(source);
		assert(type==Type.CONTAINER_CHANGE || type==Type.LINK_CHANGE);
		this.type = type;
		this.propertyName = null;
		this.oldValue = oldContainer;
		this.newValue = newContainer;
		this.cause = null;
	}

	/** Replies the type of the event.
	 * 
	 * @return the type of the event.
	 */
	public Type getType() {
		return this.type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ModelObject getSource() {
		return (ModelObject)super.getSource();
	}
	
	/** Replies the name of the property that has changed.
	 * 
	 * @return the name of the property.
	 */
	public String getPropertyName() {
		return this.propertyName;
	}
	
	/** Replies the old value of the property that has changed.
	 * 
	 * @return the old value of the property.
	 */
	public Object getOldPropertyValue() {
		return this.oldValue;
	}

	/** Replies the new value of the property that has changed.
	 * 
	 * @return the new value of the property.
	 */
	public Object getNewPropertyValue() {
		return this.newValue;
	}

	/** Replies the old container.
	 * 
	 * @return the old container.
	 */
	public ModelObject getOldContainer() {
		return (ModelObject)this.oldValue;
	}

	/** Replies the new container.
	 * 
	 * @return the new container.
	 */
	public ModelObject getNewContainer() {
		return (ModelObject)this.newValue;
	}

	/** Replies the old linked object.
	 * 
	 * @return the old linked object.
	 */
	public ModelObject getOldLinkedObject() {
		return (ModelObject)this.oldValue;
	}

	/** Replies the new linked object.
	 * 
	 * @return the new linked object.
	 */
	public ModelObject getNewLinkedObject() {
		return (ModelObject)this.newValue;
	}

	/** Replies the added object.
	 * 
	 * @return the added object.
	 */
	public ModelObject getAddedObject() {
		return (ModelObject)this.newValue;
	}

	/** Replies the removed object.
	 * 
	 * @return the removed object.
	 */
	public ModelObject getRemovedObject() {
		return (ModelObject)this.oldValue;
	}

	/** Replies the cause of this event.
	 * 
	 * @return the cause of this event.
	 */
	public ModelObjectEvent getCause() {
		ModelObjectEvent e = this;
		while (e.cause!=null) {
			e = e.cause;
		}
		return e;
	}

	/** Event in the model.
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public enum Type {
		/** A property has changed.
		 */
		PROPERTY_CHANGE,
		/** The container has changed.
		 */
		CONTAINER_CHANGE,
		/** A linked object has changed.
		 */
		LINK_CHANGE,
		/** The content of a model object has changed.
		 */
		CONTENT_CHANGE,
		/** A component was added.
		 */
		COMPONENT_ADDITION,
		/** A component was removed.
		 */
		COMPONENT_REMOVE;
	}
    
}
