/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.swing.dnd ;

import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import javax.swing.JComponent;
import javax.swing.TransferHandler;
import javax.swing.plaf.UIResource;

import org.arakhne.afc.io.filefilter.GXLFileFilter;
import org.arakhne.afc.io.filefilter.MultiFileFilter;
import org.arakhne.afc.io.filefilter.NGRFileFilter;
import org.arakhne.afc.io.filefilter.PDFFileFilter;
import org.arakhne.afc.io.filefilter.XMLFileFilter;
import org.arakhne.afc.io.stream.ReaderInputStream;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.factory.CollisionAvoider;
import org.arakhne.neteditor.fig.figure.decoration.BitmapFigure;
import org.arakhne.neteditor.fig.figure.decoration.PdfFigure;
import org.arakhne.neteditor.fig.figure.decoration.TextFigure;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.bitmap.ImageType;
import org.arakhne.neteditor.io.gxl.GXLException;
import org.arakhne.neteditor.io.gxl.GXLReader;
import org.arakhne.neteditor.io.ngr.NGRReader;
import org.arakhne.neteditor.swing.JFigureViewer;
import org.arakhne.neteditor.swing.selection.JSelectionManager;
import org.arakhne.vmutil.FileSystem;
import org.arakhne.vmutil.locale.Locale;

/** Implementation of a transfer handler dedicated to
 * the figures.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FigureTransferHandler extends TransferHandler implements UIResource {

	private static final long serialVersionUID = 8297025448193185945L;

	/**
	 */
	public FigureTransferHandler() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSourceActions(JComponent c) {
		if (c instanceof JFigureViewer<?>) {
			return COPY_OR_MOVE;
		}
		return NONE;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Transferable createTransferable(JComponent c) {
		if (c instanceof JFigureViewer<?>) {
			JFigureViewer<?> editor = (JFigureViewer<?>)c;
			JSelectionManager manager = editor.getSelectionManager();
			if (manager.isEmpty()) return null;
			return manager.getTransferableSelection();
		}
		return super.createTransferable(c);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void exportDone(JComponent source, Transferable data, int action) {
		if (action==MOVE
			&& source instanceof JFigureViewer<?>
		    && data instanceof TransferableFigureSet) {
			JFigureViewer<?> editor = (JFigureViewer<?>)source;
			if (editor.isEditable()) {
				TransferableFigureSet transferable = (TransferableFigureSet)data;
				Undoable edit = editor.getModeManager().getModeManagerOwner().removeFigures(
						editor.isAlwaysRemovingModelObjects(),
						false,
						transferable.getFigures());
				editor.getUndoManager().add(edit);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canImport(TransferSupport support) {
		if (support.getComponent() instanceof JFigureViewer<?>) {
			if (
				  support.isDataFlavorSupported(FigureDataFlavor.NGR.getDataFlavor())
				||support.isDataFlavorSupported(FigureDataFlavor.GXL.getDataFlavor())
				||support.isDataFlavorSupported(FigureDataFlavor.XML.getDataFlavor())
				||support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
				||support.isDataFlavorSupported(DataFlavor.stringFlavor)
				||support.isDataFlavorSupported(DataFlavor.imageFlavor)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings("unchecked")
	@Override
	public boolean importData(TransferSupport support) {
		if (support.getComponent() instanceof JFigureViewer<?>) {
			JFigureViewer<?> editor = (JFigureViewer<?>)support.getComponent();
			if (editor.isEditable()) {
				Transferable transferable = support.getTransferable();
				boolean isEmpty = editor.getFigureCount()==0;
				try {
					if (support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
						List<File> files = (List<File>)transferable.getTransferData(DataFlavor.javaFileListFlavor);
						return importFiles(editor, files);
					}
					else if (support.isDataFlavorSupported(DataFlavor.stringFlavor)) {
						return importString(editor, (String)transferable.getTransferData(DataFlavor.stringFlavor));
					}
					else if (support.isDataFlavorSupported(DataFlavor.imageFlavor)) {
						Image image = (Image)transferable.getTransferData(DataFlavor.imageFlavor);
						return importImage(editor, image);
					}
					else if (support.isDataFlavorSupported(FigureDataFlavor.NGR.getDataFlavor())) {
						URL url = (URL)transferable.getTransferData(FigureDataFlavor.NGR.getDataFlavor());
						return importNgr(editor, url);
					}
					else if (support.isDataFlavorSupported(FigureDataFlavor.GXL.getDataFlavor())
							||support.isDataFlavorSupported(FigureDataFlavor.XML.getDataFlavor())) {
						String str = (String)transferable.getTransferData(FigureDataFlavor.XML.getDataFlavor());
						return importXml(editor, str, Locale.getString("GXL_SOURCE")); //$NON-NLS-1$
					}
				}
				catch(IOException e) {
					editor.fireError(e);
				}
				catch (UnsupportedFlavorException e) {
					editor.fireError(e);
				}
				finally {
					if (isEmpty) {
						editor.defaultView(true);
					}
				}
			}
		}
		return false;
	}
	
	private static <T extends Graph<?,?,?,?>> boolean importNgr(JFigureViewer<T> editor, URL content) throws IOException {
		if (content!=null) {
			NGRReader reader = new NGRReader();
			Map<UUID,List<ViewComponent>> figures = new TreeMap<UUID,List<ViewComponent>>();
			T g = reader.read(editor.getSupportedGraphType(), content, figures);
			Undoable cEdit = editor.importGraph(
					FileSystem.largeBasename(content),
					g, figures);
			editor.getUndoManager().add(cEdit);
			return true;
		}
		return false;
	}

	private static <T extends Graph<?,?,?,?>> boolean importXml(JFigureViewer<T> editor, String content, String label) throws IOException {
		if (content!=null && !content.isEmpty()) {
			GXLReader reader = new GXLReader();
			StringReader sr = new StringReader(content);
			Map<UUID,List<ViewComponent>> figures = new TreeMap<UUID,List<ViewComponent>>();
			ReaderInputStream ris = new ReaderInputStream(sr);
			try {
				T g = reader.read(editor.getSupportedGraphType(), ris, figures);
				Undoable cEdit = editor.importGraph(
						label,
						g, figures);
				editor.getUndoManager().add(cEdit);
				return true;
			}
			finally {
				sr.close();
				ris.close();
			}
		}
		return false;
	}

	private static boolean importFiles(JFigureViewer<?> editor, List<File> files) throws IOException {
		boolean changed = false;
		NGRFileFilter ngr = new NGRFileFilter(false);
		GXLFileFilter gxl = new GXLFileFilter(false);
		XMLFileFilter xml = new XMLFileFilter(false);
		PDFFileFilter pdf = new PDFFileFilter(false);
		MultiFileFilter img = new MultiFileFilter(false, null, ImageType.getFileFilters());
		for(File file : files) {
			if (ngr.accept(file)) {
				if (importNgr(editor,file.toURI().toURL()))
					changed = true;
			}
			else if (gxl.accept(file)) {
				if (importXml(editor,toString(file), file.getName()))
					changed = true;
			}
			else if (xml.accept(file)) {
				if (importXml(editor,toString(file), file.getName()))
					changed = true;
			}
			else if (pdf.accept(file)) {
				if (importPdf(editor, file))
					changed = true;
			}
			else if (img.accept(file)) {
				if (importImage(editor, file))
					changed = true;
			}
			else {
				editor.fireError(new GXLException(Locale.getString("INVALID_FILE", file.getAbsolutePath()))); //$NON-NLS-1$
			}
		}
		return changed;
	}
	
	private static boolean importString(JFigureViewer<?> editor, String str) {
		if (str!=null && !str.isEmpty()) {
			try {
				if (importXml(editor,str, Locale.getString("GXL_SOURCE"))) return true; //$NON-NLS-1$
			}
			catch(Throwable _) {
				//
			}
			FontMetrics fm = editor.getFontMetrics(editor.getFont());
			Rectangle2f bounds = computeBounds(editor,
					fm.stringWidth(str), fm.getHeight());
			TextFigure figure = new TextFigure(editor.getUUID());
			figure.setText(str);
			figure.setBounds(
					bounds.getMinX(),
					bounds.getMinY(),
					bounds.getWidth(),
					bounds.getHeight());
			Undoable undo = editor.importFigure(figure);
			editor.getUndoManager().add(undo);
			return true;
		}
		return false;
	}
	
	private static boolean importImage(JFigureViewer<?> editor, Image image) {
		if (image!=null) {
			Rectangle2f bounds = computeBounds(editor, 
					Math.min(200, image.getWidth(null)),
					Math.min(200, image.getHeight(null)));
			BitmapFigure figure = new BitmapFigure(editor.getUUID());
			figure.setImage(VectorToolkit.image(image));
			figure.setBounds(
					bounds.getMinX(),
					bounds.getMinY(),
					bounds.getWidth(),
					bounds.getHeight());
			Undoable undo = editor.importFigure(figure);
			editor.getUndoManager().add(undo);
			return true;
		}
		return false;
	}
	
	private static boolean importImage(JFigureViewer<?> editor, File image) throws IOException {
		if (image!=null) {
			BitmapFigure figure = new BitmapFigure(editor.getUUID());
			figure.setImageURL(image.toURI().toURL());
			org.arakhne.afc.ui.vector.Image img = figure.getImage();
			Rectangle2f bounds = computeBounds(editor, 
					Math.min(200, img.getWidth(null)),
					Math.min(200, img.getHeight(null)));
			figure.setBounds(
					bounds.getMinX(),
					bounds.getMinY(),
					bounds.getWidth(),
					bounds.getHeight());
			Undoable undo = editor.importFigure(figure);
			editor.getUndoManager().add(undo);
			return true;
		}
		return false;
	}

	private static boolean importPdf(JFigureViewer<?> editor, File pdfFile) throws IOException {
		if (pdfFile!=null) {
			PdfFigure figure = new PdfFigure(editor.getUUID());
			figure.setPdfURL(pdfFile.toURI().toURL());
			org.arakhne.afc.ui.vector.Image img = figure.getImage();
			Rectangle2f bounds = computeBounds(editor, 
					Math.min(200, img.getWidth(null)),
					Math.min(200, img.getHeight(null)));
			figure.setBounds(
					bounds.getMinX(),
					bounds.getMinY(),
					bounds.getWidth(),
					bounds.getHeight());
			Undoable undo = editor.importFigure(figure);
			editor.getUndoManager().add(undo);
			return true;
		}
		return false;
	}

	private static String toString(File file) throws IOException {
		StringBuilder str = new StringBuilder();
		InputStream is = new FileInputStream(file);
		try {
			byte[] buffer = new byte[2048];
			int len;
			len = is.read(buffer);
			while (len>0) {
				str.append(new String(buffer, 0, len));
				len = is.read(buffer);
			}
		}
		finally {
			is.close();
		}
		return str.toString();
	}
	
	private static Rectangle2f computeBounds(JFigureViewer<?> editor, float width, float height) {
		Rectangle2D docBounds = editor.getDocumentRect();
		if (docBounds==null) return new Rectangle2f(0,0,width,height);
		CollisionAvoider ca = editor.getCollisionAvoider();
		Rectangle2f bounds = new Rectangle2f();
		Random rnd = new Random();
		float x, y;
		do {
			x = (float)((rnd.nextFloat() * (docBounds.getWidth() + 1.5f * width)) + docBounds.getX() - width);
			y = (float)((rnd.nextFloat() * (docBounds.getHeight() + 1.5f * height)) + docBounds.getY() - height);
			bounds.set(x, y, width, height);
		}
		while (ca.isCollisionFree(bounds, Collections.<ViewComponent>emptySet()));
		return bounds;
	}
	
}
