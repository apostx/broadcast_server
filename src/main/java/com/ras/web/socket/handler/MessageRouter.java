package com.ras.web.socket.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ras.web.socket.FrameMessage;
import com.ras.web.socket.bind.annotation.ConnectionClosed;
import com.ras.web.socket.bind.annotation.ConnectionEstablished;
import com.ras.web.socket.bind.annotation.JsonMessage;
import com.ras.web.socket.bind.annotation.TransportError;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageRouter extends TextWebSocketHandler {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final List<BindedMethod> connectionEstablishedCallbacks = new ArrayList<>();
    private final List<BindedMethod> connectionClosedCallbacks = new ArrayList<>();
    private final List<BindedMethod> transportErrorCallbacks = new ArrayList<>();
    private final Map<String, List<BindedMethod>> messageCallbacks = new HashMap<>();

    public void addHandler(Object handler) {
        Class<?> messageHandlerClass = handler.getClass();
        Method[] methods = messageHandlerClass.getDeclaredMethods();
        Annotation annotation;
        String messageType;
        List<BindedMethod> callbackList = null;

        for (Method method : methods) {
            if ((annotation = method.getAnnotation(JsonMessage.class)) != null) {
                messageType = ((JsonMessage) annotation).type();
                callbackList = messageCallbacks.get(messageType);

                if (callbackList == null) {
                    callbackList = new ArrayList<>();
                    messageCallbacks.put(messageType, callbackList);
                }
            } else if (method.getAnnotation(ConnectionEstablished.class) != null)
                callbackList = connectionEstablishedCallbacks;

            else if (method.getAnnotation(ConnectionClosed.class) != null)
                callbackList = connectionClosedCallbacks;

            if (method.getAnnotation(TransportError.class) != null)
                callbackList = transportErrorCallbacks;

            if (callbackList != null)
                callbackList.add(new BindedMethod(handler, method));
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        eachMethodInvoke(connectionEstablishedCallbacks, session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        eachMethodInvoke(connectionClosedCallbacks, session);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        eachMethodInvoke(transportErrorCallbacks, session, exception);
    }

    private void eachMethodInvoke(List<BindedMethod> callbackList, Object... args) throws Exception {
        for (BindedMethod method : callbackList)
            method.invoke(args);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        FrameMessage frameMessage = objectMapper.readValue(message.getPayload(), FrameMessage.class);
        List<BindedMethod> callbackList = messageCallbacks.get(frameMessage.getType());
        Object messageData;
        Class<?> messageType;
        boolean isUnhandledMessage = true;
        if (callbackList != null && 0 < callbackList.size())
            for (BindedMethod method : callbackList) {
                isUnhandledMessage = false;
                messageType = method.getParameterTypes()[1];
                messageData = objectMapper.treeToValue(frameMessage.getData(), messageType);
                method.invoke(session, messageData);
            }

        if (isUnhandledMessage) {
            String errorMessage = "Received message type is unhandled: " + frameMessage.getType();
            throw new Exception(errorMessage);
        }
    }

    private class BindedMethod {
        private Object thisObject;
        private Method method;

        private BindedMethod(Object thisObject, Method method) {
            this.thisObject = thisObject;
            this.method = method;
        }

        private Object invoke(Object... obj) throws InvocationTargetException, IllegalAccessException {
            return method.invoke(thisObject, obj);
        }

        private Class<?>[] getParameterTypes() {
            return method.getParameterTypes();
        }
    }
}
