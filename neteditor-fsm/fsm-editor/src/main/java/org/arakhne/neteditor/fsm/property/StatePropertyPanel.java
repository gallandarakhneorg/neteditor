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
package org.arakhne.neteditor.fsm.property ;

import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.UndoableEdit;

import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.neteditor.fsm.constructs.FSMState;
import org.arakhne.neteditor.fsm.figures.FSMStateFigure;
import org.arakhne.vmutil.locale.Locale;

/** Property panel for FSM states.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class StatePropertyPanel extends AbstractModelPropertyPanel<FSMStateFigure,FSMState>
implements DocumentListener {

	private static final long serialVersionUID = -2172730349530540318L;

	private final JTextField name;
	private final JTextField enterAction;
	private final JTextField insideAction;
	private final JTextField exitAction;
	private boolean update = true;
	
	/**
	 * @param figure
	 * @param undoManager
	 */
	public StatePropertyPanel(FSMStateFigure figure, UndoManager undoManager) {
		super(FSMStateFigure.class, undoManager, figure);
		setLayout(new GridLayout(4, 2));
		
		JLabel label = new JLabel(Locale.getString("NAME")); //$NON-NLS-1$
		add(label);		
		this.name = new JTextField();
		add(this.name);
		label.setLabelFor(this.name);
		this.name.getDocument().addDocumentListener(this);

		label = new JLabel(Locale.getString("ENTER_ACTION")); //$NON-NLS-1$
		add(label);		
		this.enterAction = new JTextField();
		add(this.enterAction);
		label.setLabelFor(this.enterAction);
		this.enterAction.getDocument().addDocumentListener(this);

		label = new JLabel(Locale.getString("INSIDE_ACTION")); //$NON-NLS-1$
		add(label);		
		this.insideAction = new JTextField();
		add(this.insideAction);
		label.setLabelFor(this.insideAction);
		this.insideAction.getDocument().addDocumentListener(this);

		label = new JLabel(Locale.getString("EXIT_ACTION")); //$NON-NLS-1$
		add(label);		
		this.exitAction = new JTextField();
		add(this.exitAction);
		label.setLabelFor(this.exitAction);
		this.exitAction.getDocument().addDocumentListener(this);
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
				String enterAction = null;
				String insideAction = null;
				String exitAction = null;
				FSMState modelObject = getModelObject();
				if (modelObject!=null) {
					name = modelObject.getName();
					enterAction = modelObject.getEnterAction();
					insideAction = modelObject.getAction();
					exitAction = modelObject.getExitAction();
				}
				this.name.setText(name);
				this.enterAction.setText(enterAction);
				this.insideAction.setText(insideAction);
				this.exitAction.setText(exitAction);
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
				FSMStateFigure figure = getFigure();
				if (figure!=null && !figure.isLocked()) {
					FSMState modelObject = getModelObject();
					if (modelObject!=null) {
						String name = d.getText(0, d.getLength());
						if (d==this.name.getDocument()) {
							NameUndo undo = new NameUndo(figure, name);
							undo.doEdit();
							addUndo(undo);
						}
						else if (d==this.enterAction.getDocument()) {
							EnterActionUndo undo = new EnterActionUndo(modelObject, name);
							undo.doEdit();
							addUndo(undo);
						}
						else if (d==this.insideAction.getDocument()) {
							InActionUndo undo = new InActionUndo(modelObject, name);
							undo.doEdit();
							addUndo(undo);
						}
						else if (d==this.exitAction.getDocument()) {
							ExitActionUndo undo = new ExitActionUndo(modelObject, name);
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
		FSMStateFigure figure = getFigure();
		boolean enable = (figure!=null && !figure.isLocked());
		this.name.setEnabled(enable);
		this.enterAction.setEnabled(enable);
		this.insideAction.setEnabled(enable);
		this.exitAction.setEnabled(enable);
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
	private static class EnterActionUndo extends AbstractCallableUndoableEdit {
		
		private static final long serialVersionUID = 2662728315625765902L;
		
		private final String action;
		private final FSMState modelObject;
		private String oldAction;
		
		/**
		 * @param modelObject
		 * @param newAction
		 */
		public EnterActionUndo(FSMState modelObject, String newAction) {
			this.modelObject = modelObject;
			this.oldAction = modelObject.getEnterAction();
			this.action = newAction;
		}
		
		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof EnterActionUndo) {
				EnterActionUndo eau = (EnterActionUndo)anEdit;
				if (this.modelObject==eau.modelObject &&
					!AbstractPropertyPanel.equals(this.action, eau.oldAction, true)) {
					this.oldAction = eau.oldAction;
					return true;
				}
			}
			return false;
		}

		@Override
		public void doEdit() {
			this.modelObject.setEnterAction(this.action);
		}
		
		@Override
		public void undoEdit() {
			this.modelObject.setEnterAction(this.oldAction);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TransitionPropertyPanel.class, "UNDO_PRESENTATION_ENTER_ACTION", this.modelObject.getExternalLabel()); //$NON-NLS-1$
		}
		
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ExitActionUndo extends AbstractCallableUndoableEdit {
		
		private static final long serialVersionUID = 2662728315625765902L;
		
		private final String action;
		private final FSMState modelObject;
		private String oldAction;
		
		/**
		 * @param modelObject
		 * @param newAction
		 */
		public ExitActionUndo(FSMState modelObject, String newAction) {
			this.modelObject = modelObject;
			this.oldAction = modelObject.getExitAction();
			this.action = newAction;
		}
		
		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof ExitActionUndo) {
				ExitActionUndo eau = (ExitActionUndo)anEdit;
				if (this.modelObject==eau.modelObject &&
					!AbstractPropertyPanel.equals(this.action, eau.oldAction, true)) {
					this.oldAction = eau.oldAction;
					return true;
				}
			}
			return false;
		}

		@Override
		public void doEdit() {
			this.modelObject.setExitAction(this.action);
		}
		

		@Override
		public void undoEdit() {
			this.modelObject.setExitAction(this.oldAction);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TransitionPropertyPanel.class, "UNDO_PRESENTATION_EXIT_ACTION", this.modelObject.getExternalLabel()); //$NON-NLS-1$
		}
		
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class InActionUndo extends AbstractCallableUndoableEdit {
		
		private static final long serialVersionUID = 2662728315625765902L;
		
		private final String action;
		private final FSMState modelObject;
		private String oldAction;
		
		/**
		 * @param modelObject
		 * @param newAction
		 */
		public InActionUndo(FSMState modelObject, String newAction) {
			this.modelObject = modelObject;
			this.oldAction = modelObject.getAction();
			this.action = newAction;
		}
		
		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof InActionUndo) {
				InActionUndo iau = (InActionUndo)anEdit;
				if (this.modelObject==iau.modelObject &&
					!AbstractPropertyPanel.equals(this.action, iau.oldAction, true)) {
					this.oldAction = iau.oldAction;
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
		public void undoEdit() {
			this.modelObject.setAction(this.oldAction);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TransitionPropertyPanel.class, "UNDO_PRESENTATION_IN_ACTION", this.modelObject.getExternalLabel()); //$NON-NLS-1$
		}
		
	}

}
