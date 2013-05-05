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
package org.arakhne.neteditor.android.filechooser;

import java.io.File;

import org.arakhne.afc.io.filefilter.GMLFileFilter;
import org.arakhne.afc.io.filefilter.GXLFileFilter;
import org.arakhne.afc.io.filefilter.GraphMLFileFilter;
import org.arakhne.afc.io.filefilter.NGRFileFilter;
import org.arakhne.afc.ui.android.filechooser.FileChooserIconSelector;
import org.arakhne.neteditor.android.R;

/**
 * This selector permits to select an icon for a file in a file chooser.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class NetEditorFileChooserIconSelector implements FileChooserIconSelector {

	private static final GXLFileFilter GXL = new GXLFileFilter();
	private static final GMLFileFilter GML = new GMLFileFilter();
	private static final GraphMLFileFilter GRAPHML = new GraphMLFileFilter();
	private static final NGRFileFilter NGR = new NGRFileFilter();
	
	/**
	 */
	public NetEditorFileChooserIconSelector() {
		//
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public int selectIconFor(File file, int defaultIcon) {
		if (file!=null) {
			if (NGR.accept(file))
				return R.drawable.ic_ngr;
			if (GML.accept(file))
				return R.drawable.ic_gml;
			if (GRAPHML.accept(file))
				return R.drawable.ic_graphml;
			if (GXL.accept(file))
				return R.drawable.ic_gxl;
		}
		return defaultIcon;
	}

}