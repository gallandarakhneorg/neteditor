/* 
 * $Id$
 * 
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
package org.arakhne.neteditor.fsm.about ;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import org.arakhne.vmutil.locale.Locale;

/** Dialog box with the "About" information.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class JAboutDialog extends JDialog implements Runnable {

	private static final long serialVersionUID = -5042653173225242872L;

	/**
	 * @param parent
	 */
	public JAboutDialog(JFrame parent) {
		super(parent);
		
		String applicationName = Locale.getString("APPLICATION_NAME"); //$NON-NLS-1$
		String version = Locale.getString("VERSION"); //$NON-NLS-1$
		String date = Locale.getString("COMPILATION_DATE"); //$NON-NLS-1$
		
		setModal(true);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setTitle(Locale.getString("TITLE", applicationName)); //$NON-NLS-1$
		
		JTextArea text = new JTextArea();
		text.setEditable(false);
		text.setOpaque(false);
		text.setText(Locale.getString("TEXT", applicationName, version, date)); //$NON-NLS-1$
		text.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		add(BorderLayout.CENTER, text);
		
		JButton okButton = new JButton();
		okButton.setText(UIManager.getString("OptionPane.okButtonText")); //$NON-NLS-1$
		okButton.setActionCommand("Ok"); //$NON-NLS-1$
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JAboutDialog.this.setVisible(false);
			}
		});
		
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(BorderLayout.SOUTH, buttonPanel);
		buttonPanel.add(okButton);
		
		pack();
		
		Dimension d = getSize();
		setLocation(-d.width/2,-d.height/2);
		setLocationRelativeTo(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run() {
		setVisible(true);
	}

}
