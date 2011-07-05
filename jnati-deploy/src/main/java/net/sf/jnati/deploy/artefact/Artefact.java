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
package net.sf.jnati.deploy.artefact;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.jnati.ArtefactDescriptor;
import net.sf.jnati.config.Configuration;

/**
 * @author Sam Adams
 */
public class Artefact {

	private String id;
	private String version;

	private File path;
	
	private List<ArtefactFile> fileList;
	private List<URL> repositoryList;
	
	private Configuration config;
	
	public Artefact(String id, String version) {
		super();
		
		if (id == null) {
    		throw new NullPointerException("Null ID");
    	}
    	if (version == null) {
    		throw new NullPointerException("Null version");
    	}
    	
		this.id = id;
		this.version = version;
	}
	
	public String getId() {
		return id;
	}
	
	public String getVersion() {
		return version;
	}
	
	public File getPath() {
		return path;
	}
	
	public void setPath(File path) {
		this.path = path;
	}

	public List<ArtefactFile> getFileList() {
		return fileList == null ? null : new ArrayList<ArtefactFile>(fileList);
	}
	
	public void setFileList(List<ArtefactFile> fileList) {
		this.fileList = fileList == null ? null : new ArrayList<ArtefactFile>(fileList);
	}
	
	public List<URL> getRepositoryList() {
		return repositoryList == null ? null : new ArrayList<URL>(repositoryList);
	}
	
	public void setRepositoryList(List<URL> repositoryList) {
		this.repositoryList = repositoryList == null ? null : new ArrayList<URL>(repositoryList);
	}
	
	@Override
	public String toString() {
		return id + "-" + version + "-" + getOsArch();
	}
	
	public Configuration getConfiguration() {
		return config;
	}
	
	public void setConfiguration(Configuration config) {
		this.config = config;
	}
	
	public String getProperty(String key) {
		return config.getProperty(key);
	}
	
	
	public String getOsArch() {
		return getProperty("${jnati.artefactId}.${jnati.artefactVersion}.osarch");
	}
	
	public File getLocalRepository() {
		String s = getProperty("jnati.localRepository");
		return s == null ? null : new File(s);
	}	
	
	public boolean getAutoDeploy() {
		// autoDeploy = true
		String s = getProperty("jnati.autoDeploy");
		return Boolean.parseBoolean(s);
	}

    public boolean isMultideployEnabled() {
        String s = getProperty("${jnati.artefactId}.enableMultideploy");
        return Boolean.parseBoolean(s);
    }

    public int getMaxMultideployCount() {
        String s = getProperty("${jnati.artefactId}.maxMultideployCount");
        return Integer.parseInt(s);
    }
	
	public boolean getAllowLocal() {
		String s = getProperty("jnati.allowDirectLoad");
		return Boolean.parseBoolean(s);
	}
	
	public boolean getAllowDownload() {
		String s = getProperty("jnati.allowDownload");
		return Boolean.parseBoolean(s);
	}
	
	public List<String> getRepositoryUrls() {
		String s = getProperty("${jnati.artefactId}.${jnati.artefactVersion}.repositoryUrls");
		List<String> list = new ArrayList<String>();
		for (String u : s.split(";")) {
			u = u.trim();
			if (u.length() > 0) {
				list.add(u);
			}
		}
		return list;
	}
	
	public ArtefactDescriptor getArtefactLocation() {
		return new ArtefactDescriptor(id, version, getOsArch(), path);
	}

}
