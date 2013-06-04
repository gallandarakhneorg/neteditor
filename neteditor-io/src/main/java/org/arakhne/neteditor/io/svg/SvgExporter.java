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

package org.arakhne.neteditor.io.svg ;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.AbstractVectorialExporter;
import org.arakhne.neteditor.io.VectorialExporterException;
import org.arakhne.vmutil.locale.Locale;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** This graphic context permits to create a SVG file
 *  from a graphic context.
 *  <p>
 *  The SVG format is specified in <a href="http://www.w3.org/Graphics/SVG/">W3C</a>.
 *  <p>
 *  The supported specifications are: <a href="http://www.w3.org/TR/SVG11/">1.1</a>.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SvgExporter extends AbstractVectorialExporter<SvgGraphics2D, OutputStream> {

	/** Name space to append to all the generated SVG tags.
	 */
	private String namespace = null;

	/**
	 */
	public SvgExporter() {
		//
	}
	
	/** Replies the namespace to appand to all the generated SVG tags.
	 * 
	 * @return the namespapce to append to.
	 */
	public String getNamespace() {
		return this.namespace;
	}

	/** Set the namespace to appand to all the generated SVG tags.
	 * 
	 * @param ns is the namespapce to append to.
	 */
	public void setNamespace(String ns) {
		this.namespace = (ns==null || ns.isEmpty()) ? null : ns;
	}

	/** {@inheritDoc}
	 */
	@Override
	public boolean isShadowSupported() {
		return true;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final boolean isSpecificationCompliant() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected OutputStream createStream(File currentFile, OutputStream stream) {
		return stream;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected SvgGraphics2D prepareExport(File currentFile, OutputStream stream, Rectangle2f bounds) throws IOException {
		SvgGraphics2D g = new SvgGraphics2D(bounds);
		g.setNamespace(getNamespace());
		return g;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void finalizeExport(File currentFile, OutputStream stream, Rectangle2f bounds, SvgGraphics2D graphicContext)
			throws IOException {
		if (stream!=null) {
			try {
				TransformerFactory transFactory = TransformerFactory.newInstance();
				transFactory.setAttribute("indent-number", Integer.valueOf(2)); //$NON-NLS-1$
				Transformer trans = transFactory.newTransformer();
				trans.setParameter(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
				trans.setOutputProperty(OutputKeys.INDENT, "yes"); //$NON-NLS-1$
				trans.setParameter(OutputKeys.DOCTYPE_PUBLIC, SvgGraphics2D.DTD_PUBLIC);
				trans.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, SvgGraphics2D.DTD_PUBLIC);
				trans.setParameter(OutputKeys.DOCTYPE_SYSTEM, SvgGraphics2D.DTD_SYSTEM);
				trans.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, SvgGraphics2D.DTD_SYSTEM);
	
				DOMSource source = new DOMSource(graphicContext.xmldocument);
				StreamResult xmlStream = new StreamResult(new OutputStreamWriter(stream, "UTF-8")); //$NON-NLS-1$
				trans.transform(source, xmlStream);
			}
			catch (TransformerException e) {
				throw new VectorialExporterException(e);
			}
		}
	}

	/**
	 * Generate the XML description of the SVG.
	 *
	 * @param xmlDocument is the XML document that will own the generated elements.
	 * @param graph is the graph to export.
	 * @param container is the container of the views.
	 * @return the root element of the SVG.
	 * @throws IOException
	 * @since 16.0
	 */
	public <G extends Graph<?,?,?,?>> Element generateXML(Document xmlDocument, G graph, ViewComponentContainer<?,G> container) throws IOException {
		synchronized(container.getTreeLock()) {
			ProgressionUtil.init(getProgression(), 0, container.getFigureCount()+5);
			try {
				SvgGraphics2D g = prepareExport(null, null, container.getComponentBounds());
				if (g==null) throw new IOException();
				g.xmldocument = xmlDocument; // Force to use the given XML document
				g.pushRenderingContext(null, null, container.getComponentBounds());
				g.prolog();
				boolean oldShadow = container.isShadowDrawn();
				container.setShadowDrawn(isShadowExported() && isShadowSupported());
				ProgressionUtil.advance(getProgression());
				try {
					container.paintViewComponents(g);
				}
				finally {
					container.setShadowDrawn(oldShadow);
				}
				ProgressionUtil.advance(getProgression(), container.getFigureCount());
				g.epilog();
				g.popRenderingContext();
				finalizeExport(null, null, container.getComponentBounds(), g);
				
				return extractNode(g.xmldocument, g.tag("svg")); //$NON-NLS-1$
			}
			finally {
				ProgressionUtil.end(getProgression());
			}
		}
	}

	/**
	 * Generate the XML description of the SVG.
	 *
	 * @param xmlDocument is the XML document that will own the generated elements.
	 * @param figures are the figures to output.
	 * @return the root element of the SVG.
	 * @throws IOException
	 * @since 16.0
	 */
	public Element generateXML(Document xmlDocument, Collection<? extends Figure> figures)
			throws IOException {
		ProgressionUtil.init(getProgression(), 0, figures.size()*2+3);
		try {
			Rectangle2f bounds = null;
			for(Figure figure : figures) {
				Rectangle2f fr = figure.getBounds();
				if (fr!=null) {
					if (bounds==null) bounds = fr.clone();
					else bounds = bounds.createUnion(fr);
				}
				ProgressionUtil.advance(getProgression());
			}
			if (bounds!=null && !bounds.isEmpty()) {
				Rectangle2f r;
				SvgGraphics2D g = prepareExport(null, null, bounds);
				if (g==null) throw new IOException();
				g.xmldocument = xmlDocument; // Force to use the given XML document
				g.pushRenderingContext(null, null, bounds);
				g.prolog();
				ProgressionUtil.advance(getProgression());
				for(Figure figure : figures) {
					r = figure.getBounds();
					g.pushRenderingContext(figure, figure.getClip(r), r);
					figure.paint(g);
					g.popRenderingContext();
					ProgressionUtil.advance(getProgression());
				}
				g.epilog();
				g.popRenderingContext();
				finalizeExport(null, null, bounds, g);
				
				return extractNode(g.xmldocument, g.tag("svg")); //$NON-NLS-1$
			}
		}
		finally {
			ProgressionUtil.end(getProgression());
		}
		return null;
	}

	/** Replies the child element in the specified parent and with the specified name.
	 * This function throws an exception when the not was not found.
	 * 
	 * @param parent is the node in which the child should be.
	 * @param name is the name of the node to reply.
	 * @return the node.
	 * @throws IOException when the node was not found.
	 * @see #extractNodeNoFail(Node, String)
	 */
	private static Element extractNode(Node parent, String name) throws IOException {
		NodeList list = parent.getChildNodes();
		if (list!=null) {
			for(int i=0; i<list.getLength(); ++i) {
				Node child = list.item(i);
				if (child.getNodeName().equals(name) && child instanceof Element) {
					return (Element)child;
				}
			}
		}
		throw new IOException(Locale.getString("XML_NODE_NOT_FOUND", name)); //$NON-NLS-1$
	}

}
