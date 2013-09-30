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

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fsm.constructs.FSMTransition;
import org.arakhne.neteditor.fsm.figures.FSMTransitionFigure;

/** Property panel for FSM transitions.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TransitionPropertyPanel extends AbstractModelPropertyPanel<FSMTransitionFigure,FSMTransition>
implements DocumentListener {

	private static final long serialVersionUID = 8685465797607565930L;

	private final JTextField name;
	private final JTextField guard;
	private final JTextField action;
	private boolean update = true;

	/**
	 * @param figure
	 * @param undoManager
	 */
	public TransitionPropertyPanel(FSMTransitionFigure figure, UndoManager undoManager) {
		super(FSMTransitionFigure.class, undoManager, figure);
		setLayout(new GridLayout(3, 2));

		JLabel label = new JLabel(Locale.getString("NAME")); //$NON-NLS-1$
		add(label);
		this.name = new JTextField();
		add(this.name);
		label.setLabelFor(this.name);
		this.name.getDocument().addDocumentListener(this);

		label = new JLabel(Locale.getString("GUARD")); //$NON-NLS-1$
		add(label);
		this.guard = new JTextField();
		add(this.guard);
		label.setLabelFor(this.guard);
		this.guard.getDocument().addDocumentListener(this);

		label = new JLabel(Locale.getString("ACTION")); //$NON-NLS-1$
		add(label);
		this.action = new JTextField();
		add(this.action);
		label.setLabelFor(this.action);
		this.action.getDocument().addDocumentListener(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void updateContent() {
		if (this.update) {
			this.update = false;
			try {
				String name = null;
				String guard = null;
				String action = null;
				FSMTransition modelObject = getModelObject();
				if (modelObject!=null) {
					name = modelObject.getName();
					guard = modelObject.getGuard();
					action = modelObject.getAction();
				}
				this.name.setText(name);
				this.guard.setText(guard);
				this.action.setText(action);
				updateEnableState();
			}
			finally {
				this.update = true;
			}	
		}
	}

	private synchronized void updateFromText(Document d) {
		if (this.update) {
			this.update = false;
			try {
				FSMTransitionFigure figure = getFigure();
				if (figure!=null && !figure.isLocked()) {
					FSMTransition modelObject = getModelObject();
					if (modelObject!=null) {
						String text = d.getText(0, d.getLength());
						if (d==this.name.getDocument()) {
							NameUndo undo = new NameUndo(figure, text);
							undo.doEdit();
							addUndo(undo);
						}
						else if (d==this.guard.getDocument()) {
							GuardUndo undo = new GuardUndo(modelObject, text);
							undo.doEdit();
							addUndo(undo);
						}
						else if (d==this.action.getDocument()) {
							ActionUndo undo = new ActionUndo(modelObject, text);
							undo.doEdit();
							addUndo(undo);
						}
					}
				}
			}
			catch (BadLocationException _) {
				//
			}
			finally {
				this.update = true;
			}	
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected synchronized void updateEnableState() {
		FSMTransitionFigure figure = getFigure();
		boolean enable = (figure!=null && !figure.isLocked());
		this.name.setEnabled(enable);
		this.guard.setEnabled(enable);
		this.action.setEnabled(enable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertUpdate(DocumentEvent e) {
		updateFromText(e.getDocument());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeUpdate(DocumentEvent e) {
		updateFromText(e.getDocument());

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void changedUpdate(DocumentEvent e) {
		updateFromText(e.getDocument());

	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class GuardUndo extends AbstractCallableUndoableEdit {
		
		private static final long serialVersionUID = 2662728315625765902L;
		
		private final String guard;
		private final FSMTransition modelObject;
		private String oldGuard;
		
		/**
		 * @param modelObject
		 * @param newGuard
		 */
		public GuardUndo(FSMTransition modelObject, String newGuard) {
			this.modelObject = modelObject;
			this.oldGuard = modelObject.getGuard();
			this.guard = newGuard;
		}
		
		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof GuardUndo) {
				GuardUndo gu = (GuardUndo)anEdit;
				if (this.modelObject==gu.modelObject &&
					!AbstractPropertyPanel.equals(this.guard, gu.oldGuard, true)) {
					this.oldGuard = gu.oldGuard;
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void doEdit() {
			this.modelObject.setGuard(this.guard);
		}
		
		@Override
		public void undoEdit() {
			this.modelObject.setGuard(this.oldGuard);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TransitionPropertyPanel.class, "UNDO_PRESENTATION_GUARD", this.modelObject.getExternalLabel()); //$NON-NLS-1$
		}
		
	}
	
	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ActionUndo extends AbstractCallableUndoableEdit {
		
		private static final long serialVersionUID = 2662728315625765902L;
		
		private final String action;
		private final FSMTransition modelObject;
		private String oldAction;
		
		/**
		 * @param modelObject
		 * @param newAction
		 */
		public ActionUndo(FSMTransition modelObject, String newAction) {
			this.modelObject = modelObject;
			this.oldAction = modelObject.getAction();
			this.action = newAction;
		}
		
		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof ActionUndo) {
				ActionUndo au = (ActionUndo)anEdit;
				if (this.modelObject==au.modelObject &&
					!AbstractPropertyPanel.equals(this.action, au.oldAction, true)) {
					this.oldAction = au.oldAction;
					return true;
				}
			}
			return false;
		}
		
		@Override
		public void doEdit() {
			this.modelObject.setAction(this.action);
		}
		
		@Override
		public void undoEdit() throws CannotUndoException {
			this.modelObject.setAction(this.oldAction);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TransitionPropertyPanel.class, "UNDO_PRESENTATION_ACTION", this.modelObject.getExternalLabel()); //$NON-NLS-1$
		}
		
	}

}
