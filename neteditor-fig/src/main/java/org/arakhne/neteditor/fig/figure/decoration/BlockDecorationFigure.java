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
package org.arakhne.neteditor.fig.figure.decoration;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.fig.figure.BlockFigure;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ResizeDirection;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.shadow.BlockShadowPainter;
import org.arakhne.neteditor.fig.shadow.ShadowPainter;

/** Abstract class to present a decoration figure as a block.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class BlockDecorationFigure extends DecorationFigure implements BlockFigure {
    
	private static final long serialVersionUID = -4526330468941543836L;

	/** Construct a new AbstractNodeFigure.
     *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
     * @param x horizontal postition of the upper-left corner of this FigNode.
     * @param y vertical postition of the upper-left corner of this FigNode.
     * @param width is the width of the decoration.
     * @param height is the height of the decoration.
     */
    public BlockDecorationFigure(UUID viewUUID, float x, float y, float width, float height) {
        super(viewUUID, x, y, width, height );
    }
    	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ShadowPainter createShadowPainter() {
		return new SPainter();
	}
	
	/** Abstract class to present a decoration figure.
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	protected class SPainter implements BlockShadowPainter {

		private final Rectangle2f bounds = new Rectangle2f();
		
		/**
		 */
		public SPainter() {
			//
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public UUID getUUID() {
			return BlockDecorationFigure.this.getUUID();
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Figure getFigure() {
			return BlockDecorationFigure.this;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Rectangle2f getShadowBounds() {
			return this.bounds;
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public Rectangle2f getDamagedBounds() {
			return this.bounds;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void paint(ViewGraphics2D g) {
			g.pushRenderingContext(BlockDecorationFigure.this, getClip(this.bounds), this.bounds);
			BlockDecorationFigure.this.paint(g);
			g.popRenderingContext();
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public synchronized void release() {
			BlockDecorationFigure.this.releaseShadowPainter();
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public synchronized void moveTo(float dx, float dy) {
			if (dx!=0f || dy!=0f) {
				this.bounds.set(
						getX()+dx,
						getY()+dy,
						getWidth(),
						getHeight());
			}
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public void resize(float dx, float dy, ResizeDirection direction) {
			if (dx!=0f || dy!=0f) {
				float nx1 = getX();
				float ny1 = getY();
				float nx2 = nx1 + getWidth();
				float ny2 = ny1 + getHeight();
	
				switch(direction) {
				case NORTH_WEST:
					nx1 += dx;
					ny1 += dy;
					break;
				case WEST:
					nx1 += dx;
					break;
				case SOUTH_WEST:
					nx1 += dx;
					ny2 += dy;
					break;
				case NORTH:
					ny1 += dy;
					break;
				case SOUTH:
					ny2 += dy;
					break;
				case NORTH_EAST:
					nx2 += dx;
					ny1 += dy;
					break;
				case EAST:
					nx2 += dx;
					break;
				case SOUTH_EAST:
					nx2 += dx;
					ny2 += dy;
					break;
				default:
					throw new IllegalStateException();
				}
				if (nx1>nx2) {
					float t = nx1;
					nx1 = nx2;
					nx2 = t;
				}
				if (ny1>ny2) {
					float t = ny1;
					ny1 = ny2;
					ny2 = t;
				}
				this.bounds.setFromCorners(nx1, ny1, nx2, ny2);
			}
		}
		
	}

}
