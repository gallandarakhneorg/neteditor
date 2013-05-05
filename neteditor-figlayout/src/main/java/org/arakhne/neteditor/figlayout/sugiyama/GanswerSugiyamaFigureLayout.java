/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND
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

package org.arakhne.neteditor.figlayout.sugiyama;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;

import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.afc.ui.vector.Margins;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.figlayout.AbstractDirectionBasedFigureLayout;
import org.arakhne.neteditor.figlayout.FigureLayoutDirection;
import org.arakhne.neteditor.figlayout.FigureLayoutUndoableEdit;
import org.arakhne.neteditor.figlayout.basic.BasicGridBagFigureLayout;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.util.ref.WeakTreeSet;
import org.arakhne.vmutil.locale.Locale;

/** This class represents the Ganswer-Sugiyama Layout Algorithm. The
 *  approach was first described by Gansner et al. in
 *  <a href="http://citeseer.nj.nec.com/gansner93technique.html">A Technique for Drawing Directed Graphs (1993)</a>.
 *  The Algorithm works only for non cyclic Graphs. This
 *  implementation contains three steps:<ol>
 *  <li><strong>Ranking:</strong> The Nodes divided into 
 *  different layers. For example: The
 *  first layer contains all nodes having no incoming edges.</li>
 *  <li><strong>Ordering:</strong> This Phase tries to reduce
 *  the number of crossings. There are
 *  different approaches possible: Here I use the
 *  Barycenter-Method. There are other methods thinkable.</li>
 *  <li><strong>Position:</strong> Place all the Node (and Edges)
 *  on a right place in the graph.
 *  </ol>
 *  <p>
 *  That means a long edge (over more then one layer) should be
 *  straight. The current solution is not very good and should
 *  be improved.
 *  <p>
 *  This implementation was adapted to cyclic graphs by $Author: galland$. 
 * 
 * @author $Author: baumgartner$
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class GanswerSugiyamaFigureLayout extends AbstractDirectionBasedFigureLayout {

	private float preferredInterLayerSpace = DEFAULT_MINIMAL_SIZE; 

	/**
	 */
	public GanswerSugiyamaFigureLayout() {
		//
	}

	/** Replies the preferred size to allocate to the space between the layers.
	 * 
	 * @return the preferred size to allocate to the space between the layers.
	 */
	public float getPreferredInterLayerSpace() {
		return this.preferredInterLayerSpace;
	}

	/** Set the preferred size to allocate to the space between the layers.
	 * 
	 * @param size is the preferred size to allocate to the space between the layers.
	 */
	public void setPreferredInterLayerSpace(float size) {
		this.preferredInterLayerSpace = Math.max(0, size);
	}

	/** Divides the Nodes and Edges into Layers of FakeNodes.
	 *
	 * @param figures are the figures to laying out.
	 * @param undo is the undoable edit to set.
	 * @return a grid of FakeNodes.
	 */
	private static List<List<LayerNode>> layering(Iterable<? extends Figure> figures, FigureLayoutUndoableEdit undo) {
		List<List<LayerNode>> layers = new ArrayList<List<LayerNode>>();
		// Loop on the figures and separate
		// - the decorations
		// - the nodes that have not entry point
		// - the others
		List<LayerNode> decorations = new ArrayList<LayerNode>();
		List<LayerNode> entryNodes = new ArrayList<LayerNode>();
		Set<Node<?,?,?,?>> consumedNodes = new TreeSet<Node<?,?,?,?>>();

		layers.add(decorations);
		layers.add(entryNodes);

		Figure firstEncounteredNode = null;

		for(Figure figure : figures) {
			if (figure instanceof NodeFigure<?,?>) {
				NodeFigure<?,?> nodeFigure = (NodeFigure<?,?>)figure;
				Node<?,?,?,?> node = nodeFigure.getModelObject();
				if (node!=null) {
					if (firstEncounteredNode==null)
						firstEncounteredNode = figure;
					if (!node.hasIncomingEdges()) {
						entryNodes.add(new LayerNode(1, entryNodes.size(), nodeFigure));
						consumedNodes.add(node);
					}
				}
				else {
					decorations.add(new LayerNode(0, decorations.size(), nodeFigure));
				}
			}
			else if (figure instanceof EdgeFigure<?>) {
				EdgeFigure<?> edgeFigure = (EdgeFigure<?>)figure;
				Edge<?,?,?,?> edge = edgeFigure.getModelObject();
				if (edge==null) {
					decorations.add(new LayerNode(0, decorations.size(), edgeFigure));
				}
				// Remove intermediate control points.
				while (edgeFigure.getCtrlPointCount()>2) {
					undo.addControlPointRemoval(edgeFigure, 1);
					edgeFigure.removeCtrlPointAt(1);
				}
			}
			else if (figure instanceof DecorationFigure && !(figure instanceof CoercedFigure)) {
				decorations.add(new LayerNode(0, decorations.size(), figure));
			}
		}

		if (entryNodes.isEmpty() && firstEncounteredNode!=null) {
			// The graph is cyclic; take one node randomly
			entryNodes.add(new LayerNode(1, entryNodes.size(), firstEncounteredNode));
		}

		if (entryNodes.isEmpty()) {
			// Only decorations may be drawn
			layers.remove(1); // remove the empty layer
		}
		else {
			// There is at least one node to laying out
			List<LayerNode> currentLayer = entryNodes;
			List<LayerNode> nextLayer;
			while (currentLayer!=null && !currentLayer.isEmpty()) {

				nextLayer = buildNextLayer(currentLayer, consumedNodes, undo);

				if (nextLayer!=null && !nextLayer.isEmpty()) {
					layers.add(nextLayer);
				}

				currentLayer = nextLayer;
			}
		}

		return layers;
	}

	/** Help function that checks if a LayerNode is inside a layer.
	 *
	 * @param node
	 * @param layer
	 * @return the layer node or <code>null</code> if there is no
	 * layer node for the given node.
	 */
	private static LayerNode retreiveLayerNodeFor(Node<?,?,?,?> node, List<LayerNode> layer) {
		for(LayerNode layerNode : layer) {
			if (!layerNode.isEdge() && layerNode.getNode()==node) {
				return layerNode;
			}
		}
		return null;
	}

	/** Returns the next Layer of LayerNodes coming after the given
	 *  layer.
	 *
	 * @param layer is the current Layer
	 * @param consumed contains the nodes that are already laying out.
	 * @return all Nodes following a LayerNode in the currentLayer
	 */
	private static List<LayerNode> buildNextLayer(List<LayerNode> layer, Set<Node<?,?,?,?>> consumed, FigureLayoutUndoableEdit undo) {
		List<LayerNode> nextLayer = new ArrayList<LayerNode>();

		for(LayerNode layerNode : layer) {
			if (!layerNode.isReference()) {
				if (layerNode.isEdge()) {
					// LayerNode is an Edge-LayerNode
					// If level of dummy < level of target - 1
					// Create a new LayerNode with level incremented by 1
					// Assign it Edge = same edge, Xcoord = currCoord
					// Set dummy as predecessor of this new fake node
					if (layerNode.firstLayerno() + layerNode.referencedNodeDistance() > layerNode.layerno() + 1) {
						LayerNode newLayerNode = new LayerNode(
								layerNode.layerno() + 1,
								nextLayer.size(),
								layerNode.getFigure(),
								layerNode.firstLayerno(),
								layerNode.referencedNodeDistance() - 1);
						nextLayer.add(newLayerNode);
						newLayerNode.addPredecessor(layerNode);
						layerNode.addSuccessor(newLayerNode);
					}
					else {
						// Layer node is the last dummy along an edge.
						// The target node might be a target node for other layerNode's
						// Check whether there already is a layer node for this "real" node.
						// If so, just add the current layer node as a new predecessor.
						// If not create a new layer node.
						Node<?,?,?,?> targetNode = layerNode.getEdge().getEndAnchor().getNode();
						NodeFigure<?,?> targetFigure = targetNode.getViewBinding().getView(layerNode.getView(), NodeFigure.class);
						LayerNode alreadyIn = retreiveLayerNodeFor(targetNode, nextLayer);
	
						if (alreadyIn != null) {
							alreadyIn.addPredecessor(layerNode);
							layerNode.addSuccessor(alreadyIn);
						}
						else {
							LayerNode newLayerNode = new LayerNode(
									layerNode.layerno() + 1,
									nextLayer.size(),
									targetFigure);
							nextLayer.add(newLayerNode);
							newLayerNode.addPredecessor(layerNode);
							layerNode.addSuccessor(newLayerNode);
						}
	
					}
				}
				else {
					Node<?,?,?,?> targetNode;
					NodeFigure<?,?> targetFigure;
					EdgeFigure<?> edgeFigure;
					LayerNode alreadyIn;
					Node<?,?,?,?> node = layerNode.getNode();
					assert(node!=null);
					
					for(Edge<?,?,?,?> outgoingEdge : node.getOutgoingEdges()) {
	
						// Remove intermediate control points.
						edgeFigure = outgoingEdge.getViewBinding().getView(layerNode.getView(), EdgeFigure.class);
						if (edgeFigure!=null) {
							while (edgeFigure.getCtrlPointCount()>2) {
								undo.addControlPointRemoval(edgeFigure, 1);
								edgeFigure.removeCtrlPointAt(1);
							}
						}
	
						targetNode = outgoingEdge.getOtherSideFrom(node);
						targetFigure = targetNode.getViewBinding().getView(layerNode.getView(), NodeFigure.class);
	
						if (targetFigure!=null) {
							alreadyIn = retreiveLayerNodeFor(targetNode, layer);
							
							if (alreadyIn!=null && !alreadyIn.isReference()) {
								// The node was already put in a previous layer.
								// Replace the found object by a dummy.
								alreadyIn.setReference(1);
								consumed.remove(targetNode);
							}
							
							if (!consumed.contains(targetNode)) {
								alreadyIn = retreiveLayerNodeFor(targetNode, nextLayer);
		
								if (alreadyIn!=null) {
									// Here we know the target node is on layerNode.layerno + 1
									alreadyIn.addPredecessor(layerNode);
									layerNode.addSuccessor(alreadyIn);
								}
								else {
									// Here we have to check whether the edge is over two consecutive levels
									// or not, in which case we have to insert a dummy.
									LayerNode newLayerNode;
									int maxDist = getMaxDistance(node, targetNode);
		
									assert(maxDist>=1);
		
									if (maxDist == 1) {
										// add a Node-LayerNode more
										newLayerNode = new LayerNode(
												layerNode.layerno() + 1,
												nextLayer.size(),
												targetFigure);
										nextLayer.add(newLayerNode);
										consumed.add(targetNode);
									}
									else {
										// add a dummy LayerNode
										newLayerNode = new LayerNode(
												layerNode.layerno() + 1,
												nextLayer.size(),
												targetFigure,
												layerNode.firstLayerno(),
												maxDist);
										nextLayer.add(newLayerNode);
									}
		
									newLayerNode.addPredecessor(layerNode);
									layerNode.addSuccessor(newLayerNode);
								}
							}
						}
					} // For every outgoing edge
				}
			}
		} // for every LayerNode
		
		return nextLayer;
	}

	private static int getMaxDistance(Node<?,?,?,?> from, Node<?,?,?,?> to) {
		SortedSet<MaxDistanceNodeCandidate> openList = new TreeSet<MaxDistanceNodeCandidate>();
		TreeSet<Node<?,?,?,?>> closeList = new TreeSet<Node<?,?,?,?>>();
		openList.add(new MaxDistanceNodeCandidate(null, from, 0));

		int max = -1;
		Iterator<MaxDistanceNodeCandidate> iterator;
		MaxDistanceNodeCandidate e, old;
		Node<?,?,?,?> targetNode;

		while (!openList.isEmpty()) {
			iterator = openList.iterator();
			e = iterator.next();
			iterator.remove();
			if (e.node == to) {
				if (e.distance>max) {
					max = e.distance;
				}
				closeList.add(e.node);
			}
			else {
				boolean inCloseList = closeList.contains(e.node);
				if (!inCloseList || !e.isLoop()) {
					for(Edge<?,?,?,?> edge : e.node.getOutgoingEdges()) {
						targetNode = edge.getOtherSideFrom(e.node);
						old = removeIn(targetNode, openList);
						if (old==null || old.distance<(e.distance+1)) {
							openList.add(new MaxDistanceNodeCandidate(
									e, targetNode, e.distance + 1));
						}
						else {
							// Reinject the data
							openList.add(old);
						}
					}
					if (!inCloseList) closeList.add(e.node);
				}
			}
		}
		
		openList.clear();
		closeList.clear();

		return max;
	}
	
	private static MaxDistanceNodeCandidate removeIn(Node<?,?,?,?> node, SortedSet<MaxDistanceNodeCandidate> s) {
		Iterator<MaxDistanceNodeCandidate> iterator = s.iterator();
		MaxDistanceNodeCandidate c;
		while (iterator.hasNext()) {
			c = iterator.next();
			if (c.node==node) {
				iterator.remove();
				return c;
			}
		}
		return null;
	}

	/** Computes the barycentric coordinates of nodes.
	 *
	 * @param node the node
	 * @param neighbors all the nodes of the layer above or
	 *                  below the layer of the node.
	 * @return the barycenter weight
	 */
	private static float barycenter(LayerNode node, List<LayerNode> neighbors) {
		// Must take care of a special case here
		if (neighbors.isEmpty()) {
			return node.getPositionInLayer();
		}

		float barycenter = 0f;
		for(LayerNode neighbor : neighbors) {
			barycenter += neighbor.getPositionInLayer();
		}

		return barycenter / neighbors.size();
	}

	/** Utility method: swaps two consecutive elements in a layer as
	 *  well as the associated coordinates. The variables min and max
	 *  descibe the interval of values for the nodes on the above
	 *  layer, over which the current layer should be centered.
	 *
	 * @param layer the layer
	 */
	private static void adjustCoord(List<LayerNode> layer) {
		Collections.sort(layer, new WeightComparator());
		int i=0;
		for(LayerNode node : layer) {
			node.setPositionInLayer(i);
			++i;
		}
	}

	/** Orders the Node in the layer to reduce the number of
	 *  crossings.
	 *  <p>
	 *  The first layer is dedicated to the decorations.
	 *
	 * @param layers are the layers to order.
	 */
	private static void ordering(List<List<LayerNode>> layers) {
		List<LayerNode> top = layers.get(1);
		List<LayerNode> next;

		for (int i=2; i<layers.size(); ++i) {
			next = layers.get(i);

			for(LayerNode node : top) {
				node.setWeight( barycenter(node, next) );
			}

			adjustCoord(top);

			for(LayerNode node : next) {
				node.setWeight( barycenter(node, top) );
			}

			adjustCoord(next);
		}

	}
	
	private static Dimension getLayerDimension(List<LayerNode> layer, FigureLayoutDirection direction, Margins insets, float layerSpace) {
		float maxW = 0;
		float maxH = 0;
		float s;
		Figure figure;
		switch(direction) {
		case HORIZONTAL: {
			float vSpace = Math.max(layerSpace, insets.top() + insets.bottom());
			for(LayerNode node : layer) {
				if (!node.isEdge()) {
					figure = node.getFigure();
					maxW += figure.getWidth() + insets.left() + insets.right();
					s = figure.getHeight() + vSpace;
					if (s>maxH) maxH = s;
				}
			}
			break;
		}
		case VERTICAL: {
			float hSpace = Math.max(layerSpace, insets.left() + insets.right());
			for(LayerNode node : layer) {
				if (!node.isEdge()) {
					figure = node.getFigure();
					s = figure.getWidth() + hSpace;
					if (s>maxW) maxW = s;
					maxH += figure.getHeight() + insets.top() + insets.bottom();
				}
			}
			break;
		}
		default:
		}
		return VectorToolkit.dimension(maxW, maxH);
	}

	/** Place all Node on the right position.
	 * <p>
	 *  The first layer is dedicated to the decorations.
	 *
	 * @param layers
	 * @param direction
	 * @param insets
	 * @param origin
	 * @param layerSpace
	 */
	private static void positioning(List<List<LayerNode>> layers, FigureLayoutDirection direction, Margins insets, Point2D origin, float layerSpace, FigureLayoutUndoableEdit undo) {
		float maxWidth = 0;
		float maxHeight = 0;

		int rows = layers.size();
		Dimension[] layerDimensions = new Dimension[rows];
		int[] length = new int[rows];

		// compute the max size of of the layer
		for (int i = 0; i<rows; i++) {
			length[i] = layers.get(i).size();
			layerDimensions[i] = getLayerDimension(layers.get(i), direction, insets, layerSpace);

			if (layerDimensions[i].height() > maxHeight) {
				maxHeight = layerDimensions[i].height();
			}

			if (layerDimensions[i].width() > maxWidth) {
				maxWidth = layerDimensions[i].width();
			}

		}

		switch (direction) {
		case HORIZONTAL: {
			float y = origin.getY();
			for (int i=0; i<rows; ++i) {
				List<LayerNode> layer = layers.get(i);
				float x = origin.getX();
				float dspace = (Math.max(0f,  maxWidth - layerDimensions[i].width()) / layer.size()) / 2f;
				Figure figure;

				for(LayerNode node : layer) {
					if (!node.isEdge()) {
						figure = node.getFigure();
						x += insets.left() + dspace;
						undo.addLocationChange(figure, x, y + insets.top());
						figure.setLocation(x, y + insets.top());
						x += figure.getWidth() + insets.right() + dspace;
					}
				}

				y += layerDimensions[i].height();
			}
			break;
		}
		case VERTICAL: {
			float x = origin.getX();
			for (int i=0; i<rows; ++i) {
				List<LayerNode> layer = layers.get(i);
				float y = origin.getX();
				float dspace = (Math.max(0f,  maxHeight - layerDimensions[i].height()) / layer.size()) /2f;
				Figure figure;

				for(LayerNode node : layer) {
					if (!node.isEdge()) {
						figure = node.getFigure();
						y += insets.top() + dspace;
						undo.addLocationChange(figure, x + insets.left(), y);
						figure.setLocation(x + insets.left(), y);
						y += figure.getHeight() + insets.bottom() + dspace;
					}
				}

				x += layerDimensions[i].width();
			}
			break;
		}
		default:
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Undoable layoutFigures(Collection<? extends Figure> figures) {
		FigureLayoutUndoableEdit undo = new FigureLayoutUndoableEdit(
				Locale.getString(GanswerSugiyamaFigureLayout.class, "UNDO_NAME")); //$NON-NLS-1$
		
		List<List<LayerNode>> layers = layering(figures, undo);
		switch(layers.size()) {
		case 0:
			// Nothing to do
			break;
		case 1:
			// Only decorations
		{
			BasicGridBagFigureLayout gridLayout = new BasicGridBagFigureLayout();
			gridLayout.setLayoutDirection(getLayoutDirection());
			gridLayout.setMargins(getMargins());
			undo.add(gridLayout.layoutFigures(figures));
			break;
		}
		default:
			// General case, there is something to laying out.
			ordering(layers);
			
			positioning(layers, getLayoutDirection(), getMargins(), getOrigin(), getPreferredInterLayerSpace(), undo);
		}
		
		if (undo.isEmpty()) return null;
		return undo;
	}

	/** This class represents a node during the laying out.
	 * 
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class LayerNode {

		/** Number of the layer in which this node is.
		 */
		private final int layerno;

		/** Number of the layer in which this node is appearing the first time.
		 */
		private final int startLayerno;

		/** Decoration figure embedded in this node.
		 */
		private final Figure figure;

		/** List of the predecessor nodes in the previous layer.
		 */
		private final Set<LayerNode> predecessors = new WeakTreeSet<LayerNode>();

		/** List of the successors nodes in the next layer.
		 */
		private final Set<LayerNode> successors = new WeakTreeSet<LayerNode>();

		/** Distance to the farest layer that is containing the node
		 * embedded in this LayerNode. If the value is not {@code -1},
		 * is means that this LayerNode is a link to an other LayerNode.
		 */
		private int referencedDistance;

		/** Weight of the layer node.
		 */
		private float weight = 0f;

		/** Position of this node in the layer.
		 */
		private int positionInLayer;

		/**
		 * @param layerno
		 * @param position
		 * @param figure
		 */
		public LayerNode(int layerno, int position, Figure figure) {
			this.layerno = layerno;
			this.figure = figure;
			this.startLayerno = layerno;
			this.referencedDistance = -1;
			this.positionInLayer = position;
		}

		/**
		 * @param layerno
		 * @param position
		 * @param figure
		 * @param firstLayerno
		 * @param referencedDistance
		 */
		public LayerNode(int layerno, int position, Figure figure,
				int firstLayerno, int referencedDistance) {
			this.layerno = layerno;
			this.figure = figure;
			this.startLayerno = firstLayerno;
			this.referencedDistance = referencedDistance;
			this.positionInLayer = position;
		}

		/** Replies the weight of the node.
		 * 
		 * @return the weight.
		 */
		public float getWeight() {
			return this.weight;
		}

		/** Set the weight of the node.
		 * 
		 * @param weight
		 */
		public void setWeight(float weight) {
			this.weight = weight;
		}

		/** Replies the position of the node in the layer.
		 * 
		 * @return the position in the layer.
		 */
		public int getPositionInLayer() {
			return this.positionInLayer;
		}

		/** Set the position of the node in the layer.
		 * 
		 * @param position is the position in the layer.
		 */
		public void setPositionInLayer(int position) {
			this.positionInLayer = position;
		}

		/** Replies the distance to the referenced node.
		 * 
		 * @return the distance to the referenced node.
		 */
		public int referencedNodeDistance() {
			return this.referencedDistance;
		}
		
		/** Replies if this layer node is a reference to an other LayerNode.
		 * 
		 * @return <code>true</code> if a reference.
		 */
		public boolean isReference() {
			return this.referencedDistance>=1;
		}

		/** Set this layer node as a reference.
		 * 
		 * @param referencedDistance is the distance to the referenced object.
		 */
		public void setReference(int referencedDistance) {
			this.referencedDistance = Math.max(1, referencedDistance);
		}

		/** Replies the view UUID for the embedded figure.
		 * 
		 * @return the view UUID for the embedded figure.
		 */
		public UUID getView() {
			return this.figure.getViewUUID();
		}

		/** Register the given node as a predecessor of this node.
		 * A predecessor is a LayerNode that is inside a layer with
		 * a number lower than the layer number of this LayerNode,
		 * and there is an edge from the precessor to this LayerNode.
		 * 
		 * @param predecessor
		 */
		public void addPredecessor(LayerNode predecessor) {
			assert(predecessor.layerno()<layerno());
			this.predecessors.add(predecessor);
		}

		/** Register the given node as a successor of this node.
		 * A successor is a LayerNode that is inside a layer with
		 * a number upper than the layer number of this LayerNode,
		 * and there is an edge from this LayerNode to the successor.
		 * 
		 * @param successor
		 */
		public void addSuccessor(LayerNode successor) {
			assert(successor.layerno()>layerno());
			this.successors.add(successor);
		}

		/** Replies the number of the layer in which this layer node is.
		 * 
		 * @return the number of the layer in which this layer node is.
		 */
		public int layerno() {
			return this.layerno;
		}

		/** Replies the number of the layer in which this layer node is
		 * appearing the first time.
		 * 
		 * @return the number of the first layer.
		 */
		public int firstLayerno() {
			return this.startLayerno;
		}

		/** Replies if this layer node contains an edge.
		 * 
		 * @return <code>true</code> if the figure inside is for an edge;
		 * otherwise <code>false</code>.
		 */
		public boolean isEdge() {
			return this.figure instanceof EdgeFigure<?>;
		}

		/** Replies the node embedded inside this layer node.
		 * 
		 * @return the node, or <code>null</code> if these is not a node inside.
		 */
		public Node<?,?,?,?> getNode() {
			if (this.figure instanceof NodeFigure<?,?>) {
				return ((NodeFigure<?,?>)this.figure).getModelObject();
			}
			return null;
		}

		/** Replies the edge embedded inside this layer node.
		 * 
		 * @return the edge, or <code>null</code> if these is not an edge inside.
		 */
		public Edge<?,?,?,?> getEdge() {
			if (this.figure instanceof EdgeFigure<?>) {
				return ((EdgeFigure<?>)this.figure).getModelObject();
			}
			return null;
		}

		/** Replies the figure embedded inside this layer node.
		 * 
		 * @return the figure.
		 */
		public Figure getFigure() {
			return this.figure;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("((LayerNode"); //$NON-NLS-1$
			if (isReference()) {
				b.append("->"); //$NON-NLS-1$
			}
			else {
				b.append(":"); //$NON-NLS-1$
			}
			b.append(this.figure.toString());
			b.append("))"); //$NON-NLS-1$
			return b.toString();
		}

	}

	/** Comparator of LayerNode based on the weights.
	 * 
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class WeightComparator implements Comparator<LayerNode> {

		/**
		 */
		public WeightComparator() {
			//
		}

		@Override
		public int compare(LayerNode o1, LayerNode o2) {
			if (o1==o2) return 0;
			if (o1==null) return Integer.MIN_VALUE;
			if (o2==null) return Integer.MAX_VALUE;
			int cmp = Float.compare(o1.getWeight(), o2.getWeight());
			if (cmp!=0) return cmp;
			return o1.getFigure().compareTo(o2.getFigure());
		}

	}

	/** Object that is containing a node and the maximal distance to reach.
	 * 
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class MaxDistanceNodeCandidate implements Comparable<MaxDistanceNodeCandidate> {

		/** Node.
		 */
		public final Node<?,?,?,?> node;

		/** Distance to reach the node.
		 */
		public final int distance;
		
		/** The entering node.
		 */
		public final MaxDistanceNodeCandidate from;
		
		private Boolean isLoop = null;
		
		/**
		 * @param from
		 * @param node
		 * @param distance
		 */
		public MaxDistanceNodeCandidate(MaxDistanceNodeCandidate from, Node<?,?,?,?> node, int distance) {
			this.node = node;
			this.distance = distance;
			this.from = from;
		}
		
		@Override
		public int compareTo(MaxDistanceNodeCandidate o) {
			if (o==null) return Integer.MAX_VALUE;
			int cmp = o.distance - this.distance; // invert the computation to put the max values first
			if (cmp!=0) return cmp;
			if (this.from!=o.from) {
				if (this.from==null) return Integer.MIN_VALUE;
				if (o.from==null) return Integer.MAX_VALUE;
				cmp = this.from.compareTo(o.from);
				if (cmp!=0) return cmp;
			}
			return this.node.compareTo(o.node);
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(this.node);
			b.append("{"); //$NON-NLS-1$
			b.append(this.distance);
			b.append("}"); //$NON-NLS-1$
			return b.toString();
		}
		
		/** Replies if this candidate makes a loop with itself.
		 * 
		 * @return <code>true</code> if the candidate make a loop; otherwise <code>false</code>.
		 */
		public boolean isLoop() {
			if (this.isLoop==null) {
				MaxDistanceNodeCandidate c = this.from;
				while (this.isLoop==null && c!=null) {
					if (c.node==this.node) this.isLoop = Boolean.TRUE;
					c = c.from;
				}
				if (this.isLoop==null) this.isLoop = Boolean.FALSE;
			}
			return this.isLoop.booleanValue();
		}

	}

}
