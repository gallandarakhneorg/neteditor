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

package org.arakhne.neteditor.io.gml.readers ;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.io.gml.GMLException;
import org.arakhne.vmutil.locale.Locale;
import org.w3c.dom.Element;

/** This class permits to read the
 *  <strong>graph-model</strong> into the GML format.
 *  <p>
 *  This reader supports the NetEditor/GML specification version "1".
 *  <p>
 *  Graph Modelling Language (GML) is a hierarchical ASCII-based
 *  file format for describing graphs.
 *  It has been also named Graph Meta Language. 
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://en.wikipedia.org/wiki/Graph_Modelling_Language"
 * @since 16.0
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class GMLReader1 extends AbstractGMLReader {

	/** Version of the specification supported by the GML reader.
	 */
	public static String SPECIFICATION_VERSION = "1"; //$NON-NLS-1$

	private final Map<Integer,Node> nodes = new TreeMap<Integer,Node>();
	private final Map<Integer,Anchor> anchors = new TreeMap<Integer,Anchor>();
	private Map<UUID, List<ViewComponent>> allFigures = null;

	/** Construct a new GraphWriter.          
	 */
	GMLReader1() {    
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public <G extends Graph<?, ?, ?, ?>> G readGraph(
			Class<G> type,
			Element gmlRoot,
			Map<UUID, List<ViewComponent>> figures,
			Progression progression) throws IOException {
		try {
			ProgressionUtil.init(progression, 0, gmlRoot.getChildNodes().getLength()*2);
			assertSpecificationVersion(gmlRoot, SPECIFICATION_VERSION, getResourceRepository());
			
			this.allFigures = figures;
			this.anchors.clear();
			this.nodes.clear();

			Graph<?, ?, ?, ?> g = parseGraph(gmlRoot, ProgressionUtil.sub(progression, gmlRoot.getChildNodes().getLength()));

			ProgressionUtil.ensureNoSubTask(progression);
			
			if (g!=null && !(type.isInstance(g)))
				throw new IOException(Locale.getString(GMLReader1.class, "INVALID_GRAPH_TYPE", type.getCanonicalName())); //$NON-NLS-1$

			parseOtherViews(gmlRoot, ProgressionUtil.subToEnd(progression));

			ProgressionUtil.end(progression);

			return type.cast(g);
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			throw new IOException(e);
		}
		finally {
			this.allFigures = null;
			this.anchors.clear();
			this.nodes.clear();
		}
	}

	private void registerFigure(UUID view, Figure component) {
		if (this.allFigures!=null) {
			List<ViewComponent> components = this.allFigures.get(view);
			if (components==null) {
				components = new ArrayList<ViewComponent>();
				this.allFigures.put(view, components);
			}
			components.add(component);
		}
	}

	private void parseOtherViews(Element root, Progression progression) throws IOException {
		for(Element graphicsN : elements(root, tag(K_GRAPHICS), progression)) {
			for(Element figureN : elements(graphicsN, tag(K_FIGURE),
					ProgressionUtil.sub(progression, 1))) {
				UUID viewId = enforceUUID(extractValueFromTag(figureN, K_VIEWID, String.class, getResourceRepository()));
				ViewComponent figure = createFigureInstance(ViewComponent.class, figureN, viewId);
				if (figure instanceof Figure) {
					Map<String,Object> properties = extractAttributes(figureN);
					figure.setProperties(properties);
					figure.setUUID(enforceUUID(extractValueFromTag(figureN, K_UUID, String.class, getResourceRepository())));
					figure.setViewUUID(viewId);

					registerFigure(viewId, (Figure)figure);

					for(Element coercedFigureN : elements(figureN, tag(K_FIGURE))) {
						String coerceId = extractValueFromTag(coercedFigureN, K_COERCIONID, String.class, getResourceRepository());
						CoercedFigure coercedFigure = createFigureInstance(CoercedFigure.class, coercedFigureN, viewId);
						properties = extractAttributes(coercedFigureN);
						coercedFigure.setProperties(properties);
						coercedFigure.setUUID(enforceUUID(extractValueFromTag(coercedFigureN, K_UUID, String.class, getResourceRepository())));
						coercedFigure.setViewUUID(viewId);
						registerFigure(viewId, coercedFigure);

						figure.addAssociatedFigureIntoView(coerceId, coercedFigure);					
					}
				}
			}
		}
	}

	private void parseObjectFigure(ModelObject object, Element graphicsNode)  throws IOException {
		for(Element figureN : elements(graphicsNode, tag(K_FIGURE))) {
			UUID viewId = enforceUUID(extractValueFromTag(figureN, K_VIEWID, String.class, getResourceRepository()));
			ViewComponent figure = createFigureInstance(ViewComponent.class, figureN, viewId);
			Map<String,Object> properties = extractAttributes(figureN);
			figure.setProperties(properties);
			figure.setUUID(enforceUUID(extractValueFromTag(figureN, K_UUID, String.class, getResourceRepository())));
			figure.setViewUUID(viewId);

			if (figure instanceof ModelObjectFigure<?>) {
				((ModelObjectFigure)figure).setModelObject(object);
			}

			if (figure instanceof Figure) {
				registerFigure(viewId, (Figure)figure);
			}

			for(Element coercedFigureN : elements(figureN, tag(K_FIGURE))) {
				String coerceId = extractValueFromTag(coercedFigureN, K_COERCIONID, String.class, getResourceRepository());
				CoercedFigure coercedFigure = createFigureInstance(CoercedFigure.class, coercedFigureN, viewId);
				properties = extractAttributes(coercedFigureN);
				coercedFigure.setProperties(properties);
				coercedFigure.setUUID(enforceUUID(extractValueFromTag(coercedFigureN, K_UUID, String.class, getResourceRepository())));
				coercedFigure.setViewUUID(viewId);
				registerFigure(viewId, coercedFigure);

				figure.addAssociatedFigureIntoView(coerceId, coercedFigure);
			}
		}
	}

	private Graph<?,?,?,?> parseGraph(Element root, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, 15000);
		Element graphN = extractNode(root, tag(K_GRAPH));
		Graph graph = createInstance(Graph.class, graphN);
		Map<String,Object> properties = extractAttributes(graphN);
		graph.setProperties(properties);
		graph.setUUID(enforceUUID(extractValueFromTag(graphN, K_UUID, String.class, getResourceRepository())));

		ProgressionUtil.advance(progression, 5000);

		for(Element nodeN : elements(graphN, tag(K_NODE),
				ProgressionUtil.sub(progression, 5000))) {
			parseNode(graph, nodeN);
		}

		ProgressionUtil.ensureNoSubTask(progression);

		for(Element edgeN : elements(graphN, tag(K_EDGE),
				ProgressionUtil.subToEnd(progression))) {
			parseEdge(graph, edgeN);
		}

		ProgressionUtil.end(progression);

		return graph;
	}

	private void parseNode(Graph graph, Element root) throws IOException {
		int id = extractValueFromTag(root, K_ID, Number.class, getResourceRepository()).intValue();
		Node node = createInstance(Node.class, root);
		Map<String,Object> properties = extractAttributes(root);
		node.setProperties(properties);
		node.setUUID(enforceUUID(extractValueFromTag(root, K_UUID, String.class, getResourceRepository())));

		graph.addNode(node);

		parseAnchorsFor(node, root);

		this.nodes.put(id, node);

		for(Element graphicN : elements(root, tag(K_GRAPHICS))) {
			parseObjectFigure(node, graphicN);
		}
	}

	private void parseAnchorsFor(Node node, Element nodeElement) throws IOException {
		for(Element anchorN : elements(nodeElement, tag(K_EDGEANCHOR))) {
			int id = extractValueFromTag(anchorN, K_ID, Number.class, getResourceRepository()).intValue();
			Anchor anchor = createInstance(Anchor.class, anchorN);
			Map<String,Object> properties = extractAttributes(anchorN);
			anchor.setProperties(properties);
			anchor.setUUID(enforceUUID(extractValueFromTag(anchorN, K_UUID, String.class, getResourceRepository())));
			node.addAnchor(anchor);
			this.anchors.put(id, anchor);

			for(Element graphicN : elements(anchorN, tag(K_GRAPHICS))) {
				parseObjectFigure(anchor, graphicN);
			}
		}
	}

	private void parseEdge(Graph graph, Element root) throws IOException {
		Edge edge = createInstance(Edge.class, root);
		Map<String,Object> properties = extractAttributes(root);
		edge.setProperties(properties);
		edge.setUUID(enforceUUID(extractValueFromTag(root, K_UUID, String.class, getResourceRepository())));

		graph.addEdge(edge);

		Element port;

		port = extractNode(root, tag(K_SOURCEPORT));
		if (port!=null) {
			int portId = extractValueFromTag(port, Number.class, getResourceRepository()).intValue();
			Anchor anchor = this.anchors.get(portId);
			if (anchor==null) throw new GMLException();
			edge.setStartAnchor(anchor);
		}
		else {
			int nodeId = extractValueFromTag(root, K_SOURCE, Number.class, getResourceRepository()).intValue();
			Node node = this.nodes.get(nodeId);
			if (node==null) throw new GMLException();
			Anchor anchor = (Anchor)node.getAnchors().get(0);
			edge.setStartAnchor(anchor);
		}

		port = extractNode(root, tag(K_TARGETPORT));
		if (port!=null) {
			int portId = extractValueFromTag(port, Number.class, getResourceRepository()).intValue();
			Anchor anchor = this.anchors.get(portId);
			if (anchor==null) throw new GMLException();
			edge.setEndAnchor(anchor);
		}
		else {
			int nodeId = extractValueFromTag(root, K_TARGET, Number.class, getResourceRepository()).intValue();
			Node node = this.nodes.get(nodeId);
			if (node==null) throw new GMLException();
			Anchor anchor = (Anchor)node.getAnchors().get(0);
			edge.setEndAnchor(anchor);
		}

		for(Element graphicN : elements(root, tag(K_GRAPHICS))) {
			parseObjectFigure(edge, graphicN);
		}
	}

	private Map<String,Object> extractAttributes(Element node) throws IOException {
		Map<String,Object> properties = new TreeMap<String,Object>();
		Element attrN = extractNodeNoFail(node, tag(K_ATTRIBUTES));
		if (attrN!=null) {
			for(Element elementN : elements(attrN)) {
				Object value = extractValueFromTag(elementN, Object.class, getResourceRepository());
				if (value!=null) {
					if (value instanceof Namespace) {
						((Namespace)value).fillProperties("", properties); //$NON-NLS-1$
					}
					else {
						properties.put(untag(elementN.getNodeName()), value);
					}
				}
			}
		}		
		return properties;
	}

	@Override
	protected String extractType(Element node) throws IOException {
		Element typeN = extractNode(node, tag(K_TYPE));
		if (typeN!=null) {
			String value = typeN.getAttribute(K_VALUE);
			if (value!=null) {
				value = value.trim();
				if (value.startsWith(SCHEMA_URL+"#")) { //$NON-NLS-1$
					return value.substring(SCHEMA_URL.length()+1);
				}
			}
		}
		throw new GMLException(Locale.getString("UNSUPPORTED_XML_NODE", node.getNodeName())); //$NON-NLS-1$
	}

}
