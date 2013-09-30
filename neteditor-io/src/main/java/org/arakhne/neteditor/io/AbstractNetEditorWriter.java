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

import java.math.BigInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.vmutil.locale.Locale;
import org.arakhne.neteditor.io.resource.ResourceRepository;


/** Abstract implementation of a NetEditorWriter.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 16.0
 */
public abstract class AbstractNetEditorWriter implements NetEditorWriter {
	
	/** Replies if the given object is a number that is
	 * an integer.
	 * 
	 * @param v
	 * @return <code>true</code> if <var>v</var> is an integer;
	 * <code>false</code> otherwise.
	 */
	protected static boolean isInteger(Object v) {
		if (v!=null) {
			return v instanceof Byte
					|| v instanceof Short
					|| v instanceof Integer
					|| v instanceof Long
					|| v instanceof AtomicInteger
					|| v instanceof AtomicLong
					|| v instanceof BigInteger;
		}
		return false;
	}

	/** Replies if the given object is a number that is
	 * a floating-point number.
	 * 
	 * @param v
	 * @return <code>true</code> if <var>v</var> is a
	 * floating-point number; <code>false</code> otherwise.
	 */
	protected static boolean isFloat(Object v) {
		return (v instanceof Number) && !isInteger(v);
	}

	/** Replies if the given object is a boolean.
	 * 
	 * @param v
	 * @return <code>true</code> if <var>v</var> is a
	 * boolean; <code>false</code> otherwise.
	 */
	protected static boolean isBoolean(Object v) {
		if (v!=null) {
			return v instanceof Boolean
					|| v instanceof AtomicBoolean;
		}
		return false;
	}

	/** Replies if the given object is a string.
	 * 
	 * @param v
	 * @return <code>true</code> if <var>v</var> is a
	 * string; <code>false</code> otherwise.
	 */
	protected static boolean isString(Object v) {
		if (v!=null) {
			return v instanceof CharSequence
					|| v instanceof Character;
		}
		return false;
	}

	
	
	
	private boolean isAnchorOutput = true;
	private ResourceRepository resourceRepository = new ResourceRepository();
	private Progression taskProgression = null;
	
	/**
	 */
	public AbstractNetEditorWriter() {
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
	public final boolean isAnchorOutput() {
		return this.isAnchorOutput;
	}

	@Override
	public final void setAnchorOutput(boolean anchorOutput) {
		this.isAnchorOutput = anchorOutput;
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
	public final String getWriterVersion() {
		return Locale.getString(AbstractNetEditorWriter.class, "VERSION"); //$NON-NLS-1$
	}

}
