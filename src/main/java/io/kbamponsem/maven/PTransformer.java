package io.kbamponsem.maven;

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
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Mojo(name = "transform-classes", defaultPhase = LifecyclePhase.COMPILE, threadSafe = true, requiresDependencyResolution = ResolutionScope.COMPILE)
public class PTransformer extends AbstractMojo {

    @Parameter(property = "output", required = true)
    private String output;

    @Parameter(property = "project")
    private MavenProject project;

    @Parameter(property = "persistent")
    private String persistent;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Vector<String> files = new Vector<>();
        HashMap<Class, Boolean> persistentClasses = new HashMap<>();
        try {
            File oDir = new File(output);
            getLog().info("Root: " + output);
            Arrays.stream(oDir.listFiles()).forEach(file -> {
                String s2 = changeBackSlashToForwardSlash(oDir.getAbsolutePath() + "/");
                if (file.isDirectory()) {
                    Arrays.stream(file.listFiles()).forEach(file1 -> {
                        String s1 = changeBackSlashToForwardSlash(file1.getAbsolutePath());
                        s1 = removeDotClass(s1.split(s2)[1]);
                        files.add(changeForwardSlashToDot(s1));
                    });
                } else {
                    String s1 = changeBackSlashToForwardSlash(file.getAbsolutePath());
                    s1 = removeDotClass(s1.split(s2)[1]);
                    files.add(changeForwardSlashToDot(s1));
                }
            });

            getLog().info(files.toString());

            List<URL> pathUrls = new ArrayList<>();
            for (Object compilePath : project.getCompileClasspathElements()) {
                String s = (String) compilePath;
                pathUrls.add(new File(s).toURI().toURL());
            }

            URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);

            getLog().info("urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

            ClassLoader classLoader = new URLClassLoader(urlsForClassLoader);

            files.forEach(x -> {
                try {
                    Class c = classLoader.loadClass(x);

                    if (isPersistence(c, persistent)) {
                        persistentClasses.put(c, true);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });

            getLog().info("BaseDir: "+ project.getBasedir().getAbsolutePath());

            Path pDir = Paths.get(project.getBasedir().getAbsolutePath() + "\\persistent");
            Files.createDirectories(pDir);
            persistentClasses.forEach((aClass, aBoolean) -> {
                if (aBoolean == true){
                    try {
                        byte[] bytes = transformClass(output+"\\", aClass);
                        writeToPFolder(bytes, project.getBasedir()+"\\" +"persistent\\"+ aClass.getSimpleName().replace(".","\\")+".class");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static String removeDotClass(String s) {
        return s.split(".class")[0];
    }

    static String changeBackSlashToForwardSlash(String s) {
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

    static byte[] transformClass(String classpath, Class c) throws IOException {
        String s = classpath+ c.getName().replace(".","\\")+".class";
        System.out.println(classpath+ c.getName().replace(".","\\")+".class");
        ClassWriter classWriter = new ClassWriter(0);
        ClassReader classReader = new ClassReader(Files.readAllBytes(Paths.get(s).toAbsolutePath()));
        AddPersistentMethod persistentMethod = new AddPersistentMethod(classWriter);
        classReader.accept(persistentMethod, 0);
        return classWriter.toByteArray();
    }

    static void writeToPFolder(byte[] b, String outputDirectory) {
        System.out.println("POut: "+outputDirectory);
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream(outputDirectory);
            fileOutputStream.write(b);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
