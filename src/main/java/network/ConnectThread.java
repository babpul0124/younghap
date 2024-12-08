package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;

class ConnectThread extends Thread
{
  ServerSocket serverSocket;

  ConnectThread (ServerSocket serverSocket)
  {
    this.serverSocket = serverSocket;
  }

  @Override
  public void run ()
  {
    try
    {
      while (true)
      {
        Socket socket = serverSocket.accept();
        ClientThread clientThread = new ClientThread(socket);
        clientThread.start();
      }
    } catch (IOException e)
    {
      System.out.println("    SERVER CLOSE    ");
    }
  }
}