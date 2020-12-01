package com.company.task;

/*
 @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
* @version 1.0 2020.11.27
* @since 1.0
*/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 * There are many ways to communicate 2 JVM processes running on a local machine. Interprocess
 * communication (IPC) have been tailored to different software requirements but Socket-Based
 * networking usually is a good approach. Initiator and Responder in my solution communicate on a
 * same port (4020) and local machine.
 *
 * <p>The Initiator object opens a socket as a client and send 'Hello guys' to the Responder
 * (server). After that the Responder replies to the message with the number of messages that are
 * already sent. The Initiator read and print the reply message. The messages stored in an array and
 * the Initiator send them sequentially. The send or receive operations depend on the message array
 * length, so you can add or delete the messages in a simple way.
 */
public final class Initiator {
  private Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;

  // Store 10 messages in an array to send them sequentially.
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

  // Keep send or receive messages' count separately
  private int numberOfCalls = 0;
  private int numberOfReceives = 0;

  public static void main(String[] args) {
    Initiator initiator = new Initiator();
    initiator.run();
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

  // Send or receive a message to/from the Responder
  private void processMessage() throws IOException {
    sendMessage(messages[numberOfCalls]);
    while (numberOfReceives < messages.length && numberOfCalls <= messages.length) {
      try {
        System.out.println(readMessage());
        if (numberOfCalls < messages.length) {
          sendMessage(messages[numberOfCalls]);
        }
      } catch (ClassNotFoundException classNotFoundException) {
        System.err.println("Data received in unknown format");
      }
    }
  }

  public final void sendMessage(String message) throws IOException {
    out.writeObject(message);
    out.flush();
    numberOfCalls++;
  }

  public final String readMessage() throws IOException, ClassNotFoundException {
    String message = (String) in.readObject();
    numberOfReceives++;
    return message;
  }

  // Setup and running the Initiator
  public final void openSocket() throws IOException {
    clientSocket = new Socket(InetAddress.getLocalHost(), 4020);
    System.out.println(
        "Connected to" + InetAddress.getLocalHost() + " on port " + clientSocket.getPort());
    in = new ObjectInputStream(clientSocket.getInputStream());
    out = new ObjectOutputStream(clientSocket.getOutputStream());
    out.flush();
  }

  public final void closeSocket() throws IOException {
    in.close();
    out.close();
    clientSocket.close();
  }
}
