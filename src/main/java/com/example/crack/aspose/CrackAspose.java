package com.example.crack.aspose;

import javassist.*;

import java.io.IOException;

public class CrackAspose {
    public static void main(String[] args) throws Exception {
        ClassPool.getDefault().insertClassPath("D:\\maven-repo\\aspose-words-22.11-jdk17.jar");
        CtClass clazzz = ClassPool.getDefault().getCtClass("com.aspose.words.zzW93");
        CtMethod method = clazzz.getDeclaredMethod("zzgp");
        method.setBody("{return true;}");

//这一步就是将破译完的代码放在桌面上
        clazzz.writeFile("D:\\maven-repo");
    }
}
