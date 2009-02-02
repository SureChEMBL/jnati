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

import org.apache.log4j.Logger;

public class ClasspathRepository extends ArtefactRepository {

	private static final Logger LOG = Logger.getLogger(ClasspathRepository.class);
	
	private static final String FS = "/";
	private static final String METAINF = "META-INF";
	private static final String MANIFEST = "MANIFEST.xml";
	
	private String getPath(Artefact artefact) {
		String path = METAINF + FS
					+ artefact.getId() + FS
					+ artefact.getVersion() + FS
					+ artefact.getOsArch() + FS
					+ MANIFEST;
		return path;
	}
	
	public List<? extends ArtefactSource> getArtefactSource(Artefact artefact) throws IOException {
		LOG.info("Searching classpath for: " + artefact);
		String name = getPath(artefact);
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
				LOG.warn("Unsupported protocol: " + protocol);
			}
		}
		
		return list;
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
