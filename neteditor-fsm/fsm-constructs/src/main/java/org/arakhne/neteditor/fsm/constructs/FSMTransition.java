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

import java.util.Map;

import org.arakhne.neteditor.formalism.standard.StandardEdge;

/** Define a transition in a finite state machine.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMTransition extends StandardEdge<FiniteStateMachine,AbstractFSMNode,FSMAnchor,FSMTransition> {

	private static final long serialVersionUID = 2244399983514034328L;
	
	/** */
	public static final String PROPERTY_GUARD = "guard";  //$NON-NLS-1$
	/** */
	public static final String PROPERTY_ACTION = "action";  //$NON-NLS-1$

	private String action = null;
	private String guard = null;
	
	/**
	 */
	public FSMTransition() {
		//
	}
	
	/** Replies the code of the action with the given name.
	 * 
	 * @param actionName
	 * @return the code or <code>null</code>.
	 */
	protected String getActionCode(String actionName) {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			return machine.getActionCode(actionName);
		}
		return null;
	}

	/** Change the node from which this transition is starting.
	 * 
	 * @param node
	 */
	public void setStartNode(FSMState node) {
		FSMAnchor anchor = null;
		if (node!=null && !node.getAnchors().isEmpty()) {
			anchor = node.getAnchors().get(0);
		}
		setStartAnchor(anchor);
	}
	
	/** Change the node to which this transition is finishing.
	 * 
	 * @param node
	 */
	public void setEndNode(FSMState node) {
		FSMAnchor anchor = null;
		if (node!=null && !node.getAnchors().isEmpty()) {
			anchor = node.getAnchors().get(0);
		}
		setEndAnchor(anchor);
	}

	/** Replies the action associated to this transition.
	 * 
	 * @return the action of the transition.
	 */
	public String getAction() {
		return this.action;
	}

	/** Replies the code associated to this transition.
	 * 
	 * @return the code of the action of the transition.
	 */
	public String getActionCode() {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			return machine.getActionCode(getAction());
		}
		return null;
	}

	/** Set the action associated to this transition.
	 * 
	 * @param action is the action of the transition.
	 */
	public void setAction(String action) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.action==null && na!=null) ||
			(this.action!=null && !this.action.equals(na))) {
			String old = this.action;
			this.action = na;
			ensureActionCode(old, action);
			firePropertyChanged(PROPERTY_ACTION, old, this.action); 
		}
	}

	/** Set the action associated to this transition.
	 * 
	 * @param action is the action of the transition.
	 * @param code is the code of the action.
	 */
	public void setAction(String action, String code) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.action==null && na!=null) ||
			(this.action!=null && !this.action.equals(na))) {
			String old = this.action;
			this.action = na;
			setActionCode(code);
			firePropertyChanged(PROPERTY_ACTION, old, this.action); 
		}
		else {
			setActionCode(code);
		}
	}

	/** Be sure tha the old code is copied if the new action has no code.
	 * This is helpful when renaming an action.
	 * 
	 * @param oldAction
	 * @param newAction
	 */
	private void ensureActionCode(String oldAction, String newAction) {
		String newCode = getActionCode(newAction);
		if (newCode==null) {
			String oldCode = getActionCode(oldAction);
			if(oldCode!=null) {
				setActionCode(oldCode);
			}
		}
	}

	/** Replies the code associated to this transition.
	 * 
	 * @param code is the code of the action of the transition.
	 */
	public void setActionCode(String code) {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			machine.setActionCode(getAction(), code);
		}
	}

	/** Replies the guard associated to this transition.
	 * 
	 * @return the guard of the transition.
	 */
	public String getGuard() {
		return this.guard;
	}

	/** Replies the gaurd associated to this transition.
	 * 
	 * @param guard is the guard of the transition.
	 */
	public void setGuard(String guard) {
		String ng = (guard==null || guard.isEmpty()) ? null : guard;
		if ((this.guard==null && ng!=null) ||
			(this.guard!=null && !this.guard.equals(ng))) {
			String old = this.guard;
			this.guard = ng;
			firePropertyChanged(PROPERTY_GUARD, old, this.guard); 
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_ACTION, this.action); 
		properties.put(PROPERTY_GUARD, this.guard); 
		return properties;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = super.getUIEditableProperties();
		properties.put(PROPERTY_ACTION, String.class); 
		properties.put(PROPERTY_GUARD, String.class); 
    	return properties;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setAction(propGetString(PROPERTY_ACTION, this.action, false, properties)); 
			setGuard(propGetString(PROPERTY_GUARD, this.guard, false, properties)); 
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getExternalLabel() {
		StringBuilder label = new StringBuilder();
		String guard = getGuard();
		if (guard!=null) {
			label.append(guard);
		}
		String action = getAction();
		if (action!=null) {
			label.append("/"); //$NON-NLS-1$
			label.append(action);
		}
		return label.toString();
	}
	
}
