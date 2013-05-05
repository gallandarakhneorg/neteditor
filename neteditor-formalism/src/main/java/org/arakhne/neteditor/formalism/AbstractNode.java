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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** This is the semantic object for nodes.
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
public abstract class AbstractNode<G extends Graph<?,N,A,E>, N extends Node<G,?,A,E>, A extends Anchor<G,N,?,E>, E extends Edge<G,N,A,?>>
extends AbstractModelObject implements Node<G,N,A,E> {

	private static final long serialVersionUID = 1895853418186145858L;

	private WeakReference<G> graph = null;
	
    /** Construct a new XNode.
     */
    public AbstractNode() {
    	//
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public ModelObject findModelObject(UUID id) {
    	if (getUUID().equals(id)) return this;
    	for(A anchor : getAnchors()) {
    		ModelObject obj = anchor.findModelObject(id);
    		if (obj!=null) return obj;
    	}
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalLabel() {
    	return getName();
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	public G getGraph() {
		return this.graph==null ? null : this.graph.get();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setGraph(G model) {
		if (model==null)
			this.graph = null;
		else
			this.graph = new WeakReference<G>(model);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isAnchor(A anchor) {
		return getAnchors().contains(anchor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasAnchor() {
		return !getAnchors().isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<E> getEdges() {
		List<E> edges = new ArrayList<E>();
		for(A anchor : getAnchors()) {
			edges.addAll(anchor.getEdges());
		}
		return edges;
	}
	
}
