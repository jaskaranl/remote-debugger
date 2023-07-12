package com.example.remotedebugger.javaagent;
import javassist.*;
import javassist.CannotCompileException;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;

public class javaAgent {

    public static Instrumentation instrumentation;
    public static void premain(String args, Instrumentation inst)  throws Exception {
        instrumentation=inst;
    }

    public static void logMethodParameterValues(CtMethod method) throws NotFoundException, CannotCompileException {
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
//    private static double getSystemCpuLoad() {
//        OperatingSystemMXBean osBean = (OperatingSystemMXBean)ManagementFactory.getOperatingSystemMXBean();
//        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
//            com.sun.management.OperatingSystemMXBean sunOsBean =
//                    (com.sun.management.OperatingSystemMXBean) osBean;
//            return sunOsBean.getSystemCpuLoad();
//        }
//        return -1.0;
//    }
    public static Instrumentation getInstrumentation()
    {
        return instrumentation;
    }
}
