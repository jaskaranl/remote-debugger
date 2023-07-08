package com.example.remotedebugger.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Child {
    private String kind;
    private MainObjective data;
    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public MainObjective getData() {
        return data;
    }

    public void setData(MainObjective data) {
        this.data = data;
    }


}
