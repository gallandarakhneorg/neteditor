/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io ;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.arakhne.vmutil.FileSystem;


/** Collection of files with a main file.
 * All the files are mapped to temporary files.
 * The function {@link #copyFiles()} permits to
 * copy the temporary files to the target files of
 * the collection.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FileCollection {

	private final File mainFile;
	private final Map<File,File> tempMapping = new TreeMap<File,File>();
	
	/**
	 * @param mainFile is the main file.
	 * @throws IOException
	 */
	public FileCollection(File mainFile) throws IOException {
		this.mainFile = mainFile;
		createFile(this.mainFile);
	}
	
	private File createFile(File original) throws IOException {
		assert(original!=null);
		File t = File.createTempFile("neteditorfilenamefactory", //$NON-NLS-1$
				FileSystem.extension(original));
		this.tempMapping.put(original, t);
		return t;
	}
	
	/** Replies the target main file.
	 * 
	 * @return the target main file.
	 */
	public File getMainFile() {
		return this.mainFile;
	}
	
	/** Replies the temporary file associated to the target main file.
	 * 
	 * @return the temporary file associated to the target main file.
	 */
	public File getTemporaryMainFile() {
		return this.tempMapping.get(this.mainFile);
	}
	
	/** Create a subfile associated to the main file.
	 * 
	 * @param name is the basename of the subfile.
	 * @return the temporary file.
	 * @throws IOException
	 */
	public File createSubFile(String name) throws IOException {
		File newFile = new File(this.mainFile.getParentFile(), name);
		return createFile(newFile);
	}
	
	/** Copy the temporary files
	 * 
	 * @throws IOException
	 */
	public void copyFiles() throws IOException {
		Iterator<Entry<File,File>> iterator = this.tempMapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<File,File> entry = iterator.next();
			FileSystem.copy(entry.getValue(), entry.getKey());
			entry.getValue().delete();
			iterator.remove();
		}
	}
	
	/** Delete all the temporary files.
	 * 
	 * @throws IOException
	 */
	public void deleteTemporaryFiles() throws IOException {
		Iterator<Entry<File,File>> iterator = this.tempMapping.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<File,File> entry = iterator.next();
			entry.getValue().delete();
			iterator.remove();
		}
	}

}
