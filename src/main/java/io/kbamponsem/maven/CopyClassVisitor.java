package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class CopyClassVisitor extends ClassVisitor {
    public CopyClassVisitor( ClassVisitor classVisitor) {
        super(Opcodes.ASM8, classVisitor);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
