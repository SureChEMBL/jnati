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
import java.io.IOException;

import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.artefact.ArtefactFile;

import org.apache.log4j.Logger;

/**
 * @author Sam Adams
 */
public class NativeLibraryLoader {

    private static final Logger LOG = Logger.getLogger(NativeLibraryLoader.class);

    public static void loadLibrary(String id, String version) throws NativeCodeException {

        NativeArtefactLocator locator = new NativeArtefactLocator();
        Artefact artefact = locator.getArtefact(id, version);

        int redeploy = 0;
        Artefact current = artefact;
        while (redeploy < 1000) {   // Hard limit to prevent infinite loops!
            try {
                if (current != null) {
                    loadLibrary(current);
                }
                break;
            } catch (NativeCodeException e) {
                if (artefact.isMultideployEnabled()) {
                    if (artefact.getMaxMultideployCount() > 0 && redeploy >= artefact.getMaxMultideployCount()) {
                        throw new NativeCodeException("Unable to load native code - max deploy count reached ("+redeploy+")", e);
                    }
                }
                redeploy++;
                try {
                    current = locator.redeployArtefact(artefact, redeploy);
                } catch (Exception ex) {
                    continue;
                }
            }
        }
    }

    private static void loadLibrary(Artefact artefact) throws NativeCodeException {
        File root = artefact.getPath();

        for (ArtefactFile resource : artefact.getFileList()) {
            if (resource.isLibrary()) {
                File file = new File(root, resource.getPath());
                String path = file.getAbsolutePath();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Loading library: " + path);
                }
                try {
                    System.load(path);
                } catch (UnsatisfiedLinkError e) {
                    throw new NativeCodeException("Error loading native library: " + path, e);
                }
            }
        }
    }

}
