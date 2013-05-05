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
package org.arakhne.neteditor.android.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.ui.android.filechooser.FileChooser;
import org.arakhne.afc.ui.android.progress.ProgressDialogProgressionListener;
import org.arakhne.afc.ui.android.property.PropertyEditors;
import org.arakhne.afc.io.filefilter.FileFilter;
import org.arakhne.afc.io.filefilter.GMLFileFilter;
import org.arakhne.afc.io.filefilter.GXLFileFilter;
import org.arakhne.afc.io.filefilter.GraphMLFileFilter;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.ui.actionmode.ActionPointerEvent;
import org.arakhne.afc.ui.actionmode.SelectableInteractionEvent;
import org.arakhne.afc.ui.actionmode.SelectableInteractionListener;
import org.arakhne.afc.ui.selection.Selectable;
import org.arakhne.afc.ui.selection.SelectionEvent;
import org.arakhne.afc.ui.selection.SelectionListener;
import org.arakhne.afc.ui.undo.AbstractUndoable;
import org.arakhne.afc.ui.undo.UndoListener;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.neteditor.android.R;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner.Callback;
import org.arakhne.neteditor.android.actionmode.FigureActionModeManager;
import org.arakhne.neteditor.android.actionmode.SelectionMode;
import org.arakhne.neteditor.android.event.FigureEvent;
import org.arakhne.neteditor.android.event.FigureListener;
import org.arakhne.neteditor.android.filechooser.NetEditorFileChooserIconSelector;
import org.arakhne.neteditor.android.property.FigurePropertyEditorView;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.ModelObjectListener;
import org.arakhne.neteditor.io.NetEditorContentType;
import org.arakhne.neteditor.io.NetEditorReader;
import org.arakhne.neteditor.io.NetEditorWriter;
import org.arakhne.neteditor.io.gml.GMLReader;
import org.arakhne.neteditor.io.gml.GMLWriter;
import org.arakhne.neteditor.io.graphml.GraphMLReader;
import org.arakhne.neteditor.io.graphml.GraphMLWriter;
import org.arakhne.neteditor.io.gxl.GXLReader;
import org.arakhne.neteditor.io.gxl.GXLWriter;
import org.arakhne.neteditor.io.ngr.NGRReader;
import org.arakhne.neteditor.io.ngr.NGRWriter;
import org.arakhne.vmutil.Android;
import org.arakhne.vmutil.Android.AndroidException;
import org.arakhne.vmutil.FileSystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

/** This abstract activity permits to edit an document 
 * with the NetEditor API.
 * 
 * @param <G> is the type of the graph supported by this editor.
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractEditorActivity<G extends Graph<?,?,?,?>> extends Activity {

	/** Tag for logs.
	 */
	protected static final String TAG = "NetEditor"; //$NON-NLS-1$

	/** Detect the type of the given file.
	 * 
	 * @param file
	 * @return the type of the given file or <code>null</code> if it is a NGR file.
	 */
	protected static NetEditorContentType decode(File file) {
		if (new GraphMLFileFilter().accept(file)) {
			return NetEditorContentType.GRAPHML;
		}
		if (new GXLFileFilter().accept(file)) {
			return NetEditorContentType.GXL;
		}
		if (new GMLFileFilter().accept(file)) {
			return NetEditorContentType.GML;
		}
		return null;
	}

	private final Listener eventListener = new Listener();
	private final SelectionActionMode selectionListener = new SelectionActionMode();

	private File currentDocument = null;
	private NetEditorContentType currentDocumentType = null;
	private boolean hasChanged = false; 
	private Runnable saveListener = null;
	private ActionMode selectionActionMode = null;
	private boolean killOnFinish = false;

	private Callback fileSelectionCallback = null;
	
	private CharSequence defaultTitle = null;
	
	/** Set the callback that may be invoked by the mode's file selector.
	 * 
	 * @param callback
	 */
	void setFileSelectionCallback(Callback callback) {
		this.fileSelectionCallback = callback;
	}

	/** Ask the user if he wants to save its document and run the
	 * specified action if a positive answer if given.
	 * <p>
	 * The user may answer in following ways:
	 * <ul>
	 * <li>POSITIVE; the user clicks on the "Yes" button; then the document is saved and the action is run.</li>
	 * <li>NEGATIVE; the user clicks on the "No" button; then the document is not saved and the action is run.</li>
	 * <li>CANCEL; the user clicks outside the dialog box or on the back button; then the document is not saved and the action is not run.</li>
	 * </ul>
	 * 
	 * @param action is the action to run.
	 */
	protected void runDifferedActionAfterSaving(DifferedActionType action) {
		if (action!=null) {
			AlertDialog.Builder dlg = new AlertDialog.Builder(this);
			dlg.setMessage(getString(R.string.msg_save_file));
			SavingAndDifferedActionAlertListener listener = 
					new SavingAndDifferedActionAlertListener(action);
			dlg.setPositiveButton(R.string.yes, listener);
			dlg.setNegativeButton(R.string.no, listener);
			dlg.setOnCancelListener(listener);
			dlg.setCancelable(true);
			dlg.setIcon(android.R.drawable.ic_dialog_alert);
			dlg.setTitle(R.string.msg_saving);
			dlg.show();
		}
	}

	/** Invoked to register the property editors that are known by the activity.
	 */
	@SuppressWarnings("static-method")
	protected void onRegisterPropertyEditors() {
		PropertyEditors.registerEditorFragment(
				FigurePropertyEditorView.class, Figure.class);
	}

	/** Replies if the selection manager is enabled.
	 * 
	 * @return <code>true</code> if the selection manager is enabled.
	 */
	public boolean isSelectionEnabled() {
		return getFigureView().isSelectionEnabled();
	}

	/** Set if the selection manager is enabled.
	 * 
	 * @param enable is <code>true</code> if the selection manager is enabled.
	 */
	public void setSelectionEnabled(boolean enable) {
		getFigureView().setSelectionEnabled(enable);
	}

	/** Replies the filename of the current document.
	 * 
	 * @return the filename of the current document.
	 */
	public File getCurrentDocument() {
		return this.currentDocument;
	}

	/** Replies the encoding type of the current document.
	 * 
	 * @return the encoding type of the current document.
	 */
	public NetEditorContentType getCurrentDocumentType() {
		return this.currentDocumentType;
	}

	/** Replies if the current document has been changed since
	 * its last loading.
	 * 
	 * @return <code>true</code> if the document has changed since
	 * the last loading; <code>false</code> if the document remains
	 * the same.
	 */
	public boolean hasChanged() {
		return this.hasChanged;
	}

	/**
	 * Show an error dialog box.
	 * 
	 * @param e
	 */
	protected void showError(Throwable e) {
		Throwable cause = e;
		while (cause.getCause()!=null && cause.getCause()!=e) {
			cause = cause.getCause();
		}
		Log.e(TAG, cause.getLocalizedMessage(), cause);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage(cause.getClass().getName()+": "+cause.getLocalizedMessage()); //$NON-NLS-1$
		builder.setIcon(R.drawable.ic_dialog_alert);
		builder.setTitle(android.R.string.dialog_alert_title);
		builder.setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {  
			@Override  
			public void onClick(DialogInterface dialog, int which) {  
				dialog.dismiss();                      
			}  
		});
		AlertDialog dlg = builder.create();
		dlg.show();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.defaultTitle = getTitle();

		// Initialize the Arakhne-VM-utility library to
		// support this Android activity
		try {
			Android.initialize(this);
		}
		catch (AndroidException e) {
			Log.e(TAG, e.getLocalizedMessage(), e);
		}

		setContentView(R.layout.neteditor_activity);

		FigureView<G> figureView = getFigureView();
		assert(figureView!=null);
		try {
			figureView.setGraph(getPreferredGraphType().newInstance());
			figureView.setFigureFactory(getPreferredFigureFactory().newInstance());
		} 
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		figureView.addModelObjectListener(this.eventListener);
		figureView.addFigureListener(this.eventListener);
		figureView.getSelectionManager().addSelectionListener(this.eventListener);
		figureView.getActionModeManager().addSelectableInteractionListener(this.eventListener);

		// Register any property editor
		onRegisterPropertyEditors();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}

	/** Replies the viewer of figures.
	 * 
	 * @return the viewer of figures.
	 */
	@SuppressWarnings("unchecked")
	public FigureView<G> getFigureView() {
		FigureView<G> view = null;

		View v = findViewById(R.id.figureView);
		if (v instanceof FigureView) {
			view = (FigureView<G>)v;
		}

		return view;
	}
	
	@Override
	public boolean onCreateThumbnail(Bitmap outBitmap, Canvas canvas) {
		return super.onCreateThumbnail(outBitmap, canvas);
	}

	@Override
	public void onBackPressed() {
		if (this.hasChanged) {
			runDifferedActionAfterSaving(DifferedActionType.EXIT);
		}
		else {
			killApplication();
		}
	}

	/** Kill the application.
	 */
	protected void killApplication() {
		this.killOnFinish = true;
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (this.killOnFinish) {
			/*
			 * Notify the system to finalize and collect all objects of the app
			 * on exit so that the virtual machine running the app can be killed
			 * by the system without causing issues. NOTE: If this is set to
			 * true then the virtual machine will not be killed until all of its
			 * threads have closed.
			 */
			System.runFinalization();

			/*
			 * Force the system to close the app down completely instead of
			 * retaining it in the background. The virtual machine that runs the
			 * app will be killed. The app will be completely created as a new
			 * app in a new virtual machine running in a new process if the user
			 * starts the app again.
			 */
			System.exit(0);
		}
	}

	/** Invoked when the user want to perform the standard action
	 * on the given figures.
	 * <p>
	 * By default this function does nothing.
	 * 
	 * @param isEditionEnabled indicates if the figures can be edited.
	 * @param hitPoint is the point where the action is performed. It may be <code>null</code> if
	 * the position is unknown.
	 * @param figures are the figures on which the action must be performed
	 */
	protected void onActionPerformed(boolean isEditionEnabled, Point2D hitPoint, Figure... figures) {
		//
	}
	
	/** Invoked when the user want to perform the standard secondary action
	 * on the given figures.
	 * <p>
	 * By default this function open the property editor for the figures.
	 * 
	 * @param isEditionEnabled indicates if the figures can be edited.
	 * @param figures
	 */
	protected void onSecondaryActionPerformed(boolean isEditionEnabled, Figure... figures) {
		PropertyEditors.showDialog(
				this,
				isEditionEnabled,
				getFigureView().getUndoManager(),
				Arrays.asList(figures));
	}

	/** Replies if the activity is in selection mode.
	 * 
	 * @return <code>true</code> if in selection mode.
	 */
	public boolean isSelectionMode() {
		return this.selectionActionMode!=null;
	}

	/** {@inheritDoc}
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case ActivityResultIdentifiers.LOAD_FILE_REQUEST_CODE:
			// If the file selection was successful
			if (resultCode == RESULT_OK) {		
				if (data != null) {
					// Get the URI of the selected file
					Uri uri = data.getData();
					try {
						// Create a file instance from the URI
						URI jURI = new URI(uri.toString());
						URL jURL = jURI.toURL();
						File file = FileSystem.convertURLToFile(jURL);
						LoadingTask task = new LoadingTask(file);
						task.execute();
					}
					catch (Exception e) {
						showError(e);
					}
				}
			}
			break;
		case ActivityResultIdentifiers.SAVE_FILE_REQUEST_CODE:
			// If the file selection was successful
			if (resultCode == RESULT_OK) {
				if (data != null) {
					// Get the URI of the selected file
					Uri uri = data.getData();
					try {
						// Create a file instance from the URI
						URI jURI = new URI(uri.toString());
						URL jURL = jURI.toURL();
						File file = FileSystem.convertURLToFile(jURL);

						if (!file.exists()) {
							SavingTask task = new SavingTask(file, decode(file));
							task.execute();
							Runnable r = this.saveListener;
							this.saveListener = null;
							if (r!=null) r.run();
						}
						else {
							AlertDialog.Builder dlg = new AlertDialog.Builder(this);
							dlg.setMessage(getString(R.string.msg_overwrite_file, file.getName()));
							OverwriteAlertListener listener = new OverwriteAlertListener(file, decode(file));
							dlg.setPositiveButton(R.string.yes, listener);
							dlg.setNegativeButton(R.string.no, listener);
							dlg.setOnCancelListener(listener);
							dlg.setCancelable(true);
							dlg.setIcon(android.R.drawable.ic_dialog_alert);
							dlg.setTitle(R.string.msg_saving);
							dlg.show();
						}
					}
					catch (Exception e) {
						showError(e);
					}
				}
			}
			break;
		case ActivityResultIdentifiers.MODE_FILE_SELECTION_REQUEST_CODE:
			// If the file selection was successful
			if (resultCode == RESULT_OK && this.fileSelectionCallback!=null) {
				// Get the URI of the selected file
				Uri uri = data.getData();
				try {
					// Create a file instance from the URI
					URI jURI = new URI(uri.toString());
					URL jURL = jURI.toURL();
					this.fileSelectionCallback.onFileSelected(jURL);
				}
				catch (Exception e) {
					showError(e);
				}
			}
			this.fileSelectionCallback = null;
			break;
		default:
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/** Run the new action.
	 * 
	 * @param askToSave is <code>true</code> if the user must answer
	 * to "Want to save?".
	 */
	protected void runNewDocumentAction(boolean askToSave) {
		if (this.hasChanged && askToSave) {
			runDifferedActionAfterSaving(DifferedActionType.NEW_DOCUMENT);
		}
		else {
			FigureView<G> figureView = getFigureView();
			assert(figureView!=null);
			figureView.setGraph(null);
			this.currentDocument = null;
			this.currentDocumentType = null;
			this.hasChanged = false;
			runOnUiThread(new UiTitleChange(
					this.defaultTitle,
					AbstractEditorActivity.this));
		}
	}

	/** Run the loading action.
	 * 
	 * @param askToSave is <code>true</code> if the user must answer
	 * to "Want to save?".
	 */
	protected void runLoadAction(boolean askToSave) {
		if (this.hasChanged && askToSave) {
			runDifferedActionAfterSaving(DifferedActionType.LOAD_DOCUMENT);
		}
		else {
			Class<? extends FileFilter> type = getPreferredFileFilter();
			if (type==null) type = OpenableDocumentFileFilter.class;
			FileChooser.showOpenChooser(
					this,
					ActivityResultIdentifiers.LOAD_FILE_REQUEST_CODE,
					R.string.menu_load,
					this.currentDocument,
					type,
					NetEditorFileChooserIconSelector.class);
		}
	}

	/** Replies the type of the preferred file filter for this activity.
	 * 
	 * @return the file filter type of <code>null</code> for the default type.
	 * @see OpenableDocumentFileFilter
	 */
	protected abstract Class<? extends FileFilter> getPreferredFileFilter();

	/** Replies the type of the graph supported by this editor.
	 * 
	 * @return the type of the graph.
	 */
	protected abstract Class<G> getPreferredGraphType();

	/** Replies the type of the figure factory supported by this editor.
	 * 
	 * @return the type of the figure factory.
	 */
	protected abstract Class<? extends FigureFactory<G>> getPreferredFigureFactory();

	/** Run the saving action.
	 * The user is not asked prior saving.
	 */
	protected void runSaveAction() {
		if (this.hasChanged) {
			if (this.currentDocument==null) {
				runSaveAsAction();
			}
			else {
				SavingTask task = new SavingTask(this.currentDocument, this.currentDocumentType);
				task.execute();
			}
		}
	}

	/** Run the save-as action.
	 */
	protected void runSaveAsAction() {
		Class<? extends FileFilter> type = getPreferredFileFilter();
		if (type==null) type = OpenableDocumentFileFilter.class;
		FileChooser.showSaveChooser(
				this,
				ActivityResultIdentifiers.SAVE_FILE_REQUEST_CODE,
				R.string.menu_saveas,
				this.currentDocument,
				type,
				NetEditorFileChooserIconSelector.class);
	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class LoadingTask extends AsyncTask<String,Integer,Graph<?,?,?,?>> {

		private ProgressDialog dialog = null;
		private Throwable error = null;
		private final File inputFile;
		private boolean isInterruptible = true;
		private final String title;
		private final String topMessage;

		/**
		 * @param inputFile
		 */
		public LoadingTask(File inputFile) {
			this.inputFile = inputFile;
			this.title = getString(R.string.msg_loading);
			if (this.inputFile!=null)
				this.topMessage = getString(R.string.msg_loading_file, this.inputFile.getName());
			else
				this.topMessage = this.title;
		}

		/** Cancel the task.
		 */
		protected void cancel() {
			cancel(this.isInterruptible);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPreExecute() {
			if (this.dialog==null) {
				this.dialog = new ProgressDialog(AbstractEditorActivity.this);
				this.dialog.setTitle(this.title);
				this.dialog.setMessage(this.topMessage);
				this.dialog.setIndeterminate(true);
				this.dialog.setCancelable(true);
				this.dialog.setCanceledOnTouchOutside(true);
				this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				this.dialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						cancel();
					}
				});
				this.dialog.show();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(Graph<?, ?, ?, ?> result) {
			if (this.dialog!=null) {
				if (this.dialog.isShowing())
					this.dialog.dismiss();
				this.dialog = null;
			}
			if (this.error!=null) {
				showError(this.error);
				this.error = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected Graph<?, ?, ?, ?> doInBackground(String... params) {
			ProgressDialogProgressionListener progressionListener = new ProgressDialogProgressionListener(
					AbstractEditorActivity.this, this.dialog, this.topMessage);
			DefaultProgression progression = new DefaultProgression(0, 100);
			progression.addProgressionListener(progressionListener);
			try {
				NetEditorReader reader;
				NetEditorContentType type = decode(this.inputFile);
				if (type!=null) {
					switch(type) {
					case GRAPHML:
						reader = new GraphMLReader();
						break;
					case GXL:
						reader = new GXLReader();
						break;
					case GML:
						reader = new GMLReader();
						break;
					default:
						throw new IllegalStateException();
					}
				}
				else {
					reader = new NGRReader();
				}

				reader.setProgression(progression.subTask(95));
				Map<UUID,List<ViewComponent>> figures = new TreeMap<UUID,List<ViewComponent>>();
				G g = reader.read(
						getPreferredGraphType(),
						this.inputFile,
						figures);

				progression.ensureNoSubTask();

				FigureView<G> figureView = getFigureView();
				assert(figureView!=null);
				figureView.setGraph(null);

				this.isInterruptible = false;
				if (!isCancelled()) {
					figureView.setIgnoreRepaint(true);
					try {
						figureView.importGraph(g, figures);
					}
					finally {
						figureView.setIgnoreRepaint(false);
					}
					if (!figureView.resetView()) {
						figureView.repaint();
					}

					AbstractEditorActivity.this.currentDocument = this.inputFile;
					AbstractEditorActivity.this.currentDocumentType = reader.getContentType();
					AbstractEditorActivity.this.hasChanged = false;

					runOnUiThread(new UiTitleChange(
							getString(R.string.title_activity_editor_file, this.inputFile.getName()),
							AbstractEditorActivity.this));
				}
			}
			catch (Throwable e) {
				this.error = e;
			}
			finally {
				progression.end();
				progression.removeProgressionListener(progressionListener);
			}
			return null;
		}

	} // class LoadingTask

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SavingTask extends AsyncTask<String,Integer,Graph<?,?,?,?>> {

		private ProgressDialog dialog = null;
		private Throwable error = null;
		private File tempFile = null;
		private final File outputFile;
		private final NetEditorContentType type;
		private boolean isInterruptible = true;
		private final String title;
		private final String topMessage;

		/**
		 * @param inputFile
		 * @param type
		 */
		public SavingTask(File inputFile, NetEditorContentType type) {
			this.outputFile = inputFile;
			if (type==null)
				this.type = NetEditorContentType.values()[0];
			else
				this.type = type;
			this.title = getString(R.string.msg_saving);
			if (this.outputFile!=null)
				this.topMessage = getString(R.string.msg_saving_file, this.outputFile.getName());
			else
				this.topMessage = this.title;
		}

		/** Cancel the task.
		 */
		protected void cancel() {
			cancel(this.isInterruptible);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPreExecute() {
			if (this.dialog==null) {
				this.dialog = new ProgressDialog(AbstractEditorActivity.this);
				this.dialog.setTitle(this.title);
				this.dialog.setMessage(this.topMessage);
				this.dialog.setIndeterminate(true);
				this.dialog.setCancelable(true);
				this.dialog.setCanceledOnTouchOutside(true);
				this.dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				this.dialog.setOnCancelListener(new OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						cancel();
					}
				});
				this.dialog.show();
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected void onPostExecute(Graph<?, ?, ?, ?> result) {
			if (this.dialog!=null) {
				if (this.dialog.isShowing())
					this.dialog.dismiss();
				this.dialog = null;
			}
			if (this.error!=null) {
				showError(this.error);
				this.error = null;
			}
			Runnable r = AbstractEditorActivity.this.saveListener;
			AbstractEditorActivity.this.saveListener = null;
			if (!isCancelled() && r!=null) r.run();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		protected Graph<?, ?, ?, ?> doInBackground(String... params) {
			ProgressDialogProgressionListener progressionListener = new ProgressDialogProgressionListener(
					AbstractEditorActivity.this, this.dialog, this.topMessage);
			DefaultProgression progression = new DefaultProgression(0, 100);
			progression.addProgressionListener(progressionListener);
			try {
				NetEditorWriter writer;
				NetEditorContentType type = decode(this.outputFile);
				if (type!=null) {
					switch(type) {
					case GRAPHML:
						writer = new GraphMLWriter();
						break;
					case GXL:
						writer = new GXLWriter();
						break;
					case GML:
						writer = new GMLWriter();
						break;
					default:
						throw new IllegalStateException();
					}
				}
				else {
					NGRWriter ngrWriter = new NGRWriter();
					writer = ngrWriter;
					ngrWriter.setContentType(this.type);
				}

				writer.setProgression(progression.subTask(98));

				this.tempFile = File.createTempFile("tmpsave", null); //$NON-NLS-1$
				this.tempFile.deleteOnExit();
				try {
					FileOutputStream fos = new FileOutputStream(this.tempFile);
					try {
						FigureView<G> view = getFigureView();
						assert(view!=null);
						writer.write(
								fos,
								view.getGraph(),
								view);
					}
					finally {
						fos.close();
					}

					progression.ensureNoSubTask();

					this.isInterruptible = false;
					if (!isCancelled()) {
						FileSystem.copy(this.tempFile, this.outputFile);
						AbstractEditorActivity.this.currentDocument = this.outputFile;
						AbstractEditorActivity.this.currentDocumentType = this.type;
						AbstractEditorActivity.this.hasChanged = false;

						runOnUiThread(new UiTitleChange(
								getString(R.string.title_activity_editor_file, this.outputFile.getName()),
								AbstractEditorActivity.this));
					}						
				}
				finally {
					this.tempFile.delete();
					this.tempFile = null;
				}
			}
			catch (Throwable e) {
				this.error = e;
			}
			finally {
				progression.end();
				progression.removeProgressionListener(progressionListener);
			}
			return null;
		}

	} // class LoadingTask

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class DeletionTask extends AsyncTask<Void,Integer,Void> {

		private Throwable error = null;
		private final Figure[] figures;

		/**
		 * @param figuresToRemove
		 */
		public DeletionTask(Figure[] figuresToRemove) {
			this.figures = figuresToRemove;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected void onPostExecute(Void result) {
			if (this.error!=null) {
				showError(this.error);
				this.error = null;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		protected Void doInBackground(Void... params) {
			try {
				FigureView<?> viewer = getFigureView();
				for(Figure figure : this.figures) {
					if (!figure.isLocked()) {
						viewer.removeFigure(figure);
					}
				}
			}
			catch (Throwable e) {
				this.error = e;
			}
			return null;
		}

	} // class DeletionTask

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class Listener implements ModelObjectListener, FigureListener, SelectionListener, SelectableInteractionListener {

		/**
		 */
		public Listener() {
			//
		}
				
		//------------------------------------
		// FigureListener
		//------------------------------------

		@Override
		public void figureAdded(FigureEvent event) {
			onChangedDocument();
		}

		@Override
		public void figureChanged(FigureEvent event) {
			onChangedDocument();
		}

		@Override
		public void figureRemoved(FigureEvent event) {
			onChangedDocument();
		}

		@SuppressWarnings("synthetic-access")
		private void onChangedDocument() {
			if (!AbstractEditorActivity.this.hasChanged) {
				AbstractEditorActivity.this.hasChanged = true;
				String title;
				if (AbstractEditorActivity.this.currentDocument!=null)
					title = AbstractEditorActivity.this.currentDocument.getName();
				else
					title = getString(android.R.string.unknownName);
				runOnUiThread(new UiTitleChange(
						getString(R.string.title_activity_editor_file_changed, title),
						AbstractEditorActivity.this));
			}
		}

		//------------------------------------
		// SelectionListener
		//------------------------------------

		@SuppressWarnings("synthetic-access")
		@Override
		public void selectionChanged(SelectionEvent event) {
			if (event.isLastEvent()) {
				int count = event.getSource().size();
				if (count>0) {
					// The selection changed and is not empty.
					if (AbstractEditorActivity.this.selectionActionMode==null) {
						AbstractEditorActivity.this.selectionActionMode = startActionMode(AbstractEditorActivity.this.selectionListener);
						getFigureView().getUndoManager().addUndoListener(AbstractEditorActivity.this.selectionListener);
					}
					else {
						AbstractEditorActivity.this.selectionActionMode.invalidate();
					}
					String title;
					if (count>1) {
						title = getString(R.string.n_selected, count);
					}
					else {
						title = getString(R.string.one_selected);
					}
					runOnUiThread(new UiTitleChange(title, AbstractEditorActivity.this.selectionActionMode));
				}
				else if (AbstractEditorActivity.this.selectionActionMode!=null) {
					// The selection becomes empty.
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							getFigureView().getUndoManager().removeUndoListener(AbstractEditorActivity.this.selectionListener);
							AbstractEditorActivity.this.selectionActionMode.finish();
							AbstractEditorActivity.this.selectionActionMode = null;
						}
					});
				}
			}
		}

		//------------------------------------
		// SelectableInteractionListener
		//------------------------------------

		@Override
		public void actionPerformed(SelectableInteractionEvent event) {
			FigureView<?> viewer = getFigureView();
			Selectable s = event.getSelectable();
			if (s instanceof Figure) {
				ActionPointerEvent actionEvent = event.getPointerEvent();
				Point2D position = null;
				if (actionEvent!=null) {
					position = actionEvent.getPosition();
				}
				onActionPerformed(viewer.isEnabled() && viewer.isEditable(), position, (Figure)s);
			}
		}

		@Override
		public void popupPerformed(SelectableInteractionEvent event) {
			//
		}

		@Override
		public boolean figureDeletionPerformed(Selectable figure, boolean deleteModel) {
			return true;
		}

		//------------------------------------
		// ModelObjectListener
		//------------------------------------

		@Override
		public void modelPropertyChanged(ModelObjectEvent event) {
			if (event.getSource() instanceof Graph<?,?,?,?>) {
				onChangedDocument();
			}
		}

		@Override
		public void modelContainerChanged(ModelObjectEvent event) {
			if (event.getSource() instanceof Graph<?,?,?,?>) {
				onChangedDocument();
			}
		}

		@Override
		public void modelLinkChanged(ModelObjectEvent event) {
			if (event.getSource() instanceof Graph<?,?,?,?>) {
				onChangedDocument();
			}
		}

		@Override
		public void modelContentChanged(ModelObjectEvent event) {
			if (event.getSource() instanceof Graph<?,?,?,?>) {
				onChangedDocument();
			}
		}

		@Override
		public void modelComponentAdded(ModelObjectEvent event) {
			if (event.getSource() instanceof Graph<?,?,?,?>) {
				onChangedDocument();
			}
		}

		@Override
		public void modelComponentRemoved(ModelObjectEvent event) {
			if (event.getSource() instanceof Graph<?,?,?,?>) {
				onChangedDocument();
			}
		}

	} // class Listener

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SelectionActionMode implements ActionMode.Callback, UndoListener {

		private MenuItem undoItem;
		private MenuItem redoItem;

		/**
		 */
		public SelectionActionMode() {
			//
		}

		/** Invoked when the action mode is created, ie. when
		 * {@code startActionMode()} was called.
		 * 
		 * @param mode is the new action mode.
		 * @param menu is the menu to populate with action buttons.
		 * @return <code>true</code> if the action mode should
		 * be created, <code>false</code> if entering this mode
		 * should be aborted.
		 */
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			FigureView<?> view = getFigureView();

			if (view.getSelectionManager().isEmpty()) {
				return false;
			}

			mode.getMenuInflater().inflate(R.menu.neteditor_selectionbar, menu);

			MenuItem item = menu.findItem(R.id.menu_multi_selection_mode);
			FigureActionModeManager cfg = view.getActionModeManager();
			cfg.setSelectionMode(SelectionMode.SINGLE);
			item.setIcon(R.drawable.ic_menu_singleselect);

			this.undoItem = menu.findItem(R.id.menu_revert);
			this.redoItem = menu.findItem(R.id.menu_revert_revert);

			return true;
		}

		/** Invoked when the user exists tha action mode.
		 * 
		 * @param mode is the action mode to be destroyed
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void onDestroyActionMode(ActionMode mode) {
			AbstractEditorActivity.this.selectionActionMode = null;
			FigureView<?> view = getFigureView();
			view.getSelectionManager().clear();
		}

		/** Invoked each time the action mode is shown. Always
		 * called after {@link #onCreateActionMode(ActionMode, Menu)}, but
		 * may be called multiple times if the mode is invalidated.
		 * 
		 * @param mode is the action mode to be prepared.
		 * @param menu is the menu to populate with action buttons.
		 * @return <code>true</code> if the menu or action mode
		 * was updated, <code>false</code> otherwise.
		 */
		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			FigureView<?> view = AbstractEditorActivity.this.getFigureView();
			UndoManager undoManager = view.getUndoManager();
			SelectionManager selectionManager = view.getSelectionManager();
			
			MenuItem item = menu.findItem(R.id.menu_locking_action);
			updateLockingIcon(item, selectionManager);

			item = menu.findItem(R.id.menu_revert);
			item.setEnabled(undoManager.canUndo());

			item = menu.findItem(R.id.menu_revert_revert);
			item.setEnabled(undoManager.canRedo());

			item = menu.findItem(R.id.menu_pack_objects);
			item.setEnabled(!selectionManager.isEmpty());

			return true;
		}

		private void updateLockingIcon(MenuItem item, SelectionManager manager) {
			item.setIcon(
					manager.isAllLocked()
					? R.drawable.ic_menu_unlock : R.drawable.ic_menu_lock);
		}

		/**
		 * Invoked each time the user click on an action button.
		 * 
		 * @param mode is the action mode in which the click occurs.
		 * @param item is the clicked action button.
		 * @return <code>true</code> if this callback handled the event,
		 * <code>false</code> if the standard MenuItem invocation should continue.
		 */
		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			FigureView<?> viewer = getFigureView();
			SelectionManager manager = viewer.getSelectionManager();
			int itemId = item.getItemId();
			if (itemId==R.id.menu_multi_selection_mode) {
				FigureActionModeManager cfg = AbstractEditorActivity.this.getFigureView().getActionModeManager();
				boolean multi = cfg.getSelectionMode()==SelectionMode.MULTIPLE;
				if (multi) {
					cfg.setSelectionMode(SelectionMode.SINGLE);
					item.setIcon(R.drawable.ic_menu_singleselect);
				}
				else {
					cfg.setSelectionMode(SelectionMode.MULTIPLE);
					item.setIcon(R.drawable.ic_menu_multiselect);
				}
			return true;
			}
			if (itemId==R.id.menu_locking_action) {
				if (manager.isAllLocked()) {
					boolean changed = false;
					// unlock the figures
					for(Figure fig : manager) {
						fig.setLocked(false);
						changed = true;
					}
					if (changed) {
						updateLockingIcon(item, manager);
					}
				}
				else {
					// lock the figures
					boolean changed = false;
					for(Figure fig : manager) {
						if (fig.isLockable()) {
							fig.setLocked(true);
							changed = true;
						}
					}
					if (changed) {
						updateLockingIcon(item, manager);
					}
				}
				return true;
			}
			if (itemId==R.id.menu_resetView) {
				viewer.resetView();
				return true;
			}
			if (itemId==R.id.menu_edit_selection) {
				if (viewer.isEnabled()) {
					Figure[] tab = new Figure[manager.size()];
					manager.toArray(tab);
					onSecondaryActionPerformed(viewer.isEnabled() && viewer.isEditable(), tab);
				}
				return true;
			}
			if (itemId==R.id.menu_delete_selection) {
				if (viewer.isEnabled() && viewer.isEditable()) {
					Figure[] tab = new Figure[manager.size()];
					manager.toArray(tab);
					DeletionTask task = new DeletionTask(tab);
					task.execute();
				}
				return true;
			}
			if (itemId==R.id.menu_select_all) {
				viewer.getSelectionManager().setSelection(viewer.getFigures());
				return true;
			}
			if (itemId==R.id.menu_invert_selection) {
				viewer.getSelectionManager().toggle(viewer.getFigures());
				return true;
			}
			if (itemId==R.id.menu_pack_objects) {
				if (viewer.isEnabled() && viewer.isEditable()) {
					ObjectPackingUndo undo = new ObjectPackingUndo(
							viewer.getResources().getString(R.string.undo_pack_object),
							viewer.getSelectionManager());
					undo.doEdit();
					viewer.getUndoManager().add(undo);
					return true;
				}
				return false;
			}
			if (itemId==R.id.menu_revert) {
				viewer.getUndoManager().undo();
				return true;
			}
			if (itemId==R.id.menu_revert_revert) {
				viewer.getUndoManager().redo();
				return true;
			}
			return false;
		}

		@Override
		public void undoListChanged(UndoManager manager) {
			if (this.undoItem!=null) {
				this.undoItem.setEnabled(manager.canUndo());
			}
			if (this.redoItem!=null) {
				this.redoItem.setEnabled(manager.canRedo());
			}
		}

	} // class ContextualActionMode

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class UiTitleChange implements Runnable {

		private final CharSequence title;
		private final ActionMode mode;
		private final Activity activity;

		/**
		 * @param title
		 * @param mode
		 */
		public UiTitleChange(CharSequence title, ActionMode mode) {
			this.title = title;
			this.mode = mode;
			this.activity = null;
		}

		/**
		 * @param title
		 * @param activity
		 */
		public UiTitleChange(CharSequence title, Activity activity) {
			this.title = title;
			this.activity = activity;
			this.mode = null;
		}

		@Override
		public void run() {
			if (this.activity!=null) {
				this.activity.setTitle(this.title);
			}
			else if (this.mode!=null) {
				this.mode.setTitle(this.title);
			}
		}

	} // class TitleChange

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class OverwriteAlertListener implements OnClickListener, OnCancelListener {

		private final File outputFile;
		private final NetEditorContentType type;

		/**
		 * @param outputFile
		 * @param type
		 */
		public OverwriteAlertListener(File outputFile, NetEditorContentType type) {
			this.outputFile = outputFile;
			this.type = type;
		}

		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			switch (which){
			case DialogInterface.BUTTON_POSITIVE:
				SavingTask task = new SavingTask(this.outputFile, this.type);
				task.execute();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				runSaveAsAction();
				break;
			default:
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			dialog.dismiss();
		}

	}

	/** Type of differed action that may be run by
	 * the function {@link AbstractEditorActivity#runDifferedActionAfterSaving(DifferedActionType)}.
	 * 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected static enum DifferedActionType {
		/** The application will exit. The function
		 * {@link AbstractEditorActivity#killApplication()} will be invoked.
		 */
		EXIT,
		/** A new document must be created. The function
		 * {@link AbstractEditorActivity#runNewDocumentAction} will be invoked
		 * with <code>false</code> as parameter.
		 */
		NEW_DOCUMENT,
		/** A document must be loaded from the file system. The function
		 * {@link AbstractEditorActivity#runLoadAction} will be invoked
		 * with <code>false</code> as parameter.
		 */
		LOAD_DOCUMENT,
	}

	/** 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class SavingAndDifferedActionAlertListener implements OnClickListener, OnCancelListener, Runnable {

		private final DifferedActionType action;


		/**
		 * @param action is the action to run after saving.
		 */
		public SavingAndDifferedActionAlertListener(DifferedActionType action) {
			this.action = action;
		}

		@SuppressWarnings("synthetic-access")
		@Override
		public void onClick(DialogInterface dialog, int which) {
			dialog.dismiss();
			switch (which){
			case DialogInterface.BUTTON_POSITIVE:
				// Launch the saving action.
				// The saveListener will be notified after saving is done.
				// The saveListener will reaction accoring to the differed action.
				AbstractEditorActivity.this.saveListener = this;
				runSaveAction();
				break;
			case DialogInterface.BUTTON_NEGATIVE:
				// The used wants to run the differed action, but not save the document
				// before.
				run();
				break;
			default:
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			dialog.dismiss();
		}

		/** Run the differed action.
		 */
		@Override
		public void run() {
			switch(this.action) {
			case EXIT:
				AbstractEditorActivity.this.killApplication();
				break;
			case NEW_DOCUMENT:
				runNewDocumentAction(false);
				break;
			case LOAD_DOCUMENT:
				runLoadAction(false);
				break;
			default:
				// Nothing to do?
			}
		}

	}

	/**
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ObjectPackingUndo extends AbstractUndoable {

		private static final long serialVersionUID = 6364416347702509595L;
		
		private final String label;
		private final Map<Figure,Rectangle2f> bounds = new TreeMap<Figure,Rectangle2f>();

		/**
		 * @param label
		 * @param figures
		 */
		public ObjectPackingUndo(String label, Iterable<Figure> figures) {
			this.label = label;
			for(Figure figure : figures) {
				if (!figure.isLocked()) {
					this.bounds.put(figure, figure.getBounds());
				}
			}
		}

		@Override
		public String getPresentationName() {
			return this.label;
		}

		@Override
		protected void doEdit() {
			for(Figure figure : this.bounds.keySet()) {
				figure.fitToContent();
			}
		}

		@Override
		protected void undoEdit() {
			for(Entry<Figure,Rectangle2f> entry : this.bounds.entrySet()) {
				entry.getKey().setBounds(entry.getValue());
			}
		}
		
	}
	
}
