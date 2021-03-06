package com.example.ov.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.example.ov.message.ChatMessage;
import com.example.ov.message.Message;
import com.example.ov.message.Response;

@Controller
public class WebSocketController {
	@Autowired
	SimpMessagingTemplate simpMessagingTemplate;
	private String token = "this is a token generated by your code!";

	// When the client sends a request to the server, map /broadcast to this address via `@MessageMapping`
	@MessageMapping("/broadcast")
	// When the server has a message, it will send a message to the client that subscribes to the path in @SendTo
	@SendTo("/b")
	public Response say(Message message, @Header(value = "authorization") String authorizationToken) {
		if (authorizationToken.equals(token)) {
			System.out.println("Token check success!!!");
		} else {
			System.out.println("Token check failed!!!");
		}
		return new Response("Welcome, " + message.getName() + "!");
	}

	@MessageMapping("/group/{groupID}")
	public void group(@DestinationVariable int groupID, Message message) {
		Response response = new Response("Welcome to group " + groupID + ", " + message.getName() + "!");
		simpMessagingTemplate.convertAndSend("/g/" + groupID, response);
	}

	@MessageMapping("/chat")
	public void chat(ChatMessage chatMessage) {
		Response response = new Response("Receive message from user " + chatMessage.getFromUserID() + ": " + chatMessage.getMessage());
		simpMessagingTemplate.convertAndSendToUser(String.valueOf(chatMessage.getUserID()), "/msg", response);
	}
}
