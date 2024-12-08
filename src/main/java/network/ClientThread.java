package network;

import service.*;
import persistence.dto.*;
import persistence.dao.*;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private BufferedReader br;
    private PrintWriter bw;

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
        try {
            // InputStream과 OutputStream을 설정
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            // BufferedReader와 PrintWriter 설정
            br = new BufferedReader(new InputStreamReader(is));
            bw = new PrintWriter(new OutputStreamWriter(os), true);

            // 로그인 처리
            handleLogin(br, bw);

        } catch (IOException e) {
            System.out.println("클라이언트와의 연결에 문제가 발생했습니다. " + e.getMessage());
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
        // 학생 관련 작업을 여기에 구현합니다.
        writer.println("학생 화면에 진입했습니다.");
    }

    private void handleManagerActions(BufferedReader reader, PrintWriter writer) throws IOException {
        // 관리자 관련 작업을 여기에 구현합니다.
        writer.println("관리자 화면에 진입했습니다.");
    }
}
