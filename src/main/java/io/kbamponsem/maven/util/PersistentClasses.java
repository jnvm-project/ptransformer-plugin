package io.kbamponsem.maven.util;

import java.util.HashMap;
import java.util.Vector;

public class PersistentClasses {
    static public HashMap<Class, Boolean> getPersistentClasses(Vector<String> classFiles, ClassLoader classLoader, String persistentAnnotation) {
        HashMap<Class, Boolean> persistentClasses = new HashMap<>();
        classFiles.forEach(x -> {
            try {
                Class c = classLoader.loadClass(x);

                if (Functions.isPersistence(c, persistentAnnotation)) {
                    persistentClasses.put(c, true);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        });
        return persistentClasses;
    }
}
