package io.kbamponsem.maven;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CallCopyConstructor extends MethodVisitor {
    String className;

    public CallCopyConstructor(MethodVisitor methodVisitor, String className) {
        super(Opcodes.ASM8, methodVisitor);
        this.className = className;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (opcode == Opcodes.INVOKESPECIAL) {
            System.out.println("Invoke Special: " + owner);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, descriptor, isInterface);
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className.replace(".", "/"), "$copy0", "()V", false);
            mv.visitMaxs(4,4);
        } else
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }
}
