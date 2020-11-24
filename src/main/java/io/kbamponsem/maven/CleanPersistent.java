package io.kbamponsem.maven;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Mojo(name = "clean-persistent", defaultPhase = LifecyclePhase.CLEAN)
public class CleanPersistent extends AbstractMojo {
    @Parameter(property = "project")
    private MavenProject project;


    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        File pDir = new File(project.getBasedir() + "/target/persistent");

        deleteDir(pDir);
        try {
            Files.deleteIfExists(pDir.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static public void deleteDir(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                deleteDir(file);
            }
        }
        dir.delete();
    }
}
