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
package org.arakhne.neteditor.fsm.figures ;

import java.util.UUID;

import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.vector.PathUtil;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedTextFigure;
import org.arakhne.neteditor.fig.figure.edge.PolylineEdgeFigure;
import org.arakhne.neteditor.fig.figure.edge.symbol.TriangleEdgeSymbol;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeEvent;
import org.arakhne.neteditor.fig.view.ViewComponentPropertyChangeListener;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.formalism.ModelObjectEvent.Type;
import org.arakhne.neteditor.fsm.constructs.FSMTransition;

/** Figure for the transitions of the FSM.
 *  
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMTransitionFigure extends PolylineEdgeFigure<FSMTransition> {

	private static final long serialVersionUID = 8666446314149023212L;

	private final ViewComponentPropertyChangeListener listener = new ViewComponentPropertyChangeListener() {
		@Override
		public void propertyChange(ViewComponentPropertyChangeEvent event) {
			if (PROPERTY_TEXT.equals(event.getPropertyName())) {
				String text = (String)event.getNewValue();
				int idx = text.indexOf("/"); //$NON-NLS-1$
				if (idx==-1) {
					getModelObject().setGuard(text);
					getModelObject().setAction(null);
				}
				else {
					getModelObject().setGuard(text.substring(0, idx));
					getModelObject().setAction(text.substring(idx+1));
				}
			}
		}
	};
	
	/**
	 * @param viewId
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 */
	public FSMTransitionFigure(UUID viewId, float x1, float y1, float x2, float y2) {
		super(viewId, x1, y1, x2, y2);
		setEndSymbol(new TriangleEdgeSymbol(true));
	}
		
	/**
	 * @param viewId
	 */
	public FSMTransitionFigure(UUID viewId) {
		this(viewId, 0, 0, 0, 0);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoercedFigure addAssociatedFigureIntoView(String figureId,
			CoercedFigure figure) {
		CoercedFigure previous = super.addAssociatedFigureIntoView(figureId, figure);
		if (previous instanceof CoercedTextFigure) {
			previous.removeViewComponentPropertyChangeListener(this.listener);
		}
		if (figure instanceof CoercedTextFigure) {
			figure.addViewComponentPropertyChangeListener(this.listener);
		}
		return previous;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public CoercedFigure removeAssociatedFigureFromView(String figureId) {
		CoercedFigure fig = super.removeAssociatedFigureFromView(figureId);
		if (fig instanceof CoercedTextFigure) {
			fig.removeViewComponentPropertyChangeListener(this.listener);
		}
		return fig;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateFromModel(ModelObjectEvent event) {
		super.updateFromModel(event);
		if ((event==null)
			|| (event.getType()==Type.PROPERTY_CHANGE &&
				(FSMTransition.PROPERTY_GUARD.equals(event.getPropertyName()) 
				||FSMTransition.PROPERTY_ACTION.equals(event.getPropertyName())))) { 
			FSMTransition mo = getModelObject();
			String label = mo==null ? "" : mo.getExternalLabel(); //$NON-NLS-1$
			if (label.isEmpty()) {
				removeAssociatedFigureFromView("majorLabel"); //$NON-NLS-1$
			}
			else {
				Figure figure = getAssociatedFigureInView("majorLabel"); //$NON-NLS-1$
				if (figure==null) {
					Point2D anchor = PathUtil.interpolate(getPath(), .5f);
					CoercedTextFigure text = new CoercedTextFigure(
							getViewUUID(),
							label,
							anchor.getX(),
							anchor.getY());
					text.setAnchorDescriptor(.5f);
					addAssociatedFigureIntoView("majorLabel", text); //$NON-NLS-1$
				}
				else if (figure instanceof CoercedTextFigure) {
					CoercedTextFigure text = (CoercedTextFigure)figure;
					text.setText(label);
					text.fitToContent();
				}
			}
		}
	}
			
}
