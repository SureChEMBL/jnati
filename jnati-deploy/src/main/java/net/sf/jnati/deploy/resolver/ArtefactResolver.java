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
package net.sf.jnati.deploy.resolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import net.sf.jnati.FileUtils;
import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.artefact.ArtefactFile;
import net.sf.jnati.deploy.source.ArtefactSource;

import org.apache.log4j.Logger;

public class ArtefactResolver {

	private static final Logger LOG = Logger.getLogger(ArtefactResolver.class);
	
	public void resolve(Artefact artefact, ArtefactSource source, File target) throws IOException, NativeCodeException {
		
		try {
			
			// Load manifest
			source.loadManifest(artefact);
			
			boolean linux = artefact.getOsArch().contains("LINUX");
			
			LOG.info("Copying files to repository: " + target);
			
			for (ArtefactFile artefactFile : artefact.getFileList()) {
				
				// Get output file
				String path = artefactFile.getPath();
				File outFile = new File(target, path);
				
				// Ensure output directory exists
				File outDir = outFile.getParentFile();
				if (!outDir.isDirectory()) {
					if (!outDir.mkdirs()) {
						throw new IOException();
					}
				}
				
				LOG.trace("Copying file: " + path);
				
				// Get input
				InputStream in = source.openFile(path);
				
				// Write file
				FileUtils.writeStreamToFile(in, outFile);	// closes stream
			
				if (linux && artefactFile.isExe()) {
					makeExecutable(outFile);
				}
				
			}
			
			// Copy manifest
			File outFile = new File(target, "MANIFEST.xml");
			InputStream in = source.openFile("MANIFEST.xml");
			FileUtils.writeStreamToFile(in, outFile);
			
			artefact.setPath(target);
			
		} finally {
			source.close();
		}
	}
	
	private void makeExecutable(File file) throws NativeCodeException {
        String fp = file.getAbsolutePath();
        LOG.debug("Making file executable: " + fp);
        ProcessBuilder pb = new ProcessBuilder("chmod", "u+x", fp);
        int exit;
        try {
        	Process p = pb.start();
        	exit = p.waitFor();
        } catch (InterruptedException e) {
            throw new NativeCodeException("Error setting file executable: " + fp, e);
        } catch (IOException e) {
        	throw new NativeCodeException("Error setting file executable: " + fp, e);
        }
        if (exit != 0) {
            String message = "Failed to set file as executable: " + fp;
            LOG.error(message);
            throw new NativeCodeException(message);
        }
    }
}
