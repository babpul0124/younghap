package network;

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {
    private static final String IP = "127.0.0.1"; // 서버 IP
    private static final int PORT = 5000; // 서버 포트 번호
    private Socket clientSocket; // 클라이언트 소켓
    private ExecutorService threadPool; // 스레드 풀

    public Client() {
        System.out.println("Client ready...");
    }

    // 서버와 연결을 시작하고 메시지를 처리하는 메소드
    public void run() {
        try {
            clientSocket = new Socket(IP, PORT); // 서버와 연결
            threadPool = Executors.newFixedThreadPool(10); // 최대 10개의 스레드로 클라이언트 처리 가능
            System.out.println("Connected to server at " + IP + ":" + PORT);

            // 서버로 메시지를 전송하고 서버의 응답을 처리하는 클라이언트 요청 스레드 실행
            threadPool.execute(new ServerCommunicationHandler(clientSocket));

        } catch (IOException e) {
            System.err.println("Error while connecting to the server: " + e.getMessage());
        }
    }

    // 클라이언트 종료
    public void stop() {
        try {
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown();
            }
            System.out.println("Client stopped.");
        } catch (IOException e) {
            System.err.println("Error while stopping the client: " + e.getMessage());
        }
    }

    // 서버와의 통신을 담당하는 내부 클래스
    private static class ServerCommunicationHandler implements Runnable {
        private Socket clientSocket; // 클라이언트 소켓

        // 생성자에서 클라이언트 소켓을 받음
        public ServerCommunicationHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String message;
                BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

                // 사용자로부터 메시지를 입력받아 서버로 전송
                while (true) {
                    System.out.print("Enter message to send to server (type 'exit' to quit): ");
                    message = userInput.readLine();

                    if ("exit".equalsIgnoreCase(message)) {
                        System.out.println("Exiting client...");
                        break; // 서버로 메시지 전송 후 종료
                    }

                    out.println(message); // 메시지 서버로 전송

                    // 서버로부터 응답을 받으면 출력
                    String serverResponse = in.readLine();
                    System.out.println("Server response: " + serverResponse);
                }
            } catch (IOException e) {
                System.err.println("Error during server communication: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close(); // 클라이언트 소켓 종료
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }
    }
}
