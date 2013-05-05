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
package org.arakhne.neteditor.fig.factory ;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObject;

/** This interface is a factory of figures that
 * permits to create a figure and bind it to
 * a model element.
 *
 * @param <G> is the type of the graphs supported by this factory.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface FigureFactory<G extends Graph<?,?,?,?>> {

	/** Add a collision avoider that may be used by this factory.
	 * 
	 * @param collisionAvoider
	 */
	public void addCollisionAvoider(CollisionAvoider collisionAvoider);
	
	/** Remove a collision avoider to be no more used by this factory.
	 * 
	 * @param collisionAvoider
	 */
	public void removeCollisionAvoider(CollisionAvoider collisionAvoider);

	/** Create a figure for the given object and try to
	 * put it in the best position.
	 * 
	 * @param viewID is the identifier of the view.
	 * @param documentRect is the area covered by the document.
	 * @param graph is the graph inside which the model was added.
	 * @param object is the model object for which a view should be created. 
	 * @return the created view.
	 * @throws FigureFactoryException
	 */
	public Figure createFigureFor(UUID viewID, Rectangle2f documentRect, G graph, ModelObject object) throws FigureFactoryException;

	/** Create a node figure for the given object at the specified position.
	 * 
	 * @param viewID is the identifier of the view.
	 * @param graph is the graph inside which the model was added.
	 * @param object is the model object for which a view should be created. 
	 * @param x is the position of the new set of figures.
	 * @param y is the position of the new set of figures.
	 * @return the created view.
	 * @throws FigureFactoryException
	 */
	public Figure createFigureFor(UUID viewID, G graph, ModelObject object, float x, float y) throws FigureFactoryException;

	/** Create an edge figure for the given object between the specified points.
	 * 
	 * @param viewID is the identifier of the view.
	 * @param graph is the graph inside which the model was added.
	 * @param object is the model object for which a view should be created. 
	 * @param x1 is the position of the first point of the figure.
	 * @param y1 is the position of the first point of the figure.
	 * @param x2 is the position of the second point of the figure.
	 * @param y2 is the position of the second point of the figure.
	 * @return the created view.
	 * @throws FigureFactoryException
	 */
	public Figure createFigureFor(UUID viewID, G graph, ModelObject object, float x1, float y1, float x2, float y2) throws FigureFactoryException;

	/** Create a sub figure for the given model object inside the given figure.
	 * 
	 * @param viewID is the identifier of the view.
	 * @param graph is the graph inside which the model was added.
	 * @param parent is the figure inside which the subfigure may be inserted.
	 * @param object is the model object for which a view should be created. 
	 * @return the created view.
	 */
	public SubFigure createSubFigureInside(UUID viewID, G graph, Figure parent, ModelObject object);

	/** Remove a sub figure for the given figure.
	 * 
	 * @param viewID is the identifier of the view.
	 * @param graph is the graph inside which the model was added.
	 * @param parent is the figure from which the subfigure may be removed.
	 * @param subfigure is the subfigure to remove.
	 */
	public void removeSubFigureFrom(UUID viewID, G graph, Figure parent, SubFigure subfigure);

}
