/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND
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

package org.arakhne.neteditor.figlayout;

import java.util.ArrayList;
import java.util.List;

import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;

/** Standard implementation of an undoable edit that is
 * able to apply a layout of figures and revert it.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public class FigureLayoutUndoableEdit extends AbstractCallableUndoableEdit {

	private static final long serialVersionUID = 5414039762016942941L;

	private final List<Change> changes = new ArrayList<Change>();

	private final String name;
	
	/**
	 * @param name is the presentation name of the edit.
	 */
	public FigureLayoutUndoableEdit(String name) {
		this.name = name;
	}

	@Override
	public String getPresentationName() {
		return this.name;
	}

	/** Add the undoable edits from the given edit.
	 * 
	 * @param edit
	 */
	public void add(Undoable edit) {
		if (edit!=null) {
			if (edit instanceof FigureLayoutUndoableEdit) {
				this.changes.addAll(((FigureLayoutUndoableEdit)edit).changes);
			}
			else {
				this.changes.add(new WrapChange(edit));
			}
		}
	}
	
	/** Add a change into the undo edit.
	 * 
	 * @param figure is the figure to move.
	 * @param x is the new position of the figure.
	 * @param y is the new position of the figure.
	 */
	public void addLocationChange(Figure figure, float x, float y) {
		this.changes.add(new PositionChange(figure, x, y));
	}

	/** Add a removal of a control point.
	 * 
	 * @param figure is the figure from witch the control point may be removed.
	 * @param position is the position of the control point to remove.
	 */
	public void addControlPointRemoval(EdgeFigure<?> figure, int position) {
		this.changes.add(new ControlPointRemoval(figure, position));
	}

	/** Replies if this edit is empty, ie it contains no change.
	 * 
	 * @return <code>true</code> if there is no change inside;
	 * otherwise <code>false</code>.
	 */
	public boolean isEmpty() {
		return this.changes.isEmpty();
	}

	@Override
	public void doEdit() {
		for(Change c : this.changes) {
			c.doEdit();
		}
	}

	@Override
	public void undoEdit() {
		for(int i=this.changes.size()-1; i>=0; --i) {
			this.changes.get(i).undoEdit();
		}
	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private static interface Change {
		
		public void doEdit();
		
		public void undoEdit();
		
	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private static class PositionChange implements Change {

		private final Figure figure;
		private final float ox;
		private final float oy;
		private final float nx;
		private final float ny;

		public PositionChange(Figure f, float x2, float y2) {
			this.figure = f;
			this.ox = f.getX();
			this.oy = f.getY();
			this.nx = x2;
			this.ny = y2;
		}

		@Override
		public void doEdit() {
			this.figure.setLocation(this.nx, this.ny);
		}

		@Override
		public void undoEdit() {
			this.figure.setLocation(this.ox, this.oy);
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private static class ControlPointRemoval implements Change {

		private final EdgeFigure<?> figure;
		private final int position;
		private final float ox;
		private final float oy;

		public ControlPointRemoval(EdgeFigure<?> f, int position) {
			this.figure = f;
			this.position = position;
			Point2D p = f.getCtrlPointAt(position);
			this.ox = p.getX();
			this.oy = p.getY();
		}
		
		@Override
		public void doEdit() {
			this.figure.removeCtrlPointAt(this.position);
		}

		@Override
		public void undoEdit() {
			this.figure.insertCtrlPointAt(this.position, this.ox, this.oy);
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private static class WrapChange implements Change {

		private final Undoable edit;
		
		/**
		 * @param edit
		 */
		public WrapChange(Undoable edit) {
			this.edit = edit;
		}

		@Override
		public void doEdit() {
			this.edit.redo();
		}

		@Override
		public void undoEdit() {
			this.edit.undo();
		}
		
	}
	
}
