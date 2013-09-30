/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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
package org.arakhne.neteditor.fsm.property ;

import javax.swing.undo.UndoableEdit;

import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.Figure;

/** Undo action for names of figures.
 * 
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
class NameUndo extends AbstractCallableUndoableEdit {

	private static final long serialVersionUID = 2782700349201513790L;

	private final String name;
	private final Figure figure;
	private String oldName;

	/**
	 * @param figure
	 * @param newName
	 */
	public NameUndo(Figure figure, String newName) {
		this.figure = figure;
		this.oldName = figure.getName();
		this.name = newName;
	}
	
	@Override
	public boolean replaceEdit(UndoableEdit anEdit) {
		if (anEdit instanceof NameUndo) {
			NameUndo nu = (NameUndo)anEdit;
			if (this.figure==nu.figure &&
				!AbstractPropertyPanel.equals(this.name, nu.oldName, true)) {
				this.oldName = nu.oldName;
				return true;
			}
		}
		return false;
	}	
	
	@Override
	public void doEdit() {
		this.figure.setName(this.name);
	}

	@Override
	public void undoEdit() {
		this.figure.setName(this.oldName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPresentationName() {
		return Locale.getString("UNDO_PRESENTATION", this.figure.getName()); //$NON-NLS-1$
	}

}
