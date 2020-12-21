package io.kbamponsem.maven.unsedVisitors;

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

        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "$equals", "()Z", null, null);
        Label TRUE = new Label();
        Label END = new Label();
        Label FALSE = new Label();

        if(mv != null){
            mv.visitCode();
            mv.visitInsn(Opcodes.ICONST_1);
            mv.visitInsn(Opcodes.IRETURN);
            mv.visitMaxs(4,4);
            mv.visitEnd();

        }
        super.visitEnd();
    }
}
