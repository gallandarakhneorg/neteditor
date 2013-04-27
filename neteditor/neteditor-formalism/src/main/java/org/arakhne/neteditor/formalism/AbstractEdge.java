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

import java.lang.ref.WeakReference;
import java.util.UUID;

/** Abstract {@link ModelObject}
 *  classe for representing edges between 
 *  {@link Anchor anchors}.
 *  
 * @param <G> is the type of the graph.
 * @param <N> is the type of the node inside the graph.
 * @param <A> is the type of the anchors inside the graph.
 * @param <E> is the type of the node inside the graph.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEdge<G extends Graph<?,N,A,E>, N extends Node<G,?,A,E>, A extends Anchor<G,N,?,E>, E extends Edge<G,N,A,?>>
extends AbstractModelObject implements Edge<G,N,A,E> {

	private static final long serialVersionUID = -286097395687225696L;

    /** This is the
     *  {@link Graph} that
     *  contains this Edge.
     */
    private WeakReference<G> graph = null;

    /** Contruct a new XEdge.
     */
    public AbstractEdge() {
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
	public void setGraph(G graph) {
		if (graph==null) 
			this.graph = null;
		else
			this.graph = new WeakReference<G>(graph);
	}

	/**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
    	String edgeName = super.toString();
    	A s = getStartAnchor();
    	A e = getEndAnchor();
    	if (s!=null || e!=null) {
    		StringBuilder b = new StringBuilder();
    		b.append(edgeName);
    		if (s!=null) {
    			b.append(" from "); //$NON-NLS-1$
    			b.append(s.toString());
    		}
    		if (e!=null) {
    			b.append(" to "); //$NON-NLS-1$
    			b.append(e.toString());
    		}
    		return b.toString();
    	}
    	return edgeName;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public A getOtherSideFrom(Anchor<?,?,?,?> anchor) {
    	if (anchor!=null) {
    		A s = getStartAnchor();
    		A e = getEndAnchor();
    		if (s==anchor) return e;
    		if (e==anchor) return s;
    	}
    	return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public N getOtherSideFrom(Node<?,?,?,?> node) {
    	if (node!=null) {
    		A s = getStartAnchor();
    		A e = getEndAnchor();
    		for(Anchor<?,?,?,?> anchor : node.getAnchors()) {
    			if (anchor==s) return e.getNode();
    			if (anchor==e) return s.getNode();
    		}
    	}
    	return null;
    }

}
