package io.kbamponsem.maven;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

public class AddClassIdField extends ClassVisitor {
    ClassLoader classLoader;
    Class thisClass;

    public AddClassIdField(ClassVisitor classVisitor, ClassLoader classLoader, Class c) {
        super(Opcodes.ASM8, classVisitor);
        this.classLoader = classLoader;
        this.thisClass = c;
    }

    @Override
    public void visitEnd() {
        cv.visitField(Opcodes.ACC_PRIVATE, "CLASS_ID", "J", null, -1);

        super.visitEnd();
    }
}
