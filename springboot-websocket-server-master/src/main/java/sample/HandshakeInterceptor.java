package sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.HttpSessionHandshakeInterceptor;

import java.util.Map;

public class HandshakeInterceptor extends HttpSessionHandshakeInterceptor {
	private static Logger logger = LoggerFactory.getLogger("xlui");


	@Override
	public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
		logger.info("HandshakeInterceptor: beforeHandshake");
		logger.info("Attributes: " + attributes.toString());
		return super.beforeHandshake(request, response, wsHandler, attributes);
	}
	@Override
	public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception ex) {
		logger.info("HandshakeInterceptor: afterHandshake");
		super.afterHandshake(request, response, wsHandler, ex);
	}
}
