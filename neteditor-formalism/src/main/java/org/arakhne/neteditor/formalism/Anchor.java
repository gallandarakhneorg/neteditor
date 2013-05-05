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

import java.util.List;

/** This is the connection point that permit to connect
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
public interface Anchor<G extends Graph<?,N,A,E>, N extends Node<G,?,A,E>, A extends Anchor<G,N,?,E>, E extends Edge<G,N,A,?>>
extends ModelObject {
        
    /** Set the {@link Node}
     *  that contains this Anchor.
     *
     * @param node {@link Node} container.
     */
    public void setNode(N node);

    /** Get the {@link Node}
     *  that contains this Anchor.
     *
     * @return {@link Node Node} container.
     */
    public N getNode();

    /** Reply all {@link Edge Edges}
     *  attached to this Node by the way
     *  of {@link Anchor Anchors}.
     *
     * @return a set of {@link Edge Edges}.
     */
    public List<E> getEdges();

    /** Attached an {@link Edge Edge}
     *  to this Anchor.
     *
     * @param edge a new {@link Edge Edges}.
     */
    public void addEdge(E edge);

    /** Remove an {@link Edge}
     *  attached to this Anchor.
     *
     * @param edge the {@link Edge Edges} to remove.
     */
    public void removeEdge(E edge);

    /** Removes all {@link Edge Edges}
     *  attached to this Anchor.
     *
     * @since 0.2 
     */
    public void removeAllEdges() ;
    
    
   /** Sets the location of the Anchor according to the owner     
	 * The name of the property is "anchorLocation".
     *
     * @param location the location of the Anchor     
     * @since 0.4
     */
    public void setLocation(AnchorLocation location);
    
    /** Gets the location of the Anchor according to the owner     
     *  <p>
     *  
     *
     * @return the location of the Anchor     
     * @since 0.4
     */
     public AnchorLocation getLocation();

     /** Reply if this anchor can be the beginning of the
      * specified edge if the end is binded
      * to the specified anchor.
      *  By default all edges can be connected to the anchor.
      *  Subclasses must override this method to have a more
      *  restrictive managing.
      *
      * @param edge is the edge to test against this anchor..
      * @param endAnchor is the anchor in the other side of the given edge. It may be <code>null</code>.
      * @return <code>true</code> if this anchor can be connected
      *         to the given edge as a start anchor.
      */
     public boolean canConnectAsStartAnchor(E edge, A endAnchor);

    /** Reply if this anchor can be the end of the
     * specified edge if the beginning is binded
     * to the specified anchor.
     *  By default all edges can be connected to the anchor.
     *  Subclasses must override this method to have a more
     *  restrictive managing.
     *
     * @param edge is the edge to test against this anchor..
     * @param startAnchor is the anchor in the other side of the given edge. It may be <code>null</code>.
     * @return <code>true</code> if this anchor can be connected
     *         to the given edge as a start anchor.
     */
    public boolean canConnectAsEndAnchor(E edge, A startAnchor);
    
}
