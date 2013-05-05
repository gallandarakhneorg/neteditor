/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.android.actionmode.creation ;

import org.arakhne.afc.ui.actionmode.ActionMode;
import org.arakhne.afc.ui.actionmode.ActionModeManagerOwner;
import org.arakhne.afc.ui.undo.UndoListener;
import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.android.R;

import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;

/** This class implements a Mode that permits to
 * create something on Android platforms.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractAndroidCreationMode extends ActionMode<Figure,DroidViewGraphics2D,Color> {

	private android.view.ActionMode actionBar = null;

	/** Construct a new AbstractDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 *  
	 * @param persistent indicates if the mode is persistent or not.
	 */
	public AbstractAndroidCreationMode(boolean persistent) {
		super();
		setPersistent(persistent);
	}
	
	@Override
	public org.arakhne.neteditor.android.actionmode.ActionModeManager getModeManager() {
		return (org.arakhne.neteditor.android.actionmode.ActionModeManager)super.getModeManager();
	}
	
	@Override
	public ActionModeOwner getModeManagerOwner() {
		return (ActionModeOwner)super.getModeManagerOwner();
	}
	
	/** Helping function that permits to stop the creation mode.
	 */
	protected void finish() {
		if (isPersistent()) cleanMode();
		else done();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void onModeActivated() {
		openActionBar();
	}

	/** Invoked when the action bar is opened.
	 * 
	 * @param bar
	 */
	protected void onActionBarOpened(android.view.ActionMode bar) {
		//
	}

	/** Invoked when the action bar is opened.
	 * 
	 * @param bar
	 */
	protected void onActionBarClosed(android.view.ActionMode bar) {
		//
	}

	@Override
	protected void onModeDesactivated() {
		closeActionBar();
		repaint();
	}
	
	/** Replies the current action bar.
	 * 
	 * @return the action bar; or <code>null</code>.
	 */
	protected android.view.ActionMode getActionBar() {
		return this.actionBar;
	}

	/** Open the action bar if opened.
	 */
	protected synchronized void openActionBar() {
		if (this.actionBar==null) {
			ActionBar listener = createActionBarListener();
			if (listener==null) listener = new ActionBar();
			this.actionBar = getModeManagerOwner().startActionBar(listener);
			onActionBarOpened(this.actionBar);
		}
		else {
			this.actionBar.invalidate();
		}
	}

	/** Close the action bar if opened.
	 */
	protected synchronized void closeActionBar() {
		android.view.ActionMode old = disconnectActionBar();
		if (old!=null) {
			old.finish();
		}
	}

	private synchronized android.view.ActionMode disconnectActionBar() {
		android.view.ActionMode old = this.actionBar;
		this.actionBar = null;
		if (old!=null) {
			onActionBarClosed(old);
		}
		return old;
	}

	/** Invoked to create the action bar listener for this mode.
	 * 
	 * @return the listener or <code>null</code> to use the default listener.
	 */
	@SuppressWarnings("static-method")
	protected ActionBar createActionBarListener() {
		return null;
	}

	/** Action bar listener
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class ActionBar implements Callback, UndoListener {

		/** Item "Revert action".
		 */
		protected MenuItem undoItem;
		/** Item "Revert revert action".
		 */
		protected MenuItem redoItem;

		/**
		 */
		public ActionBar() {
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
		public boolean onCreateActionMode(android.view.ActionMode mode, Menu menu) {
			ActionModeManagerOwner<?,?,?> container = getModeManagerOwner();
			UndoManager undoManager = container.getUndoManager();
			mode.getMenuInflater().inflate(R.menu.neteditor_creationmode, menu);
			this.undoItem = menu.findItem(R.id.menu_revert);
			this.redoItem = menu.findItem(R.id.menu_revert_revert);
			undoManager.addUndoListener(this);
			return true;
		}

		/** Invoked when the user exits the action mode.
		 * 
		 * @param mode is the action mode to be destroyed
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public void onDestroyActionMode(android.view.ActionMode mode) {
			ActionModeManagerOwner<?,?,?> container = getModeManagerOwner();
			UndoManager undoManager = container.getUndoManager();
			undoManager.removeUndoListener(this);
			this.undoItem = this.redoItem = null;
			AbstractAndroidCreationMode.this.disconnectActionBar();
			done();
		}
		
		/** Invoked each time the action mode is shown. Always
		 * called after {@link #onCreateActionMode(android.view.ActionMode, Menu)}, but
		 * may be called multiple times if the mode is invalidated.
		 * 
		 * @param mode is the action mode to be prepared.
		 * @param menu is the menu to populate with action buttons.
		 * @return <code>true</code> if the menu or action mode
		 * was updated, <code>false</code> otherwise.
		 */
		@Override
		public boolean onPrepareActionMode(android.view.ActionMode mode, Menu menu) {
			ActionModeManagerOwner<?,?,?> container = getModeManagerOwner();
			UndoManager undoManager = container.getUndoManager();
			this.undoItem.setEnabled(undoManager.canUndo());
			this.redoItem.setEnabled(undoManager.canRedo());
			return true;
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
		public boolean onActionItemClicked(android.view.ActionMode mode, MenuItem item) {
			int itemId = item.getItemId();
			if (itemId==R.id.menu_revert) {
				ActionModeManagerOwner<?,?,?> container = getModeManagerOwner();
				UndoManager undoManager = container.getUndoManager();
				undoManager.undo();
				return true;
			}
			if (itemId==R.id.menu_revert_revert)  {
				ActionModeManagerOwner<?,?,?> container = getModeManagerOwner();
				UndoManager undoManager = container.getUndoManager();
				undoManager.redo();
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

	} // class ActionBar

}