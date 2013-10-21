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
package org.arakhne.neteditor.swing.event ;

import java.util.EventObject;

import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.swing.JFigureView;

/** Event that is describing any change in a JFigureViewer.
 *  
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FigureEvent extends EventObject {

	private static final long serialVersionUID = -8313852001821998151L;
	
	private final Figure removed;
	private final Figure added;
	private final Figure changed;
	
	/**
	 * @param source
	 * @param removed
	 * @param added
	 * @param changed
	 */
	public FigureEvent(JFigureView<?> source, Figure removed, Figure added, Figure changed) {
		super(source);
		this.removed = removed;
		this.added = added;
		this.changed = changed;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public JFigureView<?> getSource() {
		return (JFigureView<?>)super.getSource();
	}
	
	/** Replies the added figure.
	 * 
	 * @return the added figure; or <code>null</code> if none.
	 */
	public Figure getAddedFigure() {
		return this.added;
	}
	
	/** Replies the removed figure.
	 * 
	 * @return the removed figure; or <code>null</code> if none.
	 */
	public Figure getRemovedFigure() {
		return this.removed;
	}

	/** Replies the changed figure.
	 * 
	 * @return the changed figure; or <code>null</code> if none.
	 */
	public Figure getChangedFigure() {
		return this.changed;
	}

}
