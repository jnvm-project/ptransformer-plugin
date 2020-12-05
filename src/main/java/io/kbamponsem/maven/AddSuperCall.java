package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Vector;

public class AddSuperCall extends ClassVisitor {
    String pInterface;
    long offset;
    Vector<String> copyConstructor;
    String copyClassName;
    public AddSuperCall(ClassVisitor classVisitor, String pInterface, Vector<String> copyConstructor, String copyClassName) {
        super(Opcodes.ASM8, classVisitor);
        this.pInterface = pInterface;
//        this.offset = offset;
        this.copyConstructor = copyConstructor;
        this.copyClassName = copyClassName;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(J)V", null, null);
        if(mv != null){
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.LLOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, pInterface.replace("/", "."), "<init>", "(J)V", true);
            copyConstructor.forEach(x->{
                if(x.contains("$0")){
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, copyClassName.replace("/", "."), x, "()V", true);
                }
                else{
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.LLOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, copyClassName.replace("/", "."), x, "(J)V", true);

                }
            });
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        super.visitEnd();
    }
}
