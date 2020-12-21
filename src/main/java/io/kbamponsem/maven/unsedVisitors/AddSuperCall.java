package io.kbamponsem.maven.unsedVisitors;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Vector;

public class AddSuperCall extends ClassVisitor {
    String pInterface;
    long offset;
    HashMap<String, String> copyConstructor;
    String className;
    public AddSuperCall(ClassVisitor classVisitor, String pInterface, HashMap<String, String> copyConstructor, String className) {
        super(Opcodes.ASM8, classVisitor);
        this.pInterface = pInterface;
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
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, pInterface.replace(".", "/"), "<init>", "(J)V", true);
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(5, 5);
            mv.visitEnd();
        }
        super.visitEnd();
    }
}
