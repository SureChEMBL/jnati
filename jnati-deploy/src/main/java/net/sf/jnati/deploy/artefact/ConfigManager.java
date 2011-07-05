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
package net.sf.jnati.deploy.artefact;

import net.sf.jnati.SystemType;
import net.sf.jnati.SystemTyper;
import net.sf.jnati.config.Configuration;
import net.sf.jnati.deploy.resolver.ConfigurationException;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Configuration hierarchy:</p>
 * <table border="1">
 * <tr><td>runtime properties</td></tr>
 * <tr><td>// instance-id_version properties</td></tr>
 * <tr><td>instance-id properties</td></tr>
 * <tr><td>global properties file [ ${jnati.settingsFile} ]</td></tr>
 * <tr><td>instance defaults</td></tr>
 * <tr><td>global defaults</td></tr>
 * @author Sam Adams
 *
 */
public class ConfigManager {

	private static final Logger LOG = Logger.getLogger(ConfigManager.class);
	
	private static final String DEFAULT_CONFIG_FILE = "/META-INF/jnati/jnati.default-properties";
	private static final String DEFAULT_INSTANCE_CONFIG_FILE = "/META-INF/jnati/jnati.instance.default-properties";
	
	private static final String CLASSPATH_CONFIG_FILE = "/META-INF/jnati/jnati.properties";
	
	private static Configuration defaultConfig;
	
	
	public static synchronized Configuration getDefaultConfig() throws IOException {
		
		if (defaultConfig == null) {
			
			LOG.info("Loading global configuration");
			
			Class<?> cl = ConfigManager.class;
			URL u1 = cl.getResource(DEFAULT_CONFIG_FILE);
			if (u1 == null) {
				throw new FileNotFoundException("Default config file missing: " + DEFAULT_CONFIG_FILE);
			}

            SystemType systype = SystemTyper.getDefaultInstance().detectPlatform();
			Map<String,String> params = new HashMap<String,String>();
			params.put("jnati.osarch", systype.getName());

			defaultConfig = new Configuration(params);
			LOG.debug("Loading defaults: " + u1);
			InputStream i1 = u1.openStream();
			try {
				defaultConfig.loadDefaults(i1);
			} finally {
				i1.close();
			}
			
			String filename = defaultConfig.getProperty("jnati.settingsFile");
			if (filename == null) {
				LOG.warn("jnati.settingsFile not defined");
			} else {
				File file = new File(filename);
				if (file.exists()) {
					LOG.debug("Loading config: " + file);
					InputStream i2 = new BufferedInputStream(new FileInputStream(file));
					try {
						defaultConfig.loadConfiguration(i2);
					} finally {
						i2.close();
					}
				}
			}
			
			URL u3 = cl.getResource(CLASSPATH_CONFIG_FILE);
			if (u3 != null) {
				LOG.debug("Loading configuration: " + u3);
				InputStream is = u3.openStream();
				try {
					defaultConfig.loadConfiguration(is);
				} finally {
					is.close();
				}	
			}
			
		}
		
		return defaultConfig;
	}
	
	

	public static Configuration getConfig(String id, String version) throws IOException {

		Configuration defaultConfig = getDefaultConfig();
		
		LOG.info("Loading artefact configuration: " + id + "-" + version);
		
		Map<String,String> params = new HashMap<String, String>();
		params.put("jnati.artefactId", id);
		params.put("jnati.artefactVersion", version);

        SystemType systype = SystemTyper.getDefaultInstance().detectPlatform();
        params.put("jnati.osarch", systype.getName());
		
		Class<?> cl = ConfigManager.class;
		URL u1 = cl.getResource(DEFAULT_INSTANCE_CONFIG_FILE);
		if (u1 == null) {
			throw new FileNotFoundException("Default config file missing: " + DEFAULT_INSTANCE_CONFIG_FILE);
		}
		
		Configuration config = new Configuration(defaultConfig, params);
		InputStream i1 = u1.openStream();
		LOG.debug("Loading instance defaults: " + u1);
		try {
			config.loadDefaults(i1);
		} finally {
			i1.close();
		}
		
		String s1 = "${jnati.artefactId}.settingsFile";
		String fn1 = config.getProperty(s1);
		if (fn1 == null) {
			throw new IOException(s1 + " not defined");
		}
		File f1 = new File(fn1);
		if (f1.exists()) {
			LOG.debug("Loading config: " + f1);
			InputStream is = new BufferedInputStream(new FileInputStream(f1));
			try {
				config.loadConfiguration(is);
			} finally {
				is.close();
			}
		}
		
		String artefactConfigFile = "/META-INF/jnati/" + id + ".properties";
		URL u2 = cl.getResource(artefactConfigFile);
		if (u2 != null) {
			LOG.debug("Loading configuration: " + u2);
			InputStream is = u2.openStream();
			try {
				config.loadConfiguration(is);
			} finally {
				is.close();
			}
		}
		
		return config;
	}



	public static void loadConfiguration(Artefact artefact) throws ConfigurationException {
		
		try {
			Configuration config = getConfig(artefact.getId(), artefact.getVersion());
			artefact.setConfiguration(config);
		} catch (IOException e) {
			throw new ConfigurationException("Error loading configuration", e);
		}
		
	}
	
}
