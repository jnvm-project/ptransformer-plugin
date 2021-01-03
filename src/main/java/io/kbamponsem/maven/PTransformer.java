package io.kbamponsem.maven;
import io.kbamponsem.maven.util.Functions;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.*;
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
        Vector<String> files = new Vector<>();
        HashMap<Class, Boolean> persistentClasses = new HashMap<>();
        try {
            File oDir = new File(output);
            getLog().info("Root: " + output);

            FileUtils.listFiles(oDir, TrueFileFilter.INSTANCE, TrueFileFilter.TRUE).forEach(x -> {
                if (x.toString().contains(".class"))
                    files.add(Functions.transformClassFiles(x, oDir));
            });

            ClassLoader classLoader = Functions.getProjectClassLoader(project);

            files.forEach(x -> {
                try {
                    Class c = classLoader.loadClass(x);

                    if (Functions.isPersistence(c, persistentAnnotation)) {
                        persistentClasses.put(c, true);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });


            getLog().info("-------------------------------------------------------------");
            getLog().info("Persistent classes: " + persistentClasses.size());
            getLog().info("-------------------------------------------------------------");

            persistentClasses.forEach((aClass, aBoolean) -> {
                if (aBoolean == true) {
                    try {
                        byte[] bytes = Functions.transformClass(output + "/", aClass, pInterface, project, copyClass);

                        String filename = aClass.getSimpleName();


                        String packageName = aClass.getPackage().getName();
                        Functions.writeBytes(output + "/", packageName, filename, ".class", bytes);
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }






}
