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

import java.util.Map;

/** Define a state in a finite state machine.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMState extends AbstractFSMNode {

	private static final long serialVersionUID = 7738270909000339049L;

	/** */
	public static final String PROPERTY_ENTERACTION = "enterAction";  //$NON-NLS-1$
	/** */
	public static final String PROPERTY_INSIDEACTION = "insideAction";  //$NON-NLS-1$
	/** */
	public static final String PROPERTY_EXITACTION = "exitAction";  //$NON-NLS-1$
	/** */
	public static final String PROPERTY_ACCEPTING = "accepting";  //$NON-NLS-1$

	private String enterAction = null;
	private String insideAction = null;
	private String exitAction = null;
	private boolean isAccepting = false;

	/**
	 */
	public FSMState() {
		super();
	}
	
	/**
	 * @param name
	 */
	public FSMState(String name) {
		super(name);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final FSMNodeType getType() {
		return FSMNodeType.STATE;
	}
	
	/** Replies is the state is accepting.
	 * 
	 * @return <code>true</code> if the state is accepting;
	 * <code>false</code> if it is not accepting.
	 */
	public boolean isAccepting() {
		return this.isAccepting;
	}
	
	/** Set is the state is accepting.
	 * 
	 * @param accepting is <code>true</code> if the state is accepting;
	 * <code>false</code> if it is not accepting.
	 */
	public void setAccepting(boolean accepting) {
		if (this.isAccepting!=accepting) {
			boolean old = this.isAccepting;
			this.isAccepting = accepting;
			firePropertyChanged(PROPERTY_ACCEPTING, old, this.isAccepting);
		}
	}

	/** Replies the action that should be executed when entering in this state.
	 * 
	 * @return the action to execute when entering in the state.
	 */
	public String getEnterAction() {
		return this.enterAction;
	}

	/** Replies the code that should be executed when entering in this state.
	 * 
	 * @return the code of the action to execute when entering in the state.
	 */
	public String getEnterActionCode() {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			return machine.getActionCode(getEnterAction());
		}
		return null;
	}

	/** Set the action that should be executed when entering in this state.
	 * 
	 * @param action is the action to execute when entering in the state.
	 */
	public void setEnterAction(String action) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.enterAction==null && na!=null)||
			(this.enterAction!=null && !this.enterAction.equals(na))) {
			String old = this.enterAction;
			this.enterAction = na;
			ensureActionCode(old, action);
			firePropertyChanged(PROPERTY_ENTERACTION, old, this.enterAction);
		}
	}
	
	/** Set the action that should be executed when entering in this state.
	 * 
	 * @param action is the action to execute when entering in the state.
	 * @param code is the code of the action.
	 */
	public void setEnterAction(String action, String code) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.enterAction==null && na!=null)||
			(this.enterAction!=null && !this.enterAction.equals(na))) {
			String old = this.enterAction;
			this.enterAction = na;
			setActionCode(code);
			firePropertyChanged(PROPERTY_ENTERACTION, old, this.enterAction);
		}
		else {
			setActionCode(code);
		}
	}

	/** Set the code that should be executed when entering in this state.
	 * 
	 * @param code is the code of the action to execute when entering in the state.
	 */
	public void setEnterActionCode(String code) {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			machine.setActionCode(getEnterAction(), code);
		}
	}

	/** Replies the action that should be executed when exiting from this state.
	 * 
	 * @return the action to execute when exiting from the state.
	 */
	public String getExitAction() {
		return this.exitAction;
	}

	/** Replies the code that should be executed when exiting from this state.
	 * 
	 * @return the code of the action to execute when exiting from the state.
	 */
	public String getExitActionCode() {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			return machine.getActionCode(getExitAction());
		}
		return null;
	}

	/** Set the action that should be executed when exiting in this state.
	 * 
	 * @param action is the action to execute when exiting in the state.
	 */
	public void setExitAction(String action) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.exitAction==null && na!=null)||
			(this.exitAction!=null && !this.exitAction.equals(na))) {
			String old = this.exitAction;
			this.exitAction = na;
			ensureActionCode(old, action);
			firePropertyChanged(PROPERTY_EXITACTION, old, this.exitAction); 
		}
	}

	/** Set the action that should be executed when exiting in this state.
	 * 
	 * @param action is the action to execute when exiting in the state.
	 * @param code is the code of the action.
	 */
	public void setExitAction(String action, String code) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.exitAction==null && na!=null)||
			(this.exitAction!=null && !this.exitAction.equals(na))) {
			String old = this.exitAction;
			this.exitAction = na;
			setActionCode(code);
			firePropertyChanged(PROPERTY_EXITACTION, old, this.exitAction); 
		}
		else {
			setActionCode(code);
		}
	}

	/** Set the code that should be executed when exiting in this state.
	 * 
	 * @param code is the code of the action to execute when exiting in the state.
	 */
	public void setExitActionCode(String code) {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			machine.setActionCode(getExitAction(), code);
		}
	}

	/** Replies the action that should be executed when in this state.
	 * 
	 * @return the action to execute when exiting in the state.
	 */
	public String getAction() {
		return this.insideAction;
	}

	/** Replies the code that should be executed when in this state.
	 * 
	 * @return the code of the action to execute when exiting in the state.
	 */
	public String getActionCode() {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			return machine.getActionCode(getAction());
		}
		return null;
	}

	/** Set the action that should be executed when in this state.
	 * 
	 * @param action is the action to execute when in the state.
	 */
	public void setAction(String action) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.insideAction==null && na!=null)||
			(this.insideAction!=null && !this.insideAction.equals(na))) {
			String old = this.insideAction;
			this.insideAction = na;
			ensureActionCode(old, action);
			firePropertyChanged(PROPERTY_INSIDEACTION, old, this.insideAction); 
		}
	}
	
	/** Set the action that should be executed when in this state.
	 * 
	 * @param action is the action to execute when in the state.
	 * @param code is the code of the action.
	 */
	public void setAction(String action, String code) {
		String na = (action==null || action.isEmpty()) ? null : action;
		if ((this.insideAction==null && na!=null)||
			(this.insideAction!=null && !this.insideAction.equals(na))) {
			String old = this.insideAction;
			this.insideAction = na;
			setActionCode(code);
			firePropertyChanged(PROPERTY_INSIDEACTION, old, this.insideAction); 
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

	/** Set the code that should be executed when in this state.
	 * 
	 * @param code is the code of the action to execute when in the state.
	 */
	public void setActionCode(String code) {
		FiniteStateMachine machine = getGraph();
		if (machine!=null) {
			machine.setActionCode(getAction(), code);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_ENTERACTION, this.enterAction); 
		properties.put(PROPERTY_INSIDEACTION, this.insideAction); 
		properties.put(PROPERTY_EXITACTION, this.exitAction); 
		properties.put(PROPERTY_ACCEPTING, this.isAccepting); 
		return properties;
	}
	
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Class<?>> getUIEditableProperties() {
		Map<String,Class<?>> properties = super.getUIEditableProperties();
		properties.put(PROPERTY_ENTERACTION, String.class); 
		properties.put(PROPERTY_INSIDEACTION, String.class); 
		properties.put(PROPERTY_EXITACTION, String.class); 
		properties.put(PROPERTY_ACCEPTING, Boolean.class); 
    	return properties;
    }

    /**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setEnterAction(propGetString(PROPERTY_ENTERACTION, this.enterAction, false, properties)); 
			setExitAction(propGetString(PROPERTY_EXITACTION, this.exitAction, false, properties)); 
			setAction(propGetString(PROPERTY_INSIDEACTION, this.insideAction, false, properties)); 
			setAccepting(propGetBoolean(PROPERTY_ACCEPTING, this.isAccepting, properties)); 
		}
	}
	
}
