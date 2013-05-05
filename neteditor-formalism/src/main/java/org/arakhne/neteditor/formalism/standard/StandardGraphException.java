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
package org.arakhne.neteditor.formalism.standard ;

import org.arakhne.neteditor.formalism.ModelException;

/** Exception thrown when two model objects are in different
 * graphs.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class StandardGraphException extends ModelException {

	private static final long serialVersionUID = 2551873861455524270L;

	/**
	 * @return the exception
	 */
	static StandardGraphException noLocationSpecified() {
		return new StandardGraphException("NO_LOCATION_SPECIFIED"); //$NON-NLS-1$
	}

	/**
	 * @return the exception
	 */
	static StandardGraphException alreadyInsideGraph() {
		return new StandardGraphException("ALREADY_INSIDE_GRAPH"); //$NON-NLS-1$
	}

	/**
	 * @return the exception
	 */
	static StandardGraphException alreadyInsideNode() {
		return new StandardGraphException("ALREADY_INSIDE_NODE"); //$NON-NLS-1$
	}

	/**
	 * @return the exception
	 */
	static StandardGraphException outsideGraph() {
		return new StandardGraphException("OUTSIDE_GRAPH"); //$NON-NLS-1$
	}

	/**
	 * @return the exception
	 */
	static StandardGraphException noSameGraph() {
		return new StandardGraphException("NOT_SAME_GRAPH"); //$NON-NLS-1$
	}
	
	/**
	 * @return the exception
	 */
	static StandardGraphException anchorOutsideNode() {
		return new StandardGraphException("ANCHOR_OUTSIDE_NODE"); //$NON-NLS-1$
	}

	/**
	 * @return the exception
	 */
	static StandardGraphException edgeAlreadyLinked() {
		return new StandardGraphException("EDGE_ALREADY_LINKED"); //$NON-NLS-1$
	}

	/**
	 */
	private StandardGraphException(String message) {
		super(message);
	}
	
}
