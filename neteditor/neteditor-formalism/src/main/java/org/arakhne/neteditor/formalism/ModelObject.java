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
package org.arakhne.neteditor.formalism;

import java.io.Serializable;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

/** This is the semantic object that composes a graph model.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ModelObject extends Comparable<ModelObject>, Serializable {

	/** Replies the identifier of this object.
	 * 
	 * @return the identifier of this object.
	 */
	public UUID getUUID();
	
	/** Replies the URL that permits to download the specified
	 * of the metamodel for this element.
	 * 
	 * @return the url of the schema for this model object. If <code>null</code>
	 * the default specification from NetEditor is assumed.
	 */
	public URL getMetamodelSpecification();

	/** Find, in a deep way, the model object that has the specified id.
	 * This method goes through all the model objects and sub-model objects.
	 * @param id
	 * @return the object with the specified id, or <code>null</code>.
	 */
	public ModelObject findModelObject(UUID id);

	/** Set the identifier of this object.
	 * The name of the property is "uuid".
	 * 
	 * @param uuid is the identifier of this object.
	 */
	public void setUUID(UUID uuid);

	/** Replies the name of this object.
	 * 
	 * @return the name of this object.
	 */
	public String getName();

	/** Set the name of this object.
	 * The name of the property is "name".
	 * 
	 * @param name is the name of this object.
	 */
	public void setName(String name);

	/** Add Listener on changes in this object.
	 * 
	 * @param listener
	 */
	public void addModelObjectListener(ModelObjectListener listener);

	/** Add Listener on changes in this object.
	 * 
	 * @param listener
	 */
	public void removeModelObjectListener(ModelObjectListener listener);

	/** Replies the manager of views for this model element.
	 * 
	 * @return the manager of views.
	 */
	public ViewBinding getViewBinding();

	/**
	 * Notifies the listener about the change of a property of this model object.
	 * 
	 * @param propertyName is the name of the property.
	 * @param oldValue is the old value of the property.
	 * @param newValue is the new value of the property.
	 * @return the fired event.
	 */
	public ModelObjectEvent firePropertyChanged(String propertyName, Object oldValue, Object newValue);

	/**
	 * Notifies the listener about the change of the container of this model object.
	 * 
	 * @param oldContainer is the old container.
	 * @param newContainer is the new container.
	 * @return the fired event.
	 */
	public ModelObjectEvent fireContainerChanged(ModelObject oldContainer, ModelObject newContainer);

	/**
	 * Notifies the listener about the addition of a component in this model object.
	 * 
	 * @param component is the added component.
	 * @return the fired event.
	 */
	public ModelObjectEvent fireComponentAdded(ModelObject component);

	/**
	 * Notifies the listener about the remove of a component in this model object.
	 * 
	 * @param component is the removed component.
	 * @return the fired event.
	 */
	public ModelObjectEvent fireComponentRemoved(ModelObject component);

	/**
	 * Notifies the listener about the change of the content of this model object.
	 * 
	 * @param cause is the cause of the event. If <code>null</code> the cause
	 * of the event is not another event.
	 * @return the fired event.
	 */
	public ModelObjectEvent fireContentChanged(ModelObjectEvent cause);

	/**
	 * Notifies the listener about the change of a link of this model object.
	 * 
	 * @param oldLinkedObject is the old linked object.
	 * @param newLinkedObject is the new linked object.
	 * @return the fired event.
	 */
	public ModelObjectEvent fireLinkChanged(ModelObject oldLinkedObject, ModelObject newLinkedObject);
	
	/** Replies all the properties of the model object, except the UUID.
	 * 
	 * @return the properties of this model object, except the UUID.
	 */
	public Map<String,Object> getProperties();

	/** Replies the definitions of the properties that may
	 * be edited through an UI.
	 * 
	 * @return the properties.
	 */
	public Map<String,Class<?>> getUIEditableProperties();

	/** Set the properties of the model object, except the UUID.
	 * 
	 * @param properties are the properties of this model object, except the UUID.
	 */
	public void setProperties(Map<String,Object> properties);

}
