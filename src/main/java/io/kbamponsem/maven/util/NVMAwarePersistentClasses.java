package io.kbamponsem.maven.util;

import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.HashMap;

public class NVMAwarePersistentClasses {
    static public void getNVMAwarePersistentClasses(HashMap<Class, Boolean> persistentClasses, String basedir, String pInterface, MavenProject project, String copyClass, String persistentAnnotation){
        persistentClasses.forEach((aClass, aBoolean) -> {
            if (aBoolean == true) {
                try {
                    byte[] bytes = Functions.transformClass(basedir + "/", aClass, pInterface, project, copyClass, persistentAnnotation);

                    String filename = aClass.getSimpleName();


                    String packageName = aClass.getPackage().getName();
                    Functions.writeBytes(basedir + "/", packageName, filename, ".class", bytes);
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
