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
package org.arakhne.neteditor.fsm.property ;

import org.arakhne.neteditor.fig.figure.Figure;

/** Property panel for FSM states.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EmptyPropertyPanel extends AbstractPropertyPanel<Figure> {

	private static final long serialVersionUID = 8250177577410765153L;

	/**
	 */
	public EmptyPropertyPanel() {
		super(null, null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isSupported(Figure figure) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setFigure(Figure figure) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateContent() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void release() {
		//
	}
	
}
