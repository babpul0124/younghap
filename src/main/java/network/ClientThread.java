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

    private int id;
    private UserDTO user;

    private final Socket clientSocket;
    private final int BUF_SIZE = 1024;
    private byte[] readBuf = new byte[BUF_SIZE];
    private Protocol send_protocol;

    public ClientThread(Socket clientSocket, int id) {
        this.clientSocket = clientSocket;
        this.id = id;
    }

    @Override
    public void run() {
        try {
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
                user_connect();
                System.out.println("야~~~호!");
            }
        } else {
            System.out.println("알 수 없는 Protocol Type: " + type);
        }
    }

    private void user_connect() throws IOException {
        send_protocol = new Protocol(ProtocolType.REQUEST, ProtocolCode.ID_PWD, 0, null);
        dos.write(send_protocol.getBytes());
    }

}

