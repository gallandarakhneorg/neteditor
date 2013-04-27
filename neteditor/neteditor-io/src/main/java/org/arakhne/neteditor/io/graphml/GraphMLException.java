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

package org.arakhne.neteditor.io.graphml ;

import java.io.IOException;

/** Exception dedicated to the GraphML format.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://graphml.graphdrawing.org/"
 * @since 16.0
 */
public class GraphMLException extends IOException {

	private static final long serialVersionUID = -6020943455054452935L;

	/**
	 */
	public GraphMLException() {
		super();
	}
		
	/**
	 * @param message
	 */
	public GraphMLException(String message) {
		super(message);
	}

	/**
	 * @param exception
	 */
	public GraphMLException(Throwable exception) {
		super(exception);
	}

	/**
	 * @param message
	 * @param exception
	 */
	public GraphMLException(String message, Throwable exception) {
		super(message, exception);
	}

}
