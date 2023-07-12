package com.example.remotedebugger.pojo;

import java.util.List;

public class MultiBreakpoint {
   private List<BreakpointResponse> responseList;


    public List<BreakpointResponse> getResponseList() {
        return responseList;
    }
    public MultiBreakpoint(){}

    public void setResponseList(List<BreakpointResponse> responseList) {
        this.responseList = responseList;
    }

    public MultiBreakpoint(List<BreakpointResponse> responseList) {
        this.responseList = responseList;
    }
}
