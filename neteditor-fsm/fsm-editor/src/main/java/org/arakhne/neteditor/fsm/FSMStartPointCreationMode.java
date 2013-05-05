/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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
package org.arakhne.neteditor.fsm ;

import org.arakhne.neteditor.fsm.constructs.AbstractFSMNode;
import org.arakhne.neteditor.fsm.constructs.FSMStartPoint;
import org.arakhne.neteditor.fsm.constructs.FiniteStateMachine;
import org.arakhne.neteditor.swing.actionmode.creation.NodeCreationMode;

/** Mode to create a standard FSM mode.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class FSMStartPointCreationMode extends NodeCreationMode<FiniteStateMachine,AbstractFSMNode> {

	/**
	 */
	public FSMStartPointCreationMode() {
		setPersistent(true);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected AbstractFSMNode createModelObject() {
		return new FSMStartPoint();
	}
	
}
