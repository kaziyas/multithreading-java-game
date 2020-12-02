package com.company.task;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/*
 * @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
 * @version 1.0 2020.12.01
 * @since 1.0
 */

/**
 * There are many ways to communicate 2 JVM processes running on a local machine. Interprocess
 * communication (IPC) have been tailored to different software requirements but Socket-Based
 * networking usually is a good approach. Initiator (A Player with INITIATOR type) and Responder (A
 * Player with RESPONDER type) in my solution communicate on a same port (4020) and local machine.
 *
 * <p>The Initiator object opens a socket as a client and send 'hello' to the Responder (server).
 * After that the Responder replies to the message with the number of messages that are already
 * sent. The Initiator read and print the reply message and send back to responder with the number
 * of messages that are already sent too.
 */
public final class Player {
  public static final int MAX_MESSAGE_COUNT = 10;
  private final PlayerType playerType;
  private Socket clientSocket;
  private ServerSocket serverSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;

  // Keep send or receive messages' count separately
  private int numberOfCalls = 0;
  private int numberOfReceives = 0;

  public Player(PlayerType playerType) {
    this.playerType = playerType;
  }

  public static void main(String[] args) {
    try {
      String playerType = args[0];
      Player player = new Player(PlayerType.valueOf(playerType));
      player.run();
    } catch (IllegalArgumentException illegalArgumentException) {
      System.out.println(
          "The argument is not valid. Please use INITIATOR or RESPONDER as a argument.");
    } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
      System.out.println("Please send a argument: INITIATOR or RESPONDER");
    }
  }

  // Run the related methods to open, process, and close the communication.
  public final void run() {
    try {
      openSocket();
      processMessage();
    } catch (IOException ioException) {
      ioException.printStackTrace();
    } finally {
      try {
        closeSocket();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      }
    }
  }

  // Send or receive a message to/from a player
  private void processMessage() throws IOException {
    String message;
    while (numberOfReceives < MAX_MESSAGE_COUNT && numberOfCalls <= MAX_MESSAGE_COUNT) {
      try {
        message = readMessage();
        if (numberOfCalls < MAX_MESSAGE_COUNT) {
          sendMessage(message.concat(String.valueOf(numberOfCalls)));
        }
      } catch (ClassNotFoundException classNotFoundException) {
        System.err.println("Data received in unknown format");
      }
    }
  }

  // Setup and running a player
  public final void openSocket() throws IOException {
    playerType.openSocket(this);
  }

  public final void closeSocket() throws IOException {
    in.close();
    out.close();
    playerType.closeSocket(this);
  }

  public final void sendMessage(String message) throws IOException {
    out.writeObject(message);
    out.flush();
    numberOfCalls++;
  }

  public final String readMessage() throws IOException, ClassNotFoundException {
    String message = (String) in.readObject();
    System.out.println(message);
    numberOfReceives++;
    return message;
  }

  public final int getNumberOfCalls() {
    return numberOfCalls;
  }

  public final int getNumberOfReceives() {
    return numberOfReceives;
  }

  public final ServerSocket getServerSocket() {
    return serverSocket;
  }

  public final void setServerSocket(ServerSocket serverSocket) {
    this.serverSocket = serverSocket;
  }

  public final Socket getClientSocket() {
    return clientSocket;
  }

  public final void setClientSocket(Socket clientSocket) {
    this.clientSocket = clientSocket;
  }

  public final void setIn(InputStream in) throws IOException {
    this.in = new ObjectInputStream(in);
  }

  public final void setOut(OutputStream out) throws IOException {
    this.out = new ObjectOutputStream(out);
    out.flush();
  }
}
