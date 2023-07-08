package com.example.remotedebugger.javaagent;

import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class CustomTransformer implements ClassFileTransformer
{
    private String classNameByUser;
    private List<String> methodNameToModify;
    private Set<String> transformedClasses;
    public  CustomTransformer(String className, Set<String> transformedClasses,List<String> methodNameToModify) {
        this.classNameByUser= className;
        this.transformedClasses = transformedClasses;
        this.methodNameToModify=methodNameToModify;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {

        if (className.equals(classNameByUser) && !transformedClasses.contains(classNameByUser)) {
            // Add the className to the hashset to prevent multiple instances on the same Class
            transformedClasses.add(className);
            try {
                return addLogging(classfileBuffer, className,methodNameToModify);
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return classfileBuffer;
    }

    private static byte[] addLogging(byte[] classfileBuffer, String className,List<String> methodNameToModify)   throws IOException,CannotCompileException
    {
        ClassPool classPool = ClassPool.getDefault();
        classPool.insertClassPath(new ClassClassPath(Logger.class));
        // Get the bytecode of the target class and store it for modification
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

        for (int countOfMethod = 0; countOfMethod < methodNameToModify.size(); countOfMethod++) {
            try {
                String currentMethodName= methodNameToModify.get(countOfMethod);
                CtMethod method = ctClass.getDeclaredMethod(currentMethodName);

                // Add logging statements before the method starts using insertBefore
                String logStatement = "{ java.util.logging.Logger.getLogger(\"" + className + "\").info(\""+currentMethodName+" method start...\"); }";
                method.insertBefore(logStatement);
                System.out.println(methodNameToModify.get(countOfMethod));

                // Add logging of method parameter values each time the method is called
                javaAgent.logMethodParameterValues(method);
            } catch (Exception e) {
                throw new RuntimeException("Failed to instrument class", e);
            }
        }
        // Store the modified bytecode
        byte[] bytecode = ctClass.toBytecode();
        ctClass.detach();
        return bytecode;
    }
}
