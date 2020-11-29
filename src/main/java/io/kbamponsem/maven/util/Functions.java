package io.kbamponsem.maven.util;

import org.objectweb.asm.Opcodes;

public class Functions {
    static public String capitalize(String s){
        String first = s.substring(0, 1);
        String remaining = s.substring(1);
        first = first.toUpperCase();

        return first.concat(remaining);

    }

    static public int getDescOpcode(String desc){
        switch (desc){
            case "I":
            case "C":
            case "Z":
            case "S":
            case "B":
                return Opcodes.ILOAD;
            case "J":
                return Opcodes.LLOAD;
            case "F":
                return Opcodes.FLOAD;
            case "D":
                return Opcodes.DLOAD;
            default:
                return 0;
        }
    }
}
