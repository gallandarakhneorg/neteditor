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

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.TextFigure;
import org.arakhne.neteditor.android.R;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


/** This class implements a Mode that permits to
 * create text boxes.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TextDecorationCreationMode extends AbstractRectangularDecorationCreationMode {

	/** Construct a new RectangleDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public TextDecorationCreationMode() {
		super();
	}
	
	@Override
	protected org.arakhne.neteditor.android.actionmode.creation.AbstractAndroidCreationMode.ActionBar createActionBarListener() {
		return new ActionBar();
	}
	
	@Override
	protected Shape2f getShape(Rectangle2f bounds) {
		return bounds;
	}
	
	@Override
	protected DecorationFigure createFigure(UUID viewId, Rectangle2f bounds) {
		TextFigure figure = new TextFigure(viewId);
		figure.setBounds(bounds);
		android.view.ActionMode bar = getActionBar();
		if (bar!=null) {
			CheckBox cb = (CheckBox)bar.getCustomView().findViewById(R.id.isfilled);
			figure.setFilled(cb.isChecked());
			cb = (CheckBox)bar.getCustomView().findViewById(R.id.isframed);
			figure.setFramed(cb.isChecked());
			EditText et = (EditText)bar.getCustomView().findViewById(R.id.text);
			figure.setText(et.getText().toString());
		}
		return figure;
	}

	/** Action bar listener
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class ActionBar extends AbstractAndroidCreationMode.ActionBar {

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
			super.onCreateActionMode(mode, menu);
			ActionModeOwner container = getModeManagerOwner();
			View customView = LayoutInflater.from(container.getContext()).inflate(R.layout.neteditor_actionmodetextdecoration, null);
			mode.setCustomView(customView);
			TextView titleWidget = (TextView)customView.findViewById(R.id.title);
			titleWidget.setText(R.string.actionmode_create_text_decoration);
			return true;
		}
		
	} // class ActionBar
	
}