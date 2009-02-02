/*
 * Copyright 2008 Sam Adams <sea36 at users.sourceforge.net>
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
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301 USA
 * or see <http://www.gnu.org/licenses/>.
 */
package net.sf.jnati.envtool;

import java.io.IOException;

import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.NativeLibraryLoader;

import org.apache.log4j.Logger;

public class EnvTool {

	private static final Logger LOG = Logger.getLogger(EnvTool.class);

	private static final String ID = "envtool";
    private static final String VERSION = "0.1";

    private static EnvTool tool;
    
    public synchronized static EnvTool getInstance() throws NativeCodeException {
        if (tool == null) {
        	LOG.info("Generating instance");
            try {
            	tool = new EnvTool();
            } catch (NativeCodeException ex) {
            	LOG.error("Error", ex);
                throw new NativeCodeException(ex.getMessage());
            } catch (IOException ex) {
            	LOG.error("Error", ex);
                throw new NativeCodeException(ex.getMessage());
            }
        }
        return tool;
    }

    /**
     * Constructor. Sets/detects properties.
     */
    private EnvTool() throws NativeCodeException, IOException {
    	NativeLibraryLoader.loadLibrary(ID, VERSION);
    }


    public String getEnvVar(final String key) {
    	return jniGetEnvVar(key);
    }

	public void setEnvVar(final String key, final String value) {
		jniSetEnvVar(key, value);
	}

	private native String jniGetEnvVar(final String key);

	private native void jniSetEnvVar(final String key, final String value);

}
