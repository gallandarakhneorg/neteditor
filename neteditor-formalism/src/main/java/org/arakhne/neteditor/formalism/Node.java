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

/** This is the semantic object for nodes.
 * 
 * @param <G> is the type of the graph.
 * @param <N> is the type of the node inside the graph.
 * @param <A> is the type of the anchors inside the graph.
 * @param <E> is the type of the edges inside the graph.
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Node<G extends Graph<?,N,A,E>, N extends Node<G,?,A,E>, A extends Anchor<G,N,?,E>, E extends Edge<G,N,A,?>>
extends ModelObject {

	/** Replies a string that may be used to put on the size of the edge.
	 * Basically it is composed of the name of the edge replied
	 * by {@link #getName()} and any other label added by the
	 * subclasses.
	 * 
	 * @return the external label that is describing this node.
	 */
	public String getExternalLabel();
	
    /** Get the {@link Graph}
     *  of this Node
     *
     * @return a {@link Graph}.
     */
    public G getGraph();

    /** Set the {@link Graph}
     *  of this Node
     *
     * @param model a {@link Graph}.
     */
    public void setGraph(G model);

    /** Get the anchor list of this Node.
     *
     * @return a list of {@link Anchor}.
     */
    public List<A> getAnchors();

    /** Attached a anchor to this Node.
     *
     * @param anchor the new {@link Anchor}.
     */
    public void addAnchor(A anchor);
    
    /** Attached a anchor to this Node.
     *
     * @param anchor the new {@link Anchor}.
     * @param location the location of the new {@link Anchor}.
     * @since 0.4
     */
    public void addAnchor(AnchorLocation location, A anchor);

    /** Remove a anchor attached to this Node.
     *
     * @param anchor the {@link Anchor} to remove.
     */
    public void removeAnchor(A anchor);

    /** Remove all anchors attached to this Node.
     *
     * @since 0.3
     */
    public void removeAllAnchors() ;

    /** Reply <code>true</code> if the given {@link Anchor}
     *  is attached to this Node.
     *
     * @param anchor the {@link Anchor}.
     * @return <code>true</code> if <var>anchor</var> was attached
     *         to this Node, <code>false</code> otherwise.
     */
    public boolean isAnchor(A anchor);

    /** Reply <code>true</code> if an {@link Anchor}
     *  is attached to this Node.
     *
     * @return <code>true</code> if an anchor was attached
     *         to this Node, <code>false</code> otherwise.
     * @since 16.0
     */
    public boolean hasAnchor();

    /** Reply all {@link Edge Edges}
     *  attached to this Node by the way of 
     *  {@link Anchor Ports}.
     *
     * @return an array  of {@link Edge Edges}.
     */
    public List<E> getEdges();

    /** Disconnect the node from the edges.
     */
    public void disconnect();

    /** Reply if this node has at least one incoming edge.
     * <p>
     * The definition of an incoming edge depends on the implementation
     * of the node.
     *
     * @return <code>true</code> if the node is linked to an
     * incoming edge; otherwise <code>false</code>.
     * @since 16.0
     */
    public boolean hasIncomingEdges();

    /** Reply if this node has at least one outgoing edge.
     * <p>
     * The definition of an outgoing edge depends on the implementation
     * of the node.
    *
     * @return <code>true</code> if the node is linked to an
     * outgoing edge; otherwise <code>false</code>.
     * @since 16.0
    */
   public boolean hasOutgoingEdges();

   /** Reply the incoming edges of this node.
    * <p>
    * The definition of an incoming edge depends on the implementation
    * of the node. For example, in not-directed graphs, this function
    * should replies the same edges as {@link #getEdges()}.
    *
    * @return the incoming edges.
    * @since 16.0
    */
   public Iterable<E> getIncomingEdges();

   /** Reply the outgoing edges of this node.
    * <p>
    * The definition of an outgoing edge depends on the implementation
    * of the node. For example, in not-directed graphs, this function
    * should replies the same edges as {@link #getEdges()}.
    *
    * @return the incoming edges.
    * @since 16.0
    */
   public Iterable<E> getOutgoingEdges();

}
