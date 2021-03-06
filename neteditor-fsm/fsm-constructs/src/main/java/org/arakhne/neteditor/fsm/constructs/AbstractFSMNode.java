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

import org.arakhne.neteditor.formalism.standard.StandardMonoAnchorNode;

/** Abstract implementation for all the nodes that are composed a FSM.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractFSMNode extends StandardMonoAnchorNode<FiniteStateMachine,AbstractFSMNode,FSMAnchor,FSMTransition> {

	private static final long serialVersionUID = -2088341487193767420L;

	/**
	 */
	public AbstractFSMNode() {
		this(null);
	}
	
	/**
	 * @param name
	 */
	public AbstractFSMNode(String name) {
		super(new FSMAnchor());
		setName(name);
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
	
	/** Replies the type of the FSM node.
	 * 
	 * @return the type of the FSM node.
	 */
	public abstract FSMNodeType getType();

    /**
     * {@inheritDoc}
     */
    @Override
    public String getExternalLabel() {
    	String label = super.getExternalLabel();
    	if (label==null || label.isEmpty())
    		return getType().name();
    	return label;
    }

}
