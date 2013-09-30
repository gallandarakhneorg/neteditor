/* 
 * $Id$
 * 
 * Copyright (C) 2012-13 Stephane GALLAND
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

package org.arakhne.neteditor.figlayout.force;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.TreeMap;
import java.util.UUID;

import org.arakhne.afc.math.continous.object2d.Point2f;
import org.arakhne.afc.math.continous.object2d.Vector2f;
import org.arakhne.afc.math.generic.Point2D;
import org.arakhne.afc.math.generic.Vector2D;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.afc.ui.vector.Margins;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.coercion.CoercedFigure;
import org.arakhne.neteditor.fig.figure.decoration.DecorationFigure;
import org.arakhne.neteditor.fig.figure.edge.EdgeFigure;
import org.arakhne.neteditor.fig.figure.node.NodeFigure;
import org.arakhne.neteditor.figlayout.AbstractFigureLayout;
import org.arakhne.neteditor.figlayout.FigureLayoutUndoableEdit;
import org.arakhne.neteditor.formalism.Edge;
import org.arakhne.neteditor.formalism.Node;

/** This class provides the implementation of a force-based
 * laying-out algorithm.
 * <p>
 * The force toward and away from nodes is calculated according to 
 * Hooke's Law and Coulomb's Law.
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @see "http://en.wikipedia.org/wiki/Force-based_algorithms_(graph_drawing)"
 * @since 16.0
 */
public class ForceBasedFigureLayout extends AbstractFigureLayout implements ForceBasedConstants {

	/** Mass calaculator.
	 */
	private FigureMassCalculator massCalculator = null;
	
	/** Size of the coordinate space when randomizing coordinates.
	 */
	private float randomSpaceSize = 1000f;
	
	/** The kinematic energy when stable.
	 */
	private float maxKinematicEnergy = DEFAULT_STABILITY_ENERGY_THRESHOLD;
	
	/** The damping constant.
	 * The damping constant is in (0;1) exclusive.
	 */
	private float damping = DEFAULT_DAMPING;

	/** The step of time to consider.
	 */
	private float timestep = DEFAULT_TIME_STEP;
	
	/** The Coulomb's constant to use.
	 */
	private float coulombConstant = DEFAULT_COULOMB_CONSTANT;

	/** The Coulomb's constant to use.
	 */
	private float springConstant = DEFAULT_SPRING_CONSTANT;

	/** Preferred space between two nodes (insets are outside this
	 * preferred space).
	 */
	private float preferredInterNodeSpace = DEFAULT_MINIMAL_SIZE; 

	/**
	 */
	public ForceBasedFigureLayout() {
		//
	}
	
	/** Set the preferred space between two nodes (insets are
	 * outside this preferred space).
	 * 
	 * @param space
	 */
	public void setPreferredInterNodeSpace(float space) {
		if (space>0f)
			this.preferredInterNodeSpace = space;
	}
	
	/** Replies the preferred space between two nodes (insets are
	 * outside this preferred space).
	 * 
	 * @return the preferred space.
	 */
	public float getPreferredInterNodeSpace() {
		return this.preferredInterNodeSpace;
	}

	/** Set the calculator of the node's masses.
	 * 
	 * @param calculator
	 */
	public void setNodeMassCalculator(FigureMassCalculator calculator) {
		this.massCalculator = calculator;
	}
	
	/** Replies the calculator of the node's masses.
	 * 
	 * @return the calculator of the masses.
	 */
	public FigureMassCalculator getNodeMassCalculator() {
		return this.massCalculator;
	}

	/** Replies the size of the coordinate space that is
	 * used to compute a coordinate randomly.
	 * 
	 * @return the size of the random coordinate space.
	 */
	public float getRandomCoordinateSpaceSize() {
		return this.randomSpaceSize;
	}
	
	/** Set the size of the coordinate space that is
	 * used to compute a coordinate randomly.
	 * 
	 * @param size is the size of the random coordinate space; stricly positive.
	 */
	public void setRandomCoordinateSpaceSize(float size) {
		if (size>=0f) {
			this.randomSpaceSize = size;
		}
	}

	/** Replies the maximal kinematic energy up to the algorithm
	 * is able to reach.
	 * 
	 * @return the maximal kinematic energy, stricly positive.
	 */
	public float getMaximalKinematicEnergy() {
		return this.maxKinematicEnergy;
	}

	/** Set the maximal kinematic energy up to the algorithm
	 * is able to reach.
	 * 
	 * @param energy is the maximal kinematic energy, stricly positive.
	 */
	public void setMaximalKinematicEnergy(float energy) {
		if (energy>0f) {
			this.maxKinematicEnergy = energy;
		}
	}
	
	/** Replies the Coulomb's constant to use.
	 * Default is {@link #DEFAULT_COULOMB_CONSTANT}.
	 * 
	 * @return the Coulomb's constant, stricly positive.
	 */
	public float getCoulombConstant() {
		return this.coulombConstant;
	}

	/** Set the Coulomb's constant to use.
	 * 
	 * @param k the Coulomb's constant, stricly positive.
	 */
	public void setCoulombConstant(float k) {
		if (k>0f) this.coulombConstant = k;
	}

	/** Replies the spring constant to use.
	 * Default is {@link #DEFAULT_SPRING_CONSTANT}.
	 * 
	 * @return the spring constant, stricly positive.
	 */
	public float getSpringConstant() {
		return this.springConstant;
	}

	/** Set the spring constant to use.
	 * 
	 * @param k the spring constant, stricly positive.
	 */
	public void setSpringConstant(float k) {
		if (k>0f) this.springConstant = k;
	}

	/** Replies the damping constant.
	 * The damping constant is in (0;1) exclusive.
	 * 
	 * @return the damping constant.
	 */
	public float getDamping() {
		return this.damping;
	}

	/** Set the damping constant.
	 * The damping constant is in (0;1) exclusive.
	 * 
	 * @param damping is the damping constant.
	 */
	public void setDamping(float damping) {
		if (damping<=0f) this.damping = 0f+Float.MIN_NORMAL;
		else if (damping>=1f) this.damping = 1f-Float.MIN_NORMAL;
		else this.damping = damping;
	}
	
	/** Replies the time step used by the algorithm.
	 * The time step is the amount of time during which 
	 * a node is atomically moved.
	 * 
	 * @return the time step, stricly positive.
	 */
	public float getTimeStep() {
		return this.timestep;
	}

	/** Set the time step used by the algorithm.
	 * The time step is the amount of time during which 
	 * a node is atomically moved.
	 * 
	 * @param step is the time step, stricly positive.
	 */
	public void setTimeStep(float step) {
		if (step>0f) {
			this.timestep = step;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Undoable layoutFigures(Collection<? extends Figure> figures) {
		FigureLayoutUndoableEdit undo = new FigureLayoutUndoableEdit(
				Locale.getString(ForceBasedFigureLayout.class, "UNDO_NAME")); //$NON-NLS-1$

		Random random = new Random();

		float kOfCoulomb = getCoulombConstant();
		float kOfSpring = getSpringConstant();
		float timestep = getTimeStep();
		float damping = getDamping();
		float threshold = getMaximalKinematicEnergy();
		float interNodeSpace = getPreferredInterNodeSpace();
		Margins insets = getMargins();
		Point2D origin = getOrigin();
		FigureMassCalculator calculator = getNodeMassCalculator();

		float barycenterX, barycenterY;
		barycenterX = barycenterY = 0f;
		
		// set up initial node velocities to (0,0)
		// set up initial node positions randomly: make sure no 2 nodes are in exactly the same position
		Map<Node<?,?,?,?>,LayoutNode> nodes = new TreeMap<Node<?,?,?,?>,LayoutNode>();
		List<LayoutNode> allNodes = new ArrayList<LayoutNode>();
		Map<Edge<?,?,?,?>,EdgeFigure<?>> allEdges = new TreeMap<Edge<?,?,?,?>,EdgeFigure<?>>();
		for(Figure figure : figures) {
			if (figure instanceof NodeFigure<?,?>) {
				NodeFigure<?,?> nodeFigure = (NodeFigure<?,?>)figure;
				LayoutNode n = new LayoutNode(
						origin.getX() + (random.nextFloat()-random.nextFloat()) * getRandomCoordinateSpaceSize(),
						origin.getY() + (random.nextFloat()-random.nextFloat()) * getRandomCoordinateSpaceSize(),
						figure,
						(calculator==null) ? DEFAULT_MASS : calculator.computeMassFor(figure));
				nodes.put(nodeFigure.getModelObject(), n);
				allNodes.add(n);
				barycenterX += n.getCenterX();
				barycenterY += n.getCenterY();
				for(Edge<?,?,?,?> edge : nodeFigure.getModelObject().getEdges()) {
					EdgeFigure<?> edgeFigure = edge.getViewBinding().getView(figure.getViewUUID(), EdgeFigure.class);
					if (edgeFigure!=null) {
						allEdges.put(edge, edgeFigure);
					}
				}
			}
			else if (figure instanceof DecorationFigure && !(figure instanceof CoercedFigure)) {
				LayoutNode n = new LayoutNode(
						origin.getX() + (random.nextFloat()-random.nextFloat()) * getRandomCoordinateSpaceSize(),
						origin.getY() + (random.nextFloat()-random.nextFloat()) * getRandomCoordinateSpaceSize(),
						figure,
						(calculator==null) ? DEFAULT_MASS : calculator.computeMassFor(figure));
				allNodes.add(n);
				barycenterX += n.getCenterX();
				barycenterY += n.getCenterY();
			}
		}
		
		if (allNodes.isEmpty()) return null;
				
		Point2D centerMassPoint = new Point2f();
		Vector2D netForce = new Vector2f();
		float totalKineticEnergy;
		Node<?,?,?,?> otherSideNode;
		Vector2D d;
		EdgeFigure<?> springFigure;
		LayoutNode otherSide;
				
		do {
			// Update the center mass point
			barycenterX /= allNodes.size();
			barycenterY /= allNodes.size();
			centerMassPoint.set(barycenterX, barycenterY);
			barycenterX = barycenterY = 0f;
			
			//	total_kinetic_energy := 0
			totalKineticEnergy = 0f;
			
			// running sum of total kinetic energy over all particles
			for(LayoutNode node : allNodes) {
								
				// net-force := (0, 0)
				netForce.set(0f, 0f);
				
				// running sum of total force on this particular node
				
				for(LayoutNode otherNode : allNodes) {
					if (node!=otherNode) {
						d = computeCoulombRepulsion(node, otherNode, kOfCoulomb);
						netForce.add(d);
					}
				}
				
				for(Entry<Edge<?,?,?,?>,EdgeFigure<?>> pair : allEdges.entrySet()) {
					if (pair.getKey().getStartAnchor().getNode()!=node.getNode()
						&& pair.getKey().getEndAnchor().getNode()!=node.getNode()) {
						d = computeCoulombRepulsion(node, pair.getKey(), pair.getValue(), kOfCoulomb);
						netForce.add(d);
					}
				}

				if (node.isNodeFigure()) {
					for(Edge<?,?,?,?> spring : node.getNode().getEdges()) {
						
						// Remove the intermediate control points.
						springFigure = spring.getViewBinding().getView(node.getView(), EdgeFigure.class);
						if (springFigure!=null) {
							while (springFigure.getCtrlPointCount()>2) {
								undo.addControlPointRemoval(springFigure, 1);
								springFigure.removeCtrlPointAt(1);
							}
						}
						
						otherSideNode = spring.getOtherSideFrom(node.getNode());
						otherSide = nodes.get(otherSideNode);
						
						d = computeHookeAttraction(node, otherSide, 
								spring, kOfSpring,
								insets, interNodeSpace);
						netForce.add(d);
					}
				}
				else {
					// Decoration figures are attracted by the center mass point (usually the barycenter of the previous loop)
					d = computeHookeAttraction(node, centerMassPoint, kOfSpring, insets);
					netForce.add(d);
				}
				
				// without damping, it moves forever
				
				// this_node.velocity := (this_node.velocity + timestep * net-force) * damping
				node.velocity.add(netForce.getX() * timestep, netForce.getY() * timestep);
				node.velocity.scale(damping);
				
				// this_node.position := this_node.position + timestep * this_node.velocity
				node.setX(node.getX() + timestep * node.velocity.getX());
				node.setY(node.getY() + timestep * node.velocity.getY());
				
				// total_kinetic_energy := total_kinetic_energy + this_node.mass * (this_node.velocity)^2
				totalKineticEnergy += node.mass * node.velocity.lengthSquared();

				// Update the barycenter
				barycenterX += node.getCenterX();
				barycenterY += node.getCenterY();
			}
		}
		while (totalKineticEnergy>threshold);
		
		if (!Double.isNaN(totalKineticEnergy) && !Double.isInfinite(totalKineticEnergy)) {
			// Move the nodes
			for(LayoutNode node : nodes.values()) {
				undo.addLocationChange(node.getFigure(), node.getX(), node.getY());
				node.getFigure().setLocation(node.getX(), node.getY());
			}
		}
		
		if (undo.isEmpty()) return null;
		return undo;
	}
	
	/** Coulomb's Equation is: {@code F = (k * Q1 * Q2) / d^2};
	 * where {@k} is the {@link #COULOMB_CONSTANT Coulomb's constant};
	 * {@code Q1} and {@code Q2} are the charges of the nodes; and 
	 * {@code d} is the distance between the node's centers.
	 * <p>
	 * Here we assume that {@code Q1 = Q2 = -1}, ie. the two nodes
	 * are exceeding of 1 electron. So that the two nodes are repulsives.
	 * 
	 * @param from
	 * @param to
	 * @param coulombConstant
	 * @return
	 * @see "http://en.wikipedia.org/wiki/Coulomb%27s_law"
	 */
	private static Vector2D computeCoulombRepulsion(LayoutNode from, LayoutNode to, float coulombConstant) {
		float rx = from.getCenterX() - to.getCenterX();
		float ry = from.getCenterY() - to.getCenterY();
		float squaredDistance = rx * rx + ry * ry;
		float length = (float)Math.sqrt(squaredDistance);
		float F = Math.max(-0f, coulombConstant / squaredDistance);
		return new Vector2f(
				rx * F / length,
				ry * F / length);
	}
	
	/** Coulomb's Equation is: {@code F = (k * Q1 * Q2) / d^2};
	 * where {@k} is the {@link #COULOMB_CONSTANT Coulomb's constant};
	 * {@code Q1} and {@code Q2} are the charges of the nodes; and 
	 * {@code d} is the distance between the node's centers.
	 * <p>
	 * Here we assume that {@code Q1 = Q2 = -1}, ie. the two nodes
	 * are exceeding of 1 electron. So that the two nodes are repulsives.
	 * 
	 * @param from
	 * @param edge
	 * @param edgeFigure
	 * @param coulombConstant
	 * @return
	 * @see "http://en.wikipedia.org/wiki/Coulomb%27s_law"
	 */
	private static Vector2D computeCoulombRepulsion(LayoutNode from, Edge<?,?,?,?> edge, EdgeFigure<?> edgeFigure, float coulombConstant) {
		float rx = 0f;
		float ry = 0f;

		if (from.getFigure().intersects(edgeFigure.getBounds())) {
			Point2D pts = edgeFigure.getNearestPointTo(from.getCenterX(), from.getCenterY());
			rx = (from.getCenterX() - pts.getX());
			ry = (from.getCenterY() - pts.getY());
			float squaredDistance = rx * rx + ry * ry;
			float length = (float)Math.sqrt(squaredDistance);
			float boxSize = Math.max(from.getFigure().getWidth(), from.getFigure().getHeight());
			boxSize = boxSize * boxSize;
			boxSize = (float)Math.sqrt(boxSize + boxSize);
			if (length <= boxSize) {
				float F = Math.max(0f, coulombConstant / squaredDistance);
				assert(F>=0f);
				rx = rx * F / length;
				ry = ry * F / length;
			}
		}

		return new Vector2f(rx, ry);
	}

	/** Hooke's Equation is: {@code F = -k . x};
	 * where {@code x} is the displacement of the spring's end from its 
	 * equilibrium position; {@code F} is the restoring force exerted
	 * by the spring on that end; and {@code k} is a constant called
	 * the rate or spring constant. 
	 * 
	 * @param from
	 * @param to
	 * @param spring
	 * @param springConstant
	 * @param insets
	 * @return
	 * @see "http://en.wikipedia.org/wiki/Hooke%27s_Law"
	 */
	private static Vector2D computeHookeAttraction(
			LayoutNode from, 
			LayoutNode to,
			Edge<?,?,?,?> spring,
			float springConstant,
			Margins insets,
			float interNodeSpace) {
		float displacementX, displacementY;
		
		if ((from.getMaxX()+insets.right()) < (to.getX()-insets.left()-interNodeSpace)) {
			displacementX =
					(from.getMaxX() + insets.right())
					-
					(to.getX() - insets.left() - interNodeSpace);
		}
		else if ((from.getX()-insets.left()) > (to.getMaxX()+insets.right()+interNodeSpace)) {
			displacementX =
					(from.getX() - insets.left())
					-
					(to.getMaxX() + insets.right() + interNodeSpace);
		}
		else {
			displacementX = 0f;
		}
		
		if ((from.getMaxY()+insets.bottom()) < (to.getY()-insets.top()-interNodeSpace)) {
			displacementY =
					(from.getMaxY() + insets.bottom())
					-
					(to.getY() - insets.top() - interNodeSpace);
		}
		else if ((from.getY()-insets.top()) > (to.getMaxY()+insets.bottom()+interNodeSpace)) {
			displacementY =
					(from.getY() - insets.top())
					-
					(to.getMaxY() + insets.bottom() + interNodeSpace);
		}
		else {
			displacementY = 0f;
		}
		
		float Fx = - springConstant * displacementX;
		float Fy = - springConstant * displacementY;
		
		return new Vector2f(Fx, Fy);
	}

	/** Hooke's Equation is: {@code F = -k . x};
	 * where {@code x} is the displacement of the spring's end from its 
	 * equilibrium position; {@code F} is the restoring force exerted
	 * by the spring on that end; and {@code k} is a constant called
	 * the rate or spring constant. 
	 * 
	 * @param from
	 * @param attractivePoint
	 * @param springConstant
	 * @param insets
	 * @return
	 * @see "http://en.wikipedia.org/wiki/Hooke%27s_Law"
	 */
	private static Vector2D computeHookeAttraction(
			LayoutNode from, 
			Point2D attractivePoint,
			float springConstant, Margins insets) {
		float displacementX, displacementY;
		
		if ((attractivePoint.getX()<(from.getX()-insets.left()))
			||(attractivePoint.getX()>(from.getMaxX()+insets.right()))) {
			displacementX = (from.getCenterX() - attractivePoint.getX());
		}
		else {
			displacementX = 0f;
		}
		
		if ((attractivePoint.getY()<(from.getY()-insets.top()))
				||(attractivePoint.getY()>(from.getMaxY()+insets.bottom()))) {
			displacementY = (from.getCenterY() - attractivePoint.getY());
		}
		else {
			displacementY = 0f;
		}

		float Fx = - springConstant * displacementX;
		float Fy = - springConstant * displacementY;
		
		return new Vector2f(Fx, Fy);
	}

	/** Node in the Force-based layout algorithm. 
	 * 
	 * @author $Author: galland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @see "http://en.wikipedia.org/wiki/Force-based_algorithms_(graph_drawing)"
	 * @since 16.0
	 */
	private static class LayoutNode {

		/** Position of the node.
		 */
		private float x;

		/** Position of the node.
		 */
		private float y;
		
		/** Center of the node.
		 */
		private float centerX;

		/** Center of the node.
		 */
		private float centerY;

		/** Max position of the node.
		 */
		private float maxX;

		/** Max position of the node.
		 */
		private float maxY;

		/** Velocity of the node.
		 */
		public final Vector2D velocity = new Vector2f();
		
		/** Mass of the node.
		 */
		public final float mass;

		/** Graph node represented by this LayoutNode.
		 */
		private final Figure figure;
		
		/**
		 * @param x
		 * @param y
		 * @param figure
		 * @param mass
		 */
		public LayoutNode(float x, float y, Figure figure, float mass) {
			this.mass = mass;
			this.figure = figure;
			setX(x);
			setY(y);
		}

		/** Replies the identifier of the view in whic hthe figure is.
		 * 
		 * @return the view id of the figure.
		 */
		public UUID getView() {
			return this.figure.getViewUUID();
		}
		
		/** Replies the node figure.
		 * 
		 * @return the node figure.
		 */
		public Figure getFigure() {
			return this.figure;
		}
		
		/** Replies if this LayoutNode contains a NodeFigure.
		 * 
		 * @return <code>true</code> if the figure inside is for a node;
		 * otherwise <code>false</code>.
		 */
		public boolean isNodeFigure() {
			return this.figure instanceof NodeFigure<?,?>;
		}
		
		/** Replies the node embedded in this LayoutNode.
		 * 
		 * @return the node or <code>null</code> if this LayoutNode has
		 * no node inside.
		 */
		public Node<?,?,?,?> getNode() {
			if (this.figure instanceof NodeFigure<?,?>) {
				return ((NodeFigure<?,?>)this.figure).getModelObject();
			}
			return null;
		}

		/** Replies x.
		 * 
		 * @return x
		 */
		public float getX() {
			return this.x;
		}

		/** Set x.
		 * 
		 * @param x
		 */
		public void setX(float x) {
			this.x = x;
			float w = this.figure.getWidth();
			this.centerX = this.x + w/2f;
			this.maxX = this.x + w;
		}

		/** Replies y.
		 * 
		 * @return y
		 */
		public float getY() {
			return this.y;
		}

		/** Set y.
		 * 
		 * @param y
		 */
		public void setY(float y) {
			this.y = y;
			float h = this.figure.getHeight();
			this.centerY = this.y + h/2f;
			this.maxY = this.y + h;
		}

		/** Replies the center x.
		 * 
		 * @return center x
		 */
		public float getCenterX() {
			return this.centerX;
		}

		/** Replies the center y.
		 * 
		 * @return center y
		 */
		public float getCenterY() {
			return this.centerY;
		}

		/** Replies the max x.
		 * 
		 * @return max x
		 */
		public float getMaxX() {
			return this.maxX;
		}

		/** Replies the max y.
		 * 
		 * @return max y
		 */
		public float getMaxY() {
			return this.maxY;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append("((LayoutNode:"); //$NON-NLS-1$
			b.append(this.figure);
			b.append("=("); //$NON-NLS-1$
			b.append(this.x);
			b.append(";"); //$NON-NLS-1$
			b.append(this.y);
			b.append(")))"); //$NON-NLS-1$
			return b.toString();
		}

	}

}
