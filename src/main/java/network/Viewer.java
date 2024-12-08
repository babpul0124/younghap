package network;

//import org.testng.internal.collections.Pair;
import persistence.dto.*;
//import persistence.enums.User_role;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Viewer {
    private BufferedReader keyInput;

    public Viewer(BufferedReader keyInput) {
        this.keyInput = keyInput;
    }

    public int initScreen(BufferedReader keyInput) throws IOException {
        System.out.println("[1] 로그인");
        System.out.println("[2] 종료");
        System.out.print("입력 : ");

        return Integer.parseInt(keyInput.readLine());
    }

    public UserDTO loginScreen(BufferedReader keyInput) throws IOException {
        UserDTO userInfo = new UserDTO();

        System.out.print("ID : ");
        userInfo.setLoginId(keyInput.readLine());
        System.out.print("PW : ");
        userInfo.setPassword(keyInput.readLine());

        return userInfo;
    }

    public void logout() {
        System.out.println("로그아웃합니다.\n");
    }

    public void managerScreen(UserDTO userInfo) {
        System.out.println();
        System.out.println("관리자 " + userInfo.getName() + "님 환영합니다.");
        System.out.println("무엇을 하시겠습니까?");
        System.out.println("[1] 선발 일정 등록");
        System.out.println("[2] 생활관 사용료 및 급식비 등록");
        System.out.println("[3] 신청자 조회");
        System.out.println("[4] 입사자 선발 및 호실 배정");
        System.out.println("[5] 생활관 비용 납부자 조회");
        System.out.println("[6] 생활관 비용 미납부자 조회");
        System.out.println("[7] 결핵진단서 제출 확인");
        System.out.println("[8] 로그아웃");
        System.out.print("입력 : ");
    }

    public void studentScreen(UserDTO userInfo) {
        System.out.println();
        System.out.println("학생 " + userInfo.getName() + "님 환영합니다.");
        System.out.println("무엇을 하시겠습니까?");
        System.out.println("[1] 선발 일정 및 비용 확인");
        System.out.println("[2] 입사 신청");
        System.out.println("[3] 합격 여부 및 호실 확인");
        System.out.println("[4] 생활관 비용 확인 및 납부");
        System.out.println("[5] 결핵 진단서 제출");
        System.out.println("[6] 퇴사 신청");
        System.out.println("[7] 환불 확인");
        System.out.println("[8] 로그아웃");
        System.out.print("입력 : ");
    }

    public DormitoryDTO getEvent_scheduleInfo(BufferedReader keyInput) throws IOException {
        DormitoryDTO dto = new DormitoryDTO();
        System.out.println("[선발 일정 등록]");
        System.out.print("일정 제목 : ");
        dto.setEventName(keyInput.readLine());
        System.out.print("시작일(yyyy-mm-dd hh:mm:ss): ");
        String startDate = keyInput.readLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(startDate, formatter);
        dto.setStartDate(dateTime);
        System.out.print("종료일(yyyy-mm-dd hh:mm:ss): ");
        String endDate = keyInput.readLine();
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        dateTime = LocalDateTime.parse(endDate, formatter);
        dto.setStartDate(dateTime);

        return dto;
    }

    public void viewEvent_scheduleDTOs(ArrayList<DormitoryDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.event_scheduleDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public DormitoryDTO getDormitory_feeAndmealInfo() throws IOException {
        DormitoryDTO dto =  new DormitoryDTO();

        System.out.println("[생활관 사용료 및 급식비 등록]");
        System.out.print("생활관명 : ");
        dto.setDormitoryName(keyInput.readLine());
        System.out.print("생활관비 : ");
        dto.setDormitoryFee(Integer.parseInt(keyInput.readLine()));
        System.out.print("식사 유형 (n일식)  : ");
        dto.setMealFrequency(Integer.parseInt(keyInput.readLine()));
        System.out.print("급식비  : ");
        dto.setMealFrequency(Integer.parseInt(keyInput.readLine()));

        return dto;
    }

    public void viewDormitoryDTOs(ArrayList<DormitoryDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.DormitoryDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public DormitoryDTO getDormitory_id(ArrayList<DormitoryDTO> DTOs) throws IOException {
        viewDormitoryDTOs(DTOs);
        DormitoryDTO dto = new DormitoryDTO();
        System.out.println("조회할 생활관 ID 입력: ");
        dto.setDormitoryId(Integer.parseInt(keyInput.readLine()));
        return dto;
    }

    public void viewApplicationListDTOs(ArrayList<CheckInDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.ApplicationToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewPaymentListDTOs(ArrayList<CheckInDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.PaymentListToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewUnpaymentListDTOs(ArrayList<CheckInDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.PaymentListToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewPassed(ArrayList<CheckInDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            if(DTOs.get(i).getApplicationStatus().equals("승인")){
                System.out.println("[" + i + "] " + DTOToString.PassedApplicationListDTOToString(DTOs.get(i)));
            }
        }
        System.out.println();
    }

    public void viewTuberculosisCertificaterList(ArrayList<CheckInDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.TuberculosisCertificaterListToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public UserDTO getStudent_id() throws IOException {
        System.out.println("환불할 거주자의 학번 입력: ");
        UserDTO dto = new UserDTO();
        dto.setUserId(Integer.parseInt(keyInput.readLine()));
        return dto;
    }

    public int getSelectScheduleOrCost() throws IOException {
        System.out.println("[1] 선발 일정 조회");
        System.out.println("[2] 기숙사 비용 조회");
        System.out.println("입력: ");
        return Integer.parseInt(keyInput.readLine());
    }

    public void viewEventDTO(ArrayList<DormitoryDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.event_scheduleDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public CheckInDTO applicationInfo(UserDTO me) throws IOException {
        CheckInDTO dto = new CheckInDTO();

        dto.setUserId(me.getUserId()); //학번
        System.out.println("[입사 신청 정보 입력]");
        System.out.print("생활관 ID: ");
        dto.setDormitoryId(Integer.parseInt(keyInput.readLine()));
        System.out.print("지망: ");
        dto.setPreference(Integer.parseInt(keyInput.readLine()));
        System.out.print("식사 유형 (n일식): ");
        dto.setMealFrequency(Integer.parseInt(keyInput.readLine()));
        System.out.print("코골이 여부: ");
        dto.setIsSnoring(Integer.parseInt(keyInput.readLine()));

        return dto;
    }

    public boolean viewDormitoryFee(ArrayList<CheckInDTO> DTOs) throws IOException {

        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.viewDormitoryFeeDIOToString(DTOs.get(i)));
        }
        System.out.println("납부하려면 납부, 아니면 N 입력: ");
        String result = keyInput.readLine();
        if(result == "납부"){
            return true;
        }else if(result == "N"){
            return false;
        }else{
            System.out.println("잘못된 입력입니다.");
            return false;
        }
    }

    public String image_pathInfo() throws IOException {
        System.out.println("이미지 경로 입력: ");
        return keyInput.readLine();
    }

    public CheckOutDTO check_outInfo(UserDTO me) throws IOException {
        CheckOutDTO dto = new CheckOutDTO();

        dto.setUserId(me.getUserId());
        System.out.println("[퇴사 신청 정보 입력]");
        System.out.print("퇴사일(yyyy-mm-dd hh:mm:ss): ");
        String checkOutDate = keyInput.readLine();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime dateTime = LocalDateTime.parse(checkOutDate, formatter);
        dto.setCheckOutDate(dateTime);
        System.out.print("환불 은행: ");
        dto.setBankName(keyInput.readLine());
        System.out.print("환불 계좌: ");
        dto.setAccountNum(keyInput.readLine());

        return dto;
    }

    public void viewCheckOutDTOs(ArrayList<CheckOutDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.viewCheckOutDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }
}
