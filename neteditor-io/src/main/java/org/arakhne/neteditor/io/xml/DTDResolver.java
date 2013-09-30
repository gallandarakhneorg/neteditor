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

package org.arakhne.neteditor.io.xml;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.arakhne.afc.vmutil.FileSystem;
import org.arakhne.afc.vmutil.Resources;
import org.arakhne.afc.vmutil.URISchemeType;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class provides resolver for XML entities.
 * 
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class DTDResolver implements EntityResolver {

	private final String dtdURL;
	private final URL dtdFilename;

	/**
	 * @param dtdURL
	 * @param dtdFilename
	 */
	public DTDResolver(String dtdURL, URL dtdFilename) {
		this.dtdURL = dtdURL;
		this.dtdFilename = dtdFilename;
	}

	@SuppressWarnings("resource")
	private static InputSource search(String systemId, URL path) {
		InputStream systemIdStream = null;
		URL systemUrl = null;
		try {
			systemUrl = new URL(systemId);
			if (path!=null)
				systemUrl = FileSystem.makeAbsolute(path, systemUrl);
			systemIdStream = systemUrl.openStream();
			if (systemIdStream!=null) {
				return new InputSource(systemIdStream);
			}
		}
		catch(AssertionError e) {
			throw e;
		}
		catch(Throwable e) {
			//
		}

		if (systemUrl!=null && URISchemeType.getSchemeType(systemUrl).isFileBasedScheme()) {
			String file = systemUrl.getPath();
			systemIdStream = Resources.getResourceAsStream(file);
			if (systemIdStream !=null) {
				return new InputSource(systemIdStream);
			}
		}
		else {
			String id = systemId;
			if (path!=null)
				id = FileSystem.join(path, systemId).getPath();
			systemIdStream = Resources.getResourceAsStream(id);
			if (systemIdStream !=null) {
				return new InputSource(systemIdStream);
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
		if (this.dtdURL.equals(systemId)) {
			if (this.dtdFilename!=null)
				return new InputSource(this.dtdFilename.openStream());
		}
		if (systemId!=null && !"".equals(systemId)) { //$NON-NLS-1$
			InputSource is = search(systemId, null);
			if (is!=null) return is;
		}
		return null;
	}

}
