/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.fsm.android ;

import org.arakhne.afc.math.continous.object2d.Circle2f;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.neteditor.android.actionmode.creation.AbstractNodeCreationMode;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.fsm.constructs.FSMStartPoint;

import android.view.ActionMode;

/** Mode to create a standard FSM mode.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class FSMStartPointCreationMode extends AbstractNodeCreationMode {

	/**
	 */
	public FSMStartPointCreationMode() {
		super(R.string.undo_fsm_start);
	}
	
	@Override
	protected void onActionBarOpened(ActionMode bar) {
		bar.setTitle(R.string.actionmode_create_fsm_start);
	}
	
	@Override
	protected Shape2f getShape(Rectangle2f bounds) {
		return new Circle2f(bounds.getCenterX(), bounds.getCenterY(),
				Math.min(bounds.getWidth(), bounds.getHeight())/2f);
	}

	@Override
	protected Node<?,?,?,?> createModelObject() {
		return new FSMStartPoint();
	}
	
}
