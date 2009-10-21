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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import net.sf.jnati.FileUtils;
import net.sf.jnati.deploy.artefact.Artefact;
import net.sf.jnati.deploy.source.ArtefactSource;
import net.sf.jnati.deploy.source.TempJarSource;

public class RemoteRepository extends ArtefactRepository {
	
	private static final Logger LOG = Logger.getLogger(RemoteRepository.class);
	
	private static final String FS = "/";
	private static final String S = "-";
	private static final String EXT = ".jar";
	

	private final URL root;
	
	public RemoteRepository(URL root) {
		this.root = root;
	}
	
	private URL getUrl(Artefact artefact) throws MalformedURLException {
		String path = artefact.getId() + FS
			+ artefact.getVersion() + FS
			+ artefact.getId() + S 
			+ artefact.getVersion() + S
			+ artefact.getOsArch() + EXT;
		return new URL(root.toString() + FS + path);
	}
	
	public List<? extends ArtefactSource> getArtefactSource(Artefact artefact) throws IOException {
		
		LOG.info("Searching remote repository for: " + artefact + " (" + root + ")");
		
		URL url = getUrl(artefact);
		File tempFile = File.createTempFile("jnati", ".jar");
		InputStream in;
		try {
			in = url.openStream();
		} catch (FileNotFoundException e) {
			LOG.debug("Not found: " + url);
			return Collections.emptyList();
		}
		LOG.debug("Downloading artefact: " + url);
		FileUtils.writeStreamToFile(in, tempFile);
		LOG.debug("Download complete: " + tempFile);
		return Collections.singletonList(new TempJarSource(tempFile, artefact));
	
	}
	
}
