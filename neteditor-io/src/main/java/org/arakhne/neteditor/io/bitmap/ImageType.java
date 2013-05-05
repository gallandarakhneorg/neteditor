/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.bitmap ;

import java.io.File;

import org.arakhne.afc.io.filefilter.BMPFileFilter;
import org.arakhne.afc.io.filefilter.FileFilter;
import org.arakhne.afc.io.filefilter.GIFFileFilter;
import org.arakhne.afc.io.filefilter.JPEGFileFilter;
import org.arakhne.afc.io.filefilter.PNGFileFilter;
import org.arakhne.vmutil.FileSystem;


/** Types of supported image formats supported
 * by the exporters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum ImageType {

	/** Microsoft and IBM Bitmap */
	BMP(BMPFileFilter.EXTENSION, false),

	/** Graphics Interchange Format */
	GIF(GIFFileFilter.EXTENSION, true),

	/**  Joint Photographic Experts Group */
	JPEG(JPEGFileFilter.EXTENSION_JPG, false),

	/** Portable Network Graphics */
	PNG(PNGFileFilter.EXTENSION, true);

	private final String imageIOName;
	private final boolean isAlphaSupported;

	private ImageType(String name, boolean isAlpha) {
		this.imageIOName = name;
		this.isAlphaSupported = isAlpha;
	}

	/** Replies the name to use to create the image
	 * with the ImageIO API.
	 * 
	 * @return the ImageIO name.
	 */
	public String getImageIOName() {
		return this.imageIOName;
	}

	/** Replies if this type of image supports alpha in colors.
	 * 
	 * @return <code>true</code> if alpha is supported; otherwise
	 * <code>false</code>
	 */
	public boolean isAlphaSupported() {
		return this.isAlphaSupported;
	}

	/** Replies the file filters for all the vectorial formats.
	 * 
	 * @return the file filters.
	 */
	public static FileFilter[] getFileFilters() {
		return new FileFilter[] {
				new PNGFileFilter(),
				new JPEGFileFilter(),
				new BMPFileFilter(),
				new GIFFileFilter(),
		};
	}
	
	/** Replies the type of the bitmap from the given file.
	 * 
	 * @param file
	 * @return the type of the image in the file; or <code>null</code>
	 * if the type is not recognized.
	 */
	public static ImageType valueOf(File file) {
		for(ImageType type : values()) {
			if (FileSystem.hasExtension(file, type.imageIOName))
				return type;
		}
		return null;
	}
	
	/** Replies the extension associated to the type.
	 * 
	 * @return the extension.
	 */
	public String getExtension() {
		return this.imageIOName;
	}

}
