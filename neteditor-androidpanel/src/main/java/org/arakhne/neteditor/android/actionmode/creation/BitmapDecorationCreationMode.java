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

import java.net.URL;
import java.util.UUID;

import org.arakhne.afc.io.filefilter.ImageFileFilter;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner;
import org.arakhne.neteditor.android.actionmode.ActionModeOwner.Callback;
import org.arakhne.neteditor.fig.figure.decoration.BitmapFigure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.android.R;

import android.app.Activity;
import android.content.Context;
import android.view.ActionMode;


/** This class implements a Mode that permits to
 * create bitmap boxes.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BitmapDecorationCreationMode extends AbstractRectangularDecorationCreationMode {

	/** Construct a new BitmapDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public BitmapDecorationCreationMode() {
		super();
	}
	
	@Override
	protected void onActionBarOpened(ActionMode bar) {
		bar.setTitle(R.string.actionmode_create_bitmap_decoration);
	}

	@Override
	protected Shape2f getShape(Rectangle2f bounds) {
		return bounds;
	}
	
	@Override
	protected DecorationFigure createFigure(UUID viewId, Rectangle2f bounds) {
		final BitmapFigure figure = new BitmapFigure(viewId);
		figure.setBounds(bounds);

		ActionModeOwner container = getModeManagerOwner();
		Context c = container.getContext();
		if (c instanceof Activity) {
			container.selectFile(
					"image/*", //$NON-NLS-1$
					ImageFileFilter.class,
					new Callback() {
						@Override
						public void onFileSelected(URL file) throws Exception {
							figure.setImageURL(file);
						}
					});
		}
		
		return figure;
	}

}