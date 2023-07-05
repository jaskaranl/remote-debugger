package com.example.demo;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;


public class Agent {
    private static Set<String> transformedClasses = new HashSet<>();

    public static void premain(String args, Instrumentation inst) throws Exception {
        inst.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
                if (className.equals("com/example/demo/RestController") && !transformedClasses.contains(className)) {
                    transformedClasses.add(className);
                    return addLogging(classfileBuffer, className);
                }
                return classfileBuffer;
            }
        }, true);

        Class<?>[] classes = inst.getAllLoadedClasses();
        for (Class<?> clazz : classes) {
            if (clazz.getName().equals("com.example.demo.RestController") && !transformedClasses.contains(clazz.getName())) {
                inst.retransformClasses(Class.forName("com.example.demo.RestController"));
                transformedClasses.add(clazz.getName());
            }
        }
    }

    private static byte[] addLogging(byte[] classfileBuffer, String Classname) {
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new ClassClassPath(Logger.class));
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod k = ctClass.getDeclaredMethod("greet");
            k.insertBefore("{ java.util.logging.Logger.getLogger(\"" + "com/example/demo/RestController"
                    + "\").info(\"greet method start...\"); }");
            parameterValues(k);
            byte[] bytecode = ctClass.toBytecode();
            ctClass.detach();
            return bytecode;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instrument class", e);
        }
    }

    public static void parameterValues(CtMethod k) throws NotFoundException, CannotCompileException {

        MethodInfo methodInfo = k.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        String[] paramNames = new String[k.getParameterTypes().length];
        int pos = Modifier.isStatic(k.getModifiers()) ? 0 : 1;

        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }
        StringBuilder beforeCode = new StringBuilder();
        beforeCode.append("{ System.out.print(\"Non-breaking breakpoint hit at start of method. Arguments: \"); ");

        for (int i = 0; i < paramNames.length; i++) {
            beforeCode.append("System.out.print(\"" + paramNames[i] + "=\" + $args[" + i + "] + \", \"); ");
        }

        beforeCode.append("System.out.println(\"\"); }");
        k.insertBefore(beforeCode.toString());

    }
}
