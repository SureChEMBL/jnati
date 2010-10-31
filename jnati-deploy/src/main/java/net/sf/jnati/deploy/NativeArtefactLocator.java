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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import net.sf.jnati.ArtefactDescriptor;
import net.sf.jnati.FileUtils;
import net.sf.jnati.NativeCodeException;
import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.artefact.ConfigManager;
import net.sf.jnati.deploy.repository.ClasspathRepository;
import net.sf.jnati.deploy.repository.LocalRepository;
import net.sf.jnati.deploy.repository.RemoteRepository;
import net.sf.jnati.deploy.resolver.ArtefactNotFoundException;
import net.sf.jnati.deploy.resolver.ArtefactResolver;
import net.sf.jnati.deploy.source.ArtefactSource;

import org.apache.log4j.Logger;

public class NativeArtefactLocator {
	
	private static final Logger LOG = Logger.getLogger(NativeArtefactLocator.class);

	public Artefact getArtefact(String id, String version) throws NativeCodeException {
		
		Artefact artefact = new Artefact(id, version);
		ConfigManager.loadConfiguration(artefact);
		
		// Search classpath
		ClasspathRepository cpRepo = new ClasspathRepository();
		List<? extends ArtefactSource> cpSources = null;
		try {
			cpSources = cpRepo.getArtefactSource(artefact);
		} catch (IOException e) {
			LOG.warn("Error searching classpath", e);
			cpSources = Collections.emptyList();
		}
		
		// Check for file: on classpath
		if (artefact.getAllowLocal()) {
			for (ArtefactSource source : cpSources) {
				if (source.isLocal()) {
					try {
						source.loadManifest(artefact);
						artefact.setPath(source.getPath());
						LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
						return artefact;
					} catch (IOException e) {
						LOG.warn("Error loading manifest", e);
					}
				}
			}
		}
		
		// Search local repository
		LocalRepository localRepo = new LocalRepository(artefact.getLocalRepository());
		List<? extends ArtefactSource> localSources = localRepo.getArtefactSource(artefact);
		for (ArtefactSource source : localSources) {
			if (source.isLocal()) {
				try {
					source.loadManifest(artefact);
					artefact.setPath(source.getPath());
					LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
					return artefact;
				} catch (IOException e) {
					LOG.warn("Error loading manifest", e);
				}
			}
		}
		
		// Deploy from classpath to local repository
		if (!cpSources.isEmpty()) {
			File target = localRepo.createArtefact(artefact);
			for (ArtefactSource source : cpSources) {
				try {
					new ArtefactResolver().resolve(artefact, source, target);
					LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
					return artefact;
				} catch (IOException e) {
					LOG.warn("Error resolving artefact to local repository", e);
				}
			}
			FileUtils.delTree(target);
		}
			
		// Search remote repositories
		if (artefact.getAllowDownload()) {
			List<String> urls = artefact.getRepositoryUrls();
			// Shuffle repository order - shares load
			Collections.shuffle(urls);
			for (String u : urls) {
				URL url;
				try {
					url = new URL(u);
				} catch (MalformedURLException e) {
					LOG.warn("Malformed URL", e);
					continue;
				}
				RemoteRepository remoteRepo = new RemoteRepository(url);
				List<? extends ArtefactSource> remoteSources;
				try {
					remoteSources = remoteRepo.getArtefactSource(artefact);
				} catch (IOException e) {
					LOG.warn("Error accessing remote repository", e);
					continue;
				}
				if (remoteSources.isEmpty()) {
					continue;
				}
				File target = localRepo.createArtefact(artefact);
				for (ArtefactSource source : remoteSources) {
					try {
						new ArtefactResolver().resolve(artefact, source, target);
						LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
						return artefact;
					} catch (IOException e) {
						LOG.warn("Error resolving artefact to local repository", e);
					}
				}
				FileUtils.delTree(target);
			}
		}
		
		throw new ArtefactNotFoundException("Artefact not found: " + artefact);
	}
	

	public static ArtefactDescriptor findArtefact(String id, String version) throws NativeCodeException {
		NativeArtefactLocator loc = new NativeArtefactLocator();
		Artefact artefact = loc.getArtefact(id, version);
    	return artefact.getArtefactLocation();
    }
	
}
