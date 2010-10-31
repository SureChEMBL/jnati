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
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.source.ArtefactSource;
import net.sf.jnati.deploy.source.FileSource;
import net.sf.jnati.deploy.source.JarSource;

import net.sf.jnati.deploy.source.UrlSource;
import org.apache.log4j.Logger;

public class ClasspathRepository extends ArtefactRepository {

	private static final Logger LOG = Logger.getLogger(ClasspathRepository.class);
	
	private static final String FS = "/";
	private static final String METAINF = "META-INF";
	private static final String MANIFEST = "MANIFEST.xml";
	
	private static String getManifestPath(Artefact artefact) {
		String path = getArtefactPath(artefact) + MANIFEST;
		return path;
	}

    private static String getArtefactPath(Artefact artefact) {
        String path = METAINF + FS
					+ artefact.getId() + FS
					+ artefact.getVersion() + FS
					+ artefact.getOsArch() + FS;
        return path;
    }

    public List<? extends ArtefactSource> getArtefactSource(Artefact artefact) throws IOException {
		LOG.info("Searching classpath for: " + artefact);
		String name = getManifestPath(artefact);
		Enumeration<URL> enumeration = getClass().getClassLoader().getResources(name);
		
		List<ArtefactSource> list = new ArrayList<ArtefactSource>();
		for (URL url : Collections.list(enumeration)) {
			
			LOG.trace("Manifest location: " + url);
			
			String protocol = url.getProtocol();
			if ("file".equalsIgnoreCase(protocol)) {
				list.add(getFileSource(url, artefact));
			}
			else if ("jar".equalsIgnoreCase(protocol)) {
				list.add(getJarSource(url, artefact));
			}
            else {
				LOG.warn("Unknown URL protocol: " + protocol);
                list.add(getUrlSource(url, artefact, getArtefactPath(artefact)));
			}
		}
		
		return list;
	}

    private ArtefactSource getUrlSource(URL url, Artefact artefact, String artefactPath) {
        UrlSource urlSource = new UrlSource(url, artefact, artefactPath);
        return urlSource;
    }

    private ArtefactSource getFileSource(URL url, Artefact artefact) {
		String u = getString(url);
		String filename = u.substring(5);
		File file = new File(filename);
		File dir = file.getParentFile();
		return new FileSource(dir, artefact);
	}
	
	private ArtefactSource getJarSource(URL url, Artefact artefact) throws IOException {
		String u = getString(url);
		String filename = u.substring(9, u.indexOf('!'));
		File file = new File(filename);
		return new JarSource(file, artefact);
	}
	
	private String getString(URL url) {
		// Decode url-encoded characters - %20 = <space> etc...
        try {
        	return URLDecoder.decode(url.toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 encoding not supported!", e);
        }
	}
	
}
