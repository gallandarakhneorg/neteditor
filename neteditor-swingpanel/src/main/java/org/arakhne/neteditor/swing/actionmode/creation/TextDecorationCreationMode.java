/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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
package org.arakhne.neteditor.swing.actionmode.creation ;

import javax.swing.JComponent;
import javax.swing.JOptionPane;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.TextFigure;
import org.arakhne.neteditor.swing.graphics.SwingViewGraphics2D;

/** This class implements a Mode that permits to
 * create texts.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class TextDecorationCreationMode extends AbstractRectangularDecorationCreationMode {

        /** Construct a new RectangleDecorationCreationMode with the given parent.
         *
         * @param persistent indicates if the mode is persistent or not.
         * @param modeManager a reference to the ModeManager that
         *                    contains this Mode.
         */
        public TextDecorationCreationMode(boolean persistent, ActionModeManager<Figure,SwingViewGraphics2D,Color> modeManager) { 
                super(persistent, modeManager);
        }

        /** Construct a new RectangleDecorationCreationMode. The 
         *  {@code ActionModeManager} should be
         *  set before using this object.
         *  
         * @param persistent indicates if the mode is persistent or not.
         */
        public TextDecorationCreationMode(boolean persistent) {
                super(persistent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Shape2f getShape(Rectangle2f bounds) {
                return bounds;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected DecorationFigure createFigure() {
                JComponent parent = null;
                if (getModeManagerOwner() instanceof JComponent) {
                        parent = (JComponent)getModeManagerOwner();
                }

                String text = JOptionPane.showInputDialog(parent,
                                Locale.getString(TextDecorationCreationMode.class, "MESSAGE")); //$NON-NLS-1$
                if (text!=null && !text.isEmpty()) {
                        TextFigure figure = new TextFigure(getModeManager().getViewID());
                        figure.setText(text);
                        return figure;
                }
                return null;
        }
        
}