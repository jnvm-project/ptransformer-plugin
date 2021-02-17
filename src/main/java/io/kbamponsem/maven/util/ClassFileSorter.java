package io.kbamponsem.maven.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.util.Vector;

public class ClassFileSorter {
    static public Vector<String> getAllClassFiles(String basedir){
        Vector<String> files = new Vector<>();
        File oDir = new File(basedir);

        FileUtils.listFiles(oDir, TrueFileFilter.INSTANCE, TrueFileFilter.TRUE).forEach(x -> {
            if (x.toString().contains(".class"))
                files.add(Functions.transformClassFiles(x, oDir));
        });

        return files;
    }
}
