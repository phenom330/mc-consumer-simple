package com.zb213.mc.consumer.common;

public class SendMsgException extends Exception{

    private static final long serialVersionUID = 3769209478658566537L;
    private int errorCode;

    public SendMsgException(){

        super();
    }

    public SendMsgException(String msg){

        super(msg);
    }

    public SendMsgException(String msg, int errorCode){

        super(msg);
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

}
