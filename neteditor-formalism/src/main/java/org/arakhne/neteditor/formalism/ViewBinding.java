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

package org.arakhne.neteditor.formalism;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import org.arakhne.util.ref.WeakValueTreeMap;

/** This class manage the views associated to a model element.
 * This manager allows only one view per view container.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class ViewBinding {

	private final WeakReference<ModelObject> bindedObject;
	private final Map<UUID,View> binding = new WeakValueTreeMap<UUID,View>();

	/**
	 * @param object
	 */
	ViewBinding(ModelObject object) {
		this.bindedObject = new WeakReference<ModelObject>(object);
	}

	/** Replies the binded model object.
	 * 
	 * @return the model object.
	 */
	public ModelObject getModelObject() {
		return this.bindedObject.get();
	}

	/** Replies the first binded view of the specified type.
	 * 
	 * @param <T> is the type of the view to search for.
	 * @param viewUUID is the identifier of the view container that must enclose the view. 
	 * @param type is the type of the view to search for.
	 * @return the view or <code>null</code> if none.
	 */
	public synchronized <T> T getView(UUID viewUUID, Class<T> type) {
		Object view = this.binding.get(viewUUID);
		if (view!=null) {
			if (type.isInstance(view))
				return type.cast(view);
		}
		else if (this.binding.containsKey(viewUUID)) {
			unbind(viewUUID);
		}
		return null;
	}

	/** Bind a view.
	 * 
	 * @param viewUUID is the identifier of the view container that must enclose the view. 
	 * @param view is the view to bind to the model.
	 */
	public synchronized void bind(UUID viewUUID, View view) {
		this.binding.put(viewUUID, view);
	}

	/** Unbind a view.
	 * 
	 * @param viewUUID is the identifier of the view container that must enclose the view. 
	 */
	public synchronized void unbind(UUID viewUUID) {
		this.binding.remove(viewUUID);
		if (this.binding.isEmpty()) {
			ModelObject obj = getModelObject();
			if (obj instanceof AbstractModelObject) {
				((AbstractModelObject)obj).removeViewBinding();
			}
		}
	}
	
	/** Replies the views in the bindings.
	 * 
	 * @return the views in the bindings.
	 */
	public synchronized Map<UUID,View> getViews() {
		return Collections.unmodifiableMap(this.binding);
	}
	
	/** Move all the view components from a view to an other one.
	 * 
	 * @param from is the identifier of the view from which the components should move from.
	 * @param to is the identifier of the view from which the components should move to.
	 * @return the replaced view.
	 */
	public synchronized View replaceView(UUID from, UUID to) {
		assert(from!=null);
		assert(to!=null);
		if (!from.equals(to)) {
			View v = this.binding.remove(from);
			if (v!=null) {
				return this.binding.put(to, v);
			}
		}
		return null;
	}

}
