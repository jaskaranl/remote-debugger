package com.example.demo;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import com.sun.tools.attach.VirtualMachine;
import javassist.*;
import org.springframework.javapoet.ClassName;

import java.io.ByteArrayInputStream;


public class MyJavaAgent{
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
                        addLogginger(classfileBuffer,className);
                    return addLogginger(classfileBuffer,className);

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

            CtMethod m = cc.getDeclaredMethod("greet");
//            CtMethod k=cc.getDeclaredMethod("help");
//
//            k.insertAfter("{ System.out.println(\"Non-breaking breakpoint hit at end of methoddd\"); }");


               m.insertBefore("{ System.out.println(\"Non-breaking breakpoint hit at start of method\"); }");
            m.insertAfter("{ System.out.println(\"Non-breaking breakpoint hit at end of method\"); }");


            byte[] bytecode = cc.toBytecode();
            cc.defrost();
            return bytecode;
        } catch (Exception e) {

            throw new RuntimeException("Failed to instrument class", e);
        }
    }

    public static void main(String[] args)  throws Exception{
        VirtualMachine vm = VirtualMachine.attach("19825");
        try {
//			ClassPool cp = ClassPool.getDefault();

            vm.loadAgent("/Users/jaskaran.kamboj/Downloads/demo/my-java--agent.jar");

        }
        finally {
            vm.detach();
        }

    }
}
