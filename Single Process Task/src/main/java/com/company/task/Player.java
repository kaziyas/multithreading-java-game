package com.company.task;

import java.util.concurrent.atomic.AtomicInteger;

/*
 @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
* @version 1.0 2020.11.27
* @since 1.0
*/

/**
 * A Player is guaranteed to behave sensibly when the object is being operated on via two threads.
 * Initiator and Partner use a separate queue to manage a send and receive operations. The Initiator
 * object uses an array of messages to read the message sequentially and puts it in the Partner
 * queue. The Partner object get a message and reply it to the Initiator; It uses the Initiator
 * queue too. An AtomicInteger perform add or get operations on an int value without using
 * synchronized keyword in a multi-threaded context, so the number of calls and receives messages
 * keep in two separate fields. The send or receive operations depend on the message array length,
 * so you can add or delete the messages in a simple way.
 */
public final class Player implements Runnable {
  private final static int MAX_MESSAGE_COUNT = 10;

  private final String name;
  private final String partner;
  private final boolean initiator;
  private final Queue queue;
  private String message = "hello";

  // Keep send or receive messages' count separately
  private AtomicInteger numberOfCalls = new AtomicInteger(0);
  private AtomicInteger numberOfReceives = new AtomicInteger(0);

  public Player(String name, String partner, boolean initiator, Queue queue) {
    this.name = name;
    this.partner = partner;
    this.initiator = initiator;
    this.queue = queue;
  }

  // Get the Initiator last message from own queue and put a new message in the partner's
  // queue. The Partner put a message into the Initiator's queue to reply back
  @Override
  public void run() {
    String receivedMessage;

    init();
    try {
      // The last partner's message sent when the number Of calls equal to messages' length
      while (numberOfCalls.intValue() <= MAX_MESSAGE_COUNT
          && numberOfReceives.intValue() < MAX_MESSAGE_COUNT) {
        if ((receivedMessage = queue.get(name)) != null) {
          System.out.println(receivedMessage);
          if (initiator) {
            // The last partner's message touches in this line
            if (numberOfCalls.intValue() < MAX_MESSAGE_COUNT) {
              message = receivedMessage.concat(String.valueOf(numberOfCalls.intValue()));
            }
          } else {
            message = receivedMessage.concat(String.valueOf(numberOfReceives.intValue()));
          }
          queue.put(partner, message);
          incrementCalls();
          incrementReceives();
        }
      }
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
    }
  }

  // Send the Initiator's first message
  private void init() {
    if (initiator) {
      queue.put(partner, message);
      numberOfCalls.getAndIncrement();
    }
  }

  private void incrementCalls() throws InterruptedException {
    numberOfCalls.incrementAndGet();
    Thread.sleep(200);
  }

  private void incrementReceives() throws InterruptedException {
    numberOfReceives.incrementAndGet();
    Thread.sleep(200);
  }
}
