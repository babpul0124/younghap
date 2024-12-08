package network;

import com.mysql.cj.protocol.Protocol;
import service.*;
import persistence.dto.*;
import persistence.dao.*;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private DataInputStream dis;
    private DataOutputStream dos;

    private InputStream is;
    private OutputStream os;
    private BufferedReader br;
    private BufferedWriter bw;

    private final int BUF_SIZE = 1024;
    private byte[] readBuf = new byte[BUF_SIZE];
    private Protocol send_protocol;

    private final Socket clientSocket;
    private final UserService userService;
    private final Viewer viewer;

    public ClientThread(Socket clientSocket, UserService userService, Viewer viewer) {
        this.clientSocket = clientSocket;
        this.userService = userService;
        this.viewer = viewer;  // Viewer 초기화
    }

    @Override
    public void run() {
        try (
                InputStream is = clientSocket.getInputStream();
                OutputStream os = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                PrintWriter writer = new PrintWriter(os, true)
        ) {
            writer.println("클라이언트에 연결되었습니다.");

            while (true) {
                writer.println("1. 로그인\n2. 종료");
                writer.print("선택: ");
                int choice = Integer.parseInt(reader.readLine());

                if (choice == 1) {
                    handleLogin(reader, writer);  // 로그인 처리
                } else if (choice == 2) {
                    writer.println("프로그램을 종료합니다.");
                    clientSocket.close();
                    break;
                } else {
                    writer.println("잘못된 선택입니다. 다시 시도하세요.");
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("클라이언트 통신 오류: " + e.getMessage());
        }
    }

    private void handleLogin(BufferedReader reader, PrintWriter writer) throws IOException {
        // Viewer의 loginScreen() 메서드 사용
        UserDTO userInfo = viewer.loginScreen(reader);

        String loginId = userInfo.getLoginId();
        String password = userInfo.getPassword();

        // 로그인 인증
        String role = userService.validateUser(loginId, password);

        if ("학생".equals(role)) {
            handleStudentActions(reader, writer);
        } else if ("관리자".equals(role)) {
            handleManagerActions(reader, writer);
        } else {
            writer.println("로그인 실패: 아이디 또는 비밀번호가 잘못되었습니다.");
        }
    }

    private void handleStudentActions(BufferedReader reader, PrintWriter writer) throws IOException {

    }

    private void handleManagerActions(BufferedReader reader, PrintWriter writer) throws IOException {

    }
}
