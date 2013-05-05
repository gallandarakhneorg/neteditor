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
package org.arakhne.neteditor.fsm.constructs ;

import org.arakhne.neteditor.formalism.standard.StandardAnchor;

/** Define an node's anchor in a finite state machine.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMAnchor extends StandardAnchor<FiniteStateMachine,AbstractFSMNode,FSMAnchor,FSMTransition> {

	private static final long serialVersionUID = 6877466864203153247L;

	/**
	 */
	public FSMAnchor() {
		//
	}
	
	/** Replies the type of the node associated to this anchor.
	 * 
	 * @return the type of the node.
	 */
	public FSMNodeType getNodeType() {
		return getNode().getType();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canConnectAsEndAnchor(FSMTransition edge,
			FSMAnchor startAnchor) {
		switch(getNodeType()) {
		case START_POINT:
			return false;
		case END_POINT:
			if (startAnchor!=null) {
				return startAnchor.getNodeType()==FSMNodeType.STATE;
			}
			break;
		case STATE:
			break;
		default:
			throw new IllegalStateException();
		}
		return true;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean canConnectAsStartAnchor(FSMTransition edge,
			FSMAnchor endAnchor) {
		switch(getNodeType()) {
		case START_POINT:
			if (endAnchor!=null) {
				return endAnchor.getNodeType()==FSMNodeType.STATE;
			}
			break;
		case END_POINT:
			return false;
		case STATE:
			break;
		default:
			throw new IllegalStateException();
		}
		return true;
	}
	
}
