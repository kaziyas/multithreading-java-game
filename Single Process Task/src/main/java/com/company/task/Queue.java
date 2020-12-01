package com.company.task;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/*
 * @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
 * @version 1.0 2020.11.28
 * @since 1.0
 */

/**
 * A concurrentHashMap used to simulate a two-way queue to get or put a message simultaneously. It
 * uses a queue is the BlockingQueue that capacity bounded, so at any given time, it has a remaining
 * capacity beyond which no additional messages can be put without blocking in a multi-threaded
 * context.
 */
public class Queue {
  private final Map<String, BlockingQueue<String>> queue = new ConcurrentHashMap<>(2);

  // Keep a Initiator or Partner message in a thread-safe queue
  private synchronized BlockingQueue<String> isQueueAvailable(String key) {
    return queue.computeIfAbsent(key, k -> new ArrayBlockingQueue<>(1));
  }

  // Put a message in queue depends on Initiator's or Partner's key
  public boolean put(String key, String value) {
    BlockingQueue<String> queue = isQueueAvailable(key);
    return queue.offer(value);
  }

  // Get a message in queue depends on Initiator's or Partner's key
  public String get(String key) {
    BlockingQueue<String> queue = isQueueAvailable(key);
    return queue.poll();
  }
}
