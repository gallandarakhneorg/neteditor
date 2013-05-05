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

package org.arakhne.neteditor.formalism;

import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EventListener;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.neteditor.formalism.ModelObjectEvent.Type;

/** This is the semantic object that composes a graph model.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractModelObject extends AbstractPropertyTooler implements ModelObject, PropertyNames {

	private static final long serialVersionUID = -5610619602653209014L;
	
	private String name;
	private UUID uuid;
	private transient Collection<ModelObjectListener> listeners = null;
	private transient ViewBinding viewBinding = null;
	
    /** Construct a new AbstractModelObject.
     */
    public AbstractModelObject() {
    	this.uuid = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public URL getMetamodelSpecification() {
    	return null;
    }
    
	/** {@inheritDoc}
	 */
    @Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = new TreeMap<String,Object>();
		properties.put(PROPERTY_NAME, this.name); 
		return properties;
	}
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = new TreeMap<String,Class<?>>();
		properties.put(PROPERTY_NAME, String.class); 
    	return properties;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Map<String, Object> properties) {
    	if (properties!=null) {
    		setName(propGetString(PROPERTY_NAME, getName(), false, properties)); 
    	}
    }

	/**
     * {@inheritDoc}
     */
    @Override
    public int compareTo(ModelObject o) {
    	if (o==null) return Integer.MAX_VALUE;
    	return getUUID().compareTo(o.getUUID());
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
    		firePropertyChanged(PROPERTY_UUID, old, this.uuid);
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public UUID getUUID() {
    	if (this.uuid==null)
    		this.uuid = UUID.randomUUID();
    	return this.uuid;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
    	if ((name==null && this.name!=null)
    		||(name!=null && !name.equals(this.name))) {
    		String old = this.name;
    		this.name = name;
    		firePropertyChanged(PROPERTY_NAME, old, this.name); 
    	}
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
    	return this.name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void addModelObjectListener(ModelObjectListener listener) {
    	if (this.listeners==null)
    		this.listeners = new ArrayList<ModelObjectListener>();
    	this.listeners.add(listener);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void removeModelObjectListener(ModelObjectListener listener) {
    	if (this.listeners!=null) {
    		this.listeners.remove(listener);
    		if (this.listeners.isEmpty())
    			this.listeners = null;
    	}
    }
    
    /** Replies the listeners of the specified type.
     * 
     * @param type
     * @return the listeners.
     */
    @SuppressWarnings("unchecked")
	protected synchronized <T extends EventListener> T[] getListeners(Class<T> type) {
    	if (this.listeners==null)
    		return (T[])Array.newInstance(type, 0);
    	T[] tab = (T[])Array.newInstance(type, this.listeners.size());
    	this.listeners.toArray(tab);
    	return tab;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObjectEvent firePropertyChanged(String propertyName, Object oldValue, Object newValue) {
    	ModelObjectEvent event = new ModelObjectEvent(this, propertyName, oldValue, newValue);
    	for(ModelObjectListener listener : getListeners(ModelObjectListener.class)) {
    		listener.modelPropertyChanged(event);
    	}
    	return event; 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObjectEvent fireContainerChanged(ModelObject oldContainer, ModelObject newContainer) {
    	ModelObjectEvent event = new ModelObjectEvent(this, oldContainer, newContainer, Type.CONTAINER_CHANGE);
    	for(ModelObjectListener listener : getListeners(ModelObjectListener.class)) {
    		listener.modelContainerChanged(event);
    	}
    	return event; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObjectEvent fireContentChanged(ModelObjectEvent cause) {
    	ModelObjectEvent event = new ModelObjectEvent(this, cause);
    	for(ModelObjectListener listener : getListeners(ModelObjectListener.class)) {
    		listener.modelContentChanged(event);
    	}
    	return event; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObjectEvent fireLinkChanged(ModelObject oldLinkedObject, ModelObject newLinkedObject) {
    	ModelObjectEvent event = new ModelObjectEvent(this, oldLinkedObject, newLinkedObject, Type.LINK_CHANGE);
    	for(ModelObjectListener listener : getListeners(ModelObjectListener.class)) {
    		listener.modelLinkChanged(event);
    	}
    	return event; 
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObjectEvent fireComponentAdded(ModelObject component) {
    	ModelObjectEvent event = new ModelObjectEvent(this, component, Type.COMPONENT_ADDITION);
    	for(ModelObjectListener listener : getListeners(ModelObjectListener.class)) {
    		listener.modelComponentAdded(event);
    	}
    	return event; 
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObjectEvent fireComponentRemoved(ModelObject component) {
    	ModelObjectEvent event = new ModelObjectEvent(this, component, Type.COMPONENT_REMOVE);
    	for(ModelObjectListener listener : getListeners(ModelObjectListener.class)) {
    		listener.modelComponentRemoved(event);
    	}
    	return event; 
    }

    /** {@inheritDoc}
	 */
    @Override
	public synchronized ViewBinding getViewBinding() {
    	if (this.viewBinding==null) {
    		this.viewBinding = new ViewBinding(this);
    	}
    	return this.viewBinding;
    }
    
    /** Remove the view binding object.
     */
    synchronized void removeViewBinding() {
    	this.viewBinding = null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
    	String n = getName();
    	if (n!=null && !n.isEmpty())
    		return n;
    	return getClass().getSimpleName()+"@"+ //$NON-NLS-1$
    		Integer.toHexString(System.identityHashCode(this));
    }

}
