package com.example.remotedebugger.javaagent;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class CustomTransformerForProfiling implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException
    {
        ClassPool cp = ClassPool.getDefault();
        CtClass ctClass = null;
        try
        {
            ctClass = cp.makeClass(new ByteArrayInputStream(classfileBuffer));
            for (CtMethod method : ctClass.getDeclaredMethods())
            {
                method.addLocalVariable("startTime", CtClass.longType);
                method.insertBefore("startTime = System.nanoTime();");
                method.insertAfter("System.out.println(\"Execution time (ns): \" + (System.nanoTime() - startTime));");
                return ctClass.toBytecode();
            }
        }catch(IOException | CannotCompileException e){
                e.printStackTrace();
            } finally{
                if (ctClass != null) {
                    ctClass.detach();
                }
            }

            return null;
        }
    }

