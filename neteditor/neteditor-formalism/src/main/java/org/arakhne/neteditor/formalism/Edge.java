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

/** Abstract {@link ModelObject}
 *  class for representing edges between 
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
public interface Edge<G extends Graph<?,N,A,E>, N extends Node<G,?,A,E>, A extends Anchor<G,N,?,E>, E extends Edge<G,N,A,?>>
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
	 * @param graph a {@link Graph}.
	 */
	public void setGraph(G graph);

	/** Reply the end {@link Anchor connection point}.
	 *
	 * @return {@link Anchor connection point}.
	 */
	public A getEndAnchor();

	/** Set the end {@link Anchor connection point}.
	 *
	 * @param anchor {@link Anchor connection point}.
	 */
	public void setEndAnchor(A anchor);

	/** Reply the begin {@link Anchor connection point}.
	 *
	 * @return {@link Anchor connection port}.
	 */
	public A getStartAnchor();

	/** Set the begin {@link Anchor connection point}.
	 *
	 * @param anchor {@link Anchor connection point}.
	 */
	public void setStartAnchor(A anchor);

	/** Disconnect the edge from the anchors.
	 */
	public void disconnect();

	/** Replies the anchor at the other side of this edge
	 * from the specified anchor. If the specified
	 * anchor is <code>null</code>, this function
	 * replies <code>null</code>.
	 *
	 * @param anchor {@link Anchor connection point}.
	 * @return the anchor at the other side of the given anchor.
	 * @see #getOtherSideFrom(Node)
	 */
	public A getOtherSideFrom(Anchor<?,?,?,?> anchor);

	/** Replies the node at the other side of this edge
	 * from the specified node. If the specified
	 * node is <code>null</code>, this function
	 * replies <code>null</code>.
	 * This function is a convenient version of
	 * {@link #getOtherSideFrom(Anchor)}.
	 *
	 * @param node
	 * @return the anchor at the other side of the given anchor.
	 * @see #getOtherSideFrom(Anchor)
	 * @since 16.0
	 */
	public N getOtherSideFrom(Node<?,?,?,?> node);

}
