package com.kaziyas.task;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @author Yaser Kazerooni (yaser.kazerooni@gmail.com)
 * @version 1.0 2020.12.01
 * @since 1.0
 */

/**
 * PlayerType helps a player object to find proper behavior on open and close behaviors. It depends
 * on type of a player, a true method runs in runtime.
 *
 */
public enum PlayerType {
  RESPONDER {
    @Override
    public void openSocket(Player player) throws IOException {
      ServerSocket serverSocket = new ServerSocket(4020, 10);
      player.setServerSocket(serverSocket);
      System.out.println("Server Status: Waiting for connection");
      Socket connection = serverSocket.accept();
      System.out.println(
          "Server Status: Connection received from " + connection.getInetAddress().getHostName());
      player.setOut(connection.getOutputStream());
      player.setIn(connection.getInputStream());
    }

    @Override
    public void closeSocket(Player player) throws IOException {
      player.getServerSocket().close();
    }
  },
  INITIATOR {
    @Override
    public void openSocket(Player player) throws IOException {
      player.setClientSocket(new Socket(InetAddress.getLocalHost(), 4020));
      System.out.println(
          "Connected to "
              + InetAddress.getLocalHost()
              + " on port "
              + player.getClientSocket().getPort());
      player.setIn(player.getClientSocket().getInputStream());
      player.setOut(player.getClientSocket().getOutputStream());
      player.sendMessage("hello");
    }

    @Override
    public void closeSocket(Player player) throws IOException {
      player.getClientSocket().close();
    }
  };

  public abstract void openSocket(Player player) throws IOException;

  public abstract void closeSocket(Player player) throws IOException;
}
