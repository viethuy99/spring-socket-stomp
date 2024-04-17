package com.example.socketclient;

import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import org.springframework.web.socket.sockjs.frame.Jackson2SockJsMessageCodec;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;


@Log4j2
public class StompClientExample {


    private final static WebSocketHttpHeaders headers = new WebSocketHttpHeaders();

    public ListenableFuture<StompSession> connect() {

        Transport webSocketTransport = new WebSocketTransport(new StandardWebSocketClient());
        List<Transport> transports = Collections.singletonList(webSocketTransport);

        SockJsClient sockJsClient = new SockJsClient(transports);
        sockJsClient.setMessageCodec(new Jackson2SockJsMessageCodec());

        WebSocketStompClient stompClient = new WebSocketStompClient(sockJsClient);

        String url = "ws://{host}:{port}/ws";
        return stompClient.connect(url, headers, new MyHandler(), "localhost", 8080);
    }

    public void subscribeGreetings(StompSession stompSession) throws ExecutionException, InterruptedException {
        stompSession.subscribe("/all/messages", new StompFrameHandler() {

            public Type getPayloadType(StompHeaders stompHeaders) {
                return byte[].class;
            }

            public void handleFrame(StompHeaders stompHeaders, Object o) {
                log.info("Received greeting " + new String((byte[]) o));
            }
        });
    }

    public void sendHello(StompSession stompSession, Integer num) {
        String jsonHello = "{\n" +
                "\t\"text\": \"" + num + "\"" +
                "}";
        stompSession.send("/app/all", jsonHello.getBytes());
    }

    public void sendUser(StompSession stompSession, Integer num, String user) {
        String jsonHello = "{\n" +
                "\t\"text\": \"" + num + "\",\n" +
                "\t\"to\": \""+ user +"\"\n" +
                "}";
        stompSession.send("/app/private", jsonHello.getBytes());
    }

    private class MyHandler extends StompSessionHandlerAdapter {
        public void afterConnected(StompSession stompSession, StompHeaders stompHeaders) {
            log.info("Now connected");
        }
    }

    public static void main(String[] args) throws Exception {
        StompClientExample helloClient = new StompClientExample();

        ListenableFuture<StompSession> f = helloClient.connect();
        StompSession stompSession = f.get();

        log.info("Subscribing to greeting topic using session " + stompSession);
        helloClient.subscribeGreetings(stompSession);

        log.info("Sending hello message" + stompSession);
        for (int i = 0; i < 10; i ++) {
            Thread.sleep(5000);
            if (i % 2 == 0) {
                helloClient.sendHello(stompSession, i);
            }
            else {
                helloClient.sendUser(stompSession, i, "1001");
            }
        }
    }
}
