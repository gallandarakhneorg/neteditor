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
package org.arakhne.neteditor.swing.graphics;

import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.vector.Color;

/** Implementation of a graphics context which is
 * delagating to another graphics context but
 * by forcing the transparency drawing parameters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TransparentViewGraphics2D extends DelegatedViewGraphics2D<VirtualScreenGraphics2D> {

	/**
	 * @param context
	 */
	public TransparentViewGraphics2D(VirtualScreenGraphics2D context) {
		super(context);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Graphics2DLOD getLOD() {
		return Graphics2DLOD.SHADOW;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getFillColor() {
		Color c = super.getFillColor();
		if (c!=null) c = c.transparentColor();
		return c;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Color getOutlineColor() {
		Color c = super.getOutlineColor();
		if (c!=null) c = c.transparentColor();
		return c;
	}
		
}
