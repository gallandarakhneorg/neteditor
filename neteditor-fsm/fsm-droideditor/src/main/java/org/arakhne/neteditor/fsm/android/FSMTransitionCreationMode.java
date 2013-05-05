/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.fsm.android ;

import org.arakhne.neteditor.android.actionmode.creation.AbstractEdgeCreationMode;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.fsm.constructs.FSMTransition;

import android.view.ActionMode;

/** Mode to create a standard FSM transition.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class FSMTransitionCreationMode extends AbstractEdgeCreationMode {

	/**
	 */
	public FSMTransitionCreationMode() {
		super(R.string.undo_fsm_transition);
	}

	@Override
	protected void onActionBarOpened(ActionMode bar) {
		bar.setTitle(R.string.actionmode_create_fsm_transition);
	}
	
	@Override
	protected Edge<?,?,?,?> createEdge() {
		return new FSMTransition();
	}
			
}
