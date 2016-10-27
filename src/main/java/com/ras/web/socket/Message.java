package com.ras.web.socket;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class Message<D> {
    private String type;
    private D data;

    protected Message() {}

    public void init(String type, D data) {
        this.type = type;
        this.data = data;
    }

    public String getType() {
        return type;
    }

    public D getData() {
        return data;
    }
}
