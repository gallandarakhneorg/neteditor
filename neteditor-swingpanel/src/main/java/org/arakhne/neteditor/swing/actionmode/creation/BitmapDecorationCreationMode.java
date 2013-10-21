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

import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.arakhne.afc.io.filefilter.FileFilter;
import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.swing.FileFilterSwing;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.vmutil.FileSystem;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.BitmapFigure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.io.bitmap.ImageType;
import org.arakhne.neteditor.swing.graphics.SwingViewGraphics2D;

/** This class implements a Mode that permits to
 * create bitmaps.
 *
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class BitmapDecorationCreationMode extends AbstractRectangularDecorationCreationMode {

        /** Construct a new RectangleDecorationCreationMode with the given parent.
         *
         * @param persistent indicates if the mode is persistent or not.
         * @param modeManager a reference to the ModeManager that
         *                    contains this Mode.
         */
        public BitmapDecorationCreationMode(boolean persistent, ActionModeManager<Figure,SwingViewGraphics2D,Color> modeManager) { 
                super(persistent, modeManager);
        }

        /** Construct a new RectangleDecorationCreationMode. The 
         *  {@code ActionModeManager} should be
         *  set before using this object.
         *  
         * @param persistent indicates if the mode is persistent or not.
         */
        public BitmapDecorationCreationMode(boolean persistent) {
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
                Preferences prefs = Preferences.userNodeForPackage(BitmapFigure.class);
                String path = prefs.get("DEFAULT_DIRECTORY", null); //$NON-NLS-1$
                
                File currentDirectory = null;
                if (path!=null && !path.isEmpty()) {
                        currentDirectory = new File(path); 
                }
                
                JComponent parent = null;
                if (getModeManagerOwner() instanceof JComponent) {
                        parent = (JComponent)getModeManagerOwner();
                }
                
                JFileChooser fileChooser = new JFileChooser(currentDirectory);
                for(FileFilter ff : ImageType.getFileFilters()) {
                        fileChooser.addChoosableFileFilter(new FileFilterSwing(ff));
                }
                if (fileChooser.showOpenDialog(parent)==JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        
                        prefs.put("DEFAULT_DIRECTORY", selectedFile.getParentFile().getAbsolutePath()); //$NON-NLS-1$
                        
                        BitmapFigure figure = new BitmapFigure(getModeManager().getViewID());
                        try {
                                figure.setImageURL(FileSystem.convertFileToURL(selectedFile));
                                return figure;
                        }
                        catch (IOException e) {
                                getModeManagerOwner().fireError(e);
                        }
                }
                return null;
        }
        
}