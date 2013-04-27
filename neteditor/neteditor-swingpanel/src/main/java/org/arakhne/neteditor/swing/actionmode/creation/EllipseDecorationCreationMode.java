/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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
package org.arakhne.neteditor.swing.actionmode.creation ;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.EllipseFigure;

/** This class implements a Mode that permits to
 * create ellipses.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class EllipseDecorationCreationMode extends AbstractRectangularDecorationCreationMode {

        /** Construct a new EllipseDecorationCreationMode with the given parent.
         *
         * @param persistent indicates if the mode is persistent or not.
         * @param modeManager a reference to the ModeManager that
         *                    contains this Mode.
         */
        public EllipseDecorationCreationMode(boolean persistent, ActionModeManager<Figure,VirtualScreenGraphics2D,java.awt.Color> modeManager) { 
                super(persistent, modeManager);
        }

        /** Construct a new EllipseDecorationCreationMode. The 
         *  {@code ActionModeManager} should be
         *  set before using this object.
         *  
         * @param persistent indicates if the mode is persistent or not.
         */
        public EllipseDecorationCreationMode(boolean persistent) {
                super(persistent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected DecorationFigure createFigure() {
                return new EllipseFigure(getModeManager().getViewID());
        }
        
        /**
         * {@inheritDoc}
         */
        @Override
        protected Shape getShape(Rectangle2D bounds) {
                return new Ellipse2D.Double(
                                bounds.getMinX(),
                                bounds.getMinY(),
                                bounds.getWidth(),
                                bounds.getHeight());
        }

}