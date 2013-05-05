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
package org.arakhne.neteditor.android.graphics;

import org.arakhne.afc.ui.Graphics2DLOD;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.ui.vector.Paint;

/** Implementation of a graphics context which is
 * delegating to another graphics context but
 * by forcing the transparency drawing parameters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TransparentViewGraphics2D extends DelegatedViewGraphics2D {

	/**
	 * @param context
	 */
	public TransparentViewGraphics2D(DroidViewGraphics2D context) {
		super(context);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public final Graphics2DLOD getLOD() {
		return Graphics2DLOD.SHADOW;
	}
	
	@Override
	protected void onAttributePainting(
			Color fillColor, Color outlineColor,
			Paint paint,
			boolean drawInterior, boolean drawOutline,
			String interiorText) {
		Color fc = fillColor;
		if (fc!=null) {
			fc = fc.transparentColor();
		}
		Color oc = outlineColor;
		if (oc!=null) {
			oc = oc.transparentColor();
		}
		super.onAttributePainting(
				fc, oc, 
				paint, 
				drawInterior, drawOutline,
				interiorText);
	}
	
	@Override
	protected Image onImagePainting(
			Color fillColor, Color outlineColor,
			Paint paint,
			boolean drawInterior, boolean drawOutline,
			String interiorText, Image image) {
		Color fc = fillColor;
		if (fc!=null) {
			fc = fc.transparentColor();
		}
		Color oc = outlineColor;
		if (oc!=null) {
			oc = oc.transparentColor();
		}
		return super.onImagePainting(
				fc, oc, paint, drawInterior,
				drawOutline, interiorText, image);
	}
	
}
