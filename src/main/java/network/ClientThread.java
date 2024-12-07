package network;

import persistence.dao.*;
import persistence.dto.*;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ClientThread extends Thread {
    private Socket socket;
    private DataOutputStream dos;
    private DataInputStream dis;

    private final int BUF_SIZE = 1024;
    private byte[] readBuf = new byte[BUF_SIZE]; // 메시지 크기에 맞게 조정

    // DAO 객체는 외부에서 주입받는 방식으로 처리
    private LoginDAO loginDAO;
    private CheckInDAO checkInDAO;
    private CheckOutDAO checkOutDAO;
    private DormitoryDAO dormitoryDAO;

    ClientThread(Socket socket, LoginDAO loginDAO, CheckInDAO checkInDAO, CheckOutDAO checkOutDAO, DormitoryDAO dormitoryDAO) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            dis = new DataInputStream(socket.getInputStream());
            dos = new DataOutputStream(socket.getOutputStream());

            while (true) {
                // 클라이언트로부터 메시지를 읽음
                int len = dis.read(readBuf);
                if (len == -1) break; // 연결이 끊어지면 종료

                String clientMessage = new String(readBuf, 0, len).trim();
                System.out.println("Received message: " + clientMessage);

                // 클라이언트 요청에 대한 처리
                handleClientRequest(clientMessage);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (dis != null) dis.close();
                if (dos != null) dos.close();
                if (socket != null) socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleClientRequest(String clientMessage) {
        String[] request = clientMessage.split(" ");
        String type = request[0]; // 메시지 타입
        String code = request[1]; // 코드
        String data = request.length > 2 ? request[2] : ""; // 추가 데이터

        // 요청 타입에 따라 분기 처리
        switch (type) {
            case "0x01": // 접속
                processType0x01(code, data);
                break;
            case "0x02": // 요청
                processType0x02(code, data);
                break;
            case "0x03": // 응답
                processType0x03(code, data);
                break;
            case "0x04": // 전송
                processType0x04(code, data);
                break;
            case "0x05": // 결과
                processType0x05(code, data);
                break;
            default:
                sendResponse("ERROR: Invalid type");
        }
    }

    // 메시지 타입 0x01 처리 (접속)
    private void processType0x01(String code, String data) {
        // 접속 시 ID와 PWD 확인
        if ("0x01".equals(code)) { // 접속 코드
            handleLogin(data);
        } else {
            sendResponse("ERROR: Invalid code for type 0x01");
        }
    }
    //로그인 끝O
    private void handleLogin(String data) {
        String[] credentials = data.split(",");
        String username = credentials[0];
        String password = credentials[1];

        // getIdPwList에서 모든 유저 정보를 가져온 후, 원하는 유저가 있는지 확인
        ArrayList<UserDTO> userIdPws = loginDAO.getIdPwList();

        boolean isValid = false;
        for (UserDTO userDTO : userIdPws) {
            if (userDTO.getLogin_id().equals(username) && userDTO.getPassword().equals(password)) {
                isValid = true;
                break;
            }
        }

        if (isValid) {
            sendResponse("SUCCESS: Login successful");
        } else {
            sendResponse("FAILURE: Invalid username or password");
        }
    }

    // 메시지 타입 0x02 처리 (요청)
    private void processType0x02(String code, String data) {
        switch (code) {
            case "0x02": // 선발 일정 및 비용 조회
                handleSelectionScheduleAndCost();
                break;
            case "0x03": // 입사 신청
                handleCheckIn(data);
                break;
            case "0x04": // 합격 여부 및 호실 조회
                handleRoomAssignment(data);
                break;
            case "0x05": // 합격 생활관 비용 조회
                handleDormitoryCost(data);
                break;
            case "0x06": // 결핵진단서 제출
                handleTBTestSubmission(data);
                break;
            case "0x07": // 퇴사 신청
                handleCheckOut(data);
                break;
            case "0x08": // 환불 조회
                handleRefund(data);
                break;
            default:
                sendResponse("ERROR: Invalid code for type 0x02");
        }
    }

    private void handleSelectionScheduleAndCost() {
        // 선발 일정 및 비용 조회 로직 추가
        sendResponse("SELECTION SCHEDULE AND COST: Details...");
    }
    //입사
    private void handleCheckIn(String data) {
        try {
            // 데이터 처리 (예: data에서 student_id와 application_status 추출)
            String[] parsedData = data.split(",");
            int studentId = Integer.parseInt(parsedData[0].trim());
            String applicationStatus = parsedData[1].trim();

            // 상태에 따라 메시지 전송
            if ("대기".equals(applicationStatus) || "승인".equals(applicationStatus)) {
                // 입사 신청이 정상적으로 된 경우 (대기 또는 승인 상태)
                checkInDAO.updateApplicationStatus(studentId, applicationStatus);
                sendResponse("Success: Application status updated to " + applicationStatus + " for student ID " + studentId);
            } else {
                // 그 외 상태일 경우 실패 메시지 전송
                sendResponse("Failure: Application status update failed for student ID " + studentId);
            }
        } catch (Exception e) {
            // 예외 처리 및 에러 메시지 전송
            sendResponse("ERROR: Invalid data format or processing issue - " + e.getMessage());
        }
    }

    //호실
    private void handleRoomAssignment(String data) {
        try {
            // data는 학생 ID와 개인 호실 ID를 구분할 수 있는 형식으로 가정
            String[] parts = data.split(",");
            if (parts.length != 2) {
                sendResponse("ERROR: Invalid input format. Expected: studentId,personalRoomId");
                return;
            }

            int studentId = Integer.parseInt(parts[0].trim());
            int personalRoomId = Integer.parseInt(parts[1].trim());

            // 호실 배정 및 passed 테이블 업데이트
            checkInDAO.updatePassedAndDormitory(studentId, personalRoomId);

            sendResponse("ROOM ASSIGNMENT: Successfully updated the dormitory assignment for student ID " + studentId);

        } catch (NumberFormatException e) {
            sendResponse("ERROR: Invalid student ID or personal room ID format.");
        } catch (Exception e) {
            sendResponse("ERROR: Unable to assign room or update passed table.");
        }
    }

    //생활관 비용
    private void handleDormitoryCost(String data) {
        try {
            int studentId = Integer.parseInt(data); // 학생 ID로 변환

            // 학생 ID에 해당하는 생활관 및 비용 정보 조회
            ArrayList<DormitoryDTO> dormitoryInfoList = dormitoryDAO.getAllDormitoryFee();

            // 응답에 필요한 정보만 추출하여 전송
            if (!dormitoryInfoList.isEmpty()) {
                // 여러 개의 결과가 있을 수 있기 때문에 첫 번째 항목을 예시로 처리
                DormitoryDTO info = dormitoryInfoList.get(0);
                String cost = "Dormitory Fee: " + info.getDormitoryFee() + " , Meal Cost: " + info.getMoney();
                sendResponse("DORMITORY COST: " + cost);
            } else {
                sendResponse("No dormitory cost information found for student ID: " + studentId);
            }

        } catch (NumberFormatException e) {
            sendResponse("ERROR: Invalid student ID format.");
        } catch (Exception e) {
            sendResponse("ERROR: Unable to process dormitory cost.");
        }
    }

    //결핵진단서
    private void handleTBTestSubmission(String data) {
        try {
            // 'data'가 학생 ID인 경우라고 가정합니다.
            int studentId = Integer.parseInt(data); // 학생 ID로 변환

            // 결핵 진단서 제출 여부 확인
            String submissionStatus = checkInDAO.checkStudentImageSubmitted(studentId);

            if (submissionStatus.equals("Yes")) {
                sendResponse("TB Test Document: Submitted");
            } else {
                sendResponse("TB Test Document: Not Submitted");
            }
        } catch (NumberFormatException e) {
            sendResponse("ERROR: Invalid student ID format.");
        } catch (Exception e) {
            sendResponse("ERROR: Unable to process TB test submission.");
        }
    }
    // 퇴실
    private void handleCheckOut(String data) {
        try {
            // 데이터 처리 (예: data에서 student_id, checkout_status, bank_name, account_num, check_out_date 추출)
            String[] parsedData = data.split(",");
            int studentId = Integer.parseInt(parsedData[0].trim());
            String checkoutStatus = parsedData[1].trim();  // 퇴실 상태

            // 상태에 따라 메시지 전송
            if ("환불 대기".equals(checkoutStatus)) {
                // 입사 신청이 정상적으로 된 경우 (대기 또는 승인 상태)
                checkOutDAO.updateCheckOutStatus(studentId, checkoutStatus);
                sendResponse("Success: Check-out request processed successfully for student ID " + studentId);
            } else {
                // 그 외 상태일 경우 실패 메시지 전송
                sendResponse("Failure: Check-out request failed for student ID " + studentId + " due to invalid status.");
            }
        } catch (Exception e) {
            // 예외 처리 및 에러 메시지 전송
            sendResponse("ERROR: Invalid data format or processing issue - " + e.getMessage());
        }
    }

    // 환불
    private void handleRefund(String data) {
        try {
            int studentId = Integer.parseInt(data); // 데이터에서 학생 ID를 파싱
            List<UserCheckOutResultDTO> checkOutInfoList = checkOutDAO.getCheckOutInfoByStudentId(studentId);

            if (checkOutInfoList.isEmpty()) {
                sendResponse("No checkout information found for student ID: " + studentId);
            } else {
                // 환불 처리만 알림
                sendResponse("Refund processed successfully for Student ID: " + studentId);
            }
        } catch (NumberFormatException e) {
            sendResponse("ERROR: Invalid student ID format.");
        } catch (Exception e) {
            sendResponse("ERROR: Unable to process refund.");
        }
    }

    // 메시지 타입 0x03 처리 (응답)
    private void processType0x03(String code, String data) {
        // 응답을 클라이언트에 맞게 처리
        if ("0x03".equals(code)) {
            sendResponse("CHECK-IN RESPONSE: " + data);
        } else {
            sendResponse("ERROR: Invalid code for type 0x03");
        }
    }

    // 메시지 타입 0x04 처리 (전송)
    private void processType0x04(String code, String data) {
        // 전송된 데이터를 처리
        if ("0x03".equals(code)) {
            sendResponse("TB Test Document: " + data);
        } else {
            sendResponse("ERROR: Invalid code for type 0x04");
        }
    }

    // 메시지 타입 0x05 처리 (결과)
    private void processType0x05(String code, String data) {
        switch (code) {
            case "0x01": // 성공
                sendResponse("SUCCESS: " + data);
                break;
            case "0x02": // 실패
                sendResponse("FAILURE: " + data);
                break;
            default:
                sendResponse("ERROR: Invalid code for type 0x05");
        }
    }

    private void sendResponse(String message) {
        try {
            dos.writeUTF(message); // UTF-8로 문자열을 전송
            dos.flush();
        } catch (IOException e) {
            System.err.println("Error sending response: " + e.getMessage());
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException closeException) {
                System.err.println("Error closing socket: " + closeException.getMessage());
            }
        }
    }
}