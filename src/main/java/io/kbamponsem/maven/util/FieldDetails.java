package io.kbamponsem.maven.util;

public class FieldDetails {
    int access;
    String name;
    String descriptor;
    String signature;
    Object value;

    public FieldDetails(int access, String name, String descriptor, String signature, Object value){
        this.access = access;
        this.descriptor = descriptor;
        this.name = name;
        this.value= value;
        this.signature=signature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAccess(int access) {
        this.access = access;
    }

    public int getAccess() {
        return access;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setValue(String[] exceptions) {
        this.value = exceptions;
    }

    public Object getValue() {
        return value;
    }
}
