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

import org.arakhne.afc.io.filefilter.GMLFileFilter;
import org.arakhne.afc.io.filefilter.GXLFileFilter;
import org.arakhne.afc.io.filefilter.GraphMLFileFilter;
import org.arakhne.afc.io.filefilter.MultiFileFilter;
import org.arakhne.afc.io.filefilter.NGRFileFilter;

/** File filter that is for all the documents openable by the editor.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see NGRFileFilter
 * @see GMLFileFilter
 * @see GXLFileFilter
 * @see GraphMLFileFilter
 */
public class OpenableDocumentFileFilter extends MultiFileFilter {

	/**
	 */
	public OpenableDocumentFileFilter() {
		super(true, null,
				new NGRFileFilter(true),
				new GraphMLFileFilter(true),
				new GMLFileFilter(true),
				new GXLFileFilter(true));
	}

}
