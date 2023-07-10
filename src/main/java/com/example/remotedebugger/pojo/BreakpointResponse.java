package com.example.remotedebugger.pojo;

public class BreakpointResponse {
    String className;
    String methodName;

    public BreakpointResponse(String className, String methodName) {
        this.className = className;
        this.methodName = methodName;
    }

    public BreakpointResponse() {}
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
