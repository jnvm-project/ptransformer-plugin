package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Vector;

/**
 * This class looks for the non-transient fields and then it creates getters and setters
 * for them.
 */
public class TransformNonVolatileFields extends ClassVisitor {

    HashMap<String, String> nonTransientFields = new HashMap<>();
    String pInterface;
    int version;
    int access;
    String name;
    String signature;
    String superName;
    String descriptor;
    String[] interfaces, exceptions;

    public TransformNonVolatileFields(ClassVisitor classVisitor, String pInterface) {
        super(Opcodes.ASM8, classVisitor);
        this.pInterface = pInterface;
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

        this.nonTransientFields.forEach((x, y)->{
            nonTransients.add(x);
        });
        mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if(mv != null & !name.contains("<init>")){
            mv = new FieldAccessMethodTransformer(mv, nonTransients);
        }
        return mv;
    }

    /**
     * This is where finally all the effects are added
     */
    @Override
    public void visitEnd() {
//        addInterface(cv);
        addSuperName(cv);
        nonTransientFields.forEach((name, descriptor) -> {
            createGetter(name, descriptor, cv, 8L);
            createSetter(name, descriptor, cv, 16L);
        });
        super.visitEnd();
    }

    /**
     * The required interface is added to the class c
     * @param cv
     */
    void addInterface(ClassVisitor cv) {

        String[] _interfaces = Arrays.copyOf(this.interfaces, this.interfaces.length + 1);
        _interfaces[this.interfaces.length] = this.pInterface;

        cv.visit(this.version, this.access, this.name, this.superName, this.signature, _interfaces);
    }

    void addSuperName(ClassVisitor cv) {
        cv.visit(this.version, this.access, this.name, this.signature,this.pInterface.replace("/", "."), this.interfaces);
    }

    void createSetter(String name, String descriptor, ClassVisitor cv, long offset) {
        MethodVisitor mv =
                cv.visitMethod(Opcodes.ACC_PUBLIC, "$set" + name.toUpperCase(), "(" + descriptor + ")V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(offset);
        mv.visitVarInsn(Opcodes.ILOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, pInterface.replace("/", "."), "setIntFieldAt", "(J"+descriptor+ ")V", true);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(5, 5);
        mv.visitEnd();

    }

    void createGetter(String name, String descriptor, ClassVisitor cv, long offset) {
        MethodVisitor mv =
                cv.visitMethod(Opcodes.ACC_PUBLIC, "$get" + name.toUpperCase(), "()" + descriptor, null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitLdcInsn(offset);
        mv.visitMethodInsn(Opcodes.INVOKEINTERFACE, pInterface.replace("/", "."), "getIntFieldAt", "(J)I", true);
        mv.visitInsn(Opcodes.IRETURN);
        mv.visitMaxs(5, 5);
        mv.visitEnd();
    }
}
