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
 * @author sea36
 * @goal detect
 */
public class JnatiMavenPlugin extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     */
    private MavenProject project;

//    /**
//     * @parameter expression="${session}"
//     */
//    private MavenSession mavenSession;


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
