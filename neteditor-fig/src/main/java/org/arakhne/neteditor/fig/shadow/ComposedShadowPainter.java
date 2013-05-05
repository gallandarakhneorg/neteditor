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
package org.arakhne.neteditor.fig.shadow;

import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;

/** This implementation of ShadowPainter is
 * a composition of shadow painters.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ComposedShadowPainter implements BlockShadowPainter {

	private final Set<ShadowPainter> painters = new TreeSet<ShadowPainter>(ShadowPainterComparator.SINGLETON);
	private final UUID uuid = UUID.randomUUID();
	
	private Figure figure = null;
	private Rectangle2f bufferedBounds = null;
	private Rectangle2f bufferedDamagedBounds = null;
	
	/**
	 */
	public ComposedShadowPainter() {
		//
	}
	
	/** Add the shadow painter associated to the specified figure.
	 * <p>
	 * If <var>isPreferredFigure</var> is <code>true</code> then
	 * the function {@link #getFigure()} will replied the given
	 * figure until this painter is released or the function
	 * {@link #offers(Figure, boolean)} is called with <code>true</code>
	 * as parameter.
	 * 
	 * @param figure is the figure for which a painter must be added.
	 * @param isPreferredFigure
	 */
	public void offers(Figure figure, boolean isPreferredFigure) {
		assert(figure!=null);
		ShadowPainter painter = figure.getShadowPainter();
		assert(painter!=null);
		this.painters.add(painter);
		if (isPreferredFigure || this.figure==null)
			this.figure = figure;
		this.bufferedBounds = null;
		this.bufferedDamagedBounds = null;
	}
	
	/** Replies the painters embedded inside this composed ShadowPainter.
	 * 
	 * @return the reference to the painter set.
	 */
	public Set<ShadowPainter> getPainters() {
		return this.painters;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void moveTo(float dx, float dy) {
		if (dx!=0f || dy!=0f) {
			this.bufferedBounds = new Rectangle2f();
			this.bufferedDamagedBounds = new Rectangle2f();
			boolean init1 = false;
			boolean init2 = false;
			Rectangle2f bb;
			for(ShadowPainter painter : this.painters) {
				painter.moveTo(dx, dy);
				bb = painter.getShadowBounds();
				if (bb!=null) {
					if (init1) {
						this.bufferedBounds.setUnion(bb);
					}
					else {
						init1 = true;
						this.bufferedBounds.set(bb);
					}
				}
				bb = painter.getDamagedBounds();
				if (bb!=null) {
					if (init2) {
						this.bufferedDamagedBounds.setUnion(bb);
					}
					else {
						init2 = true;
						this.bufferedDamagedBounds.set(bb);
					}
				}
			}
		}
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public UUID getUUID() {
		return this.uuid;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Figure getFigure() {
		return this.figure;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Rectangle2f getShadowBounds() {
		if (this.bufferedBounds==null) {
			this.bufferedBounds = new Rectangle2f();
			boolean init = false;
			Rectangle2f bb;
			for(ShadowPainter painter : this.painters) {
				bb = painter.getShadowBounds();
				if (bb!=null) {
					if (init) {
						this.bufferedBounds.setUnion(bb);
					}
					else {
						init = true;
						this.bufferedBounds.set(bb);
					}
				}
			}
		}
		return this.bufferedBounds;
	}
	
	@Override
	public Rectangle2f getDamagedBounds() {
		if (this.bufferedDamagedBounds==null) {
			this.bufferedDamagedBounds = new Rectangle2f();
			boolean init = false;
			Rectangle2f bb;
			for(ShadowPainter painter : this.painters) {
				bb = painter.getDamagedBounds();
				if (bb!=null) {
					if (init) {
						this.bufferedDamagedBounds.setUnion(bb);
					}
					else {
						init = true;
						this.bufferedDamagedBounds.set(bb);
					}
				}
			}
		}
		return this.bufferedDamagedBounds;
	}

	/** {@inheritDoc}
	 */
	@Override
	public void paint(ViewGraphics2D g) {
		for(ShadowPainter painter : this.painters) {
			painter.paint(g);
		}
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public void release() {
		for(ShadowPainter painter : this.painters) {
			painter.release();
		}
		this.painters.clear();
		this.bufferedBounds = null;
		this.bufferedDamagedBounds = null;
		this.figure = null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void resize(float dx, float dy, ResizeDirection direction) {
		if (dx!=0f || dy!=0f) {
			this.bufferedBounds = new Rectangle2f();
			this.bufferedDamagedBounds = new Rectangle2f();
			boolean init1 = false;
			boolean init2 = false;
			Rectangle2f bb;
			for(ShadowPainter painter : this.painters) {
				if (painter instanceof BlockShadowPainter) {
					((BlockShadowPainter)painter).resize(dx, dy, direction);
				}
				bb = painter.getShadowBounds();
				if (bb!=null) {
					if (init1) {
						this.bufferedBounds.setUnion(bb);
					}
					else {
						init1 = true;
						this.bufferedBounds.set(bb);
					}
				}
				bb = painter.getDamagedBounds();
				if (bb!=null) {
					if (init2) {
						this.bufferedDamagedBounds.setUnion(bb);
					}
					else {
						init2 = true;
						this.bufferedDamagedBounds.set(bb);
					}
				}
			}
		}
	}
	
	/** Replies the number of sub-painters stored in this composed painter.
	 * 
	 * @return the number of sub-painters.
	 */
	public int size() {
		return this.painters.size();
	}
	
}
