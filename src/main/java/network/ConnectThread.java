package network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.*;

class ConnectThread extends Thread
{
    ServerSocket serverSocket;
    int count = 1;

    ConnectThread (ServerSocket serverSocket)
    {
        this.serverSocket = serverSocket;
    }

    @Override
    public void run ()
    {
        try {
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("    Thread " + count + " is started.");
                ClientThread clientThread = new ClientThread(socket, count);
                clientThread.start();
                count++;
            }
        } catch (IOException e) {
            System.out.println("    SERVER CLOSE    ");
        }
    }
}