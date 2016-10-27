package com.ras.broadcastserver.ws.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ras.broadcastserver.ws.game.Room;
import com.ras.web.socket.FrameMessage;
import com.ras.web.socket.bind.annotation.ConnectionClosed;
import com.ras.web.socket.bind.annotation.ConnectionEstablished;
import com.ras.web.socket.bind.annotation.JsonMessage;
import com.ras.web.socket.bind.annotation.TransportError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMessageHandler {

    @Autowired
    private Room<WebSocketSession> gameRoom;

    @Autowired
    private ObjectMapper objectMapper;

    private Logger logger = Logger.getAnonymousLogger();

    @JsonMessage(type = "join")
    public void joinMessageHandler(WebSocketSession session, JsonNode message) throws Exception {
    }

    @JsonMessage(type = "broadcast")
    public void broadcastMessageHandler(WebSocketSession session, JsonNode message) throws Exception {
        logger.log(Level.SEVERE, "BroadcastMessage: " + message.toString());
        FrameMessage broadcastMessage = new FrameMessage("broadcast", message);
        TextMessage broadcastTextMessage = new TextMessage(objectMapper.writeValueAsString(broadcastMessage));

        StringBuilder strBuilder = new StringBuilder("MessageInfo[" + gameRoom.getSize() + "]");
        strBuilder.append("  MESSAGE: " + message.toString());
        strBuilder.append("  FROM: " + connectionInfo(session));
        strBuilder.append("  TO:");
        gameRoom.forEachOtherPlayer((WebSocketSession playerIdentifier) -> {
            try {
                strBuilder.append(" " + connectionInfo(playerIdentifier));
                playerIdentifier.sendMessage(broadcastTextMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, session);
        logger.log(Level.SEVERE, " " + strBuilder.toString());
    }

    private String connectionInfo(WebSocketSession session) {
        return getHttpSessionId(session) + "(" + session.getId() + ")";
    }

    private String getHttpSessionId(WebSocketSession session) {
        String httpSessionIdKey = HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;
        String sessionId = "Undefined";

        try {
            sessionId = session.getAttributes().get(httpSessionIdKey).toString();
        } catch (Exception e) {}

        return sessionId;
    }

    @ConnectionEstablished
    public void connectionEstablishedHandler(WebSocketSession session) {
        logger.log(Level.SEVERE, "Established...");
        gameRoom.addPlayer(session);
        logRoomInfo();
    }

    @ConnectionClosed
    public void connectionClosedHandler(WebSocketSession session) {
        logger.log(Level.SEVERE, "Closed...");
        gameRoom.removePlayer(session);
        logRoomInfo();
    }

    private void logRoomInfo() {
        StringBuilder strBuilder = new StringBuilder("RoomInfo[" + gameRoom.getSize() + "]");
        gameRoom.forEachPlayer((WebSocketSession playerIdentifier) -> {
            strBuilder.append(" " + connectionInfo(playerIdentifier));
        });
        logger.log(Level.SEVERE, " " + strBuilder.toString());
    }

    @TransportError
    public void transportErrorHandler(WebSocketSession session) {
        logger.log(Level.SEVERE, "TransportError...");
    }
}
