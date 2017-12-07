package com.example.dugbang.twopi;

/**
 * Created by shbae on 2017-11-02.
 */
public class StateRuleException extends RuntimeException {
//    String msg;
    int errorCode = 0;

    public StateRuleException(String message) {
        super(message);
//        this.errorCode = errorCode;
    }
}
