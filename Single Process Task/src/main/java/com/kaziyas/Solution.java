package com.kaziyas;

import com.kaziyas.task.Player;
import com.kaziyas.task.Queue;

/*
 @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
* @version 1.0 2020.11.27
* @since 1.0
*/

/**
 * This solution tries to use pure java concurrency statements to resolve the task. It uses two
 * separate threads as a player.
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
