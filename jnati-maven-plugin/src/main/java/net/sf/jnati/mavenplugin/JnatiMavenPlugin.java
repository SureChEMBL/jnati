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
package net.sf.jnati.mavenplugin;

import net.sf.jnati.SystemType;
import net.sf.jnati.SystemTyper;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

/**
 * @author Sam Adams
 * @goal detect
 */
public class JnatiMavenPlugin extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

    public void execute() throws MojoExecutionException, MojoFailureException {

        // Detect current system
        try {
            super.getLog().info("JNATI - detecting platform");
            SystemType type = SystemTyper.getDefaultInstance().detectPlatform();
            super.getLog().info("JNATI - platform: "+type.getName());

            Properties properties = project.getProperties();
            properties.setProperty("jnati.platform", type.getName());

        } catch (IOException e) {
            throw new MojoExecutionException("Platform detection failed", e);
        }

    }

}
