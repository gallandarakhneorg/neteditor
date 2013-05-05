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

package org.arakhne.neteditor.io.xml ;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionInputStream;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.anchor.AnchorFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.io.resource.ResourceRepository;
import org.arakhne.vmutil.FileSystem;
import org.arakhne.vmutil.locale.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/** Abstract implementation of an XML reader.
 * This class provides the basic abstract implementation
 * of a XML reader based on DOM and on DTD validation.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractXMLReader extends AbstractXMLToolReader implements XMLConstants {

	private boolean isDtdValidation = true;
	private boolean connectFigures = true;

	private Document currentDocument = null;
	private final Map<UUID,ViewDescription> views = new TreeMap<UUID,ViewDescription>();
	private Map<UUID,List<ViewComponent>> figures = null;

	/**
	 */
	public AbstractXMLReader() {
		//
	}

	/**
	 * @param dtdValidation indicates if the DTD should be validated.
	 */
	public AbstractXMLReader(boolean dtdValidation) {
		this.isDtdValidation = dtdValidation;
	}

	/** Replies the document under reading.
	 * 
	 * @return the document under reading.
	 */
	protected final Document document() {
		return this.currentDocument;
	}

	/**
	 * Set the flag that permits to connect the model objects and
	 * the figures, or not.
	 * 
	 * @param connectFigures indicates if the figures should be linked to their model
	 * objects.
	 */
	public void setFigureConnection(boolean connectFigures) {
		this.connectFigures = connectFigures;
	}

	/**
	 * Replies the flag that permits to connect the model objects and
	 * the figures, or not.
	 * 
	 * @return <code>true</code> if the figures should be linked to their model
	 * objects; otherwise <code>false</code>.
	 */
	public boolean isFigureConnection() {
		return this.connectFigures;
	}

	/**
	 * Set the flag that permits to validate, or not, the DTD.
	 * 
	 * @param dtdValidation indicates if the DTD should be validated.
	 */
	public void setDTDValidation(boolean dtdValidation) {
		this.isDtdValidation = dtdValidation;
	}

	/**
	 * Replies if the DTD is validating when reading.
	 * 
	 * @return <code>true</code> if the DTD is validated; otherwise <code>false</code>.
	 */
	public boolean isDTDValidation() {
		return this.isDtdValidation;
	}

	/** Replies the URL of the SYSTEM DTD.
	 * 
	 * @return the URL.
	 */
	protected abstract URL getSystemDTD();

	/** Replies the local filename of the PUBLIC DTD.
	 * 
	 * @return the local filename.
	 */
	protected abstract URL getPublicDTD();

	/** Replies the description of a view.
	 * 
	 * @param viewId
	 * @return description, never <code>null</code>.
	 */
	protected final ViewDescription getViewDescription(UUID viewId) {
		ViewDescription d = this.views.get(viewId);
		if (d==null) {
			d = new ViewDescription(viewId);
			this.views.put(viewId, d);
		}
		return d;
	}

	/** Replies the descriptions of a view.
	 * 
	 * @return descriptions, never <code>null</code>.
	 */
	protected final Map<UUID,ViewDescription> getViewDescriptions() {
		return this.views;
	}

	/** Go through the view descriptions and bind the model objects and the views.
	 * 
	 * @param graph
	 * @param progression notifies about the progression of the binding.
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void bindModelAndViews(Graph<?,?,?,?> graph, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, getViewDescriptions().size());
		// Binding the model objects and the views
		for(ViewDescription viewDescription : getViewDescriptions().values()) {

			// Bind the anchors
			for(AnchorFigure anchorFigure : viewDescription.getAnchorFigures()) {
				if (graph!=null && isFigureConnection()) {
					UUID modelObjectId = viewDescription.getModelObjectIdFor(anchorFigure.getUUID());
					if (modelObjectId==null) {
						throw new IOException(Locale.getString("MODEL_OBJECT_NOT_FOUND_FOR", anchorFigure.getUUID())); //$NON-NLS-1$
					}
					ModelObject mo = graph.findModelObject(modelObjectId);
					if (mo instanceof Anchor<?,?,?,?>) {
						anchorFigure.setModelObject(mo);
					}
					else {
						throw new IOException(Locale.getString("MODEL_OBJECT_NOT_FOUND", modelObjectId)); //$NON-NLS-1$
					}
				}
			}

			// Bind the nodes
			for(NodeFigure nodeFigure : viewDescription.getNodeFigures()) {
				if (graph!=null && isFigureConnection()) {
					UUID modelObjectId = viewDescription.getModelObjectIdFor(nodeFigure.getUUID());
					if (modelObjectId==null) {
						throw new IOException(Locale.getString("MODEL_OBJECT_NOT_FOUND_FOR", nodeFigure.getUUID())); //$NON-NLS-1$
					}
					ModelObject mo = graph.findModelObject(modelObjectId);
					if (mo instanceof org.arakhne.neteditor.formalism.Node<?,?,?,?>) {
						nodeFigure.setModelObject(mo);
					}
					else {
						throw new IOException(Locale.getString("MODEL_OBJECT_NOT_FOUND", modelObjectId)); //$NON-NLS-1$
					}
				}
			}

			// Bind the edges
			for(EdgeFigure edgeFigure : viewDescription.getEdgeFigures()) {
				if (graph!=null && isFigureConnection()) {
					UUID modelObjectId = viewDescription.getModelObjectIdFor(edgeFigure.getUUID());
					if (modelObjectId==null) {
						throw new IOException(Locale.getString("MODEL_OBJECT_NOT_FOUND_FOR", edgeFigure.getUUID())); //$NON-NLS-1$
					}
					ModelObject mo = graph.findModelObject(modelObjectId);
					if (mo instanceof Edge<?,?,?,?>) {
						edgeFigure.setModelObject(mo);
					}
					else {
						throw new IOException(Locale.getString("MODEL_OBJECT_NOT_FOUND", modelObjectId)); //$NON-NLS-1$
					}
				}
			}

			// Coerces the figures
			for(Entry<UUID,Map<String,UUID>> entry : viewDescription.getCoercions().entrySet()) {
				ViewComponent coerciveFigure = viewDescription.getComponentWithId(entry.getKey());
				if (coerciveFigure==null) {
					throw new IOException(Locale.getString("FIGURE_NOT_FOUND", entry.getKey())); //$NON-NLS-1$
				}
				for(Entry<String,UUID> cEntry : entry.getValue().entrySet()) {
					ViewComponent coercedFigure = viewDescription.getComponentWithId(cEntry.getValue());
					if (coercedFigure==null) {
						throw new IOException(Locale.getString("FIGURE_NOT_FOUND", cEntry.getKey())); //$NON-NLS-1$
					}
					if (!(coercedFigure instanceof CoercedFigure)) {
						throw new IOException(Locale.getString("NOT_COERCED_FIGURE", cEntry.getKey())); //$NON-NLS-1$
					}
					coerciveFigure.addAssociatedFigureIntoView(
							cEntry.getKey(), 
							(CoercedFigure)coercedFigure);
				}
			}

			// Fill the list of the figures to display
			List<ViewComponent> figures = null;
			if (this.figures!=null) {
				figures = this.figures.get(viewDescription.getViewId());
				if (figures==null) {
					figures = new ArrayList<ViewComponent>();
					this.figures.put(viewDescription.getViewId(), figures);
				}
			}

			if (figures!=null) {
				for(UUID figureId : viewDescription.getLayeredFigures()) {
					figures.add(viewDescription.getComponentWithId(figureId));
				}
			}
			
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, File inputFile,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		FileInputStream fis = new FileInputStream(inputFile);
		try {
			return read(type, fis, figures);
		}
		finally {
			fis.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <G extends Graph<?, ?, ?, ?>> G read(Class<G> type, URL inputURL,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		InputStream is = inputURL.openStream();
		try {
			return read(type, is, figures);
		}
		finally {
			is.close();
		}
	}

	/** {@inheritDoc} 
	 */
	@SuppressWarnings("resource")
	@Override
	public final <G extends Graph<?,?,?,?>> G read(
			Class<G> type,
			InputStream is,
			Map<UUID,List<ViewComponent>> figures) throws IOException {
		ProgressionUtil.init(getProgression(), 0, 100000);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		}
		catch (ParserConfigurationException e) {
			throw new IOException(e);
		}

		// Force the builder to use the entity resolver devoted
		// to the support of dtd.
		if (isDTDValidation()) {
			factory.setValidating(true);
			builder.setEntityResolver(new DTDResolver(
					getPublicDTD().toString(), getSystemDTD()));
			builder.setErrorHandler(new XMLErrorHandler());
		}
		
		// Read the input stream and extract the XML tree
		try {
			Document document = builder.parse(new ProgressionInputStream(
					is,	ProgressionUtil.sub(getProgression(), 50000)));
			ProgressionUtil.ensureNoSubTask(getProgression());
			G g = readGraph(type, document, figures,
					ProgressionUtil.sub(getProgression(), 50000));
			ProgressionUtil.end(getProgression());
			return g;
		}
		catch (SAXException e) {
			throw new IOException(e);
		}
	}

	/**
	 * Read the graph from the XML representation of the GraphML content.
	 * 
	 * @param type is the type of the graph to read.
	 * @param graphMLDocument is the XML root document.
	 * @param figures are the figures extracted from the file from the front to the background.
	 * @return the graph.
	 * @throws IOException
	 */
	public final <G extends Graph<?, ?, ?, ?>> G readGraph(
			Class<G> type,
			Document graphMLDocument,
			Map<UUID, List<ViewComponent>> figures) throws IOException {
		return readGraph(type, graphMLDocument, figures, getProgression());
	}
	
	/**
	 * Read the graph from the XML representation of the GraphML content.
	 * 
	 * @param type is the type of the graph to read.
	 * @param graphMLDocument is the XML root document.
	 * @param figures are the figures extracted from the file from the front to the background.
	 * @param progression is the reading progression.
	 * @return the graph.
	 * @throws IOException
	 */
	public final <G extends Graph<?, ?, ?, ?>> G readGraph(
			Class<G> type,
			Document graphMLDocument,
			Map<UUID, List<ViewComponent>> figures,
			Progression progression) throws IOException {
		this.currentDocument = graphMLDocument;
		this.views.clear();
		this.figures = figures;
		try {
			// Parse the DOM tree and create the graph
			Graph<?,?,?,?> g = null;
			try {
				g = extractGraph(progression);
			}
			catch(IOException e) {
				throw e;
			}
			catch(Throwable e) {
				throw new IOException(e);
			}

			if (g!=null && !(type.isInstance(g)))
				throw new IOException(Locale.getString(AbstractXMLReader.class, "INVALID_GRAPH_TYPE", type.getCanonicalName())); //$NON-NLS-1$

			return type.cast(g);
		}
		finally {
			this.currentDocument = null;
			this.views.clear();
			this.figures = null;
		}
	}

	/**
	 * Extract a graph from a XML tree.
	 * 
	 * @param progression is the reading progression.
	 * @return the graph extracted from the GXL tree.
	 * @throws IOException
	 */
	protected abstract Graph<?,?,?,?> extractGraph(Progression progression) throws IOException;

	/** Extract the value of an attribute.
	 * 
	 * @param node is the XML node to explore.
	 * @return return the value.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	protected final List<Object> extractAttributeValue(Element node) throws IOException {
		List<Object> values = new ArrayList<Object>();
		NodeList children = node.getChildNodes();
		for(int i=0; i<children.getLength(); ++i) {
			Node child = children.item(i);
			if (child instanceof Element) {
				Element element = (Element)child;
				String textValue = element.getTextContent().trim();
				if (N_INT.equals(element.getNodeName())) {
					if (textValue!=null && !textValue.isEmpty()) {
						values.add(Long.valueOf(textValue));
					}
				}
				else if (N_FLOAT.equals(element.getNodeName())) {
					if (textValue!=null && !textValue.isEmpty()) {
						values.add(Double.valueOf(textValue));
					}
				}
				else if (N_BOOL.equals(element.getNodeName())) {
					if (textValue!=null && !textValue.isEmpty()) {
						values.add(Boolean.valueOf(textValue));
					}
				}
				else if (N_ENUM.equals(element.getNodeName())) {
					if (textValue!=null && !textValue.isEmpty()) {
						int index = textValue.lastIndexOf('.');
						if (index>0) {
							try {
								String className = textValue.substring(0, index);
								String name = textValue.substring(index+1);
								Class<? extends Enum<?>> type = (Class<? extends Enum<?>>)Class.forName(className);
								boolean found = false;
								for(Enum<?> enumConstant : type.getEnumConstants()) {
									if (enumConstant.name().equals(name)) {
										values.add(enumConstant);
										found = true;
										break;
									}
								}
								if (!found) {
									throw new IOException();
								}
							}
							catch (Exception e) {
								throw new IOException(e);
							}
						}
						else {
							throw new IOException();
						}
					}
				}
				else if (N_LOCATOR.equals(element.getNodeName())) {
					String href = element.getAttribute(A_XLINK_HREF);
					if (href!=null) {
						href = href.trim();
						if (!href.isEmpty()) {
							ResourceRepository rr = getResourceRepository();
							try {
								href = href.trim();
								URL u = (rr==null) ? null : rr.getURL(href);
								if (u==null) {
									u = FileSystem.convertStringToURL(href, true);
								}
								if (u!=null) {
									values.add(u);
								}
							}
							catch (Exception _) {
								try {
									File file;
									if (FileSystem.isWindowsNativeFilename(href)) {
										file = FileSystem.normalizeWindowsNativeFilename(href);
									}
									else {
										file = new File(href);
									}
									URL u = null;
									if (rr!=null) {
										URL root = rr.getRoot();
										if (root!=null) {
											u = FileSystem.makeAbsolute(file, root);
										}
									}
									if (u==null) {
										u = file.toURI().toURL();
									}
									values.add(u);
								}
								catch (IOException e) {
									throw e;
								}
								catch (Throwable e) {
									throw new IOException(e);
								}
							}
						}
					}
				}
				else if (N_SET.equals(element.getNodeName())) {
					Set<Object> collection = new HashSet<Object>();
					List<Object> subValues = extractAttributeValue(element);
					collection.addAll(subValues);
					values.add(collection);
				}
				else if (N_SEQ.equals(element.getNodeName())) {
					List<Object> collection = new ArrayList<Object>();
					List<Object> subValues = extractAttributeValue(element);
					collection.addAll(subValues);
					values.add(collection);
				}
				else if (N_BAG.equals(element.getNodeName())) {
					Collection<Object> collection = new ArrayList<Object>();
					List<Object> subValues = extractAttributeValue(element);
					collection.addAll(subValues);
					values.add(collection);
				}
				else if (N_STRING.equals(element.getNodeName())) {
					if (textValue!=null && !textValue.isEmpty()) {
						values.add(textValue);
					}
				}
			}
		}
		return values;
	}

	/** Extract one attribute from the specified node.
	 *
	 * @param <T> is the expected type for the value.
	 * @param type is the expected type for the value.
	 * @param node is the node in which the attr node is located.
	 * @param attrName is the name of the attribute to extract.
	 * @param defaultValue is the default value.
	 * @return the value; or the default value.
	 * @throws IOException
	 */
	protected final <T> T extractAttribute(Class<T> type, Element node, String attrName, T defaultValue) throws IOException {
		for(Element attrN : elements(node, N_ATTR)) {
			String name = attrN.getAttribute(A_NAME);
			if (attrName.equals(name)) {
				List<Object> values = extractAttributeValue(attrN);
				if (!values.isEmpty()) { 
					Object value = values.get(0);
					if (value==null || type.isInstance(value)) {
						return type.cast(value);
					}
				}
				return defaultValue;
			}
		}
		return defaultValue;
	}


	/** Extract the attributes from the specified node.
	 * 
	 * @param node is the node to explore.
	 * @param progression is the progression indicator to be used to notify about the progression of the extraction.
	 * @return the extracted attributes.
	 * @throws IOException
	 */
	protected abstract Map<String,Object> extractAttributes(Element node, Progression progression) throws IOException;

	/** Extract the attributes from the specified node and put them in the specified view component.
	 * 
	 * @param object is the view component to set.
	 * @param node is the node to explore.
	 * @param progression is the progression indicator to be used to notify about the progression of the extraction.
	 * @return the extracted attributes.
	 * @throws IOException
	 */
	protected final Map<String,Object> extractAttributes(ViewComponent object, Element node, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, 1000);
		Map<String,Object> extractedProperties = extractAttributes(node, ProgressionUtil.sub(progression, 600));
		if (object!=null) {
			object.setProperties(extractedProperties);
		}
		ProgressionUtil.end(progression);
		return extractedProperties;
	}

	/** Extract the attributes from the specified node and put them in the specified model object.
	 * 
	 * @param object is the model object to set.
	 * @param node is the node to explore.
	 * @param progression is the progression indicator to be used to notify about the progression of the extraction.
	 * @return the extracted attributes
	 * @throws IOException
	 */
	protected final Map<String,Object> extractAttributes(ModelObject object, Element node, Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, 1000);
		Map<String,Object> extractedProperties = extractAttributes(node, ProgressionUtil.sub(progression, 600));
		if (object!=null) {
			object.setProperties(extractedProperties);
		}
		ProgressionUtil.end(progression);
		return extractedProperties;
	}

	/** Describes a view.
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected static class ViewDescription {

		private final UUID id;
		private final Map<UUID,NodeFigure<?,?>> nodes = new TreeMap<UUID, NodeFigure<?,?>>();
		private final Map<UUID,EdgeFigure<?>> edges = new TreeMap<UUID, EdgeFigure<?>>();
		private final Map<UUID,AnchorFigure<?>> anchors = new TreeMap<UUID, AnchorFigure<?>>();
		private final Map<UUID,Figure> otherFigures = new TreeMap<UUID, Figure>();
		private final Map<UUID,UUID> modelObjects = new TreeMap<UUID, UUID>();
		private final Map<UUID,Map<String,UUID>> coercions = new TreeMap<UUID, Map<String,UUID>>();
		private final Map<Integer,UUID> layers = new TreeMap<Integer,UUID>();

		/**
		 * @param viewId
		 */
		public ViewDescription(UUID viewId) {
			this.id = viewId;
		}

		/** Replies the view id.
		 * 
		 * @return the view id.
		 */
		public UUID getViewId() {
			return this.id;
		}

		/** Add a coercion link between figures.
		 * 
		 * @param coerciveFigure
		 * @param coercedFigure
		 * @param name
		 * @throws IOException
		 */
		public void coerceFigure(UUID coerciveFigure, UUID coercedFigure, String name) throws IOException {
			Map<String,UUID> coercedFigures = this.coercions.get(coerciveFigure);
			if (coercedFigures==null) {
				coercedFigures = new TreeMap<String, UUID>();
				this.coercions.put(coerciveFigure, coercedFigures);
			}
			if (coercedFigures.put(name, coercedFigure)!=null) {
				throw new IOException();
			}
		}

		/** Replies the coercions.
		 * 
		 * @return ths coercions.
		 */
		public Map<UUID,Map<String,UUID>> getCoercions() {
			return this.coercions;
		}

		/** Replies the figures, one per layer.
		 * 
		 * @return the figures.
		 */
		public Iterable<UUID> getLayeredFigures() {
			return Collections.unmodifiableCollection(this.layers.values());
		}

		/** Add view component in the description.
		 * 
		 * @param figureId
		 * @param component
		 * @param position is the position of the figure is the list of figures.
		 */
		public void addViewComponent(UUID figureId, ViewComponent component, int position) {
			if (component instanceof NodeFigure<?,?>) {
				this.nodes.put(figureId,(NodeFigure<?,?>)component);
				this.layers.put(position, figureId);
			}
			else if (component instanceof EdgeFigure<?>) {
				this.edges.put(figureId, (EdgeFigure<?>)component);
				this.layers.put(position, figureId);
			}
			else if (component instanceof AnchorFigure<?>) {
				this.anchors.put(figureId, (AnchorFigure<?>)component);
			}
			else if (component instanceof Figure) {
				this.otherFigures.put(figureId, (Figure)component);
				this.layers.put(position, figureId);
			}
		}

		/** Add model object reference in the description.
		 * 
		 * @param figureId
		 * @param modelObjectId
		 */
		public void addModelObject(UUID figureId, UUID modelObjectId) {
			this.modelObjects.put(figureId, modelObjectId);
		}

		/** Replies the anchor figures.
		 * 
		 * @return the anchor figures.
		 */
		public Iterable<AnchorFigure<?>> getAnchorFigures() {
			return this.anchors.values();
		}

		/** Replies the node figures.
		 * 
		 * @return the node figures.
		 */
		public Iterable<NodeFigure<?,?>> getNodeFigures() {
			return this.nodes.values();
		}

		/** Replies the edge figures.
		 * 
		 * @return the edge  figures.
		 */
		public Iterable<EdgeFigure<?>> getEdgeFigures() {
			return this.edges.values();
		}

		/** Replies the other types of figures.
		 * 
		 * @return the other types of figures.
		 */
		public Iterable<Figure> getOtherFigures() {
			return this.otherFigures.values();
		}

		/** Replies the identifier of the model object that should be associated
		 * to the figure with the specified identifier.
		 * 
		 * @param figureId
		 * @return the model object id.
		 */
		public UUID getModelObjectIdFor(UUID figureId) {
			return this.modelObjects.get(figureId);
		}

		/** Replies the view component that has the specified id.
		 * 
		 * @param id
		 * @return the component or <code>null</code>.
		 */
		public ViewComponent getComponentWithId(UUID id) {
			ViewComponent vc;
			vc = this.nodes.get(id);
			if (vc!=null) return vc;
			vc = this.edges.get(id);
			if (vc!=null) return vc;
			vc = this.anchors.get(id);
			if (vc!=null) return vc;
			return this.otherFigures.get(id);
		}

	}

}
