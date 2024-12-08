package network;

import persistence.dto.*;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/*
 * 서버 소켓과 클라이언트 소켓을 같은 포트로 연결
 * 클라이언트가 접속할 때까지 lock (accpet() 이용)
 * 클라이언트 접속 확인
 * Thread를 생성 해 서브 소켓과 연결, 서버 소켓과의 연결은 끊음
 * 클라이언트와 소켓에 각각 Buffer를 달아 줌
 * 서버 소켓은 또 다른 클라이언트의 통신을 받을 수 있게함
 * */


//port : 5000
public class Server {
    private static final String IP = "127.0.0.1";
    //private static final String IP = "192.168.0.7";
    private static final int PORT = 5000;
    private ServerSocket serverSocket;
    private Socket socket;
    private BufferedReader br;

    public Server() {
        System.out.println("Server ready...");
    }

    public void run() {
        try {
            InetAddress ir = InetAddress.getByName(IP);
            Scanner sc = new Scanner(System.in);
            /*backlog
             * queue에 size
             * backlog가 10이고 한 번에 처리할 수 있는 양(Thread)가 3일 경우
             * 10개의 Client로 부터 요청이 들어올 경우
             * 1, 2, 3은 연결이 되어 서버와 데이터를 주고 받을 수 있음
             * 4, 5, 6, 7, 8의 경우 연결은 되었지만 서버와 데이터를 주고 받을 수 없음
             * 9, 10의 경우 Client는 게속 SYNC, Server는 계속 패킷을 Drop
             * */
            serverSocket = new ServerSocket(PORT, 50, ir);

            ConnectThread connectThread = new ConnectThread(serverSocket);
            connectThread.start();

            int temp = sc.nextInt();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

/*package network;

// dto를 안넣어놓았습니다. 
// 나중에 한 번에 할 때 연결할 때 필요하면 넣으세요.
// import persistence.dto.*;

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
// ------------------------------------------------------------
//밑의 코드들이 없어도 서버와 클라이언트의 연결은 정상적으로 이루어진다.
// ClientHandler가 없으면 클라이언트의 요청을 처리할 수 없기 때문에 연결은 되지만 상호작용이 불가능.
// 클라이언트와의 상호작용 및 처리 로직이 필요 없다면 ClientHandler 없이 코드를 사용해도 무관하다.
// 그러니, 클라이언트로부터 상호작용이 필요하거나 그에 따른 처리가 필요할 경우는 밑의 코드를 사용하고, 아니면 지우면 됩니더.
// 일단, 밑의 코드들을 만든 이유는 클라이언트도 상호작용이 필요하겠다고 생각을 해서 만든 것이다.
// 클라이언트와 서버 간의 데이터 교환 또는 커뮤니케이션이 네트워크를 통한 요청-응답 패턴 때문에 필요한 줄 알았다.(상호작용이.)
    
    // 클라이언트 요청을 처리하는 내부 클래스
    private static class ClientHandler implements Runnable {
        private Socket clientSocket;

        // 생성자에서 클라이언트 소켓을 받아옴
        public ClientHandler(Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run() {
            try (
                    // 클라이언트로부터 데이터를 읽을 BufferedReader와 응답할 PrintWriter 생성
                    BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
            ) {
                String message;

                // 클라이언트로부터 메시지 수신 및 처리
                while ((message = in.readLine()) != null) {
                    System.out.println("Received: " + message);

                    // 메시지에 따른 처리 로직 (학생, 관리자 구분)
                    if (message.startsWith("STUDENT")) {
                        handleStudentRequest(message, out); // 학생 요청 처리
                    } else if (message.startsWith("MANAGER")) {
                        handleManagerRequest(message, out); // 관리 요청 처리
                    } else {
                        out.println("UNKNOWN COMMAND"); // 알 수 없는 명령어 처리
                    }
                }
            } catch (IOException e) {
                System.err.println("Client handler error: " + e.getMessage());
            } finally {
                try {
                    clientSocket.close(); // 클라이언트와의 연결 종료
                } catch (IOException e) {
                    System.err.println("Error closing client socket: " + e.getMessage()); // 클라이언트 소켓 종료 시 오류 발생 시 메시지 출력
                }
            }
        }

        private void handleStudentRequest(String message, PrintWriter out) {
            // 학생 요청 처리 로직 구현
            out.println("Student request received: " + message); // 학생 요청을 받았음을 클라이언트에게 응답
        }

        private void handleManagerRequest(String message, PrintWriter out) {
            // 관리자 요청 처리 로직 구현
            out.println("Manager request received: " + message); // 관리자 요청을 받았음을 클라이언트에게 응답
        }
    }
}
*/