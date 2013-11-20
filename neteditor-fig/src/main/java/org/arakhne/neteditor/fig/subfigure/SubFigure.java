/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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
package org.arakhne.neteditor.fig.subfigure;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.view.ViewComponent;

/** This interface represents a generic figure.
 * All the coordinates in a subfigure are relative
 * to its containing figure.
 * <p>
 * The active state of a subfigure depends on the
 * semantics of this subfigure. It is used by 
 * the parent figure to change the drawing
 * of the subfigure from active to inactive.
 * The way how a subfigure is activated depends
 * on the subfigure itself.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface SubFigure extends ViewComponent {
	
	/** Replies the containing figure.
	 * 
	 * @return the containing figure.
	 */
	public Figure getParent();
	
	/** Replies the absolute x position.
	 * <p>
	 * {@code absX = parentNode.x + this.x}
	 * 
	 * @return the absolute x position.
	 */
	public float getAbsoluteX();

	/** Replies the absolute y position.
	 * <p>
	 * {@code absY = parentNode.y + this.y}
	 * 
	 * @return the absolute y position.
	 */
	public float getAbsoluteY();

	/** Replies the absolute bounds of this sub-figure.
	 * <p>
	 * <pre><code>
	 * absBounds = ( x => parentNode.x + this.x,
	 *               y => parentNode.y + this.y,
	 *               width => this.width,
	 *               height => this.height)
	 * </code></pre>
	 * 
	 * @return the bounds in absolute coordinates.
	 * @see #getBounds()
	 * @since 11.0
	 */
	public Rectangle2f getAbsoluteBounds();

}
