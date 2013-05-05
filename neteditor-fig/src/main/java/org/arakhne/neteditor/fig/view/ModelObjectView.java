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
package org.arakhne.neteditor.fig.view;

import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.View;

/** This interface represents the model element figures.
 *
 * @param <M> is the type of the model object supported by this figure.
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ModelObjectView<M extends ModelObject> extends View {

	/** Add listener on changes.
	 * 
	 * @param listener
	 */
	public void addViewComponentModelChangeListener(ViewComponentModelChangeListener listener);

	/** Add listener on changes.
	 * 
	 * @param listener
	 */
	public void removeViewComponentModelChangeListener(ViewComponentModelChangeListener listener);

	/** Replies the name of this figure.
	 * 
	 * @return the name of this figure.
	 */
	public String getName();
	
	/** Reply the model object associated to this figure.
     *
     * @return the model object associated to this figure.
     */
    public M getModelObject();

    /** Set the the model object associated to this figure.
     *
     * @param modelObject is the model object to associated to this figure.
     */
    public void setModelObject(M modelObject);

}
