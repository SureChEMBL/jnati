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
package net.sf.jnati.deploy.repository;

import java.io.File;
import java.util.Collections;
import java.util.List;

import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.source.ArtefactSource;
import net.sf.jnati.deploy.source.FileSource;

import org.apache.log4j.Logger;

/**
 * @author Sam Adams
 */
public class LocalRepository extends ArtefactRepository {
	
	private static final Logger LOG = Logger.getLogger(LocalRepository.class);
	
	private static final String FS = "/";
	
	private final File root;
	
	public LocalRepository(File root) {
		this.root = root;
	}
	
	private File getPath(Artefact artefact) {
		String path = artefact.getId() + FS
					+ artefact.getVersion() + FS
					+ artefact.getOsArch();
		return new File(root, path);
	}

    private File getPath(Artefact artefact, int index) {
		String path = artefact.getId() + FS
					+ artefact.getVersion() + FS
					+ artefact.getOsArch()
                    + "~" + index;
		return new File(root, path);
	}
	
	public boolean containsArtefact(Artefact artefact) {
		File file = getPath(artefact);
		return file.isDirectory();
	}

	public List<? extends ArtefactSource> getArtefactSource(Artefact artefact) {
		LOG.info("Searching local repository for: " + artefact);
		
		File path = getPath(artefact);
		LOG.debug("Artefact path: " + path);
		if (!path.isDirectory()) {
			return Collections.emptyList();
		}
		return Collections.singletonList(new FileSource(path, artefact));
	}

    public List<? extends ArtefactSource> getArtefactSource(Artefact artefact, int index) {
		LOG.info("Searching local repository for: " + artefact);

		File path = getPath(artefact, index);
		LOG.debug("Artefact path: " + path);
		if (!path.isDirectory()) {
			return Collections.emptyList();
		}
		return Collections.singletonList(new FileSource(path, artefact));
	}
	
	public File createArtefact(Artefact artefact) throws NativeCodeException {
		File path = getPath(artefact);
		LOG.info("Creating artefact: " + path);
		if (!path.mkdirs()) {
			throw new NativeCodeException("Error creating directory: " + path);
		}
		return path;
	}

    public File createArtefact(Artefact artefact, int index) throws NativeCodeException {
		File path = getPath(artefact, index);
		LOG.info("Creating artefact: " + path);
		if (!path.mkdirs()) {
			throw new NativeCodeException("Error creating directory: " + path);
		}
		return path;
	}
	
}
