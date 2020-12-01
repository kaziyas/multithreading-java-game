package com.company.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/*
 @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
* @version 1.0 2020.11.30
* @since 1.0
*/

public class ResponderIntegrationTest {
  private Initiator client = new Initiator();
  private String[] messages = {
    "Hello guys :)",
    "My name is yaser!",
    "I'm originally from Iran.",
    "But, I am living in stuttgart now.",
    "I am working as a Java Developer for many years.",
    "I am fan of Java and related concepts.",
    "I worked with monolith applications for many years.",
    "I am working with microservice application now.",
    "Have a good day.",
    "Bye"
  };
  private int numberOfCalls = 0;
  private int numberOfReceives = 0;
  private List<String> receivedMessages = new ArrayList<>();

  @BeforeEach
  public void start() throws InterruptedException, IOException {
    Executors.newSingleThreadExecutor().submit(() -> new Responder().run());
    Thread.sleep(500);
    init();
  }

  public void init() throws IOException {
    client.openSocket();
  }

  public void tearDown() throws IOException {
    client.closeSocket();
  }

  @Test
  public void givenInitiator_whenResponderEchosMessage_thenCorrect() throws IOException {
    client.sendMessage(messages[numberOfCalls]);
    numberOfCalls++;
    while (numberOfReceives < messages.length) {
      try {
        String message = client.readMessage();
        receivedMessages.add(message);
        numberOfReceives++;

        if (numberOfCalls < messages.length) {
          client.sendMessage(messages[numberOfCalls]);
          numberOfCalls++;
        }
      } catch (ClassNotFoundException classNotFoundException) {
        System.err.println("Data received in unknown format");
      }
    }
    tearDown();

    String firstMessage = "Server reply:'HELLO GUYS :)', 0 messages already sent before.";
    assertEquals(10, numberOfCalls);
    assertEquals(10, numberOfReceives);
    assertEquals(10, receivedMessages.size());
    assertEquals(receivedMessages.get(0), firstMessage);
    assertNotEquals(receivedMessages.get(0), firstMessage.toLowerCase());
  }
}
