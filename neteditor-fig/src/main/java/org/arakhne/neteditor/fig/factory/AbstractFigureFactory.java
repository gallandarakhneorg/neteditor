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
package org.arakhne.neteditor.fig.factory ;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.Node;

/** This class is a factory of figures that
 * permits to create a figure and bind it to
 * a model element.
 *
 * @param <G> is the type of the graphs supported by this factory.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractFigureFactory<G extends Graph<?,?,?,?>> implements FigureFactory<G> {

	/** Maximal number of tries before the creation of a figure failed.
	 */
	public static final int TRIES = 50;
	
	/** Maximal width and height for the new edges that have at least
	 * one free end.
	 */
	public static final float FREE_EDGE_SIZE = 50;

	private final List<CollisionAvoider> collisionAvoiders = new ArrayList<CollisionAvoider>(1);
	private final Random random = new Random();
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void addCollisionAvoider(CollisionAvoider collisionAvoider) {
		this.collisionAvoiders.add(collisionAvoider);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void removeCollisionAvoider(CollisionAvoider collisionAvoider) {
		this.collisionAvoiders.remove(collisionAvoider);
	}

	private synchronized boolean isValidBounds(Rectangle2f bounds) {
		if (this.collisionAvoiders.isEmpty()) return true;
		Set<? extends ViewComponent> exceptions = Collections.emptySet();
		for(CollisionAvoider avoider : this.collisionAvoiders) {
			if (!avoider.isCollisionFree(bounds, exceptions))
				return false;
		}
		return true;
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public Figure createFigureFor(UUID viewID, Rectangle2f documentRect, 
			G graph, ModelObject object) throws FigureFactoryException {
		if (object instanceof Node<?,?,?,?>) {
			return buildNodeFigure(viewID, graph, documentRect, (Node<?,?,?,?>)object);
		}
		if (object instanceof Edge<?,?,?,?>) {
			return buildEdgeFigure(viewID, graph, documentRect, (Edge<?,?,?,?>)object);
		}
		throw new FigureFactoryException();
	}
	
	/** Replies the preferred size for the specified node.
	 *  
	 * @param node
	 * @return the size, never <code>null</code>.
	 */
	protected abstract Dimension getPreferredNodeSize(Node<?,?,?,?> node);
	
	private Figure buildNodeFigure(UUID viewID, G graph, Rectangle2f documentRect, 
			Node<?,?,?,?> node) throws FigureFactoryException {
		float x, y;
		Rectangle2f nodeBounds = new Rectangle2f();
		if (documentRect!=null) {
			Dimension dim = getPreferredNodeSize(node);
			float w = (documentRect.getWidth() + 3f * dim.width());
			float h = (documentRect.getHeight() + 3f * dim.height());
			float x0 = (documentRect.getMinX() - 1.5f * dim.width());
			float y0 = (documentRect.getMinY() - 1.5f * dim.height());
			int tries = 0;
			do {
				
				x = (float)Math.rint(this.random.nextFloat() * w + x0);
				y = (float)Math.rint(this.random.nextFloat() * h + y0);
				
				nodeBounds.set(x, y, dim.width(), dim.height());
				
				if (isValidBounds(nodeBounds)) {
					return createFigureFor(viewID, graph, node, x, y);
				}
				
				++tries;
			}
			while (tries<TRIES);
			throw new FigureFactoryException();
		}
		
		return createFigureFor(viewID, graph, node, 0, 0);
	}
	
	private static Point2D getAnchorPoint(UUID viewId, Anchor<?,?,?,?> anchor) {
		SubFigure fig = anchor.getViewBinding().getView(viewId, SubFigure.class);
		if (fig==null) return null;
		Rectangle2f r = fig.getBounds();
		if (r==null) return null;
		return new Point2f(r.getCenterX(), r.getCenterY());
	}
	
	private Figure buildEdgeFigure(UUID viewID, G graph, Rectangle2f documentRect, 
			Edge<?,?,?,?> edge) throws FigureFactoryException {
		Anchor<?,?,?,?> sAnchor = edge.getStartAnchor();
		Anchor<?,?,?,?> eAnchor = edge.getEndAnchor();

		Point2D pos1 = (sAnchor!=null) ? getAnchorPoint(viewID, sAnchor) : null;
		Point2D pos2 = (eAnchor!=null) ? getAnchorPoint(viewID, eAnchor) : null;
		
		if (documentRect!=null) {
			if (pos1==null && pos2!=null) {
				pos1 = new Point2f(
						pos2.getX() - FREE_EDGE_SIZE,
						pos2.getY() - FREE_EDGE_SIZE);
			}
			else if (pos1!=null && pos2==null) {
				pos2 = new Point2f(
						pos1.getX() + FREE_EDGE_SIZE,
						pos1.getY() + FREE_EDGE_SIZE);
			}
			else if (pos1==null && pos2==null) {
				float x0 = (documentRect.getMinX() - FREE_EDGE_SIZE);
				float y0 = (documentRect.getMinY() - FREE_EDGE_SIZE);
				pos1 = new Point2f(x0, y0);
				pos2 = new Point2f(
						pos1.getX() + FREE_EDGE_SIZE,
						pos1.getY() + FREE_EDGE_SIZE);
			}
		}
		else {
			pos1 = new Point2f();
			pos1 = new Point2f(FREE_EDGE_SIZE, FREE_EDGE_SIZE);
		}
		assert(pos1!=null && pos2!=null);
		return createFigureFor(viewID, graph, edge,
				pos1.getX(), pos1.getY(),
				pos2.getX(), pos2.getY());
	}

}
