/*
 * Copyright 2008-2010 Sam Adams <sea36 at users.sourceforge.net>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.jnati.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements a multi-layered configuration. There are three layers of
 * configuration - in order of increasing precedence: defaults, configuration
 * and runtime. Defaults and configured properties can be loaded (from a
 * .properties file) or set (from a Map), while runtime properties are
 * retrieved from System.getProperties().
 *
 * @author Sam Adams
 */
public class Configuration {

	private Map<String,String> parameters;

	private ResolvingProperties base;
	private ResolvingProperties loaded;
	private ResolvingProperties runtime;

	/**
	 * Construct a new configuration with no parameters.
	 */
	public Configuration() {
		this(null);
	}

	/**
	 * Construct a new configuration with the specified parameters.
	 * @param params
	 */
	public Configuration(Map<String,String> params) {
		if (params != null) {
			this.parameters = new HashMap<String, String>(params);
		}
		base = new ResolvingProperties(parameters);
		loaded = new ResolvingProperties(base, parameters);
		runtime = new ResolvingProperties(loaded, parameters);
		runtime.putAll(System.getProperties());
	}

	/**
	 * Construct a new configuration with the specified parent configuration,
	 * and parameters.
	 * @param parentConfig
	 * @param params
	 */
	public Configuration(Configuration parentConfig, Map<String,String> params) {
		if (parentConfig.parameters != null) {
			this.parameters = new HashMap<String, String>(parentConfig.parameters);
			if (params != null) {
				this.parameters.putAll(params);
			}
		} else if (params != null) {
			this.parameters = new HashMap<String, String>(params);
		}
		base = new ResolvingProperties(parameters);
		base.putAll(parentConfig.base);
		loaded = new ResolvingProperties(base, parameters);
		loaded.putAll(parentConfig.loaded);
		runtime = new ResolvingProperties(loaded, parameters);
		runtime.putAll(parentConfig.runtime);
		runtime.putAll(System.getProperties());
	}


	/**
	 * Load the default property values. Existing defaults with the same name
	 * will be overridden. Invokes java.util.Properties.load().
	 * @param in
	 * @throws java.io.IOException
	 */
	public void loadDefaults(InputStream in) throws IOException {
		this.base.load(in);
	}

	/**
	 * Sets the default property values. Existing defaults with the same name
	 * will be overridden.
	 * @param defaults
	 */
	public void setDefaults(Map<String,String> defaults) {
		this.base.putAll(defaults);
	}


	/**
	 * Load the configured properties. Invokes java.util.Properties.load().
	 * @param in
	 * @throws IOException
	 */
	public void loadConfiguration(InputStream in) throws IOException {
		this.loaded.load(in);
	}

	/**
	 * Sets the configured properties.
	 * @param config
	 */
	public void setConfiguration(Map<String,String> config) {
		this.loaded.putAll(config);
	}


	/**
	 * Gets the property's value.
	 * @param key
	 * @return	The value for the specified key, or null if no property of that
	 * name exists.
	 */
	public String getProperty(String key) {
		return runtime.getProperty(key);
	}

}
