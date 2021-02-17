package io.kbamponsem.maven.util;

import io.kbamponsem.maven.*;
import io.kbamponsem.maven.unsedVisitors.AddSizeField;
import org.apache.maven.project.MavenProject;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
            if (m.getName().contains(type + capitalize(name))) {
                return m;
            }
        }

        for(Method m : methods) {
            if (m.getName().contains("Long")) return m;
        }

        return null;
    }

    static public String getTypeFromDesc(String desc) {
        switch (desc) {
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

    static public long getSizeOfFields(Class c) {
        int size = 0;
        for (Field f : c.getDeclaredFields()) {
            if (f.getType().getSimpleName().compareTo("int") == 0) {
                size += Integer.BYTES;
            } else if (f.getType().getSimpleName() == "short") {
                size += Short.BYTES;
            } else if (f.getType().getSimpleName() == "char") {
                size += Character.BYTES;
            } else if (f.getType().getSimpleName() == "float") {
                size += Float.BYTES;
            } else if (f.getType().getSimpleName() == "double") {
                size += Double.BYTES;
            } else if (f.getType().getSimpleName() == "long") {
                size += Long.BYTES;
            } else if (f.getType().getSimpleName() == "byte") {
                size += Byte.BYTES;
            } else {
                size += 0;
            }
        }

        return size;
    }

    static public int getFieldOffset(int current, String desc) {
        switch (getTypeFromDesc(desc)) {
            case "int":
                current += Integer.BYTES;
                return current;
            case "double":
                current += Double.BYTES;
                return current;
            case "char":
                current += Character.BYTES;
            case "float":
                current += Float.BYTES;
                return current;
            case "long":
                current += Long.BYTES;
                return current;
            case "short":
                current += Short.BYTES;
                return current;
            case "byte":
                current += Byte.BYTES;
                return current;
        }
        return 0;
    }

    static public long getClassID(ClassLoader classLoader, Class userClass) {
        try {
            Class fakeOffHeap = classLoader.loadClass("eu.telecomsudparis.jnvm.offheap.OffHeap");
            Class fakeKlass = null;
            for (Class aClass : fakeOffHeap.getClasses()) {
                if (aClass.getName().contains("Klass")) {
                    fakeKlass = aClass;
                }
            }

            // after, we have the enum;
            Method registerKlass = fakeKlass.getMethod("registerUserKlass", Class.class);
            long classId = (long) registerKlass.invoke(null, userClass.getClass());
            return classId;
        } catch (Exception e) {

        } finally {
            return -1;
        }
    }

    static public int getOpcodeReturnFromDesc(String desc) {
        switch (desc) {
            case "J":
                return Opcodes.LRETURN;
            case "D":
                return Opcodes.DRETURN;
            case "I":
            case "B":
                return Opcodes.IRETURN;
            case "F":
                return Opcodes.FRETURN;
        }
        return -1;
    }

    static public byte[] transformClass(String classpath, Class c, String pInterface, MavenProject project, String copyClassName, String persistentAnnotation) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Functions.getProjectClassLoader(project);
        long size = Functions.getSizeOfFields(c);

        String s = classpath + c.getName().replace(".", "/") + ".class";
        String s1 = copyClassName.replace(".", "/") + ".class";
        ClassReader classReader1 = new ClassReader(Files.readAllBytes(Paths.get(s).toAbsolutePath()));
        assert classLoader != null;
        ClassReader classReader2 = new ClassReader(Objects.requireNonNull(classLoader.getResourceAsStream(s1)));

        ClassWriter classWriter = new ClassWriter(0);

        AddSizeField addSizeField = new AddSizeField(classWriter, size, c.getName().replace(".","/"));
        CopyClassVisitor copyClassVisitor = new CopyClassVisitor(classWriter, copyClassName, c.getName(), classLoader, c);
        AddClassIdField addClassIdField = new AddClassIdField(addSizeField, classLoader, c);
        TransformNonVolatileFields transformNonVolatileFields = new TransformNonVolatileFields(
                addClassIdField, pInterface, classLoader, c, copyClassVisitor.getCopyConstructors(), persistentAnnotation);
        AddResurrector resurrector = new AddResurrector(transformNonVolatileFields, c.getName(), classLoader);
        RemoveSizeMethod removeSizeMethod = new RemoveSizeMethod(copyClassVisitor);
        classReader2.accept(removeSizeMethod, 0);
        classReader1.accept(resurrector, 0);
        return classWriter.toByteArray();
    }

    static public String transformClassFiles(File file, File parentDirectory) {
        String fileName = file.getAbsolutePath();
        String directoryName = parentDirectory.getAbsolutePath();

        fileName = fileName.split(directoryName + "/")[1];

        return changeForwardSlashToDot(removeDotClass(fileName));
    }

    static public void writeBytes(String classpath, String packageName, String fileName, String extension, byte[] b) {
        FileOutputStream fileOutputStream = null;
        File fileWithDir;
        try {
            fileWithDir = new File(classpath.concat(packageName.replace(".", "/")));

            Files.createDirectories(fileWithDir.toPath());

            fileOutputStream = new FileOutputStream(classpath + packageName.replace(".","/") + "/" + fileName + extension);
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

    static public boolean isPersistence(Class c, String persistent) {
        for (Annotation annotation : c.getDeclaredAnnotations()) {
            if (annotation.annotationType().getCanonicalName().compareTo(persistent) == 0) {
                return true;
            } else continue;
        }
        return false;
    }

    static String changeForwardSlashToDot(String s) {
        return s.replace("/", ".");
    }

    static String removeDotClass(String s) {
        return s.split(".class")[0];
    }

//    static String changeFromDotToSlash(String s) {
//        return s.replace("\\", "/");
//    }


}
