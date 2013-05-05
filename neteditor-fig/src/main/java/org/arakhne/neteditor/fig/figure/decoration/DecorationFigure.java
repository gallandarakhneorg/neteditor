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
package org.arakhne.neteditor.fig.figure.decoration;

import java.util.Collections;
import java.util.UUID;

import org.arakhne.neteditor.fig.figure.AbstractFigure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;

/** Abstract class to present a decoration figure.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class DecorationFigure extends AbstractFigure {
    
	private static final long serialVersionUID = -1907564260060053377L;

	/** Construct a new AbstractNodeFigure.
     *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
     * @param x horizontal postition of the upper-left corner of this FigNode.
     * @param y vertical postition of the upper-left corner of this FigNode.
     * @param width is the width of the decoration.
     * @param height is the height of the decoration.
     */
    public DecorationFigure(UUID viewUUID, float x, float y, float width, float height) {
        super(viewUUID, x, y, width, height );
    }
    
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateAssociatedGeometry() {
		//
	}

	/** {@inheritDoc} 
	 */
	@Override
	public Iterable<? extends SubFigure> getSubFigures() {
		return Collections.emptyList();
	}
	
}
