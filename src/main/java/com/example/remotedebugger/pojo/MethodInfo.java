package com.example.remotedebugger.pojo;

import java.util.List;

public class MethodInfo {
    String methodName;
    List<String> codeToExecute;

    public String getMethodName() {
        return methodName;
    }


    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<String> getCodeToExecute() {
        return codeToExecute;
    }

    public void setCodeToExecute(List<String> codeToExecute) {
        this.codeToExecute = codeToExecute;
    }

    public MethodInfo(String methodName, List<String> codeToExecute) {
        this.methodName = methodName;
        this.codeToExecute = codeToExecute;
    }
    public MethodInfo() {}
}
