package io.kbamponsem.maven;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CopyMethodVisitor extends MethodVisitor {
    public CopyMethodVisitor(MethodVisitor methodVisitor) {
        super(Opcodes.ASM8, methodVisitor);
    }

    @Override
    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
    }
}
