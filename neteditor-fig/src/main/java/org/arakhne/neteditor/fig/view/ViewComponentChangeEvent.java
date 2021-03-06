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

import java.util.EventObject;

/** Event of a change in the view component.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ViewComponentChangeEvent extends EventObject {

	private static final long serialVersionUID = -5948395354456449665L;

	private final Type type;
	
	/**
	 * @param source
	 * @param type
	 */
	public ViewComponentChangeEvent(ViewComponent source, Type type) {
		super(source);
		this.type = type;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public ViewComponent getSource() {
		return (ViewComponent)super.getSource();
	}
	
	/** Replies the type of the event.
	 * 
	 * @return the type of the event.
	 */
	public Type getType() {
		return this.type;
	}

	/** Types of events for a change in the view component.
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public static enum Type {

		/** A property has changed.
		 */
		PROPERTY_CHANGE,
		
		/** The model associated to the figure has changed.
		 */
		MODEL_CHANGE,
		
	}
	
}
