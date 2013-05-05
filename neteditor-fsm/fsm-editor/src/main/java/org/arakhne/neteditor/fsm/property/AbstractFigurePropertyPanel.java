/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.fsm.property ;

import java.lang.ref.WeakReference;

import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeEvent;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeListener;

/** Abstract implementation of a property panel.
 *
 * @param <F>
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractFigurePropertyPanel<F extends Figure>
extends AbstractPropertyPanel<F> implements ViewComponentPropertyChangeListener {

	private static final long serialVersionUID = -7400277944080315909L;
	
	private WeakReference<F> figure = null;
	
	/**
	 * @param type
	 * @param undoManager
	 */
	public AbstractFigurePropertyPanel(Class<F> type, UndoManager undoManager) {
		super(type, undoManager);
	}

	/** Replies the edited figure.
	 * 
	 * @return the figure.
	 */
	public F getFigure() {
		return this.figure == null ? null : this.figure.get();
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void setFigure(Figure figure) {
		if (isSupported(figure)) {
			F old = getFigure();
			if ((old==null && figure!=null) || (old!=null && old!=figure)) {
				if (old!=null) {
					old.removeViewComponentPropertyChangeListener(this);
				}
				this.figure = figure==null ? null : new WeakReference<F>(castFigure(figure));
				updateContent();
				if (figure!=null) {
					figure.addViewComponentPropertyChangeListener(this);
				}
			}
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void release() {
		Figure fig = getFigure();
		if (fig!=null)
			fig.removeViewComponentPropertyChangeListener(this);
	}

	/** Update the enable state of the swing components.
	 */
	protected abstract void updateEnableState();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(ViewComponentPropertyChangeEvent event) {
		updateContent();
	}
	
}
