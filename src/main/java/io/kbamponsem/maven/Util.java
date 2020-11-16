package io.kbamponsem.maven;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Util<T> {
    List<T> list;
    public void printList(T[] a){
        this.list = Arrays.asList(a);
        list.stream().forEach(x->System.out.println(x));
    }
    public AtomicBoolean find(T t, T[] a){
        this.list = Arrays.asList(a);
        AtomicBoolean found = new AtomicBoolean(false);
        if(list.size() != 0){
            list.stream().forEach(x->{
                if(x.getClass() == t.getClass()){
                    found.set(true);
                }
            });
        }else found.set(false);

        return found;
    }

    public void writeBytes(String name, byte[] b){
        FileOutputStream fileOutputStream = null;

        try{
            fileOutputStream = new FileOutputStream(name);
            fileOutputStream.write(b);
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
