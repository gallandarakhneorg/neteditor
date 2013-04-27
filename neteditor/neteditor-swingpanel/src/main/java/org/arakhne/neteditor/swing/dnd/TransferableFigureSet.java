/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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
package org.arakhne.neteditor.swing.dnd ;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.arakhne.afc.io.stream.WriterOutputStream;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.awt.DefaultLODGraphics2D;
import org.arakhne.afc.ui.awt.LODGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.io.eps.EpsExporter;
import org.arakhne.neteditor.io.graphviz.DotExporter;
import org.arakhne.neteditor.io.gxl.GXLWriter;
import org.arakhne.neteditor.io.pdf.PdfExporter;
import org.arakhne.neteditor.io.svg.SvgExporter;
import org.arakhne.neteditor.swing.graphics.DelegatedViewGraphics2D;

/** This is a collection of figures that can be tranfered.
 *  It is used to copy a selection into
 *  the clipboard for example.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TransferableFigureSet implements Transferable {

	private final Set<Figure> figures = new TreeSet<Figure>();

	/** Creates a new TransferableFigureSet.
	 * 
	 * @param figures are the figures to transfer.
	 */
	public TransferableFigureSet(Set<Figure> figures) {
		if (figures!=null) this.figures.addAll(figures);
	}
	
	/** Replies the figures that may be transfered.
	 * 
	 * @return the figures that may be transfered.
	 */
	public Set<Figure> getFigures() {
		return Collections.unmodifiableSet(this.figures);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] {
				FigureDataFlavor.XML.getDataFlavor(),
				FigureDataFlavor.GXL.getDataFlavor(),
				FigureDataFlavor.SVG.getDataFlavor(),
				FigureDataFlavor.DOT.getDataFlavor(),
				FigureDataFlavor.PDF.getDataFlavor(),
				FigureDataFlavor.IMAGE.getDataFlavor(),
				FigureDataFlavor.STRING.getDataFlavor(),
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		FigureDataFlavor f = FigureDataFlavor.valueOf(flavor);
		return f!=null && f!=FigureDataFlavor.NGR;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
		switch(FigureDataFlavor.valueOf(flavor)) {
		case GXL:
		case XML:
			return toDataXML();
		case SVG:
			return toDataSVG();
		case DOT:
			return toDataDOT();
		case PDF:
			return toDataPDF();
		case PS:
			return toDataEPS();
		case IMAGE:
			return toDataImage();
		case STRING:
			return toDataString();
		case NGR:
		default:
		}
		throw new UnsupportedFlavorException(flavor);
	}
	
	private Image toDataImage() throws IOException {
		Rectangle2f dim = null;
		for(Figure figure : this.figures) {
			Rectangle2f fr = figure.getBounds();
			if (fr!=null) {
				if (dim==null) dim = fr.clone();
				else dim = dim.createUnion(fr);
			}
		}
		if (dim==null) throw new IOException();
    	BufferedImage image = new BufferedImage(
    			(int)Math.ceil(dim.getWidth()),
    			(int)Math.ceil(dim.getHeight()),
    			BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g = (Graphics2D)image.getGraphics();
    	LODGraphics2D lg = new DefaultLODGraphics2D(
				g, null, true, true, Graphics2DLOD.HIGH_LEVEL_OF_DETAIL);
    	DelegatedViewGraphics2D<LODGraphics2D> vg = new DelegatedViewGraphics2D<LODGraphics2D>(lg);
    	Rectangle2f bounds;
    	g.translate(-Math.round(dim.getMinX()), -Math.round(dim.getMinY()));
    	for(Figure figure : this.figures) {
    		bounds = figure.getBounds();
    		vg.pushRenderingContext(figure, figure.getClip(bounds), bounds);
    		figure.paint(vg);
    		vg.popRenderingContext();
    	}
    	g.dispose();
    	return image;
	}
	
	private String toDataXML() throws IOException {
		GXLWriter writer = new GXLWriter();
		StringWriter sw = new StringWriter();
		try {
			WriterOutputStream wos = new WriterOutputStream(sw);
			try {
				writer.write(wos, this.figures);
			}
			finally {
				wos.close();
			}
			return sw.toString();
		}
		finally {
			sw.close();
		}
	}

	private String toDataSVG() throws IOException {
		SvgExporter writer = new SvgExporter();
		StringWriter sw = new StringWriter();
		try {
			WriterOutputStream wos = new WriterOutputStream(sw);
			try {
				writer.write(wos, this.figures);
			}
			finally {
				wos.close();
			}
			return sw.toString();
		}
		finally {
			sw.close();
		}
	}

	private String toDataPDF() throws IOException {
		PdfExporter writer = new PdfExporter();
		StringWriter sw = new StringWriter();
		try {
			WriterOutputStream wos = new WriterOutputStream(sw);
			try {
				writer.write(wos, this.figures);
			}
			finally {
				wos.close();
			}
			return sw.toString();
		}
		finally {
			sw.close();
		}
	}

	private String toDataEPS() throws IOException {
		EpsExporter writer = new EpsExporter();
		StringWriter sw = new StringWriter();
		try {
			WriterOutputStream wos = new WriterOutputStream(sw);
			try {
				writer.write(wos, this.figures);
			}
			finally {
				wos.close();
			}
			return sw.toString();
		}
		finally {
			sw.close();
		}
	}

	private String toDataDOT() throws IOException {
		DotExporter writer = new DotExporter();
		StringWriter sw = new StringWriter();
		try {
			WriterOutputStream wos = new WriterOutputStream(sw);
			try {
				writer.write(wos, this.figures);
			}
			finally {
				wos.close();
			}
			return sw.toString();
		}
		finally {
			sw.close();
		}
	}

	private String toDataString() throws IOException {
		return toDataXML();
	}

}
