/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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
package org.arakhne.neteditor.swing.dnd ;

import java.awt.datatransfer.DataFlavor;
import java.net.URL;

/** Data flavors for the figures.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum FigureDataFlavor { 

	/** Index of the NetEditor XML flavor.
	 */
	NGR(create("application/neteditor-graph;class=\""+URL.class.getName()+"\"")), //$NON-NLS-1$ //$NON-NLS-2$

	/** Index of the GXL flavor.
	 */
	GXL(create("application/gfx")), //$NON-NLS-1$

	/** Index of the XML flavor.
	 */
	XML(create("application/xml")), //$NON-NLS-1$

	/** Scalable vector Graphics.
	 */
	SVG(create("image/svg+xml")), //$NON-NLS-1$
	
	/** Portable Document Format.
	 */
	PDF(create("application/pdf")), //$NON-NLS-1$

	/** PostScript.
	 */
	PS(create("application/postscript")), //$NON-NLS-1$

	/** Graphviz dot file.
	 */
	DOT(create("text/x-graphviz")), //$NON-NLS-1$
	
	/** Index of the image flavor.
	 */
	IMAGE(DataFlavor.imageFlavor),

	/** Index of the string flavor.
	 */
	STRING(DataFlavor.stringFlavor);
	
	private final DataFlavor flavor;
	
	private FigureDataFlavor(DataFlavor flavor) {
		this.flavor = flavor;
	}
	
	/** Replies the data flavor associated to the enumeration value.
	 * 
	 * @return the data flavor.
	 */
	public DataFlavor getDataFlavor() {
		return this.flavor;
	}
	
	private static DataFlavor[] flavors = null;

	private static DataFlavor create(String mimeType) {
		try {
			return new DataFlavor(mimeType);
		}
		catch (ClassNotFoundException e) {
			throw new Error(e);
		}
	}

	/** Replies all the flavors described by this enumeration.
	 * 
	 * @return the flavors.
	 */
	public static DataFlavor[] getFlavors() {
		if (flavors==null) {
			DataFlavor[] tab = new DataFlavor[values().length];
			int i=0;
			for(FigureDataFlavor flavor : values()) {
				tab[i] = flavor.getDataFlavor();
				++i;
			}
			flavors = tab;
		}
		return flavors;
	}
	
	/** Replies the enumeration that is corresponding to
	 * the specified flavor.
	 * 
	 * @param flavor
	 * @return the enumeration value; or <code>null</code>
	 */
	public static FigureDataFlavor valueOf(DataFlavor flavor) {
		for(FigureDataFlavor f : values()) {
			if (f.getDataFlavor().equals(flavor)) {
				return f;
			}
		}
		return null;
	}

}
