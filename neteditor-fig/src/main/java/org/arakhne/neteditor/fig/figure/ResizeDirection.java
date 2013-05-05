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
package org.arakhne.neteditor.fig.figure;

import java.util.Arrays;
import java.util.Collection;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;

/** Direction of resizing.
 * 
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public enum ResizeDirection {

    /** resize to the north west */		
    NORTH_WEST {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrMinX, rrMinY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrMinX, rrMinY, rrWidth, rrHeight);
		}
    },
    /** resize to the north */
    NORTH {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrCenterX, rrMinY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrCenterX, rrMinY, rrWidth, rrHeight);
		}
    },
    /** resize to the north east */
    NORTH_EAST {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrMaxX, rrMinY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrMaxX, rrMinY, rrWidth, rrHeight);
		}
    },
    /** resize to the west */
    WEST {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrMinX, rrCenterY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrMinX, rrCenterY, rrWidth, rrHeight);
		}
    },
    /** resize to the east */
    EAST {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrMaxX, rrCenterY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrMaxX, rrCenterY, rrWidth, rrHeight);
		}
    },
    /** resize to the southwest */
    SOUTH_WEST {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrMinX, rrMaxY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrMinX, rrMaxY, rrWidth, rrHeight);
		}
    },
    /** resize to the south */
    SOUTH {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrCenterX, rrMaxY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrCenterX, rrMaxY, rrWidth, rrHeight);
		}
    },
    /** resize to the south east */
    SOUTH_EAST {
		@Override
		boolean isResizingRectangle(float x, float y, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isInsideRectangle(x, y, rrMaxX, rrMaxY, rrWidth, rrHeight);
		}
		@Override
		boolean isResizingRectangle(Shape2f pointerArea, float rrMinX, float rrCenterX,
				float rrMaxX, float rrMinY, float rrCenterY, float rrMaxY, float rrWidth,
				float rrHeight) {
			return isIntersectRectangle(pointerArea, rrMaxX, rrMaxY, rrWidth, rrHeight);
		}
    };
    
    /** Replies if the specified point is inside
     * the resizing rectangle of this direction
     * according to the given coordinates of
     * the enclosing figure.
     * 
     * @param x is the coordinate of the point to test.
     * @param y is the coordinate of the point to test.
     * @param rrMinX is the lowest X coordinate of the enclosing figure.
     * @param rrCenterX is the center X coordinate of the enclosing figure.
     * @param rrMaxX is the uppest X coordinate of the enclosing figure.
     * @param rrMinY is the lowest Y coordinate of the enclosing figure.
     * @param rrCenterY is the center Y coordinate of the enclosing figure.
     * @param rrMaxY is the uppest Y coordinate of the enclosing figure.
     * @param rrWidth is the size of the resizing rectangle.
     * @param rrHeight is the size of the resizing rectangle.
     * @return <code>true</code> if the specified point
     * is in the resizing rectangle of this direction;
     * otherwise <code>false</code>
     */
    abstract boolean isResizingRectangle(float x, float y,
    		float rrMinX, float rrCenterX, float rrMaxX,
    		float rrMinY, float rrCenterY, float rrMaxY,
    		float rrWidth, float rrHeight);
	
    /** Replies if the specified area intersects
     * the resizing rectangle of this direction
     * according to the given coordinates of
     * the enclosing figure.
     * 
	 * @param pointerArea is the area covered by the mouse.
     * @param rrMinX is the lowest X coordinate of the enclosing figure.
     * @param rrCenterX is the center X coordinate of the enclosing figure.
     * @param rrMaxX is the uppest X coordinate of the enclosing figure.
     * @param rrMinY is the lowest Y coordinate of the enclosing figure.
     * @param rrCenterY is the center Y coordinate of the enclosing figure.
     * @param rrMaxY is the uppest Y coordinate of the enclosing figure.
     * @param rrWidth is the size of the resizing rectangle.
     * @param rrHeight is the size of the resizing rectangle.
     * @return <code>true</code> if the given area
     * intersects the resizing rectangle of this direction;
     * otherwise <code>false</code>
     */
    abstract boolean isResizingRectangle(Shape2f pointerArea,
    		float rrMinX, float rrCenterX, float rrMaxX,
    		float rrMinY, float rrCenterY, float rrMaxY,
    		float rrWidth, float rrHeight);

    /** Replies if the given point is inside the given rectangle.
     * 
     * @param x
     * @param y
     * @param rrX
     * @param rrY
     * @param rrWidth
     * @param rrHeight
     * @return <code>true</code> if the point is inside the rectangle;
     * otherwise <code>false</code>.
     */
	static boolean isInsideRectangle(float x, float y, float rrX, float rrY,
			float rrWidth, float rrHeight) {
		return (x >= rrX && x <= (rrX+rrWidth))
				&&
				(y >= rrY && y <= (rrY+rrHeight));
	}

    /** Replies if the given area intersects the given rectangle.
     * 
     * @param area
     * @param rrX
     * @param rrY
     * @param rrWidth
     * @param rrHeight
     * @return <code>true</code> if the point is inside the rectangle;
     * otherwise <code>false</code>.
     */
	static boolean isIntersectRectangle(Shape2f area, float rrX, float rrY,
			float rrWidth, float rrHeight) {
		return area.intersects(new Rectangle2f(rrX, rrY, rrWidth, rrHeight));
	}

	/** Reply the direction of the resizing if the mouse is on a resize area.
	 *
	 * @param x the horizontal position of the mouse.
	 * @param y the vertical position of the mouse.
	 * @param bounds are the bounds of the area to consider.
	 * @param handleSize is the size of the handles that permits to resize.
	 * @return the resize direction; or <code>null</code> if
	 * the specified point is not on a valid resizing handler.
	 * @see #getResizingDirection(float, float, Rectangle2f, float, ResizeDirection...)
	 */
	public static ResizeDirection findResizingDirection(float x, float y, Rectangle2f bounds, float handleSize) {
		return getResizingDirection(x, y, bounds, handleSize, ResizeDirection.values());
	}


	/** Reply the direction of the resizing if the mouse is on a resize area.
	 *
	 * @param x the horizontal position of the mouse.
	 * @param y the vertical position of the mouse.
	 * @param bounds are the bounds of the area to consider.
	 * @param handleSize is the size of the handles that permits to resize.
	 * @param directionsToTest are the directions to test.
	 * @return the resize direction; or <code>null</code> if
	 * the specified point is not on a valid resizing handler.
	 * @see #findResizingDirection(float, float, Rectangle2f, float)
	 */
	public static ResizeDirection getResizingDirection(float x, float y, Rectangle2f bounds, float handleSize, ResizeDirection... directionsToTest) {
		return getResizingDirection(x, y, bounds, handleSize, Arrays.asList(directionsToTest));
	}

	/** Reply the direction of the resizing if the mouse is on a resize area.
	 *
	 * @param x the horizontal position of the mouse.
	 * @param y the vertical position of the mouse.
	 * @param bounds are the bounds of the area to consider.
	 * @param handleSize is the size of the handles that permits to resize.
	 * @param directionsToTest are the directions to test.
	 * @return the resize direction; or <code>null</code> if
	 * the specified point is not on a valid resizing handler.
	 * @see #findResizingDirection(float, float, Rectangle2f, float)
	 */
	public static ResizeDirection getResizingDirection(float x, float y, Rectangle2f bounds, float handleSize, Collection<ResizeDirection> directionsToTest) {
		float x1 = bounds.getMinX() - handleSize;
		float y1 = bounds.getMinY() - handleSize;
		float xc = bounds.getMinX() + ( bounds.getWidth() - handleSize ) / 2f;
		float yc = bounds.getMinY() + ( bounds.getHeight() - handleSize ) / 2f;
		float x2 = bounds.getMinX() + bounds.getWidth();
		float y2 = bounds.getMinY() + bounds.getHeight();
		for(ResizeDirection direction : directionsToTest) {
			if ( direction.isResizingRectangle(
					x, y,
					x1, xc, x2,
					y1, yc, y2,
					handleSize, handleSize)) {
				return direction;
			}   
		}
		return null;
	}

	/** Reply the direction of the resizing if the mouse is on a resize area.
	 *
	 * @param pointerArea is the area covered by the mouse.
	 * @param bounds are the bounds of the area to consider.
	 * @param handleSize is the size of the handles that permits to resize.
	 * @return the resize direction; or <code>null</code> if
	 * the specified point is not on a valid resizing handler.
	 * @see #getResizingDirection(float, float, Rectangle2f, float, ResizeDirection...)
	 */
	public static ResizeDirection findResizingDirection(Shape2f pointerArea, Rectangle2f bounds, float handleSize) {
		return getResizingDirection(pointerArea, bounds, handleSize, ResizeDirection.values());
	}


	/** Reply the direction of the resizing if the mouse is on a resize area.
	 *
	 * @param pointerArea is the area covered by the mouse.
	 * @param bounds are the bounds of the area to consider.
	 * @param handleSize is the size of the handles that permits to resize.
	 * @param directionsToTest are the directions to test.
	 * @return the resize direction; or <code>null</code> if
	 * the specified point is not on a valid resizing handler.
	 * @see #findResizingDirection(float, float, Rectangle2f, float)
	 */
	public static ResizeDirection getResizingDirection(Shape2f pointerArea, Rectangle2f bounds, float handleSize, ResizeDirection... directionsToTest) {
		return getResizingDirection(pointerArea, bounds, handleSize, Arrays.asList(directionsToTest));
	}

	/** Reply the direction of the resizing if the mouse is on a resize area.
	 *
	 * @param pointerArea is the area covered by the mouse.
	 * @param bounds are the bounds of the area to consider.
	 * @param handleSize is the size of the handles that permits to resize.
	 * @param directionsToTest are the directions to test.
	 * @return the resize direction; or <code>null</code> if
	 * the specified point is not on a valid resizing handler.
	 * @see #findResizingDirection(float, float, Rectangle2f, float)
	 */
	public static ResizeDirection getResizingDirection(Shape2f pointerArea, Rectangle2f bounds, float handleSize, Collection<ResizeDirection> directionsToTest) {
		float x1 = bounds.getMinX() - handleSize;
		float y1 = bounds.getMinY() - handleSize;
		float xc = bounds.getMinX() + ( bounds.getWidth() - handleSize ) / 2f;
		float yc = bounds.getMinY() + ( bounds.getHeight() - handleSize ) / 2f;
		float x2 = bounds.getMinX() + bounds.getWidth();
		float y2 = bounds.getMinY() + bounds.getHeight();
		for(ResizeDirection direction : directionsToTest) {
			if ( direction.isResizingRectangle(
					pointerArea,
					x1, xc, x2,
					y1, yc, y2,
					handleSize, handleSize)) {
				return direction;
			}   
		}
		return null;
	}

}
