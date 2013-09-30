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

package org.arakhne.neteditor.io.resource ;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeMap;

import org.arakhne.afc.ui.vector.Image;
import org.arakhne.afc.util.Pair;
import org.arakhne.afc.vmutil.FileSystem;

/** Repository of resources that are pointed by the NetEditor files. 
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ResourceRepository {

	private long num = 0;
	
	private URL root;
	
	private final Map<URL,String> urlToInternalMapping = new TreeMap<URL, String>(new Comparator<URL>() {
		@Override
		public int compare(URL o1, URL o2) {
			if (o1==o2) return 0;
			if (o1==null) return Integer.MIN_VALUE;
			if (o2==null) return Integer.MAX_VALUE;
			return o1.toExternalForm().compareTo(o2.toExternalForm());
		}
	});
	private final Map<String,Object> internalToUrlMapping = new TreeMap<String, Object>();
	
	/**
	 */
	public ResourceRepository() {
		this(null);
	}
	
	/**
	 * @param root is the URL of the root for all the elements referenced by this repository.
	 */
	public ResourceRepository(URL root) {
		this.root = root;
	}

	/** Replies the root used by this ressource directory.
	 * 
	 * @return the root.
	 * @since 16.1
	 */
	public URL getRoot() {
		return this.root;
	}
	
	/** Set the root used by this ressource directory.
	 * 
	 * @param root is the root.
	 * @since 16.1
	 */
	public void setRoot(URL root) {
		this.root = root;
	}

	/** Copy the specified reposotiry in this repository.
	 * 
	 * @param repos
	 */
	public void copyFrom(ResourceRepository repos) {
		if (repos!=null) {
			this.urlToInternalMapping.putAll(repos.urlToInternalMapping);
			this.internalToUrlMapping.putAll(repos.internalToUrlMapping);
			this.num = Math.max(this.num, repos.num);
		}
	}
	
	/** Replies the pairs of URL/internal identifier.
	 * 
	 * @return the pairs.
	 */
	public Set<Entry<URL,String>> getURLEntries() {
		return Collections.unmodifiableSet(this.urlToInternalMapping.entrySet());
	}
	
	/** Replies the pairs of URL/internal identifier for images.
	 * 
	 * @return the pairs.
	 */
	public Iterable<Pair<String,Image>> getImages() {
		return new ImageIterable();
	}

	/** Register the specified pair.
	 * 
	 * @param iid
	 * @param url
	 */
	public synchronized void register(String iid, URL url) {
		this.internalToUrlMapping.put(iid, url);
		this.urlToInternalMapping.put(url, iid);
	}

	/** Register the specified URL and replies the internal identifier
	 * for this resource.
	 * @param url
	 * @return the internal identifier for the specified resource.
	 */
	public synchronized String mapsTo(URL url) {
		String iid = this.urlToInternalMapping.get(url);
		if (iid!=null) return iid;
		String ext = FileSystem.extension(url);
		iid = "r"+this.num+ext; //$NON-NLS-1$
		++this.num;
		this.urlToInternalMapping.put(url, iid);
		this.internalToUrlMapping.put(iid, url);
		return iid;
	}
	
	/** Register the specified image and replies the internal identifier
	 * for this resource.
	 * @param image
	 * @return the internal identifier for the specified resource.
	 */
	public synchronized String mapsTo(Image image) {
		String iid = "r"+this.num+".png"; //$NON-NLS-1$ //$NON-NLS-2$
		++this.num;
		this.internalToUrlMapping.put(iid, image);
		return iid;
	}

	/** Replies the internal identifier for the specified URL.
	 * @param url
	 * @return the internal identifier for the specified resource;
	 * or <code>null</code> if the specified value was never registered before.
	 */
	public synchronized String getInternalIdentifierFor(URL url) {
		return this.urlToInternalMapping.get(url);
	}

	/** Replies the URL for the specified internal identifier.
	 * @param iid
	 * @return the URL for the specified internal identifier;
	 * or <code>null</code> if the specified value was never registered before.
	 */
	public synchronized URL getURL(String iid) {
		Object o = this.internalToUrlMapping.get(iid);
		if (o instanceof File) {
			File file = (File)o;
			if (!file.isAbsolute() && this.root!=null) {
				URL u = FileSystem.makeAbsolute(file, this.root);
				if (u!=null) return u;
			}
			try {
				return ((File)o).toURI().toURL();
			}
			catch (MalformedURLException e) {
				return null;
			}
		}
		if (o instanceof URL) {
			return (URL)o;
		}
		return null;
	}

	/** Replies the Image for the specified internal identifier.
	 * @param iid
	 * @return the image for the specified internal identifier;
	 * or <code>null</code> if the specified value was never registered before.
	 */
	public synchronized Image getImage(String iid) {
		Object o = this.internalToUrlMapping.get(iid);
		if (o instanceof Image) {
			return (Image)o;
		}
		return null;
	}

	/**
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private class ImageIterable implements Iterable<Pair<String,Image>> {
		
		/**
		 */
		public ImageIterable() {
			//
		}

		/**
		 * {@inheritDoc}
		 */
		@SuppressWarnings("synthetic-access")
		@Override
		public Iterator<Pair<String, Image>> iterator() {
			return new ImageIterator(ResourceRepository.this.internalToUrlMapping.entrySet().iterator());
		}
		
	}

	/** Repository of resources that are pointed by the GXL file. 
	 *
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ImageIterator implements Iterator<Pair<String,Image>> {
		
		private final Iterator<Entry<String,Object>> iterator;
		
		private Pair<String,Image> pair;
		
		/**
		 * @param it
		 */
		public ImageIterator(Iterator<Entry<String,Object>> it) {
			this.iterator = it;
			searchNext();
		}
		
		private void searchNext() {
			this.pair = null;
			while (this.pair==null && this.iterator.hasNext()) {
				Entry<String,Object> entry = this.iterator.next();
				if (entry.getValue() instanceof Image) {
					this.pair = new Pair<String, Image>(entry.getKey(), (Image)entry.getValue());
				}
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			return this.pair!=null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Pair<String, Image> next() {
			Pair<String,Image> p = this.pair;
			if (p==null) throw new NoSuchElementException();
			searchNext();
			return p;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}

	}
	
}
