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

import java.util.Set;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.actionmode.creation.AbstractAndroidCreationMode;
import org.arakhne.neteditor.android.actionmode.creation.AbstractNodeCreationMode;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.fsm.constructs.FSMState;
import org.arakhne.neteditor.fsm.constructs.FiniteStateMachine;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/** Mode to create a standard FSM mode.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class FSMStateCreationMode extends AbstractNodeCreationMode {

	/**
	 */
	public FSMStateCreationMode() {
		super(R.string.undo_fsm_state);
	}
	
	@Override
	protected Shape2f getShape(Rectangle2f bounds) {
		return bounds;
	}
	
	private static String findName(FiniteStateMachine fsm) {
		Set<String> names = fsm.getNodeNames();
		int i = 1;
		String n = Integer.toString(i);
		while (names.contains(n)) {
			++i;
			n = Integer.toString(i);
		}
		return n;
	}
		
	@Override
	protected Node<?,?,?,?> createModelObject() {
		ActionModeOwner container = getModeManagerOwner();
		FiniteStateMachine stateMachine = (FiniteStateMachine)container.getGraph();
		FSMState state = new FSMState(findName(stateMachine));
		android.view.ActionMode bar = getActionBar();
		if (bar!=null) {
			CheckBox cb = (CheckBox)bar.getCustomView().findViewById(R.id.isaccepting);
			state.setAccepting(cb.isChecked());
		}
		return state;
	}
	
	@Override
	protected org.arakhne.neteditor.android.actionmode.creation.AbstractAndroidCreationMode.ActionBar createActionBarListener() {
		return new ActionBar();
	}

	/** Action bar listener
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class ActionBar extends AbstractAndroidCreationMode.ActionBar {

		/**
		 */
		public ActionBar() {
			//
		}

		/** Invoked when the action mode is created, ie. when
		 * {@code startActionMode()} was called.
		 * 
		 * @param mode is the new action mode.
		 * @param menu is the menu to populate with action buttons.
		 * @return <code>true</code> if the action mode should
		 * be created, <code>false</code> if entering this mode
		 * should be aborted.
		 */
		@Override
		public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
			super.onCreateActionMode(mode, menu);
			ActionModeOwner container = getModeManagerOwner();
			View customView = LayoutInflater.from(container.getContext()).inflate(R.layout.actionmode_fsm_state, null);
			mode.setCustomView(customView);
			TextView titleWidget = (TextView)customView.findViewById(R.id.title);
			titleWidget.setText(R.string.actionmode_create_fsm_state);
			return true;
		}
		
	} // class ActionBar

}
