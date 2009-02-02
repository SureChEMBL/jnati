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
package net.sf.jnati;

import java.io.File;

/**
 * Describes the location of an artefact.
 * 
 * @author sea36
 */
public class ArtefactLocation {
    
	private final String id;
	private final String version;
	private final String osarch;
	
	private final File root;

	/**
	 * Construct artefact location. Throws NullPointerException if any arguments
	 * are null.
	 * @param id		- artefact ID.
	 * @param version	- artefact version.
	 * @param osarch	- artefact OS/architecture.
	 * @param root		- directory containing root of artefact.
	 */
    public ArtefactLocation(String id, String version, String osarch, File root) {
    	
    	if (id == null) {
    		throw new NullPointerException("Null ID");
    	}
    	if (version == null) {
    		throw new NullPointerException("Null version");
    	}
    	if (osarch == null) {
    		throw new NullPointerException("Null OS/architecture");
    	}
    	if (root == null) {
    		throw new NullPointerException("Null root directory");
    	}
    	
    	this.id = id;
    	this.version = version;
    	this.osarch = osarch;
    	this.root = root;
    	
    }
    
    /**
     * Returns artefact ID.
     * @return
     */
    public String getId() {
		return id;
	}
    
    /**
     * Returns artefact version.
     * @return
     */
    public String getVersion() {
		return version;
	}
    
    /**
     * Returns OS/architecture.
     * @return
     */
    public String getOsArch() {
		return osarch;
	}
    
    /**
     * Returns root directory.
     * @return
     */
    public File getPath() {
		return root;
	}
    
}