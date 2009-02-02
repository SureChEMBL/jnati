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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.sf.jnati.deploy.artefact.Artefact;

public class FileSource extends ArtefactSource {

	private File root;
//	private Artefact artefact;
	
	public FileSource(File root, Artefact artefact) {
		this.root = root;
//		this.artefact = artefact;
	}
	
	@Override
	public InputStream openFile(String path) throws IOException {
		File file = getFile(path);
		return new BufferedInputStream(new FileInputStream(file));
	}
	
	@Override
	public boolean containsFile(String path) throws IOException {
		File file = getFile(path);
		return file.isFile();
	}

	private File getFile(String path) {
		File file = new File(root, path);
		return file;
	}
	
	@Override
	public void close() throws IOException {
		;
	}

	@Override
	public boolean isLocal() {
		return true;
	}
	
	@Override
	public File getPath() {
		return root;
	}
	
}
