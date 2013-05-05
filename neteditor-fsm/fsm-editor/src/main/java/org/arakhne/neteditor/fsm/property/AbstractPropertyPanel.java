/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.fsm.property ;

import java.awt.Dimension;
import java.lang.ref.WeakReference;

import javax.swing.JPanel;

import org.arakhne.afc.ui.undo.UndoManager;
import org.arakhne.afc.ui.undo.Undoable;
import org.arakhne.neteditor.fig.figure.Figure;

/** Abstract implementation of a property panel.
 *
 * @param <F>
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractPropertyPanel<F extends Figure> extends JPanel {

	private static final long serialVersionUID = 2689935911495511326L;
	
	/** Enforced version of the equality test on two strings.
	 * This enforced version supported <code>null</code> values
	 * given as parameters.
	 * 
	 * @param a
	 * @param b
	 * @param isNullEmptyEquivalence indicates if the <code>null</code> value
	 * is assimilated to the empty string.
	 * @return <code>true</code> if a is equal to b; otherwise <code>false</code>.
	 */
	protected static boolean equals(String a, String b, boolean isNullEmptyEquivalence) {
		String aa = (a!=null || !isNullEmptyEquivalence) ? a : "";  //$NON-NLS-1$
		String bb = (b!=null || !isNullEmptyEquivalence) ? b : "";  //$NON-NLS-1$
		if (aa==null) return bb==null;
		if (bb==null) return false;
		return aa.equals(bb);
	}


	private final Class<F> type;
	private final WeakReference<UndoManager> undoManager;
	
	/**
	 * @param type
	 * @param undoManager
	 */
	public AbstractPropertyPanel(Class<F> type, UndoManager undoManager) {
		this.type = type;
		this.undoManager = undoManager==null ? null : new WeakReference<UndoManager>(undoManager);
		Dimension d = new Dimension(400,100);
		setMinimumSize(d);
		setPreferredSize(d);
	}

	/** Release any resource.
	 */
	public abstract void release();
	
	/** Update the content of the panel.
	 */
	public abstract void updateContent();
	
	/** Add an undoable command into the system.
	 * 
	 * @param cmd
	 */
	public void addUndo(Undoable cmd) {
		UndoManager manager = this.undoManager==null ? null : this.undoManager.get();
		if (manager!=null) {
			manager.add(cmd);
		}
	}
	
	/** Replies if the specified figure is supported by this panel.
	 * 
	 * @param figure
	 * @return <code>true</code> if the properties of the figure or
	 * its model are editable by this panel; otherwise <code>false</code>.
	 */
	public boolean isSupported(Figure figure) {
		return figure!=null && this.type.isInstance(figure);
	}

	/** Attach the editable elements of the specified figure to this panel.
	 * 
	 * @param figure
	 */
	public abstract void setFigure(Figure figure);
	
	/** Cast the figure.
	 * 
	 * @param f
	 * @return f.
	 */
	protected F castFigure(Figure f) {
		return (f==null) ? null : this.type.cast(f);
	}
	
}
