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
package org.arakhne.neteditor.fig.graphics;

import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.vector.VectorGraphics2D;

/** Utilities for ViewGraphics2D.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ViewGraphicsUtil {

	private static Factory FACTORY = null;

	/** Change the inner factory.
	 * 
	 * @param factory is the new factory.
	 */
	public static void setFactory(Factory factory) {
		FACTORY = factory;
	}

	/** Create a ViewGraphics2D that is delegating to the given
	 * VectorGraphics2D.
	 * 
	 * @param g
	 * @param antialiasing permits to force the anti-aliasing flag for the target graphical context
	 * @param isForPrinting indicates if this graphics environment is for printing or not.
	 * @return the view vector.
	 */
	public static ViewGraphics2D createViewGraphics(VectorGraphics2D g, boolean antialiasing, boolean isForPrinting) {
		Factory factory = FACTORY;
		if (factory!=null)
			return factory.createViewGraphics(g, antialiasing, isForPrinting);
		throw new IllegalStateException("You must given a Factory to ViewGraphicsUtil"); //$NON-NLS-1$
	}

	/** Create a ViewGraphics2D that is delegating to the given
	 * VectorGraphics2D.
	 * 
	 * @param g
	 * @param antialiasing permits to force the anti-aliasing flag for the target graphical context
	 * @param isForPrinting indicates if this graphics environment is for printing or not.
	 * @param lod is the desired Level-Of-Details
	 * @return the view vector.
	 */
	public static ViewGraphics2D createViewGraphics(VectorGraphics2D g, boolean antialiasing, boolean isForPrinting, Graphics2DLOD lod) {
		Factory factory = FACTORY;
		if (factory!=null)
			return factory.createViewGraphics(g, antialiasing, isForPrinting, lod);
		throw new IllegalStateException("You must given a Factory to ViewGraphicsUtil"); //$NON-NLS-1$
	}

	/** Factory to create ViewVector2D.
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	public interface Factory {

		/** Create a ViewGraphics2D that is delegating to the given
		 * VectorGraphics2D.
		 * 
		 * @param g
		 * @param antialiasing permits to force the anti-aliasing flag for the target graphical context
		 * @param isForPrinting indicates if this graphics environment is for printing or not.
		 * @param lod is the desired Level-Of-Details
		 * @return the view vector.
		 */
		public ViewGraphics2D createViewGraphics(VectorGraphics2D g, boolean antialiasing, boolean isForPrinting, Graphics2DLOD lod);

		/** Create a ViewGraphics2D that is delegating to the given
		 * VectorGraphics2D.
		 * 
		 * @param g
		 * @param antialiasing permits to force the anti-aliasing flag for the target graphical context
		 * @param isForPrinting indicates if this graphics environment is for printing or not.
		 * @return the view vector.
		 */
		public ViewGraphics2D createViewGraphics(VectorGraphics2D g, boolean antialiasing, boolean isForPrinting);

	}

}
