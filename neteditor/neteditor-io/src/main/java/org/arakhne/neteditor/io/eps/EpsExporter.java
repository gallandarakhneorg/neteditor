/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
 * Copyright (C) 2012-13 Stephane GALLAND.
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

package org.arakhne.neteditor.io.eps ;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.arakhne.afc.math.continous.object2d.Rectangle2f;
import org.arakhne.neteditor.io.AbstractVectorialExporter;

/** This exporter permits to create an Encapsulated Postscript file
 *  from a graphic context.
 * <p>
 * This graphic context supports the
 * <a href="http://www.adobe.com/products/postscript/pdfs/PLRM.pdf">Postscript Reference Document Third Edition</a>, and the
 * <a href="http://partners.adobe.com/public/developer/en/ps/5002.EPSF_Spec.pdf">EPS Reference Document 3.0</a>.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 0.4
 */
public class EpsExporter extends AbstractVectorialExporter<EpsGraphics2D, EpsOutputStream> {
	
	/**
	 */
	public EpsExporter() {
		//
	}
	
	/** {@inheritDoc}
	 */
	@Override
	public boolean isShadowSupported() {
		return false;
	}

	/** {@inheritDoc}
	 */
	@Override
	public final boolean isSpecificationCompliant() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EpsOutputStream createStream(OutputStream stream) {
		return new EpsOutputStream(stream);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	protected EpsGraphics2D prepareExport(File currentFile, EpsOutputStream stream, Rectangle2f bounds) throws IOException {
		return new EpsGraphics2D(bounds);
	}

	@Override
	protected void finalizeExport(File currentFile, EpsOutputStream stream,
			Rectangle2f documentBounds, EpsGraphics2D graphicContext)
			throws IOException {
		String content = graphicContext.getGeneratedString();
		stream.write(content);
	}

}
