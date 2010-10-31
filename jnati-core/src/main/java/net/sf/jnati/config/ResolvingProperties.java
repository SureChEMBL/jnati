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

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Values ${name} are substituted with either parameters specified on
 * construction, or other properties. Parameters can also be included in
 * property names, but other properties cannot.
 * @author sea36
 *
 */
public class ResolvingProperties extends Properties {

    private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(ResolvingProperties.class);

    private Map<String, String> parameters = new HashMap<String, String>();

    public ResolvingProperties() {
    	super();
    }

    public ResolvingProperties(Map<String,String> params) {
    	super();
    	if (params != null) {
    		this.parameters.putAll(params);
    	}
    }

    public ResolvingProperties(ResolvingProperties defaults) {
    	super(defaults);
    }

    public ResolvingProperties(ResolvingProperties defaults, Map<String,String> params) {
        super(defaults);
        if (params != null) {
        	this.parameters.putAll(params);
        }
    }


    @Override
    public synchronized Object get(Object key) {
    	if (key instanceof String) {
    		key = resolveKey((String) key);
    	}
    	String sval = parameters.get(key);
    	if (sval == null) {
	    	Object oval = super.get(key);
			if (oval instanceof String) {
				sval = (String) oval;
			}
    	}
		return sval;
	}

    @Override
    public synchronized Object put(Object key, Object value) {
        if (key instanceof String) {
        	key = resolveKey((String)key);
        	if (parameters.containsKey(key)) {
        		throw new IllegalArgumentException("Parameter value cannot be overridden: " + key);
        	}
        }
        return super.put(key, value);
    }

    @Override
    public String getProperty(String key) {
        String value = resolvingGetProperty(key);
        return value;
    }

    /**
     * Returns value without resolving any parameters/values.
     * @param key
     * @return
     */
    private synchronized String nonResolvingGetProperty(String key) {
        String sval = null;
        Object oval = get(key);
        if (oval != null && oval instanceof String) {
            sval = (String) oval;
        } else {
            if (defaults != null) {
                if (defaults instanceof ResolvingProperties) {
                    sval = ((ResolvingProperties)defaults).nonResolvingGetProperty(key);
                } else {
                    sval = defaults.getProperty(key);
                }
            }
        }
        return sval;
    }


    private synchronized String resolvingGetProperty(String key) {

    	String value = nonResolvingGetProperty(key);
    	if (value != null) {

    		int i0 = value.indexOf("${");
        	while (i0 != -1 && value.length() > i0+2) {
    			int n = 1;
    	    	int i = i0+1;
        		char c = value.charAt(i+1);
    			while (n > 0 && i+1 < value.length()) {
    				c = value.charAt(i+1);
    				if (c == '}') {
    					n--;
    				} else if (c == '$' && value.charAt(i+2) == '{') {
    					n++;
    				}
    				i++;
    			}
    			if (n == 0) {
    				String k = value.substring(i0+2, i);
        			String val = resolvingGetProperty(k);
        			if (val != null) {
        				value = value.substring(0, i0)
    						+ val
    						+ value.substring(i+1);
        				i0 = value.indexOf("${", i0);
        			} else {
        				i0 = value.indexOf("${", i0+1);
        			}
        		} else {
        			i0 = value.indexOf("${", i0+1);
        		}
        	}

    	}
    	LOG.trace("GET " + key + " = " + value);

    	return value;
    }

    /**
     * Substitutes parameter values into key.
     * @param key
     * @return
     */
    private synchronized String resolveKey(String key) {
    	int i0 = key.indexOf("${");
    	if (i0 == -1) {
    		return key;
    	}
    	String oldkey = key;
    	while (i0 != -1 && key.length() > i0+2) {
			int n = 1;
	    	int i = i0+2;
    		char c = key.charAt(i+1);
			while (n > 0 && i+1 < key.length()) {
				c = key.charAt(i+1);
				if (c == '}') {
					n--;
				} else if (c == '$' && key.charAt(i+2) == '{') {
					n++;
				}
				i++;
			}
			if (n == 0) {
				String k = key.substring(i0+2, i);
    			String val = parameters.get(k);
    			if (val != null) {
    				String newkey = key.substring(0, i0)
						+ val
						+ key.substring(i+1);
    				LOG.trace(key + " > " + newkey);
    				key = newkey;
    				i0 = key.indexOf("${", i0);
    			} else {
    				LOG.warn("Unknown parameter: " + k);
        			i0 = key.indexOf("${", i0+1);
        		}
    		} else {
    			i0 = key.indexOf("${", i0+1);
    		}
    	}
    	LOG.trace("KEY " + oldkey + " = " + key);
    	return key;
    }

}
