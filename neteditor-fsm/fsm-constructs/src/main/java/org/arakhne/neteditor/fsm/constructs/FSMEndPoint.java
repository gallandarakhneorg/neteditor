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
package org.arakhne.neteditor.fsm.constructs ;

/** Define an end point in a finite state machine.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMEndPoint extends AbstractFSMNode {

	private static final long serialVersionUID = -1496777357787642211L;

	/**
	 */
	public FSMEndPoint() {
		super();
	}
	
	/**
	 * @param name
	 */
	public FSMEndPoint(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FSMNodeType getType() {
		return FSMNodeType.END_POINT;
	}

}
