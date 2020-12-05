package io.kbamponsem.maven.util;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.Arrays;

public class MethodDetails {
    int access;
    String name;
    String descriptor;
    String signature;
    String[] exceptions;
    MethodVisitor mv;

    public MethodDetails(int access, String name, String descriptor, String signature, String[] exceptions, MethodVisitor mv){
        this.access = access;
        this.name = name;
        this.descriptor = descriptor;
        this.signature = signature;
        this.exceptions = exceptions;
        this.mv = mv;
    }
    public int getAccess() {
        return access;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSignature() {
        return signature;
    }

    public void setExceptions(String[] exceptions) {
        this.exceptions = exceptions;
    }

    public String[] getExceptions() {
        return exceptions;
    }

    public MethodVisitor getMv() {
        return mv;
    }

    public void setMv(MethodVisitor mv) {
        this.mv = mv;
    }

    public void displayMethod(){
        MethodNode mn = new MethodNode();
        mn.accept(this.mv);
        System.out.println("--------------------------------------------");
        System.out.println(this.name + "\t" + this.descriptor);
        System.out.println(mn.name);
//        System.out.println("---Instruction---");
//        Arrays.asList(mn.instructions.toArray()).forEach(System.out::println);
        System.out.println("--------------------------------------------");

    }
}

