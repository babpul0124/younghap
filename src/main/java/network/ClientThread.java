package network;

import service.*;
import persistence.dto.*;
import network.Protocol;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread {
    private BufferedReader br;
    private PrintWriter bw;
    private DataOutputStream dos; // DataOutputStream 추가

    private final Socket clientSocket;
    private final UserService userService;
    private final Viewer viewer;

    private final int BUF_SIZE = 1024;
    private byte[] readBuf = new byte[BUF_SIZE];
    private Protocol send_protocol;

    public ClientThread(Socket clientSocket, UserService userService, Viewer viewer) {
        this.clientSocket = clientSocket;
        this.userService = userService;
        this.viewer = viewer;
    }

    @Override
    public void run() {
        try {
            // InputStream과 OutputStream 초기화
            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            br = new BufferedReader(new InputStreamReader(is));
            bw = new PrintWriter(new OutputStreamWriter(os), true);
            dos = new DataOutputStream(os); // dos 초기화

            while (is.read(readBuf) != -1) {
                Protocol protocol = new Protocol(readBuf);

                selectFunction(protocol);
            }

        } catch (IOException e) {
            System.out.println("클라이언트와의 연결에 문제가 발생했습니다. " + e.getMessage());
        }
    }

    private void selectFunction(Protocol protocol) throws IOException {
        byte type = protocol.getType();
        byte code = protocol.getCode();
        DTO data = protocol.getData();

        if (type == ProtocolType.REQUEST) { //요청
            if (code == ProtocolCode.CONNECT) { //접속 요청
                user_connection();
            }
            else if (code == ProtocolCode.ID_PWD) { //아이디, 비번 요청
                user_login((UserDTO) data);
            }
        }
        else if (type == ProtocolType.RESPOND) { //응답
            if (code == ProtocolCode.ID_PWD) { //아이디, 비번 응답.
                // 응답 처리
            }
        }
        else if (type == ProtocolType.RESULT) { //결과
            if(code == (ProtocolCode.ID_PWD | ProtocolCode.SUCCESS)){ //아이디, 비번 결과
                user_login_accept((UserDTO)data);
            }
            else if(code == (ProtocolCode.ID_PWD | ProtocolCode.FAILURE)){ //아이디, 비번 결과
                user_login_refuse((UserDTO)data);
            }
        } else {
            System.out.println("알 수 없는 Protocol Type: " + type);
        }
    }

    // 접속 요청을 처리하는 메서드
    private void user_connection() throws IOException {
        // Viewer에서 initScreen()을 통해 사용자의 선택을 받음
        int userChoice = viewer.initScreen(br);

        if (userChoice == 1) {
            // 1번 선택 -> 로그인
            handleLogin();
        } else if (userChoice == 2) {
            // 2번 선택 -> 종료
            System.out.println("프로그램을 종료합니다.");
            clientSocket.close();  // 클라이언트 연결 종료
        } else {
            System.out.println("잘못된 선택입니다. 다시 시도하세요.");
            user_connection();  // 다시 선택 받기
        }
    }

    private void handleLogin() throws IOException {
        // Viewer의 loginScreen() 메서드 사용하여 로그인 정보 받기
        UserDTO userInfo = viewer.loginScreen(br);

        // 받은 로그인 정보로 인증
        user_login(userInfo);
    }

    private void user_login(UserDTO userDTO) throws IOException {
        String loginId = userDTO.getLoginId();
        String password = userDTO.getPassword();

        // 로그인 인증
        String role = userService.validateUser(loginId, password);

        if ("학생".equals(role)) {
            // 성공: 학생으로 로그인
            user_login_accept(userDTO);
        } else if ("관리자".equals(role)) {
            // 성공: 관리자로 로그인
            user_login_accept(userDTO);
        } else {
            // 실패: 로그인 실패 처리
            user_login_refuse(userDTO);
        }
    }

    private void user_login_accept(UserDTO data) {
        // 로그인 성공 시 처리 로직
        send_protocol = new Protocol(ProtocolType.RESPOND, ProtocolCode.SUCCESS, 0, null); // 성공 응답
        dos.write(send_protocol.getBytes());
        System.out.println("로그인 성공: " + data.getLoginId());
    }

    private void user_login_refuse(UserDTO data) {
        // 로그인 실패 시 처리 로직
        send_protocol = new Protocol(ProtocolType.RESPOND, ProtocolCode.FAILURE, 0, null); // 실패 응답
        dos.write(send_protocol.getBytes());
        System.out.println("로그인 실패: " + data.getLoginId());
    }
}
