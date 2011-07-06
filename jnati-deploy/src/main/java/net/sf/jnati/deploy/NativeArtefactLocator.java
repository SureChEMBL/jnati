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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

/**
 * @author Sam Adams
 */
public class NativeArtefactLocator {
	
	private static final Logger LOG = Logger.getLogger(NativeArtefactLocator.class);

	public Artefact getArtefact(String id, String version) throws NativeCodeException {
		
		Artefact artefact = new Artefact(id, version);
		ConfigManager.loadConfiguration(artefact);
		
		// Search classpath
        List<? extends ArtefactSource> classpathSources = findClasspathSources(artefact);
		
		// Check for file: on classpath
        if (findArtefactOnFilesystem(artefact, classpathSources)) {
            return artefact;
        }

        // Search local repository
		LocalRepository localRepo = new LocalRepository(artefact.getLocalRepository());
        if (findArtefactInLocalRepository(artefact, localRepo)) {
            return artefact;
        }

        // Deploy from classpath to local repository
        if (deployArtefactToLocalRepository(artefact, classpathSources, localRepo)) {
            return artefact;
        }

        // Search remote repositories
        if (deployArtefactFromRemoteRepository(artefact, localRepo)) {
            return artefact;
        }

        throw new ArtefactNotFoundException("Artefact not found: " + artefact);
	}

    private boolean deployArtefactFromRemoteRepository(Artefact artefact, LocalRepository localRepo) throws NativeCodeException {
        if (artefact.getAllowDownload()) {
            List<String> urls = artefact.getRepositoryUrls();
            // Shuffle repository order - shares load
            Collections.shuffle(urls);
            for (String u : urls) {
                List<? extends ArtefactSource> remoteSources = findRemoteSources(artefact, u);
                if (!remoteSources.isEmpty()) {
                    File target = localRepo.createArtefact(artefact);
                    for (ArtefactSource source : remoteSources) {
                        try {
                            new ArtefactResolver().resolve(artefact, source, target);
                            LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
                            return true;
                        } catch (IOException e) {
                            LOG.warn("Error resolving artefact to local repository", e);
                        }
                    }
                    FileUtils.delTree(target);
                }
            }
        }
        return false;
    }

    private List<? extends ArtefactSource> findRemoteSources(Artefact artefact, String u) {
        URL url;
        try {
            url = new URL(u);
            RemoteRepository remoteRepo = new RemoteRepository(url);
            try {
                List<? extends ArtefactSource> remoteSources = remoteRepo.getArtefactSource(artefact);
                return remoteSources;
            } catch (IOException e) {
                LOG.warn("Error accessing remote repository", e);
            }
        } catch (MalformedURLException e) {
            LOG.warn("Malformed URL", e);
        }
        return Collections.emptyList();
    }

    private boolean deployArtefactToLocalRepository(Artefact artefact, List<? extends ArtefactSource> classpathSources, LocalRepository localRepo) throws NativeCodeException {
        if (!classpathSources.isEmpty()) {
            File target = localRepo.createArtefact(artefact);
            for (ArtefactSource source : classpathSources) {
                try {
                    new ArtefactResolver().resolve(artefact, source, target);
                    LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
                    return true;
                } catch (IOException e) {
                    LOG.warn("Error resolving artefact to local repository", e);
                }
            }
            FileUtils.delTree(target);
        }
        return false;
    }

    private boolean findArtefactInLocalRepository(Artefact artefact, LocalRepository localRepo) {
        List<? extends ArtefactSource> localSources = localRepo.getArtefactSource(artefact);
        for (ArtefactSource source : localSources) {
            if (source.isLocal()) {
                try {
                    source.loadManifest(artefact);
                    artefact.setPath(source.getPath());
                    LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
                    return true;
                } catch (IOException e) {
                    LOG.warn("Error loading manifest", e);
                }
            }
        }
        return false;
    }

    private boolean findArtefactInLocalRepository(Artefact artefact, LocalRepository localRepo, int index) {
        List<? extends ArtefactSource> localSources = localRepo.getArtefactSource(artefact, index);
        for (ArtefactSource source : localSources) {
            if (source.isLocal()) {
                try {
                    source.loadManifest(artefact);
                    artefact.setPath(source.getPath());
                    LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
                    return true;
                } catch (IOException e) {
                    LOG.warn("Error loading manifest", e);
                }
            }
        }
        return false;
    }

    private boolean findArtefactOnFilesystem(Artefact artefact, List<? extends ArtefactSource> classpathSources) {
        if (artefact.getAllowLocal()) {
            for (ArtefactSource source : classpathSources) {
                if (source.isLocal()) {
                    try {
                        source.loadManifest(artefact);
                        artefact.setPath(source.getPath());
                        LOG.info("Artefact (" + artefact + ") location: " + artefact.getPath());
                        return true;
                    } catch (IOException e) {
                        LOG.warn("Error loading manifest", e);
                    }
                }
            }
        }
        return false;
    }

    private List<? extends ArtefactSource> findClasspathSources(Artefact artefact) {
        ClasspathRepository cpRepo = new ClasspathRepository();
        List<? extends ArtefactSource> cpSources = null;
        try {
            cpSources = cpRepo.getArtefactSource(artefact);
        } catch (IOException e) {
            LOG.warn("Error searching classpath", e);
            cpSources = Collections.emptyList();
        }
        return cpSources;
    }

    public Artefact redeployArtefact(Artefact artefact, int index) throws NativeCodeException, IOException {
        LocalRepository localRepo = new LocalRepository(artefact.getLocalRepository());

        Artefact redeploy = new Artefact(artefact);
        if (findArtefactInLocalRepository(redeploy, localRepo, index)) {
            return redeploy;
        }

        List<? extends ArtefactSource> sources = localRepo.getArtefactSource(artefact);
        if (!sources.isEmpty()) {
            ArtefactSource source = sources.get(0);
            File target = localRepo.createArtefact(artefact, index);
            source.loadManifest(redeploy);
            new ArtefactResolver().resolve(redeploy, source, target);
            return redeploy;
        }

        return null;
    }

	public static ArtefactDescriptor findArtefact(String id, String version) throws NativeCodeException {
		NativeArtefactLocator loc = new NativeArtefactLocator();
		Artefact artefact = loc.getArtefact(id, version);
    	return artefact.getArtefactLocation();
    }

}
