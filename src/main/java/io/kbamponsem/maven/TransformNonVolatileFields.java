package io.kbamponsem.maven;

import io.kbamponsem.maven.util.Functions;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

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
    final String[] getSetType = new String[] {"get", "set"};

    public TransformNonVolatileFields(ClassVisitor classVisitor, String pInterface, ClassLoader classLoader) {
        super(Opcodes.ASM8, classVisitor);
        this.pInterface = pInterface;
        this.classLoader = classLoader;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.version = version;
        this.access = access;
        this.name = name;
        this.signature = signature;
        this.superName = superName;
        this.interfaces = interfaces;

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (access - Opcodes.ACC_TRANSIENT < 0) {
            nonTransientFields.put(name, descriptor);
        }
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

        return mv;
    }

    /**
     * This is where finally all the effects are added
     */
    @Override
    public void visitEnd() {
        addSuperName(cv);
        nonTransientFields.forEach((name, descriptor) -> {
            createGetter(name, descriptor, cv, 8L);
            createSetter(name, descriptor, cv, 16L);
        });
        super.visitEnd();
    }

    /**
     * The required interface is added to the class c
     *
     * @param cv
     */
    void addInterface(ClassVisitor cv) {

        String[] _interfaces = Arrays.copyOf(this.interfaces, this.interfaces.length + 1);
        _interfaces[this.interfaces.length] = this.pInterface;

        cv.visit(this.version, this.access, this.name, this.superName, this.signature, _interfaces);
    }

    void addSuperName(ClassVisitor cv) {
        cv.visit(this.version, this.access, this.name, this.signature, this.pInterface.replace("/", "."), this.interfaces);
    }

    /**
     * Creates a setter for a non-transient field.
     * @param name
     * @param descriptor
     * @param cv
     * @param offset
     */
    void createSetter(String name, String descriptor, ClassVisitor cv, long offset) {
        try {
            Class superClass = this.classLoader.loadClass(this.pInterface);
            Method[] superClassMethods = superClass.getDeclaredMethods();
            String methodName = Functions.getMethodFromName(superClassMethods, Functions.getTypeFromDesc(descriptor), getSetType[1]).getName();
            name = Functions.capitalize(name);
            MethodVisitor mv =
                    cv.visitMethod(Opcodes.ACC_PUBLIC, "$set" + name, "(" + descriptor + ")V", null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitLdcInsn(offset);
            mv.visitVarInsn(Functions.getDescOpcode(descriptor), 1);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, pInterface.replace("/", "."), methodName, "(J" + descriptor + ")V", true);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        } catch (Exception e) {

        }
    }

    /**
     * Creates a getter for a non-transient field.
     * @param name
     * @param descriptor
     * @param cv
     * @param offset
     */
    void createGetter(String name, String descriptor, ClassVisitor cv, long offset) {
        try{

            Class superClass = this.classLoader.loadClass(this.pInterface);
            Method[] superClassMethods = superClass.getDeclaredMethods();
            String methodName = Functions.getMethodFromName(superClassMethods, Functions.getTypeFromDesc(descriptor), getSetType[0]).getName();
            name = Functions.capitalize(name);
            MethodVisitor mv =
                    cv.visitMethod(Opcodes.ACC_PUBLIC, "$get" + name, "()" + descriptor, null, null);
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitLdcInsn(offset);
            mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, pInterface.replace("/", "."), methodName, "(J)"+descriptor, true);
            mv.visitInsn(Opcodes.IRETURN);
            mv.visitMaxs(5, 5);
            mv.visitEnd();

        }catch (Exception e){

        }
    }
}
