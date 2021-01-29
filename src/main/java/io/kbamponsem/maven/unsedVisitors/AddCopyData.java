package io.kbamponsem.maven.unsedVisitors;

import io.kbamponsem.maven.util.FieldDetails;
import io.kbamponsem.maven.util.MethodDetails;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;
import java.util.Vector;

public class AddCopyData extends ClassVisitor {

    Vector<MethodNode> methodDetails;
    Vector<FieldNode> fieldDetails;
    String className;

    public AddCopyData(ClassVisitor classVisitor, Vector<MethodNode> methodDetails, Vector<FieldNode> fieldDetails, String className) {
        super(Opcodes.ASM8, classVisitor);
        this.fieldDetails = fieldDetails;
        this.methodDetails = methodDetails;
        this.className = className;
    }

    @Override
    public void visitEnd() {
        fieldDetails.forEach(x -> {
            cv.visitField(x.access, x.name, x.desc, x.signature, x.value);
        });

        methodDetails.forEach(x -> {
            createMethod(cv, x);
        });
        super.visitEnd();
    }

    void createMethod(ClassVisitor cv, MethodNode m) {
        cv.visitMethod(m.access, m.name, m.desc, m.signature, m.exceptions.toArray(new String[0]));
    }
}
