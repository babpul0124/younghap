package network;

import service.*;
import persistence.dto.*;
import persistence.dao.*;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {
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
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                PrintWriter writer = new PrintWriter(output, true)
        ) {
            writer.println("클라이언트에 연결되었습니다.");

            while (true) {
                writer.println("1. 로그인\n2. 종료");
                writer.print("선택: ");
                int choice = Integer.parseInt(reader.readLine());

                if (choice == 1) {
                    handleLogin(reader, writer);
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
            writer.println("로그인 성공: 학생으로 접속합니다.");
            handleStudentActions(reader, writer);
        } else if ("관리자".equals(role)) {
            writer.println("로그인 성공: 관리자로 접속합니다.");
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
