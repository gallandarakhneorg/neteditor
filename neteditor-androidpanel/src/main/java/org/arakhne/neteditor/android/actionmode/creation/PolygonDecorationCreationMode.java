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

import org.arakhne.afc.math.continous.object2d.Path2f;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.PolylineFigure;
import org.arakhne.neteditor.android.R;

import android.view.ActionMode;


/** This class implements a Mode that permits to
 * create polylines.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class PolygonDecorationCreationMode extends AbstractPolypointDecorationCreationMode {

	/** Construct a new RectangleDecorationCreationMode. The 
	 *  {@code ActionModeManager} should be
	 *  set before using this object.
	 */
	public PolygonDecorationCreationMode() {
		super();
	}
	
	@Override
	protected void onActionBarOpened(ActionMode bar) {
		bar.setTitle(R.string.actionmode_create_polyline_decoration);
	}

	@Override
	protected DecorationFigure createFigure(UUID viewId, Path2f path) {
		PolylineFigure pl = new PolylineFigure(viewId);
		pl.setCtrlPoints(path.toCollection());
		pl.setClosed(true);
		pl.setFilled(true);
		pl.setFramed(true);
		return pl;
	}

}