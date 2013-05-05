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

import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.neteditor.fig.anchor.AnchorFigure;
import org.arakhne.neteditor.fig.anchor.InvisibleCircleAnchorFigure;
import org.arakhne.neteditor.fig.anchor.InvisibleRoundRectangularAnchorFigure;
import org.arakhne.neteditor.fig.factory.AbstractStandardFigureFactory;
import org.arakhne.neteditor.fig.factory.FigureFactoryException;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.ModelObject;
import org.arakhne.neteditor.formalism.Node;
import org.arakhne.neteditor.fsm.constructs.FSMAnchor;
import org.arakhne.neteditor.fsm.constructs.FSMEndPoint;
import org.arakhne.neteditor.fsm.constructs.FSMStartPoint;
import org.arakhne.neteditor.fsm.constructs.FSMState;
import org.arakhne.neteditor.fsm.constructs.FSMTransition;
import org.arakhne.neteditor.fsm.constructs.FiniteStateMachine;

/** Factory for figures of a FSM.
 *  
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMFigureFactory extends AbstractStandardFigureFactory<FiniteStateMachine> {

	/**
	 */
	public FSMFigureFactory() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Figure createFigureFor(UUID viewID, FiniteStateMachine graph,
			ModelObject object, float x, float y) throws FigureFactoryException {
		Figure fig = null;
		FSMAnchor anchor = null;
		if (object instanceof FSMState) {
			FSMState node = (FSMState) object;
			anchor = node.getAnchors().get(0);
			FSMStateFigure figure = new FSMStateFigure(viewID, x, y);
			figure.setModelObject(node);
			fig = figure;
		}
		else if (object instanceof FSMStartPoint) {
			FSMStartPoint node = (FSMStartPoint) object;
			anchor = node.getAnchors().get(0);
			FSMStartPointFigure figure = new FSMStartPointFigure(viewID, x, y);
			figure.setModelObject(node);
			fig = figure;
		}
		else if (object instanceof FSMEndPoint) {
			FSMEndPoint node = (FSMEndPoint) object;
			anchor = node.getAnchors().get(0);
			FSMEndPointFigure figure = new FSMEndPointFigure(viewID, x, y);
			figure.setModelObject(node);
			fig = figure;
		}
		if (fig!=null && anchor!=null) {
			AnchorFigure<FSMAnchor> subfig;
			if (fig instanceof FSMStateFigure) {
				subfig = createStateAnchorFigure(viewID,
					fig.getWidth(), fig.getHeight());
			}
			else {
				subfig = createEndAnchorFigure(viewID,
					Math.max(fig.getWidth(), fig.getHeight()));
			}
			subfig.setModelObject(anchor);
			return fig;
		}
		throw new FigureFactoryException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SubFigure createSubFigureInside(UUID viewID,
			FiniteStateMachine graph, Figure parent, ModelObject object) {
		if (object instanceof FSMAnchor) {
			if ((parent instanceof FSMStartPointFigure)
				||(parent instanceof FSMEndPointFigure)) {
				AnchorFigure<FSMAnchor> subfig = createEndAnchorFigure(viewID,
						Math.max(parent.getWidth(), parent.getHeight()));
				subfig.setModelObject((FSMAnchor)object);
				return subfig;
			}
			if (parent instanceof FSMStateFigure) {
				AnchorFigure<FSMAnchor> subfig = createStateAnchorFigure(viewID,
						parent.getWidth(), parent.getHeight());
				subfig.setModelObject((FSMAnchor)object);
				return subfig;
			}
		}
		throw new FigureFactoryException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Figure createFigureFor(UUID viewID, FiniteStateMachine graph,
			ModelObject object, float x1, float y1, float x2, float y2) throws FigureFactoryException {
		if (object instanceof FSMTransition) {
			FSMTransitionFigure figure = new FSMTransitionFigure(viewID, x1, y1, x2, y2);
			figure.setModelObject((FSMTransition)object);
			return figure;
		}
		throw new FigureFactoryException();
	}

	private static AnchorFigure<FSMAnchor> createEndAnchorFigure(UUID viewID, float size) {
		AnchorFigure<FSMAnchor> figure = new InvisibleCircleAnchorFigure<FSMAnchor>(
				viewID, 0, 0, size/2f);
		return figure;
	}

	private static AnchorFigure<FSMAnchor> createStateAnchorFigure(UUID viewID, float width, float height) {
		AnchorFigure<FSMAnchor> figure = new InvisibleRoundRectangularAnchorFigure<FSMAnchor>(
				viewID, 0, 0, width, height);
		return figure;
	}

	@Override
	protected Dimension getPreferredNodeSize(Node<?, ?, ?, ?> node) {
		return VectorToolkit.dimension(
				ViewComponentConstants.DEFAULT_MINIMAL_SIZE,
				ViewComponentConstants.DEFAULT_MINIMAL_SIZE);
	}

}