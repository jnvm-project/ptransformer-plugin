package io.kbamponsem.maven;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CopyClassVisitor extends ClassVisitor {
    int count = 0;
    public CopyClassVisitor(ClassVisitor classVisitor) {
        super(Opcodes.ASM8, classVisitor);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.compareTo("<init>") == 0) {
            name = "$copy" + count;
        }
        count += 1;
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

}
