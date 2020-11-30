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
 * The Responder object opens a socket as a client, also it replies to a message with the number of
 * messages that are already sent to the Initiator before.
 */
public final class Responder {
  private String message;
  private Socket clientSocket;
  private ObjectInputStream in;
  private ObjectOutputStream out;

  // Keep receive messages' count
  private int numberOfReceives = 0;

  public static void main(String[] args) {
    Responder responder = new Responder();
    responder.run();
  }

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

  // Send or receive a message to/from the Initiator
  private void processMessage() throws IOException {
    do {
      try {
        message = (String) in.readObject();
        sendMessage(
            "Client reply:"
                + "'"
                + message.toUpperCase()
                + "', "
                + numberOfReceives
                + " messages already sent before.");
      } catch (ClassNotFoundException classNot) {
        System.err.println("data received in unknown format");
      }
      numberOfReceives++;
    } while (!message.equals("Bye"));
  }

  // Send a message to the Initiator
  private void sendMessage(String message) throws IOException {
    out.writeObject(message);
    out.flush();
  }

  // Setup and running the Responder
  private void openSocket() throws IOException {
    clientSocket = new Socket(InetAddress.getLocalHost(), 4020);
    System.out.println(
        "Connected to" + InetAddress.getLocalHost() + "in port " + clientSocket.getPort());
    in = new ObjectInputStream(clientSocket.getInputStream());
    out = new ObjectOutputStream(clientSocket.getOutputStream());
    out.flush();
  }

  private void closeSocket() throws IOException {
    in.close();
    out.close();
    clientSocket.close();
  }
}
