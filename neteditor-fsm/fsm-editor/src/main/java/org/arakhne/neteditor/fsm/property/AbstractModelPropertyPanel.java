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
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeEvent;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeListener;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.ModelObjectListener;

/** Abstract implementation of a property panel.
 *
 * @param <F>
 * @param <MO>
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractModelPropertyPanel<F extends ModelObjectFigure<MO>, MO extends ModelObject>
extends AbstractPropertyPanel<F> implements ModelObjectListener, ViewComponentPropertyChangeListener {

	private static final long serialVersionUID = -7497442144472606726L;
	
	private WeakReference<F> figure = null;
	private WeakReference<MO> modelObject = null;
	
	/**
	 * @param type
	 * @param undoManager
	 * @param figure
	 */
	public AbstractModelPropertyPanel(Class<F> type, UndoManager undoManager, F figure) {
		super(type, undoManager);
		setFigure(figure);
	}

	/** Replies the edited model object.
	 * 
	 * @return the model object.
	 */
	public MO getModelObject() {
		return this.modelObject == null ? null : this.modelObject.get();
	}
	
	/** Replies the edited figure.
	 * 
	 * @return the figure.
	 */
	public F getFigure() {
		return this.figure == null ? null : this.figure.get();
	}

	/** Set the figure.
	 * 
	 * @param modelObject
	 */
	public void setModelObject(MO modelObject) {
		MO old = getModelObject();
		if ((old==null && modelObject!=null) || (old!=null && old!=modelObject)) {
			if (old!=null) {
				old.removeModelObjectListener(this);
			}
			this.modelObject = modelObject==null ? null : new WeakReference<MO>(modelObject);
			updateContent();
			if (modelObject!=null) {
				modelObject.addModelObjectListener(this);
			}
		}
	}

	/** {@inheritDoc}
	 */
	@Override
	public void release() {
		F figure = getFigure();
		if (figure!=null)
			figure.removeViewComponentPropertyChangeListener(this);
		ModelObject mo = getModelObject();
		if (mo!=null)
			mo.removeModelObjectListener(this);
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void setFigure(Figure figure) {
		if (isSupported(figure)) {
			F old = getFigure();
			if (old!=figure) {
				if (old!=null) {
					old.removeViewComponentPropertyChangeListener(this);
				}
				F fig = castFigure(figure);
				this.figure = fig==null ? null : new WeakReference<F>(fig);
				if (fig!=null) {
					fig.addViewComponentPropertyChangeListener(this);
					setModelObject(fig.getModelObject());
				}
			}
		}
	}
	
	/** Update the enable state of the swing components.
	 */
	protected abstract void updateEnableState();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modelPropertyChanged(ModelObjectEvent event) {
		updateContent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modelContainerChanged(ModelObjectEvent event) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modelLinkChanged(ModelObjectEvent event) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modelContentChanged(ModelObjectEvent event) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modelComponentAdded(ModelObjectEvent event) {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modelComponentRemoved(ModelObjectEvent event) {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void propertyChange(ViewComponentPropertyChangeEvent event) {
		updateContent();
	}
	
}
