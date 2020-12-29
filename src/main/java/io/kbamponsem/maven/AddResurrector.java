package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class AddResurrector extends ClassVisitor {
    String className;
    String superName;
    ClassLoader classLoader;

    public AddResurrector(ClassVisitor classVisitor, String className, ClassLoader classLoader) {
        super(Opcodes.ASM8, classVisitor);
        this.className = className;
        this.classLoader = classLoader;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superName = superName;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        addCallToResurrector();
        super.visitEnd();
    }

    void addCallToResurrector() {
        MethodVisitor mv = cv.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "(Leu/telecomsudparis/jnvm/offheap/MemoryBlockHandle;)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, superName.replace(".", "/"), "<init>", "()V", false);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "eu/telecomsudparis/jnvm/offheap/MemoryBlockHandle", "getOffset", "()J", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, className.replace(".", "/"), "$copy1", "(J)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(5,5);
        mv.visitEnd();
    }
}
