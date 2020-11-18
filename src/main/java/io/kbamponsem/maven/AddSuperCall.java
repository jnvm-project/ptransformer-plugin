package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddSuperCall extends ClassVisitor {
    String pSuperName;
    long offset;
    public AddSuperCall( ClassVisitor classVisitor, String pSuperName) {
        super(Opcodes.ASM8, classVisitor);
        this.pSuperName = pSuperName;
//        this.offset = offset;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(J)V", null, null);
        if(mv != null){
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.LLOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, pSuperName.replace("/", "."), "<init>", "(J)V", true);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        super.visitEnd();
    }
}
