package com.example.demo;

import com.sun.tools.attach.VirtualMachine;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

public class jj {
    public static void agentmain(String agentArgs, Instrumentation inst) {

        inst.addTransformer(new ClassFileTransformer() {


            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//                System.out.println(className);
                if (className.equals("com/example/demo/RestController")) {

                    return addLogging(classfileBuffer,className);

                }
                return classfileBuffer;
            }
        }, true);

        inst.addTransformer(new ClassFileTransformer() {


            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//                System.out.println(className);
                if (className.equals("com/example/demo/RestController")) {

                    return addLogging(classfileBuffer,className);

                }
                return classfileBuffer;
            }
        }, true);


        try {

            inst.retransformClasses(Class.forName("com.example.demo.RestController"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retransform class", e);
        }
    }

    private static byte[] addLogging(byte[] classfileBuffer,String Classname) {
        try {
            ClassPool cp = ClassPool.getDefault();

//
            CtClass cc = cp.makeClass(new ByteArrayInputStream(classfileBuffer));


            System.out.println(1);




            byte[] bytecode = cc.toBytecode();
            cc.detach();
            return bytecode;
        } catch (Exception e) {

            throw new RuntimeException("Failed to instrument class", e);
        }
    }
    public static int count=0;
    public static void main(String[] args)  throws Exception{
        VirtualMachine vm = VirtualMachine.attach("9913");

        try {
//			ClassPool cp = ClassPool.getDefault();

            vm.loadAgent("/Users/jaskaran.kamboj/Downloads/demo/gg.jar");

        }
        finally {
            vm.detach();
        }



    }
}
