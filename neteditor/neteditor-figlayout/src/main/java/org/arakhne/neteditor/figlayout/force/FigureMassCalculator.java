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

import org.arakhne.neteditor.fig.figure.Figure;

/** This interface represents an object that is able 
 * to compute the mass of a figure.
 * The mass is used durin gthe force-based laying-out algorithm
 * for weighting the positions of the figures.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://en.wikipedia.org/wiki/Force-based_algorithms_(graph_drawing)"
 * @since 16.0
 */
public interface FigureMassCalculator {

	/** Compute the mass for the given figure.
	 * 
	 * @param node
	 * @return the mass.
	 */
	public float computeMassFor(Figure node);

}
