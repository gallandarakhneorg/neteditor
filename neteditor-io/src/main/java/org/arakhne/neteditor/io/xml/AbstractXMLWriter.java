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

package org.arakhne.neteditor.io.xml ;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ModelObjectView;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.View;
import org.arakhne.neteditor.io.AbstractNetEditorWriter;
import org.arakhne.neteditor.io.resource.ResourceRepository;
import org.arakhne.vmutil.ExternalizableResource;
import org.arakhne.vmutil.FileSystem;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;


/** This class provides XML utilities for writers. 
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public abstract class AbstractXMLWriter extends AbstractNetEditorWriter implements XMLConstants {

	/** Add the given child into the given parent if
	 * the child is not empty.
	 * 
	 * @param parent
	 * @param child
	 * @return <code>true</code> if the child is added, <code>false</code>
	 * if the child is not added.
	 */
	protected static boolean append(Node parent, Node child) {
		if (parent!=null && !isEmpty(child)) {
			parent.appendChild(child);
			return true;
		}
		return false;
	}

	/** Replies if the given node is empty, no child nor attribute.
	 * 
	 * @param node
	 * @return <code>true</code> if the node is empty or <code>null</code>, <code>false</code>
	 * if the child contains one attribute or one child.
	 */
	protected static boolean isEmpty(Node node) {
		return (node==null || 
			(node.getChildNodes().getLength()==0 &&
			 node.getAttributes().getLength()==0));
	}

	/** Extract the views that are not related to model objects.
	 * 
	 * @param figures are the views to parse
	 * @param viewObjects is the map inside which the views are put.
	 * @param outputFigures are the figures to output. It must be filled with
	 * the figures to output
	 * @param progression is the task progression.
	 */
	protected static void extractNoModelObjectViews(Collection<? extends Figure> figures,
			Map<UUID,List<ViewComponent>> viewObjects, Map<Figure,Integer> outputFigures,
			Progression progression) {
		ProgressionUtil.init(progression, 0, figures.size());
		int i=0;
		for(Figure figure : figures) {
			outputFigures.put(figure, Integer.valueOf(i));
			if (!(figure instanceof ModelObjectView<?>)) {
				List<ViewComponent> vfigures = viewObjects.get(figure.getViewUUID());
				if (vfigures==null) {
					vfigures = new ArrayList<ViewComponent>();
					viewObjects.put(figure.getViewUUID(), vfigures);
				}
				vfigures.add(figure);
			}
			++i;
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	/** Extract the views of the model object.
	 * 
	 * @param object is the object from which the views are extracted.
	 * @param viewObjects is the map inside which the views are put.
	 * @param outputFigures are the figures to output. It must be filled with
	 * the figures to output
	 * @param progression is the task progression.
	 */
	protected static void extractViews(ModelObject object,
			Map<UUID,List<ViewComponent>> viewObjects,
			Map<Figure,Integer> outputFigures,
			Progression progression) {
		if (object!=null) {
			Integer index;
			UUID lastViewKey = null;
			List<ViewComponent> figures = null;

			ProgressionUtil.init(progression, 0, object.getViewBinding().getViews().size());
			
			for(Entry<UUID,View> view : object.getViewBinding().getViews().entrySet()) {
				if (view.getValue() instanceof ViewComponent) {
					ViewComponent vcmp = (ViewComponent)view.getValue();
					
					if (vcmp instanceof Figure) {
						index = outputFigures.get(vcmp);
					}
					else if (vcmp instanceof SubFigure) {
						index = null;
					}
					else {
						index = 0;
					}
					
					if (index!=null && index.intValue()>=0) {
						if (figures==null || lastViewKey==null || !lastViewKey.equals(view.getKey())) {
							figures = viewObjects.get(view.getKey());
							lastViewKey = view.getKey();
							if (figures==null) {
								figures = new ArrayList<ViewComponent>();
								viewObjects.put(view.getKey(), figures);
							}
						}
						if (index==0) {
							figures.add(0, vcmp);
						}
						else if (figures.isEmpty()) {
							figures.add(vcmp);
						}
						else {
							int f = 0;
							int l = figures.size() - 1;
							while (f<=l) {
								int c = (f+l)/2;
								ViewComponent center = figures.get(c);
								Integer indexCenter = outputFigures.get(center);
								assert(indexCenter!=null);
								if (index<indexCenter) {
									l = c - 1;
								}
								else {
									f = c + 1;
								}
							}
							figures.add(f, vcmp);
						}
					}
				}
				ProgressionUtil.advance(progression);
			}
			ProgressionUtil.end(progression);
		}
	}
	
	private Document currentDocument = null;

	/**
	 */
	protected AbstractXMLWriter() {
		//
	}
	
	/** Replies the document under construction.
	 * 
	 * @return the document under construction.
	 */
	protected final Document document() {
		return this.currentDocument;
	}
	
	/** Create an XML element with the given name.
	 * This function invokes <code>{@link #document()}.{@link Document#createDocumentFragment()}</code>.
	 * 
	 * @param tagName
	 * @return the XML element.
	 */
	protected final Element createElement(String tagName) {
		return this.currentDocument.createElement(tagName);
	}
	
	/** Create an XML text node with the given data.
	 * This function invokes <code>{@link #document()}.{@link Document#createTextNode(String)}</code>.
	 * 
	 * @param data
	 * @return the XML text node.
	 */
	protected final Text createTextNode(String data) {
		return this.currentDocument.createTextNode(data);
	}

	/** Create the DOM of the content.
	 * 
	 * @param graphs are the graphs to output.
	 * @param figures are the figures to output. The list is ordered
	 * from the front layer to the background layer.
	 * @param progression is the task progression.
	 * @return the DOM element.
	 * @throws IOException
	 */
	protected abstract Element createGraphDOM(
			Map<UUID, ? extends Graph<?,?,?,?>> graphs, 
			Collection<? extends Figure> figures,
			Progression progression) throws IOException;
	
	/** Replies the URL of the SYSTEM DTD.
	 * 
	 * @return the URL.
	 */
	protected abstract URL getSystemDTD();
	
	/** Replies the description of the PUBLIC DTD.
	 * 
	 * @return the public DTD.
	 */
	protected abstract URL getPublicDTD();

	/** Write the graph.
	 * <p>
	 * This function could be overridden by subclasses.
	 * 
	 * @param os is the output stream.
	 * @param graphs are the graphs to output.
	 * @param figures are the figures to output. The list is sorted from
	 * the front layer to the background layer.
	 * @param progression is the task progression.
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	private void writeGraph(
			OutputStream os,
			Map<UUID, ? extends Graph<?,?,?,?>> graphs, 
			Collection<? extends Figure> figures,
			Progression progression) throws IOException {
		try {
			ProgressionUtil.init(progression, 0, 10000);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document xmldocument = builder.newDocument();
			
			this.currentDocument = xmldocument;

			Node node = createGraphDOM(graphs, figures,
					ProgressionUtil.sub(progression, 8000));

			if (node==null) throw new IOException();

			xmldocument.setXmlVersion("1.0"); //$NON-NLS-1$

			Comment comment;
			comment = xmldocument.createComment(
					"\n\tCreator: Arakhne.org NetEditor " //$NON-NLS-1$
					+getClass().getName()
					+" "+getWriterVersion() //$NON-NLS-1$
					+"\n\tCreationDate: " //$NON-NLS-1$
					+(new Date())
					+"\n"); //$NON-NLS-1$
			xmldocument.appendChild(comment);

			xmldocument.appendChild(node);

			TransformerFactory transFactory = TransformerFactory.newInstance();
			try {
				transFactory.setAttribute("indent-number", Integer.valueOf(2)); //$NON-NLS-1$
			}
			catch(Throwable _) {
				// Ignore the error when the attribute is not supported by the backend.
			}
			Transformer trans = transFactory.newTransformer();
			trans.setParameter(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			trans.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
			URL u;
			u = getPublicDTD();
			if (u!=null) { 
				URL su = FileSystem.toShortestURL(u);
				if (su!=null) u = su;
				trans.setParameter(OutputKeys.DOCTYPE_PUBLIC, u.toExternalForm());
				trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, u.toExternalForm());
			}
			u = getSystemDTD();
			if (u!=null) {
				URL su = FileSystem.toShortestURL(u);
				if (su!=null) u = su;
				trans.setParameter(OutputKeys.DOCTYPE_SYSTEM, u.toExternalForm());
				trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, u.toExternalForm());
			}

			DOMSource source = new DOMSource(xmldocument);
			StreamResult xmlStream = new StreamResult(new OutputStreamWriter(os, "UTF-8")); //$NON-NLS-1$
			trans.transform(source, xmlStream);
			ProgressionUtil.end(progression);
		}
		catch(IOException e) {
			throw e;
		}
		catch(Exception e) {
			throw new IOException(e);
		}		
		finally {
			this.currentDocument = null;
			if (os!=null) os.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(OutputStream os, Graph<?,?,?,?> graph) throws IOException {
		Map<UUID,Graph<?,?,?,?>> graphs = new TreeMap<UUID, Graph<?,?,?,?>>();
		if (graph!=null) graphs.put(graph.getUUID(), graph);
		List<? extends Figure> figures = Collections.emptyList();
		writeGraph(os, graphs, figures, getProgression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <G extends Graph<?,?,?,?>> void write(OutputStream os, G graph, ViewComponentContainer<?,G> container) throws IOException {
		synchronized(container.getTreeLock()) {
			ProgressionUtil.init(getProgression(), 0, 100000);
			Map<UUID,Graph<?,?,?,?>> graphs = new TreeMap<UUID, Graph<?,?,?,?>>();
			if (graph!=null) graphs.put(graph.getUUID(), graph);
			List<Figure> figures = new ArrayList<Figure>();
			for(ViewComponent vc : container) {
				if (vc instanceof Figure) {
					figures.add((Figure)vc);
				}
				ProgressionUtil.advance(getProgression(), 1);
			}
			writeGraph(os, graphs, figures,
					ProgressionUtil.subToEnd(getProgression()));
			ProgressionUtil.end(getProgression());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(OutputStream os, Collection<? extends Figure> figures) throws IOException {
		ProgressionUtil.init(getProgression(), 0, figures.size()*2);
		// Find a graph.
		Map<UUID,Graph<?,?,?,?>> graphs = new TreeMap<UUID, Graph<?,?,?,?>>();
		Graph<?,?,?,?> g;
		for(Figure figure : figures) {
			if (figure instanceof ModelObjectFigure<?>) {
				g = null;
				ModelObject mo = ((ModelObjectFigure<?>)figure).getModelObject();
				if (mo instanceof Graph<?,?,?,?>) {
					g = (Graph<?,?,?,?>)mo;
				}
				else if (mo instanceof org.arakhne.neteditor.formalism.Node<?,?,?,?>) {
					g = ((org.arakhne.neteditor.formalism.Node<?,?,?,?>)mo).getGraph();
				}
				else if (mo instanceof Edge<?,?,?,?>) {
					g = ((Edge<?,?,?,?>)mo).getGraph();
				}
				if (g!=null) {
					graphs.put(g.getUUID(), g);
				}
			}
			ProgressionUtil.advance(getProgression());
		}

		// Write
		writeGraph(os, graphs, figures,
				ProgressionUtil.subToEnd(getProgression()));
		ProgressionUtil.end(getProgression());
	}

	/** Create the XML node for an attribute.
	 * 
	 * @param name
	 * @param value
	 * @return the XML node.
	 * @throws IOException
	 */
	protected final Element createAttribute(String name, Object value) throws IOException {
		Element valueN = createAttributeValue(value);
		if (valueN==null) return null;

		Element attrN = createElement(N_ATTR);
		attrN.setAttribute(A_NAME, name);
		append(attrN, valueN);
		return attrN;
	}
	
	/** Create the XML node for an value.
	 * 
	 * @param jvalue the java value to put in the XML.
	 * @return the XML node.
	 * @throws IOException
	 */
	protected final Element createAttributeValue(Object jvalue) throws IOException {
		Object value = jvalue;
		if (value==null) return null;
		if (isInteger(value)) {
			Element valN = createElement(N_INT);
			valN.appendChild(createTextNode(value.toString()));
			return valN;
		}
		if (isFloat(value)) {
			Element valN = createElement(N_FLOAT);
			valN.appendChild(createTextNode(value.toString()));
			return valN;
		}
		if (isBoolean(value)) {
			Element valN = createElement(N_BOOL);
			valN.appendChild(createTextNode(value.toString()));
			return valN;
		}
		if (value instanceof Enum<?>) {
			Element valN = createElement(N_ENUM);
			Enum<?> enumValue = (Enum<?>)value;
			Class<?> enumType = enumValue.getDeclaringClass();
			String strValue = enumType.getName()+"."+enumValue.name(); //$NON-NLS-1$
			valN.appendChild(createTextNode(strValue));
			return valN;
		}
		if (value instanceof URL) {
			Element valN = createElement(N_LOCATOR);
			ResourceRepository rr = getResourceRepository();
			valN.setAttribute(A_XLINK_HREF,
					(rr==null)
					? ((URL)value).toExternalForm()
					: rr.mapsTo((URL)value));
			return valN;
		}
		if (value instanceof File) {
			Element valN = createElement(N_LOCATOR);
			ResourceRepository rr = getResourceRepository();
			valN.setAttribute(A_XLINK_HREF,
					(rr==null)
					? ((File)value).toURI().toASCIIString()
					: rr.mapsTo(((File)value).toURI().toURL()));
			return valN;
		}
		if (value instanceof URI) {
			Element valN = createElement(N_LOCATOR);
			String r;
			ResourceRepository rr = getResourceRepository();
			try {
				URL u = ((URI)value).toURL();
				r = (rr==null)
						? u.toExternalForm()
						: rr.mapsTo(u);
			}
			catch (Throwable e) {
				r = ((URI)value).toASCIIString();
			}
			valN.setAttribute(A_XLINK_HREF, r);
			return valN;
		}
		if (value instanceof Set<?>) {
			Set<?> collection = (Set<?>)value;
			Element setN = createElement(N_SET);
			for(Object v : collection) {
				Element subValue = createAttributeValue(v);
				append(setN, subValue);
			}
			return setN;
		}
		if (value instanceof List<?>) {
			List<?> collection = (List<?>)value;
			Element listN = createElement(N_SEQ);
			for(Object v : collection) {
				Element subValue = createAttributeValue(v);
				append(listN, subValue);
			}
			return listN;
		}
		if (value instanceof Collection<?>) {
			Collection<?> collection = (Collection<?>)value;
			Element listN = createElement(N_BAG);
			for(Object v : collection) {
				Element subValue = createAttributeValue(v);
				append(listN, subValue);
			}
			return listN;
		}
		if (value instanceof ExternalizableResource) {
			ExternalizableResource er = (ExternalizableResource)value;
			URL erUrl = er.getExternalizableResourceLocation();
			if (erUrl!=null) {
				Element valN = createElement(N_LOCATOR);
				valN.setAttribute(A_XLINK_HREF, erUrl.toExternalForm());
				return valN;
			}
		}
		if (value instanceof Image) {
			ResourceRepository rr = getResourceRepository();
			if (rr!=null) {
				Element valN = createElement(N_LOCATOR);
				String iid = rr.mapsTo((Image)value);
				valN.setAttribute(A_XLINK_HREF, iid);
				return valN;
			}
			return null;
		}
		if (value instanceof CharSequence) {
			Element stringN = createElement(N_STRING);
			stringN.appendChild(createTextNode(value.toString()));
			return stringN;
		}
		return null;
	}

}
