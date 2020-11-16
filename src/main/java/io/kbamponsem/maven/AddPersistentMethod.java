package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddPersistentMethod extends ClassVisitor {
    public AddPersistentMethod( ClassVisitor classVisitor) {
        super(Opcodes.ASM8, classVisitor);
    }

    @Override
    public void visitEnd() {
        MethodVisitor methodVisitor = cv.visitMethod(Opcodes.ACC_PUBLIC, "persistent", "()I", null, null);
        if(methodVisitor != null){
            methodVisitor.visitCode();
            methodVisitor.visitVarInsn(Opcodes.ALOAD, 0);
            methodVisitor.visitInsn(Opcodes.ICONST_1);
            methodVisitor.visitInsn(Opcodes.IRETURN);
            methodVisitor.visitMaxs(3,3);
            methodVisitor.visitEnd();
        }
        super.visitEnd();
    }
}
