package duelcard.ws.handler;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;

public class EchoMessageHandler extends TextWebSocketHandler {

    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        session.sendMessage(message);
    }
}
