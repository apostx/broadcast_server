package com.ras.broadcastserver.ws.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ras.broadcastserver.ws.game.Room;
import com.ras.broadcastserver.ws.handler.ServerMessageHandler;
import com.ras.web.socket.handler.MessageRouter;
import com.ras.web.socket.handler.MessageRouterDebugDecorator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        MessageRouter jsonMessageRouter = this.jsonMessageRouter();

        registry.addHandler(jsonMessageRouter, "/ws")
                .addInterceptors(httpSessionHandshakeInterceptor())
                .setAllowedOrigins("*");

        jsonMessageRouter.addHandler(serverMessageHandler());
    }

    @Bean
    public MessageRouter jsonMessageRouter() {
        return new MessageRouterDebugDecorator(new MessageRouter());
    }

    @Bean
    public Object serverMessageHandler() {
        return new ServerMessageHandler();
    }

    @Bean
    public HandshakeInterceptor httpSessionHandshakeInterceptor() {
        return new HttpSessionHandshakeInterceptor();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public Room<WebSocketSession> gameRoom() {
        return new Room<>();
    }
}
