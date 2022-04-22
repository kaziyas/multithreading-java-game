package com.company.task;

import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

/*
 @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
* @version 1.0 2020.11.30
* @since 1.0
*/

public class ReadAllMessagesTest {

  public static void runResponder() {
    // Run Responder
    Player server = new Player(PlayerType.RESPONDER);
    server.run();
  }

  public static void runInitiator() {
    // Run Initiator
    Player server = new Player(PlayerType.INITIATOR);
    server.run();
  }

  @Test
  public void givenResponderAndInitiator_whenInitiatorSendsAndResponderReceivesData_thenCorrect() {
    // Run Responder in new thread
    Runnable runnable1 =
        ReadAllMessagesTest::runResponder;
    Thread thread1 = new Thread(runnable1);
    thread1.start();
    // Wait for 2 seconds
    try {
      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    // Run Initiator in a new thread
    Runnable runnable2 =
        ReadAllMessagesTest::runInitiator;
    Thread thread2 = new Thread(runnable2);
    thread2.start();

    try {
      TimeUnit.SECONDS.sleep(2);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
