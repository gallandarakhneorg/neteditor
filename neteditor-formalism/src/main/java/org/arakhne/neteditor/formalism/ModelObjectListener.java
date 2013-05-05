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
package org.arakhne.neteditor.formalism;

import java.util.EventListener;

/** Event in the model.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ModelObjectListener extends EventListener {

	/** Invoked each time the property of a model has changed.
	 * 
	 * @param event
	 */
	public void modelPropertyChanged(ModelObjectEvent event);
    
	/** Invoked each time the container of a model object has changed.
	 * 
	 * @param event
	 */
	public void modelContainerChanged(ModelObjectEvent event);

	/** Invoked each time the link to an other model object has
	 * changed.
	 * 
	 * @param event
	 */
	public void modelLinkChanged(ModelObjectEvent event);

	/** Invoked each time the content of a model object has
	 * changed.
	 * 
	 * @param event
	 */
	public void modelContentChanged(ModelObjectEvent event);

	/** Invoked each time a component was added.
	 * 
	 * @param event
	 */
	public void modelComponentAdded(ModelObjectEvent event);

	/** Invoked each time a component was removed.
	 * 
	 * @param event
	 */
	public void modelComponentRemoved(ModelObjectEvent event);

}
