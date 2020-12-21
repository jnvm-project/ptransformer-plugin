package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AddClassIdField extends ClassVisitor {
    ClassLoader classLoader;
    Class thisClass;
    public AddClassIdField( ClassVisitor classVisitor, ClassLoader classLoader, Class c) {
        super(Opcodes.ASM8, classVisitor);
        this.classLoader = classLoader;
        this.thisClass = c;
    }

    @Override
    public void visitEnd() {
        try {
            Class fakeOffHeap = this.classLoader.loadClass("eu.telecomsudparis.jnvm.offheap.OffHeap");
            Class fakeKlass = null;
            for(Class aClass: fakeOffHeap.getClasses()){
                if(aClass.getName().contains("Klass")){
                    fakeKlass = aClass;
                }
            }

            // after, we have the enum;
            Arrays.asList(fakeKlass.getMethods()).forEach(System.out::println);
            Method registerKlass = fakeKlass.getMethod("registerUserKlass", Class.class);
            long classId = (long) registerKlass.invoke(null, thisClass.getClass());
            System.out.println("ClassId " +classId);
            cv.visitField(Opcodes.ACC_FINAL | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "CLASS_ID", "J", null, classId);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        super.visitEnd();
    }
}
