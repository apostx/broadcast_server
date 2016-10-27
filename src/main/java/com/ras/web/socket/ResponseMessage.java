package com.ras.web.socket;

public class ResponseMessage<D> extends Message<D> {
    private int status;

    public ResponseMessage(String type, D data, int status) {
        this.init(type, data);
        this.status = status;
    }
}
