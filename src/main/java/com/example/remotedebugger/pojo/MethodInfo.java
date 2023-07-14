package com.example.remotedebugger.pojo;

import java.util.List;

public class MethodInfo {
    String methodName;
    List<CodeWithLineNumber> codeToExecute;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public List<CodeWithLineNumber> getCodeToExecute() {
        return codeToExecute;
    }

    public void setCodeToExecute(List<CodeWithLineNumber> codeToExecute) {
        this.codeToExecute = codeToExecute;
    }

    public MethodInfo() {
    }

    public MethodInfo(String methodName, List<CodeWithLineNumber> codeToExecute) {
        this.methodName = methodName;
        this.codeToExecute = codeToExecute;
    }
}
