package com.ras.web.socket.handler;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageRouterDebugDecorator extends MessageRouter {
    private MessageRouter instance;
    private Logger logger;

    public MessageRouterDebugDecorator(MessageRouter instance) {
        this.instance = instance;
        logger = Logger.getAnonymousLogger();
    }

    @Override
    public void addHandler(Object handler) {
        instance.addHandler(handler);
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        errorHandler(() -> instance.afterConnectionEstablished(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        errorHandler(() -> instance.afterConnectionClosed(session, status));
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        errorHandler(() -> instance.handleTransportError(session, exception));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        errorHandler(() -> instance.handleTextMessage(session, message));
    }

    private void errorHandler(IThrowableRunnable callback) throws Exception {
        try {
            callback.run();
        } catch (Exception e) {
            synchronized (logger) {
                logger.log(Level.WARNING, e.getMessage());
                throw e;
            }
        }
    }

    private interface IThrowableRunnable {
        void run() throws Exception;
    }
}
