package io.kbamponsem.maven;

import org.objectweb.asm.*;

public class AddEqualMethod extends ClassVisitor {
    Class aClass;
    public AddEqualMethod(ClassVisitor classVisitor, Class c) {
        super(Opcodes.ASM8, classVisitor);
        this.aClass = c;
    }


    @Override
    public void visitEnd() {
        cv.visitField(Opcodes.ACC_PRIVATE, "obj", "Ljava/lang/Object;", null, null);

        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "equals", "(Ljava/lang/Object;)Z", null, null);
        Label TRUE = new Label();
        Label END = new Label();
        Label FALSE = new Label();

        if(mv != null){
            mv.visitCode();

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitFieldInsn(Opcodes.PUTFIELD, aClass.getName().replace(".", "/"), "obj", "Ljava/lang/Object;");

            mv.visitVarInsn(Opcodes.ALOAD, 0);
//            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitFieldInsn(Opcodes.GETFIELD | Opcodes.ACC_FINAL, aClass.getName().replace(".", "/"), "obj", "Ljava/lang/Object;");
//            mv.visitJumpInsn(Opcodes.IF_ACMPNE, TRUE);

            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitJumpInsn(Opcodes.IF_ACMPNE, FALSE);

            mv.visitLabel(TRUE);
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IRETURN);


            mv.visitLabel(FALSE);
            mv.visitInsn(Opcodes.ICONST_0);
            mv.visitInsn(Opcodes.IRETURN);

            mv.visitMaxs(5,5);
            mv.visitEnd();

        }
        super.visitEnd();
    }
}
