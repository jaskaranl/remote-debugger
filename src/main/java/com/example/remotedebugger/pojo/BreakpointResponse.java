package com.example.remotedebugger.pojo;

import java.util.List;

public class BreakpointResponse {
    private String className;
    private List<MethodInfo> method;

    public BreakpointResponse(String className, List<MethodInfo> method) {
        this.className = className;
        this.method = method;
    }

    public BreakpointResponse() {
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public List<MethodInfo> getMethod() {
        return method;
    }

    public void setMethod(List<MethodInfo> method) {
        this.method = method;
    }
}
