package com.company.task;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
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
 * queue too. This queue is a BlockingQueue that capacity bounded, so at any given time, it has a
 * remaining capacity beyond which no additional messages can be put without blocking in a
 * multi-threaded context. An AtomicInteger perform add or get operations on an int value without
 * using synchronized keyword in a multi-threaded context, so the number of calls and receives
 * messages keep in two separate fields. The send or receive operations depend on the message array
 * length, so you can add or delete the messages in a simple way.
 */
public final class Player implements Runnable {
  private final String name;
  private final String partner;
  private final boolean initiator;
  private final Map<String, BlockingQueue<String>> queue;
  private String[] messages = {
    "Hello guys :)",
    "My name is yaser!",
    "I'm originally from Iran.",
    "But, I am living in stuttgart now.",
    "I am working as a Java Developer for many years.",
    "I am fan of Java and related concepts.",
    "I worked with monolith applications for many years.",
    "I am working on microservice application now.",
    "Have a good day.",
    "Bye"
  };

  // Keep send or receive messages' count separately
  private AtomicInteger numberOfCalls = new AtomicInteger(0);
  private AtomicInteger numberOfReceives = new AtomicInteger(0);

  public Player(
      String name, String partner, boolean initiator, Map<String, BlockingQueue<String>> queue) {
    this.name = name;
    this.partner = partner;
    this.initiator = initiator;
    this.queue = queue;
  }

  // Keep a Initiator or Partner message in a thread-safe queue
  private synchronized BlockingQueue<String> isQueueAvailable(String key) {
    return queue.computeIfAbsent(key, k -> new ArrayBlockingQueue<>(1));
  }

  // Put a message in queue depends on Initiator's or Partner's key
  private boolean put(String key, String value) {
    BlockingQueue<String> queue = isQueueAvailable(key);
    return queue.offer(value);
  }

  // Get a message in queue depends on Initiator's or Partner's key
  private String get(String key) {
    BlockingQueue<String> queue = isQueueAvailable(key);
    return queue.poll();
  }

  //  Get the Initiator last message from own queue and put a new message in the partner's
  // queue. The Partner put a message into the Initiator's queue to reply back
  @Override
  public void run() {
    String receivedMessage;
    int callsCounter = 0;
    int receivesCounter = 0;

    init();
    try {
      while (callsCounter < messages.length && receivesCounter < messages.length) {
        if ((receivedMessage = get(name)) != null) {
          callsCounter = incrementAndGetCalls();
          if (initiator) {
            System.out.println(receivedMessage);
            if (callsCounter < messages.length) {
              String message = messages[callsCounter];
              put(partner, message);
              System.out.println(Thread.currentThread().getName() + " send a message: " + message);
            }
          } else {
            put(
                partner,
                Thread.currentThread().getName()
                    + " : Client reply:"
                    + "'"
                    + receivedMessage.toUpperCase()
                    + "', "
                    + receivesCounter
                    + " messages already sent before.");
          }
          receivesCounter = incrementAndGetReceives();
        }
      }
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
    }
  }

  // Send the Initiator's first message
  private void init() {
    if (initiator) {
      String message = messages[0];
      put(partner, message);
      System.out.println(Thread.currentThread().getName() + " send a message: " + message);
    }
  }

  private int incrementAndGetCalls() throws InterruptedException {
    final int counter = numberOfCalls.incrementAndGet();
    Thread.sleep(200);
    return counter;
  }

  private int incrementAndGetReceives() throws InterruptedException {
    final int counter = numberOfReceives.incrementAndGet();
    Thread.sleep(200);
    return counter;
  }
}
