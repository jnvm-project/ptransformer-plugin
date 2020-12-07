package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.SimpleRemapper;

import java.util.Vector;

public class CopyClassVisitor extends ClassVisitor {
    private Vector<String> copyConstructors = new Vector<>();
    int count = 0;
    String owner;
    public CopyClassVisitor( ClassVisitor classVisitor, String owner) {
        super(Opcodes.ASM8, classVisitor);
        this.owner = owner;
    }

//    @Override
//    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
//        super.visit(version, access, name, signature, superName, interfaces);
//    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.compareTo("<init>") == 0) {
            name = "copy$"+ count;
            copyConstructors.add(name);
        }

        count += 1;
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public Vector<String> getCopyConstructors(){
        return copyConstructors;
    }
}
