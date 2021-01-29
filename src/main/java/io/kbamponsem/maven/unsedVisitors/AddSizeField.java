package io.kbamponsem.maven.unsedVisitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddSizeField extends ClassVisitor {
    long SIZE;
    public AddSizeField(ClassVisitor classVisitor, long SIZE) {
        super(Opcodes.ASM8, classVisitor);
        this.SIZE = SIZE;
    }

    @Override
    public void visitEnd() {
        cv.visitField(Opcodes.ACC_FINAL | Opcodes.ACC_PRIVATE | Opcodes.ACC_STATIC, "SIZE", "J", null, this.SIZE);
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "size", "()J", null, null);
        mv.visitCode();
        mv.visitLdcInsn(this.SIZE);
        mv.visitInsn(Opcodes.LRETURN);
        mv.visitMaxs(2,2);
        mv.visitEnd();
        super.visitEnd();
    }
}
