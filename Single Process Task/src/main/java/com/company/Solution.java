package com.company;

import com.company.task.Player;
import com.company.task.Queue;

/*
 @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
* @version 1.0 2020.11.27
* @since 1.0
*/

/**
 * This solution tries to use pure java concurrency statements to resolve the challenge. It uses two
 * separate thread as the players. A concurrentHashMap used to simulate a two-way queue to get or
 * put a message simultaneously.
 */
public class Solution {
  private static final String PLAYER_1 = "Initiator";
  private static final String PLAYER_2 = "Partner";

  // Run app into a single process and create two separate threads that send or reply a
  // massage.
  public static void main(String[] args) {
    Queue queue = new Queue();
    new Thread(new Player(PLAYER_1, PLAYER_2, true, queue)).start();
    new Thread(new Player(PLAYER_2, PLAYER_1, false, queue)).start();
  }
}
