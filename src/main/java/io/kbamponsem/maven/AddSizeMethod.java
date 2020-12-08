package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddSizeMethod extends ClassVisitor {
    String SIZE_NAME;
    String className;
    boolean sizeExists = false;
    public AddSizeMethod(ClassVisitor classVisitor, String className) {
        super(Opcodes.ASM8, classVisitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {

        if(name == "SIZE"){
            SIZE_NAME = name;
        }
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public void visitEnd() {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PUBLIC, "$size" , "()J", null, null);
        if (methodVisitor != null) {
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitFieldInsn(Opcodes.GETFIELD, className.replace(".", "/"), "SIZE", "J");
            methodVisitor.visitInsn(Opcodes.LRETURN);
            methodVisitor.visitMaxs(3, 3);
            methodVisitor.visitEnd();
        }
        super.visitEnd();
    }
}
