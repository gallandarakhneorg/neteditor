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

package org.arakhne.neteditor.io.gxl ;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
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
import org.arakhne.neteditor.io.xml.AbstractXMLWriter;
import org.arakhne.vmutil.Resources;
import org.w3c.dom.Element;

/** This class permits to export the
 *  <strong>graph-model</strong> into the GXL format.
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
public class GXLWriter extends AbstractXMLWriter implements GXLConstants {

	/** Version of the specification supported by the GXL writer.
	 */
	public static String SPECIFICATION_VERSION = "2"; //$NON-NLS-1$

	private final Map<UUID,List<ViewComponent>> viewObjects = new TreeMap<UUID,List<ViewComponent>>();
	private final Map<Figure,Integer> outputFigures = new TreeMap<Figure,Integer>();
	
	/** Construct a new GraphWriter.          
	 */
	public GXLWriter() {    
		//
	}
	
	/** {@inheritDoc}
	 */
	@Override
	protected URL getSystemDTD() {
		return Resources.getResource(GXLWriter.class, C_GXL_DTD_FILENAME);
	}
	
	@Override
	protected URL getPublicDTD() {
		try {
			return new URL(C_GXL_DTD_URL);
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
		
		Element gxlN = createElement(N_GXL);
		gxlN.setAttribute(A_XMLNS_XLINK, C_XLINK_NS_URI);
		gxlN.setAttribute(A_XMLNS_NETEDITOR, C_NETEDITOR_NS_URI);
		gxlN.setAttribute(A_SPECIFICATION_VERSION, SPECIFICATION_VERSION);
		
		this.viewObjects.clear();
		this.outputFigures.clear();

		// Dispatch the figures
		extractNoModelObjectViews(figures, this.viewObjects, this.outputFigures,
				ProgressionUtil.sub(progression, figures.size()));

		try {
			// Put the raw description of the graphs
			for(Graph<?,?,?,?> graph : graphs.values()) {
				Element graphN = createGraph(graph, ProgressionUtil.sub(progression, 1));
				append(gxlN, graphN);
			}
			
			if (!this.outputFigures.isEmpty()) {
				// Create the graph representations of the views
				createViews(gxlN, ProgressionUtil.sub(progression, figures.size()));
			}
			
			ProgressionUtil.end(progression);
		}
		finally {
			this.outputFigures.clear();
			this.viewObjects.clear();
		}
		
		return gxlN;
	}
	
	/** Create the views.
	 * 
	 * @param gxlN is the XML node that contains GXL.
	 * @param progression notifies on the progression of the creation.
	 * @throws IOException
	 */
	private void createViews(Element gxlN, Progression progression) throws IOException {
		List<Element> relations = new ArrayList<Element>();
		
		ProgressionUtil.init(progression, 0, this.viewObjects.size());
		for(Entry<UUID,List<ViewComponent>> entry : this.viewObjects.entrySet()) {
			relations.clear();
			
			Element viewN = createElement(N_GRAPH);
			viewN.setAttribute(A_ID, entry.getKey().toString());
			Element typeN = createElement(N_TYPE);
			typeN.setAttribute(A_XLINK_HREF, C_INTERNAL_VIEW_TYPE);
			typeN.setAttribute(A_XLINK_TYPE, C_XLINK_SIMPLE);
			append(viewN, typeN);
			
			for(ViewComponent view : entry.getValue()) {
				Element figureN = createElement(N_NODE);
				setType(figureN, view);
				setAttributes(figureN, view);
				append(viewN, figureN);
			
				if (view instanceof ModelObjectView<?>) {
					ModelObjectView<?> figure = (ModelObjectView<?>)view;
					ModelObject mo = figure.getModelObject();
					if (mo!=null) {
						Element figureLinkN = createElement(N_REL);
						figureLinkN.setAttribute(A_ISDIRECTED, Boolean.FALSE.toString());
						typeN = createElement(N_TYPE);
						typeN.setAttribute(A_XLINK_HREF, C_INTERNAL_VIEW_RELATION_TYPE);
						typeN.setAttribute(A_XLINK_TYPE, C_XLINK_SIMPLE);
						append(figureLinkN, typeN);
						Element figureLinkEndN = createElement(N_RELEND);
						figureLinkEndN.setAttribute(A_IDREF, figure.getUUID().toString());
						figureLinkEndN.setAttribute(A_DIRECTION, C_GXL_REL_IN);
						append(figureLinkN, figureLinkEndN);
						figureLinkEndN = createElement(N_RELEND);
						figureLinkEndN.setAttribute(A_IDREF, mo.getUUID().toString());
						figureLinkEndN.setAttribute(A_DIRECTION, C_GXL_REL_OUT);
						append(figureLinkN, figureLinkEndN);
						relations.add(figureLinkN);
					}
				}

				if (view instanceof Figure) {
					Figure figure = (Figure)view;
					for(SubFigure subfigure : figure.getSubFigures()) {
						Element subfigureN = createElement(N_NODE);
						setType(subfigureN, subfigure);
						setAttributes(subfigureN, subfigure);
						append(viewN, subfigureN);
						
						if (subfigure instanceof ModelObjectView<?>) {
							ModelObjectView<?> ofigure = (ModelObjectView<?>)subfigure;
							ModelObject mo = ofigure.getModelObject();
							if (mo!=null) {
								Element figureLinkN = createElement(N_REL);
								figureLinkN.setAttribute(A_ISDIRECTED, Boolean.FALSE.toString());
								typeN = createElement(N_TYPE);
								typeN.setAttribute(A_XLINK_HREF, C_INTERNAL_VIEW_RELATION_TYPE);
								typeN.setAttribute(A_XLINK_TYPE, C_XLINK_SIMPLE);
								append(figureLinkN, typeN);
								Element figureLinkEndN = createElement(N_RELEND);
								figureLinkEndN.setAttribute(A_IDREF, ofigure.getUUID().toString());
								figureLinkEndN.setAttribute(A_DIRECTION, C_GXL_REL_IN);
								append(figureLinkN, figureLinkEndN);
								figureLinkEndN = createElement(N_RELEND);
								figureLinkEndN.setAttribute(A_IDREF, mo.getUUID().toString());
								figureLinkEndN.setAttribute(A_DIRECTION, C_GXL_REL_OUT);
								append(figureLinkN, figureLinkEndN);
								relations.add(figureLinkN);
							}
						}					}
				}
				
				for(Entry<String,CoercedFigure> pair : view.getAssociatedFiguresInView().entrySet()) {
					Element figureLinkN = createElement(N_REL);
					figureLinkN.setAttribute(A_ISDIRECTED, Boolean.TRUE.toString());
					typeN = createElement(N_TYPE);
					typeN.setAttribute(A_XLINK_HREF, C_INTERNAL_COERCION_RELATION_TYPE);
					typeN.setAttribute(A_XLINK_TYPE, C_XLINK_SIMPLE);
					append(figureLinkN, typeN);
					
					Element attrN = createElement(N_ATTR);
					attrN.setAttribute(A_NAME, "name"); //$NON-NLS-1$
					append(figureLinkN, attrN);
					Element stringN = createElement(N_STRING);
					stringN.appendChild(createTextNode(pair.getKey()));
					append(attrN, stringN);
					
					Element figureLinkEndN = createElement(N_RELEND);
					figureLinkEndN.setAttribute(A_IDREF, view.getUUID().toString());
					figureLinkEndN.setAttribute(A_DIRECTION, C_GXL_REL_IN);
					append(figureLinkN, figureLinkEndN);
					figureLinkEndN = createElement(N_RELEND);
					figureLinkEndN.setAttribute(A_IDREF, pair.getValue().getUUID().toString());
					figureLinkEndN.setAttribute(A_DIRECTION, C_GXL_REL_OUT);
					append(figureLinkN, figureLinkEndN);
					relations.add(figureLinkN);
				}
			}
			
			for(Element xmlNode : relations) {
				append(viewN, xmlNode);
			}
			
			append(gxlN, viewN);

			ProgressionUtil.advance(progression);
		}

		ProgressionUtil.end(progression);
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
		setAttributes(graphN, graph);
		graphN.setAttribute(A_EDGEIDS, Boolean.TRUE.toString());
		graphN.setAttribute(A_EDGEMODE, C_GXL_EDGE_DEFAULTDIRECTED);
		ProgressionUtil.ensureNoSubTask(progression);
		
		for(org.arakhne.neteditor.formalism.Node<?,?,?,?> node : graph.getNodes()) {
			createNode(graphN, node, ProgressionUtil.sub(progression, 1));
		}
		
		for(Edge<?,?,?,?> edge : graph.getEdges()) {
			createEdge(graphN, edge, ProgressionUtil.sub(progression, 1));
		}
		
		ProgressionUtil.end(progression);

		return graphN;
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
		setAttributes(nodeN, node);
		Element attrN = createAttribute(C_ATTR_LABEL, node.getExternalLabel());
		append(nodeN, attrN);
		append(graphNode, nodeN);
		
		ProgressionUtil.ensureNoSubTask(progression);
		
		if (isAnchorOutput()) {
			int n = ProgressionUtil.getValue(progression);
			for(Anchor<?,?,?,?> anchor : node.getAnchors()) {
				createAnchor(graphNode, anchor, ProgressionUtil.sub(progression, 1));
				ProgressionUtil.setValue(progression, ++n);
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
		
		Element anchorN = createElement(N_NODE);
		setType(anchorN, anchor);
		setAttributes(anchorN, anchor);
		append(graphNode, anchorN);
		
		Element anchorLinkN = createElement(N_REL);
		anchorLinkN.setAttribute(A_ISDIRECTED, Boolean.FALSE.toString());
		Element typeN = createElement(N_TYPE);
		typeN.setAttribute(A_XLINK_HREF, C_INTERNAL_NODE_ANCHOR_RELATION_TYPE);
		typeN.setAttribute(A_XLINK_TYPE, C_XLINK_SIMPLE);
		append(anchorLinkN, typeN);
		Element anchorLinkNodeN = createElement(N_RELEND);
		anchorLinkNodeN.setAttribute(A_IDREF, anchor.getNode().getUUID().toString());
		anchorLinkNodeN.setAttribute(A_DIRECTION, C_GXL_REL_IN);
		append(anchorLinkN, anchorLinkNodeN);
		Element anchorLinkEdgeN = createElement(N_RELEND);
		anchorLinkEdgeN.setAttribute(A_IDREF, anchor.getUUID().toString());
		anchorLinkEdgeN.setAttribute(A_DIRECTION, C_GXL_REL_OUT);
		append(anchorLinkN, anchorLinkEdgeN);
		append(graphNode, anchorLinkN);
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
		setAttributes(edgeN, edge);
		Element attrN = createAttribute(C_ATTR_LABEL, edge.getExternalLabel());
		append(edgeN, attrN);
		
		if (isAnchorOutput()) {
			edgeN.setAttribute(A_FROM, edge.getStartAnchor().getUUID().toString());
			edgeN.setAttribute(A_TO, edge.getEndAnchor().getUUID().toString());
		}
		else {
			edgeN.setAttribute(A_FROM, edge.getStartAnchor().getNode().getUUID().toString());
			edgeN.setAttribute(A_TO, edge.getEndAnchor().getNode().getUUID().toString());
		}
		edgeN.setAttribute(A_ISDIRECTED, Boolean.TRUE.toString());
		
		append(graphNode, edgeN);
	}
	
	/** Add the attributes of the specified object into the specified XML node.
	 * 
	 * @param document
	 * @param node
	 * @param object
	 * @throws IOException
	 */
	private void setAttributes(Element node, ModelObject object) throws IOException {
		if (object!=null) {
			node.setAttribute(A_ID, object.getUUID().toString());
			for(Entry<String,Object> attr : object.getProperties().entrySet()) {
				Element attrN = createAttribute(attr.getKey(), attr.getValue());
				append(node, attrN);
			}
		}
	}
	
	/** Add the attributes of the specified figure into the specified XML node.
	 * 
	 * @param document
	 * @param node
	 * @param figure
	 * @throws IOException
	 */
	private void setAttributes(Element node, ViewComponent figure) throws IOException {
		if (figure!=null) {
			node.setAttribute(A_ID, figure.getUUID().toString());
			for(Entry<String,Object> attr : figure.getProperties().entrySet()) {
				Element attrN = createAttribute(attr.getKey(), attr.getValue());
				append(node, attrN);
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
		Element typeN = createElement(N_TYPE);
		typeN.setAttribute(A_XLINK_HREF, specification.toExternalForm());
		typeN.setAttribute(A_XLINK_TYPE, C_XLINK_SIMPLE);
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
		Element typeN = createElement(N_TYPE);
		typeN.setAttribute(A_XLINK_HREF, specification.toExternalForm());
		typeN.setAttribute(A_XLINK_TYPE, C_XLINK_SIMPLE);
		append(node, typeN);
	}

}
