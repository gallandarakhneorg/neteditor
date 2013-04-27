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
import org.arakhne.neteditor.fig.figure.decoration.TextFigure;
import org.arakhne.vmutil.locale.Locale;

/** Property panel fortext decorations.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TextPropertyPanel extends AbstractFigurePropertyPanel<TextFigure>
implements DocumentListener {

	private static final long serialVersionUID = -478138250033796070L;
	
	private final JTextField name;
	private final JTextField text;
	private boolean update = true;
	
	/**
	 * @param figure
	 * @param undoManager
	 */
	public TextPropertyPanel(TextFigure figure, UndoManager undoManager) {
		super(TextFigure.class, undoManager);
		setLayout(new GridLayout(2, 2));
		
		JLabel label = new JLabel(Locale.getString("NAME")); //$NON-NLS-1$
		add(label);
		this.name = new JTextField();
		add(this.name);
		label.setLabelFor(this.name);
		this.name.getDocument().addDocumentListener(this);

		label = new JLabel(Locale.getString("TEXT")); //$NON-NLS-1$
		add(label);
		this.text = new JTextField();
		add(this.text);
		label.setLabelFor(this.text);
		this.text.getDocument().addDocumentListener(this);
		
		updateEnableState();
		
		setFigure(figure);
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
				String text = null;
				TextFigure figure = getFigure();
				if (figure!=null) {
					name = figure.getName();
					text = figure.getText();
				}
				this.name.setText(name);
				this.text.setText(text);
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
				TextFigure figure = getFigure();
				if (figure!=null && !figure.isLocked()) {
					String text = d.getText(0, d.getLength());
					if (d==this.name.getDocument()) {
						NameUndo undo = new NameUndo(figure, text);
						undo.doEdit();
						addUndo(undo);
					}
					else if (d==this.text.getDocument()) {
						TextUndo undo = new TextUndo(figure, text);
						undo.doEdit();
						addUndo(undo);
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
		TextFigure figure = getFigure();
		boolean enable = (figure!=null && !figure.isLocked());
		this.name.setEnabled(enable);
		this.text.setEnabled(enable);
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
	private static class TextUndo extends AbstractCallableUndoableEdit {
		
		private static final long serialVersionUID = 2662728315625765902L;
		
		private final String text;
		private final TextFigure figure;
		private String oldText;
		
		/**
		 * @param figure
		 * @param newText
		 */
		public TextUndo(TextFigure figure, String newText) {
			this.figure = figure;
			this.oldText = figure.getText();
			this.text = newText;
		}
		
		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof TextUndo) {
				TextUndo tu = (TextUndo)anEdit;
				if (this.figure==tu.figure &&
					!AbstractPropertyPanel.equals(this.text, tu.oldText, true)) {
					this.oldText = tu.oldText;
					return true;
				}
			}
			return false;
		}	

		@Override
		public void doEdit() {
			this.figure.setText(this.text);
		}
		
		@Override
		public void undoEdit() {
			this.figure.setText(this.oldText);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TextPropertyPanel.class, "UNDO_PRESENTATION", this.figure.getName()); //$NON-NLS-1$
		}
		
	}

}
