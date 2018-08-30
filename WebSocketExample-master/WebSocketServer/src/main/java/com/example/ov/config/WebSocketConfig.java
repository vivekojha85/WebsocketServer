package com.example.ov.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@Configuration
//Enable Websocket's message broker
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	// Register the node of the STOMP protocol (Endpoint) and map it to the specified URL
	// We use STOMP, so there is no need to implement WebSocketHandler.
	// The purpose of implementing WebSocketHandler is to receive and process messages, and STOMP has done this for us.
	@Override
	public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
		// Register the node of the STOMP protocol and specify the SockJS protocol
		stompEndpointRegistry.addEndpoint("/im").addInterceptors(new HandshakeInterceptor()).withSockJS();
	}

	// Configure the use of message broker	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		// Unified configuration of the message broker, the message broker is the subscription point,
		//and the client accepts the message by subscribing to the message proxy point
		registry.enableSimpleBroker("/b", "/g", "/user");

		// Configure the prefix of the peer-to-peer message
		registry.setUserDestinationPrefix("/user");
	}
}
