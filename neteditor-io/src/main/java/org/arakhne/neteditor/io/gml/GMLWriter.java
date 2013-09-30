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

package org.arakhne.neteditor.io.gml ;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.afc.text.Base64Coder;
import org.arakhne.afc.text.TextUtil;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.vmutil.ExternalizableResource;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ModelObjectView;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.io.AbstractNetEditorWriter;
import org.arakhne.neteditor.io.resource.ResourceRepository;

/** This class permits to export the
 *  <strong>graph-model</strong> into the GML format.
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
public class GMLWriter extends AbstractNetEditorWriter implements GMLConstants {

	/** Version of the specification supported by the GML writer.
	 */
	public static String SPECIFICATION_VERSION = "2"; //$NON-NLS-1$
	
	private PrintWriter out = null;
	private int indent = 0; 
	private final List<UUID> uniqIdsToUUID = new ArrayList<UUID>(); 
	private final Map<UUID,Integer> uuidTouniqIds = new TreeMap<UUID,Integer>();  

	/** Construct a new GraphWriter.          
	 */
	public GMLWriter() {    
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(OutputStream os, Graph<?, ?, ?, ?> graph)
			throws IOException {
		Map<UUID,Graph<?,?,?,?>> graphs = new TreeMap<UUID,Graph<?,?,?,?>>();
		if (graph!=null) graphs.put(graph.getUUID(), graph);
		List<? extends Figure> figures = Collections.emptyList();
		writeGraph(os, graphs, figures, getProgression());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final <G extends Graph<?, ?, ?, ?>> void write(OutputStream os, G graph,
			ViewComponentContainer<?, G> container) throws IOException {
		synchronized(container.getTreeLock()) {
			ProgressionUtil.init(getProgression(), 0, 100000);
			try {
				Map<UUID,Graph<?,?,?,?>> graphs = new TreeMap<UUID,Graph<?,?,?,?>>();
				if (graph!=null) graphs.put(graph.getUUID(), graph);
				List<Figure> figures = new ArrayList<Figure>();
				for(ViewComponent vc : container) {
					if (vc instanceof Figure) {
						figures.add((Figure)vc);
					}
					ProgressionUtil.advance(getProgression());
				}
				writeGraph(os, graphs, figures,
						ProgressionUtil.subToEnd(getProgression()));
			}
			finally {
				ProgressionUtil.end(getProgression());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void write(OutputStream os, Collection<? extends Figure> figures)
			throws IOException {
		// Find a graph.
		ProgressionUtil.init(getProgression(), 0, 3*figures.size());
		Map<UUID,Graph<?,?,?,?>> graphs = new TreeMap<UUID,Graph<?,?,?,?>>();
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
		writeGraph(os, graphs, figures, ProgressionUtil.subToEnd(getProgression()));

		ProgressionUtil.end(getProgression());
	}

	/** Write the graph.
	 * <p>
	 * This function could be overridden by subclasses.
	 * 
	 * @param os is the output stream.
	 * @param graphs are the graphs to output.
	 * @param figures are the figures to output. The list is ordered
	 * from the front layer to the background layer.
	 * @param progression is the progression indicator to be used.
	 * @throws IOException
	 */
	protected void writeGraph(
			OutputStream os,
			Map<UUID, ? extends Graph<?,?,?,?>> graphs, 
			Collection<? extends Figure> figures,
			Progression progression) throws IOException {
		ProgressionUtil.init(progression, 0, figures.size()+graphs.size());
		
		PrintWriter out = new PrintWriter(new OutputStreamWriter(os, "ISO-8859-1")); //$NON-NLS-1$
		try {

			this.out = out;
			this.indent = 0;
			this.uniqIdsToUUID.clear();
			this.uuidTouniqIds.clear();

			printComment("!/usr/bin/gmlviewer"); //$NON-NLS-1$
			printString(K_VERSION, SPECIFICATION_VERSION);
			printString(K_CREATOR, getClass().getCanonicalName()+" "+getWriterVersion()); //$NON-NLS-1$

			for(Graph<?,?,?,?> graph : graphs.values()) {
				printGraph(graph);
				ProgressionUtil.advance(progression);
			}

			printFullViews(figures, progression);

			ProgressionUtil.end(progression);
		}
		finally {
			out.close();
			this.out = null;
			this.indent = 0;
			this.uniqIdsToUUID.clear();
			this.uuidTouniqIds.clear();
		}
	}

	private void printGraph(Graph<?,?,?,?> graph) throws IOException {
		start(K_GRAPH);

		printString(K_DIRECTED, K_TRUE);
		printType(graph);
		printString(K_UUID, graph.getUUID().toString());
		printAttributes(graph.getProperties());

		for(Node<?,?,?,?> node : graph.getNodes()) {
			printNode(node);
		}

		for(Edge<?,?,?,?> edge : graph.getEdges()) {
			printEdge(edge);
		}

		end();
	}
	
	private void printFullViews(Iterable<? extends Figure> figures,
			Progression progression) throws IOException {
		Iterator<? extends Figure> iterator = figures.iterator();
		if (iterator.hasNext()) {
			start(K_GRAPHICS);
			Figure figure;
			while (iterator.hasNext()) {
				figure = iterator.next();
				start(K_FIGURE);
				printString(K_VIEWID, figure.getViewUUID().toString());
				printType(figure);
				printString(K_UUID, figure.getUUID().toString());
				if (figure instanceof ModelObjectView<?>) {
					ModelObjectView<?> mov = (ModelObjectView<?>)figure;
					int id = getUniqId(mov.getModelObject().getUUID());
					if (id!=-1) {
						printString(K_MODELID, Integer.toString(id));
					}
				}
				printAttributes(figure.getProperties());
				for(SubFigure subFigure : figure.getSubFigures()) {
					start(K_FIGURE);
					printType(subFigure);
					printString(K_UUID, subFigure.getUUID().toString());
					if (subFigure instanceof ModelObjectView<?>) {
						ModelObjectView<?> mov = (ModelObjectView<?>)subFigure;
						int id = getUniqId(mov.getModelObject().getUUID());
						if (id!=-1) {
							printString(K_MODELID, Integer.toString(id));
						}
					}
					printAttributes(subFigure.getProperties());
					end();
				}
				if (!(figure instanceof CoercedFigure)) {
					for(Entry<String,CoercedFigure> entry : figure.getAssociatedFiguresInView().entrySet()) {
						start(K_FIGURE);
						printString(K_COERCIONID, entry.getKey());
						printString(K_UUID, entry.getValue().getUUID().toString());
						end();
					}
				}
				end();
				ProgressionUtil.advance(progression);
			}
			end();
		}
	}

	private void printNode(Node<?,?,?,?> node) throws IOException {
		start(K_NODE);

		printUniqId(node.getUUID());
		printType(node);
		printString(K_UUID, node.getUUID().toString());
		printAttributes(node.getProperties());

		if (isAnchorOutput()) {
			for(Anchor<?,?,?,?> anchor : node.getAnchors()) {
				printAnchor(anchor);
			}
		}

		end();
	}

	private void printAnchor(Anchor<?,?,?,?> anchor) throws IOException {
		start(K_EDGEANCHOR);

		printUniqId(anchor.getUUID());
		printType(anchor);
		printString(K_UUID, anchor.getUUID().toString());
		printAttributes(anchor.getProperties());

		end();
	}

	private void printEdge(Edge<?,?,?,?> edge) throws IOException {
		start(K_EDGE);

		printUniqId(edge.getUUID());
		printType(edge);
		printString(K_UUID, edge.getUUID().toString());
		printAttributes(edge.getProperties());
		
		int n = getUniqId(edge.getStartAnchor().getNode().getUUID());
		if (n<0) throw new GMLException();
		printNumber(K_SOURCE, n);
		
		n = getUniqId(edge.getEndAnchor().getNode().getUUID());
		if (n<0) throw new GMLException();
		printNumber(K_TARGET, n);
		
		if (isAnchorOutput()) {
			n = getUniqId(edge.getStartAnchor().getUUID());
			if (n<0) throw new GMLException();
			printNumber(K_SOURCEPORT, n);
			
			n = getUniqId(edge.getEndAnchor().getUUID());
			if (n<0) throw new GMLException();
			printNumber(K_TARGETPORT, n);
		}

		end();
	}

	private void printAttributes(Map<String,Object> properties) throws IOException {
		if (properties!=null && !properties.isEmpty()) {
			Iterator<Entry<String,Object>> iterator = properties.entrySet().iterator();
			if (iterator.hasNext()) {
				start(K_ATTRIBUTES);
				
				Namespace rootNs = new Namespace();
	
				Entry<String,Object> property;
				
				while (iterator.hasNext()) {
					property = iterator.next();
					Object value = property.getValue();
					if (value!=null) {
						String name = property.getKey();
						if (name!=null && !name.isEmpty()) {
							String[] n = name.split("[.]"); //$NON-NLS-1$
							if (n.length==1) {
								printAttributeValue(name, value);
							}
							else {
								Namespace ns = rootNs;
								for(String nns : n) {
									if (nns!=null && !nns.isEmpty())
										ns = ns.ensure(nns);
								}
								ns.setValue(value);
							}
						}
					}
				}
				
				rootNs.print();
	
				end();
			}
		}
	}

	private void printAttributeValue(String name, Object value) throws IOException {
		if (value!=null) {
			if (isInteger(value)) {
				printNumber(name, ((Number)value).longValue());
			}
			else if (isFloat(value)) {
				printNumber(name, ((Number)value).doubleValue());
			}
			else if (isBoolean(value)) {
				start(name);
				printString(K_TYPE, K_BOOLEAN);
				printNumber(K_VALUE, ((Boolean)value).booleanValue() ? 1 : 0);
				end();
			}
			else if (isString(value)) {
				printString(name, value.toString());
			}
			else if (value instanceof Enum<?>) {
				start(name);
				printString(K_TYPE, K_ENUM);
				Enum<?> enumValue = (Enum<?>)value;
				Class<?> enumType = enumValue.getDeclaringClass();
				printString(K_NAME, enumType.getName());
				printString(K_VALUE, enumValue.name());
				end();
			}
			else if (value instanceof URL) {
				start(name);
				printString(K_TYPE, K_URL);
				ResourceRepository rr = getResourceRepository();
				printString(K_VALUE, (rr==null)
						? ((URL)value).toExternalForm()
								: rr.mapsTo((URL)value));
				end();
			}
			else if (value instanceof File) {
				start(name);
				printString(K_TYPE, K_URL);
				ResourceRepository rr = getResourceRepository();
				printString(K_VALUE, (rr==null)
						? ((File)value).toURI().toASCIIString()
								: rr.mapsTo(((File)value).toURI().toURL()));
				end();
			}
			else if (value instanceof URI) {
				start(name);
				printString(K_TYPE, K_URL);
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
				printString(K_VALUE, r);
				end();
			}
			else if (value instanceof Set<?>) {
				start(name);
				printString(K_TYPE, K_SET);
				for(Object v : (Set<?>)value) {
					printAttributeValue(K_VALUE, v);
				}
				end();
			}
			else if (value instanceof List<?>) {
				start(name);
				printString(K_TYPE, K_LIST);
				for(Object v : (List<?>)value) {
					printAttributeValue(K_VALUE, v);
				}
				end();
			}
			else if (value instanceof Collection<?>) {
				start(name);
				printString(K_TYPE, K_COLLECTION);
				for(Object v : (Collection<?>)value) {
					printAttributeValue(K_VALUE, v);
				}
				end();
			}
			else if (value instanceof ExternalizableResource) {
				start(name);
				ExternalizableResource er = (ExternalizableResource)value;
				URL erUrl = er.getExternalizableResourceLocation();
				if (erUrl!=null) {
					printString(K_TYPE, K_URL);
					printString(K_VALUE, erUrl.toExternalForm());
				}
				end();
			}
			else if (value instanceof Image) {
				start(name);
				ResourceRepository rr = getResourceRepository();
				if (rr!=null) {
					String iid = rr.mapsTo((Image)value);
					printString(K_TYPE, K_URL);
					printString(K_VALUE, iid);
				}
				end();
			}
			else if (value instanceof UUID) {
				start(name);
				UUID uuid = (UUID)value;
				printString(K_TYPE, K_UUID);
				printString(K_VALUE, uuid.toString());
				end();
			}
			else {
				if (value instanceof Serializable) {
					start(name);
					printString(K_TYPE, K_SERIAL);
					byte[] tab;
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					try {
						ObjectOutputStream oos = new ObjectOutputStream(baos);
						try {
							oos.writeObject(value);
						}
						finally {
							oos.close();
						}
						baos.flush();
						tab = baos.toByteArray();
					}
					finally {
						baos.close();
					}
					printString(K_VALUE, new String(Base64Coder.encode(tab)));
					end();
				}
			}
		}
	}

	private void printString(String name, String value) {
		StringBuilder str = new StringBuilder();
		str.append("\""); //$NON-NLS-1$
		str.append(TextUtil.toHTML(value));
		str.append("\""); //$NON-NLS-1$
		this.out.println(computeIndent()+name+" "+str.toString()); //$NON-NLS-1$
	}

	private void printComment(String comment) {
		StringBuilder b = new StringBuilder();
		b.append(computeIndent());
		b.append("#"); //$NON-NLS-1$
		if (comment!=null) {
			if (!comment.startsWith("!")) //$NON-NLS-1$
				b.append(" "); //$NON-NLS-1$
			b.append(comment);
		}
		this.out.println(b.toString());
	}

	private void printNumber(String name, long n) {
		this.out.println(computeIndent()+name+" "+Long.toString(n)); //$NON-NLS-1$
	}

	private void printNumber(String name, double n) {
		this.out.println(computeIndent()+name+" "+Double.toString(n)); //$NON-NLS-1$
	}

	private void printUniqId(UUID id) {
		int iid = this.uniqIdsToUUID.size();
		this.uuidTouniqIds.put(id, iid);
		this.uniqIdsToUUID.add(id);
		printNumber(K_ID, iid);
	}
	
	private int getUniqId(UUID uuid) {
		Integer v = this.uuidTouniqIds.get(uuid);
		if (v==null) return -1;
		return v.intValue();
	}

	private void start(String keyName) {
		this.out.println(computeIndent()+keyName + " ["); //$NON-NLS-1$
		this.indent += 2;
	}

	private void end() {
		this.indent -= 2;
		this.out.println(computeIndent()+"]"); //$NON-NLS-1$
	}

	private String computeIndent() {
		StringBuilder b = new StringBuilder();
		for(int i=0; i<this.indent; ++i) {
			b.append(" "); //$NON-NLS-1$
		}
		return b.toString();
	}
	
	private void printType(ModelObject object) throws IOException {
		URL specification = object.getMetamodelSpecification();
		if (specification==null) {
			try {
				specification = new URL(SCHEMA_URL+"#"+object.getClass().getCanonicalName()); //$NON-NLS-1$
			}
			catch (MalformedURLException e) {
				throw new IOException(e);
			}
		}
		printString(K_TYPE, specification.toExternalForm());
	}

	private void printType(ViewComponent component) throws IOException {
		URL specification = component.getMetamodelSpecification();
		if (specification==null) {
			try {
				specification = new URL(SCHEMA_URL+"#"+component.getClass().getCanonicalName()); //$NON-NLS-1$
			}
			catch (MalformedURLException e) {
				throw new IOException(e);
			}
		}
		printString(K_TYPE, specification.toExternalForm());
	}

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private class Namespace {

		private final Map<String,Namespace> children = new TreeMap<String,Namespace>();
		private final String name;
		private Object value = null;
		
		/**
		 */
		public Namespace() {
			this.name = null;
		}
		
		private Namespace(String name) {
			this.name = name;
		}

		/** Ensure that the child namespace with the given name exists.
		 * 
		 * @param childName
		 * @return the child
		 */
		public Namespace ensure(String childName) {
			Namespace ns = this.children.get(childName);
			if (ns==null) {
				ns = new Namespace(childName);
				this.children.put(childName, ns);
			}
			return ns;
		}
		
		/** Set the value associated to the namespace.
		 * 
		 * @param value
		 */
		public void setValue(Object value) {
			this.value = value;
		}
		
		/** Output the properties in the namespaces.
		 * 
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public void print() throws IOException {
			if (this.children.isEmpty()) {
				if (this.name!=null && !this.name.isEmpty() && this.value!=null)
					printAttributeValue(this.name, this.value);
			}
			else {
				if (this.name!=null && !this.name.isEmpty()) {
					start(this.name);
					printString(K_TYPE, K_NS);
					start(K_VALUE);
				}
				for(Namespace ns : this.children.values()) {
					ns.print();
				}
				if (this.name!=null && !this.name.isEmpty()) {
					end();
					end();
				}
			}
		}
		
	}
	
}
