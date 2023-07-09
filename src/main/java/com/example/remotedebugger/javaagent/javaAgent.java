package com.example.remotedebugger.javaagent;

import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
//import com.sun.management.OperatingSystemMXBean;
import java.lang.reflect.Modifier;
import java.util.*;

public class javaAgent {
    private static Set<String> transformedClasses = new HashSet<>();

    public static void premain(String args, Instrumentation inst)  throws Exception {

        initialiseAllInput(args,inst);

//        System.out.println("The user input is: " + userInput);
//        double cpuLoadBefore = getSystemCpuLoad();
//        System.out.println("CPU Load Before: " + cpuLoadBefore);
//
//        inst.addTransformer(new CustomTransformer("com/example/remotedebugger/controller/CommentController",transformedClasses),true);
//        Class<?>[] classes = inst.getAllLoadedClasses();
//        for (Class<?> clazz : classes) {
//            if (clazz.getName().equals("com.example.remotedebugger.controller.CommentController") && !transformedClasses.contains(clazz.getName())) {
//                inst.retransformClasses(Class.forName("com.example.remotedebugger.controller.CommentController"));
//                transformedClasses.add(clazz.getName());
//            }
//        }
//        double cpuLoadAfter = getSystemCpuLoad();
//        System.out.println("CPU Load After: " + cpuLoadAfter);
    }

    public static void initialiseAllInput(String args,Instrumentation inst) throws ClassNotFoundException, UnmodifiableClassException {

        try  {
            BufferedReader reader = new BufferedReader(new FileReader(getFileName()));
            String line = reader.readLine();
            while (line != null) {
                List<String> methodNameToModify=new ArrayList<>();

                // Process each line of input
                String clazz[] =line.split(":",-1);
                String clazzName=clazz[0];
                for (int i=1;i<clazz.length;i++) {
                    methodNameToModify.add(clazz[i]);
                }
                CustomTransformer customTransformer = new CustomTransformer(clazzName, transformedClasses, methodNameToModify);
                inst.addTransformer(customTransformer,true);

                Class<?>[] classes = inst.getAllLoadedClasses();
                for (Class<?> clazzez : classes){
                    if(clazzez.getName().equals(clazzName)&& !transformedClasses.contains(clazzez.getName())) {
                        inst.retransformClasses(Class.forName(clazzName));
                        transformedClasses.add(clazzez.getName());
                    }
                }
                line=reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public static void logMethodParameterValues(CtMethod method) throws NotFoundException, CannotCompileException
    {
        MethodInfo methodInfo = method.getMethodInfo();
        CodeAttribute codeAttribute = methodInfo.getCodeAttribute();

        // LocalVariableAttribute represents the LocalVariableTable attribute of a method,
        // which contains information about the local variables in the method.
        LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute.getAttribute(LocalVariableAttribute.tag);
        String[] paramNames = new String[method.getParameterTypes().length];

        // The first local variable is 'this', not a method parameter.
        // If the method is not static, skip the first local variable when retrieving parameter names.
        int pos = Modifier.isStatic(method.getModifiers()) ? 0 : 1;
        for (int i = 0; i < paramNames.length; i++) {
            paramNames[i] = attr.variableName(i + pos);
        }

        StringBuilder beforeCode = new StringBuilder();
        beforeCode.append("{ System.out.print(\"Non-breaking breakpoint hit at start of method. Arguments: \"); ");
        for (int i = 0; i < paramNames.length; i++) {
            beforeCode.append("System.out.print(\"" + paramNames[i] + "=\" + $args[" + i + "] + \", \"); ");
        }
        beforeCode.append("System.out.println(\"\"); }");
        method.insertBefore(beforeCode.toString());
    }
    private static double getSystemCpuLoad() {
        OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean =
                    (com.sun.management.OperatingSystemMXBean) osBean;
            return sunOsBean.getSystemCpuLoad();
        }
        return -1.0;
    }

    public static  String getFileName() {
        return "/Users/jaskaran.kamboj/Downloads/RemoteDebugger/src/main/java/com/example/remotedebugger/entryforpremain/breakpointsEntry.txt";
    }
}
