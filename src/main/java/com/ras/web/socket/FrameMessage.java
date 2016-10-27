package com.ras.web.socket;

import com.fasterxml.jackson.databind.JsonNode;

public class FrameMessage extends Message<JsonNode> {
    public FrameMessage() {}

    public FrameMessage(String type, JsonNode data) {
        init(type, data);
    }
}
