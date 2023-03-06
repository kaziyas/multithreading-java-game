package com.kaziyas.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
  private Player client = new Player(PlayerType.INITIATOR);
  private List<String> receivedMessages = new ArrayList<>();

  @BeforeEach
  public void start() throws InterruptedException, IOException {
    Executors.newSingleThreadExecutor().submit(() -> new Player(PlayerType.RESPONDER).run());
    Thread.sleep(500);
    init();
  }

  public void init() throws IOException {
    client.openSocket();
    receivedMessages.add("hello");
  }

  public void tearDown() throws IOException {
    client.closeSocket();
  }

  @Test
  public void givenInitiator_whenResponderEchosMessage_thenCorrect() throws IOException {
    String message;
    while (client.getNumberOfReceives() < Player.MAX_MESSAGE_COUNT
        && client.getNumberOfCalls() <= Player.MAX_MESSAGE_COUNT) {
      try {
        message = client.readMessage();
        receivedMessages.add(message);
        if (client.getNumberOfCalls() < Player.MAX_MESSAGE_COUNT) {
          message = message.concat(String.valueOf(client.getNumberOfCalls()));
          client.sendMessage(message);
          receivedMessages.add(message);
        }
      } catch (ClassNotFoundException classNotFoundException) {
        System.err.println("Data received in unknown format");
      }
    }
    tearDown();

    List<String> outputList = generateOutput();
    assertEquals(10, client.getNumberOfCalls());
    assertEquals(10, client.getNumberOfReceives());
    assertEquals(20, receivedMessages.size());
    assertEquals(outputList, receivedMessages);
    assertEquals(outputList.get(7), receivedMessages.get(7));
    assertEquals(outputList.get(13), receivedMessages.get(13));
    assertEquals(outputList.get(19), receivedMessages.get(19));
  }

  private List<String> generateOutput() {
    List<String> outputList = new ArrayList<>(20);
    outputList.add("hello");
    outputList.add("hello0");
    for (int i = 1; i <= 9; i++) {
      for (int j = 1; j <= 2; j++) {
        int lastIndex = outputList.size() - 1;
        outputList.add(outputList.get(lastIndex).concat(String.valueOf(i)));
      }
    }
    return outputList;
  }
}
