/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.fsm.figures ;

import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.afc.math.continous.object2d.RoundRectangle2f;
import org.arakhne.afc.math.continous.object2d.Segment2f;
import org.arakhne.afc.math.continous.object2d.Shape2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.ui.TextAlignment;
import org.arakhne.afc.ui.vector.Dimension;
import org.arakhne.afc.ui.vector.Font;
import org.arakhne.afc.ui.vector.FontMetrics;
import org.arakhne.afc.ui.vector.FontStyle;
import org.arakhne.afc.ui.vector.VectorToolkit;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.node.RoundRectangleNodeFigure;
import org.arakhne.neteditor.fig.graphics.ViewGraphics2D;
import org.arakhne.neteditor.formalism.ModelObjectEvent;
import org.arakhne.neteditor.fsm.constructs.FSMAnchor;
import org.arakhne.neteditor.fsm.constructs.FSMState;

/** Figure for the nodes of the FSM.
 *  
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FSMStateFigure extends RoundRectangleNodeFigure<FSMState,FSMAnchor> {

	private static final long serialVersionUID = -652293907037781116L;

	private Rectangle2f nameBox = null;
	private Rectangle2f enterActionBox = null;
	private Rectangle2f insideActionBox = null;
	private Rectangle2f exitActionBox = null;

	/**
	 * @param viewId
	 * @param x
	 * @param y
	 */
	public FSMStateFigure(UUID viewId, float x, float y) {
		super(viewId, x, y);
	}

	/**
	 * @param viewId
	 */
	public FSMStateFigure(UUID viewId) {
		this(viewId, 0, 0);
	}

	/** Replies if the given point is inside the box of the enter action.
	 * 
	 * @param position
	 * @return <code>true</code> if there is an enter action and
	 * the given position is inside the associated box.
	 */
	public boolean isInEnterActionBox(Point2D position) {
		if (position!=null && this.enterActionBox!=null) {
			return this.enterActionBox.contains(position);
		}
		return false;
	}

	/** Replies if the given point is inside the box of the inside action.
	 * 
	 * @param position
	 * @return <code>true</code> if there is an inside action and
	 * the given position is inside the associated box.
	 */
	public boolean isInInsideActionBox(Point2D position) {
		if (position!=null && this.insideActionBox!=null) {
			return this.insideActionBox.contains(position);
		}
		return false;
	}

	/** Replies if the given point is inside the box of the exit action.
	 * 
	 * @param position
	 * @return <code>true</code> if there is an exit action and
	 * the given position is inside the associated box.
	 */
	public boolean isInExitActionBox(Point2D position) {
		if (position!=null && this.exitActionBox!=null) {
			return this.exitActionBox.contains(position);
		}
		return false;
	}

	/** Replies if the given point is inside the box of the name.
	 * 
	 * @param position
	 * @return <code>true</code> if there is a name and
	 * the given position is inside the associated box.
	 */
	public boolean isInNameBox(Point2D position) {
		if (position!=null && this.nameBox!=null) {
			return this.nameBox.contains(position);
		}
		return false;
	}

	private Dimension getPreferredSize() {
		FSMState mo = getModelObject();
		if (mo!=null) {
			String[] texts = new String[] {
					mo.getName(),
					mo.getEnterAction(),
					mo.getExitAction(),
					mo.getAction()
			};

			Font defaultFont = VectorToolkit.font();
			float fontSize = defaultFont.getSize();
			float maxWidth = 0;
			float height = fontSize;
			for(String str : texts) {
				if (str!=null && !str.isEmpty()) {
					float w = fontSize * str.length();
					if (maxWidth<w) maxWidth = w;
					height += fontSize + 2;
				}
			}
			if (height<getMinimalHeight()) height = getMinimalHeight();
			if (maxWidth<getMinimalWidth()) maxWidth = getMinimalWidth();
			if (maxWidth>getMaximalWidth()) maxWidth = getMaximalWidth();
			if (height>getMaximalHeight()) height = getMaximalHeight();
			return VectorToolkit.dimension(maxWidth, height);
		}
		return getMinimalDimension();
	}

	private void expandsBoundsIfNecessary() {
		Dimension pref = getPreferredSize();
		Rectangle2f bounds = getBounds();
		if (pref.width()>bounds.getWidth() || pref.height()>bounds.getHeight()) {
			setBounds(
					bounds.getMinX(),
					bounds.getMinY(),
					Math.max(pref.width(), bounds.getWidth()),
					Math.max(pref.height(), bounds.getHeight()));
			avoidCollision();
		}
	}
	
	@Override
	public void fitToContent() {
		Dimension pref = getPreferredSize();
		Rectangle2f bounds = getBounds();
		if (pref.width()!=bounds.getWidth() || pref.height()!=bounds.getHeight()) {
			setBounds(
					bounds.getMinX(),
					bounds.getMinY(),
					pref.width(),
					pref.height());
			avoidCollision();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateFromModel(ModelObjectEvent event) {
		super.updateFromModel(event);
		expandsBoundsIfNecessary();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void paintNode(ViewGraphics2D g) {
		boolean isAccepting = getModelObject().isAccepting();
		Font oldFont = g.getFont();
		Font boldFont = oldFont.deriveFont(FontStyle.BOLD);

		// Compute the inner frame.
		Rectangle2f bounds = g.getCurrentViewComponentBounds().clone();
		if (isAccepting) {
			bounds.inflate(-4, -4, -4, -4);
		}

		g.beginGroup();

		// Draw the background of the state
		super.paintNode(g);

		g.setInteriorPainted(false);
		g.setOutlineDrawn(true);

		// Draw the inner frame for accepting states.
		if (isAccepting) {
			Shape2f r = new RoundRectangle2f(
					bounds.getMinX(),
					bounds.getMinY(),
					bounds.getWidth(),
					bounds.getHeight(),
					getArcSize()/2, getArcSize()/2);
			g.draw(r);
		}
		
		if (!g.isShadowDrawing()) {
			Rectangle2f nameBounds = new Rectangle2f(
					bounds.getMinX(),
					bounds.getMinY(),
					bounds.getWidth(),
					boldFont.getSize() + 10f);
			g.draw(new Segment2f(
					nameBounds.getMinX(), nameBounds.getMaxY(),
					nameBounds.getMaxX(), nameBounds.getMaxY()));
	
			String str = getName();
			if (str!=null && !str.isEmpty()) {
				g.setFont(boldFont);
				Point2D pos = g.computeTextPosition(str, nameBounds,
						TextAlignment.CENTER_ALIGN, TextAlignment.CENTER_ALIGN);
				g.drawString(str, pos.getX(), pos.getY(), nameBounds);
				g.setFont(oldFont);
				this.nameBox = nameBounds;
			}
			else {
				this.nameBox = null;
			}
	
			FontMetrics fm = g.getFontMetrics(oldFont);
			Rectangle2f contentBounds = new Rectangle2f(
					bounds.getMinX()+1,
					nameBounds.getMaxY()+1,
					bounds.getWidth()-2,
					(bounds.getHeight() - nameBounds.getHeight() - 2));
			float y = Float.NaN;
			switch(g.getStringAnchor()) {
			case UPPER_LEFT:
				y = contentBounds.getMinY();
				break;
			case LOWER_LEFT:
				y = contentBounds.getMinY() + oldFont.getSize();
				break;
			case LEFT_BASELINE:
				y = contentBounds.getMinY() + fm.getMaxAscent();
				break;
			default:
				throw new IllegalStateException();
			}
	
			float boxY = contentBounds.getMinY();
			str = getModelObject().getEnterAction();
			if (str!=null && !str.isEmpty()) {
				str = Locale.getString("ENTER_ACTION", str); //$NON-NLS-1$
				g.drawString(str,
						contentBounds.getMinX()+1,
						y,
						contentBounds);
				this.enterActionBox = new Rectangle2f(
						contentBounds.getMinX(),
						boxY,
						contentBounds.getWidth(),
						fm.getHeight());
				y += oldFont.getSize() + 2;
				boxY += oldFont.getSize() + 2;
			}
			else {
				this.enterActionBox = null;
			}
			str = getModelObject().getAction();
			if (str!=null && !str.isEmpty()) {
				str = Locale.getString("IN_ACTION", str); //$NON-NLS-1$
				g.drawString(str,
						contentBounds.getMinX()+1,
						y,
						contentBounds);
				this.insideActionBox = new Rectangle2f(
						contentBounds.getMinX(),
						boxY,
						contentBounds.getWidth(),
						fm.getHeight());
				y += oldFont.getSize() + 2;
				boxY += oldFont.getSize() + 2;
			}
			else {
				this.insideActionBox = null;
			}
			str = getModelObject().getExitAction();
			if (str!=null && !str.isEmpty()) {
				str = Locale.getString("EXIT_ACTION", str); //$NON-NLS-1$
				g.drawString(str,
						contentBounds.getMinX()+1,
						y,
						contentBounds);
				this.exitActionBox = new Rectangle2f(
						contentBounds.getMinX(),
						boxY,
						contentBounds.getWidth(),
						fm.getHeight());
			}
			else {
				this.exitActionBox = null;
			}
		}
		
		g.endGroup();
	}

}
