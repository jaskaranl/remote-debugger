package com.example.remotedebugger.javaagent;

import com.example.remotedebugger.pojo.RedditResponse;
import javassist.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class CustomTransformer implements ClassFileTransformer {

    private String classNameToModify;
    private String methodNameToModify;

    private String codeToExecute;
    private int lineNumber;

    public CustomTransformer(String className,String methodName) {
        this.classNameToModify=className;
        this.methodNameToModify=methodName;

    }
    public CustomTransformer(){}
    public CustomTransformer(String className,String methodName,String codeToExecute,int lineNumber ) {
        this.classNameToModify=className;
        this.methodNameToModify=methodName;
        this.codeToExecute=codeToExecute;
        this.lineNumber=lineNumber;
    }

    public String getClassNameToModify() {
        return classNameToModify;
    }

    public void setClassNameToModify(String classNameToModify) {
        this.classNameToModify = classNameToModify;
    }

    public String getMethodNameToModify() {
        return methodNameToModify;
    }

    public void setMethodNameToModify(String methodNameToModify) {
        this.methodNameToModify = methodNameToModify;
    }

    public String getCodeToExecute() {
        return codeToExecute;
    }

    public void setCodeToExecute(String codeToExecute) {
        this.codeToExecute = codeToExecute;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException
    {

        if(className.equals(classNameToModify)) {
            try {
                ClassPool classPool = new ClassPool(true);
                return addLogging(classfileBuffer, className, loader,classPool);
//                return addLogging(classfileBuffer, className, loader,methodNameToModify,classPool);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return classfileBuffer;
    }

    private byte[] addLogging(byte[] classfileBuffer, String className,
                               ClassLoader loader,
                               ClassPool classPool)
            throws IOException,CannotCompileException
    {

        classPool.appendClassPath(new LoaderClassPath(loader));
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

        try {
            CtMethod method = ctClass.getDeclaredMethod(methodNameToModify);
            StringBuilder condition=new StringBuilder();
            condition.append(codeToExecute);
            int i = method.insertAt(lineNumber, condition.toString());
//            System.out.println(condition);
//            System.out.println(i);
////            String logStatement = "{ java.util.logging.Logger.getLogger(\"" + className + "\").info(\""+methodNameToModify+" method start...\"); }";
////            condition.append(logStatement);
////            method.insertAfter(logStatement);
////            method.insertAfter(condition.toString());
////            javaAgent.logMethodParameterValues(method);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instrument class", e);
        }

        byte[] bytecode = ctClass.toBytecode();
        ctClass.defrost();

        return bytecode;
    }

    private static byte[] addLoggingFirst(byte[] classfileBuffer, String className,ClassLoader loader,
                                     String methodNameToModify,ClassPool classPool)
            throws IOException,CannotCompileException
    {
        classPool.appendClassPath(new LoaderClassPath(loader));
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
        try {
            CtMethod method = ctClass.getDeclaredMethod(methodNameToModify);
            String logStatement = "{ java.util.logging.Logger.getLogger(\"" + className + "\").info(\""+methodNameToModify+" method start...\"); }";

            method.insertAfter(logStatement);
            javaAgent.logMethodParameterValues(method);
        } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to instrument class", e);
            }

        byte[] bytecode = ctClass.toBytecode();
        ctClass.defrost();
        return bytecode;

    }

}
