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

package org.arakhne.neteditor.io ;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.io.resource.ResourceRepository;


/** Abstract implementation of a NetEditorReader.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public abstract class AbstractNetEditorReader implements NetEditorReader {
	
	private ResourceRepository resourceRepository = new ResourceRepository();
	private Progression taskProgression = null;

	/**
	 */
	public AbstractNetEditorReader() {
		//
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Progression getProgression() {
		return this.taskProgression;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProgression(Progression model) {
		Progression old = this.taskProgression;
		this.taskProgression = model;
		if (old!=null && this.taskProgression!=null) {
			this.taskProgression.setProperties(
					old.getValue(),
					old.getMinimum(),
					old.getMaximum(),
					old.isAdjusting(),
					old.getComment());
			this.taskProgression.setIndeterminate(old.isIndeterminate());
		}
	}
	
	@Override
	public final ResourceRepository getResourceRepository() {
		return this.resourceRepository;
	}

	@Override
	public final void setResourceRepository(ResourceRepository repos) {
		if (repos!=null && this.resourceRepository!=null)
			repos.copyFrom(this.resourceRepository);
		this.resourceRepository = repos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final String getReaderVersion() {
		return Locale.getString(AbstractNetEditorReader.class, "VERSION"); //$NON-NLS-1$
	}

}
