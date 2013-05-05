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
package org.arakhne.neteditor.fsm.property ;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

import org.arakhne.afc.ui.swing.JColorSelector;
import org.arakhne.afc.ui.swing.JColorSelector.ColorButtonModel;
import org.arakhne.afc.ui.swing.JColorSelector.ColorButtonModelListener;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.figure.decoration.PolylineFigure;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.vmutil.locale.Locale;

/** Property panel for polyline and polygon decorations.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PolylinePropertyPanel extends AbstractFigurePropertyPanel<PolylineFigure>
implements DocumentListener, ActionListener, ColorButtonModelListener {

	private static final long serialVersionUID = -2657161460405020495L;

	private final JTextField name;
	private final JCheckBox polygon;
	private final JColorSelector lineColor;
	private final JColorSelector fillColor;
	private boolean update = true;

	/**
	 * @param figure
	 * @param undoManager
	 */
	public PolylinePropertyPanel(PolylineFigure figure, UndoManager undoManager) {
		super(PolylineFigure.class, undoManager);
		setLayout(new GridLayout(4, 2));

		JLabel label = new JLabel(Locale.getString("NAME")); //$NON-NLS-1$
		add(label);
		this.name = new JTextField();
		add(this.name);
		label.setLabelFor(this.name);
		this.name.getDocument().addDocumentListener(this);

		label = new JLabel(Locale.getString("POLYGON")); //$NON-NLS-1$
		add(label);
		this.polygon = new JCheckBox();
		add(this.polygon);
		label.setLabelFor(this.polygon);
		this.polygon.addActionListener(this);

		label = new JLabel(Locale.getString("LINE_COLOR")); //$NON-NLS-1$
		add(label);
		this.lineColor = new JColorSelector();
		add(this.lineColor);
		label.setLabelFor(this.lineColor);
		this.lineColor.getColorButtonModel().addColorButtonModelListener(this);

		label = new JLabel(Locale.getString("FILL_COLOR")); //$NON-NLS-1$
		add(label);
		this.fillColor = new JColorSelector();
		add(this.fillColor);
		label.setLabelFor(this.fillColor);
		this.fillColor.getColorButtonModel().addColorButtonModelListener(this);

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
				boolean closed = false;
				Color lineColor = null;
				Color fillColor = null;
				PolylineFigure figure = getFigure();
				if (figure!=null) {
					name = figure.getName();
					closed = figure.isClosed();
					lineColor = VectorToolkit.nativeUIObject(Color.class, figure.getLineColor());
					fillColor = VectorToolkit.nativeUIObject(Color.class, figure.getFillColor());
				}
				this.name.setText(name);
				this.polygon.setSelected(closed);
				this.lineColor.setSelectedColor(lineColor==null ?
						VectorToolkit.nativeUIObject(Color.class, ViewComponentConstants.DEFAULT_LINE_COLOR)
						: lineColor);
				this.fillColor.setSelectedColor(fillColor==null ?
						VectorToolkit.nativeUIObject(Color.class, ViewComponentConstants.DEFAULT_FILL_COLOR)
						: fillColor);
				updateEnableState();
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
		PolylineFigure figure = getFigure();
		boolean enable = (figure!=null && !figure.isLocked());
		this.name.setEnabled(enable);
		this.polygon.setEnabled(enable);
		this.lineColor.setEnabled(enable);
		this.fillColor.setEnabled(enable && figure!=null && figure.isClosed());
	}

	private synchronized void updateFromText(Document d) {
		if (this.update) {
			this.update = false;
			try {
				PolylineFigure figure = getFigure();
				if (figure!=null && !figure.isLocked()) {
					String text = d.getText(0, d.getLength());
					if (d==this.name.getDocument()) {
						NameUndo undo = new NameUndo(figure, text);
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
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void actionPerformed(ActionEvent e) {
		if (this.update) {
			this.update = false;
			try {
				if (e.getSource()==this.polygon) {
					PolylineFigure figure = getFigure();
					if (figure!=null && !figure.isLocked()) {
						boolean polygon = this.polygon.isSelected();
						CloseFlagUndo undo = new CloseFlagUndo(figure, polygon);
						undo.doEdit();
						addUndo(undo);
						updateEnableState();
					}
				}
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
	public synchronized void onColorChange(ColorButtonModel source, Color oldColor,
			Color newColor) {
		if (this.update) {
			this.update = false;
			try {
				PolylineFigure figure = getFigure();
				if (figure!=null && !figure.isLocked()) {
					if (source==this.lineColor.getColorButtonModel()) {
						ColorUndo undo = new ColorUndo(figure,
								VectorToolkit.color(this.lineColor.getSelectedColor()),
								true);
						undo.doEdit();
						addUndo(undo);
					}
					else if (source==this.fillColor.getColorButtonModel()) {
						ColorUndo undo = new ColorUndo(figure,
								VectorToolkit.color(this.fillColor.getSelectedColor()),
								false);
						undo.doEdit();
						addUndo(undo);
					}
				}
			}
			finally {
				this.update = true;
			}	
		}
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ColorUndo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = 2662728315625765902L;

		private final org.arakhne.afc.ui.vector.Color color;
		private final PolylineFigure figure;
		private final boolean isOutline;
		private org.arakhne.afc.ui.vector.Color oldColor;

		/**
		 * @param figure
		 * @param newColor
		 * @param isOutline
		 */
		public ColorUndo(PolylineFigure figure, org.arakhne.afc.ui.vector.Color newColor, boolean isOutline) {
			this.isOutline = isOutline;
			this.figure = figure;
			this.oldColor = isOutline ? figure.getLineColor() : figure.getFillColor();
			this.color = newColor;
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof ColorUndo) {
				ColorUndo cu = (ColorUndo)anEdit;
				if (this.figure==cu.figure && this.isOutline==cu.isOutline) {
					this.oldColor = cu.oldColor;
					return true;
				}
			}
			return false;
		}

		@Override
		public void doEdit() {
			if (this.isOutline)
				this.figure.setLineColor(this.color);
			else
				this.figure.setFillColor(this.color);
		}

		@Override
		public void undoEdit() {
			if (this.isOutline)
				this.figure.setLineColor(this.oldColor);
			else
				this.figure.setFillColor(this.oldColor);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TransitionPropertyPanel.class, 
					this.isOutline ?
							"UNDO_PRESENTATION_OUTLINE_COLOR" : //$NON-NLS-1$ 
								"UNDO_PRESENTATION_FILL_COLOR", //$NON-NLS-1$
								this.figure.getName());
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class CloseFlagUndo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = 2662728315625765902L;

		private final boolean isClosed;
		private final PolylineFigure figure;

		/**
		 * @param figure
		 * @param isClosed
		 */
		public CloseFlagUndo(PolylineFigure figure, boolean isClosed) {
			this.figure = figure;
			this.isClosed = isClosed;
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof CloseFlagUndo) {
				CloseFlagUndo cfu = (CloseFlagUndo)anEdit;
				if (this.figure==cfu.figure && this.isClosed==cfu.isClosed) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void doEdit() {
			this.figure.setClosed(this.isClosed);
			this.figure.setFilled(this.isClosed);
		}

		@Override
		public void undoEdit() throws CannotUndoException {
			this.figure.setClosed(!this.isClosed);
			this.figure.setFilled(!this.isClosed);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(TransitionPropertyPanel.class, 
					this.isClosed ?
							"UNDO_PRESENTATION_CLOSE" : //$NON-NLS-1$
								"UNDO_PRESENTATION_UNCLOSE", //$NON-NLS-1$
								this.figure.getName());
		}

	}

}
