package io.kbamponsem.maven;

import io.kbamponsem.maven.util.Functions;
import org.objectweb.asm.*;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Vector;

public class CopyClassVisitor extends ClassVisitor {
    int count = 0;
    String originalOwner;
    String newOwner;
    Vector<String> copyConstructors = new Vector<>();
    ClassLoader classLoader;
    Class aClass;

    public CopyClassVisitor(ClassVisitor classVisitor, String originalOwner, String newOwner, ClassLoader classLoader, Class aClass) {
        super(Opcodes.ASM8, classVisitor);
        this.originalOwner = originalOwner.replace(".", "/");
        this.newOwner = newOwner.replace(".", "/");
        this.classLoader = classLoader;
        this.aClass = aClass;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        /*
            In this visitMethod:
                * we go through the class and look for all "<init>" methods -> which represent constructors.
                * And also at the return MethodVisitor, we take ownership from OffHeapObjectHandle and give it to the respective class.
         */
        if (name.compareTo("<init>") == 0) {
            name = "$copy" + count;
            copyConstructors.add(name);
            count += 1;
        }

        MethodVisitor mv = cv.visitMethod(access, name, descriptor, signature, exceptions);
        if (name.contains("attach")) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitLdcInsn(Functions.getClassID(classLoader, aClass));
            mv.visitFieldInsn(Opcodes.PUTFIELD, newOwner, "CLASS_ID", "J");
            return new MethodVisitor(super.api, mv) {
                @Override
                public void visitCode() {
                    super.visitCode();
                }

                @Override
                public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                    owner = newOwner;
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    if (owner.equals("eu/telecomsudparis/jnvm/offheap/MemoryBlockHandle")) {
                    } else owner = newOwner;
                    if (name.contains("classId")) {
//
                        mv.visitFieldInsn(Opcodes.GETFIELD, newOwner, "CLASS_ID", "J");
                        mv.visitMaxs(6, 2);
                    } else
                        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }

                @Override
                public void visitMaxs(int maxStack, int maxLocals) {
                    super.visitMaxs(maxStack, maxLocals);
                }

                @Override
                public void visitEnd() {
                    super.visitEnd();
                }
            };
        } else {
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
                    if (opcode == Opcodes.CHECKCAST) {
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
                    if (type.equals(originalOwner) && (opcode == Opcodes.INSTANCEOF || opcode == Opcodes.CHECKCAST)) {
                        type = newOwner;
                    }
                    super.visitTypeInsn(opcode, type);
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                    String tmp = "";

                    if (opcode == Opcodes.INVOKESPECIAL && owner.equals("java/lang/Object") && name.compareTo("<init>") == 0) {
                    } else {
                        tmp = "(L" + originalOwner + ";)V"; // original owner -> OffHeapObjectHandle
                        if (descriptor.compareTo(tmp) == 0) {
                            descriptor = "(L" + newOwner + ";)V"; // new owner -> the persistent class, with void return
                        } else if (descriptor.compareTo("(L" + originalOwner + ";)" + originalOwner) == 0) {
                            descriptor = "(L" + newOwner + ";)" + newOwner; // new owner -> persistent class, with new owner return;
                        }
                        if (owner.equals(originalOwner)) {
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

    }

    public Vector<String> getCopyConstructors() {
        return copyConstructors;
    }

}
