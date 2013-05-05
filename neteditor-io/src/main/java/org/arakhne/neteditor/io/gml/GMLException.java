/* 
 * $Id$
 * 
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

package org.arakhne.neteditor.io.gml ;

import java.io.IOException;

/** Exception dedicated to the GML format.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://en.wikipedia.org/wiki/Graph_Modelling_Language"
 * @since 16.0
 */
public class GMLException extends IOException {

	private static final long serialVersionUID = 8847814979785904893L;

	/**
	 */
	public GMLException() {
		super();
	}
		
	/**
	 * @param message
	 */
	public GMLException(String message) {
		super(message);
	}

	/**
	 * @param exception
	 */
	public GMLException(Throwable exception) {
		super(exception);
	}

	/**
	 * @param message
	 * @param exception
	 */
	public GMLException(String message, Throwable exception) {
		super(message, exception);
	}

}
