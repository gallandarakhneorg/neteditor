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
package org.arakhne.neteditor.fig.figure.node;

import java.util.Map;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.fig.view.ViewComponentConstants;
import org.arakhne.neteditor.formalism.Anchor;
import org.arakhne.neteditor.formalism.Node;

/** Node figure that is displaying a round rectangle.
 *
 * @param <N> is the type of the model node supported by this figure.
 * @param <A> is the type of the model anchor supported by this figure.
 * @author $Author: galland$
 * @author $Author: baumgartner$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class RoundRectangleNodeFigure<N extends Node<?,? super N,? super A,?>,A extends Anchor<?,? super N,? super A,?>> extends NodeFigure<N,A> {

	private static final long serialVersionUID = -3636770782966999384L;
	
	private float arcSize = 10f;
	
	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 */
	public RoundRectangleNodeFigure(UUID viewUUID) {
		this(viewUUID, 0, 0, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE );
	}

	/** Construct a new figure.
	 *
	 * @param viewUUID is the UUID of the view that is enclosing this figure.
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 */
	public RoundRectangleNodeFigure(UUID viewUUID, float x, float y) {
		this(viewUUID, x, y, DEFAULT_MINIMAL_SIZE, DEFAULT_MINIMAL_SIZE );
	}

	/** Construct a new figure.
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
	 * @param x horizontal postition of the upper-left corner of this FigNode.
	 * @param y vertical postition of the upper-left corner of this FigNode.
	 * @param width is the width of the figure.
	 * @param height is the height of the figure.
	 */
	public RoundRectangleNodeFigure(UUID viewUUID, float x, float y, float width, float height) {
		super(viewUUID, x, y, width, height );
	}
	
	@Override
	public void fitToContent() {
		Dimension prefs = getMinimalDimension();
		if (prefs.width()!=getMinimalWidth() || prefs.height()!=getMinimalHeight()) {
			setBounds(
					getX(),
					getY(),
					prefs.width(),
					prefs.height());
			avoidCollision();
		}
	}

	@Override
	public boolean contains(float x, float y) {
		return RoundRectangle2f.containsRoundRectanglePoint(
				getX(), getY(), getWidth(), getHeight(), getArcSize(), getArcSize(),
				x, y);
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
			firePropertyChange(PROPERTY_ARCSIZE, old, this.arcSize); 
			repaint(false);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Object> getProperties() {
		Map<String,Object> properties = super.getProperties();
		properties.put(PROPERTY_ARCSIZE, this.arcSize); 
		return properties;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProperties(Map<String, Object> properties) {
		super.setProperties(properties);
		if (properties!=null) {
			setArcSize(propGetFloat(PROPERTY_ARCSIZE, this.arcSize, properties)); 
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintNode(ViewGraphics2D g) {
		g.setInteriorPainted(true);
		g.setOutlineDrawn(true);
		Rectangle2f bounds = g.getCurrentViewComponentBounds();
		Shape2f r = new RoundRectangle2f(
				bounds.getMinX(),
				bounds.getMinY(),
				bounds.getWidth(),
				bounds.getHeight(),
				getArcSize(), getArcSize());
		g.draw(r);
	}
	
}
