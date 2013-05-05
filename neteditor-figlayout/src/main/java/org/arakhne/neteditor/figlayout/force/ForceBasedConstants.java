/* 
 * $Id$
 * 
 * Copyright (C) 2012 Stephane GALLAND
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

package org.arakhne.neteditor.figlayout.force;

import org.arakhne.neteditor.figlayout.FigureLayoutConstants;


/** Constants for the force-based laying-out algorithms.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://en.wikipedia.org/wiki/Force-based_algorithms_(graph_drawing)"
 * @since 16.0
 */
public interface ForceBasedConstants extends FigureLayoutConstants {

	/** The Coulomb's constant k.
	 */
	public static final float DEFAULT_COULOMB_CONSTANT = 10f;
	
	/** The spring constant k in the Hooke's equation.
	 */
	public static final float DEFAULT_SPRING_CONSTANT = .001f;
	
	/** Default mass: 1.
	 */
	public static final float DEFAULT_MASS = 1f;

	/** Default damping: 0.8.
	 */
	public static final float DEFAULT_DAMPING = .8f;
	
	/** Default time step: 500ms
	 */
	public static final float DEFAULT_TIME_STEP = 1f;
	
	/** The energy below which the graph is assumed to be stable.
	 */
	public static final float DEFAULT_STABILITY_ENERGY_THRESHOLD = .2f;

}
