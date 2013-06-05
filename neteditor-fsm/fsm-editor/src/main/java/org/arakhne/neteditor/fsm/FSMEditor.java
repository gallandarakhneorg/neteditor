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
package org.arakhne.neteditor.fsm ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.FlavorEvent;
import java.awt.datatransfer.FlavorListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.undo.UndoableEdit;

import org.arakhne.afc.io.filefilter.FileFilter;
import org.arakhne.afc.io.filefilter.JavaFileFilter;
import org.arakhne.afc.io.filefilter.NGRFileFilter;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.ui.MouseCursor;
import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeListener;
import org.arakhne.afc.ui.actionmode.SelectableInteractionEvent;
import org.arakhne.afc.ui.actionmode.SelectableInteractionListener;
import org.arakhne.afc.ui.awt.AwtUtil;
import org.arakhne.afc.ui.awt.ExceptionListener;
import org.arakhne.afc.ui.event.PointerEvent;
import org.arakhne.afc.ui.selection.Selectable;
import org.arakhne.afc.ui.selection.SelectionEvent;
import org.arakhne.afc.ui.selection.SelectionListener;
import org.arakhne.afc.ui.swing.FileFilterSwing;
import org.arakhne.afc.ui.swing.JGroupButton;
import org.arakhne.afc.ui.swing.StandardAction;
import org.arakhne.afc.ui.swing.progress.ProgressMonitor;
import org.arakhne.afc.ui.swing.undo.AbstractCallableUndoableEdit;
import org.arakhne.afc.ui.swing.undo.UndoableAction;
import org.arakhne.afc.ui.undo.UndoListener;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.PolylineFigure;
import org.arakhne.neteditor.fig.figure.decoration.TextFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.edge.PolylineEdgeFigure;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.view.DrawingMethod;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.figlayout.FigureLayoutDirection;
import org.arakhne.neteditor.figlayout.force.ForceBasedFigureLayout;
import org.arakhne.neteditor.figlayout.sugiyama.GanswerSugiyamaFigureLayout;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.ModelObjectListener;
import org.arakhne.neteditor.fsm.about.JAboutDialog;
import org.arakhne.neteditor.fsm.constructs.FiniteStateMachine;
import org.arakhne.neteditor.fsm.constructs.java.FSMJavaGenerator;
import org.arakhne.neteditor.fsm.figures.FSMFigureFactory;
import org.arakhne.neteditor.fsm.figures.FSMStateFigure;
import org.arakhne.neteditor.fsm.figures.FSMTransitionFigure;
import org.arakhne.neteditor.fsm.property.AbstractPropertyPanel;
import org.arakhne.neteditor.fsm.property.EmptyPropertyPanel;
import org.arakhne.neteditor.fsm.property.PolylinePropertyPanel;
import org.arakhne.neteditor.fsm.property.StatePropertyPanel;
import org.arakhne.neteditor.fsm.property.TextPropertyPanel;
import org.arakhne.neteditor.fsm.property.TransitionPropertyPanel;
import org.arakhne.neteditor.io.BitmapExporter;
import org.arakhne.neteditor.io.FileCollection;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.NetEditorReader;
import org.arakhne.neteditor.io.VectorialExporter;
import org.arakhne.neteditor.io.VectorialPictureFileType;
import org.arakhne.neteditor.io.bitmap.ImageIOBitmapExporter;
import org.arakhne.neteditor.io.bitmap.ImageType;
import org.arakhne.neteditor.io.eps.EpsExporter;
import org.arakhne.neteditor.io.eps.EpsTeXExporter;
import org.arakhne.neteditor.io.gml.GMLExporter;
import org.arakhne.neteditor.io.graphml.GraphMLExporter;
import org.arakhne.neteditor.io.graphviz.DotExporter;
import org.arakhne.neteditor.io.gxl.GXLExporter;
import org.arakhne.neteditor.io.ngr.NGRReader;
import org.arakhne.neteditor.io.ngr.NGRWriter;
import org.arakhne.neteditor.io.pdf.PdfExporter;
import org.arakhne.neteditor.io.pdf.PdfTeXExporter;
import org.arakhne.neteditor.io.svg.SvgExporter;
import org.arakhne.neteditor.swing.JFigureViewer;
import org.arakhne.neteditor.swing.actionmode.creation.BitmapDecorationCreationMode;
import org.arakhne.neteditor.swing.actionmode.creation.EllipseDecorationCreationMode;
import org.arakhne.neteditor.swing.actionmode.creation.PdfDecorationCreationMode;
import org.arakhne.neteditor.swing.actionmode.creation.PolygonDecorationCreationMode;
import org.arakhne.neteditor.swing.actionmode.creation.PolylineDecorationCreationMode;
import org.arakhne.neteditor.swing.actionmode.creation.RectangleDecorationCreationMode;
import org.arakhne.neteditor.swing.actionmode.creation.TextDecorationCreationMode;
import org.arakhne.neteditor.swing.event.FigureEvent;
import org.arakhne.neteditor.swing.event.FigureListener;
import org.arakhne.neteditor.swing.selection.JSelectionManager;
import org.arakhne.vmutil.FileSystem;
import org.arakhne.vmutil.Resources;
import org.arakhne.vmutil.locale.Locale;

/** This is a simple Finite State Machine editor based on
 * the NetEditor API
 *  
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMEditor extends JFrame {

	private static final long serialVersionUID = 7892228922433146101L;

	private static final boolean MODE_PERSISTENCE = true;

	private static final URL LOAD_ICON = Resources.getResource(FSMEditor.class, "load.png"); //$NON-NLS-1$
	private static final URL SAVE_ICON = Resources.getResource(FSMEditor.class, "save.png"); //$NON-NLS-1$
	private static final URL SAVEAS_ICON = Resources.getResource(FSMEditor.class, "save-as.png"); //$NON-NLS-1$
	private static final URL EXPORT_IMAGE_ICON = Resources.getResource(FSMEditor.class, "export_image.png"); //$NON-NLS-1$
	private static final URL UNDO_ICON = Resources.getResource(FSMEditor.class, "undo.png"); //$NON-NLS-1$
	private static final URL REDO_ICON = Resources.getResource(FSMEditor.class, "redo.png"); //$NON-NLS-1$
	private static final URL MOUSE_ICON = Resources.getResource(FSMEditor.class, "mouse.png"); //$NON-NLS-1$
	private static final URL STATE_ICON = Resources.getResource(FSMEditor.class, "newstate.png"); //$NON-NLS-1$
	private static final URL START_POINT_ICON = Resources.getResource(FSMEditor.class, "startpoint.png"); //$NON-NLS-1$
	private static final URL END_POINT_ICON = Resources.getResource(FSMEditor.class, "endpoint.png"); //$NON-NLS-1$
	private static final URL TRANSITION_ICON = Resources.getResource(FSMEditor.class, "transition.png"); //$NON-NLS-1$
	private static final URL ELLIPSE_ICON = Resources.getResource(FSMEditor.class, "ellipse.png"); //$NON-NLS-1$
	private static final URL RECTANGLE_ICON = Resources.getResource(FSMEditor.class, "rectangle.png"); //$NON-NLS-1$
	private static final URL BITMAP_ICON = Resources.getResource(FSMEditor.class, "bitmap.png"); //$NON-NLS-1$
	private static final URL PDF_ICON = Resources.getResource(FSMEditor.class, "pdf.png"); //$NON-NLS-1$
	private static final URL TEXT_ICON = Resources.getResource(FSMEditor.class, "text.png"); //$NON-NLS-1$
	private static final URL POLYLINE_ICON = Resources.getResource(FSMEditor.class, "polyline.png"); //$NON-NLS-1$
	private static final URL POLYGON_ICON = Resources.getResource(FSMEditor.class, "polygon.png"); //$NON-NLS-1$
	private static final URL MOVE_TOP_ICON = Resources.getResource(FSMEditor.class, "layer_move_top.png"); //$NON-NLS-1$
	private static final URL MOVE_BOTTOM_ICON = Resources.getResource(FSMEditor.class, "layer_move_bottom.png"); //$NON-NLS-1$
	private static final URL MOVE_UP_ICON = Resources.getResource(FSMEditor.class, "layer_move_up.png"); //$NON-NLS-1$
	private static final URL MOVE_DOWN_ICON = Resources.getResource(FSMEditor.class, "layer_move_down.png"); //$NON-NLS-1$
	private static final URL COPY_ICON = Resources.getResource(FSMEditor.class, "copy.png"); //$NON-NLS-1$
	private static final URL CUT_ICON = Resources.getResource(FSMEditor.class, "cut.png"); //$NON-NLS-1$
	private static final URL PASTE_ICON = Resources.getResource(FSMEditor.class, "paste.png"); //$NON-NLS-1$
	private static final URL PRINT_ICON = Resources.getResource(FSMEditor.class, "print.png"); //$NON-NLS-1$
	private static final URL LOGO_ICON = Resources.getResource(FSMEditor.class, "arakhne_logo.png"); //$NON-NLS-1$
	private static final URL ABOUT_ICON = Resources.getResource(FSMEditor.class, "about.png"); //$NON-NLS-1$
	private static final URL LOCK_ICON = Resources.getResource(FSMEditor.class, "lock.png"); //$NON-NLS-1$
	private static final URL UNLOCK_ICON = Resources.getResource(FSMEditor.class, "unlock.png"); //$NON-NLS-1$
	private static final URL H_LAYER_LAYOUT_ICON = Resources.getResource(FSMEditor.class, "h_layers.png"); //$NON-NLS-1$
	private static final URL V_LAYER_LAYOUT_ICON = Resources.getResource(FSMEditor.class, "v_layers.png"); //$NON-NLS-1$
	private static final URL FORCE_LAYOUT_ICON = Resources.getResource(FSMEditor.class, "force_layout.png"); //$NON-NLS-1$

	/** Load the icon at the specified location.
	 * 
	 * @param url
	 * @return the icon.
	 * @throws IOException
	 */
	static Icon loadIcon(URL url) throws IOException {
		if (url==null) throw new FileNotFoundException();
		Image image = ImageIO.read(url);
		return new ImageIcon(image);
	}

	/** Create a toolbar button.
	 * 
	 * @param action
	 * @return the button.
	 */
	static JButton makeToolbarButton(Action action) {
		JButton b = new JButton(action);
		b.setHideActionText(true);
		return b;
	}

	/** Create a toolbar button.
	 * 
	 * @param action
	 * @param buttons
	 * @return the button
	 */
	static JToggleButton makeToolbarToggleButton(Action action, Collection<AbstractButton> buttons) {
		JToggleButton b = new JToggleButton(action);
		b.setHideActionText(true);
		buttons.add(b);
		return b;
	}

	/** Create a menu item.
	 * 
	 * @param action
	 * @param buttons
	 * @return the button.
	 */
	static JRadioButtonMenuItem makeMenuToggleButton(Action action, Collection<AbstractButton> buttons) {
		JRadioButtonMenuItem b = new JRadioButtonMenuItem(action);
		buttons.add(b);
		return b;
	}

	/** Create a menu item with a shotcut key. 
	 * 
	 * @param action
	 * @param accelerator
	 * @return the menu item.
	 */
	static JMenuItem makeMenuItem(Action action, KeyStroke accelerator) {
		JMenuItem item = new JMenuItem(action);
		item.setAccelerator(accelerator);
		return item;
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		FSMEditor editor = new FSMEditor();
		editor.setVisible(true);
	}

	private final Action aboutAction = new AboutAction();
	private final Action quitAction = new QuitAction();
	private final Action newAction = new NewAction();
	private final Action loadAction = new LoadAction();
	private final Action saveAction = new SaveAction();
	private final Action saveAsAction = new SaveAsAction();
	private final Action exportAction = new ExportAction();
	private final Action printAction = new PrintAction();
	private final Action copyAction = new CopyAction();
	private final Action cutAction = new CutAction();
	private final Action pasteAction = new PasteAction();
	private final UndoAction undoAction = new UndoAction();
	private final RedoAction redoAction = new RedoAction();

	private final Action moveTopAction;
	private final Action moveUpAction;
	private final Action moveDownAction;
	private final Action moveBottomAction;

	private final Action generateJavaAction = new GenerateJavaAction();

	private final Action hLayerLayoutAction = new GanswerSugiyamaLayoutAction(false);
	private final Action vLayerLayoutAction = new GanswerSugiyamaLayoutAction(true);
	private final Action kkLayoutAction = new ForceBasedLayoutAction();

	private final Action selectAction = new SelectionToolAction();
	private final Action fsmStartAction = new FsmStartToolAction();
	private final Action fsmEndAction = new FsmEndToolAction();
	private final Action fsmStateAction = new FsmStateToolAction();
	private final Action fsmTransitionAction = new FsmTransitionToolAction();

	private final Action decoTextAction = new DecorationTextToolAction();
	private final Action decoBitmapAction = new DecorationBitmapToolAction();
	private final Action decoPdfAction = new DecorationPdfToolAction();
	private final Action decoRectangleAction = new DecorationRectangleToolAction();
	private final Action decoEllipseAction = new DecorationEllipseToolAction();
	private final Action decoPolylineAction = new DecorationPolylineToolAction();
	private final Action decoPolygonAction = new DecorationPolygonToolAction();

	private final AbstractButton defaultToolButton;

	private final JFigureViewer<FiniteStateMachine> figurePanel;

	private NetEditorContentType currentDocumentFileFormat = null;
	private File currentDocument = null;
	private boolean changed = false;

	private AbstractPropertyPanel<?> propertyPanel = new EmptyPropertyPanel();
	private JScrollPane propertyPanelContainer;

	private final EventHandler eventHandler = new EventHandler();

	private FiniteStateMachine stateMachine;

	/**
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public FSMEditor() throws IOException {
		super(Locale.getString("TITLE")); //$NON-NLS-1$

		JToggleButton toggleButton;

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setLayout(new BorderLayout());

		setMinimumSize(new Dimension(200, 200));
		setPreferredSize(new Dimension(800, 600));

		this.propertyPanelContainer = new JScrollPane(this.propertyPanel);
		add(BorderLayout.SOUTH, this.propertyPanelContainer);

		this.stateMachine = new FiniteStateMachine();

		this.figurePanel = new JFigureViewer<FiniteStateMachine>(
				FiniteStateMachine.class,
				this.stateMachine,
				new FSMFigureFactory(),
				true);
		this.figurePanel.setAxisDrawn(true);
		this.figurePanel.setOutsideGrayed(true);
		this.figurePanel.setBackground(Color.WHITE);
		add(BorderLayout.CENTER, this.figurePanel);

		this.moveTopAction = new UndoableAction(
				Locale.getString(FSMEditor.class, "ACTION_NAME_MOVE_TOP"), //$NON-NLS-1$
				Locale.getString(FSMEditor.class, "TOOLTIP_MOVE_TOP"), //$NON-NLS-1$
				loadIcon(MOVE_TOP_ICON),
				this.figurePanel.getUndoManager(),
				MoveTopActionUndo.class,
				this);
		this.moveUpAction = new UndoableAction(
				Locale.getString(FSMEditor.class, "ACTION_NAME_MOVE_UP"), //$NON-NLS-1$
				Locale.getString(FSMEditor.class, "TOOLTIP_MOVE_UP"), //$NON-NLS-1$
				loadIcon(MOVE_UP_ICON),
				this.figurePanel.getUndoManager(),
				MoveUpActionUndo.class,
				this);
		this.moveDownAction = new UndoableAction(
				Locale.getString(FSMEditor.class, "ACTION_NAME_MOVE_DOWN"), //$NON-NLS-1$
				Locale.getString(FSMEditor.class, "TOOLTIP_MOVE_DOWN"), //$NON-NLS-1$
				loadIcon(MOVE_DOWN_ICON),
				this.figurePanel.getUndoManager(),
				MoveDownActionUndo.class,
				this);
		this.moveBottomAction = new UndoableAction(
				Locale.getString(FSMEditor.class, "ACTION_NAME_MOVE_BOTTOM"), //$NON-NLS-1$
				Locale.getString(FSMEditor.class, "TOOLTIP_MOVE_BOTTOM"), //$NON-NLS-1$
				loadIcon(MOVE_BOTTOM_ICON),
				this.figurePanel.getUndoManager(),
				MoveBottomActionUndo.class,
				this);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu fileMenu = new JMenu(Locale.getString("MENU_FILE")); //$NON-NLS-1$
		menuBar.add(fileMenu);
		JMenu editMenu = new JMenu(Locale.getString("MENU_EDIT")); //$NON-NLS-1$
		menuBar.add(editMenu);
		JMenu toolMenu = new JMenu(Locale.getString("MENU_TOOLS")); //$NON-NLS-1$
		menuBar.add(toolMenu);
		JMenu generatorMenu = new JMenu(Locale.getString("MENU_GENERATE")); //$NON-NLS-1$
		menuBar.add(generatorMenu);
		JMenu helpMenu = new JMenu(Locale.getString("MENU_HELP")); //$NON-NLS-1$
		menuBar.add(helpMenu);

		JMenu layoutSubmenu = new JMenu(Locale.getString("MENU_LAYOUT")); //$NON-NLS-1$

		fileMenu.add(makeMenuItem(this.newAction, KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK)));
		fileMenu.add(makeMenuItem(this.loadAction, KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK)));
		fileMenu.add(makeMenuItem(this.saveAction, KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK)));
		fileMenu.add(new JMenuItem(this.saveAsAction));
		fileMenu.addSeparator();
		fileMenu.add(makeMenuItem(this.exportAction, KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK)));
		fileMenu.addSeparator();
		fileMenu.add(makeMenuItem(this.printAction, KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK)));
		fileMenu.addSeparator();
		fileMenu.add(makeMenuItem(this.quitAction, KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK)));

		editMenu.add(makeMenuItem(this.undoAction, KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK)));
		editMenu.add(makeMenuItem(this.redoAction, KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK)));
		editMenu.addSeparator();
		editMenu.add(makeMenuItem(this.copyAction, KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK)));
		editMenu.add(makeMenuItem(this.cutAction, KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK)));
		editMenu.add(makeMenuItem(this.pasteAction, KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK)));
		editMenu.addSeparator();
		editMenu.add(layoutSubmenu);
		editMenu.addSeparator();
		editMenu.add(new JMenuItem(this.moveTopAction));
		editMenu.add(new JMenuItem(this.moveUpAction));
		editMenu.add(new JMenuItem(this.moveDownAction));
		editMenu.add(new JMenuItem(this.moveBottomAction));

		Collection<AbstractButton>[] buttons = new Collection[12];
		for(int i=0; i<buttons.length; ++i) {
			buttons[i] = new ArrayList<AbstractButton>(2);
		}

		toolMenu.add(makeMenuToggleButton(this.selectAction, buttons[0]));
		toolMenu.addSeparator();
		toolMenu.add(makeMenuToggleButton(this.fsmStateAction, buttons[1]));
		toolMenu.add(makeMenuToggleButton(this.fsmTransitionAction, buttons[2]));
		toolMenu.add(makeMenuToggleButton(this.fsmStartAction, buttons[3]));
		toolMenu.add(makeMenuToggleButton(this.fsmEndAction, buttons[4]));
		toolMenu.addSeparator();
		toolMenu.add(makeMenuToggleButton(this.decoTextAction, buttons[5]));
		toolMenu.add(makeMenuToggleButton(this.decoBitmapAction, buttons[6]));
		toolMenu.add(makeMenuToggleButton(this.decoPdfAction, buttons[7]));
		toolMenu.add(makeMenuToggleButton(this.decoRectangleAction, buttons[8]));
		toolMenu.add(makeMenuToggleButton(this.decoEllipseAction, buttons[9]));
		toolMenu.add(makeMenuToggleButton(this.decoPolylineAction, buttons[10]));
		toolMenu.add(makeMenuToggleButton(this.decoPolygonAction, buttons[11]));

		generatorMenu.add(new JMenuItem(this.generateJavaAction));

		layoutSubmenu.add(makeMenuItem(
				this.hLayerLayoutAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_1, InputEvent.CTRL_MASK)));
		layoutSubmenu.add(makeMenuItem(
				this.vLayerLayoutAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_2, InputEvent.CTRL_MASK)));
		layoutSubmenu.add(makeMenuItem(
				this.kkLayoutAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_3, InputEvent.CTRL_MASK)));

		helpMenu.add(new JMenuItem(this.aboutAction));

		JToolBar toolBar = new JToolBar();
		add(BorderLayout.NORTH, toolBar);

		toolBar.add(makeToolbarButton(this.loadAction));
		toolBar.add(makeToolbarButton(this.saveAction));
		toolBar.add(makeToolbarButton(this.saveAsAction));
		toolBar.add(makeToolbarButton(this.exportAction));
		toolBar.addSeparator();
		toolBar.add(makeToolbarButton(this.printAction));
		toolBar.addSeparator();
		toolBar.add(makeToolbarButton(this.copyAction));
		toolBar.add(makeToolbarButton(this.cutAction));
		toolBar.add(makeToolbarButton(this.pasteAction));
		toolBar.addSeparator();
		toolBar.add(makeToolbarButton(this.undoAction));
		toolBar.add(makeToolbarButton(this.redoAction));

		updateUndoRedoButtons();
		updateCCPButtons();

		toolBar.addSeparator();

		toggleButton = makeToolbarToggleButton(this.selectAction, buttons[0]);
		toolBar.add(toggleButton);
		this.defaultToolButton = toggleButton;
		toolBar.add(makeToolbarToggleButton(this.fsmStartAction, buttons[3]));
		toolBar.add(makeToolbarToggleButton(this.fsmStateAction, buttons[1]));
		toolBar.add(makeToolbarToggleButton(this.fsmTransitionAction, buttons[2]));
		toolBar.add(makeToolbarToggleButton(this.fsmEndAction, buttons[4]));

		toolBar.addSeparator();

		toolBar.add(makeToolbarToggleButton(this.decoTextAction, buttons[5]));
		toolBar.add(makeToolbarToggleButton(this.decoBitmapAction, buttons[6]));
		toolBar.add(makeToolbarToggleButton(this.decoPdfAction, buttons[7]));
		toolBar.add(makeToolbarToggleButton(this.decoRectangleAction, buttons[8]));
		toolBar.add(makeToolbarToggleButton(this.decoEllipseAction, buttons[9]));
		toolBar.add(makeToolbarToggleButton(this.decoPolylineAction, buttons[10]));
		toolBar.add(makeToolbarToggleButton(this.decoPolygonAction, buttons[11]));

		ButtonGroup buttonGroup = new ButtonGroup();
		for(Collection<AbstractButton> bts : buttons) {
			JGroupButton gb = new JGroupButton(bts);
			buttonGroup.add(gb);
		}

		this.defaultToolButton.doClick();

		toolBar.addSeparator();

		toolBar.add(makeToolbarButton(this.moveTopAction));
		toolBar.add(makeToolbarButton(this.moveUpAction));
		toolBar.add(makeToolbarButton(this.moveDownAction));
		toolBar.add(makeToolbarButton(this.moveBottomAction));

		pack();

		setLocationRelativeTo(null);

		this.stateMachine.addModelObjectListener(this.eventHandler);
		this.figurePanel.getModeManager().addModeListener(this.eventHandler);
		this.figurePanel.getModeManager().addSelectableInteractionListener(this.eventHandler);
		this.figurePanel.getSelectionManager().addSelectionListener(this.eventHandler);
		this.figurePanel.getUndoManager().addUndoListener(this.eventHandler);
		this.figurePanel.addFigureListener(this.eventHandler);
		this.figurePanel.addExceptionListener(this.eventHandler);
		Toolkit.getDefaultToolkit().getSystemClipboard().addFlavorListener(this.eventHandler);
		addWindowListener(this.eventHandler);
	}

	/** Replies if the current document has changed since its last saving/loading.
	 * 
	 * @return <code>true</code> if the current document has changed; otherwise <code>false</code>.
	 */
	public boolean isChanged() {
		return this.changed;
	}

	/** Mark all the UI components about the change of the current document.
	 */
	protected void changed() {
		if (!this.changed) {
			this.changed = true;
			if (this.currentDocument!=null) {
				setTitle(Locale.getString("CHANGED_TITLE", this.currentDocument.getName())); //$NON-NLS-1$
			}
			else {
				setTitle(Locale.getString("CHANGED_MAIN_TITLE")); //$NON-NLS-1$
			}
		}
	}

	/** Mark all the UI components about the saving of the current document.
	 */
	protected void saved() {
		this.changed = false;
		if (this.currentDocument!=null) {
			setTitle(Locale.getString("SAVED_TITLE", this.currentDocument.getName())); //$NON-NLS-1$
		}
		else {
			setTitle(Locale.getString("TITLE")); //$NON-NLS-1$
		}
	}

	private void updateUndoRedoButtons() {
		UndoManager manager = this.figurePanel.getUndoManager();
		String label, tooltip;

		tooltip = label = manager.getUndoPresentationName();
		this.undoAction.setText(label);
		this.undoAction.setToolTipText(tooltip);
		this.undoAction.setEnabled(manager.canUndo());

		tooltip = label = manager.getRedoPresentationName();
		this.redoAction.setText(label);
		this.redoAction.setToolTipText(tooltip);
		this.redoAction.setEnabled(manager.canRedo());
	}

	private void updateCCPButtons() {
		boolean copy = false;
		boolean paste = false;
		TransferHandler handler = this.figurePanel.getTransferHandler();
		if (handler!=null) {
			copy = !this.figurePanel.getSelectionManager().isEmpty();
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
			DataFlavor[] flavors = clipboard.getAvailableDataFlavors();
			paste = handler.canImport(this.figurePanel, flavors);
		}
		this.copyAction.setEnabled(copy);
		this.cutAction.setEnabled(copy);
		this.pasteAction.setEnabled(paste);
	}

	private void updateLayerButtons() {
		boolean canMoveUp = false;
		boolean canMoveDown = false;

		for(Figure figure : this.figurePanel.getSelectionManager()) {
			int index = this.figurePanel.indexOfFigure(figure);
			if (index>=0) {
				if (index>0) {
					canMoveUp = true;
				}
				if (index<(this.figurePanel.getFigureCount()-1)) {
					canMoveDown = true;
				}
			}
		}

		this.moveTopAction.setEnabled(canMoveUp);
		this.moveUpAction.setEnabled(canMoveUp);
		this.moveDownAction.setEnabled(canMoveDown);
		this.moveBottomAction.setEnabled(canMoveDown);
	}

	private void updatePropertyPanelFor(Figure figure) {
		UndoManager manager = this.figurePanel.getUndoManager();
		if (this.propertyPanel!=null) {
			if (this.propertyPanel.isSupported(figure)) {
				this.propertyPanel.setFigure(figure);
				return;
			}
			this.propertyPanel.release();
			remove(this.propertyPanelContainer);
			this.propertyPanel = null;
			this.propertyPanelContainer = null;
		}
		if (figure instanceof FSMStateFigure) {
			this.propertyPanel = new StatePropertyPanel(
					(FSMStateFigure)figure,
					manager);
		}
		else if (figure instanceof FSMTransitionFigure) {
			this.propertyPanel = new TransitionPropertyPanel(
					(FSMTransitionFigure)figure,
					manager);
		}
		else if (figure instanceof TextFigure) {
			this.propertyPanel = new TextPropertyPanel((TextFigure)figure, manager);
		}
		else if (figure instanceof PolylineFigure) {
			this.propertyPanel = new PolylinePropertyPanel((PolylineFigure)figure, manager);
		}
		else {
			this.propertyPanel = new EmptyPropertyPanel();
		}
		this.propertyPanel.updateContent();
		this.propertyPanelContainer = new JScrollPane(this.propertyPanel);
		add(BorderLayout.SOUTH, this.propertyPanelContainer);
	}

	/** Replies the preferred directory for the user.
	 * 
	 * @return the preferred directory.
	 */
	public static File getPreferredDirectory() {
		Preferences prefs = Preferences.userNodeForPackage(FSMEditor.class);
		String n = prefs.get("PREFERRED_DIRECTORY", null); //$NON-NLS-1$
		if (n==null || n.isEmpty()) return null;
		return new File(n);
	}

	/** Set the preferred directory for the user.
	 * 
	 * @param directory is the preferred directory.
	 */
	public static void setPreferredDirectory(File directory) {
		Preferences prefs = Preferences.userNodeForPackage(FSMEditor.class);
		if (directory==null) {
			prefs.remove("PREFERRED_DIRECTORY"); //$NON-NLS-1$
		}
		else {
			File d = directory;
			while (!d.isDirectory()) {
				d = d.getParentFile();
			}
			prefs.put("PREFERRED_DIRECTORY", d.getAbsolutePath()); //$NON-NLS-1$
		}
		try {
			prefs.sync();
		}
		catch (BackingStoreException e) {
			//
		}
	}

	/** Create a new document.
	 * 
	 * @return <code>true</code> if a new document is created;
	 * otherwise <code>false</code>.
	 */
	protected boolean newDocument() {
		if (isChanged() && this.currentDocument!=null) {
			String message = Locale.getString("DOCUMENT_NOT_SAVED_1", this.currentDocument.getName()); //$NON-NLS-1$
			int opt = JOptionPane.showConfirmDialog(
					this,
					message,
					Locale.getString("SAVING"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			switch(opt) {
			case JOptionPane.CANCEL_OPTION:
				return false; // cancel the new document
			case JOptionPane.YES_OPTION:
				save();
				break;
			default:
			}
		}

		this.figurePanel.getUndoManager().discardAllEdits();
		this.figurePanel.getSelectionManager().clear();
		this.figurePanel.getModeManager().resetModes();
		this.stateMachine.removeModelObjectListener(this.eventHandler);
		this.stateMachine = new FiniteStateMachine();
		this.figurePanel.setGraph(this.stateMachine);
		this.currentDocument = null;
		this.currentDocumentFileFormat = null;
		saved();
		this.stateMachine.addModelObjectListener(this.eventHandler);

		return true;
	}

	/** Open the "about" dialog box.
	 */
	protected void about() {
		JAboutDialog dlg = new JAboutDialog(this);
		SwingUtilities.invokeLater(dlg);
	}

	/** Load a document.
	 */
	protected void load() {
		JFileChooser chooser = new JFileChooser(getPreferredDirectory());
		chooser.setFileFilter(new FileFilterSwing(new NGRFileFilter()));
		if (chooser.showOpenDialog(FSMEditor.this)==JFileChooser.APPROVE_OPTION) {
			try {
				if (newDocument()) {
					File inputFile = chooser.getSelectedFile();
					NetEditorReader gw = new NGRReader();
					Map<UUID,List<ViewComponent>> figures = new TreeMap<UUID,List<ViewComponent>>();
					Progression progression = ProgressMonitor.createProgression(
							this,
							Locale.getString("LOADING_FILE", inputFile.getName()), //$NON-NLS-1$
							null);
					gw.setProgression(progression);
					FiniteStateMachine g = gw.read(
							FiniteStateMachine.class,
							inputFile,
							figures);
					this.figurePanel.importGraph(null, g, figures);
					this.currentDocument = inputFile;
					this.currentDocumentFileFormat = gw.getContentType();
					saved();
					this.figurePanel.defaultView(true);
				}
			}
			catch (Throwable ex) {
				this.figurePanel.fireError(ex);
			}
		}
	}

	/**
	 * Save the document.
	 */
	protected void save() {
		if (this.currentDocument==null) {
			saveAs();
		}
		else {
			try {
				NGRWriter gw = new NGRWriter();
				Progression progression = ProgressMonitor.createProgression(
						this,
						Locale.getString("SAVING_FILE", this.currentDocument.getName()), //$NON-NLS-1$
						null);
				gw.setProgression(progression);
				if (this.currentDocumentFileFormat!=null) {
					gw.setContentType(this.currentDocumentFileFormat);
				}

				File tempFile = File.createTempFile("tmpsave", null); //$NON-NLS-1$
				try {
					FileOutputStream fos = new FileOutputStream(tempFile);
					try {
						gw.write(
								fos,
								FSMEditor.this.figurePanel.getGraph(),
								FSMEditor.this.figurePanel);
					}
					finally {
						fos.close();
					}
					FileSystem.copy(tempFile, this.currentDocument);
				}
				finally {
					tempFile.delete();
				}
				saved();
			}
			catch (Throwable ex) {
				this.figurePanel.fireError(ex);
			}
		}
	}

	/**
	 * Output a file chooser to select a file for writting.
	 * 
	 * @param accessory is the accessory to put in the file chooser.
	 * @param fileFilter
	 * @return the filename or <code>null</code> to cancel.
	 */
	protected File selectFileToSave(FileChooserAccessory accessory, FileFilter... fileFilter) {
		String preferredExtension = null;
		JFileChooser chooser = new JFileChooser(getPreferredDirectory());
		for(FileFilter ff : fileFilter) {
			if (preferredExtension==null) {
				preferredExtension = ff.getExtensions()[0];
			}
			chooser.addChoosableFileFilter(new FileFilterSwing(ff));
		}

		if (accessory!=null) {
			chooser.setAccessory(accessory);
		}

		File outputFile;

		do {
			outputFile = null;
			if (chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
				outputFile = chooser.getSelectedFile();
				javax.swing.filechooser.FileFilter sff = chooser.getFileFilter();
				if (sff instanceof FileFilterSwing) {
					FileFilter currentFileFilter = ((FileFilterSwing)sff).getFileFilter();
					if (!currentFileFilter.accept(outputFile)) {
						outputFile = FileSystem.addExtension(outputFile, currentFileFilter.getExtensions()[0]);
					}
				}
				if (preferredExtension!=null && "".equals(FileSystem.extension(outputFile))) { //$NON-NLS-1$
					outputFile = FileSystem.addExtension(outputFile, preferredExtension);
				}
				int opt;
				if (!outputFile.exists()
						|| (opt=JOptionPane.showConfirmDialog(
								FSMEditor.this,
								Locale.getString(
										FSMEditor.class,
										"FILE_EXIST_OVERWRITE", //$NON-NLS-1$
										outputFile.getName()))
								)==JOptionPane.YES_OPTION) {
					return outputFile;
				}
				else if (opt==JOptionPane.CANCEL_OPTION) {
					return null;
				}
			}
		}
		while(outputFile!=null);
		return outputFile;
	}

	/**
	 * Save the document into a new file.
	 */
	protected void saveAs() {
		FileChooserAccessory accessory = new FileChooserAccessory();
		File outputFile = selectFileToSave(accessory, new NGRFileFilter());
		if (outputFile!=null) {
			try {
				NGRWriter gw = new NGRWriter();
				Progression progression = ProgressMonitor.createProgression(
						this,
						Locale.getString("SAVING_FILE", outputFile.getName()), //$NON-NLS-1$
						null);
				gw.setProgression(progression);
				NetEditorContentType type = accessory.getContentType();
				if (type!=null) {
					gw.setContentType(type);
				}
				File tempFile = File.createTempFile("tmpsave", NGRFileFilter.EXTENSION_NGR); //$NON-NLS-1$
				try {
					FileOutputStream fos = new FileOutputStream(tempFile);
					try {
						gw.write(
								fos,
								FSMEditor.this.figurePanel.getGraph(),
								FSMEditor.this.figurePanel);
					}
					finally {
						fos.close();
					}
					FileSystem.copy(tempFile, outputFile);
				}
				finally {
					tempFile.delete();
				}
				this.currentDocument = outputFile;
				this.currentDocumentFileFormat = null;
				saved();
			}
			catch (Throwable ex) {
				this.figurePanel.fireError(ex);
			}
		}
	}

	/** Export the document.
	 */
	protected void export() {
		setCursor(AwtUtil.getCursor(MouseCursor.WAIT));
		try {
			FileFilter[] filters1 = VectorialPictureFileType.getFileFilters();
			FileFilter[] filters2 = ImageType.getFileFilters();
			FileFilter[] filters = new FileFilter[filters1.length+filters2.length];
			System.arraycopy(filters1, 0, filters, 0, filters1.length);
			System.arraycopy(filters2, 0, filters, filters1.length, filters2.length);
			File outputFile = selectFileToSave(null, filters);
			if (outputFile!=null) {
				ImageType type = ImageType.valueOf(outputFile);
				if (type!=null) {
					BitmapExporter exporter = new ImageIOBitmapExporter(type);
					try {
						File tmpOutputFile = File.createTempFile("fsmeditorexport", "."+type.getExtension()); //$NON-NLS-1$ //$NON-NLS-2$
						try {
							if (exporter.write(
									new FileOutputStream(tmpOutputFile),
									this.figurePanel,
									Math.max(1, this.figurePanel.logical2pixel_size(1)))) {
								FileSystem.copy(tmpOutputFile, outputFile);
							}
							else {
								this.figurePanel.fireError(new IOException());
							}
						}
						finally {
							tmpOutputFile.delete();
						}
					}
					catch (Throwable ex) {
						this.figurePanel.fireError(ex);
					}
				}
				else {
					VectorialPictureFileType vType = VectorialPictureFileType.valueOf(outputFile);
					if (vType!=null) {
						VectorialExporter vExporter = null;
						switch(vType) {
						case SVG:
							vExporter = new SvgExporter();
							break;
						case PDF:
							vExporter = new PdfExporter();
							break;
						case EPS:
							vExporter = new EpsExporter();
							break;
						case DOT:
							DotExporter dotExport = new DotExporter();
							dotExport.setFigureExported(true);
							vExporter = dotExport;
							break;
						case GXL:
							vExporter = new GXLExporter();
							break;
						case GRAPHML:
							vExporter = new GraphMLExporter();
							break;
						case GML:
							vExporter = new GMLExporter();
							break;
						case PDF_TEX:
							vExporter = new PdfTeXExporter();
							break;
						case EPS_TEX:
							vExporter = new EpsTeXExporter();
							break;
						default:
							throw new IllegalStateException();
						}
						boolean export = true;
						if (!vExporter.isSpecificationCompliant()) {
							export = JOptionPane.showConfirmDialog(FSMEditor.this,
									Locale.getString("INCOMPLE_EXPORTER_CONTINUE")) //$NON-NLS-1$
									== JOptionPane.YES_OPTION;
						}
						if (export) {
							try {
								vExporter.setShadowExported(FSMEditor.this.figurePanel.isShadowDrawn());
								FileCollection fileCollection = new FileCollection(outputFile);
								vExporter.setFileCollection(fileCollection);
								try {
									vExporter.write(
											fileCollection.getTemporaryMainFile(),
											FSMEditor.this.figurePanel.getGraph(),
											FSMEditor.this.figurePanel);
									fileCollection.copyFiles();
								}
								finally {
									fileCollection.deleteTemporaryFiles();
								}
							}
							catch (Throwable ex) {
								FSMEditor.this.figurePanel.fireError(ex);
							}
						}
					}
				}
			}
		}
		finally {
			setCursor(AwtUtil.getCursor(MouseCursor.DEFAULT));
		}
	}

	/** Generate the Jave source code.
	 */
	protected void generateJava() {
		setCursor(AwtUtil.getCursor(MouseCursor.WAIT));
		try {
			File outputFile = selectFileToSave(null, new JavaFileFilter());
			if (outputFile!=null) {
				String className = FileSystem.shortBasename(outputFile);
				FSMJavaGenerator generator = new FSMJavaGenerator(className);
				String content = generator.generate(this.stateMachine);
				FileWriter writer = new FileWriter(outputFile);
				try {
					writer.write(content);
				}
				finally {
					writer.close();
				}
			}
		}
		catch(IOException e) {
			this.figurePanel.fireError(e);
		}
		finally {
			setCursor(AwtUtil.getCursor(MouseCursor.DEFAULT));
		}
	}

	/** Print the document.
	 */
	protected void print() {
		PrinterJob job = PrinterJob.getPrinterJob();
		job.setPrintable(new Printer(this.figurePanel));
		if (job.printDialog()) {
			setCursor(AwtUtil.getCursor(MouseCursor.WAIT));
			try {
				job.print();
			}
			catch (PrinterException e) {
				this.figurePanel.fireError(e);
			}
			finally {
				setCursor(AwtUtil.getCursor(MouseCursor.DEFAULT));
			}
		}
	}

	/** Quit the application.
	 */
	protected void quit() {
		if (isChanged()) {
			String message;
			if (this.currentDocument!=null) {
				message = Locale.getString("DOCUMENT_NOT_SAVED_1", this.currentDocument.getName()); //$NON-NLS-1$
			}
			else {
				message = Locale.getString("DOCUMENT_NOT_SAVED_0"); //$NON-NLS-1$
			}
			int opt = JOptionPane.showConfirmDialog(
					this,
					message,
					Locale.getString("SAVING"), //$NON-NLS-1$
					JOptionPane.YES_NO_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			switch(opt) {
			case JOptionPane.CANCEL_OPTION:
				return; // cancel the closing
			case JOptionPane.YES_OPTION:
				save();
				break;
			default:
			}
		}
		setVisible(false);
		dispose();
		System.exit(0);
	}

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class EventHandler implements UndoListener, FlavorListener,
	SelectionListener, ActionModeListener,
	SelectableInteractionListener, FigureListener,
	WindowListener, ModelObjectListener,
	ExceptionListener {
		/**
		 */
		public EventHandler() {
			//
		}

		//
		// UndoListener
		//

		/**
		 * Invoked when the undo/redo manager has changed.
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void undoListChanged(UndoManager m) {
			updateUndoRedoButtons();
		}


		//
		// FlavorListener
		//

		/**
		 * Invoked when the flavor of the system clipboard has changed.
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void flavorsChanged(FlavorEvent e) {
			updateCCPButtons();
		}

		//
		// SelectionListener
		//

		/**
		 * Invoked when the selection in the editor panel has changed. 
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void selectionChanged(SelectionEvent event) {
			updateLayerButtons();
			updateCCPButtons();
			Figure figure = null;
			if (event.isSelected()) {
				figure = (Figure)event.getSelectable();
			}
			else {
				SelectionManager manager = (SelectionManager)event.getSource();
				if (!manager.isEmpty()) {
					figure = manager.iterator().next();
				}
			}
			updatePropertyPanelFor(figure);
		}

		//
		// ModeListener
		//

		/**
		 * Invoked when an interactive mode was activated.
		 */
		@Override
		public void modeActivated(ActionMode<?,?,?> mode) {
			//
		}

		/**
		 * Invoked when an interactive mode was desactivated.
		 * Force the default tool to be selected again.
		 * This is necessary to update the UI, the selected
		 * mode in the ModeManager should be the default mode, at all.
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void modeDesactivated(ActionMode<?,?,?> mode) {
			FSMEditor.this.defaultToolButton.doClick();
		}

		//
		// FigureInteractionListener
		//

		/**
		 * Invoked when the action was performed on a figure.
		 */
		@Override
		public void actionPerformed(SelectableInteractionEvent event) {
			//
		}

		/**
		 * Invoked when the popup request was performed on a figure.
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void popupPerformed(SelectableInteractionEvent event) {
			JPopupMenu menu = new JPopupMenu();

			Figure figure = (Figure)event.getSelectable();

			if (figure!=null) {
				// Force the source to be selected
				JSelectionManager selectionManager = FSMEditor.this.figurePanel.getSelectionManager();
				if (!selectionManager.contains(figure)) {
					selectionManager.setSelection(figure);
				}


				if (figure.isLockable()) {
					try {
						String label;
						Icon icon;
						if (figure.isLocked()) {
							label = Locale.getString(FSMEditor.class, "MENU_UNLOCK", figure.toString()); //$NON-NLS-1$
							icon = loadIcon(UNLOCK_ICON);
						}
						else {
							label = Locale.getString(FSMEditor.class, "MENU_LOCK", figure.toString()); //$NON-NLS-1$
							icon = loadIcon(LOCK_ICON);
						}
						JMenuItem item = new JMenuItem(new UndoableAction(
								label,
								icon,
								FSMEditor.this.figurePanel.getUndoManager(),
								LockingActionUndo.class,
								FSMEditor.this,
								figure,
								!figure.isLocked()));
						menu.add(item);
					}
					catch(IOException e) {
						FSMEditor.this.figurePanel.fireError(e);
					}
				}

				if (figure instanceof EdgeFigure<?>) {
					EdgeFigure<?> edgeFigure = (EdgeFigure<?>)figure;
					if (edgeFigure instanceof PolylineEdgeFigure<?>) {
						PolylineEdgeFigure<?> polyline = (PolylineEdgeFigure<?>)edgeFigure;

						if (menu.getComponentCount()>0) menu.addSeparator();

						JMenu edgeLayout = new JMenu(Locale.getString(FSMEditor.class, "MENU_EDGE_LAYOUT")); //$NON-NLS-1$
						DrawingMethod method = polyline.getDrawingMethod();
						for(DrawingMethod m : DrawingMethod.values()) {
							JMenuItem item = new JRadioButtonMenuItem();
							if (m==method)
								item.setSelected(true);
							String label = Locale.getString(FSMEditor.class, "MENU_EDGE_LAYOUT_"+m.name().toUpperCase()); //$NON-NLS-1$
							item.setAction(new UndoableAction(
									label,
									FSMEditor.this.figurePanel.getUndoManager(),
									EdgeLayoutActionUndo.class,
									FSMEditor.this,
									polyline, m));
							edgeLayout.add(item);
						}
						menu.add(edgeLayout);
					}				
				}
			}

			if (menu.getComponentCount()>0) menu.addSeparator();

			JCheckBoxMenuItem axisItem = new JCheckBoxMenuItem(
					Locale.getString(FSMEditor.class, "MENU_SHOW_AXIS")); //$NON-NLS-1$
			axisItem.setSelected(FSMEditor.this.figurePanel.isAxisDrawn());
			axisItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FSMEditor.this.figurePanel.setAxisDrawn(
							!FSMEditor.this.figurePanel.isAxisDrawn());
				}
			});
			menu.add(axisItem);

			JCheckBoxMenuItem borderItem = new JCheckBoxMenuItem(
					Locale.getString(FSMEditor.class, "MENU_SHOW_DOCUMENT_BOUNDS")); //$NON-NLS-1$
			borderItem.setSelected(FSMEditor.this.figurePanel.isOutsideGrayed());
			borderItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FSMEditor.this.figurePanel.setOutsideGrayed(
							!FSMEditor.this.figurePanel.isOutsideGrayed());
				}
			});
			menu.add(borderItem);

			JCheckBoxMenuItem shadowItem = new JCheckBoxMenuItem(
					Locale.getString(FSMEditor.class, "MENU_SHOW_SHADOWS")); //$NON-NLS-1$
			shadowItem.setSelected(FSMEditor.this.figurePanel.isShadowDrawn());
			shadowItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FSMEditor.this.figurePanel.setShadowDrawn(
							!FSMEditor.this.figurePanel.isShadowDrawn());
				}
			});
			menu.add(shadowItem);

			if (menu.getComponentCount()>0) {
				PointerEvent me = event.getPointerEvent();
				float x, y;
				if (me!=null) {
					x = me.getX();
					y = me.getY();
				}
				else {
					x = y = 0;
				}
				menu.show(FSMEditor.this.figurePanel, (int)x, (int)y);
			}
		}

		/**
		 * Invoked to ask to the user if the specified figure could be deleted.
		 * According to the implementation of the modes, the deletion
		 * can be undoed.
		 */
		@Override
		public boolean figureDeletionPerformed(Selectable figure,
				boolean deleteModel) {
			return true;
		}

		//
		// FigureListener
		//

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void figureAdded(FigureEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void figureChanged(FigureEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void figureRemoved(FigureEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowOpened(WindowEvent e) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowClosing(WindowEvent e) {
			quit();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowClosed(WindowEvent e) {
			//	
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowIconified(WindowEvent e) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowDeiconified(WindowEvent e) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowActivated(WindowEvent e) {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void windowDeactivated(WindowEvent e) {
			//
		}

		//
		// ModelObjectListener
		//

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelPropertyChanged(ModelObjectEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelContainerChanged(ModelObjectEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelLinkChanged(ModelObjectEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelContentChanged(ModelObjectEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelComponentAdded(ModelObjectEvent event) {
			changed();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void modelComponentRemoved(ModelObjectEvent event) {
			changed();
		}

		//
		// ExceptionListener
		//

		@Override
		public boolean exceptionThrown(Throwable ex) {
			assert(ex!=null);
			Throwable exception = ex;
			while (exception.getCause()!=null) {
				exception = exception.getCause();
			}
			StringWriter sw = new StringWriter();
			exception.printStackTrace(new PrintWriter(sw));
			String stack = sw.toString();
			JOptionPane.showMessageDialog(FSMEditor.this,
					Locale.getString(FSMEditor.class, "ERROR_MESSAGE", exception.getLocalizedMessage(), stack), //$NON-NLS-1$
					exception.getClass().getName(),
					JOptionPane.ERROR_MESSAGE);
			exception.printStackTrace();
			return true;
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class Printer implements Printable {

		private final JFigureViewer<?> p;

		/**
		 * @param p
		 */
		public Printer(JFigureViewer<?> p) {
			this.p = p;
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
				throws PrinterException {
			if (pageIndex==0) {
				boolean old = this.p.isAxisDrawn();
				this.p.setAxisDrawn(false);
				try {
					Image image = ImageIO.read(LOGO_ICON);
					assert(image!=null);

					Graphics2D g2d = (Graphics2D)graphics;
					int w = image.getWidth(null);
					int h = image.getHeight(null);
					double f = .25*pageFormat.getImageableWidth() / w;
					double x2 = pageFormat.getImageableX() + pageFormat.getImageableWidth();
					double y2 = pageFormat.getImageableY() + pageFormat.getImageableHeight();
					double x1 = x2 - f * w;
					double y1 = y2 - f * h;
					g2d.drawImage(image, (int)x1, (int)y1, (int)x2, (int)y2, 0, 0, w, h, null);

					return this.p.print(graphics, pageFormat, pageIndex);
				}
				catch (IOException e) {
					throw new PrinterException(e.getLocalizedMessage());
				}
				finally {
					this.p.setAxisDrawn(old);
				}
			}
			return NO_SUCH_PAGE;
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class LoadAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public LoadAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_LOAD")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_LOAD")); //$NON-NLS-1$
			setIcon(loadIcon(LOAD_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			load();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SaveAction extends StandardAction {

		private static final long serialVersionUID = -4704806038783995288L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public SaveAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_SAVE")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_SAVE")); //$NON-NLS-1$
			setIcon(loadIcon(SAVE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			save();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SaveAsAction extends StandardAction {

		private static final long serialVersionUID = -7352567278420513332L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public SaveAsAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_SAVEAS")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_SAVEAS")); //$NON-NLS-1$
			setIcon(loadIcon(SAVEAS_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			saveAs();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class ExportAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public ExportAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_EXPORT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_EXPORT")); //$NON-NLS-1$
			setIcon(loadIcon(EXPORT_IMAGE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			export();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PrintAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public PrintAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_PRINT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_PRINT")); //$NON-NLS-1$
			setIcon(loadIcon(PRINT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			print();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class CopyAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public CopyAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_COPY")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_COPY")); //$NON-NLS-1$
			setIcon(loadIcon(COPY_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.copy();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class CutAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public CutAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CUT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CUT")); //$NON-NLS-1$
			setIcon(loadIcon(CUT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.cut();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class PasteAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public PasteAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_PASTE")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_PASTE")); //$NON-NLS-1$
			setIcon(loadIcon(PASTE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.paste();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class UndoAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public UndoAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_UNDO")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_UNDO")); //$NON-NLS-1$
			setIcon(loadIcon(UNDO_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getUndoManager().undo();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class RedoAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public RedoAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_REDO")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_REDO")); //$NON-NLS-1$
			setIcon(loadIcon(REDO_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getUndoManager().redo();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SelectionToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public SelectionToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_SELECT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_SELECT")); //$NON-NLS-1$
			setIcon(loadIcon(MOUSE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().resetModes();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FsmStartToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public FsmStartToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_START_POINT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_START_POINT")); //$NON-NLS-1$
			setIcon(loadIcon(START_POINT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new FSMStartPointCreationMode());
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FsmEndToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public FsmEndToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_END_POINT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_END_POINT")); //$NON-NLS-1$
			setIcon(loadIcon(END_POINT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new FSMEndPointCreationMode());
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FsmStateToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public FsmStateToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_STATE")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_STATE")); //$NON-NLS-1$
			setIcon(loadIcon(STATE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new FSMStateCreationMode());
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class FsmTransitionToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public FsmTransitionToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_TRANSITION")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_TRANSITION")); //$NON-NLS-1$
			setIcon(loadIcon(TRANSITION_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new FSMTransitionCreationMode());
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationTextToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public DecorationTextToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_TEXT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_TEXT")); //$NON-NLS-1$
			setIcon(loadIcon(TEXT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new TextDecorationCreationMode(MODE_PERSISTENCE));
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationBitmapToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public DecorationBitmapToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_BITMAP")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_BITMAP")); //$NON-NLS-1$
			setIcon(loadIcon(BITMAP_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new BitmapDecorationCreationMode(MODE_PERSISTENCE));
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationPdfToolAction extends StandardAction {

		private static final long serialVersionUID = -4270090946820640096L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public DecorationPdfToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_PDF")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_PDF")); //$NON-NLS-1$
			setIcon(loadIcon(PDF_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new PdfDecorationCreationMode(MODE_PERSISTENCE));
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationRectangleToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public DecorationRectangleToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_RECTANGLE")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_RECTANGLE")); //$NON-NLS-1$
			setIcon(loadIcon(RECTANGLE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new RectangleDecorationCreationMode(MODE_PERSISTENCE));
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationEllipseToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public DecorationEllipseToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_ELLIPSE")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_ELLIPSE")); //$NON-NLS-1$
			setIcon(loadIcon(ELLIPSE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new EllipseDecorationCreationMode(MODE_PERSISTENCE));
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationPolylineToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public DecorationPolylineToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_POLYLINE")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_POLYLINE")); //$NON-NLS-1$
			setIcon(loadIcon(POLYLINE_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new PolylineDecorationCreationMode(MODE_PERSISTENCE));
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DecorationPolygonToolAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public DecorationPolygonToolAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_CREATE_POLYGON")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_CREATE_POLYGON")); //$NON-NLS-1$
			setIcon(loadIcon(POLYGON_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			FSMEditor.this.figurePanel.getModeManager().beginMode(
					new PolygonDecorationCreationMode(MODE_PERSISTENCE));
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private abstract class AbstractMoveActionUndo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = -4632407053206655114L;

		private final Map<Integer,Figure> oldPositions = new TreeMap<Integer,Figure>();

		/**
		 */
		public AbstractMoveActionUndo() {
			//
		}

		protected abstract void move(Figure figure);

		/**
		 * Do the move action.
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public final void doEdit() {
			if (this.oldPositions.isEmpty()) {
				JSelectionManager selManager = FSMEditor.this.figurePanel.getSelectionManager();
				for(Figure figure : selManager) {
					int p = FSMEditor.this.figurePanel.indexOfFigure(figure);
					if (p>=0) {
						this.oldPositions.put(p, figure);
					}
				}
			}
			for(Figure figure : this.oldPositions.values()) {
				move(figure);
			}
			updateLayerButtons();
		}

		/**
		 * Undo the move action.
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public final void undoEdit() {
			if (!this.oldPositions.isEmpty()) {
				for(Entry<Integer,Figure> entry : this.oldPositions.entrySet()) {
					FSMEditor.this.figurePanel.moveFigureAt(entry.getValue(), entry.getKey());
				}
				updateLayerButtons();
			}
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class MoveUpActionUndo extends AbstractMoveActionUndo {

		private static final long serialVersionUID = -371425528593310071L;

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected void move(Figure figure) {
			FSMEditor.this.figurePanel.moveFigureUp(figure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(FSMEditor.class, "UNDO_PRESENTATION_MOVE_UP"); //$NON-NLS-1$
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class MoveDownActionUndo extends AbstractMoveActionUndo {

		private static final long serialVersionUID = 5817597688505110201L;

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected void move(Figure figure) {
			FSMEditor.this.figurePanel.moveFigureDown(figure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(FSMEditor.class, "UNDO_PRESENTATION_MOVE_DOWN"); //$NON-NLS-1$
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class MoveTopActionUndo extends AbstractMoveActionUndo {

		private static final long serialVersionUID = 2559588786541767488L;

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected void move(Figure figure) {
			FSMEditor.this.figurePanel.moveFigureFront(figure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(FSMEditor.class, "UNDO_PRESENTATION_MOVE_TOP"); //$NON-NLS-1$
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class MoveBottomActionUndo extends AbstractMoveActionUndo {

		private static final long serialVersionUID = -5229528000381427997L;

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected void move(Figure figure) {
			FSMEditor.this.figurePanel.moveFigureBackground(figure);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			return Locale.getString(FSMEditor.class, "UNDO_PRESENTATION_MOVE_BOTTOM"); //$NON-NLS-1$
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class NewAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		public NewAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_NEW")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_NEW")); //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			newDocument();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class QuitAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		public QuitAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_QUIT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_QUIT")); //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			quit();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class AboutAction extends StandardAction {

		private static final long serialVersionUID = 5309957181512854494L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public AboutAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_ABOUT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_ABOUT")); //$NON-NLS-1$
			setIcon(loadIcon(ABOUT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			about();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class LockingActionUndo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = 5309957181512854494L;

		private final Figure figure;
		private final boolean lockingValue;

		/**
		 * @param figure
		 * @param lockingValue
		 */
		@SuppressWarnings("unused")
		public LockingActionUndo(Figure figure, boolean lockingValue) {
			this.figure = figure;
			this.lockingValue = lockingValue;
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof LockingActionUndo) {
				LockingActionUndo lau = (LockingActionUndo)anEdit;
				return (this.figure==lau.figure && this.lockingValue==lau.lockingValue);
			}
			return false;
		}

		@Override
		public void doEdit() {
			if (this.figure!=null) {
				this.figure.setLocked(this.lockingValue);
			}
		}

		@Override
		public void undoEdit() {
			if (this.figure!=null) {
				this.figure.setLocked(!this.lockingValue);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			if (this.figure!=null) {
				String txt = this.figure.toString();
				if (txt!=null && !txt.isEmpty()) {
					return Locale.getString(FSMEditor.class,
							(this.lockingValue ?
									"UNDO_PRESENTATION_LOCK_1" //$NON-NLS-1$
									: "UNDO_PRESENTATION_UNLOCK_1"), //$NON-NLS-1$
									txt);
				}
			}
			return Locale.getString(FSMEditor.class,
					(this.lockingValue ?
							"UNDO_PRESENTATION_LOCK_0" //$NON-NLS-1$
							: "UNDO_PRESENTATION_UNLOCK_0")); //$NON-NLS-1$
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class EdgeLayoutActionUndo extends AbstractCallableUndoableEdit {

		private static final long serialVersionUID = 7639235968304348939L;

		private final PolylineEdgeFigure<?> figure;
		private final DrawingMethod method;
		private DrawingMethod previousMethod;

		/**
		 * @param figure
		 * @param method
		 */
		@SuppressWarnings("unused")
		public EdgeLayoutActionUndo(PolylineEdgeFigure<?> figure, DrawingMethod method) {
			this.figure = figure;
			this.method = method;
			this.previousMethod = figure.getDrawingMethod();
		}

		@Override
		public boolean replaceEdit(UndoableEdit anEdit) {
			if (anEdit instanceof EdgeLayoutActionUndo) {
				EdgeLayoutActionUndo elau = (EdgeLayoutActionUndo)anEdit;
				if (this.figure==elau.figure && this.method!=elau.previousMethod) {
					this.previousMethod = elau.previousMethod;
					return true;
				}
			}
			return false;
		}

		@Override
		public void doEdit() {
			if (this.figure!=null) {
				this.figure.setDrawingMethod(this.method);
			}
		}

		@Override
		public void undoEdit() {
			if (this.figure!=null) {
				this.figure.setDrawingMethod(this.previousMethod);
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public String getPresentationName() {
			if (this.figure!=null) {
				String txt = this.figure.toString();
				if (txt!=null && !txt.isEmpty()) {
					return Locale.getString(FSMEditor.class,
							"UNDO_PRESENTATION_EDGELAYOUT_1", //$NON-NLS-1$
							txt);
				}
			}
			return Locale.getString(FSMEditor.class, "UNDO_PRESENTATION_EDGELAYOUT_0"); //$NON-NLS-1$
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class GenerateJavaAction extends StandardAction {

		private static final long serialVersionUID = 702118077009730939L;

		/**
		 * @throws IOException
		 */
		public GenerateJavaAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_GENERATE_JAVA")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_GENERATE_JAVA")); //$NON-NLS-1$
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void actionPerformed(ActionEvent e) {
			generateJava();
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private class GanswerSugiyamaLayoutAction extends StandardAction {

		private static final long serialVersionUID = 3195811072879756982L;

		private final boolean isVertical;

		/**
		 * @param vertical
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public GanswerSugiyamaLayoutAction(boolean vertical) throws IOException {
			this.isVertical = vertical;
			setText(Locale.getString(FSMEditor.class, 
					vertical ? "ACTION_NAME_V_LAYER_LAYOUT" : "ACTION_NAME_H_LAYER_LAYOUT")); //$NON-NLS-1$ //$NON-NLS-2$
			setToolTipText(Locale.getString(FSMEditor.class,
					vertical ? "TOOLTIP_V_LAYER_LAYOUT" : "TOOLTIP_H_LAYER_LAYOUT")); //$NON-NLS-1$ //$NON-NLS-2$
			setIcon(loadIcon(vertical ? V_LAYER_LAYOUT_ICON : H_LAYER_LAYOUT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			setCursor(AwtUtil.getCursor(MouseCursor.WAIT));
			try {
				GanswerSugiyamaFigureLayout layout = new GanswerSugiyamaFigureLayout();
				layout.setLayoutDirection(
						this.isVertical ? FigureLayoutDirection.VERTICAL : FigureLayoutDirection.HORIZONTAL);
				Undoable edit = layout.layoutFigures(FSMEditor.this.figurePanel.getFigures());
				if (edit!=null) {
					FSMEditor.this.figurePanel.getUndoManager().add(edit);
					FSMEditor.this.figurePanel.defaultView(true);
				}
			}
			finally {
				setCursor(AwtUtil.getCursor(MouseCursor.DEFAULT));
			}
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private class ForceBasedLayoutAction extends StandardAction {

		private static final long serialVersionUID = -8290542994565836963L;

		/**
		 * @throws IOException
		 */
		@SuppressWarnings("synthetic-access")
		public ForceBasedLayoutAction() throws IOException {
			setText(Locale.getString(FSMEditor.class, "ACTION_NAME_FB_LAYOUT")); //$NON-NLS-1$
			setToolTipText(Locale.getString(FSMEditor.class, "TOOLTIP_FB_LAYOUT")); //$NON-NLS-1$
			setIcon(loadIcon(FORCE_LAYOUT_ICON));
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void actionPerformed(ActionEvent e) {
			setCursor(AwtUtil.getCursor(MouseCursor.WAIT));
			try {
				ForceBasedFigureLayout layout = new ForceBasedFigureLayout();
				Undoable edit = layout.layoutFigures(FSMEditor.this.figurePanel.getFigures());
				if (edit!=null) {
					FSMEditor.this.figurePanel.getUndoManager().add(edit);
					FSMEditor.this.figurePanel.defaultView(true);
				}
			}
			finally {
				setCursor(AwtUtil.getCursor(MouseCursor.DEFAULT));
			}
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 16.0
	 */
	private static class FileChooserAccessory extends JPanel {

		private static final long serialVersionUID = -7157390431819042692L;

		private final JRadioButton[] buttons;

		/**
		 */
		public FileChooserAccessory() {
			NetEditorContentType[] values = NetEditorContentType.values();

			this.buttons = new JRadioButton[values.length];

			setLayout(new GridLayout(values.length+1, 1));
			setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

			JLabel label = new JLabel(Locale.getString(FSMEditor.class, "FILE_CHOOSER_SELECT_TYPE")); //$NON-NLS-1$
			add(label);

			ButtonGroup group = new ButtonGroup();

			JRadioButton first = null;

			int i=0;
			for(NetEditorContentType type : values) {
				this.buttons[i] = new JRadioButton(
						Locale.getString(FSMEditor.class, "FILE_CHOOSER_SELECT_TYPE_LABEL", //$NON-NLS-1$
								Locale.getString(FSMEditor.class, type.name())));
				if (first==null) first = this.buttons[i];
				group.add(this.buttons[i]);
				add(this.buttons[i]);
				++i;
			}

			assert(first!=null);
			first.setSelected(true);
		}

		/** Replies the selected content type.
		 * 
		 * @return the selected content type.
		 */
		public NetEditorContentType getContentType() {
			NetEditorContentType[] values = NetEditorContentType.values();
			for(int i=0; i<this.buttons.length; ++i) {
				if (this.buttons[i].isSelected()) {
					return values[i];
				}
			}
			return null;
		}

	}

}
