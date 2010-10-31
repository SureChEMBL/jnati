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
package net.sf.jnati.deploy;

import java.io.File;

import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.artefact.ArtefactFile;

import org.apache.log4j.Logger;


public class NativeLibraryLoader {
	
	private static final Logger LOG = Logger.getLogger(NativeLibraryLoader.class);
	
	public static void loadLibrary(String id, String version) throws NativeCodeException {
		
		NativeArtefactLocator locator = new NativeArtefactLocator();
		Artefact artefact = locator.getArtefact(id, version);
		File root = artefact.getPath();
		
		for (ArtefactFile mr : artefact.getFileList()) {
			if (mr.isLibrary()) {
				File file = new File(root, mr.getPath());
				String path = file.getPath();
				if (LOG.isDebugEnabled()) {
					LOG.debug("Loading library: " + path);
				}
				try {
					System.load(path);
				} catch (UnsatisfiedLinkError e) {
	            	LOG.error("Error loading native library: " + path, e);
	            	throw new NativeCodeException("Error loading native library: " + path, e);
	            }
			}
		}
		
	}
	
}
