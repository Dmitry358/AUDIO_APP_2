package com.mitiok.audio_app_2;

import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SignalHandler extends TextWebSocketHandler {

  private final List<WebSocketSession> sessions = new CopyOnWriteArrayList<>();

  private final Map<String, Integer> userNumbers =
    new ConcurrentHashMap<>();

  private final AtomicInteger counter = new AtomicInteger(0);

  @Override
  public void afterConnectionEstablished(WebSocketSession session) throws Exception {
    sessions.add(session);
    System.out.println("New connection: " + session.getId());

    int userNumber = counter.incrementAndGet();
    userNumbers.put(session.getId(), userNumber);

    System.out.println(
      "NEW USER CONNECTED | sessionId=" + session.getId()
        + " | userNumber=" + userNumber
    );

    // 📤 invia il numero al browser
    session.sendMessage(new TextMessage(
      "{\"type\":\"your-id\",\"userNumber\":" + userNumber + "}"
    ));
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

    String payload = message.getPayload();

    // 🧾 log bottone CALL
    if (payload.contains("\"type\":\"call-pressed\"")) {
      System.out.println(
        "CALL BUTTON PRESSED | userNumber="
          + userNumbers.get(session.getId())
      );
    }

    // Пересылаем сообщение всем остальным сессиям
    for (WebSocketSession s : sessions) {
      if (s.isOpen() && s != session) {
        s.sendMessage(message);
      }
    }
  }

  @Override
  public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
    sessions.remove(session);
    System.out.println("Connection closed: " + session.getId());

    userNumbers.remove(session.getId());

    System.out.println(
      "USER DISCONNECTED | sessionId=" + session.getId()
    );
  }
}
