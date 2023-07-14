package com.example.remotedebugger.javaagent;

import com.example.remotedebugger.pojo.CodeWithLineNumber;
import com.example.remotedebugger.pojo.MethodInfo;
import javassist.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;
import java.util.List;

public class CustomTransformers implements ClassFileTransformer {
    private String classNametoModify;
    private List<MethodInfo> methodInfo;

    public String getClassName() {
        return classNametoModify;
    }

    public void setClassName(String className) {
        this.classNametoModify = className;
    }

    public List<MethodInfo> getMethod() {
        return methodInfo;
    }

    public void setMethod(List<MethodInfo> method) {
        this.methodInfo = method;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer)
            throws IllegalClassFormatException {
        if (className.equals(classNametoModify)) {
            try {
                ClassPool classPool = new ClassPool(true);
                return addLogging(classfileBuffer, className, loader, classPool, methodInfo);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return classfileBuffer;
    }

    private byte[] addLogging(byte[] classfileBuffer, String className, ClassLoader loader, ClassPool classPool, List<MethodInfo> methodInfo) throws IOException, CannotCompileException {
        classPool.appendClassPath(new LoaderClassPath(loader));
        CtClass ctClass = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
        StringBuilder condition = new StringBuilder();

        try {
            for (MethodInfo methodInfoIterator : methodInfo) {
                String methodName = methodInfoIterator.getMethodName();
                CtMethod method = ctClass.getDeclaredMethod(methodName);
                List<CodeWithLineNumber> codeWithLine = methodInfoIterator.getCodeToExecute();
                for (CodeWithLineNumber codeWithLineNumber : codeWithLine) {
                    condition.append(codeWithLineNumber.getCode());
                    method.insertAt(codeWithLineNumber.getLineNumber(), condition.toString());
                    condition = new StringBuilder();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to instrument class", e);
        }

        byte[] bytecode = ctClass.toBytecode();
        ctClass.defrost();

        return bytecode;
    }
}
