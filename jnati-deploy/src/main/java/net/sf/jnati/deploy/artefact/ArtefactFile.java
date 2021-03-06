/*
 * Copyright 2008-2011 Sam Adams <sea36 at users.sourceforge.net>
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
package net.sf.jnati.deploy.artefact;

/**
 * @author Sam Adams
 */
public class ArtefactFile {

	private String path;
	private boolean exe;
	private boolean library;
	
	public ArtefactFile(String path) {
		this.path = path;
	}
	
	public String getPath() {
		return path;
	}

	public boolean isExe() {
		return exe;
	}
	
	public boolean isLibrary() {
		return library;
	}
	
	public void setExe(boolean exe) {
		this.exe = exe;
	}
	
	public void setLibrary(boolean library) {
		this.library = library;
	}
	
}
