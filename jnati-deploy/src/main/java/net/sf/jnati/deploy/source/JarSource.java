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
package net.sf.jnati.deploy.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import net.sf.jnati.deploy.artefact.Artefact;

import org.apache.log4j.Logger;

public class JarSource extends ArtefactSource {
	
	private static final Logger LOG = Logger.getLogger(JarSource.class);
	
	private static final String FS = "/";
	private static final String METAINF = "META-INF";

	private final Artefact artefact;
	private JarFile jar;
	private final String basePath;
	private File file;
	
	public JarSource(JarFile jar, Artefact artefact) {
		this.jar = jar;
		this.artefact = artefact;
		this.basePath = getBasePath();
	}
	
	public JarSource(File file, Artefact artefact) throws IOException {
		this.file = file;
		this.artefact = artefact;
		this.basePath = getBasePath();
	}

	private String getBasePath() {
		return METAINF + FS
			 + artefact.getId() + FS
			 + artefact.getVersion() + FS
			 + artefact.getOsArch() + FS;
	}
	
	@Override
	public InputStream openFile(String path) throws IOException {
		JarEntry entry = getEntry(path);
		if (entry == null) {
			throw new FileNotFoundException("File not found: " + path);
		}
		return jar.getInputStream(entry);
	}
	
	private JarEntry getEntry(String path) throws IOException {
		if (jar == null) {
			LOG.debug("Opening jar: " + file);
			jar = new JarFile(file);
		}
		String name = basePath + path;
		JarEntry entry = jar.getJarEntry(name);
		return entry;
	}
	
	@Override
	public boolean containsFile(String path) throws IOException {
		JarEntry entry = getEntry(path);
		return entry != null &! entry.isDirectory();
	}
	
	@Override
	public void close() throws IOException {
		if (jar != null) {
			jar.close();
			jar = null;
		}
	}
	
	@Override
	public boolean isLocal() {
		return false;
	}

	@Override
	public File getPath() {
		return null;
	}
	
}
