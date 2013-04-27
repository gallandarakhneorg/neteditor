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
package org.arakhne.neteditor.fig.view;

import java.util.EventListener;

import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.formalism.ModelObject;

/** Listener on figure creation and removal.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ViewComponentBindingListener extends EventListener {

	/** Invoked when a component wants to be created.
	 * 
	 * @param parent is the graphical component that should be the parent of
	 * the new component. It must be never <code>null</code>.
	 * @param modelObject is the model object for which the graphical component
	 * should be created. It must be never <code>null</code>.
	 */
	public void componentCreation(Figure parent, ModelObject modelObject);
	
	/** Invoked when a component wants to be removed.
	 * 
	 * @param parent is the graphical component that is the parent of
	 * the component to remove. It must be never <code>null</code>.
	 * @param subfigure is the graphical component to remove.
	 * It must be never <code>null</code>.
	 */
	public void componentRemoval(Figure parent, SubFigure subfigure);

	/** Invoked when a component should be added into the view.
	 * 
	 * @param figure is the graphical component to add.
	 * It must be never <code>null</code>.
	 */
	public void componentAddition(Figure figure);

	/** Invoked when a component should be removed from the view.
	 * 
	 * @param figure is the graphical component to remove.
	 * It must be never <code>null</code>.
	 */
	public void componentRemoval(Figure figure);

}
