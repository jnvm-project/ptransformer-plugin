package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class AddResurrector extends ClassVisitor {
    String className;
    String superName;
    boolean resurrectorPresent = false;
    public AddResurrector(ClassVisitor classVisitor, String className) {
        super(Opcodes.ASM8, classVisitor);
        this.className = className;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superName = superName;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if(name.equals("<init>") && descriptor.equals("(J)V")){
            resurrectorPresent = true;
        }
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        if(!resurrectorPresent){
            addCallToResurrector();
        }
        super.visitEnd();
    }

    void addCallToResurrector(){
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(J)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.LLOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className.replace(".", "/"), "$copy1", "(J)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitEnd();
    }
}
