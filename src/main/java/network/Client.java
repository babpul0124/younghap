package network;

import java.io.*;
import java.net.*;

import persistence.dto.*;
import persistence.enums.User_role;

public class Client {
    private static final String IP = "127.0.0.1";
    private Socket cliSocket;
    private DataInputStream dis;
    private DataOutputStream dos;
    private BufferedReader keyInput;
    private ClientController con;
    private UserDTO me;

    public Client() {
        try{
            cliSocket = new Socket(IP, 5000);
            dis = new DataInputStream(cliSocket.getInputStream());
            dos = new DataOutputStream(cliSocket.getOutputStream());
            keyInput = new BufferedReader(new InputStreamReader(System.in));
            con = new ClientController(dis, dos, keyInput);
        }catch(UnknownHostException e){
            System.err.println("서버를 찾지 못했습니다.");
        }catch(IOException e){
            System.err.println(e);
        }
    }

    public void exit() {
        try{
            cliSocket.close();
        }catch(IOException e){
            System.out.println(e);
        }
    }

    public void run() throws IOException {
        while(true){
            me = con.login();

            if (me != null) {
                String User_role = me.getUser_role(); //User_role 얻어옴
                if (User_role.equals(User_role.MANAGER.getName())) {
                    managerRun();
                }
                else if (user_role.equals(User_role.USER.getName())) {
                    studentRun();
                }
            }
            else {
                System.out.println(ErrorMessage.LOGIN_FAILED);
            }
        }
    }

    private void managerRun() throws IOException {
        boolean login = true;
        final int EVENT_SCHEDULE_REGIST_REQUEST = 1;
        final int DORMITORY_FEE_AND_MEAL_REGIST_REQUEST = 2;
        final int APPLICATION_VIEW = 3;
        final int SELECTION_AND_ALLOCATION_OF_ROOMS_REQUEST = 4;
        final int PASSED_VIEW = 5;
        final int PAYERS_FOR_DORMITORY_FEE_VIEW = 6;
        final int UNPAYERS_FOR_DORMITORY_FEE_VIEW = 7;
        final int SUBMITTER_OF_TUBERCULOSIS_CERTIFICATE_VIEW = 8;
        final int CHECK_OUT_APPLICANT_VIEW_AND_REQUEST = 9;
        final int LOGOUT = 10;

        while(login) {
            con.showManagerScreen(me);

            int option = Integer.parseInt(keyInput.readLine());
            switch (option) {
                case EVENT_SCHEDULE_REGIST_REQUEST:
                    con.registEvent_schedule();
                    break;

                case DORMITORY_FEE_AND_MEAL_REGIST_REQUEST:
                    con.registDormitory_feeAndmeal();
                    break;

                case APPLICATION_VIEW:
                    con.viewApplication();
                    break;

                case SELECTION_AND_ALLOCATION_OF_ROOMS_REQUEST:
                    con.requestSelectionAndAllocationOfRooms();
                    break;

                case PASSED_VIEW:
                    con.viewPassed();
                    break;

                case PAYERS_FOR_DORMITORY_FEE_VIEW:
                    con.viewPayersForDormitoryFee();
                    break;

                case UNPAYERS_FOR_DORMITORY_FEE_VIEW:
                    con.viewUnpayersForDormitoryFee();
                    break;

                case SUBMITTER_OF_TUBERCULOSIS_CERTIFICATE_VIEW:
                    con.viewTuberculosisCertificater();
                    break;

                case CHECK_OUT_APPLICANT_VIEW_AND_REQUEST:
                    con.viewCheck_out_ApplicantAndRequest();
                    break;

                case LOGOUT:
                    con.showLogoutMessage();
                    login = false;
                    break;

                default:
                    System.out.println(ErrorMessage.OUT_OF_BOUND);
                    break;
            }
        }
    }

    private void studentRun() throws IOException {
        boolean login = true;
        final int EVENT_SCHEDULE_VIEW = 1;
        final int APPLICATION_REGIST_REQUEST = 2;
        final int PASSED_VIEW = 3;
        final int DORMITORY_FEE_VIEW_AND_PAY_REQUEST = 4;
        final int SUBMIT_TUBERCULOSIS_CERTIFICATE_REQUEST = 5;
        final int CHECK_OUT_APPLICATION_REQUEST = 6;
        final int CHECK_OUT_STATUS_VIEW = 7;
        final int LOGOUT = 8;

        while(login) {
            con.showStudentScreen(me);

            int option = Integer.parseInt(keyInput.readLine());
            switch (option) {
                case EVENT_SCHEDULE_VIEW:
                    con.viewEvent_schedule();
                    break;

                case APPLICATION_REGIST_REQUEST:
                    con.viewStore();
                    break;

                case PASSED_VIEW:
                    con.registOrder(me);
                    break;

                case DORMITORY_FEE_VIEW_AND_PAY_REQUEST:
                    con.orderCancel(me);
                    break;

                case SUBMIT_TUBERCULOSIS_CERTIFICATE_REQUEST:
                    con.viewOrder(me);
                    break;

                case CHECK_OUT_APPLICATION_REQUEST:
                    con.registReview(me);
                    break;

                case CHECK_OUT_STATUS_VIEW:
                    break;

                case LOGOUT:
                    con.showLogoutMessage();
                    login = false;
                    break;

                default:
                    System.out.println(ErrorMessage.OUT_OF_BOUND);
                    break;
            }
        }
    }
}
