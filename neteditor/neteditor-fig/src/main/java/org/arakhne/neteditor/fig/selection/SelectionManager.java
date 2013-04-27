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
package org.arakhne.neteditor.fig.selection ;

import java.util.Iterator;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.selection.TreeSetSelectionManager;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.view.LinearFeature;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeEvent;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeListener;

/** This class is the selection manager. It is basically a
 *  collection of selected figures.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class SelectionManager extends TreeSetSelectionManager<Figure> {

	private final FigureListener listener = new FigureListener();

	private Rectangle2f bounds = null;

	private Boolean containsNode = null;
	private Boolean containsEdge = null;
	private Boolean containsDecoration = null;
	private Boolean containsOther = null;
	private Boolean containsLinearFeature = null;
	private Boolean containsNoLinearFeature = null;
	private Boolean allLocked = null;

	/** Create a new SelectionManager.
	 */
	public SelectionManager() {
		super(Figure.class);
	}
	
	/** Returns the bounds covered by the selected objects.
	 *
	 * @return the bound of the selection.
	 */
	public synchronized Rectangle2f getBounds() {
		if (this.bounds==null) {
			computeInternalBuffers();
		}
		return this.bounds;
	}

	/** Clean the selected elements.
	 */
	public synchronized void cleanUp() {
		Iterator<Figure> iterator = getIteratorOnStorage();
		Figure figure;
		while (iterator.hasNext()) {
			figure = iterator.next();
			figure.cleanUp();
		}
		resetInternalBuffers();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void resetInternalBuffers() {
		this.bounds = null;
		this.containsDecoration = null;
		this.containsNode = null;
		this.containsEdge = null;
		this.containsOther = null;
		this.containsLinearFeature = null;
		this.containsNoLinearFeature = null;
		this.allLocked = null;
	}
	
	@Override
	protected void onRemovedObject(Figure object) {
		object.removeViewComponentPropertyChangeListener(this.listener);
	}
	
	@Override
	protected void onAddedObject(Figure object) {
		object.addViewComponentPropertyChangeListener(this.listener);
	}

	private void computeInternalBuffers() {
		this.bounds = null;
		this.containsDecoration = Boolean.FALSE;
		this.containsNode = Boolean.FALSE;
		this.containsEdge = Boolean.FALSE;
		this.containsOther = Boolean.FALSE;
		this.containsLinearFeature = Boolean.FALSE;
		this.containsNoLinearFeature = Boolean.FALSE;
		this.allLocked = Boolean.TRUE;
		int flags = 0x0;
		Iterator<Figure> iterator = getIteratorOnStorage();
		Figure figure;
		while(iterator.hasNext()) {
			figure = iterator.next();
			if ( this.bounds == null ) this.bounds = figure.getBounds().clone();
			else this.bounds = this.bounds.createUnion( figure.getBounds() );
			if (figure instanceof NodeFigure<?,?>) {
				this.containsNode = Boolean.TRUE;
				flags |= 0x1;
			}
			else if (figure instanceof EdgeFigure<?>) {
				this.containsEdge = Boolean.TRUE;
				flags |= 0x2;
			}
			else if (figure instanceof DecorationFigure) {
				this.containsDecoration = Boolean.TRUE;
				flags |= 0x4;
			}
			else {
				this.containsOther = Boolean.TRUE;
				flags |= 0x8;
			}
			if (figure instanceof LinearFeature) {
				this.containsLinearFeature = Boolean.TRUE;
				flags |= 0x10;
			}
			else {
				this.containsNoLinearFeature = Boolean.TRUE;
				flags |= 0x20;
			}
			if (!figure.isLocked()) {
				this.allLocked = Boolean.FALSE;
				flags |= 0x40;
			}
			
			// Stop the loop because all the flags were set
			if (flags==0x7F) break;
		}
	}

	/** Replies if the contains contains at least one decoration figure.
	 * 
	 * @return <code>true</code> if the selection contains at least one
	 * decoration figure
	 * @see #containsNodeFigure()
	 * @see #containsEdgeFigure()
	 * @see #containsUnknownTypeFigure()
	 * @see #containsNoLinearFeature()
	 */
	public synchronized boolean containsDecorationFigure() {
		if (this.containsDecoration==null) {
			computeInternalBuffers();
		}
		return this.containsDecoration.booleanValue();
	}

	/** Replies if the contains contains at least one node figure.
	 * 
	 * @return <code>true</code> if the selection contains at least one
	 * node figure
	 * @see #containsDecorationFigure()
	 * @see #containsEdgeFigure()
	 * @see #containsUnknownTypeFigure()
	 * @see #containsLinearFeature()
	 * @see #containsNoLinearFeature()
	 */
	public synchronized boolean containsNodeFigure() {
		if (this.containsNode==null) {
			computeInternalBuffers();
		}
		return this.containsNode.booleanValue();
	}

	/** Replies if the contains contains at least one edge figure.
	 * 
	 * @return <code>true</code> if the selection contains at least one
	 * edge figure
	 * @see #containsDecorationFigure()
	 * @see #containsNodeFigure()
	 * @see #containsUnknownTypeFigure()
	 * @see #containsLinearFeature()
	 * @see #containsNoLinearFeature()
	 */
	public synchronized boolean containsEdgeFigure() {
		if (this.containsEdge==null) {
			computeInternalBuffers();
		}
		return this.containsEdge.booleanValue();
	}

	/** Replies if the contains contains at least one figure
	 * that is not a node, nor an edge, nor a decoration.
	 * 
	 * @return <code>true</code> if the selection contains at least one
	 * figure of unknown type.
	 * @see #containsDecorationFigure()
	 * @see #containsNodeFigure()
	 * @see #containsEdgeFigure()
	 * @see #containsLinearFeature()
	 * @see #containsNoLinearFeature()
	 */
	public synchronized boolean containsUnknownTypeFigure() {
		if (this.containsOther==null) {
			computeInternalBuffers();
		}
		return this.containsOther.booleanValue();
	}

	/** Replies if the contains contains at least one
	 * {@link LinearFeature linear feature}.
	 * 
	 * @return <code>true</code> if the selection contains at least one
	 * linear feature.
	 * @see #containsDecorationFigure()
	 * @see #containsNodeFigure()
	 * @see #containsEdgeFigure()
	 * @see #containsUnknownTypeFigure()
	 * @see #containsNoLinearFeature()
	 */
	public synchronized boolean containsLinearFeature() {
		if (this.containsLinearFeature==null) {
			computeInternalBuffers();
		}
		return this.containsLinearFeature.booleanValue();
	}

	/** Replies if the contains contains at least one
	 * that is not a {@link LinearFeature linear feature}.
	 * 
	 * @return <code>true</code> if the selection contains at least one
	 * object that is not a linear feature.
	 * @see #containsDecorationFigure()
	 * @see #containsNodeFigure()
	 * @see #containsEdgeFigure()
	 * @see #containsUnknownTypeFigure()
	 * @see #containsLinearFeature()
	 */
	public synchronized boolean containsNoLinearFeature() {
		if (this.containsNoLinearFeature==null) {
			computeInternalBuffers();
		}
		return this.containsNoLinearFeature.booleanValue();
	}

	/** Replies if the selected figures are all locked.
	 * 
	 * @return <code>true</code> if all the figures are locked;
	 * otherwise <code>false</code>.
	 */
	public synchronized boolean isAllLocked() {
		if (this.allLocked==null) {
			computeInternalBuffers();
		}
		return this.allLocked.booleanValue();
	}

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FigureListener implements ViewComponentPropertyChangeListener {

		/**
		 */
		public FigureListener() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void propertyChange(ViewComponentPropertyChangeEvent event) {
			if (event.getSource() instanceof Figure) {
				String name = event.getPropertyName();
				if ("isselectable".equals(name)) { //$NON-NLS-1$
					Figure figure = (Figure)event.getSource();
					if (!figure.isSelectable()) remove(figure);
				}
				else if ("islocked".equals(name)) { //$NON-NLS-1$
					resetInternalBuffers();
				}
				else if (SelectionManager.this.bounds!=null) {
					if ("x".equals(name) //$NON-NLS-1$
						||"y".equals(name) //$NON-NLS-1$
						||"width".equals(name) //$NON-NLS-1$
						||"height".equals(name)) { //$NON-NLS-1$
						resetInternalBuffers();
					}
				}
			}
		}

	}

}
