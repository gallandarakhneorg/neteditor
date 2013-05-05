/* 
 * $Id$
 * 
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

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.prefs.Preferences;

import javax.swing.JComponent;
import javax.swing.JFileChooser;

import org.arakhne.afc.io.filefilter.PDFFileFilter;
import org.arakhne.afc.ui.actionmode.ActionModeManager;
import org.arakhne.afc.ui.awt.VirtualScreenGraphics2D;
import org.arakhne.afc.ui.swing.FileFilterSwing;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.decoration.BitmapFigure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.decoration.PdfFigure;
import org.arakhne.vmutil.FileSystem;

/** This class implements a Mode that permits to
 * create PDF viewers.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public class PdfDecorationCreationMode extends AbstractRectangularDecorationCreationMode {

        /** Construct a new RectangleDecorationCreationMode with the given parent.
         *
         * @param persistent indicates if the mode is persistent or not.
         * @param modeManager a reference to the ModeManager that
         *                    contains this Mode.
         */
        public PdfDecorationCreationMode(boolean persistent, ActionModeManager<Figure,VirtualScreenGraphics2D,java.awt.Color> modeManager) { 
                super(persistent, modeManager);
        }

        /** Construct a new RectangleDecorationCreationMode. The 
         *  {@code ActionModeManager} should be
         *  set before using this object.
         *  
         * @param persistent indicates if the mode is persistent or not.
         */
        public PdfDecorationCreationMode(boolean persistent) {
                super(persistent);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected Shape getShape(Rectangle2D bounds) {
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
                fileChooser.addChoosableFileFilter(new FileFilterSwing(new PDFFileFilter()));
                if (fileChooser.showOpenDialog(parent)==JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        
                        prefs.put("DEFAULT_DIRECTORY", selectedFile.getParentFile().getAbsolutePath()); //$NON-NLS-1$
                        
                        PdfFigure figure = new PdfFigure(getModeManager().getViewID());
                        try {
                                figure.setPdfURL(FileSystem.convertFileToURL(selectedFile));
                                return figure;
                        }
                        catch (IOException e) {
                                getModeManagerOwner().fireError(e);
                        }
                }
                return null;
        }
        
}