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
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

        super.visitMethodInsn(Opcodes.INVOKESPECIAL, this.owner.replace(".", "/"), "copy$0", "()V", false);
    }
}
