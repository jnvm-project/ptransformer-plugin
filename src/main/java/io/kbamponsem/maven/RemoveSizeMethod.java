package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class RemoveSizeMethod extends ClassVisitor {


    public RemoveSizeMethod(ClassVisitor cv) {
        super(Opcodes.ASM8, cv);
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.equals("size")) {
            return null;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }


}
