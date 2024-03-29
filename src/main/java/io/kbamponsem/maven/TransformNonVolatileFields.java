package io.kbamponsem.maven;

import io.kbamponsem.maven.util.Functions;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class looks for the non-transient fields and then creates getters and setters
 * for them.
 */
public class TransformNonVolatileFields extends ClassVisitor {

    ClassLoader classLoader;
    HashMap<String, String> nonTransientFields = new HashMap<>();
    String pInterface;
    int version;
    int access;
    String name;
    String signature;
    String superName;
    String descriptor;
    String[] interfaces, exceptions;
    int current = 0;
    final String[] getSetType = new String[]{"get", "set"};
    String className;
    Class clazz;
    Vector<String> copyConst;
    String persistentAnnotation;

    public TransformNonVolatileFields(ClassVisitor classVisitor,
                                      String pInterface,
                                      ClassLoader classLoader,
                                      Class c,
                                      Vector<String> copyConst,
                                      String persistentAnnotation) {
        super(Opcodes.ASM8, classVisitor);
        this.pInterface = pInterface;
        this.classLoader = classLoader;
        this.className = c.getName();
        this.clazz = c;
        this.copyConst = copyConst;
        this.persistentAnnotation = persistentAnnotation;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;

        String[] _interfaces = Arrays.copyOf(this.interfaces, this.interfaces.length + 1);
        _interfaces[this.interfaces.length] = this.pInterface.replace(".", "/");
        super.visit(version, access, name, signature, superName, _interfaces);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.out.println(descriptor);
        System.out.println(this.persistentAnnotation);
        this.persistentAnnotation = "L"+this.persistentAnnotation.replace(".", "/")+";";
        if(descriptor.equals(this.persistentAnnotation))
            return null;
        return super.visitAnnotation(descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (access - Opcodes.ACC_TRANSIENT < 0) {
            nonTransientFields.put(name, descriptor);
            return null;
        } else
            return super.visitField(access, name, descriptor, signature, value);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv;
        Vector<String> nonTransients = new Vector<>();

        this.nonTransientFields.forEach((x, y) -> {
            nonTransients.add(x);
        });

        mv = cv.visitMethod(access, name, descriptor, signature, exceptions);

        if (mv != null) {
            mv = new FieldAccessMethodTransformer(mv, nonTransients);
        }
        if (name.compareTo("<init>") == 0 && !descriptor.contains("(Leu/telecomsudparis/jnvm/offheap/MemoryBlockHandle;)")) {
            if (copyConst.contains("$copy0")) {
                mv = new CallCopyConstructor(mv, className, "$copy0", "()V");
            }
        }
        return mv;
    }

    /**
     * This is where finally all the effects are added
     */
    @Override
    public void visitEnd() {
        nonTransientFields.forEach((name, descriptor) -> {
            this.current = Functions.getFieldOffset(this.current, descriptor);
            createGetter(name, descriptor, cv, this.current);
            createSetter(name, descriptor, cv, this.current);
        });
        super.visitEnd();
    }

    /**
     * Creates a setter for a non-transient field.
     *
     * @param name
     * @param descriptor
     * @param cv
     * @param offset
     */
    void createSetter(String name, String descriptor, ClassVisitor cv, long offset) {
        try {
            Class pInterfaceClass = this.classLoader.loadClass(this.pInterface);

            Method[] superClassMethods = pInterfaceClass.getDeclaredMethods();

            String methodName = Functions.getMethodFromName(superClassMethods, Functions.getTypeFromDesc(descriptor), getSetType[1]).getName();

            System.out.println(methodName);
            if (!methodName.equals("")) {
                name = Functions.capitalize(name);
                MethodVisitor mv =
                        cv.visitMethod(Opcodes.ACC_PUBLIC, "$set" + name, "(" + descriptor + ")V", null, null);
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(offset);
                mv.visitVarInsn(Functions.getDescOpcode(descriptor), 1);
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, pInterface.replace(".", "/"), methodName, "(J" + descriptor + ")V", true);
                mv.visitInsn(Opcodes.RETURN);
                mv.visitMaxs(5, 5);
                mv.visitEnd();
            }
        } catch (Exception e) {

        }
    }

    /**
     * Creates a getter for a non-transient field.
     *
     * @param name
     * @param descriptor
     * @param cv
     * @param offset
     */
    void createGetter(String name, String descriptor, ClassVisitor cv, long offset) {
        try {
            Class pInterfaceClass = this.classLoader.loadClass(this.pInterface); // load interface class

            Method[] pInterfaceClassDeclaredMethods = pInterfaceClass.getDeclaredMethods(); // get all methods with the interface class

            String methodName = Functions.getMethodFromName(pInterfaceClassDeclaredMethods, Functions.getTypeFromDesc(descriptor), getSetType[0]).getName();

            name = Functions.capitalize(name);
            if (!methodName.equals("")) {
                MethodVisitor mv =
                        cv.visitMethod(Opcodes.ACC_PUBLIC, "$get" + name, "()" + descriptor, null, null);
                mv.visitCode();
                mv.visitVarInsn(Opcodes.ALOAD, 0);
                mv.visitLdcInsn(offset);
                mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, pInterface.replace(".", "/"), methodName, "(J)" + descriptor, true);
                mv.visitInsn(Functions.getOpcodeReturnFromDesc(descriptor));
                mv.visitMaxs(5, 5);
                mv.visitEnd();
            }


        } catch (Exception e) {

        }
    }

    public HashMap<String, String> getNonTransientFields() {
        return this.nonTransientFields;
    }

}
