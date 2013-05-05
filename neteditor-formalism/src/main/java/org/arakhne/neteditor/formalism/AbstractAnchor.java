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

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.UUID;

/** This is the Anchor that permit to connect
 *  {@link Edge Edges}
 *  and {@link Node Nodes}.
 *
 * @param <G> is the type of the graph.
 * @param <N> is the type of the node inside the graph.
 * @param <A> is the type of the anchors inside the graph.
 * @param <E> is the type of the node inside the graph.
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractAnchor<G extends Graph<?,N,A,E>, N extends Node<G,?,A,E>, A extends Anchor<G,N,?,E>, E extends Edge<G,N,A,?>>
extends AbstractModelObject implements Anchor<G,N,A,E> {

    /**
	 */
	private static final long serialVersionUID = -2361316544500614256L;
	
	private WeakReference<N> node;
	
	private AnchorLocation location = AnchorLocation.CENTER;

	/** Construct a new XAnchor.
     */
    public AbstractAnchor() {
    	//
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObject findModelObject(UUID id) {
    	if (getUUID().equals(id)) return this;
    	return null;
    }

    /** {@inheritDoc}
	 */
    @Override
	public Map<String,Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_LOCATION, this.location); 
		return properties;
	}

    /**
     * {@inheritDoc}
     */
    @Override
    public void setProperties(Map<String, Object> properties) {
    	super.setProperties(properties);
    	if (properties!=null) {
    		setLocation(propGet(AnchorLocation.class, PROPERTY_LOCATION, getLocation(), true, properties)); 
    	}
    }

    /**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void setNode(N node) {
		N old = getNode();
		if (node!=old) {
			if (old!=null)
				old.removeAnchor((A)this);
			if (node==null)
				this.node = null;
			else {
				this.node = new WeakReference<N>(node);
				node.addAnchor(this.location, (A)this);
			}
			fireContainerChanged(old, node);
		}
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public N getNode() {
		return this.node==null ? null : this.node.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setLocation(AnchorLocation location) {
		if (location!=null && this.location!=location) {
			AnchorLocation old = this.location;
			this.location = location;
			firePropertyChanged(PROPERTY_ANCHORLOCATION, old, this.location);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AnchorLocation getLocation() {
		return this.location;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canConnectAsStartAnchor(E edge, A endAnchor) {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canConnectAsEndAnchor(E edge, A startAnchor) {
		return true;
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
    	String anchorName = super.toString();
    	N node = getNode();
    	if (node!=null)
    		return anchorName+" in "+node.toString(); //$NON-NLS-1$
    	return anchorName;
    }
    
}
