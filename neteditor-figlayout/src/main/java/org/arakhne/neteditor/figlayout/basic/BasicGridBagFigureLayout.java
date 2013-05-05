/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND
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

package org.arakhne.neteditor.figlayout.basic;

import java.util.Collection;

import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.vector.Margins;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.figlayout.AbstractDirectionBasedFigureLayout;
import org.arakhne.neteditor.figlayout.FigureLayoutUndoableEdit;
import org.arakhne.vmutil.locale.Locale;

/** Laying out figures on a grid. This laying out algorithm assumes that
 * all the given nodes are decorations.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BasicGridBagFigureLayout extends AbstractDirectionBasedFigureLayout {

	/**
	 */
	public BasicGridBagFigureLayout() {
		//
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Undoable layoutFigures(Collection<? extends Figure> figures) {
		FigureLayoutUndoableEdit undo = new FigureLayoutUndoableEdit(
				Locale.getString(BasicGridBagFigureLayout.class, "UNDO_NAME")); //$NON-NLS-1$
		if (!figures.isEmpty()) {
			int ncolumns = (int)Math.sqrt(figures.size());
			assert(ncolumns>=1);
			
			float x = getOrigin().getX();
			float y = getOrigin().getY();
			float max = 0;
			float size;
			int c = 0;
			Margins insets = getMargins();
			
			switch(getLayoutDirection()) {
			case HORIZONTAL: 
				for(Figure figure : figures) {
					if (figure instanceof EdgeFigure<?>) {
						EdgeFigure<?> edgeFigure = (EdgeFigure<?>)figure;
						while (edgeFigure.getCtrlPointCount()>2) {
							undo.addControlPointRemoval(edgeFigure, 1);
							edgeFigure.removeCtrlPointAt(1);
						}
					}
					else if (!(figure instanceof CoercedFigure)) {
						if (c>=ncolumns) {
							x = getOrigin().getX();
							y += max;
							max = 0;
							c = 0;
						}
						
						size = figure.getHeight() + insets.top() + insets.bottom();
						if (size>max) max = size;
						
						x += insets.left();
						undo.addLocationChange(figure, x, y + insets.top());
						figure.setLocation(x, y + insets.top());
						x += insets.right();
						
						++c;
					}
				}
				break;
			case VERTICAL: 
				for(Figure figure : figures) {
					if (figure instanceof EdgeFigure<?>) {
						EdgeFigure<?> edgeFigure = (EdgeFigure<?>)figure;
						while (edgeFigure.getCtrlPointCount()>2) {
							undo.addControlPointRemoval(edgeFigure, 1);
							edgeFigure.removeCtrlPointAt(1);
						}
					}
					else if (!(figure instanceof CoercedFigure)) {
						if (c>=ncolumns) {
							y = getOrigin().getY();
							x += max;
							max = 0;
							c = 0;
						}
						
						size = figure.getWidth() + insets.left() + insets.right();
						if (size>max) max = size;
						
						y += insets.top();
						undo.addLocationChange(figure, x + insets.left(), y);
						figure.setLocation(x + insets.left(), y);
						y += insets.bottom();
						
						++c;
					}
				}
				break;
			default:
			}
		}
		if (undo.isEmpty()) return null;
		return undo;
	}
		
}
