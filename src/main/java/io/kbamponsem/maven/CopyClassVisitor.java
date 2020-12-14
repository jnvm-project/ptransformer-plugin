package io.kbamponsem.maven;

import org.objectweb.asm.*;

import java.util.Vector;

public class CopyClassVisitor extends ClassVisitor {
    int count = 0;
    String originalOwner;
    String newOwner;
    Vector<String> copyConstructors = new Vector<>();

    public CopyClassVisitor(ClassVisitor classVisitor, String originalOwner, String newOwner) {
        super(Opcodes.ASM8, classVisitor);
        this.originalOwner = originalOwner.replace(".", "/");
        this.newOwner = newOwner.replace(".", "/");
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (name.compareTo("<init>") == 0) {
            name = "$copy" + count;
            copyConstructors.add(name);
            count += 1;
        }
        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        return new MethodVisitor(super.api, mv) {
            @Override
            public void visitCode() {
                super.visitCode();
            }

            @Override
            public void visitInsn(int opcode) {
                super.visitInsn(opcode);
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                super.visitJumpInsn(opcode, label);
            }

            @Override
            public void visitVarInsn(int opcode, int var) {
                if(opcode == Opcodes.CHECKCAST){
                    visitVarInsn(Opcodes.ALOAD, 0);
                    visitVarInsn(Opcodes.ALOAD, 1);
                    visitInsn(Opcodes.CHECKCAST);
                }
                super.visitVarInsn(opcode, var);
            }

            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                if (owner.compareTo(originalOwner) == 0) {
                    owner = newOwner;
                }
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }

            @Override
            public void visitTypeInsn(int opcode, String type) {
                System.out.println("T_Opcode: "+opcode + "\t Type: "+ type);
                if(type.equals(originalOwner) && (opcode == Opcodes.INSTANCEOF || opcode == Opcodes.CHECKCAST)){
                    type = newOwner;
                }
                super.visitTypeInsn(opcode, type);
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                String tmp = "";
                System.out.println(owner + "\t" + name + "\t" + descriptor);
                System.out.println("M_Opcode: "+ opcode);
                if (opcode == Opcodes.INVOKESPECIAL && owner.equals("java/lang/Object") && name.compareTo("<init>") == 0) {
//                    opcode = Opcodes.NOP;
                } else{
                    tmp = "(L"+originalOwner+";)V";
                    if(descriptor.compareTo(tmp) == 0){
                        descriptor = "(L"+newOwner+";)V";
                    }
                    else if(descriptor.compareTo("(L"+originalOwner+";)"+originalOwner) == 0){
                        descriptor = "(L"+newOwner+";)"+newOwner;
                    }
                    if(owner.equals(originalOwner)){
                        owner = newOwner;
                    }
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            }


            @Override
            public void visitMaxs(int maxStack, int maxLocals) {
                super.visitMaxs(5, 5);
            }

            @Override
            public void visitEnd() {

                super.visitEnd();
            }
        };
    }

    public Vector<String> getCopyConstructors(){return copyConstructors;}

}
