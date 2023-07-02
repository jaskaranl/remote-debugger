package com.example.demo;

import com.sun.tools.attach.VirtualMachine;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class Agent {

    private static Set<String> transformedClasses = new HashSet<>();
    public static void agentmain(String agentArgs, Instrumentation inst) {

        inst.addTransformer(new ClassFileTransformer() {

            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

                //                System.out.println(className);
                if (className.equals("com/example/demo/RestController")&& !transformedClasses.contains(className)) {
                    transformedClasses.add(className);
                    return addLogging(classfileBuffer,className);

                }
                return classfileBuffer;
            }
        }, true);




        try {
            Class<?>[] classes = inst.getAllLoadedClasses();
            for (Class<?> clazz : classes) {
                if (clazz.getName().equals("com.example.demo.RestController") && !transformedClasses.contains(clazz.getName()))
                {
                    inst.retransformClasses(Class.forName("com.example.demo.RestController"));
//                    transformedClasses.add(clazz.getName());
                }
            }
//            inst.retransformClasses(Class.forName("com.example.demo.RestController"));
        } catch (Exception e) {
            throw new RuntimeException("Failed to retransform class", e);
        }
    }

    private static byte[] addLogging(byte[] classfileBuffer,String Classname) {
        try {


            ClassPool cp = ClassPool.getDefault();

//
            CtClass cc = cp.makeClass(new ByteArrayInputStream(classfileBuffer));


            CtMethod k=cc.getDeclaredMethod("greet");

//            k.insertBefore("System.out.println(java.util.Arrays.toString($args));");
            System.out.println(1000);




            byte[] bytecode = cc.toBytecode();
          cc.detach();
            return bytecode;
        } catch (Exception e) {

            throw new RuntimeException("Failed to instrument class", e);
        }
    }
public static int count=0;
    public static void main(String[] args)  throws Exception{
        VirtualMachine vm = VirtualMachine.attach("89912");
        System.out.println(count);
        try {
//			ClassPool cp = ClassPool.getDefault();

            vm.loadAgent("/Users/jaskaran.kamboj/Downloads/demo/my-javaagent.jar");

        }
        finally {
            vm.detach();
        }



    }
}
