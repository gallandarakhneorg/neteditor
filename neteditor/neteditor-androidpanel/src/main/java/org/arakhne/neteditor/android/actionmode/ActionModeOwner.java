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
package org.arakhne.neteditor.android.actionmode ;

import java.net.URL;

import org.arakhne.afc.io.filefilter.FileFilter;
import org.arakhne.afc.ui.actionmode.ActionModeManagerOwner;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.android.graphics.DroidViewGraphics2D;
import org.arakhne.neteditor.fig.factory.FigureFactory;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.formalism.Graph;

import android.content.Context;
import android.view.ActionMode;

/** This interface describes a container of modes.
 * 
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ActionModeOwner extends ActionModeManagerOwner<Figure,DroidViewGraphics2D,Color>, FigureActionModeManager {
	
	/** Returns the graph.
	 * 
	 * @return the graph.
	 */
	public Graph<?,?,?,?> getGraph();
	
	/** Replies the factory of figures.
	 * 
	 * @return the factory.
	 */
	public FigureFactory<?> getFigureFactory();
	
	/** Replies the android context.
	 * 
	 * @return the android context.
	 */
	public Context getContext();
	
	/** Start the action bar with the given listener.
	 * 
	 * @param listener
	 * @return the action bar.
	 */
	public ActionMode startActionBar(ActionMode.Callback listener);
	
	/** Run the given action in the UI thread.
	 * 
	 * @param action
	 */
	public void runOnUIThread(Runnable action);
	
	/** Open a file chooser to select a file of the given type
	 * and run the callback.
	 * 
	 * @param mimeType is the mime type of the file to select.
	 * @param fileFilter is the file filter to use.
	 * @param callback is the object invoked when the file is selected.
	 */
	public void selectFile(String mimeType, Class<? extends FileFilter> fileFilter, Callback callback);

	/** Callback for {@link ActionModeOwner#selectFile(String, Class, Callback)}.
	 * 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public interface Callback {
		
		/** Invoked when a file was selected.
		 * 
		 * @param file
		 * @throws Exception
		 */
		public void onFileSelected(URL file) throws Exception;
		
	}
	
}
