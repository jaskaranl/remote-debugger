package com.example.remotedebugger.javaagent;
import java.lang.instrument.Instrumentation;

public class AgentForInstrumentation {

    public static Instrumentation instrumentation;
    public static void premain(String args, Instrumentation inst)  throws Exception {
        instrumentation=inst;
    }

    public static Instrumentation getInstrumentation()
    {
        return instrumentation;
    }

}
