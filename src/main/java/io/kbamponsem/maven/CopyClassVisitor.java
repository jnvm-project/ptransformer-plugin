package io.kbamponsem.maven;

import io.kbamponsem.maven.util.FieldDetails;
import io.kbamponsem.maven.util.MethodDetails;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.SimpleRemapper;

import java.util.HashMap;
import java.util.Vector;

public class CopyClassVisitor extends ClassVisitor {
    private HashMap<String, String> copyConstructors = new HashMap<>();
    private Vector<FieldDetails> fieldDetails = new Vector<>();
    private Vector<MethodDetails> methodDetails = new Vector<>();

    int count = 0;
    String owner;

    public CopyClassVisitor(ClassVisitor classVisitor, String owner) {
        super(Opcodes.ASM8, classVisitor);
        this.owner = owner;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        fieldDetails.add(new FieldDetails(access, name, descriptor, signature, value));
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {

            MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
            if(name.compareTo("<init>") == 0){
                name = "$copy"+count;
            }
            methodDetails.add(new MethodDetails(access, name, descriptor, signature, exceptions, mv));

        count += 1;
        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    public HashMap<String, String> getCopyConstructors() {
        return copyConstructors;
    }

    public Vector<FieldDetails> getFieldDetails() {
        return fieldDetails;
    }

    public Vector<MethodDetails> getMethodDetails(){
        return methodDetails;
    }
}
