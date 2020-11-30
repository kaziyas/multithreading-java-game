package com.company.task;

/*
  @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
 * @version 1.0 2020.11.27
 * @since 1.0
 */
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * There are many ways to communicate 2 JVM processes running on a local machine. Interprocess
 * communication (IPC) have been tailored to different software requirements but Socket-Based
 * networking usually is a good approach. Initiator and Responder in my solution communicate on a
 * same port (4020) and local machine.
 *
 * <p>The Initiator object opens a socket as a server and send 'Hello guys' to the Responder. After
 * that the Responder replies to the message with the number of messages that are already sent. The
 * Initiator read and print the reply message. The messages stored in an array and the Initiator
 * send them sequentially. The send or receive operations depend on the message array length, so you
 * can add or delete the messages in a simple way.
 */
public final class Initiator {
  private ServerSocket serverSocket;
  private Socket connection = null;
  private ObjectOutputStream out;
  private ObjectInputStream in;

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
  private void run() {
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
    while (numberOfReceives < messages.length) {
      try {
        String message = (String) in.readObject();
        numberOfReceives++;
        System.out.println(message);

        if (numberOfCalls < messages.length - 1) {
          numberOfCalls++;
          sendMessage(messages[numberOfCalls]);
        }
      } catch (ClassNotFoundException classNotFoundException) {
        System.err.println("Data received in unknown format");
      }
    }
  }

  private void sendMessage(String message) throws IOException {
    out.writeObject(message);
    out.flush();
  }

  // Setup and running the Initiator
  private void openSocket() throws IOException {
    serverSocket = new ServerSocket(4020, 10);
    System.out.println("Server Status: Waiting for connection");
    connection = serverSocket.accept();
    System.out.println(
        "Server Status: Connection received from " + connection.getInetAddress().getHostName());
    out = new ObjectOutputStream(connection.getOutputStream());
    out.flush();
    in = new ObjectInputStream(connection.getInputStream());
  }

  private void closeSocket() throws IOException {
    in.close();
    out.close();
    serverSocket.close();
  }
}
