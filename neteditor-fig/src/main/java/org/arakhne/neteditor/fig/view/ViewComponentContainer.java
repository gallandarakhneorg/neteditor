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
package org.arakhne.neteditor.fig.view;

import java.io.Serializable;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.formalism.Graph;

/** Container of view components.
 *
 * @param <C> is the type of the components.
 * @param <G> is the type of the graph.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ViewComponentContainer<C extends ViewComponent, G extends Graph<?,?,?,?>> extends Iterable<C>, Serializable {

	/**
	 * Gets this container's locking object (the object that owns the thread
	 * synchronization monitor) for rendering tree and layout
	 * operations.
	 * @return this component's locking object
	 */
	public Object getTreeLock();

	/** Relies the id of the view.
	 * 
	 * @return the id of the view.
	 */
	public UUID getUUID();

	/** Replies the bounds of the components.
	 * 
	 * @return the bounds of the components.
	 */
	public Rectangle2f getComponentBounds();

	/** Replies the number of components in this container.
	 * 
	 * @return the number of components in this container.
	 */
	public int getFigureCount();

	/** Replies the element at the specified index.
	 * 
	 * @param index is the index of the element.
	 * @return the element at the specified index.
	 */
	public C getFigureAt(int index);

	/** Add the element at the specified index.
	 * 
	 * @param component is the component to add.
	 * @return the position where the figure is inserted; or
	 * {@code -1} if the figure was not added.
	 */
	public int addFigure(C component);

	/** Remove the specified element.
	 * 
	 * @param component is the component to remove.
	 * @return the position of the removed figure.
	 */
	public int removeFigure(C component);

	/** Remove the element at the specified index.
	 * 
	 * @param index is the position of the element to remove.
	 * @return the removed figure.
	 */
	public C removeFigureAt(int index);

	/** Clear the components.
	 */
	public void removeAllFigures();

	/** Method to paint the figures.
	 *
	 * @param g the graphic context.
	 */
	public void paintViewComponents(ViewGraphics2D g) ;

	/** Replies the figure factory used by this panel.
	 * 
	 * @return the figure factory.
	 */
	public FigureFactory<G> getFigureFactory();

	/** Replies the foreground color associated to selection.
	 * 
	 * @return the foreground selection color
	 */
	public Color getSelectionForegroundColor();

	/** Replies the background color associated to selection.
	 * 
	 * @return the background selection color
	 */
	public Color getSelectionBackgroundColor();

	/** Replies the background color of the panel.
	 * 
	 * @return the background color
	 */
	public Color getBackgroundColor();

	/** Replies if the shadows are drawn.
	 * 
	 * @return <code>true</code> if the shadows are drawn;
	 * otherwise <code>false</code>.
	 */
	public boolean isShadowDrawn();

	/** Set if the shadows are drawn.
	 * 
	 * @param draw is <code>true</code> if the shadows are drawn;
	 * otherwise <code>false</code>.
	 */
	public void setShadowDrawn(boolean draw);
}
