package com.example.remotedebugger.javaagent;

import java.lang.instrument.Instrumentation;

public class ProfilingJavaAgent {
    public static void premain(String agentArgs, Instrumentation inst)
    {
            inst.addTransformer(new CustomTransformerForProfiling(),true);
    }
}
