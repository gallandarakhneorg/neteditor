/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.android.property;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.arakhne.afc.ui.android.property.TablePropertyEditorView;
import org.arakhne.afc.util.MultiValue;
import org.arakhne.afc.util.Pair;
import org.arakhne.afc.util.PropertyOwner;
import org.arakhne.neteditor.android.R;
import org.arakhne.neteditor.fig.figure.Figure;
import org.arakhne.neteditor.fig.figure.ModelObjectFigure;
import org.arakhne.neteditor.formalism.ModelObject;

import android.content.Context;


/** Property editor fragment for figures.  
 * 
 * @author $Author: galland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public class FigurePropertyEditorView extends TablePropertyEditorView {

	private static List<String> newPackageNameList(List<String> l) {
		List<String> list = (l==null) ? new ArrayList<String>() : l;
		list.add(R.class.getPackage().getName());
		return list;
	}
	
	/** Create a view to edit properties with in the list
	 * of package names only the default
	 * packages inherited from the ui-android ApkLib, and this
	 * ApkLib package.
	 * 
	 * @param context
	 */
	public FigurePropertyEditorView(Context context) {
		super(context, newPackageNameList(null));
	}

	/**
	 * @param context
	 * @param packageNames is the list of the package names from which 
	 * the label resources may be retreived.
	 */
	public FigurePropertyEditorView(Context context, List<String> packageNames) {
		super(context, newPackageNameList(packageNames));
	}

	/** Invoked to detect the type of the given property.
	 * The default implementation of this function invokes
	 * {@link #toFieldType(Class)}.
	 * <p>
	 * This function should be overwritten by subclasses
	 * to refine the type detection.
	 * 
	 * @param propertyName is the name of the property to consider.
	 * @param type is the java type of the property to consider.
	 * @return the field type; or <code>null</code>.
	 */
	@SuppressWarnings("static-method")
	protected FieldType detectTypeFor(String propertyName, Class<?> type) {
		return toFieldType(type);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void onCreateFields(Collection<? extends PropertyOwner> editedObject) {
		Map<String,Pair<FieldType,MultiValue<Object>>> fields = new TreeMap<String,Pair<FieldType,MultiValue<Object>>>();
		
		FieldType type;
		Map<String,Class<?>> fieldTypes;
		Map<String,Object> fieldValues;
		Pair<FieldType,MultiValue<Object>> pair;
		Object value;
		MultiValue<Object> multivalue;
		
		for(PropertyOwner object : editedObject) {
			if (object instanceof Figure) {
				Figure figure = (Figure)object;
				//
				// Treat the figure itself
				//
				fieldTypes = figure.getUIEditableProperties();
				fieldValues = figure.getProperties();
				for(Entry<String,Class<?>> entry : fieldTypes.entrySet()) {
					type = detectTypeFor(entry.getKey(), entry.getValue());
					if (type!=null) {
						value = fieldValues.get(entry.getKey());
						pair = fields.get(entry.getKey());
						if (pair==null) {
							multivalue = new MultiValue<Object>();
							if (value!=null) multivalue.add(value);
							pair = new Pair<FieldType, MultiValue<Object>>(
									type, multivalue);
							fields.put(entry.getKey(), pair);
						}
						else if (pair.getA()!=null && pair.getA()==type) {
							multivalue = pair.getB();
							if(value!=null) multivalue.add(value);
						}
						else if (pair.getA()!=null) {
							pair.setA(null);
						}
					}
				}
				//
				// Treat the model object
				//
				if (figure instanceof ModelObjectFigure<?>) {
					ModelObjectFigure<?> modelFigure = (ModelObjectFigure<?>)figure;
					ModelObject modelObject = modelFigure.getModelObject();
					if (modelObject!=null) {
						fieldTypes = modelObject.getUIEditableProperties();
						fieldValues = modelObject.getProperties();
						for(Entry<String,Class<?>> entry : fieldTypes.entrySet()) {
							type = detectTypeFor(entry.getKey(), entry.getValue());
							if (type!=null) {
								value = fieldValues.get(entry.getKey());
								pair = fields.get(entry.getKey());
								if (pair==null) {
									multivalue = new MultiValue<Object>();
									if (value!=null) multivalue.add(value);
									pair = new Pair<FieldType, MultiValue<Object>>(
											type, multivalue);
									fields.put(entry.getKey(), pair);
								}
								else if (pair.getA()!=null && pair.getA()==type) {
									multivalue = pair.getB();
									if(value!=null) multivalue.add(value);
								}
								else if (pair.getA()!=null) {
									pair.setA(null);
								}
							}
						}
					}
				}
			}
		}
		
		String label;
		for(Entry<String,Pair<FieldType,MultiValue<Object>>> entry : fields.entrySet()) {
			pair = entry.getValue();
			if (pair.getA()!=null) {
				label = getPropertyLabel(entry.getKey());
				
				switch(pair.getA()) {
				case BOOLEAN:
					addBooleanField(label, entry.getKey(), (Boolean)pair.getB().get());
					break;
				case INTEGER:
					addIntegerField(label, entry.getKey(), false, ((Number)pair.getB().get()).longValue());
					break;
				case FLOAT:
					addFloatField(label, entry.getKey(), false, ((Number)pair.getB().get()).doubleValue());
					break;
				case STRING:
					addStringField(label, entry.getKey(), (String)pair.getB().get());
					break;
				case PASSWORD:
					addTextPasswordField(label, entry.getKey(), (String)pair.getB().get());
					break;
				case EMAIL:
					addEmailField(label, entry.getKey(), (URI)pair.getB().get());
					break;
				case COLOR:
					addColorField(label, entry.getKey(), toColor(pair.getB().get()));
					break;
				case URL:
					addUriField(label, entry.getKey(), (URL)pair.getB().get());
					break;
				case COMBO:
					Enum e = (Enum)pair.getB().get();
					addComboField(
							label, entry.getKey(),
							Arrays.<Enum>asList(e.getClass().getEnumConstants()),
							e);
					break;
				default:
				}
			}
		}
	}
	
	@Override
	public void setPropertiesOf(PropertyOwner owner,
			Map<String, Object> properties) {
		super.setPropertiesOf(owner, properties);
		if (owner instanceof ModelObjectFigure<?>) {
			ModelObject mo = ((ModelObjectFigure<?>)owner).getModelObject();
			if (mo!=null) {
				mo.setProperties(properties);
			}
		}
	}

}
