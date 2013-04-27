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

/** Event of a change in the view component.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ViewComponentPropertyChangeEvent extends ViewComponentChangeEvent {

	private static final long serialVersionUID = -6608812973329861807L;
	
	private final String propertyName;
	private final Object oldValue;
	private final Object newValue;
	
	/**
	 * @param source
	 * @param propertyName
	 * @param oldValue
	 * @param newValue
	 */
	public ViewComponentPropertyChangeEvent(ViewComponent source, String propertyName, Object oldValue, Object newValue) {
		super(source, Type.PROPERTY_CHANGE);
		this.propertyName = propertyName;
		this.oldValue = oldValue;
		this.newValue = newValue;
	}
	
	/** Replies the name of the property that has changed.
	 * 
	 * @return the property name.
	 */
	public String getPropertyName() {
		return this.propertyName;
	}
	
	/** Replies the value of the property before its change.
	 * 
	 * @return the old value of the property.
	 */
	public Object getOldValue() {
		return this.oldValue;
	}
		
	/** Replies the value of the property after its change.
	 * 
	 * @return the new value of the property.
	 */
	public Object getNewValue() {
		return this.newValue;
	}

}
