/* 
 * $Id$
 * 
 * Copyright (C) 2002 Stephane GALLAND, Madhi HANNOUN, Marc BAUMGARTNER.
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

package org.arakhne.neteditor.io.xml ;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;

import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.progress.ProgressionUtil;
import org.arakhne.neteditor.fig.view.ViewComponent;
import org.arakhne.neteditor.formalism.Graph;
import org.arakhne.neteditor.io.AbstractNetEditorReader;
import org.arakhne.vmutil.locale.Locale;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/** Abstract implementation of a NetEditor reader that provides XML tools.
 * This class provides basic tools to parse XML nodes.
 * It does not provides any abstract implementation of the parser.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractXMLToolReader extends AbstractNetEditorReader {

	/** Size of a progression step in the iterators.
	 */
	protected static final int PROGRESS_STEP_SIZE_IN_ITERATOR = 100;
	
	/**
	 */
	public AbstractXMLToolReader() {
		//
	}

	/** Replies the child element in the specified parent and with the specified name.
	 * This functions returns <code>null</code> when the node was not found.
	 * 
	 * @param parent is the node in which the child should be.
	 * @param name is the name of the node to reply.
	 * @return the node.
	 * @see #extractNode(Node, String)
	 */
	protected static Element extractNodeNoFail(Node parent, String name) {
		NodeList list = parent.getChildNodes();
		if (list!=null) {
			for(int i=0; i<list.getLength(); ++i) {
				Node child = list.item(i);
				if (child.getNodeName().equals(name) && child instanceof Element) {
					return (Element)child;
				}
			}
		}
		return null;
	}

	/** Replies the child element in the specified parent and with the specified name.
	 * This function throws an exception when the not was not found.
	 * 
	 * @param parent is the node in which the child should be.
	 * @param name is the name of the node to reply.
	 * @return the node.
	 * @throws IOException when the node was not found.
	 * @see #extractNodeNoFail(Node, String)
	 */
	protected static Element extractNode(Node parent, String name) throws IOException {
		Element e = extractNodeNoFail(parent, name);
		if (e!=null) return e;
		throw new IOException(Locale.getString("XML_NODE_NOT_FOUND", name)); //$NON-NLS-1$
	}
	
	/** Replies the XML Elements with the given name inside the given parent.
	 *  
	 * @param parent
	 * @param name
	 * @return the elements with the given name in the given parent.
	 */
	protected static Iterable<Element> elements(Node parent, String name) {
		return new ElementIterable(parent, name);
	}

	/** Replies the XML Elements with the given name inside the given parent.
	 *  
	 * @param parent
	 * @param name
	 * @param progression
	 * @return the elements with the given name in the given parent.
	 */
	protected static Iterable<Element> elements(Node parent, String name, Progression progression) {
		return new ElementIterable(parent, name, progression);
	}

	/** Replies the XML Elements with the given name inside the given parent.
	 *  
	 * @param parent
	 * @param name
	 * @return the elements with the given name in the given parent.
	 */
	protected static Iterator<Element> elementIterator(Node parent, String name) {
		return new ElementIterator(parent, name);
	}

	/** Replies the XML Elements with the given name inside the given parent.
	 *  
	 * @param parent
	 * @param name
	 * @param progression
	 * @return the elements with the given name in the given parent.
	 */
	protected static Iterator<Element> elementIterator(Node parent, String name, Progression progression) {
		return new ElementIterator(parent, name, progression);
	}

	/** Replies the XML Elements inside the given parent.
	 *  
	 * @param parent
	 * @return the elements with the given name in the given parent.
	 */
	protected static Iterable<Element> elements(Node parent) {
		return new ElementIterable(parent, null);
	}

	/** Replies the XML Elements inside the given parent.
	 *  
	 * @param parent
	 * @param progression
	 * @return the elements with the given name in the given parent.
	 */
	protected static Iterable<Element> elements(Node parent, Progression progression) {
		return new ElementIterable(parent, null, progression);
	}

	/** Extract the type of the specified node as a string.
	 * 
	 * @param node is the node to explore
	 * @return the type of the node, never <code>null</code>.
	 * @throws IOException
	 */
	protected abstract String extractType(Element node) throws IOException;
	
	/** Extract the type of the specified node as a class.
	 * 
	 * @param node is the node to explore
	 * @return the type of the node, never <code>null</code>.
	 * @throws IOException
	 */
	protected final Class<?> extractTypeClass(Element node) throws IOException {
		String type = extractType(node);
		try {
			return Class.forName(type);
		}
		catch (ClassNotFoundException _) {
			//
		}
		throw new IOException(Locale.getString("UNSUPPORTED_XML_NODE", node.getNodeName())); //$NON-NLS-1$
	}

	/** Extract the type of a node and create the corresponding instance.
	 * 
	 * @param type is the expected type of the new instance.
	 * @param node is the node from which the type should be extracted.
	 * @return the new instance.
	 * @throws IOException
	 */
	protected final <T> T createInstance(Class<T> type, Element node) throws IOException {
		Class<?> foundType = extractTypeClass(node);
		if (type.isAssignableFrom(foundType)) {
			try {
				Object obj = foundType.newInstance();
				return type.cast(obj);
			}
			catch (Exception e) {
				throw new IOException(e);
			}
		}
		throw new IOException(Locale.getString("UNEXPECTED_TYPE", node.getNodeName(), type.getCanonicalName())); //$NON-NLS-1$
	}

	/** Extract the type of a node and create the corresponding instance.
	 * 
	 * @param type is the expected type of the new instance.
	 * @param node is the node from which the type should be extracted.
	 * @param viewId is the identifier of the view.
	 * @return the new instance.
	 * @throws IOException
	 */
	protected final <T extends ViewComponent> T createFigureInstance(Class<T> type, Element node, UUID viewId) throws IOException {
		Class<?> foundType = extractTypeClass(node);
		if (type.isAssignableFrom(foundType)) {
			try {
				Constructor<?> cons = foundType.getConstructor(UUID.class);
				Object obj = cons.newInstance(viewId);
				return type.cast(obj);
			}
			catch (Exception e) {
				throw new IOException(e);
			}
		}
		throw new IOException(Locale.getString("UNEXPECTED_TYPE", node.getNodeName(), type.getCanonicalName())); //$NON-NLS-1$
	}

	/** Replies if the specified node is the node for a graph model.
	 * 
	 * @param node is the node to test.
	 * @return <code>true</code> if the specified node contains a graph model;
	 * otherwise <code>false</code>.
	 * @throws IOException
	 */
	protected final boolean isGraphModel(Element node) throws IOException {
		try {
			Class<?> type = extractTypeClass(node);
			if (type!=null && Graph.class.isAssignableFrom(type)) {
				return true;
			}
		}
		catch(Throwable _) {
			//
		}
		return false;
	}

	/** Parse the given string to obtain an UUID.
	 * This function never throws an exception when the given string
	 * cannot be parsed. It returns a <code>null</code> value in this case
	 * @param str
	 * @return the UUID or <code>null</code> if the given string cannot be parsed.
	 * @see #enforceUUID(String)
	 */
	protected static UUID parseUUID(String str) {
		try {
			if (str!=null && !str.isEmpty()) {
				return UUID.fromString(str);
			}
		}
		catch(Throwable _) {
			//
		}
		return null;
	}
	
	/** Parse the given string to obtain an UUID.
	 * This function throws an exception when the given string
	 * cannot be parsed.
	 * @param str
	 * @return the UUID, never <code>null</code>.
	 * @throws IOException
	 * @see #parseUUID(String)
	 */
	protected static UUID enforceUUID(String str) throws IOException {
		try {
			return UUID.fromString(str);
		}
		catch(Throwable e) {
			throw new IOException(e);
		}
	}

	/**
	 * This class provides in iterator on XML elements.
	 * 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ElementIterable implements Iterable<Element> {
		
		private final Node parent;
		private final String name;
		private final Progression progression;
		
		public ElementIterable(Node parent, String name) {
			this(parent, name, null);
		}
		
		public ElementIterable(Node parent, String name, Progression progression) {
			this.parent = parent;
			this.name = name;
			this.progression = progression;
		}

		@Override
		public Iterator<Element> iterator() {
			return new ElementIterator(this.parent, this.name, this.progression);
		}
	}

	/**
	 * This class provides in iterator on XML elements.
	 * 
	 * @author $Author: galland$
	 * @version $FullVersion$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 */
	private static class ElementIterator implements Iterator<Element> {

		private final NodeList list;
		private final String name;
		private final Progression progression;
		private int index = 0;

		private Element next = null;

		/**
		 * @param parent
		 * @param name
		 */
		public ElementIterator(Node parent, String name) {
			this(parent, name, null);
		}

		/**
		 * @param parent
		 * @param name
		 * @param progression
		 */
		public ElementIterator(Node parent, String name, Progression progression) {
			this.list = parent.getChildNodes();
			this.name = name;
			this.progression = progression;
			ProgressionUtil.init(progression, 0, this.list.getLength()*PROGRESS_STEP_SIZE_IN_ITERATOR);
			searchNext();
		}

		private void searchNext() {
			this.next = null;
			while (this.next==null && this.index<this.list.getLength()) {
				Node n = this.list.item(this.index);
				if (n instanceof Element &&
					( ( this.name==null) || n.getNodeName().equals(this.name)) ) {
					this.next = (Element)n;
				}
				++this.index;
			}
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public boolean hasNext() {
			if (this.next==null) {
				ProgressionUtil.end(this.progression);
			}
			return this.next!=null;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public Element next() {
			Element e = this.next;
			if (e==null) throw new NoSuchElementException();
			if (!ProgressionUtil.isMinValue(this.progression)) {
				ProgressionUtil.setValue(this.progression, this.index*PROGRESS_STEP_SIZE_IN_ITERATOR);
			}
			searchNext();
			return e;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}


	} // class ElementIterator

}
