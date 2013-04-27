/* 
 * $Id$
 * 
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

package org.arakhne.neteditor.io.graphviz ;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.formalism.View;
import org.arakhne.neteditor.formalism.ViewBinding;
import org.arakhne.neteditor.io.VectorialExporter;
import org.arakhne.vmutil.locale.Locale;

/** This graphic context permits to create a dot/graphviz file.
 * <p>
 * The .dot file format is defined by the <a href="http://www.graphviz.org/">GraphViz project</a>.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DotExporter implements VectorialExporter {

	private boolean exportShadows = false;
	private boolean exportFigures = false;
	private Progression taskProgression = null;

	/**
	 */
	public DotExporter() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progression getProgression() {
		return this.taskProgression;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProgression(Progression model) {
		Progression old = this.taskProgression;
		this.taskProgression = model;
		if (old!=null && this.taskProgression!=null) {
			this.taskProgression.setProperties(
					old.getValue(),
					old.getMinimum(),
					old.getMaximum(),
					old.isAdjusting(),
					old.getComment());
			this.taskProgression.setIndeterminate(old.isIndeterminate());
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isShadowSupported() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSpecificationCompliant() {
		return true;
	}

	private static String protect(String v) {
		if (v==null) return ""; //$NON-NLS-1$
		return v.replaceAll("[\"]", "\\\\\\\""); //$NON-NLS-1$ //$NON-NLS-2$
	}

	private static Node<?,?,?,?> extractNode(Anchor<?,?,?,?> a) {
		if (a==null) return null;
		return a.getNode();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <G extends Graph<?, ?, ?, ?>> void write(File output, G graph,
			ViewComponentContainer<?, G> container) throws IOException {
		synchronized(container.getTreeLock()) {
			printIn(new PrintWriter(output), graph, true, isFigureExported(),
					getProgression());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(File output, Collection<? extends Figure> figures)
			throws IOException {
		PrintWriter writer = new PrintWriter(output);
		try {
			printIn(writer, figures, true, isFigureExported(),
					getProgression());
		}
		finally {
			writer.close();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <G extends Graph<?, ?, ?, ?>> void write(OutputStream output,
			G graph, ViewComponentContainer<?, G> container) throws IOException {
		synchronized(container.getTreeLock()) {
			printIn(new PrintWriter(output), graph, false, isFigureExported(),
					getProgression());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(OutputStream output, Collection<? extends Figure> figures)
			throws IOException {
		printIn(new PrintWriter(output), figures, false, isFigureExported(),
				getProgression());
	}

	private static <G extends Graph<?, ?, ?, ?>> void printIn(PrintWriter writer, G graph, boolean closeStream, boolean isFigureOut, Progression progression) {
		int size = graph.getNodeCount() + graph.getEdgeCount();
		ProgressionUtil.init(progression, 0, size*2+10);
		writer.write("/* Creator: Arakhne.org NetEditor "+DotExporter.class.getName()+" */\n"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.write("/* CreationDate: "+(new Date())+" */\n"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.write("digraph G"); //$NON-NLS-1$
		writer.write(Integer.toHexString(System.identityHashCode(graph)));
		writer.write(" {\n"); //$NON-NLS-1$
		if (isFigureOut) {
			writer.write("subgraph {\n"); //$NON-NLS-1$
		}
		ProgressionUtil.advance(progression, 1);
		write(writer, graph.getNodes(), graph.getEdges(), ProgressionUtil.sub(progression, size));
		if (isFigureOut) {
			writer.write("}\n"); //$NON-NLS-1$
			writer.write("subgraph {\n"); //$NON-NLS-1$
			ProgressionUtil.advance(progression, 10);
			writeFigures(writer, graph.getNodes(), graph.getEdges(),
					ProgressionUtil.sub(progression, size));

			writer.write("}\n"); //$NON-NLS-1$
		}
		writer.write("}\n\n"); //$NON-NLS-1$
		if (closeStream) writer.close();
		ProgressionUtil.end(progression);
	}

	private static void printIn(PrintWriter writer, Collection<? extends Figure> figures, boolean closeStream, boolean isFigureOut, Progression progression) {
		ProgressionUtil.init(progression, 0, figures.size()*3 + 5);
		Collection<Node<?,?,?,?>> nodes = new ArrayList<Node<?,?,?,?>>();
		Collection<Edge<?,?,?,?>> edges = new ArrayList<Edge<?,?,?,?>>();
		for(Figure figure : figures) {
			if (figure instanceof ModelObjectFigure<?>) {
				ModelObjectFigure<?> moFigure = (ModelObjectFigure<?>)figure;
				ModelObject mo = moFigure.getModelObject();
				if (mo instanceof Node<?,?,?,?>) {
					nodes.add((Node<?,?,?,?>)mo);
				}
				else if (mo instanceof Edge<?,?,?,?>) {
					edges.add((Edge<?,?,?,?>)mo);
				}
			}
			ProgressionUtil.advance(progression);
		}

		writer.write("/* Creator: Arakhne.org NetEditor "+DotExporter.class.getName()+" */\n"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.write("/* CreationDate: "+(new Date())+" */\n"); //$NON-NLS-1$ //$NON-NLS-2$
		writer.write("digraph G"); //$NON-NLS-1$
		writer.write(Integer.toHexString(System.identityHashCode(figures)));
		writer.write(" {\n"); //$NON-NLS-1$
		if (isFigureOut) {
			writer.write("subgraph {\n"); //$NON-NLS-1$
		}
		write(writer, nodes, edges, ProgressionUtil.sub(progression, figures.size()));
		if (isFigureOut) {
			writer.write("}\n"); //$NON-NLS-1$
			writer.write("subgraph {\n"); //$NON-NLS-1$
			writeFigures(writer, nodes, edges,
					ProgressionUtil.sub(progression,
					ProgressionUtil.getValueToEnd(progression) - 100));
			writer.write("}\n"); //$NON-NLS-1$
			ProgressionUtil.advance(progression);
		}
		writer.write("}\n\n"); //$NON-NLS-1$
		if (closeStream) writer.close();
		ProgressionUtil.end(progression);
	}

	private static void write(PrintWriter pw, Collection<? extends Node<?,?,?,?>> nodes, Collection<? extends Edge<?,?,?,?>> edges, Progression progression) {
		ProgressionUtil.init(progression, 0, nodes.size()+edges.size());
		writeNodes(pw, nodes, ProgressionUtil.sub(progression, nodes.size()));
		writeEdges(pw, edges, ProgressionUtil.sub(progression, edges.size()));
		ProgressionUtil.end(progression);
	}

	private static void writeFigures(PrintWriter pw, Collection<? extends Node<?,?,?,?>> nodes, Collection<? extends Edge<?,?,?,?>> edges, Progression progression) {
		int size = nodes.size()+edges.size();
		ProgressionUtil.init(progression, 0, size*2);
		Set<UUID> views = new TreeSet<UUID>();
		writeNodeFigures(pw, nodes, views, ProgressionUtil.sub(progression, nodes.size()));
		writeEdgeFigures(edges, views, ProgressionUtil.sub(progression, edges.size()));

		for(UUID view : views) {
			String id = "VIEW"+Integer.toHexString(System.identityHashCode(view)); //$NON-NLS-1$
			pw.write("\t" //$NON-NLS-1$
					+id
					+" [label=\"" //$NON-NLS-1$
					+protect(Locale.getString(DotExporter.class, "VIEW", view.toString())) //$NON-NLS-1$
					+"\"]\n"); //$NON-NLS-1$
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	private static void writeNodeFigures(PrintWriter pw, Collection<? extends Node<?,?,?,?>> nodes, Set<UUID> views, Progression progression) {
		UUID viewI;
		ProgressionUtil.init(progression, 0, nodes.size());
		for(Node<?,?,?,?> node : nodes) {
			ViewBinding binding = node.getViewBinding();
			for(Entry<UUID,View> viewEntry : binding.getViews().entrySet()) {
				View view = viewEntry.getValue();
				String figureId = "FIGURE"+Integer.toHexString(System.identityHashCode(view)); //$NON-NLS-1$
				pw.write("\t" //$NON-NLS-1$
						+figureId
						+" [label=\"" //$NON-NLS-1$
						+protect(Locale.getString(DotExporter.class, "FIGURE", view.getClass().getName())) //$NON-NLS-1$
						+"\"]\n"); //$NON-NLS-1$

				String nodeId = "NODE"+Integer.toHexString(System.identityHashCode(node)); //$NON-NLS-1$
				pw.write("\t" //$NON-NLS-1$
						+nodeId
						+"->" //$NON-NLS-1$
						+figureId
						+" [label=\"" //$NON-NLS-1$
						+protect(Locale.getString(DotExporter.class, "REPRESENTATION_LABEL")) //$NON-NLS-1$
						+"\"]\n"); //$NON-NLS-1$

				viewI = viewEntry.getKey();
				views.add(viewI);
				String viewId = "VIEW"+Integer.toHexString(System.identityHashCode(viewI)); //$NON-NLS-1$
				pw.write("\t" //$NON-NLS-1$
						+figureId
						+"->" //$NON-NLS-1$
						+viewId
						+" [label=\"" //$NON-NLS-1$
						+protect(Locale.getString(DotExporter.class, "INVIEW_LABEL")) //$NON-NLS-1$
						+"\"]\n"); //$NON-NLS-1$

				if (view instanceof Figure) {
					Figure figure = (Figure)view;
					for(SubFigure subfigure : figure.getSubFigures()) {
						viewI = subfigure.getViewUUID();
						views.add(viewI);
						viewId = "VIEW"+Integer.toHexString(System.identityHashCode(viewI)); //$NON-NLS-1$

						String subfigureId = "SUBFIGURE"+Integer.toHexString(System.identityHashCode(subfigure)); //$NON-NLS-1$
						pw.write("\t" //$NON-NLS-1$
								+subfigureId
								+" [label=\"" //$NON-NLS-1$
								+protect(Locale.getString(DotExporter.class, "FIGURE", subfigure.getClass().getName())) //$NON-NLS-1$
								+"\"]\n"); //$NON-NLS-1$

						pw.write("\t" //$NON-NLS-1$
								+figureId
								+"->" //$NON-NLS-1$
								+subfigureId
								+" [label=\"" //$NON-NLS-1$
								+protect(Locale.getString(DotExporter.class, "SUBFIGURE_LABEL")) //$NON-NLS-1$
								+"\"]\n"); //$NON-NLS-1$

						pw.write("\t" //$NON-NLS-1$
								+subfigureId
								+"->" //$NON-NLS-1$
								+viewId
								+" [label=\"" //$NON-NLS-1$
								+protect(Locale.getString(DotExporter.class, "INVIEW_LABEL")) //$NON-NLS-1$
								+"\"]\n"); //$NON-NLS-1$
					}
				}
			}
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	private static void writeEdgeFigures(Collection<? extends Edge<?,?,?,?>> edges, Set<UUID> views, Progression progression) {
		ProgressionUtil.init(progression, 0, edges.size());
		for(Edge<?,?,?,?> edge : edges) {
			ViewBinding binding = edge.getViewBinding();
			for(Entry<UUID,View> viewEntry : binding.getViews().entrySet()) {
				views.add(viewEntry.getKey());
			}
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	private static void writeNodes(PrintWriter pw, Collection<? extends Node<?,?,?,?>> nodes, Progression progression) {
		ProgressionUtil.init(progression, 0, nodes.size());
		for(Node<?,?,?,?> node : nodes) {
			String id = "NODE"+Integer.toHexString(System.identityHashCode(node)); //$NON-NLS-1$
			pw.write("\t" //$NON-NLS-1$
					+id
					+" [label=\"" //$NON-NLS-1$
					+protect(node.getExternalLabel())
					+"\"]\n"); //$NON-NLS-1$
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	private static void writeEdges(PrintWriter pw, Collection<? extends Edge<?,?,?,?>> edges, Progression progression) {
		Node<?,?,?,?> node1, node2;
		String name1, name2;
		ProgressionUtil.init(progression, 0, edges.size());
		for(Edge<?,?,?,?> edge : edges) {
			node1 = extractNode(edge.getStartAnchor());
			node2 = extractNode(edge.getEndAnchor());
			if (node1!=null && node2!=null) {
				name1 = "NODE"+Integer.toHexString(System.identityHashCode(node1)); //$NON-NLS-1$
				name2 = "NODE"+Integer.toHexString(System.identityHashCode(node2)); //$NON-NLS-1$
				pw.write("\t" //$NON-NLS-1$
						+name1
						+"->" //$NON-NLS-1$
						+name2
						+" [label=\"" //$NON-NLS-1$
						+protect(edge.getExternalLabel())
						+"\"]\n"); //$NON-NLS-1$
			}
			ProgressionUtil.advance(progression);
		}
		ProgressionUtil.end(progression);
	}

	@Override
	public boolean isShadowExported() {
		return this.exportShadows;
	}

	@Override
	public void setShadowExported(boolean export) {
		this.exportShadows = export;
	}

	/**
	 * Replies if the figures are exported.
	 * 
	 * @return <code>true</code> if the figures are exported;
	 * otherwise <code>false</code>.
	 * @since 16.1
	 */
	public boolean isFigureExported() {
		return this.exportFigures;
	}

	/**
	 * Set if the figures are exported.
	 * 
	 * @param export is <code>true</code> if the figures are exported;
	 * otherwise <code>false</code>.
	 * @since 16.1
	 */
	public void setFigureExported(boolean export) {
		this.exportFigures = export;
	}

}
