package com.ras.broadcastserver.ws.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Room<T> {

    private Logger logger = Logger.getAnonymousLogger();

    private List<T> _playerList = Collections.synchronizedList(new ArrayList<T>());

    public void addPlayer(T playerIdentifier) {
        _playerList.add(playerIdentifier);
    }

    public void removePlayer(T playerSession) {
        _playerList.remove(playerSession);
    }

    public void forEachPlayer(Consumer<T> callback) {
        synchronized (_playerList) {
            for (T playerIdentifier : _playerList)
                callback.accept(playerIdentifier);
        }
    }

    public void forEachOtherPlayer(Consumer<T> callback, T playerIdentifier) {
        forEachPlayer((T currentPlayerIdentifier) -> {
            if (!currentPlayerIdentifier.equals(playerIdentifier))
                callback.accept(currentPlayerIdentifier);
        });
    }

    public int getSize() {
        int size = 0;
        synchronized (_playerList) {
            size = _playerList.size();
        }
        return size;
    }

    /*public void broadcastMessage(WebSocketSession senderPlayerSession, TextMessage message) throws IOException {
        synchronized (_playerList) {
            StringBuilder strBuilder = new StringBuilder("MessageInfo[" + _playerList.size() + "]");
            strBuilder.append("  MESSAGE: " + message.getPayload());
            strBuilder.append("  FROM: " + connectionInfo(senderPlayerSession));
            strBuilder.append("  TO:");

            for (T playerSession : _playerList)
                if (!senderPlayerSession.equals(playerSession)) {
                    strBuilder.append(" " + connectionInfo(playerSession));
                    playerSession.sendMessage(message);
                }

            logger.log(Level.SEVERE, " " + strBuilder.toString());
        }
    }*/

    /*private String getHttpSessionId(WebSocketSession session) {
        String httpSessionIdKey = HttpSessionHandshakeInterceptor.HTTP_SESSION_ID_ATTR_NAME;
        return session.getAttributes().get(httpSessionIdKey).toString();
    }*/

    /*private void logRoomInfo() {
        StringBuilder strBuilder = new StringBuilder("RoomInfo[" + _playerList.size() + "]");

        for (WebSocketSession session : _playerList)
            strBuilder.append(" " + connectionInfo(session));

        logger.log(Level.SEVERE, " " + strBuilder.toString());
    }

    private String connectionInfo(WebSocketSession session) {
        return getHttpSessionId(session)+ "(" + session.getId() +")";
    }*/
}
