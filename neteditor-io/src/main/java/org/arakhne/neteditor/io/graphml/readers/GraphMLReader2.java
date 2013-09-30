/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.graphml.readers ;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.afc.util.Pair;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.graphml.GraphMLException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This class permits to read a
 *  <strong>graph-model</strong> from the GraphML format.
 *  <p>
 *  This reader supports the NetEditor/GraphML specification version "2".
 *  <p>
 *  GraphML is a comprehensive and easy-to-use file format for graphs.
 *  It consists of a language core to describe the structural properties
 *  of a graph and a flexible extension mechanism to add
 *  application-specific data. 
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://graphml.graphdrawing.org/"
 * @since 16.0
 */
public class GraphMLReader2 extends AbstractGraphMLReader {

	/** Version of the specification supported by the GraphML reader.
	 */
	public static String SPECIFICATION_VERSION = "2"; //$NON-NLS-1$
	
	/**
	 */
	GraphMLReader2() {
		//
	}

	/** Extract the figures.
	 * 
	 * @param root is the GraphML root.
	 * @param progression notifies on the progression of the extracted.
	 * @throws IOException
	 */
	private void extractFigures(Element root, Progression progression) throws IOException {
		for(Element dataN : elements(root, N_DATA, progression)) {
			String dkey = dataN.getAttribute(A_KEY);
			if (dkey!=null && dkey.equals(C_KEY_NETEDITOR_VIEWS)) {
				Progression subTask = ProgressionUtil.sub(progression, PROGRESS_STEP_SIZE_IN_ITERATOR);
				for(Element viewN : elements(dataN, N_NETEDITOR_VIEW, subTask)) {
					UUID viewId = enforceUUID(viewN.getAttribute(A_ID));
					
					ViewDescription viewDescription = getViewDescription(viewId);
					
					int position = 0;
					
					for(Element componentN : elements(viewN, N_NETEDITOR_VIEWCOMPONENT, ProgressionUtil.sub(subTask, PROGRESS_STEP_SIZE_IN_ITERATOR))) {
						UUID componentId = enforceUUID(componentN.getAttribute(A_ID));
						
						Figure figure = createFigureInstance(Figure.class, componentN, viewId);
						
						extractAttributes(figure, componentN, null);
						figure.setUUID(componentId);
						figure.setViewUUID(viewId);
						
						Element modelIdNode = extractData(componentN, C_KEY_NETEDITOR_MODELID);
						UUID modelId = null;
						if (modelIdNode!=null) {
							try {
								modelId = UUID.fromString(modelIdNode.getTextContent());
							}
							catch(Throwable _) {
								//
							}
						}
						
						viewDescription.addViewComponent(componentId, figure, position);
						if (modelId!=null) {
							viewDescription.addModelObject(componentId, modelId);
						}
						
						extractSubFigures(componentN, figure, componentId, viewId, position, null);
						
						extractCoercedFigures(componentN, componentId, viewId);
						
						++position;
					}
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Graph<?,?,?,?> extractGraph(Progression progression) throws IOException {
		try {
			Element gmlN = extractNode(document(), N_GRAPHML);
			if (gmlN==null) throw new GraphMLException();

			int size = gmlN.getChildNodes().getLength();
			ProgressionUtil.init(progression, 0, size*4);

			extractKeys(gmlN, ProgressionUtil.sub(progression, size));
			
			Graph<?,?,?,?> graph = null;

			for(Element graphN : elements(gmlN, N_GRAPH)) {
				if (isGraphModel(graphN)) {
					//if (graph!=null) throw new GraphMLException("TOO_MANY_GRAPHS_IN_GraphML"); //$NON-NLS-1$
					graph = extractGraphModel(graphN, ProgressionUtil.sub(progression, size));
					if (graph!=null) break;
				}
			}

			extractFigures(gmlN, ProgressionUtil.sub(progression, size));

			bindModelAndViews(graph, ProgressionUtil.sub(progression, size));
			
			ProgressionUtil.end(progression);

			return graph;
		}
		finally {
			clearKeys();
		}
	}
		
	/** Create the instance of a node from the GraphML.
	 * 
	 * @param nodeNode is the GraphML node to read.
	 * @param nodes is the map of nodes to fill with the extracted data.
	 * @param anchors is the data structure to fill with the anchors of the node.
	 * @param anchorLinks is the data structure to fill with the node-anchor links.
	 * @param progression notifies on the progression of the extracted.
	 * @throws IOException
	 */
	private void extractNode(Element nodeNode,
			Map<UUID,org.arakhne.neteditor.formalism.Node<?,?,?,?>> nodes,
			Map<UUID,Anchor<?,?,?,?>> anchors,
			Map<UUID,UUID> anchorLinks,
			Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, nodeNode.getChildNodes().getLength()*2);
		org.arakhne.neteditor.formalism.Node<?,?,?,?> n = createInstance(org.arakhne.neteditor.formalism.Node.class, nodeNode);
		extractAttributes(n, nodeNode, ProgressionUtil.sub(progression, nodeNode.getChildNodes().getLength()));
		UUID id = enforceUUID(nodeNode.getAttribute(A_ID));
		n.setUUID(id);
		nodes.put(id, n);

		// Extract anchors
		Progression subTask = ProgressionUtil.sub(progression, nodeNode.getChildNodes().getLength());
		for(Element portN : elements(nodeNode, N_PORT, subTask)) {
			UUID anchorId = extractAnchor(portN, anchors, ProgressionUtil.sub(subTask, PROGRESS_STEP_SIZE_IN_ITERATOR));
			if (anchorId!=null) anchorLinks.put(anchorId, id);
		}

		ProgressionUtil.end(progression);
	}

	/** Create the instance of an anchor from the GraphML.
	 * 
	 * @param xmlNode is the GraphML node to read.
	 * @param anchors is the map of anchors to fill with the extracted data.
	 * @param progression notifies on the progression of the extracted.
	 * @return the id of the extracted anchor.
	 * @throws IOException
	 */
	private UUID extractAnchor(Element xmlNode, Map<UUID,Anchor<?,?,?,?>> anchors, Progression progression) throws IOException {
		Anchor<?,?,?,?> a = createInstance(Anchor.class, xmlNode);
		extractAttributes(a, xmlNode, progression);
		UUID id = enforceUUID(xmlNode.getAttribute(A_NAME));
		a.setUUID(id);
		anchors.put(id, a);
		return id;
	}

	/** Extract the coerced figures of a figure.
	 * 
	 * @param figureNode is the GraphML node to read.
	 * @param figureId is the id of the figure that owns the coerced figures.
	 * @param viewId is the id of the view.
	 * @throws IOException
	 */
	private void extractCoercedFigures(Element figureNode, UUID figureId, UUID viewId) throws IOException {
		ViewDescription description = getViewDescription(viewId);
		for(Element dataN : elements(figureNode, N_DATA)) {
			String dkey = dataN.getAttribute(A_KEY);
			if (dkey!=null && dkey.equals(C_KEY_NETEDITOR_COERCEDFIGURES)) {
				for(Element coercedN : elements(dataN, N_NETEDITOR_COERCEDFIGURE)) {
					UUID id = enforceUUID(coercedN.getAttribute(A_ID));
					String name = coercedN.getAttribute(A_COERCEDID);
					if (name!=null && !name.isEmpty()) {
						description.coerceFigure(figureId, id, name);
					}
				}
			}
		}
	}

	/** Extract the subfigures of a figure.
	 * 
	 * @param figureNode is the GraphML node to read.
	 * @param parent is the parent figure.
	 * @param figureId is the id of the figure that owns the subfigures.
	 * @param viewId is the id of the view.
	 * @param progression notifies on the progression of the extracted.
	 * @throws IOException
	 */
	private void extractSubFigures(Element figureNode, Figure parent, UUID figureId, UUID viewId, int position,
			Progression progression) throws IOException {
		ViewDescription viewDescription = getViewDescription(viewId);
		for(Element dataN : elements(figureNode, N_DATA, progression)) {
			String dkey = dataN.getAttribute(A_KEY);
			if (dkey!=null && dkey.equals(C_KEY_NETEDITOR_SUBFIGURES)) {
				for(Element subfigureN : elements(dataN, N_NETEDITOR_SUBFIGURE)) {
					UUID id = enforceUUID(subfigureN.getAttribute(A_ID));
					SubFigure subfigure = createFigureInstance(SubFigure.class, subfigureN, viewId);
					extractAttributes(subfigure, subfigureN, null);
					subfigure.setUUID(id);
					UUID modelId = null;
					String modelIdValue = subfigureN.getAttribute(A_MODELID);
					if (modelIdValue!=null && !modelIdValue.isEmpty()) {
						try {
							modelId = UUID.fromString(modelIdValue);
						}
						catch(Throwable _) {
							//
						}
					}
					
					viewDescription.addViewComponent(id, subfigure, position);
					if (modelId!=null) {
						viewDescription.addModelObject(id, modelId);
					}
				}
			}
		}
	}

	/** Create the instance of an edge from the GraphML.
	 * 
	 * @param edgeNode is the GraphML node to read.
	 * @param edges is the map of edges to fill with the extracted data.
	 * @param edgeLinks are the links for the edge.
	 * @param progression notifies on the progression of the extracted.
	 * @throws IOException
	 */
	private void extractEdge(Element edgeNode,
			Map<UUID,Edge<?,?,?,?>> edges,
			Map<UUID,Pair<UUID,UUID>> edgeLinks,
			Progression progression) throws IOException {
		Edge<?,?,?,?> e = createInstance(Edge.class, edgeNode);
		extractAttributes(e, edgeNode, progression);
		UUID id = enforceUUID(edgeNode.getAttribute(A_ID));
		e.setUUID(id);
		UUID fromNode = parseUUID(edgeNode.getAttribute(A_SOURCE));
		UUID toNode = parseUUID(edgeNode.getAttribute(A_TARGET));
		if (fromNode==null || toNode==null) {
			throw new GraphMLException(Locale.getString("UNSUPPORTED_XML_NODE", edgeNode.getNodeName())); //$NON-NLS-1$
		}
		UUID fromAnchor = parseUUID(edgeNode.getAttribute(A_SOURCEPORT));
		UUID toAnchor = parseUUID(edgeNode.getAttribute(A_TARGETPORT));
		if (fromAnchor==null && toAnchor!=null) {
			throw new GraphMLException(Locale.getString("MISSING_SOURCE_PORT", edgeNode.getNodeName())); //$NON-NLS-1$
		}
		if (fromAnchor!=null && toAnchor==null) {
			throw new GraphMLException(Locale.getString("MISSING_TARGET_PORT", edgeNode.getNodeName())); //$NON-NLS-1$
		}

		if (fromAnchor==null) {
			fromAnchor = fromNode;
		}

		if (toAnchor==null) {
			toAnchor = toNode;
		}

		edges.put(id, e);
		edgeLinks.put(id, new Pair<UUID,UUID>(fromAnchor,toAnchor));
	}


	/** Extract a graph model from the specified node.
	 * 
	 * @param graphNode is the node that is containing the graph model description.
	 * @param progression is the progression indicator.
	 * @return the extracted graph.
	 * @throws IOException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Graph<?,?,?,?> extractGraphModel(Element graphNode, Progression progression) throws IOException {
		NodeList children = graphNode.getChildNodes();
		ProgressionUtil.init(progression, 0, children.getLength()*4);
		Graph g = createInstance(Graph.class, graphNode);
		extractAttributes(g, graphNode, ProgressionUtil.sub(progression, children.getLength()));
		g.setUUID(enforceUUID(graphNode.getAttribute(A_ID)));

		Map<UUID,org.arakhne.neteditor.formalism.Node<?,?,?,?>> nodes = new TreeMap<UUID,org.arakhne.neteditor.formalism.Node<?,?,?,?>>();
		Map<UUID,Anchor<?,?,?,?>> anchors = new TreeMap<UUID,Anchor<?,?,?,?>>();
		Map<UUID,Edge<?,?,?,?>> edges = new TreeMap<UUID,Edge<?,?,?,?>>();
		Map<UUID,UUID> anchorLinks = new TreeMap<UUID,UUID>();
		Map<UUID,Pair<UUID,UUID>> edgeLinks = new TreeMap<UUID,Pair<UUID,UUID>>();

		Progression subTask = ProgressionUtil.sub(progression, children.getLength());
		for(int i=0; i<children.getLength(); ++i) {
			ProgressionUtil.setValue(subTask, i);
			Node node = children.item(i);
			if (node instanceof Element) {
				Element element = (Element)node;
				if (N_NODE.equals(element.getNodeName())) {
					Class<?> nodeType = extractTypeClass(element);
					if (org.arakhne.neteditor.formalism.Node.class.isAssignableFrom(nodeType)) {
						extractNode(element, nodes, anchors, anchorLinks, ProgressionUtil.sub(subTask, 1));
					}
					else {
						throw new GraphMLException(Locale.getString("UNSUPPORTED_XML_NODE", element.getNodeName())); //$NON-NLS-1$
					}
				}
				else if (N_EDGE.equals(element.getNodeName())) {
					Class<?> edgeType = extractTypeClass(element);
					if (Edge.class.isAssignableFrom(edgeType)) {
						extractEdge(element, edges, edgeLinks, ProgressionUtil.sub(subTask, 1));
					}
					else {
						throw new GraphMLException(Locale.getString("UNSUPPORTED_XML_NODE", element.getNodeName())); //$NON-NLS-1$
					}
				}
			}
		}

		// Link the model objects
		subTask = ProgressionUtil.sub(progression, children.getLength());

		for(org.arakhne.neteditor.formalism.Node node : nodes.values()) {
			g.addNode(node);
			node.removeAllAnchors(); // Be sure that the model contains only the anchors below
		}

		for(Entry<UUID,UUID> entry : anchorLinks.entrySet()) {
			UUID anchorId = entry.getKey();
			UUID nodeId = entry.getValue();
			org.arakhne.neteditor.formalism.Node node = nodes.get(nodeId);
			if (node!=null) {
				Anchor anchor = anchors.get(anchorId);
				if (anchor!=null) {
					node.addAnchor(anchor);
				}
				else {
					throw new GraphMLException(Locale.getString("ANCHOR_NOT_FOUND", anchorId)); //$NON-NLS-1$
				}
			}
			else {
				throw new GraphMLException(Locale.getString("NODE_NOT_FOUND", nodeId)); //$NON-NLS-1$
			}
			ProgressionUtil.advance(subTask);
		}

		for(Edge edge : edges.values()) {
			g.addEdge(edge);
		}

		for(Entry<UUID,Pair<UUID,UUID>> entry : edgeLinks.entrySet()) {
			UUID edgeId = entry.getKey();
			UUID startId = entry.getValue().getA();
			UUID endId = entry.getValue().getB();
			Anchor start = anchors.get(startId);
			if (start==null) {
				org.arakhne.neteditor.formalism.Node<?,?,?,?> node = nodes.get(startId);
				if (node!=null && node.hasAnchor()) {
					start = node.getAnchors().get(0);
				}
			}
			if (start!=null) {
				Anchor end = anchors.get(endId);
				if (end==null) {
					org.arakhne.neteditor.formalism.Node<?,?,?,?> node = nodes.get(endId);
					if (node!=null && node.hasAnchor()) {
						end = node.getAnchors().get(0);
					}
				}
				if (end!=null) {
					Edge edge = edges.get(edgeId);
					if (edge!=null) {
						edge.setStartAnchor(start);
						edge.setEndAnchor(end);
					}
					else {
						throw new GraphMLException(Locale.getString("EDGE_NOT_FOUND", edgeId)); //$NON-NLS-1$
					}
				}
				else {
					throw new GraphMLException(Locale.getString("ANCHOR_NOT_FOUND", endId)); //$NON-NLS-1$
				}
			}
			else {
				throw new GraphMLException(Locale.getString("ANCHOR_NOT_FOUND", startId)); //$NON-NLS-1$
			}
			ProgressionUtil.advance(subTask);
		}

		ProgressionUtil.end(progression);
		
		return g;
	}

}
