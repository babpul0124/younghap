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
                String login_id = me.getLogin_id(); //login_id 얻어옴
                if (login_id.equals(User_role.MANAGER.getName())) {
                    managerRun();
                }
                else if (user_role.equals(User_role.USER.getName())) {
                    userRun();
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
        final int DETERMINATION_ORDER = 4;
        final int VIEW_REVIEW = 5;
        final int REGIST_RECOMMENT = 6;
        final int STATISTICAL_INFO_VIEW = 7;
        final int LOGOUT = 8;

        while(login) {
            con.showOwnerScreen(me);

            int option = Integer.parseInt(keyInput.readLine());
            switch (option) {
                case STORE_REGIST_REQUEST:
                    con.registStore(me);
                    break;

                case MENU_REGIST_REQUEST:
                    con.registMenuAndOption(me);
                    break;

                case MANAGEMENT_TIME_MODIFICATION:
                    con.setRunningTime(me);
                    break;

                case DETERMINATION_ORDER:
                    con.orderDetermination(me);
                    break;

                case VIEW_REVIEW:
                    con.viewReview(me);
                    break;

                case REGIST_RECOMMENT:
                    con.registRecommnet(me);
                    break;

                case STATISTICAL_INFO_VIEW:
                    con.ownerStatisticsView(me);
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

    private void userRun() throws IOException {
        boolean login = true;
        final int USER_MODIFICATION = 1;
        final int VIEW_STORE = 2;
        final int REGIST_ORDER = 3;
        final int CANCEL_ORDER = 4;
        final int VIEW_ORDER = 5;
        final int REGIST_REVIEW = 6;
        final int LOGOUT = 7;

        while(login) {
            con.showUserScreen(me);

            int option = Integer.parseInt(keyInput.readLine());
            switch (option) {
                case USER_MODIFICATION:
                    con.modificationUser(me);
                    break;

                case VIEW_STORE:
                    con.viewStore();
                    break;

                case REGIST_ORDER:
                    con.registOrder(me);
                    break;

                case CANCEL_ORDER:
                    con.orderCancel(me);
                    break;

                case VIEW_ORDER:
                    con.viewOrder(me);
                    break;

                case REGIST_REVIEW:
                    con.registReview(me);
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
