package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Vector;

public class AddSuperCall extends ClassVisitor {
    String pInterface;
    long offset;
    Vector<String> copyConstructor;
    String className;
    public AddSuperCall(ClassVisitor classVisitor, String pInterface, Vector<String> copyConstructor, String className) {
        super(Opcodes.ASM8, classVisitor);
        this.pInterface = pInterface;
//        this.offset = offset;
        this.copyConstructor = copyConstructor;
        this.className = className;
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(J)V", null, null);
        if(mv != null){
            mv.visitCode();
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitVarInsn(Opcodes.LLOAD, 1);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, pInterface.replace("/", "."), "<init>", "(J)V", true);
            mv.visitInsn(Opcodes.RETURN);
            copyConstructor.forEach(x->{
                if(x.contains("$0")){
                    mv.visitCode();
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, className.replace("/", "."), x, "()V", true);
                    mv.visitInsn(Opcodes.RETURN);
                }
                else{
                    mv.visitCode();
                    mv.visitVarInsn(Opcodes.ALOAD, 0);
                    mv.visitVarInsn(Opcodes.LLOAD, 1);
                    mv.visitMethodInsn(Opcodes.INVOKESPECIAL, className.replace("/", "."), x, "(J)V", true);
                    mv.visitInsn(Opcodes.RETURN);
                }
            });
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        super.visitEnd();
    }
}
