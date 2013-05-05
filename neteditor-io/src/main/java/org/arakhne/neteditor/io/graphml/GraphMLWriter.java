/* 
 * $Id$
 * 
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

package org.arakhne.neteditor.io.graphml ;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ModelObjectView;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.View;
import org.arakhne.neteditor.formalism.ViewBinding;
import org.arakhne.neteditor.io.svg.SvgExporter;
import org.arakhne.neteditor.io.xml.AbstractXMLWriter;
import org.arakhne.vmutil.Resources;
import org.w3c.dom.Element;

/** This class permits to export the
 *  <strong>graph-model</strong> into the GraphML format.
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
public class GraphMLWriter extends AbstractXMLWriter implements GraphMLConstants {

	/** Version of the specification supported by the GraphML writer.
	 */
	public static String SPECIFICATION_VERSION = "2"; //$NON-NLS-1$

	private final Map<UUID,List<ViewComponent>> viewObjects = new TreeMap<UUID,List<ViewComponent>>();
	private final Map<String,Element> additionalKeys = new TreeMap<String,Element>();
	private final Map<Figure,Integer> outputFigures = new TreeMap<Figure,Integer>();
	private boolean isSvgDrawings = false;
	
	/** Construct a new GraphWriter.          
	 */
	public GraphMLWriter() {    
		//
	}
	
	/** Replies if this writer is also writing SVG drawing of the nodes and the edges.
	 * 
	 * @return <code>true</code> if the SVG drawing are written; <code>false</code>
	 * otherwise.
	 */
	public boolean isWriteSVGDrawings() {
		return this.isSvgDrawings;
	}

	/** Set if this writer is also writing SVG drawing of the nodes and the edges.
	 * 
	 * @param drawSvg is <code>true</code> if the SVG drawing are written; <code>false</code>
	 * otherwise.
	 */
	public void setWriteSVGDrawings(boolean drawSvg) {
		this.isSvgDrawings = drawSvg;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected URL getSystemDTD() {
		return Resources.getResource(GraphMLWriter.class, C_GRAPHML_DTD_FILENAME);
	}
	
	@Override
	protected URL getPublicDTD() {
		try {
			return new URL(C_GRAPHML_DTD_URL);
		}
		catch (MalformedURLException e) {
			return null;
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	protected Element createGraphDOM(Map<UUID, ? extends Graph<?,?,?,?>> graphs,  Collection<? extends Figure> figures, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, figures.size()*2+graphs.size()+50);
		
		this.viewObjects.clear();
		this.additionalKeys.clear();
		this.outputFigures.clear();
		
		// Dispatch the figures
		extractNoModelObjectViews(figures, this.viewObjects, this.outputFigures,
				ProgressionUtil.sub(progression, figures.size()));
		
		try {
			Element gmlN = createElement(N_GRAPHML);
			gmlN.setAttribute(A_XMLNS, C_XMLNS_URI);
			gmlN.setAttribute(A_XMLNS_XLINK, C_XLINK_NS_URI);
			gmlN.setAttribute(A_XMLNS_NETEDITOR, C_XMLNS_NETEDITOR_URI);
			gmlN.setAttribute(A_GRAPHML_SPECIFICATION_VERSION, SPECIFICATION_VERSION);
			
			if (isWriteSVGDrawings()) {
				gmlN.setAttribute(A_XMLNS_XSI, C_XMLNS_XSI_URI);
				gmlN.setAttribute(A_XSI_SCHEMALOCATION, C_XSI_SCHEMALOCATION_URI);
				gmlN.setAttribute(A_XMLNS_SVG, C_XMLNS_SVG_URI);
			}

			Element refKeyN = createDefaultKeys(gmlN);

			// Put the raw description of the graphs
			for(Graph<?,?,?,?> graph : graphs.values()) {
				Element graphN = createGraph(graph, ProgressionUtil.sub(progression, 1));
				append(gmlN, graphN);
			}

			// Create the graph representations of the views
			if (!this.outputFigures.isEmpty()) {
				createFigures(gmlN, ProgressionUtil.sub(progression, figures.size()));
			}
			
			addAdditionalKeys(gmlN, refKeyN);

			ProgressionUtil.end(progression);

			return gmlN;
		}
		finally {
			this.outputFigures.clear();
			this.viewObjects.clear();
			this.additionalKeys.clear();
		}
	}
	
	/** Put the additional keys just before the given key node.
	 * 
	 * @param gmlN
	 * @param refKeyN
	 */
	private void addAdditionalKeys(Element gmlN, Element refKeyN) {
		for(Element keyN : this.additionalKeys.values()) {
			gmlN.insertBefore(keyN, refKeyN);
		}
	}

	/** Generate the keys of the GraphML.
	 * 
	 * @param graphNode is the node of the graphml.
	 * @return the first key
	 */
	private Element createDefaultKeys(Element graphNode) {
		Element keyN = createElement(N_KEY);
		Element firstKeyN = keyN;
		keyN.setAttribute(A_ID, C_KEY_NETEDITOR_TYPE);
		keyN.setAttribute(A_FOR, C_FOR_ALL);
		append(graphNode, keyN);

		keyN = createElement(N_KEY);
		keyN.setAttribute(A_ID, C_KEY_NETEDITOR_ATTRIBUTES);
		keyN.setAttribute(A_FOR, C_FOR_ALL);
		append(graphNode, keyN);

		if (!this.outputFigures.isEmpty()) {
			keyN = createElement(N_KEY);
			keyN.setAttribute(A_ID, C_KEY_NETEDITOR_VIEWS);
			keyN.setAttribute(A_FOR, C_FOR_ALL);
			append(graphNode, keyN);

			keyN = createElement(N_KEY);
			keyN.setAttribute(A_ID, C_KEY_NETEDITOR_MODELID);
			keyN.setAttribute(A_FOR, C_FOR_ALL);
			append(graphNode, keyN);

			keyN = createElement(N_KEY);
			keyN.setAttribute(A_ID, C_KEY_NETEDITOR_SUBFIGURES);
			keyN.setAttribute(A_FOR, C_FOR_ALL);
			append(graphNode, keyN);

			keyN = createElement(N_KEY);
			keyN.setAttribute(A_ID, C_KEY_NETEDITOR_COERCEDFIGURES);
			keyN.setAttribute(A_FOR, C_FOR_ALL);
			append(graphNode, keyN);
		}
		
		if (isWriteSVGDrawings()) {
			keyN = createElement(N_KEY);
			keyN.setAttribute(A_ID, C_KEY_SVG_NODE);
			keyN.setAttribute(A_FOR, C_FOR_NODE);
			append(graphNode, keyN);

			keyN = createElement(N_KEY);
			keyN.setAttribute(A_ID, C_KEY_SVG_EDGE);
			keyN.setAttribute(A_FOR, C_FOR_EDGE);
			append(graphNode, keyN);
		}
		
		return firstKeyN;
	}

	/** Create the decorations.
	 * 
	 * @param gmlN is the XML node that contains GraphML.
	 * @param progression notifies on the progression of the creation.
	 * @throws IOException
	 */
	private void createFigures(Element gmlN, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, this.viewObjects.size());
		Element viewsN = createElement(N_DATA);
		for(Entry<UUID,List<ViewComponent>> ventry : this.viewObjects.entrySet()) {
			Element viewN = createElement(N_NETEDITOR_VIEW);
			for(ViewComponent component : ventry.getValue()) {
				if (!(component instanceof SubFigure)) {

					Element componentN = createElement(N_NETEDITOR_VIEWCOMPONENT);
					setType(componentN, component);
					
					if (component instanceof ModelObjectView<?>) {
						ModelObjectView<?> mov = (ModelObjectView<?>) component;
						ModelObject mo = mov.getModelObject();
						if (mo!=null) {
							Element modelObjectN = createElement(N_DATA);
							modelObjectN.setAttribute(A_KEY, C_KEY_NETEDITOR_MODELID);
							modelObjectN.appendChild(createTextNode(mo.getUUID().toString()));
							append(componentN, modelObjectN);
						}
					}

					setAttributes(A_ID, componentN, component);

					if (component instanceof Figure) {
						Figure figure = (Figure)component;
						createSubFigures(
								componentN,
								component,
								figure.getSubFigures());
					}

					Map<String,CoercedFigure> coercedFigures = component.getAssociatedFiguresInView();
					if (!coercedFigures.isEmpty()) {
						Element coercedFiguresN = createElement(N_DATA);
						coercedFiguresN.setAttribute(A_KEY, C_KEY_NETEDITOR_COERCEDFIGURES);
						for(Entry<String,CoercedFigure> entry : coercedFigures.entrySet()) {
							CoercedFigure coercedFigure = entry.getValue();
							if (coercedFigure!=null) {
								Element coercedFigureN = createElement(N_NETEDITOR_COERCEDFIGURE);
								coercedFigureN.setAttribute(A_COERCEDID, entry.getKey());
								coercedFigureN.setAttribute(A_ID, coercedFigure.getUUID().toString());
								append(coercedFiguresN, coercedFigureN);
							}
						}
						append(componentN, coercedFiguresN);
					}
					
					append(viewN,componentN);
				}
			}
			if (!isEmpty(viewN)) {
				viewN.setAttribute(A_ID, ventry.getKey().toString());
				viewsN.appendChild(viewN);
			}
			ProgressionUtil.advance(progression);
		}
		if (!isEmpty(viewsN)) {
			viewsN.setAttribute(A_KEY, C_KEY_NETEDITOR_VIEWS);
			gmlN.appendChild(viewsN);
		}
		ProgressionUtil.end(progression);
	}

	/** Create the views for the subfigures.
	 * 
	 * @param elementN is the XML node that contains GraphML to set.
	 * @param parent is the figure that owns the coerced figures.
	 * @param subfigures are the subfigures to output.
	 * @throws IOException
	 */
	private void createSubFigures(Element elementN, ViewComponent parent, Iterable<? extends SubFigure> subfigures) throws IOException {
		Element dataN = createElement(N_DATA);
		Iterator<? extends SubFigure> iterator = subfigures.iterator();
		while (iterator.hasNext()) {
			SubFigure subfigure = iterator.next();
			Element subfigureN = createElement(N_NETEDITOR_SUBFIGURE);
			setType(subfigureN, subfigure);
			setAttributes(A_ID, subfigureN, subfigure);
			if (subfigure instanceof ModelObjectView<?>) {
				ModelObjectView<?> mov = (ModelObjectView<?>)subfigure;
				ModelObject mo = mov.getModelObject();
				if (mo!=null) {
					UUID moid = mo.getUUID();
					if (moid!=null) {
						subfigureN.setAttribute(A_MODELID, moid.toString());
					}
				}
			}
			append(dataN, subfigureN);
		}
		if (!isEmpty(dataN)) {
			dataN.setAttribute(A_KEY, C_KEY_NETEDITOR_SUBFIGURES);
			elementN.appendChild(dataN);
		}
	}

	/** Generate the XML node for the graph.
	 * 
	 * @param graph is the graph to put inside.
	 * @param progression notifies on the progression of the creation.
	 * @return the XML node of the graph. 
	 * @throws IOException
	 */
	private Element createGraph(Graph<?,?,?,?> graph, Progression progression) throws IOException {
		if (graph==null) return null;
		ProgressionUtil.init(progression, 0, 10+graph.getNodeCount()+graph.getEdgeCount());
		extractViews(graph, this.viewObjects, this.outputFigures,
				ProgressionUtil.sub(progression, 10));
		Element graphN = createElement(N_GRAPH);
		setType(graphN, graph);
		setAttributes(A_ID, graphN, graph);
		graphN.setAttribute(A_EDGEDEFAULT, C_DIRECTED);
		ProgressionUtil.ensureNoSubTask(progression);

		for(org.arakhne.neteditor.formalism.Node<?,?,?,?> node : graph.getNodes()) {
			createNode(graphN, node, ProgressionUtil.sub(progression, 1));
		}
		ProgressionUtil.ensureNoSubTask(progression);

		for(Edge<?,?,?,?> edge : graph.getEdges()) {
			createEdge(graphN, edge, ProgressionUtil.sub(progression, 1));
		}
		
		ProgressionUtil.end(progression);

		return graphN;
	}

	/** Generate the XML node for the graph edge.
	 * 
	 * @param graphNode is the XML node that is corresponding to the graph model.
	 * @param edge is the graph edge to put inside.
	 * @param progression notifies on the progression of the creation.
	 * @throws IOException
	 */
	private void createEdge(Element graphNode, Edge<?,?,?,?> edge, Progression progression) throws IOException {
		if (edge==null) return;
		extractViews(edge, this.viewObjects, this.outputFigures, progression);
		Element edgeN = createElement(N_EDGE);
		setType(edgeN, edge);
		setAttributes(A_ID, edgeN, edge);

		edgeN.setAttribute(A_SOURCE, edge.getStartAnchor().getNode().getUUID().toString());
		edgeN.setAttribute(A_TARGET, edge.getEndAnchor().getNode().getUUID().toString());

		if (isAnchorOutput()) {
			edgeN.setAttribute(A_SOURCEPORT, edge.getStartAnchor().getUUID().toString());
			edgeN.setAttribute(A_TARGETPORT, edge.getEndAnchor().getUUID().toString());
		}

		if (isWriteSVGDrawings()) {
			Element svgDescription = createSvgDescription(edge.getViewBinding());
			if (!isEmpty(svgDescription)) {
				Element svgN = createElement(N_DATA);
				svgN.setAttribute(A_KEY, C_KEY_SVG_EDGE);
				svgN.appendChild(svgDescription);
				edgeN.appendChild(svgN);
			}
		}

		append(graphNode, edgeN);
	}

	/** Generate the XML node for the graph node.
	 * 
	 * @param graphNode is the XML node that is corresponding to the graph model.
	 * @param node is the graph node to put inside.
	 * @param progression notifies on the progression of the creation.
	 * @throws IOException
	 */
	private void createNode(Element graphNode, org.arakhne.neteditor.formalism.Node<?,?,?,?> node, Progression progression) throws IOException {
		if (node==null) return;
		ProgressionUtil.init(progression, 0, node.getAnchors().size()+10);
		extractViews(node, this.viewObjects, this.outputFigures,
				ProgressionUtil.sub(progression, 10));
		Element nodeN = createElement(N_NODE);
		setType(nodeN, node);
		setAttributes(A_ID, nodeN, node);
		append(graphNode, nodeN);

		ProgressionUtil.ensureNoSubTask(progression);

		if (isAnchorOutput()) {
			int n = ProgressionUtil.getValue(progression);
			for(Anchor<?,?,?,?> anchor : node.getAnchors()) {
				createAnchor(nodeN, anchor, ProgressionUtil.sub(progression, 1));
				ProgressionUtil.setValue(progression, ++n);
			}
		}

		if (isWriteSVGDrawings()) {
			Element svgDescription = createSvgDescription(node.getViewBinding());
			if (!isEmpty(svgDescription)) {
				Element svgN = createElement(N_DATA);
				svgN.setAttribute(A_KEY, C_KEY_SVG_NODE);
				svgN.appendChild(svgDescription);
				nodeN.appendChild(svgN);
			}
		}
		
		ProgressionUtil.end(progression);
	}

	/** Generate the XML node for the graph anchors.
	 * 
	 * @param graphNode is the XML node that is corresponding to the graph model.
	 * @param anchor is the graph anchor to put inside.
	 * @param progression notifies on the progression of the creation.
	 * @throws IOException
	 */
	private void createAnchor(Element graphNode, Anchor<?,?,?,?> anchor, Progression progression) throws IOException {
		if (anchor==null) return;

		extractViews(anchor, this.viewObjects, this.outputFigures, progression);

		Element anchorN = createElement(N_PORT);
		setType(anchorN, anchor);
		setAttributes(A_NAME, anchorN, anchor);

		append(graphNode, anchorN);
	}

	/** Add the attributes of the specified object into the specified XML node.
	 * 
	 * @param idAttr
	 * @param document
	 * @param node
	 * @param object
	 * @throws IOException
	 */
	private void setAttributes(String idAttr, Element node, ModelObject object) throws IOException {
		if (object!=null) {
			Element attrsN = createElement(N_DATA);
			node.setAttribute(idAttr, object.getUUID().toString());
			for(Entry<String,Object> attr : object.getProperties().entrySet()) {
				Object value = attr.getValue();
				if (value!=null) {
					String id = null;
					String type = null;
					
					if (isInteger(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_LONG;
					}
					else if (isFloat(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_DOUBLE;
					}
					else if (isBoolean(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_BOOLEAN;
					}
					else if (isString(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_STRING;
					}
					
					if (id!=null && type!=null) {
						if (!this.additionalKeys.containsKey(id)) {
							Element keyN = createElement(N_KEY);
							keyN.setAttribute(A_ID, id);
							keyN.setAttribute(A_FOR, C_FOR_ALL);
							keyN.setAttribute(A_ATTR_NAME, attr.getKey());
							keyN.setAttribute(A_ATTR_TYPE, type);
							this.additionalKeys.put(id, keyN);
						}
						Element valueN = createElement(N_DATA);
						valueN.setAttribute(A_KEY, id);
						valueN.appendChild(createTextNode(value.toString()));
						append(node, valueN);
					}
					else {
						Element attrN = createAttribute(attr.getKey(), attr.getValue());
						append(attrsN, attrN);
					}
				}
			}
			if (!isEmpty(attrsN)) {
				attrsN.setAttribute(A_KEY, C_KEY_NETEDITOR_ATTRIBUTES);
				node.appendChild(attrsN);
			}
		}
	}

	/** Add the attributes of the specified object into the specified XML node.
	 * 
	 * @param idAttr
	 * @param document
	 * @param node
	 * @param object
	 * @throws IOException
	 */
	private void setAttributes(String idAttr, Element node, ViewComponent object) throws IOException {
		if (object!=null) {
			Element attrsN = createElement(N_DATA);
			node.setAttribute(idAttr, object.getUUID().toString());
			for(Entry<String,Object> attr : object.getProperties().entrySet()) {
				Object value = attr.getValue();
				if (value!=null) {
					String id = null;
					String type = null;
					
					if (isInteger(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_LONG;
					}
					else if (isFloat(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_DOUBLE;
					}
					else if (isBoolean(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_BOOLEAN;
					}
					else if (isString(value)) {
						id = C_KEY_ATTR_PREFIX+attr.getKey();
						type = C_ATTR_TYPE_STRING;
					}
					
					if (id!=null && type!=null) {
						if (!this.additionalKeys.containsKey(id)) {
							Element keyN = createElement(N_KEY);
							keyN.setAttribute(A_ID, id);
							keyN.setAttribute(A_FOR, C_FOR_ALL);
							keyN.setAttribute(A_ATTR_NAME, attr.getKey());
							keyN.setAttribute(A_ATTR_TYPE, type);
							this.additionalKeys.put(id, keyN);
						}
						Element valueN = createElement(N_DATA);
						valueN.setAttribute(A_KEY, id);
						valueN.appendChild(createTextNode(value.toString()));
						append(node, valueN);
					}
					else {
						Element attrN = createAttribute(attr.getKey(), attr.getValue());
						append(attrsN, attrN);
					}
				}
			}
			if (!isEmpty(attrsN)) {
				attrsN.setAttribute(A_KEY, C_KEY_NETEDITOR_ATTRIBUTES);
				node.appendChild(attrsN);
			}
		}
	}

	/** Add the type of the specified object into the specified XML node.
	 * 
	 * @param document
	 * @param node
	 * @param object
	 * @throws IOException
	 */
	private void setType(Element node, ModelObject object) throws IOException {
		URL specification = object.getMetamodelSpecification();
		if (specification==null) {
			try {
				specification = new URL(SCHEMA_URL+"#"+object.getClass().getCanonicalName()); //$NON-NLS-1$
			}
			catch (MalformedURLException e) {
				throw new IOException(e);
			}
		}
		Element typeN = createElement(N_DATA);
		typeN.setAttribute(A_KEY, C_KEY_NETEDITOR_TYPE);
		typeN.appendChild(createTextNode(specification.toExternalForm()));
		append(node, typeN);
	}

	/** Add the type of the specified object into the specified XML node.
	 * 
	 * @param document
	 * @param node
	 * @param figure
	 * @throws IOException
	 */
	private void setType(Element node, ViewComponent figure) throws IOException {
		URL specification = figure.getMetamodelSpecification();
		if (specification==null) {
			try {
				specification = new URL(SCHEMA_URL+"#"+figure.getClass().getCanonicalName()); //$NON-NLS-1$
			}
			catch (MalformedURLException e) {
				throw new IOException(e);
			}
		}
		Element typeN = createElement(N_DATA);
		typeN.setAttribute(A_KEY, C_KEY_NETEDITOR_TYPE);
		typeN.appendChild(createTextNode(specification.toExternalForm()));
		append(node, typeN);
	}
	
	/** Create a SVG description of the first view in the given binding.
	 * 
	 * @param viewBinding
	 * @return the SVG description or <code>null</code> if none. 
	 */
	private Element createSvgDescription(ViewBinding viewBinding) {
		try {
			Figure figure = null;
			Iterator<View> iterator = viewBinding.getViews().values().iterator();
			while (figure==null && iterator.hasNext()) {
				View v = iterator.next();
				if (v instanceof Figure) {
					figure = (Figure)v;
				}
			}
			
			if (figure!=null) {
				SvgExporter svgExporter = new SvgExporter();
				svgExporter.setNamespace(C_NS_SVG);
				svgExporter.setShadowExported(false);
				Element e = svgExporter.generateXML(document(), Collections.singleton(figure));
				if (e!=null) {
					e.removeAttribute(A_XMLNS);
					e.removeAttribute(A_XMLNS_XLINK);
				}
				return e;
			}
		}
		catch(Throwable _) {
			//
		}
		return null;
	}

}
