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
package org.arakhne.neteditor.swing.selection ;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

import org.arakhne.neteditor.fig.selection.SelectionManager;
import org.arakhne.neteditor.swing.dnd.TransferableFigureSet;

/** This class is the selection manager for Swing. It is basically a
 *  collection of selected figures.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JSelectionManager extends SelectionManager implements ClipboardOwner {

	/** Create a new JSelectionManager.
	 */
	public JSelectionManager() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void updateSystemSelection() {
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemSelection();
		clipboard.setContents(
				getTransferableSelection(),
				this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public synchronized void lostOwnership(Clipboard clipboard, Transferable contents) {
		//
	}

	/** Replies a transferable selection that
	 *  contains a copy of the current selection.
	 *
	 * @return the transferable version of this selection.
	 * @since 0.2
	 */
	public synchronized TransferableFigureSet getTransferableSelection() {
		return new TransferableFigureSet(this) ;
	}

}
