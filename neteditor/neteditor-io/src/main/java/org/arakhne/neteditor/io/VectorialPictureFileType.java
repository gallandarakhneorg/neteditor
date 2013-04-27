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
package org.arakhne.neteditor.io ;

import java.io.File;

import org.arakhne.afc.io.filefilter.DOTFileFilter;
import org.arakhne.afc.io.filefilter.EPSFileFilter;
import org.arakhne.afc.io.filefilter.FileFilter;
import org.arakhne.afc.io.filefilter.GMLFileFilter;
import org.arakhne.afc.io.filefilter.GXLFileFilter;
import org.arakhne.afc.io.filefilter.GraphMLFileFilter;
import org.arakhne.afc.io.filefilter.PDFFileFilter;
import org.arakhne.afc.io.filefilter.SVGFileFilter;
import org.arakhne.vmutil.FileSystem;

/**
 * Type of vectorial pictures supported by this file filter.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum VectorialPictureFileType {

	/** SCALABLE VECTOR GRAPHICS */
	SVG(SVGFileFilter.EXTENSION),
	/** PORTABLE DOCUMENT FORMAT */
	PDF(PDFFileFilter.EXTENSION),
	/** ENCAPSULED POSTSCRIPT */
	EPS(EPSFileFilter.EXTENSION),
	/** Graphviz */
	DOT(DOTFileFilter.EXTENSION),
	/** GRAPH EXCHANGE FORMAT */
	GXL(GXLFileFilter.EXTENSION),
	/** GRAPHML FORMAT */
	GRAPHML(GraphMLFileFilter.EXTENSION_GRAPHML),
	/** Graph Modelling Language */
	GML(GMLFileFilter.EXTENSION);

	private final String[] extensions;

	private VectorialPictureFileType(String... exts) {
		this.extensions = exts;
	}

	/** Replies the extensions supported by this file type.
	 * 
	 * @return the extensions.
	 */
	public String[] getExtensions() {
		return this.extensions;
	}

	/** Replies the preferred extension supported by this file type.
	 * 
	 * @return the extension without the leading point character.
	 */
	public String getExtension() {
		return this.extensions[0];
	}

	/** Replies if the specified file is of this type.
	 * 
	 * @param file
	 * @return <code>true</code> if the file is of this type;
	 * otherwise <code>false</code>.
	 */
	public boolean isFile(File file) {
		for(String ext : this.extensions) {
			if (FileSystem.hasExtension(file, ext))
				return true;
		}
		return false;
	}
	
	/** Replies the type of the specified file.
	 * 
	 * @param file
	 * @return the type of the file or <code>null</code> if
	 * the file is not an image file.
	 */
	public static VectorialPictureFileType valueOf(File file) {
		for(VectorialPictureFileType type : VectorialPictureFileType.values()) {
			if (type.isFile(file)) return type;
		}
		return null;
	}
	
	
	/** Replies the file filters for all the vectorial formats.
	 * 
	 * @return the file filters.
	 */
	public static FileFilter[] getFileFilters() {
		return new FileFilter[] {
				new SVGFileFilter(),
				new PDFFileFilter(),
				new EPSFileFilter(),
				new DOTFileFilter(),
				new GXLFileFilter(),
				new GraphMLFileFilter(),
		};
	}

}
