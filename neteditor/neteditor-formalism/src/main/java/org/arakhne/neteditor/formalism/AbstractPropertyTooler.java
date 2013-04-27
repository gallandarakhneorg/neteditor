/* 
 * $Id$
 * 
 * Copyright (C) 2013 Stephane GALLAND.
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
package org.arakhne.neteditor.formalism;

import java.net.URI;
import java.net.URL;
import java.util.Map;


/** Provides standard tools to manage properties.
 *
 * @author $Author: galland$
 * @version $FullVersion$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public abstract class AbstractPropertyTooler {

	/** Utility function that permits to extract a property value from a map.
	 * This function could reply <code>null</code> if the given map contains
	 * a property with <code>null</code> value. In this case the default
	 * value is ignored. The default value is replied only if the
	 * given map does not contains the <var>name</var>.
	 * The parameter <var>forceDefaultIfNull</var> overrides this behavior
	 * by forcing the use of the default value when the map contains
	 * a <code>null</code>.
	 * 
	 * @param type is the type of the property.
	 * @param name is the name of the property.
	 * @param defaultValue is the default value.
	 * @param forceDefaultIfNull indicates if the default value must be replied
	 * when a <code>null</code> value is found in the map.
	 * @param properties is the set of properties.
	 * @return the property value from the given map. 
	 */
	public static <T> T propGet(Class<T> type, String name, T defaultValue, boolean forceDefaultIfNull, Map<String,Object> properties) {
		Object v = properties.get(name);
		if (v!=null) {
			if (type.isInstance(v)) {
				return type.cast(v);
			}
		}
		if (!forceDefaultIfNull && properties.containsKey(name)) return null;
		return defaultValue;
	}

	/** Utility function that permits to extract a property URL from a map.
	 * This function could reply <code>null</code> if the given map contains
	 * a property with <code>null</code> value. In this case the default
	 * value is ignored. The default value is replied only if the
	 * given map does not contains the <var>name</var>.
	 * The parameter <var>forceDefaultIfNull</var> overrides this behavior
	 * by forcing the use of the default value when the map contains
	 * a <code>null</code>.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @param forceDefaultIfNull indicates if the default value must be replied
	 * when a <code>null</code> value is found in the map.
	 * @return the property value
	 */
	public static URL propGetURL(String name, URL defaultValue, boolean forceDefaultIfNull, Map<String,Object> properties) {
		Object v = properties.get(name);
		if (v!=null) {
			try {
				if (v instanceof URL) {
					return (URL)v;
				}
				if (v instanceof URI) {
					return ((URI)v).toURL();
				}
				return new URL(v.toString());
			}
			catch(Throwable _) {
				//
			}
		}
		if (!forceDefaultIfNull && properties.containsKey(name)) return null;
		return defaultValue;
	}

	/** Utility function that permits to extract a property string from a map.
	 * This function could reply <code>null</code> if the given map contains
	 * a property with <code>null</code> value. In this case the default
	 * value is ignored. The default value is replied only if the
	 * given map does not contains the <var>name</var>.
	 * The parameter <var>forceDefaultIfNull</var> overrides this behavior
	 * by forcing the use of the default value when the map contains
	 * a <code>null</code>.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @param forceDefaultIfNull indicates if the default value must be replied
	 * when a <code>null</code> value is found in the map.
	 * @return the property value
	 */
	public static String propGetString(String name, String defaultValue, boolean forceDefaultIfNull, Map<String,Object> properties) {
		Object v = properties.get(name);
		String str = null;
		try {
			if (v!=null) str = v.toString();
		}
		catch(Throwable _) {
			str = null;
		}
		if ((str==null||str.isEmpty()) && 
				(forceDefaultIfNull || !properties.containsKey(name))) str = defaultValue;
		return str;
	}

	/** Utility function that permits to extract a property boolean from a map.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @return the property value
	 */
	public static boolean propGetBoolean(String name, boolean defaultValue, Map<String,Object> properties) {
		Object v = properties.get(name);
		Boolean bool = null;
		try {
			if (v instanceof Boolean) {
				bool = (Boolean)v;
			}
			else if (v!=null) {
				bool = Boolean.parseBoolean(v.toString());
			}
		}
		catch(Throwable _) {
			bool = null;
		}
		if (bool==null) return defaultValue;
		return bool.booleanValue();
	}

	/** Utility function that permits to extract a property integer from a map.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @return the property value
	 */
	public static int propGetInt(String name, int defaultValue, Map<String,Object> properties) {
		Object v = properties.get(name);
		Integer value = null;
		try {
			if (v instanceof Number) {
				value = ((Number)v).intValue();
			}
			else if (v!=null) {
				value = Integer.parseInt(v.toString());
			}
		}
		catch(Throwable _) {
			value = null;
		}
		if (value==null) return defaultValue;
		return value.intValue();
	}

	/** Utility function that permits to extract a property integer from a map.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @return the property value
	 */
	public static long propGetLong(String name, long defaultValue, Map<String,Object> properties) {
		Object v = properties.get(name);
		Long value = null;
		try {
			if (v instanceof Number) {
				value = ((Number)v).longValue();
			}
			else if (v!=null) {
				value = Long.parseLong(v.toString());
			}
		}
		catch(Throwable _) {
			value = null;
		}
		if (value==null) return defaultValue;
		return value.intValue();
	}

	/** Utility function that permits to extract a property byte from a map.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @return the property value
	 */
	public static byte propGetByte(String name, byte defaultValue, Map<String,Object> properties) {
		Object v = properties.get(name);
		Byte value = null;
		try {
			if (v instanceof Number) {
				value = ((Number)v).byteValue();
			}
			else if (v!=null) {
				value = Byte.parseByte(v.toString());
			}
		}
		catch(Throwable _) {
			value = null;
		}
		if (value==null) return defaultValue;
		return value.byteValue();
	}

	/** Utility function that permits to extract a property float from a map.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @return the property value
	 */
	public static float propGetFloat(String name, float defaultValue, Map<String,Object> properties) {
		Object v = properties.get(name);
		Float value = null;
		try {
			if (v instanceof Number) {
				value = ((Number)v).floatValue();
			}
			else if (v!=null) {
				value = Float.parseFloat(v.toString());
			}
		}
		catch(Throwable _) {
			value = null;
		}
		if (value==null) return defaultValue;
		return value.intValue();
	}

	/** Utility function that permits to extract a property float from a map.
	 * 
	 * @param name is the name of the property.
	 * @param properties is the set of properties.
	 * @param defaultValue is the default value.
	 * @return the property value
	 */
	public static double propGetDouble(String name, double defaultValue, Map<String,Object> properties) {
		Object v = properties.get(name);
		Double value = null;
		try {
			if (v instanceof Number) {
				value = ((Number)v).doubleValue();
			}
			else if (v!=null) {
				value = Double.parseDouble(v.toString());
			}
		}
		catch(Throwable _) {
			value = null;
		}
		if (value==null) return defaultValue;
		return value.intValue();
	}

}
