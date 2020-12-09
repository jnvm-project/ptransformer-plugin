package io.kbamponsem.maven;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CopyMethodVisitor extends MethodVisitor {
    String owner;
    public CopyMethodVisitor(MethodVisitor methodVisitor, String owner) {
        super(Opcodes.ASM8, methodVisitor);
        this.owner = owner;
    }

    @Override
    public void visitCode() {
        mv.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        mv.visitInsn(opcode);
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        mv.visitVarInsn(opcode, var);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        mv.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        mv.visitEnd();
    }
}
