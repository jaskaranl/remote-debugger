package com.example.remotedebugger.pojo;

public class CodeWithLineNumber {
    String code;
    int lineNumber;

    public CodeWithLineNumber(String code, int lineNumber) {
        this.code = code;
        this.lineNumber = lineNumber;
    }
    public CodeWithLineNumber(){}
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }
}
