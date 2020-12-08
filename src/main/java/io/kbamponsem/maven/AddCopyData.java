package io.kbamponsem.maven;

import io.kbamponsem.maven.util.FieldDetails;
import io.kbamponsem.maven.util.MethodDetails;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;

import java.util.Vector;

public class AddCopyData extends ClassVisitor {

    Vector<MethodDetails> methodDetails;
    Vector<FieldDetails> fieldDetails;

    public AddCopyData(ClassVisitor classVisitor, Vector<MethodDetails> methodDetails, Vector<FieldDetails> fieldDetails) {
        super(Opcodes.ASM8, classVisitor);
        this.fieldDetails = fieldDetails;
        this.methodDetails = methodDetails;
    }

    @Override
    public void visitEnd() {
        fieldDetails.forEach(x->{
            cv.visitField(x.getAccess(), x.getName(), x.getDescriptor(), x.getSignature(), x.getValue());
        });

        methodDetails.forEach(x->{
            createMethod(cv, x);
        });
        super.visitEnd();
    }

    MethodVisitor createMethod(ClassVisitor cv, MethodDetails m){
        MethodVisitor mv = cv.visitMethod(m.getAccess(), m.getName(), m.getDescriptor(), m.getSignature(), m.getExceptions());
        if(mv != null){
            return mv;
        }
        return null;
    }
}
