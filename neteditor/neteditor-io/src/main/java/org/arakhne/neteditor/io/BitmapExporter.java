/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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

import java.io.IOException;
import java.io.OutputStream;

import org.arakhne.afc.progress.Progression;
import org.arakhne.neteditor.fig.view.ViewComponentContainer;

/** This interface represents exporters into graphic
 *  formats such as GIF, JPEG...
 *
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface BitmapExporter {

	/** Replies the task progression model used by
	 * this reader.
	 * 
	 * @return the task progression model.
	 */
	public Progression getProgression();

	/** Set the task progression model used by
	 * this reader.
	 * 
	 * @param model is the task progression model.
	 */
	public void setProgression(Progression model);

	/** Replies if the shadows of the objects are exported or not.
	 * 
	 * @return <code>true</code> if the shadows of the objects are also exported.
	 */
	public boolean isShadowExported();

	/** Set if the shadows of the objects are exported or not.
	 * 
	 * @param export is <code>true</code> if the shadows of the objects are exported.
	 */
	public void setShadowExported(boolean export);

	/** Exports the file.
     *
     * @param stream the output
     * @param container is the container of view componets to export.
     * @param scale is the scaling factor from the model coordinate system to the pixels of the procuded image.
     * For example, a value of {@code 2} means that the produced image will be 2 times larger than the exported model.
	 * @return success status.
     * @throws IOException
     */
    public boolean write(OutputStream stream, ViewComponentContainer<?,?> container, float scale) 
	throws IOException ;

}
