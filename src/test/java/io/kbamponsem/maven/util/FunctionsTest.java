package io.kbamponsem.maven.util;

import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Method;

class TestClass{
    int x;
}
public class FunctionsTest {
    @Test
    public void getTypeFromDescTest(){
        Assert.assertSame("int is I", "int", Functions.getTypeFromDesc("I"));
    }

    @Test
    public void getDescFromTypeTest(){
        Assert.assertSame( Opcodes.ILOAD, Functions.getDescOpcode("I"));
    }

    @Test
    public void getMethodFromNameTest(){
        Assert.assertSame(null, Functions.getMethodFromName(new Method[]{}, "", ""));
    }

    @Test
    public void getSizeOfFieldsTest(){
        Assert.assertEquals(4, Functions.getSizeOfFields(TestClass.class));
    }

    @Test
    public void getFieldOffsetTest(){
        Assert.assertEquals(8, Functions.getFieldOffset(0, "D"));
    }

    @Test
    public void getClassIDTest(){
        Assert.assertEquals(-1, Functions.getClassID(null, TestClass.class));
    }

    @Test
    public void isPersistentTest(){
        Assert.assertEquals(false, Functions.isPersistence(TestClass.class, ""));
    }
}
