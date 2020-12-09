package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;

import java.util.Vector;

public class CopyClassVisitor extends ClassVisitor {
    int count = 0;
    String originalOwner;
    String newOwner;

    public CopyClassVisitor(ClassVisitor classVisitor, String originalOwner, String newOwner) {
        super(Opcodes.ASM8, classVisitor);
        this.originalOwner = originalOwner;
        this.newOwner = newOwner;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.compareTo("<init>") == 0) {
            name = "$copy" + count;
            count += 1;
        }
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        Vector<FieldNode> fieldNodes = new Vector<>();
        return new MethodVisitor(super.api, mv) {
            @Override
            public void visitCode() {
                super.visitCode();

            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                System.out.println(owner);
                if (owner.compareTo(originalOwner.replace(".", "/")) == 0) {
                    owner = newOwner.replace("." , "/");
                }
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (opcode == Opcodes.INVOKESPECIAL && name.equals("java/lang/Object")) {
                    opcode = Opcodes.NOP;
                }
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }

            @Override
            public void visitEnd() {

                super.visitEnd();
            }
        };
    }

}
