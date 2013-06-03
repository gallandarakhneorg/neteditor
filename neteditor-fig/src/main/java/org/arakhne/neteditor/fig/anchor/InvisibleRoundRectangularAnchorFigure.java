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
package org.arakhne.neteditor.fig.anchor;

import java.util.Map;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.AnchorLocation;

/** This figure is for an anchor that is not drawn in its
 * inactive state, and the activated side of the anchor
 * is drawn along a round rectangle frame.
 *
 * @param <A> is the type of the model anchor associated to this figure.
 * @author $Author: galland$
 * @author $Author: hannoun$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class InvisibleRoundRectangularAnchorFigure<A extends Anchor<?,?,?,?>>
extends AbstractRectangularAnchorFigure<A> {

	private static final long serialVersionUID = -3451655934118279974L;

	private float arcSize = 10f;

	/** Construct a new Anchor.
	 * <p>
	 * The specified width and height are set inconditionally.
	 * The minimal width becomes the min between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The minimal height becomes the min between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MINIMAL_SIZE}.
	 * The maximal width becomes the max between the specified width and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 * The maximal height becomes the max between the specified height and
	 * the {@link ViewComponentConstants#DEFAULT_MAXIMAL_SIZE}.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 * @param width horizontal dimension of this Anchor within his {@link NodeFigure}.
	 * @param height vertical dimension of this Anchor within his {@link NodeFigure}.
	 */
	public InvisibleRoundRectangularAnchorFigure(UUID viewUUID, float x, float y, float width, float height) { 
		super(viewUUID, x, y, width, height) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal position of this Anchor within his {@link NodeFigure}.
	 * @param y vertical position of this Anchor within his {@link NodeFigure}.
	 */
	public InvisibleRoundRectangularAnchorFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE) ;
	}

	/** Construct a new Anchor. With this constructor, the
	 *  size of the port is nul.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public InvisibleRoundRectangularAnchorFigure(UUID viewUUID) {
		this(viewUUID, 0, 0, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE) ;
	}
	
	@Override
	public boolean contains(float x, float y) {
		return RoundRectangle2f.containsRoundRectanglePoint(
				getX(), getY(), getWidth(), getHeight(), getArcSize(), getArcSize(),
				x, y);
	}
	
	@Override
	public boolean contains(Rectangle2f r) {
		return RoundRectangle2f.containsRoundRectangleRectangle(
				getX(), getY(), getWidth(), getHeight(), getArcSize(), getArcSize(),
				r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final void paint(ViewGraphics2D g) {
		//
	}

	/** Replies the size of the arc.
	 * 
	 * @return the width and height of the arcs.
	 */
	public float getArcSize() {
		return this.arcSize;
	}

	/** Set the size of the arc.
	 * 
	 * @param size is the width and height of the arcs.
	 */
	public void setArcSize(float size) {
		if (this.arcSize!=size) {
			float old = this.arcSize;
			this.arcSize = size;
			firePropertyChange("arcSize", old, this.arcSize); //$NON-NLS-1$
			repaint(false);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put("arcSize", this.arcSize); //$NON-NLS-1$
		return properties;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setArcSize(propGetFloat("arcSize", this.arcSize, properties)); //$NON-NLS-1$
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Point2D getConnectionPointFrom(Point2D position,
			Point2D absoluteAnchorPosition, AnchorLocation location) {
		return super.getConnectionPointFrom(position, absoluteAnchorPosition, location);
	}

}
