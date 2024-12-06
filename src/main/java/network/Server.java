package network;

// dto를 안넣어놓았습니다. 
// 나중에 연결할 때 필요하면 넣으세요.

import java.io.*;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static final String IP = "127.0.0.1"; //IP
    private static final int PORT = 5000; //포트 번호

    private ServerSocket serverSocket; // 서버 소켓
    private ExecutorService threadPool; // 스레드 풀

    public Server(){
        System.out.println("Server ready...");
    }
    //서버 소켓 시작
    public void run(){
        try {
            //서버 소켓 생성 및 초기화 / 클라이언트 연결 기다림.
            serverSocket = new ServerSocket(PORT, 50, InetAddress.getByName(IP));
            threadPool = Executors.newFixedThreadPool(10); //최대 10개의 클라이언트 처리 가능

            System.out.println("System is running at " + IP + " : " + PORT);

            while(true){
                //클라이언트 접속 대기 / 클라이언트 연결 기다림
                Socket clientSocket = serverSocket.accept(); //클라이언트 연결 요청 수
                System.out.println("New client connected: " + clientSocket.getInetAddress().getHostAddress());

                // 클라이언트 처리 스레드 실행
                threadPool.execute(new ClientHandler(clientSocket)); // 각 클라이언트마다 처리할 스레드를 실행
            }
        } catch (IOException e) {
            System.err.println("Server error: " + e.getMessage()); // 예외 발생 시 서버 오류 메시지 출력
        } finally {
            stop(); //서버 종료시 호
        }
    }

    // 서버 중지
    public void stop() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close(); // 서버 소켓을 닫음
            }
            if (threadPool != null && !threadPool.isShutdown()) {
                threadPool.shutdown(); // 스레드 풀 종료
            }
            System.out.println("Server stopped.");
        } catch (IOException e) {
            System.err.println("Error stopping server: " + e.getMessage()); // 종료 중 오류 발생 시 메시지 출력
        }
    }

    // 클라이언트 요청을 처리하는 내부 클래스
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String message;

                // 클라이언트로부터 메시지 수신 및 처리
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);

                    // 메시지에 따른 처리 로직 (학생, 관리자 구분)
                    if (message.startsWith("STUDENT")) {
                        handleStudentRequest(message, out);
                    } else if (message.startsWith("MANAGER")) {
                        handleManagerRequest(message, out);
                    } else {
                        out.println("UNKNOWN COMMAND");
                    }
                }
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage());
                }
            }
        }

        private void handleStudentRequest(String message, PrintWriter out) {
            // 학생 요청 처리 로직 구현
            out.println("Student request received: " + message);
        }

        private void handleManagerRequest(String message, PrintWriter out) {
            // 관리자 요청 처리 로직 구현
            out.println("Manager request received: " + message);
        }
    }
}
