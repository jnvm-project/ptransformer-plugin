package io.kbamponsem.maven;
import io.kbamponsem.maven.util.ClassFileSorter;
import io.kbamponsem.maven.util.Functions;
import io.kbamponsem.maven.util.NVMAwarePersistentClasses;
import io.kbamponsem.maven.util.PersistentClasses;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import java.util.*;

@Mojo(name = "transform-classes", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME)
public class PTransformer extends AbstractMojo {

    @Parameter(property = "output", required = true)
    private String output;

    @Parameter(property = "project")
    private MavenProject project;

    @Parameter(property = "persistentAnnotation")
    private String persistentAnnotation;

    @Parameter(property = "pSuperName")
    private String pSuperName;

    @Parameter(property = "pInterface")
    private String pInterface;

    @Parameter(property = "copyClass")
    private String copyClass;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Vector<String> files;
        HashMap<Class, Boolean> persistentClasses;

        long startTime = System.currentTimeMillis();

        try {

            ClassLoader classLoader = Functions.getProjectClassLoader(project);

            files = ClassFileSorter.getAllClassFiles(output);

            persistentClasses = PersistentClasses.getPersistentClasses(files, classLoader, persistentAnnotation);

            NVMAwarePersistentClasses.getNVMAwarePersistentClasses(persistentClasses, output, pInterface, project, copyClass, persistentAnnotation);

        } catch (Exception e) {
            e.printStackTrace();
        }

        long stopTime = System.currentTimeMillis();

        System.out.println("Total Plugin exec time: " + (stopTime - startTime) +" ms");
    }






}
