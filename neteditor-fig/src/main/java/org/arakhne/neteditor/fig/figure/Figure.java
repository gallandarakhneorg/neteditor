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
package org.arakhne.neteditor.fig.figure;

import java.util.Map;
import java.util.Set;

import org.arakhne.afc.ui.selection.Selectable;
import org.arakhne.afc.ui.vector.Color;
import org.arakhne.neteditor.fig.shadow.ShadowPainter;
import org.arakhne.neteditor.fig.subfigure.SubFigure;
import org.arakhne.neteditor.fig.view.ViewComponent;

/** This interface represents a generic figure.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface Figure extends ViewComponent, Selectable {

	/** Replies the painter that may be used to paint this
	 * figure in shadow mode.
	 * You must call {@link ShadowPainter#release()} when
	 * you have finished to paint the figure in shadow mode.
	 * 
	 * @return the painter, never <code>null</code>
	 */
	public ShadowPainter getShadowPainter();

	/** Set the color of the lines.
	 *
	 * @param color is the color of the lines.
	 */
	public void setLineColor(Color color);

	/** Set the color of the filled areas.
	 *
	 * @param color is the color of the filled areas.
	 */
	public void setFillColor(Color color);

	/** Reply the directions this Fig can be
	 *  resized by the user
	 *
	 * @return the directions
	 */    
	public Set<ResizeDirection> getResizeDirections() ;

	/** Set the directions the figure can be
	 *  resized by the user
	 *
	 * @param resizeDirections the new directions. If none was given,
	 * the shape cannot be resized.
	 */      
	public void setResizeDirections(ResizeDirection... resizeDirections) ;

	/** Set the directions the figure can be
	 *  resized by the user to all the available directions.
	 */      
	public void setResizeAllDirections() ;

	/** Reply <code>true</code> if this Fig can be
	 *  resized by the user
	 *
	 * @return <code>true</code> if this Fig can be resized,
	 *         <code>false</code> otherwise.
	 */
	public boolean isResizable() ;

	/** Replies if the figure accept to be moved.
	 * 
	 * @return <code>true</code> if the figure accept to be moved;
	 * Otherwise <code>false</code>.
	 */
	public boolean isMovable();

	/** Replies if this figure accept to be locked.
	 *
	 * @return <code>true</code> if the figure accept to be locked;
	 * otherwise <code>false</code>.
	 */
	public boolean isLockable() ;

	/** Set the lock-state of this Fig. When a Fig is locked,
	 *  it should not be moved by the user.
	 *
	 * @param lock <code>true</code> if this Fig is locked.
	 */
	public void setLocked(boolean lock) ;

	/** Reply the lock-state of this Fig. When a Fig is locked,
	 *  it should not be moved by the user.
	 *
	 * @return <code>true</code> if this Fig is locked.
	 */
	public boolean isLocked() ;

	/** Reply the lock-state of the associated figures is
	 * automatically updated when the lock-state of this figure
	 * changed.
	 *
	 * @return <code>true</code> if this lock-state of the
	 * associated figures changed as the lock-state of this
	 * figure change; <code>false</code> otherwise.
	 */
	public boolean isAssociatedFiguresAutoLocked() ;

	/** Set if the lock-state of the associated figures is
	 * automatically updated when the lock-state of this figure
	 * changed.
	 *
	 * @param autoLock is <code>true</code> if this lock-state of the
	 * associated figures changed as the lock-state of this
	 * figure change; <code>false</code> otherwise.
	 */
	public void setAssociatedFiguresAutoLocked(boolean autoLock) ;

	/** Replies the color used to outline the figure as locked.
	 * 
	 * @return the locking-state color. 
	 */
	public Color getLockOutlineColor();

	/** Set the color used to outline the figure as locked.
	 * 
	 * @param color is the new locking-state color. If <code>null</code> the default
	 * value will be used.
	 */
	public void setLockOutlineColor(Color color);

	/** Replies the color used to fill the figure as locked.
	 * 
	 * @return the locking-state color. 
	 */
	public Color getLockFillColor();

	/** Set the color used to fill the figure as locked.
	 * 
	 * @param color is the new locking-state color. If <code>null</code> the default
	 * value will be used.
	 */
	public void setLockFillColor(Color color);

	/** Replies the sub figures.
	 * 
	 * @return the sub figures. 
	 */
	public abstract Iterable<? extends SubFigure> getSubFigures();

	/** Change the size of this figure to fit its content
	 * as best as possible.
	 */
	public void fitToContent();
	
	/** Replies the definitions of the properties that may
	 * be edited through an UI.
	 * 
	 * @return the properties.
	 */
	public Map<String,Class<?>> getUIEditableProperties();

}
