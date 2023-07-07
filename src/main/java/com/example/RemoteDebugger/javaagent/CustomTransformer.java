package com.example.RemoteDebugger.javaagent;

import javassist.*;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import org.springframework.data.geo.Metrics;

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static com.example.RemoteDebugger.javaagent.javaAgent.logMethodParameterValues;

public class CustomTransformer implements ClassFileTransformer
{
    private String className;
    private Set<String> transformedClasses;
    public  CustomTransformer(String className, Set<String> transformedClasses)
    {

        this.className = className;
        this.transformedClasses = transformedClasses;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        if (className.equals("com/example/RemoteDebugger/controller/CommentController") && !transformedClasses.contains(className))
        {
            // Add the className to the hashset to prevent multiple instances on the same Class
            transformedClasses.add(className);
            return addLogging(classfileBuffer, className);
        }
        return classfileBuffer;
    }

    private static byte[] addLogging(byte[] classfileBuffer, String className)
    {
        try {
            ClassPool classPool = ClassPool.getDefault();
            classPool.insertClassPath(new ClassClassPath(Logger.class));

            // Get the bytecode of the target class and store it for modification
            CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            CtMethod method = ctClass.getDeclaredMethod("greet");

            // Add logging statements before the method starts using insertBefore
            String logStatement = "{ java.util.logging.Logger.getLogger(\"" + className + "\").info(\"greet method start...\"); }";
            method.insertBefore(logStatement);

            // Add logging of method parameter values each time the method is called
           javaAgent.logMethodParameterValues(method);

            // Store the modified bytecode
            byte[] bytecode = ctClass.toBytecode();
            ctClass.detach();
            return bytecode;
        } catch (Exception e) {
            throw new RuntimeException("Failed to instrument class", e);
        }
    }

}
