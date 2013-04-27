/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012 Stephane GALLAND.
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
package org.arakhne.neteditor.fig.view;

import org.arakhne.afc.ui.vector.Color;
import org.arakhne.afc.ui.vector.Colors;
import org.arakhne.afc.ui.vector.VectorToolkit;

/** Constants for all the view components.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface ViewComponentConstants {

	/** The precision, on the trigonometric circle, under which the segments are flattening.
	 */
	public static final float FLATTENING_PRECISION = .01f ;

	/** Amount that should be added to bounds to obtain
	 * a damaged bound. 
	 */
	public static final float DEFAULT_DAMAGING_EXTENTS = 3; // To enclose the shadows
	
	/** Default minimal size of the figures in pixels.
	 */
	public static final float DEFAULT_MINIMAL_SIZE = 40;

	/** Default maximal size of the figures in pixels.
	 */
	public static final float DEFAULT_MAXIMAL_SIZE = Float.MAX_VALUE;
	
	/** Default minimal size of the subfigures in pixels.
	 */
	public static final float DEFAULT_MINIMAL_SUBFIGURE_SIZE = 0f;

	/** Default flag that indicates if the shadows should be drawn.
	 * This flag may be otherriden by the preference "DRAW_SHADOW".
	 */
	public static final boolean DEFAULT_DRAW_SHADOW = true;

	/** Default color for the locking state.
	 * This flag may be otherriden by the preference "LOCKING_OUTLINE_COLOR".
	 * This preference is an integer value that is representing
	 * the components of the RGBA color.
	 */
	public static final Color DEFAULT_LOCKING_OUTLINE_COLOR = VectorToolkit.color(128, 128, 128, 255);

	/** Default color for the locking state.
	 * This flag may be otherriden by the preference "LOCKING_FILL_COLOR".
	 * This preference is an integer value that is representing
	 * the components of the RGBA color.
	 */
	public static final Color DEFAULT_LOCKING_FILL_COLOR = VectorToolkit.color(255, 252, 206, 255);

	/** Default color for the selected objects.
	 * This color may be provided by the {@link ViewComponentContainer}
	 * when it is known.
	 * This flag may be otherriden by the preference "BACKGROUND_SELECTION_COLOR".
	 * This preference is an integer value that is representing
	 * the components of the RGBA color.
	 */
	public static final Color DEFAULT_BACKGROUND_SELECTION_COLOR = Colors.BLUE;

	/** Default color for the selected objects.
	 * This color may be provided by the {@link ViewComponentContainer}
	 * when it is known.
	 * This flag may be otherriden by the preference "FOREGROUND_SELECTION_COLOR".
	 * This preference is an integer value that is representing
	 * the components of the RGBA color.
	 */
	public static final Color DEFAULT_FOREGROUND_SELECTION_COLOR = Colors.DARK_GRAY;

	/** Default color for the filled areas.
	 * This flag may be otherriden by the preference "FILL_COLOR".
	 * This preference is an integer value that is representing
	 * the components of the RGBA color.
	 */
	public static final Color DEFAULT_FILL_COLOR = Colors.WHITE;

	/** Default color for the lines.
	 * This flag may be otherriden by the preference "LINE_COLOR".
	 * This preference is an integer value that is representing
	 * the components of the RGBA color.
	 */
	public static final Color DEFAULT_LINE_COLOR = Colors.BLACK;

	/** Default color for the active objects.
	 * This flag may be otherriden by the preference "ACTIVE_COLOR".
	 * This preference is an integer value that is representing
	 * the components of the RGBA color.
	 */
	public static final Color DEFAULT_ACTIVE_COLOR = Colors.ORANGE;
	
	/** Distance to project the shadows.
	 */
	public static final float DEFAULT_SHADOW_PROJECTION_DISTANCE_X = 3f;

	/** Distance to project the shadows.
	 */
	public static final float DEFAULT_SHADOW_PROJECTION_DISTANCE_Y = 3f;

}
