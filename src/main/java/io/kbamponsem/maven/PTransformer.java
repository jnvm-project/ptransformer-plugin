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
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
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


    public void execute() throws MojoExecutionException, MojoFailureException {
        Vector<String> files = new Vector<>();
        HashMap<Class, Boolean> persistentClasses = new HashMap<>();
        try {
            File oDir = new File(output);
            getLog().info("Root: " + output);

            FileUtils.listFiles(oDir, TrueFileFilter.INSTANCE, TrueFileFilter.TRUE).forEach(x -> {
                files.add(transformClassFiles(x, oDir));
            });
//
//            List<URL> pathUrls = new ArrayList<>();
//            for (Object compilePath : project.getRuntimeClasspathElements()) {
//                String s = (String) compilePath;
//                pathUrls.add(new File(s).toURI().toURL());
//            }
//
//            URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);

            ClassLoader classLoader = Functions.getProjectClassLoader(project);

            files.forEach(x -> {
                try {

                    Class c = classLoader.loadClass(x);

                    if (isPersistence(c, persistentAnnotation)) {
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
                        String persistentClasspath = project.getBasedir() + "/target/persistent/";
                        byte[] bytes = transformClass(output + "/", aClass, pSuperName, project);

                        String filename = aClass.getSimpleName();


                        String packageName = aClass.getPackage().getName();
                        writeBytes(output+"/", packageName, filename, ".class", bytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            // Delete old classes folder.
//            CleanPersistent.deleteDir(new File(project.getBasedir().getAbsolutePath(), "/target/classes"));

            // Though cleaned, after building it replaces it since it is the classpath.
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String transformClassFiles(File file, File parentDirectory) {
        String fileName = file.getAbsolutePath();
        String directoryName = parentDirectory.getAbsolutePath();

        fileName = fileName.split(directoryName + "/")[1];

        return changeForwardSlashToDot(removeDotClass(fileName));
    }

    static String removeDotClass(String s) {
        return s.split(".class")[0];
    }

    static String changeFromDotToSlash(String s) {
        return s.replace("\\", "/");
    }

    static String changeForwardSlashToDot(String s) {
        return s.replace("/", ".");
    }

    static boolean isPersistence(Class c, String persistent) {
        for (Annotation annotation : c.getDeclaredAnnotations()) {
            if (annotation.annotationType().getCanonicalName().compareTo(persistent) == 0) {
                return true;
            } else continue;
        }
        return false;
    }

    static byte[] transformClass(String classpath, Class c, String pSuperName, MavenProject project) throws IOException {
        ClassLoader classLoader = Functions.getProjectClassLoader(project);
        long size = Functions.getSizeOfFields(c);
        String s = classpath + c.getName().replace(".", "/") + ".class";
        ClassWriter classWriter = new ClassWriter(0);
        ClassReader classReader = new ClassReader(Files.readAllBytes(Paths.get(s).toAbsolutePath()));
        AddSizeMethod persistentMethod = new AddSizeMethod(classWriter, c.getName());
        AddSizeField addSizeField = new AddSizeField(persistentMethod, size);
        AddSuperCall addSuperCall = new AddSuperCall(addSizeField, pSuperName);
        AddEqualMethod addEqualMethod = new AddEqualMethod(addSuperCall, c);
        TransformNonVolatileFields transformNonVolatileFields = new TransformNonVolatileFields(addEqualMethod, pSuperName, classLoader);
        classReader.accept(transformNonVolatileFields, 0);
        return classWriter.toByteArray();
    }

    static void writeBytes(String classpath, String packageName, String fileName, String extension, byte[] b) {
        FileOutputStream fileOutputStream = null;
        File fileWithDir;
        try {
            fileWithDir = new File(classpath.concat(packageName));

            Files.createDirectories(fileWithDir.toPath());

            fileOutputStream = new FileOutputStream(classpath + packageName + "/" + fileName + extension);
            fileOutputStream.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
