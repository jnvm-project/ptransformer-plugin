package io.kbamponsem.maven;

import io.kbamponsem.maven.util.Functions;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.Vector;

public class FieldAccessMethodTransformer extends MethodVisitor {
    Vector<String> nonTransientFields;
    public FieldAccessMethodTransformer(MethodVisitor methodVisitor, Vector<String> nonTransientFields) {
        super(Opcodes.ASM8, methodVisitor);
        this.nonTransientFields = nonTransientFields;
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        String _name = Functions.capitalize(name);
        if (opcode == Opcodes.GETFIELD & findField(name)) {
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, "$get".concat(_name), "()".concat(descriptor), false);
        } else if (opcode == Opcodes.PUTFIELD & findField(name)) {
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, "$set".concat(_name), "(" + descriptor + ")V", false);
        } else
            super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    boolean findField(String name){
        for(String s : nonTransientFields){
            if(s.compareTo(name) == 0)
                return true;
        }
        return false;
    }
}
