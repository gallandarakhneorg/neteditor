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

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.arakhne.neteditor.formalism.standard.StandardGraph;

/** Define a finite state machine.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FiniteStateMachine extends StandardGraph<FiniteStateMachine,AbstractFSMNode,FSMAnchor,FSMTransition> {

	private static final long serialVersionUID = 8316897236653623400L;

	/** Name of the property that is storing the codes of the actions.
	 */
	public static final String PROPERTY_ACTIONCODES = "actionCode#"; //$NON-NLS-1$

	private final Map<String,String> actionCodes = new TreeMap<String,String>();
	private boolean actionChanged = false;

	/**
	 */
	public FiniteStateMachine() {
		//
	}
	
	/** Change the code of the given action.
	 * 
	 * @param actionName is the name of the action.
	 * @param code is the code; <code>null</code> or empty to clear the code.
	 */
	public void setActionCode(String actionName, String code) {
		if (actionName==null || actionName.isEmpty()) return;
		if (code==null || code.isEmpty()) {
			String old = this.actionCodes.remove(actionName);
			if (old!=null) {
				this.actionChanged = true;
				firePropertyChanged(PROPERTY_ACTIONCODES+actionName, old, code);
			}
		}
		else {
			String old = this.actionCodes.put(actionName, code);
			if (!code.equals(old)) {
				this.actionChanged = true;
				firePropertyChanged(PROPERTY_ACTIONCODES+actionName, old, code);
			}
		}
	}

	/** Replies the code of the given action.
	 * 
	 * @param actionName is the name of the action.
	 * @return the code or <code>null</code> if none.
	 */
	public String getActionCode(String actionName) {
		if (actionName==null || actionName.isEmpty()) return null;
		return this.actionCodes.get(actionName);
	}

	/** Replies all the actions in the machine.
	 * 
	 * @return the map of "action name"/"action code".
	 */
	public Map<String,String> getActionCodes() {
		cleanActionCodes();
		return Collections.unmodifiableMap(this.actionCodes);
	}

	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		cleanActionCodes();
		for(Entry<String,String> entry : this.actionCodes.entrySet()) {
			properties.put(PROPERTY_ACTIONCODES+entry.getKey(), entry.getValue());
		}
		return properties;
	}

	private void cleanActionCodes() {
		if (this.actionChanged) {
			this.actionChanged = false;

			Set<String> actions = new TreeSet<String>(); 
			for(AbstractFSMNode node : getNodes()) {
				if (node instanceof FSMState) {
					FSMState state = (FSMState)node;
					String name = state.getEnterAction();
					if (name!=null && !name.isEmpty())
						actions.add(name);
					name = state.getAction();
					if (name!=null && !name.isEmpty())
						actions.add(name);
					name = state.getExitAction();
					if (name!=null && !name.isEmpty())
						actions.add(name);
				}
			}
			for(FSMTransition transition : getEdges()) {
				String name = transition.getAction();
				if (name!=null && !name.isEmpty())
					actions.add(name);
			}

			this.actionCodes.keySet().retainAll(actions);
		}
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			for(Entry<String,Object> prop : properties.entrySet()) {
				if (prop.getKey().startsWith(PROPERTY_ACTIONCODES)) {
					String actionName = prop.getKey().substring(PROPERTY_ACTIONCODES.length());
					Object obj = prop.getValue();
					String code = (obj==null) ? null : obj.toString();
					setActionCode(actionName, code);
				}
			}
		}
	}

}
