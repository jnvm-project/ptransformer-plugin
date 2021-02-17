package io.kbamponsem.maven.util;
import org.junit.Assert;
import org.junit.Test;

import java.util.Vector;

public class ClassFileSorterTest {

    @Test
    public void classFileSorterTest(){
        Vector<String> empty = new Vector<>();
        Assert.assertEquals("must be empty", empty, ClassFileSorter.getAllClassFiles("/home/amponsem/Projects/jnvm/ptransformer-plugin/src"));
    }
}
