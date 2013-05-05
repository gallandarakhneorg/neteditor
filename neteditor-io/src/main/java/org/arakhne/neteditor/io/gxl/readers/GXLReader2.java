/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.gxl.readers ;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.afc.util.Pair;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.gxl.GXLException;
import org.arakhne.vmutil.locale.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This class permits to read a
 *  <strong>graph-model</strong> from the GXL format.
 *  <p>
 *  This reader supports the NetEditor/GXL specification version "2".
 *  <p>
 *  GXL (Graph eXchange Language) is designed to be a standard exchange
 *  format for graphs. GXL is an XML sublanguage and the syntax is 
 *  given by a XML DTD (Document Type Definition). This exchange format 
 *  offers an adaptable and flexible means to support interoperability 
 *  between graph-based tools.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://www.gupro.de/GXL/"
 */
public class GXLReader2 extends AbstractGXLReader {

	/** Version of the specification supported by the GXL reader.
	 */
	public static String SPECIFICATION_VERSION = "2"; //$NON-NLS-1$

	/**
	 */
	public GXLReader2() {
		//
	}
	
	/** {@inheritDoc}
	 */
	@Override
	protected Graph<?,?,?,?> extractGraph(Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, 60000);
		Element gxlN = extractNode(document(), N_GXL);
		if (gxlN==null) throw new GXLException();
		Graph<?,?,?,?> graph = null;

		for(Element graphN : elements(gxlN, N_GRAPH)) {
			if (isGraphModel(graphN)) {
				if (graph!=null) throw new GXLException("TOO_MANY_GRAPHS_IN_GXL"); //$NON-NLS-1$
				graph = extractGraphModel(graphN, ProgressionUtil.sub(progression, 30000));
			}
			else if (isFigureModel(graphN)) {
				extractFigures(graphN, ProgressionUtil.sub(progression, 20000));
			}
		}
		
		bindModelAndViews(graph, ProgressionUtil.sub(progression, 10000));

		ProgressionUtil.end(progression);
		
		return graph;
	}

	/** Extract a figure and put it and its binding in the specified map.
	 *  
	 * @param node
	 * @param progression notifies on the progression of the reading.
	 * @throws IOException
	 */
	private void extractFigures(Element node, Progression progression) throws IOException {
		int childCount = node.getChildNodes().getLength();
		ProgressionUtil.init(progression, 0, childCount*2);
		UUID viewID = enforceUUID(node.getAttribute(A_ID));

		int i=0; 
		for(Element element : elements(node, N_NODE,
				ProgressionUtil.sub(progression, childCount))) {
			ViewComponent component = createFigureInstance(ViewComponent.class, element, viewID);
			extractAttributes(component, element, null);
			component.setUUID(enforceUUID(element.getAttribute(A_ID)));
			getViewDescription(viewID).addViewComponent(component.getUUID(), component, i);
			if (component instanceof Figure) ++i;
		}

		for(Element element : elements(node, N_REL,
				ProgressionUtil.sub(progression, childCount))) {
			String internalType = extractInternalType(element);

			if (C_INTERNAL_COERCION_RELATION_TYPE.equals(internalType)) {
				String name = extractAttribute(String.class, element, "name", null); //$NON-NLS-1$
				if (name==null || name.isEmpty()) throw new GXLException();
				UUID coercedFigureId = null;
				UUID coerciveFigureId = null;
				Iterator<Element> elementIterator = elementIterator(element, N_RELEND);
				while ((coercedFigureId==null || coerciveFigureId==null) && elementIterator.hasNext()) {
					Element relEndN = elementIterator.next();
					if (C_GXL_REL_IN.equals(relEndN.getAttribute(A_DIRECTION))) {
						coerciveFigureId = parseUUID(relEndN.getAttribute(A_IDREF));
					}
					else if (C_GXL_REL_OUT.equals(relEndN.getAttribute(A_DIRECTION))) {
						coercedFigureId = parseUUID(relEndN.getAttribute(A_IDREF));
					}
				}
				if (coercedFigureId!=null && coerciveFigureId!=null) {
					getViewDescription(viewID).coerceFigure(coerciveFigureId, coercedFigureId, name);
				}
			}
			else if (C_INTERNAL_VIEW_RELATION_TYPE.equals(internalType)) {
				UUID figureId = null;
				UUID modelObjectId = null;
				Iterator<Element> elementIterator = elementIterator(element, N_RELEND);
				while ((figureId==null || modelObjectId==null) && elementIterator.hasNext()) {
					Element relEndN = elementIterator.next();
					if (C_GXL_REL_IN.equals(relEndN.getAttribute(A_DIRECTION))) {
						figureId = parseUUID(relEndN.getAttribute(A_IDREF));
					}
					else if (C_GXL_REL_OUT.equals(relEndN.getAttribute(A_DIRECTION))) {
						modelObjectId = parseUUID(relEndN.getAttribute(A_IDREF));
					}
				}
				if (figureId!=null && modelObjectId!=null) {
					getViewDescription(viewID).addModelObject(figureId, modelObjectId);
				}
			}
		}
		ProgressionUtil.end(progression);
	}

	/** Extract a graph model from the specified node.
	 * 
	 * @param graphNode is the node that is containing the graph model description.
	 * @param progression notifies on the progression of the reading.
	 * @return the extracted graph.
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Graph<?,?,?,?> extractGraphModel(Element graphNode, Progression progression) throws IOException {
		NodeList children = graphNode.getChildNodes();
		ProgressionUtil.init(progression, 0, children.getLength()*PROGRESS_STEP_SIZE_IN_ITERATOR+104);

		Graph g = createInstance(Graph.class, graphNode);
		extractAttributes(g, graphNode, ProgressionUtil.sub(progression, 100));
		g.setUUID(enforceUUID(graphNode.getAttribute(A_ID)));

		Map<UUID,org.arakhne.neteditor.formalism.Node<?,?,?,?>> nodes = new TreeMap<UUID,org.arakhne.neteditor.formalism.Node<?,?,?,?>>();
		Map<UUID,Anchor<?,?,?,?>> anchors = new TreeMap<UUID,Anchor<?,?,?,?>>();
		Map<UUID,Edge<?,?,?,?>> edges = new TreeMap<UUID,Edge<?,?,?,?>>();
		Map<UUID,UUID> anchorLinks = new TreeMap<UUID,UUID>();
		Map<UUID,Pair<UUID,UUID>> edgeLinks = new TreeMap<UUID,Pair<UUID,UUID>>();

		ProgressionUtil.ensureNoSubTask(progression);
		
		for(int i=0; i<children.getLength(); ++i) {
			ProgressionUtil.setValue(progression, i*PROGRESS_STEP_SIZE_IN_ITERATOR);
			Node node = children.item(i);
			if (node instanceof Element) {
				Element element = (Element)node;
				if (N_NODE.equals(element.getNodeName())) {
					Class<?> nodeType = extractTypeClass(element);
					if (org.arakhne.neteditor.formalism.Node.class.isAssignableFrom(nodeType)) {
						extractNode(nodes, element, ProgressionUtil.sub(progression, PROGRESS_STEP_SIZE_IN_ITERATOR));
					}
					else if (Anchor.class.isAssignableFrom(nodeType)) {
						extractAnchor(anchors, element, ProgressionUtil.sub(progression, PROGRESS_STEP_SIZE_IN_ITERATOR));
					}
					else {
						throw new GXLException(Locale.getString("UNSUPPORTED_XML_NODE", element.getNodeName())); //$NON-NLS-1$
					}
				}
				else if (N_EDGE.equals(element.getNodeName())) {
					Class<?> edgeType = extractTypeClass(element);
					if (Edge.class.isAssignableFrom(edgeType)) {
						extractEdge(edges, edgeLinks, element, ProgressionUtil.sub(progression, PROGRESS_STEP_SIZE_IN_ITERATOR));
					}
					else {
						throw new GXLException(Locale.getString("UNSUPPORTED_XML_NODE", element.getNodeName())); //$NON-NLS-1$
					}
				}
				else if (N_REL.equals(element.getNodeName())) {
					String type = extractInternalType(element);
					if (C_INTERNAL_NODE_ANCHOR_RELATION_TYPE.equals(type)) {
						UUID nodeId = null;
						UUID anchorId = null;
						Iterator<Element> elementIterator = elementIterator(element, N_RELEND);
						while ((nodeId==null || anchorId==null) && elementIterator.hasNext()) {
							Element elt = elementIterator.next();
							if (C_GXL_REL_IN.equals(elt.getAttribute(A_DIRECTION))) {
								nodeId = parseUUID(elt.getAttribute(A_IDREF));
							}
							else if (C_GXL_REL_OUT.equals(elt.getAttribute(A_DIRECTION))) {
								anchorId = parseUUID(elt.getAttribute(A_IDREF));
							}
						}
						if (nodeId!=null && anchorId!=null) {
							anchorLinks.put(anchorId, nodeId);
						}
						else {
							throw new GXLException(Locale.getString("UNSUPPORTED_XML_NODE", element.getNodeName())); //$NON-NLS-1$
						}
					}
					else {
						throw new GXLException(Locale.getString("UNSUPPORTED_XML_NODE", element.getNodeName())); //$NON-NLS-1$
					}
				}
			}
		}

		// Link the model objects

		for(org.arakhne.neteditor.formalism.Node node : nodes.values()) {
			g.addNode(node);
			node.removeAllAnchors(); // Be sure that the model contains only the anchors below
		}

		ProgressionUtil.advance(progression);

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
					throw new GXLException(Locale.getString("ANCHOR_NOT_FOUND", anchorId)); //$NON-NLS-1$
				}
			}
			else {
				throw new GXLException(Locale.getString("NODE_NOT_FOUND", nodeId)); //$NON-NLS-1$
			}
		}

		ProgressionUtil.advance(progression);

		for(Edge edge : edges.values()) {
			g.addEdge(edge);
		}

		ProgressionUtil.advance(progression);

		for(Entry<UUID,Pair<UUID,UUID>> entry : edgeLinks.entrySet()) {
			UUID edgeId = entry.getKey();
			UUID startId = entry.getValue().getA();
			UUID endId = entry.getValue().getB();
			Anchor start = anchors.get(startId);
			if (start!=null) {
				Anchor end = anchors.get(endId);
				if (end!=null) {
					Edge edge = edges.get(edgeId);
					if (edge!=null) {
						edge.setStartAnchor(start);
						edge.setEndAnchor(end);
					}
					else {
						throw new GXLException(Locale.getString("EDGE_NOT_FOUND", edgeId)); //$NON-NLS-1$
					}
				}
				else {
					throw new GXLException(Locale.getString("ANCHOR_NOT_FOUND", endId)); //$NON-NLS-1$
				}
			}
			else {
				throw new GXLException(Locale.getString("ANCHOR_NOT_FOUND", startId)); //$NON-NLS-1$
			}
		}

		ProgressionUtil.end(progression);

		return g;
	}

	/** Create the instance of a node from the GXL.
	 * 
	 * @param nodes is the map of nodes to fill with the extracted data.
	 * @param xmlNode is the GXL node to read.
	 * @param progression notifies on the progression of the reading.
	 * @throws IOException
	 */
	private void extractNode(Map<UUID,org.arakhne.neteditor.formalism.Node<?,?,?,?>> nodes, Element xmlNode, Progression progression) throws IOException {
		org.arakhne.neteditor.formalism.Node<?,?,?,?> n = createInstance(org.arakhne.neteditor.formalism.Node.class, xmlNode);
		extractAttributes(n, xmlNode, progression);
		UUID id = enforceUUID(xmlNode.getAttribute(A_ID));
		n.setUUID(id);
		nodes.put(id, n);
	}

	/** Create the instance of an anchor from the GXL.
	 * 
	 * @param anchors is the map of anchors to fill with the extracted data.
	 * @param xmlNode is the GXL node to read.
	 * @param progression notifies on the progression of the reading.
	 * @throws IOException
	 */
	private void extractAnchor(Map<UUID,Anchor<?,?,?,?>> anchors, Element xmlNode, Progression progression) throws IOException {
		Anchor<?,?,?,?> a = createInstance(Anchor.class, xmlNode);
		extractAttributes(a, xmlNode, progression);
		UUID id = enforceUUID(xmlNode.getAttribute(A_ID));
		a.setUUID(id);
		anchors.put(id, a);
	}

	/** Create the instance of an edge from the GXL.
	 * 
	 * @param edges is the map of edges to fill with the extracted data.
	 * @param edgeLinks are the links for the edge.
	 * @param xmlNode is the GXL node to read.
	 * @param progression notifies on the progression of the reading.
	 * @throws IOException
	 */
	private void extractEdge(Map<UUID,Edge<?,?,?,?>> edges, Map<UUID,Pair<UUID,UUID>> edgeLinks, Element xmlNode, Progression progression) throws IOException {
		Edge<?,?,?,?> e = createInstance(Edge.class, xmlNode);
		extractAttributes(e, xmlNode, progression);
		UUID id = enforceUUID(xmlNode.getAttribute(A_ID));
		e.setUUID(id);
		UUID from = parseUUID(xmlNode.getAttribute(A_FROM));
		UUID to = parseUUID(xmlNode.getAttribute(A_TO));
		if (from==null || to==null) {
			throw new GXLException(Locale.getString("UNSUPPORTED_XML_NODE", xmlNode.getNodeName())); //$NON-NLS-1$
		}
		edges.put(id, e);
		edgeLinks.put(id, new Pair<UUID,UUID>(from,to));
	}

}
