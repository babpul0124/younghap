package network;

import persistence.dao.*;
import service.*;
import persistence.dto.*;
import network.Protocol;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import java.util.List;

public class ClientThread extends Thread {
  private BufferedReader br;
  private PrintWriter bw;
  private DataOutputStream dos; // DataOutputStream 추가

  private UserDTO user;

  private final Socket clientSocket;
  private final int BUF_SIZE = 1024;
  private byte[] readBuf = new byte[BUF_SIZE];
  private Protocol send_protocol;
  static Connection connect;

  private static CheckInDAO checkInDAO = new CheckInDAO(connect);
  private static CheckOutDAO checkOutDAO = new CheckOutDAO(connect);
  private static DormitoryDAO dormitoryDAO = new DormitoryDAO(connect);
  private static LoginDAO loginDAO = new LoginDAO(connect);

  private static UserService userService = new UserService(checkInDAO, checkOutDAO, dormitoryDAO, loginDAO);

  static {
    try {
      connect = DatabaseConnection.getConnection(); // Connection 초기화
      checkInDAO = new CheckInDAO(connect);
      checkOutDAO = new CheckOutDAO(connect);
      dormitoryDAO = new DormitoryDAO(connect);
      loginDAO = new LoginDAO(connect);
      userService = new UserService(checkInDAO, checkOutDAO, dormitoryDAO, loginDAO);
    } catch (Exception e) {
      throw new RuntimeException("DAO 또는 Service 초기화 중 오류 발생: " + e.getMessage(), e);
    }
  }

  public ClientThread(Socket clientSocket) {
    this.clientSocket = clientSocket;

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
    Object data = protocol.getData();

    if (type == ProtocolType.REQUEST) {
      if (code == ProtocolCode.CONNECT) {
        user_connect();
      } else if (code == ProtocolCode.SCHEDULE_COST_QUERY) {
        viewScheduleMenu();
      }
    } else if (type == ProtocolType.RESPOND) {
      if (code == ProtocolCode.ID_PWD) {
        user_logIn((LoginDTO) data);
      } else if (code == ProtocolCode.SCHEDULE_QUERY) {
        viewSchedule();
      } else if (code == ProtocolCode.COST_QUERY) {
        viewDormitoryCost();
      }

    } else {
      System.out.println("알 수 없는 Protocol Type: " + type);
    }
  }

  private void user_connect() throws IOException {
    send_protocol = new Protocol(ProtocolType.REQUEST, ProtocolCode.ID_PWD, 0, null);
    dos.write(send_protocol.getBytes());
  }

  private void user_logIn(LoginDTO loginDTO) throws IOException {
    UserDTO userDto = userService.login(loginDTO);
    if (userDto != null) {
      send_protocol = new Protocol(ProtocolType.RESULT, ProtocolCode.SUCCESS, 0, userDto);
    } else{
      send_protocol = new Protocol(ProtocolType.RESULT, ProtocolCode.FAILURE, 0, null);
    }
    dos.write(send_protocol.getBytes());
  }

  private void viewScheduleMenu() throws IOException {
    send_protocol = new Protocol(ProtocolType.RESPOND, ProtocolCode.SCHEDULE_COST_QUERY, 0, null);
    dos.write(send_protocol.getBytes());
  }

  private void viewSchedule() throws IOException {
    List<DormitoryDTO> totalScheduleDTOs = userService.viewEvent();
    dos.write(Serializer.intToByteArray(totalScheduleDTOs.size()));
  }

  private void viewDormitoryCost() throws IOException {
    List<DormitoryDTO> totalCostDTOs = userService.viewCost();
    dos.write(Serializer.intToByteArray(totalCostDTOs.size()));
  }
}

