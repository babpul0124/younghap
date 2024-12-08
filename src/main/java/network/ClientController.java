package network;

import jdk.jfr.Event;
import network.*;
import org.testng.internal.collections.Pair;
import persistence.dto.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ClientController {
    private DataInputStream dis;
    private DataOutputStream dos;

    private final int BUF_SIZE = 1024;
    private byte[] readBuf = new byte[BUF_SIZE];

    private BufferedReader keyInput;
    private Viewer viewer;

    public ClientController(DataInputStream dis, DataOutputStream dos, BufferedReader keyInput) {
        this.dis = dis;
        this.dos = dos;
        this.keyInput = keyInput;
        viewer = new Viewer(keyInput);
    }

    public UserDTO login() throws IOException {
        final int LOGIN = 1;
        final int TERMINATION = 2;

        while (true) {
            int option = viewer.initScreen(keyInput);

            if(option == LOGIN) {
                Protocol request_login = new Protocol(ProtocolType.REQUEST, ProtocolCode.CONNECT, 0, null);
                dos.write(request_login.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol protocol = new Protocol(readBuf);

                    if (protocol.getCode() == ProtocolCode.ID_PWD) {
                        UserDTO user = viewer.loginScreen(keyInput);
                        Protocol respond_login = new Protocol(ProtocolType.RESPOND, ProtocolCode.ID_PWD, (user.getLogin_id().getBytes().length + user.getPassword().getBytes().length + 8), user);
                        dos.write(respond_login.getBytes());

                        UserDTO me = null;
                        if (dis.read(readBuf) != -1) {
                            me = (UserDTO) new Protocol(readBuf).getData();
                            return me;
                        }
                    }
                    else {
                        System.out.println("로그인을 할 수 없습니다.");
                    }
                }
            }

            else if (option == TERMINATION) {
                System.exit(0);
            }

            else {
                System.out.println(ErrorMessage.OUT_OF_BOUND);
            }
        }
    }

    public void showManagerScreen(UserDTO userInfo) {
        viewer.managerScreen(userInfo);
    }

    public void showStudentScreen(UserDTO userInfo) {
        viewer.studentScreen(userInfo);
    }

    public void showLogoutMessage() {
        viewer.logout();
    }

    public void responseReceive() throws IOException {
        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);

            if (protocol.getCode() == ProtocolCode.SUCCESS) {
                System.out.println("성공");
            }
            else {
                System.out.println("실패");
            }
        }
    }

    public void registEvent_schedule() throws IOException {
        ArrayList<EventDTO> DTOs = new ArrayList<>();

        Protocol registRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.SCHEDULE_REGISTER, 0, null);
        dos.write(registRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.RESPONSE_SCHEDULE_INFO) {
                String[] event_scheduleInfo = viewer.getEvent_scheduleInfo();
                Protocol respond_event_scheduleInfo = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.RESPONSE_SCHEDULE_INFO,
                        (byte) (event_scheduleInfo[0].getBytes().length +
                                event_scheduleInfo[1].getBytes().length +
                                event_scheduleInfo[2].getBytes().length),
                        event_scheduleInfo
                );
                dos.write(respond_event_scheduleInfo.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.SUCCESS) {
                        int dataCount = Deserializer.byteArrayToInt(readBuf);
                        for (int i = 0; i < dataCount; i++) {
                            if (dis.read(readBuf) != -1) {
                                DTOs.add((EventDTO) new Protocol(readBuf).getData());
                                send_ack();
                            }
                        }
                        viewer.viewEvent_scheduleDTOs(DTOs);
                    } else {
                        System.out.println("등록 오류");
                    }
                }
            }
        }
    }

    public void registDormitory_feeAndmeal() throws IOException {
        ArrayList<DormitoryDTO> DTOs = new ArrayList<>();

        Protocol registRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.FEE_REGISTER, 0, null);
        dos.write(registRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.RESPONSE_FEE_INFO) {
                String[] Dormitory_feeAndmealInfo = viewer.getDormitory_feeAndmealInfo();
                Protocol respondDormitory_feeAndmealInfo = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.RESPONSE_FEE_INFO,
                        (byte) (Dormitory_feeAndmealInfo[0].getBytes().length +
                                Dormitory_feeAndmealInfo[1].getBytes().length +
                                Dormitory_feeAndmealInfo[2].getBytes().length +
                                Dormitory_feeAndmealInfo[3].getBytes().length),
                        Dormitory_feeAndmealInfo
                );
                dos.write(respondDormitory_feeAndmealInfo.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.SUCCESS) {
                        int dataCount = Deserializer.byteArrayToInt(readBuf);
                        for (int i = 0; i < dataCount; i++) {
                            if (dis.read(readBuf) != -1) {
                                DTOs.add((DormitoryDTO) new Protocol(readBuf).getData());
                                send_ack();
                            }
                        }
                        viewer.viewDormitoryDTOs(DTOs);
                    } else {
                        System.out.println("등록 오류");
                    }
                }
            }
        }
    }

    public void viewApplicationList() throws IOException {
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();
        ArrayList<ApplicationListDTO> ApplicationListDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.DORM_LIST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.DORM_LIST_QUERY) {
                int Dormitory_id = viewer.getDormitory_id(DormitoryDTOs);
                Protocol respondDormitory_id = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.DORM_APPLICANT_QUERY,
                        Integer.BYTES,
                        Dormitory_id
                );
                dos.write(respondDormitory_id.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.DORM_APPLICANT_QUERY) {
                        int dataCount = Deserializer.byteArrayToInt(readBuf);
                        for (int i = 0; i < dataCount; i++) {
                            if (dis.read(readBuf) != -1) {
                                ApplicationListDTOs.add((ApplicationListDTO) new Protocol(readBuf).getData());
                                send_ack();
                            }
                        }
                        viewer.viewApplicationListDTOs(ApplicationListDTOs);
                    }
                }
            }
        }
    }

    public void requestSelectionAndAllocationOfRooms() throws IOException {
        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.ROOM_ASSIGNMENT, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.SUCCESS) {
                System.out.println("성공");
            }
        }
    }

    public void viewPassed() throws IOException {
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();
        ArrayList<ApplicationListDTO> ApplicationListDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.DORM_LIST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.DORM_LIST_QUERY) {
                int Dormitory_id = viewer.getDormitory_id(DormitoryDTOs);
                Protocol respondDormitory_id = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.ROOM_ASSIGNMENT_QUERY,
                        Integer.BYTES,
                        Dormitory_id
                );
                dos.write(respondDormitory_id.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.ROOM_ASSIGNMENT_QUERY) {
                        int dataCount = Deserializer.byteArrayToInt(readBuf);
                        for (int i = 0; i < dataCount; i++) {
                            if (dis.read(readBuf) != -1) {
                                ApplicationListDTOs.add((ApplicationListDTO) new Protocol(readBuf).getData());
                                send_ack();
                            }
                        }
                        viewer.viewPassed(ApplicationListDTOs);
                    }
                }
            }
        }
    }

    public void viewPayersForDormitoryFee() throws IOException {
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();
        ArrayList<ApplicationListDTO> ApplicationListDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.DORM_LIST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.DORM_LIST_QUERY) {
                int Dormitory_id = viewer.getDormitory_id(DormitoryDTOs);
                Protocol respondDormitory_id = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.PAID_APPLICANT_QUERY,
                        Integer.BYTES,
                        Dormitory_id
                );
                dos.write(respondDormitory_id.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.PAID_APPLICANT_QUERY) {
                        int dataCount = Deserializer.byteArrayToInt(readBuf);
                        for (int i = 0; i < dataCount; i++) {
                            if (dis.read(readBuf) != -1) {
                                ApplicationListDTOs.add((ApplicationListDTO) new Protocol(readBuf).getData());
                                send_ack();
                            }
                        }
                        viewer.viewPaymentListDTOs(ApplicationListDTOs);
                    }
                }
            }
        }
    }

    public void viewUnpayersForDormitoryFee() throws IOException {
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();
        ArrayList<ApplicationListDTO> ApplicationListDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.DORM_LIST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.DORM_LIST_QUERY) {
                int Dormitory_id = viewer.getDormitory_id(DormitoryDTOs);
                Protocol respondDormitory_id = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.UNPAID_APPLICANT_QUERY,
                        Integer.BYTES,
                        Dormitory_id
                );
                dos.write(respondDormitory_id.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.UNPAID_APPLICANT_QUERY) {
                        int dataCount = Deserializer.byteArrayToInt(readBuf);
                        for (int i = 0; i < dataCount; i++) {
                            if (dis.read(readBuf) != -1) {
                                ApplicationListDTOs.add((ApplicationListDTO) new Protocol(readBuf).getData());
                                send_ack();
                            }
                        }
                        viewer.viewUnpaymentListDTOs(ApplicationListDTOs);
                    }
                }
            }
        }
    }

    public void viewTuberculosisCertificater() throws IOException {
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();
        ArrayList<ApplicationListDTO> ApplicationListDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.DORM_LIST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.DORM_LIST_QUERY) {
                int Dormitory_id = viewer.getDormitory_id(DormitoryDTOs);
                Protocol respondDormitory_id = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.TUBERCULOSIS_CERTIFICATE_QUERY,
                        Integer.BYTES,
                        Dormitory_id
                );
                dos.write(respondDormitory_id.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.TUBERCULOSIS_CERTIFICATE_QUERY) {
                        int dataCount = Deserializer.byteArrayToInt(readBuf);
                        for (int i = 0; i < dataCount; i++) {
                            if (dis.read(readBuf) != -1) {
                                ApplicationListDTOs.add((ApplicationListDTO) new Protocol(readBuf).getData());
                                send_ack();
                            }
                        }
                        viewer.viewTuberculosisCertificaterList(ApplicationListDTOs);
                    }
                }
            }
        }
    }

    public void viewCheck_out_ApplicantAndRequest() throws IOException {
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.DORM_LIST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.DORM_LIST_QUERY) {
                int Dormitory_id = viewer.getDormitory_id(DormitoryDTOs);
                Protocol respondDormitory_id = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.WITHDRAWAL_APPLICANT_QUERY,
                        Integer.BYTES,
                        Dormitory_id
                );
                dos.write(respondDormitory_id.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.WITHDRAWAL_APPLICANT_QUERY) {
                        int Student_id = viewer.getStudent_id();
                        Protocol respondStudent_id = new Protocol(
                                ProtocolType.RESPOND,
                                ProtocolCode.WITHDRAWAL_APPLICANT_QUERY,
                                Integer.BYTES,
                                Student_id
                        );
                        dos.write(respondStudent_id.getBytes());
                        if (dis.read(readBuf) != -1) {
                            if (response.getCode() == ProtocolCode.SUCCESS) {
                                System.out.println("성공");
                            }else{
                                System.out.println("환불 대상자가 아닙니다.");
                            }
                        }
                    }
                }
            }
        }
    }

    public void viewEvent_schedule() throws IOException {
        ArrayList<EventDTO> EventDTOs = new ArrayList<>();
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.SCHEDULE_COST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.SCHEDULE_COST_QUERY) {
                int selectScheduleOrCost = viewer.getSelectScheduleOrCost();
                if(selectScheduleOrCost == 1){
                    Protocol respondMenu = new Protocol(
                            ProtocolType.RESPOND,
                            ProtocolCode.SCHEDULE_QUERY,
                            Integer.BYTES,
                            selectScheduleOrCost
                    );
                    dos.write(respondMenu.getBytes());

                    if (dis.read(readBuf) != -1) {
                        Protocol response = new Protocol(readBuf);
                        if (response.getCode() == ProtocolCode.SCHEDULE_QUERY) {
                            int dataCount = Deserializer.byteArrayToInt(readBuf);
                            for (int i = 0; i < dataCount; i++) {
                                if (dis.read(readBuf) != -1) {
                                    EventDTOs.add((EventDTO) new Protocol(readBuf).getData());
                                    send_ack();
                                }
                            }
                            viewer.viewEventDTO(EventDTOs);
                        }
                    }
                }else if(selectScheduleOrCost == 2){
                    Protocol respondMenu = new Protocol(
                            ProtocolType.RESPOND,
                            ProtocolCode.COST_QUERY,
                            Integer.BYTES,
                            selectScheduleOrCost
                    );
                    dos.write(respondMenu.getBytes());

                    if (dis.read(readBuf) != -1) {
                        Protocol response = new Protocol(readBuf);
                        if (response.getCode() == ProtocolCode.COST_QUERY) {
                            int dataCount = Deserializer.byteArrayToInt(readBuf);
                            for (int i = 0; i < dataCount; i++) {
                                if (dis.read(readBuf) != -1) {
                                    DormitoryDTOs.add((DormitoryDTO) new Protocol(readBuf).getData());
                                    send_ack();
                                }
                            }
                            viewer.viewDormitoryDTOs(DormitoryDTOs);
                        }
                    }
                }else{
                    System.out.println("잘못된 선택입니다.");
                }
            }
        }
    }

    public void requestApplicationRegist(UserDTO me) throws IOException {
        ArrayList<DormitoryDTO> DormitoryDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.COST_QUERY, 0, null);
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.COST_QUERY) {
                int dataCount = Deserializer.byteArrayToInt(readBuf);
                for (int i = 0; i < dataCount; i++) {
                    if (dis.read(readBuf) != -1) {
                        DormitoryDTOs.add((DormitoryDTO) new Protocol(readBuf).getData());
                        send_ack();
                    }
                }
                viewer.viewDormitoryDTOs(DormitoryDTOs);
                String[] applicationInfo = viewer.applicationInfo(me);
                Protocol respondApplicationInfo = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.RESPONSE_APPLICATION_INFO,
                        (applicationInfo[0].getBytes().length +
                         applicationInfo[1].getBytes().length +
                         applicationInfo[2].getBytes().length +
                         applicationInfo[3].getBytes().length +
                         applicationInfo[4].getBytes().length
                        ),
                        applicationInfo
                );
                dos.write(respondApplicationInfo.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.SUCCESS) {
                        System.out.println("신청 완료");
                    }
                }else{
                    System.out.println("중복 신청");
                }
            }else{
                System.out.println("신청 기간 아님");
            }
        }
    }

    public void viewPassed(UserDTO me) throws IOException {
        ArrayList<ApplicationListDTO> ApplicationListDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.SELECTION_RESULT_ROOM_QUERY, Integer.BYTES , me.getId());
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol response = new Protocol(readBuf);
            if (response.getCode() == ProtocolCode.SELECTION_RESULT_ROOM_QUERY) {
                int dataCount = Deserializer.byteArrayToInt(readBuf);
                for (int i = 0; i < dataCount; i++) {
                    if (dis.read(readBuf) != -1) {
                        ApplicationListDTOs.add((ApplicationListDTO) new Protocol(readBuf).getData());
                        send_ack();
                    }
                }
                viewer.viewPassed(ApplicationListDTOs);
            }else{
                System.out.println("합격 정보가 존재하지 않습니다.");
            }
        }
    }

    public void viewDormitoryFeeAndRequestPay(UserDTO me) throws IOException {
        ArrayList<ApplicationListDTO> ApplicationListDTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.DORM_COST_QUERY, Integer.BYTES, me.getId());
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.DORM_COST_QUERY) {
                int dataCount = Deserializer.byteArrayToInt(readBuf);
                for (int i = 0; i < dataCount; i++) {
                    if (dis.read(readBuf) != -1) {
                        ApplicationListDTOs.add((ApplicationListDTO) new Protocol(readBuf).getData());
                        send_ack();
                    }
                }
                String ans = viewer.viewDormitoryFee(ApplicationListDTOs);
                Protocol respond = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.PAYMENT,
                        ans.getBytes().length,
                        ans
                );
                dos.write(respond.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.SUCCESS) {
                        System.out.println("납부 성공");
                    }else{
                            System.out.println("실패");
                    }
                }
            }else{
                System.out.println("실패");
            }
        }
    }

    public void requestSumitTuberculosisCertificate(UserDTO me) throws IOException {
        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.TUBERCULOSIS_CERTIFICATE_SUBMIT, Integer.BYTES, me.getId());
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.TUBERCULOSIS_CERTIFICATE_SUBMIT) {
                String image_path = viewer.image_pathInfo();
                BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
                File outputImage = new File(image_path);
                ImageIO.write(image, "jpg", outputImage);
                Protocol respond = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.TUBERCULOSIS_CERTIFICATE_UPLOAD,
                        outputImage.length(),
                        outputImage
                );
                dos.write(respond.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.SUCCESS) {
                        System.out.println("이미지 업로드 성공");
                    }else{
                        System.out.println("실패");
                    }
                }
            }else{
                System.out.println("제출 불가");
            }
        }
    }

    public void requestCheck_out(UserDTO me) throws IOException {
        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.CHECK_OUT_APPLICATION, Integer.BYTES, me.getId());
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.CHECK_OUT_APPLICATION) {
                String[] check_outInfo = viewer.check_outInfo(me);
                Protocol respondCheck_outInfo = new Protocol(
                        ProtocolType.RESPOND,
                        ProtocolCode.RESPONSE_APPLICATION_INFO,
                        (check_outInfo[0].getBytes().length +
                                check_outInfo[1].getBytes().length +
                                check_outInfo[2].getBytes().length +
                                check_outInfo[3].getBytes().length
                        ),
                        check_outInfo
                );
                dos.write(respondCheck_outInfo.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol response = new Protocol(readBuf);
                    if (response.getCode() == ProtocolCode.SUCCESS) {
                        System.out.println("퇴사 신청 완료");
                    }else{
                        System.out.println("퇴사 정보 오류");
                    }
                }
            }else{
                System.out.println("퇴사 신청 불가");
            }
        }
    }

    public void viewCheck_out(UserDTO me) throws IOException {
        ArrayList<CheckOutDTO> DTOs = new ArrayList<>();

        Protocol viewRequest = new Protocol(ProtocolType.REQUEST, ProtocolCode.REFUND_QUERY, Integer.BYTES, me.getId());
        dos.write(viewRequest.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);
            if (protocol.getCode() == ProtocolCode.REFUND_QUERY) {
                int dataCount = Deserializer.byteArrayToInt(readBuf);
                for (int i = 0; i < dataCount; i++) {
                    if (dis.read(readBuf) != -1) {
                        DTOs.add((CheckOutDTO) new Protocol(readBuf).getData());
                        send_ack();
                    }
                }
                viewer.viewCheckOutDTOs(DTOs);
            }else{
                System.out.println("실패");
            }
        }
    }
    private void send_ack() throws IOException {
        Protocol protocol = new Protocol(ProtocolType.RESPONSE, ProtocolCode.ACK, 0, null);
        dos.write(protocol.getBytes());
    }

}