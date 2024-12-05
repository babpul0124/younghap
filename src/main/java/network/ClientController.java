package network;

import network.*;
import org.testng.internal.collections.Pair;
import persistence.PooledDataSource;
import persistence.dto.*;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
                Protocol request_login = new Protocol(ProtocolType.REQUEST, ProtocolCode.LOGIN, 0, null);
                dos.write(request_login.getBytes());

                if (dis.read(readBuf) != -1) {
                    Protocol protocol = new Protocol(readBuf);

                    if (protocol.getCode() == ProtocolCode.Login) {
                        UserDTO user = viewer.loginScreen(keyInput);
                        Protocol respond_login = new Protocol(ProtocolType.RESPOND, ProtocolCode.LOGIN, (user.getLogin_id().getBytes().length + user.getPassword().getBytes().length + 8), user);
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

            if (protocol.getCode() == ProtocolCode.ACCEPT) {
                System.out.println("성공");
            }
            else {
                System.out.println("실패");
            }
        }
    }

    public ArrayList<StatisticsDTO> getAllStatDTO() throws IOException {
        Protocol requestAllStatDTOs = new Protocol(ProtocolType.SEARCH, (byte)(ProtocolCode.STORE | ProtocolCode.HISTORY), 0, null);
        dos.write(requestAllStatDTOs.getBytes());

        ArrayList<StatisticsDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }
        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((StatisticsDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public ArrayList<StatisticsDTO> getAllStatDTO(StoreDTO info) throws IOException {
        Protocol requestAllStatDTOs = new Protocol(ProtocolType.SEARCH, (byte)(ProtocolCode.STORE | ProtocolCode.HISTORY), 0, info);
        dos.write(requestAllStatDTOs.getBytes());

        ArrayList<StatisticsDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }
        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((StatisticsDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public ArrayList<OrdersDTO> getAllOrderDTO() throws IOException {
        Protocol requestAllOrderDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.ORDER, 0, null);
        dos.write(requestAllOrderDTOs.getBytes());

        ArrayList<OrdersDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }
        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((OrdersDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public ArrayList<TotalOrdersDTO> getAllTotalOrderDTO(DTO info) throws IOException {
        Protocol requestAllMyOrderDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.ORDER, 0, info);
        dos.write(requestAllMyOrderDTOs.getBytes());
        //info에 해당하는 모든 Orders 리스트를 가져옴

        ArrayList<TotalOrdersDTO> DTOs = new ArrayList<>();
        int listLength = 0;

        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((TotalOrdersDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public ArrayList<StoreDTO> getAllStoreDTO() throws IOException {
        Protocol searchStoreInfo = new Protocol(ProtocolType.SEARCH, ProtocolCode.STORE, 0, null);
        dos.write(searchStoreInfo.getBytes());

        ArrayList<StoreDTO> DTOs = new ArrayList<>();
        int listLength = 0;

        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((StoreDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    private void send_ack() throws IOException {
        Protocol protocol = new Protocol(ProtocolType.RESPONSE, ProtocolCode.ACK, 0, null);
        dos.write(protocol.getBytes());
    }



    public ArrayList<StoreDTO> getAllStoreDTO(DTO info) throws IOException {
        Protocol requestAllMyStoreDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.STORE, 0, info);
        dos.write(requestAllMyStoreDTOs.getBytes());
        //info에 해당하는 모든 Store 리스트를 가져옴

        ArrayList<StoreDTO> DTOs = new ArrayList<>();

        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((StoreDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public ArrayList<MenuDTO> getAllMenuDTO() throws IOException {
        Protocol searchMenuInfo = new Protocol(ProtocolType.SEARCH, ProtocolCode.MENU, 0, null);
        dos.write(searchMenuInfo.getBytes());

        ArrayList<MenuDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((MenuDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public <T> ArrayList<MenuDTO> getAllMenuDTO(T info) throws IOException {
        Protocol requestAllMyMenuDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.MENU, 0, (DTO) info);
        dos.write(requestAllMyMenuDTOs.getBytes());
        //info가 지닌 모든 menu 리스트를 가져옴

        ArrayList<MenuDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((MenuDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public <T> ArrayList<DetailsDTO> getAllOptionDTO(T info) throws IOException {
        Protocol requestAllMyOptionDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.OPTION, 0, (DTO) info);
        dos.write(requestAllMyOptionDTOs.getBytes());
        //info가 지닌 모든 DetailsDTO 리스트를 가져옴

        ArrayList<DetailsDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((DetailsDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public ArrayList<ClassificationDTO> getAllClassificationDTO() throws IOException {
        Protocol requestAllMyClassificationDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.CLASSIFICATION, 0, null);
        dos.write(requestAllMyClassificationDTOs.getBytes());
        //모든 카테고리DTO 리스트를 가져옴

        ArrayList<ClassificationDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((ClassificationDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public <T> ArrayList<ClassificationDTO> getAllClassificationDTO(T info) throws IOException {
        Protocol requestAllMyClassificationDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.CLASSIFICATION, 0, (DTO) info);
        dos.write(requestAllMyClassificationDTOs.getBytes());
        //info가 지닌 모든 카테고리DTO 리스트를 가져옴

        ArrayList<ClassificationDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            if (dis.read(readBuf) != -1) {
                DTOs.add((ClassificationDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }

        return DTOs;
    }

    public ArrayList<UserDTO> getAllOwnerDTO() throws IOException {
        Protocol requestAllUserDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.USER, 0, null);
        dos.write(requestAllUserDTOs.getBytes());

        ArrayList<UserDTO> DTOs = new ArrayList<>();
        int listLength = 0;
        if (dis.read(readBuf) != -1) {
            listLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            UserDTO cur = null;

            if (dis.read(readBuf) != -1) {
                cur = (UserDTO) new Protocol(readBuf).getData();
                send_ack();
            }

            String curAuthority = cur.getAuthorityEnum().getName();
            if (curAuthority.equals("OWNER")) {
                DTOs.add(cur);
            }
        }

        return DTOs;
    }

    public ArrayList<UserDTO> getAllOwnerAndUserDTO() throws IOException {
        Protocol requestAllUserDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.USER, 0, null);
        dos.write(requestAllUserDTOs.getBytes());

        ArrayList<UserDTO> DTOs = new ArrayList<>();
        int listLength = 0;

        if (dis.read(readBuf) != -1) {
            Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        for(int i = 0; i < listLength; i++) {
            UserDTO cur = null;
            if (dis.read(readBuf) != -1) {
                cur = (UserDTO) new Protocol(readBuf).getData();
                send_ack();
            }

            String curAuthority = cur.getAuthorityEnum().getName();
            if (curAuthority.equals("OWNER") || curAuthority.equals("USER")) {
                DTOs.add(cur);
            }
        }

        return DTOs;
    }

    public void registOwnerDetermination() throws IOException {
        ArrayList<UserDTO> DTOs = getAllOwnerDTO();

        while(DTOs.size() > 0) {
            viewer.viewUserDTOs(DTOs);
            int idx = viewer.getIdx();

            if (0 <= idx && idx < DTOs.size()) {
                while (true) {
                    String ans = viewer.getDetermination();

                    if (ans.equals("Y") || ans.equals("y")) {
                        viewer.showAcceptMessage();
                        Protocol registAccept = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.USER | ProtocolCode.ACCEPT), 0, DTOs.get(idx));
                        dos.write(registAccept.getBytes());
                        DTOs.remove(idx);
                        break;
                    }

                    else if (ans.equals("N") || ans.equals("n")) {
                        viewer.showRefusalMessage();
                        Protocol registRefuse = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.USER | ProtocolCode.REFUSAL), 0, DTOs.get(idx));
                        dos.write(registRefuse.getBytes());
                        DTOs.remove(idx);
                        break;
                    }

                    else {
                        System.out.println(ErrorMessage.OUT_OF_BOUND);
                    }
                }
            }

            else {
                break;
            }
        }
    }

    public void registStoreDetermination() throws IOException {
        ArrayList<StoreDTO> DTOs = getAllStoreDTO();

        while(DTOs.size() > 0) {
            viewer.viewStoreDTOs(DTOs);
            int idx = viewer.getIdx();

            if (0 <= idx && idx < DTOs.size()) {
                while (true) {
                    String ans = viewer.getDetermination();

                    if (ans.equals("Y") || ans.equals("y")) {
                        viewer.showAcceptMessage();
                        Protocol registAccept = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.STORE | ProtocolCode.ACCEPT), 0, DTOs.get(idx));
                        //전달한 가게 등록 DTO의 정보를 가게 등록 테이블에서 삭제하고 가게 테이블에 등록하시오.
                        dos.write(registAccept.getBytes());
                        DTOs.remove(idx);
                        break;
                    }

                    else if (ans.equals("N") || ans.equals("n")) {
                        viewer.showRefusalMessage();
                        Protocol registRefuse = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.STORE | ProtocolCode.REFUSAL), 0, DTOs.get(idx));
                        //전달한 가게 등록 DTO의 정보를 가게 등록 테이블에서 삭제하시오.
                        dos.write(registRefuse.getBytes());
                        DTOs.remove(idx);
                        break;
                    }

                    else {
                        System.out.println(ErrorMessage.OUT_OF_BOUND);
                    }
                }
            }

            else {
                break;
            }
        }
    }

    public void registMenuDetermination() throws IOException {
        ArrayList<MenuDTO> DTOs = getAllMenuDTO();

        while(DTOs.size() > 0) {
            viewer.viewMenuDTOs(DTOs);
            int idx = viewer.getIdx();

            if (0 <= idx && idx < DTOs.size()) {
                while (true) {
                    String ans = viewer.getDetermination();

                    if (ans.equals("Y") || ans.equals("y")) {
                        viewer.showAcceptMessage();
                        Protocol registAccept = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.MENU | ProtocolCode.ACCEPT), 0, DTOs.get(idx));
                        dos.write(registAccept.getBytes());
                        DTOs.remove(idx);
                        break;
                    }

                    else if (ans.equals("N") || ans.equals("n")) {
                        viewer.showRefusalMessage();
                        Protocol registRefuse = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.MENU | ProtocolCode.REFUSAL), 0, DTOs.get(idx));
                        dos.write(registRefuse.getBytes());
                        DTOs.remove(idx);
                        break;
                    }

                    else {
                        System.out.println(ErrorMessage.OUT_OF_BOUND);
                    }
                }
            }

            else {
                break;
            }
        }
    }

    public void viewReview(UserDTO userInfo) throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO(userInfo);
        viewer.viewStoreDTOs(storeDTOs);
        int idx = viewer.getIdx();
        int curPage = 1;

        if (idx >= storeDTOs.size() || idx < 0) {
            return;
        }
        if (!storeDTOs.get(idx).getStatusEnum().equals(RegistStatus.ACCEPT)) {
            System.out.println("승인 되지 않은 가게입니다.");
            return;
        }

        Protocol requestReview = new Protocol(ProtocolType.SEARCH, ProtocolCode.REVIEW, 0, storeDTOs.get(idx));
        dos.write(requestReview.getBytes());

        int maxPage = 0;
        if (dis.read(readBuf) != -1) {
            maxPage = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        while(true) {
            dos.write(Serializer.intToByteArray(curPage));;
            receive_ack();

            int reviewListLength = 0;
            if (dis.read(readBuf) != -1) {
                reviewListLength = Deserializer.byteArrayToInt(readBuf);
                send_ack();
            }

            ArrayList<ReviewDTO> reviewDTOs = new ArrayList<>();
            for (int j = 0; j < reviewListLength; j++) {
                if (dis.read(readBuf) != -1) {
                    reviewDTOs.add((ReviewDTO) new Protocol(readBuf).getData());
                    send_ack();
                }
            }

            viewer.viewReviewDTOs(reviewDTOs);
            viewer.viewPage(curPage, maxPage, 5);
            curPage = viewer.getNextPage();

            if (!(1 <= curPage && curPage <= maxPage)) {
                dos.write(Serializer.intToByteArray(curPage));
                break;
            }
        }
    }

    public void adminStatisticsView() throws IOException {
        viewer.viewStatisticsDTOs(getAllStatDTO());
    }

    public void ownerStatisticsView(UserDTO me) throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO(me);
        viewer.viewStoreDTOs(storeDTOs);
        int idx = viewer.getIdx();

        if(0 <= idx && idx < storeDTOs.size()) {
            Protocol requestStat = new Protocol(ProtocolType.SEARCH, (byte)(ProtocolCode.STORE | ProtocolCode.HISTORY), 0, storeDTOs.get(idx));
            dos.write(requestStat.getBytes());

            int listLength = 0;
            if(dis.read(readBuf) != -1) {
                listLength = Deserializer.byteArrayToInt(readBuf);
                send_ack();
            }

            ArrayList<StatisticsDTO> statDTOs = new ArrayList<>();
            for(int i = 0; i < listLength; i++) {
                if(dis.read(readBuf) != -1) {
                    statDTOs.add((StatisticsDTO) new Protocol(readBuf).getData());
                    send_ack();
                }
            }

            viewer.viewStatisticsDTOs(statDTOs);
        }

        else {
            System.out.println(ErrorMessage.OUT_OF_BOUND);
        }
    }

    public void registStore(UserDTO userInfo) throws IOException {
        StringTokenizer st;
        String[] storeInfo = viewer.getStoreInfo();

        StoreDTO newStore = new StoreDTO();
        newStore.setName(storeInfo[0]);
        newStore.setComment(storeInfo[1]);
        newStore.setAddress(storeInfo[2]);
        newStore.setPhone(storeInfo[3]);
        st = new StringTokenizer(storeInfo[4]);
        newStore.setOpen_time(LocalDateTime.of(1, 1, 1, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
        st = new StringTokenizer(storeInfo[5]);
        newStore.setClose_time(LocalDateTime.of(1, 1, 1, Integer.parseInt(st.nextToken()), Integer.parseInt(st.nextToken())));
        newStore.setUser_pk(userInfo.getPk());

        Protocol requestStoreRegist = new Protocol(ProtocolType.REGISTER, ProtocolCode.STORE, 0, newStore);
        dos.write(requestStoreRegist.getBytes());
        responseReceive();
    }

    public void registMenuAndOption(UserDTO userInfo) throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO(userInfo);
        StoreDTO storeInfo = viewer.selectStore(storeDTOs);

        if (!storeInfo.getStatusEnum().equals(RegistStatus.ACCEPT)) {
            System.out.println("승인 되지 않은 가게입니다.");
            return;
        }

        int option;
        do {
            option = viewer.registMenuAndOptionScreen();

            if(option == 1) {
                registMenu(storeInfo);
            }

            else if(option == 2) {
                registOption(storeInfo);
            }

            else if(option == 3) {
                registClassification(storeInfo);
            }
        } while(option == 1 || option == 2 || option == 3);
    }

    public void registMenu(StoreDTO storeInfo) throws IOException {
        ArrayList<ClassificationDTO> classificationDTOs = getAllClassificationDTO(storeInfo);
        ClassificationDTO selectedClass = viewer.selectClassification(classificationDTOs);

        ArrayList<DetailsDTO> optionDTOs = getAllOptionDTO(storeInfo);
        ArrayList<Integer> selectedOption = viewer.selectOption(optionDTOs);

        MenuDTO newMenu = viewer.setNewMenu(selectedClass);

        Protocol registMenu = new Protocol(ProtocolType.REGISTER, ProtocolCode.MENU, 0, newMenu);
        dos.write(registMenu.getBytes());
        //옵션을 제외한 정보들을 먼저 보내고

        dos.write(Serializer.intToByteArray(selectedOption.size()));
        receive_ack();
        //보낼 옵션 리스트의 크기
        for(int i = 0; i < selectedOption.size(); i++) {
            try {
                dos.write(new Protocol(ProtocolType.RESPONSE, ProtocolCode.OPTION, 0, optionDTOs.get(selectedOption.get(i))).getBytes());
                receive_ack();
            } catch (Exception e) {
                e.printStackTrace();
            }

            //옵션들의 정보들을 쭉 보낸다.
        }
    }

    public void registOption(StoreDTO storeInfo) throws IOException {
        DetailsDTO newOption = viewer.setNewOption(storeInfo);

        Protocol registOption = new Protocol(ProtocolType.REGISTER, ProtocolCode.OPTION, 0, newOption);
        dos.write(registOption.getBytes());
        responseReceive();
    }

    public void registClassification(StoreDTO storeInfo) throws IOException {
        ClassificationDTO newClassification = viewer.setNewClassification(storeInfo);

        Protocol registClass = new Protocol(ProtocolType.REGISTER, ProtocolCode.CLASSIFICATION, 0, newClassification);
        dos.write(registClass.getBytes());
        responseReceive();
    }

    private boolean receive_ack() throws IOException {
        if (dis.read(readBuf) != -1) {
            return true;
        }
        return false;
    }

    public void registOrder(UserDTO userInfo) throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO();
        viewer.viewStoreDTOs(storeDTOs);
        int storeIdx = viewer.getIdx();

        if(0 <= storeIdx && storeIdx < storeDTOs.size()) {
            LocalDateTime now = LocalDateTime.of(1, 1, 1, LocalDateTime.now().getHour(), LocalDateTime.now().getMinute());
            if (storeDTOs.get(storeIdx).getOpen_time().toLocalTime().isBefore(now.toLocalTime()) && storeDTOs.get(storeIdx).getClose_time().toLocalTime().isAfter(now.toLocalTime())) {
                TotalOrdersDTO temp = new TotalOrdersDTO();
                temp.setStore_id(storeDTOs.get(storeIdx).getId());
                Protocol sendTotalOrder = new Protocol(ProtocolType.REGISTER, ProtocolCode.ORDER, 0, temp);
                dos.write(sendTotalOrder.getBytes());

                ArrayList<MenuDTO> menuDTOs = new ArrayList<>();
                Protocol requestAllMyMenuDTOs = new Protocol(ProtocolType.SEARCH, ProtocolCode.MENU, 0, storeDTOs.get(storeIdx));
                dos.write(requestAllMyMenuDTOs.getBytes());
                receive_ack();

                int classificationListLength = 0;
                if(dis.read(readBuf) != -1) {
                    classificationListLength = Deserializer.byteArrayToInt(readBuf);
                    send_ack();
                }

                ArrayList<ClassificationDTO> classificationDTOs = new ArrayList<>();
                int menuDTOIdx = 0;
                for(int i = 0; i < classificationListLength; i++) {
                    if(dis.read(readBuf) != -1) {
                        classificationDTOs.add((ClassificationDTO) new Protocol(readBuf).getData());
                        send_ack();

                        int menuListLength = 0;
                        if(dis.read(readBuf) != -1) {
                            menuListLength = Deserializer.byteArrayToInt(readBuf);
                            send_ack();
                        }

                        ArrayList<MenuDTO> tempMenu = new ArrayList<>();
                        for(int j = 0; j < menuListLength; j++) {
                            if(dis.read(readBuf) != -1) {
                                MenuDTO cur = (MenuDTO) new Protocol(readBuf).getData();
                                menuDTOs.add(cur);
                                tempMenu.add(cur);
                                send_ack();
                            }
                        }

                        viewer.viewClassificationDTO(classificationDTOs.get(i));
                        viewer.viewMenuDTOs(tempMenu, menuDTOIdx);
                        menuDTOIdx += menuListLength;
                    }
                }

                ArrayList<OrdersDTO> ordersDTOs = new ArrayList<>();
                int idx = viewer.getIdx();
                Protocol options = new Protocol(ProtocolType.SEARCH, ProtocolCode.OPTION, 0, storeDTOs.get(storeIdx));
                dos.write(options.getBytes());
                int optionListLength = 0; // TODO

                if(dis.read(readBuf) != -1) {
                    optionListLength = Deserializer.byteArrayToInt(readBuf);
                    send_ack();
                }

                ArrayList<DetailsDTO> optionDTOs = new ArrayList<>();
                for(int i = 0; i < optionListLength; i++) {
                    if(dis.read(readBuf) != -1) {
                        optionDTOs.add((DetailsDTO) new Protocol(readBuf).getData());
                        send_ack();
                    }
                }

                while(0 <= idx && idx < menuDTOs.size()) {
                    ArrayList<Integer> optionIdx = viewer.selectOption(optionDTOs);
                    String optionToString = "";
                    Integer price = 0;
                    for(int i = 0; i < optionIdx.size(); i++) {
                        optionToString += optionIdx.get(i);
                        price += optionDTOs.get(optionIdx.get(i)).getPrice();
                    }

                    OrdersDTO curOrder = OrdersDTO.builder()
                            .details(optionToString)
                            .price(price + menuDTOs.get(idx).getPrice())
                            .menu_id(menuDTOs.get(idx).getId())
                            .total_orders_id(temp.getId())
                            .build();
                    ordersDTOs.add(curOrder);
                    viewer.viewMenuDTOs(menuDTOs);
                    idx = viewer.getIdx();
                }

                dos.write(Serializer.intToByteArray(ordersDTOs.size()));
                receive_ack();

                if (ordersDTOs.size() == 0) {
                    return;
                }

                for(int i = 0; i < ordersDTOs.size(); i++) {
                    dos.write(new Protocol(ProtocolType.RESPONSE, ProtocolCode.ORDER, 0, ordersDTOs.get(i)).getBytes());
                    receive_ack();
                }
                responseReceive();
            } else {
                System.out.println(ErrorMessage.OUT_OF_TIME);
            }
        } else {
            System.out.println(ErrorMessage.OUT_OF_BOUND);
        }
    }

    public void registReview(UserDTO userInfo) throws IOException {
        ArrayList<TotalOrdersDTO> DTOs = getAllTotalOrderDTO(userInfo);

        while(true) {
            viewer.viewTotalOrderDTOs(DTOs);
            int select = viewer.getIdx();

            if (0 <= select && select < DTOs.size()) {
                Pair<String, Integer> reviewInfo = viewer.getReviewInfo();

                ReviewDTO newReview = ReviewDTO.builder()
                        .comment(reviewInfo.first())
                        .regdate(LocalDateTime.now())
                        .star_rating(reviewInfo.second())
                        .user_pk(userInfo.getPk())
                        .total_orders_id(DTOs.get(select).getId())
                        .build();

                Protocol registReview = new Protocol(ProtocolType.REGISTER, ProtocolCode.REVIEW, 0, newReview);
                //데이터로 전달한 녀석을 리뷰 테이블에 insert
                dos.write(registReview.getBytes());
                responseReceive();
            }

            else {
                break;
            }
        }
    }

    public void orderDetermination(UserDTO userInfo) throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO(userInfo);
        viewer.viewStoreDTOs(storeDTOs);
        int storeIdx = viewer.getIdx();

        if(0 <= storeIdx && storeIdx < storeDTOs.size()) {
            ArrayList<TotalOrdersDTO> orderDTOs;
            while (true) {
                orderDTOs = getAllTotalOrderDTO(storeDTOs.get(storeIdx));
                viewer.viewTotalOrderDTOs(orderDTOs);
                int idx = viewer.getIdx();

                if (0 <= idx && idx < orderDTOs.size()) {
                    while (true) {
                        String ans = viewer.getDetermination();

                        if (ans.equals("Y") || ans.equals("y")) {
                            viewer.showAcceptMessage();
                            if (orderDTOs.get(idx).getStatusEnum().equals(OrdersStatus.IN_DELIVERY)) {
                                orderDTOs.get(idx).setStatus(OrdersStatus.COMPLETE);
                            }
                            Protocol orderAccept = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.ORDER | ProtocolCode.ACCEPT), 0, orderDTOs.get(idx));
                            //전달한 DTO의 Status를 변경
                            dos.write(orderAccept.getBytes());
                            orderDTOs.remove(idx);
                            break;
                        }

                        else if (ans.equals("N") || ans.equals("n")) {
                            viewer.showRefusalMessage();
                            Protocol registRefuse = new Protocol(ProtocolType.RESPONSE, (byte) (ProtocolCode.ORDER | ProtocolCode.REFUSAL), 0, orderDTOs.get(idx));
                            //전달한 DTO의 Status를 변경
                            dos.write(registRefuse.getBytes());
                            orderDTOs.remove(idx);
                            break;
                        }

                        else {
                            System.out.println(ErrorMessage.OUT_OF_BOUND);
                        }
                    }
                }

                else {
                    break;
                }
            }
        }
    }

    public void modificationUser(UserDTO userInfo) throws IOException {
        //개인정보 및 비밀번호 수정
        boolean escapeFlag = false;
        while(!escapeFlag) {
            int option = viewer.modifiUserScreenAndGetOption();

            switch (option) {
                case 1:
                    viewer.changeUserPW(userInfo);
                    break;

                case 2:
                    viewer.changeUserName(userInfo);
                    break;

                case 3:
                    viewer.changeUserAge(userInfo);
                    break;

                case 4:
                    viewer.changeUserPhoneNumber(userInfo);
                    break;

                case 5:
                    escapeFlag = true;
                    break;

                default:
                    System.out.println(ErrorMessage.OUT_OF_BOUND);
                    break;
            }
        }

        Protocol userModification = new Protocol(ProtocolType.MODIFICATION, ProtocolCode.USER, 0, userInfo);
        //데이터로 전달한 DTO로 변경, pk로 찾아오면 될것임.
        dos.write(userModification.getBytes());

        if (dis.read(readBuf) != -1) {
            Protocol protocol = new Protocol(readBuf);

            if (protocol.getCode() == ProtocolCode.ACCEPT) {
                viewer.showSaveMessage();
            }
            else {
                System.out.println("실패!"); // TODO
            }
        }
    }

    public void modificationMenu(DTO info) throws IOException {
        ArrayList<MenuDTO> menuDTOs = viewMenu(info);
        int idx = viewer.getIdx();
        MenuDTO selected = menuDTOs.get(idx);
        Pair<String, Integer> modificationInfo = viewer.modificationMenuScreen(selected);
        selected.setName(modificationInfo.first());
        selected.setPrice(modificationInfo.second());

        Protocol requestModification = new Protocol(ProtocolType.MODIFICATION, ProtocolCode.MENU, 0, selected);
        //전달한 메뉴 DTO를 update
        dos.write(requestModification.getBytes());
    }

    public void setRunningTime(UserDTO userInfo) throws IOException {
        ArrayList<StoreDTO> DTOs = getAllStoreDTO(userInfo);

        while(DTOs.size() > 0) {
            viewer.viewStoreDTOs(DTOs);
            int idx = viewer.getIdx();

            if (0 <= idx && idx < DTOs.size()) {
                int[] changeTimeInfo = viewer.getChangeTimeInfo();

                LocalDateTime newOpenTime = LocalDateTime.of(1, 1, 1, changeTimeInfo[0], changeTimeInfo[1]);
                LocalDateTime newCloseTime = LocalDateTime.of(1, 1, 1, changeTimeInfo[2], changeTimeInfo[3]);
                DTOs.get(idx).setOpen_time(newOpenTime);
                DTOs.get(idx).setClose_time(newCloseTime);
                Protocol requestSetRunningTime = new Protocol(ProtocolType.MODIFICATION, ProtocolCode.STORE, 0, DTOs.get(idx));
                //보내진 DTO의 내용으로 스토어 수정
                dos.write(requestSetRunningTime.getBytes());
            }

            else {
                break;
            }
        }
    }

    public void orderCancel(UserDTO userInfo) throws IOException {
        while(true) {
            ArrayList<TotalOrdersDTO> DTOs = getAllTotalOrderDTO(userInfo);
            viewer.viewTotalOrderDTOs(DTOs);
            int select = viewer.getIdx();

            if(0 <= select && select < DTOs.size()) {
                // DTOs.get(select).setStatus(OrdersStatus.CANCEL.getCode());
                Protocol requestCancel = new Protocol(ProtocolType.MODIFICATION, ProtocolCode.ORDER, 0, DTOs.get(select));
                //데이터로 전달한 DTO로 변경
                dos.write(requestCancel.getBytes());
                responseReceive();
            }

            else {
                break;
            }
        }

    }

    public void viewStore() throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO();
        viewer.viewStoreDTOs(storeDTOs);
        int idx = viewer.getIdx();

        int curPage = 1;

        if (idx >= storeDTOs.size() || idx < 0) {
            return;
        }

        Protocol requestReview = new Protocol(ProtocolType.SEARCH, ProtocolCode.REVIEW, 0, storeDTOs.get(idx));
        dos.write(requestReview.getBytes());

        int maxPage = 0;
        if (dis.read(readBuf) != -1) {
            maxPage = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        while(true) {
            dos.write(Serializer.intToByteArray(curPage));;
            receive_ack();

            int reviewListLength = 0;
            if (dis.read(readBuf) != -1) {
                reviewListLength = Deserializer.byteArrayToInt(readBuf);
                send_ack();
            }

            ArrayList<ReviewDTO> reviewDTOs = new ArrayList<>();
            for (int j = 0; j < reviewListLength; j++) {
                if (dis.read(readBuf) != -1) {
                    reviewDTOs.add((ReviewDTO) new Protocol(readBuf).getData());
                    send_ack();
                }
            }

            viewer.viewReviewDTOs(reviewDTOs);
            viewer.viewPage(curPage, maxPage, 5);
            curPage = viewer.getNextPage();

            if (!(1 <= curPage && curPage <= maxPage)) {
                dos.write(Serializer.intToByteArray(curPage));
                break;
            }
        }
    }

    public ArrayList<MenuDTO> viewMenu(DTO info) throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO(info);
        viewer.viewStoreDTOs(storeDTOs);
        int idx = viewer.getIdx();

        Protocol requestMenu = new Protocol(ProtocolType.SEARCH, ProtocolCode.MENU, 0, storeDTOs.get(idx));
        dos.write(requestMenu.getBytes());

        int classificationListLength = 0;

        if (dis.read(readBuf) != -1) {
            Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }
        ArrayList<ClassificationDTO> classificationDTOs = new ArrayList<>();
        ArrayList<MenuDTO> result = new ArrayList<>();
        for(int i = 0; i < classificationListLength; i++) {
            if (dis.read(readBuf) != -1) {
                classificationDTOs.add((ClassificationDTO) new Protocol(readBuf).getData());
                send_ack();
            }

            int menuListLength = 0;
            if (dis.read(readBuf) != -1) {
                Deserializer.byteArrayToInt(readBuf);
                send_ack();
            }

            ArrayList<MenuDTO> menuDTOs = new ArrayList<>();
            for (int j = 0; j < menuListLength; j++) {
                MenuDTO cur = null;
                if (dis.read(readBuf) != -1) {
                    cur = (MenuDTO) new Protocol(readBuf).getData();
                    send_ack();
                }
                menuDTOs.add(cur);
                result.add(cur);
            }

            viewer.viewClassificationDTO(classificationDTOs.get(i));
            viewer.viewMenuDTOs(menuDTOs, result.size());
        }

        return result;
    }

    public void viewOrder(UserDTO info) throws IOException {
        viewer.viewTotalOrderDTOs(getAllTotalOrderDTO(info));
    }

    public void registRecommnet(UserDTO me) throws IOException {
        ArrayList<StoreDTO> storeDTOs = getAllStoreDTO(me);
        viewer.viewStoreDTOs(storeDTOs);
        int idx = viewer.getIdx();

        if(idx >= storeDTOs.size())
            return;

        Protocol requestReview = new Protocol(ProtocolType.SEARCH, (byte) (ProtocolCode.REVIEW | ProtocolCode.HISTORY), 0, storeDTOs.get(idx));
        dos.write(requestReview.getBytes());

        int reviewListLength = 0;
        if(dis.read(readBuf) != -1) {
            reviewListLength = Deserializer.byteArrayToInt(readBuf);
            send_ack();
        }

        ArrayList<ReviewDTO> reviewDTOs = new ArrayList<>();
        for(int i = 0; i < reviewListLength; i++) {
            if(dis.read(readBuf) != -1) {
                reviewDTOs.add((ReviewDTO) new Protocol(readBuf).getData());
                send_ack();
            }
        }


        viewer.viewReviewDTOs(reviewDTOs);
        int reviewIdx = viewer.getReviewIdx();
        while(0 <= reviewIdx && reviewIdx < reviewDTOs.size()) {
            String comment = viewer.getComment();
            reviewDTOs.get(reviewIdx).setOwner_comment(comment);
            Protocol modifiReview = new Protocol(ProtocolType.MODIFICATION, ProtocolCode.REVIEW, 0, reviewDTOs.get(reviewIdx));
            dos.write(modifiReview.getBytes());
            responseReceive();

            viewer.viewReviewDTOs(reviewDTOs);
            reviewIdx = viewer.getReviewIdx();
        }
    }
}