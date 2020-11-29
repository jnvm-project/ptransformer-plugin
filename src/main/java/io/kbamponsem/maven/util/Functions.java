package io.kbamponsem.maven.util;

import org.apache.maven.project.MavenProject;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public class Functions {
    static public String capitalize(String s) {
        String first = s.substring(0, 1);
        String remaining = s.substring(1);
        first = first.toUpperCase();

        return first.concat(remaining);

    }

    static public int getDescOpcode(String desc) {
        switch (desc) {
            case "I":
            case "C":
            case "Z":
            case "S":
            case "B":
                return Opcodes.ILOAD;
            case "J":
                return Opcodes.LLOAD;
            case "F":
                return Opcodes.FLOAD;
            case "D":
                return Opcodes.DLOAD;
            default:
                return 0;
        }
    }

    static public URLClassLoader getProjectClassLoader(MavenProject project) {
        List<URL> pathUrls = new ArrayList<>();
        try {
            for (Object compilePath : project.getRuntimeClasspathElements()) {
                String s = (String) compilePath;
                pathUrls.add(new File(s).toURI().toURL());
            }

            URL[] urlsForClassLoader = pathUrls.toArray(new URL[pathUrls.size()]);

            return new URLClassLoader(urlsForClassLoader);

        } catch (Exception e) {

        }
        return null;
    }

    static public Method getMethodFromName(Method[] methods, String name, String type) {
        for (Method m : methods) {
            if (m.getName().contains(type+capitalize(name))) {
                return m;
            }
        }
        return null;
    }

    static public String getTypeFromDesc(String desc){
        switch (desc){
            case "I":
                return "int";
            case "B":
                return "byte";
            case "Z":
                return "boolean";
            case "C":
                return "char";
            case "S":
                return "short";
            case "F":
                return "float";
            case "J":
                return "long";
            case "D":
                return "double";
        }
        return "";
    }
}
