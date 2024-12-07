package network;

import org.testng.internal.collections.Pair;
import persistence.dto.*;
import persistence.enums.User_role;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

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
        userInfo.setLogin_id(keyInput.readLine());
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

    public String[] getEvent_scheduleInfo() throws IOException {
        String[] result = new String[3];

        System.out.println("[선발 일정 등록]");
        System.out.print("일정 제목 : ");
        result[0] = keyInput.readLine();
        System.out.print("시작일 : ");
        result[1] = keyInput.readLine();
        System.out.print("종료일 : ");
        result[2] = keyInput.readLine();

        return result;
    }

    public void viewEvent_scheduleDTOs(ArrayList<EventDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.event_scheduleDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public String[] getDormitory_feeAndmealInfo() throws IOException {
        String[] result = new String[4];

        System.out.println("[생활관 사용료 및 급식비 등록]");
        System.out.print("생활관명 : ");
        result[0] = keyInput.readLine();
        System.out.print("생활관비 : ");
        result[1] = keyInput.readLine();
        System.out.print("식사 유형 (n일식)  : ");
        result[2] = keyInput.readLine();
        System.out.print("급식비  : ");
        result[3] = keyInput.readLine();

        return result;
    }

    public void viewDormitoryDTOs(ArrayList<DormitoryDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.DormitoryDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public int registMenuAndOptionScreen() throws IOException {
        System.out.println("[1] 메뉴 등록");
        System.out.println("[2] 옵션 등록");
        System.out.println("[3] 분류 등록");
        System.out.print("입력(범위 외 입력 시 종료) : ");

        return Integer.parseInt(keyInput.readLine());
    }

    public int getDormitory_id(ArrayList<DormitoryDTO> DTOs) throws IOException {
        viewDormitoryDTOs(DTOs);
        System.out.println("조회할 생활관 ID 입력: ");
        return Integer.parseInt(keyInput.readLine());
    }

    public void viewApplicationListDTOs(ArrayList<ApplicationListDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.ApplicationListDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewPaymentListDTOs(ArrayList<paymentListDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.PaymentListDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewUnpaymentListDTOs(ArrayList<paymentListDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.PaymentListDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewPassed(ArrayList<ApplicationListDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            if(DTOs.get(i).getApplicationDTO().getApplicationStatus().equals(ApplicationDTO.ApplicationStatus.승인)){
                System.out.println("[" + i + "] " + DTOToString.ApplicationListDTOToString(DTOs.get(i)));
            }
        }
        System.out.println();
    }

    public int getStudent_id() throws IOException {
        System.out.println("환불할 거주자의 학번 입력: ");
        return Integer.parseInt(keyInput.readLine());
    }

    public int getSelectScheduleOrCost() throws IOException {
        System.out.println("[1] 선발 일정 조회");
        System.out.println("[2] 기숙사 비용 조회");
        System.out.println("입력: ");
        return Integer.parseInt(keyInput.readLine());
    }

    public void viewEventDTO(ArrayList<EventDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.viewEventDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public StoreDTO selectStore(ArrayList<StoreDTO> storeDTOs) throws IOException {
        viewStoreDTOs(storeDTOs);
        StoreDTO storeInfo = null;
        while(true) {
            System.out.print("가게를 선택해주세요 : ");
            int select = Integer.parseInt(keyInput.readLine());

            if(0 <= select && select <  storeDTOs.size()) {
                storeInfo = storeDTOs.get(select);
                break;
            }

            else {
                System.out.println(ErrorMessage.OUT_OF_BOUND);
            }
        }

        return storeInfo;
    }

    public ClassificationDTO selectClassification(ArrayList<ClassificationDTO> classificationDTOs) throws IOException {
        viewClassificationDTOs(classificationDTOs);
        ClassificationDTO selectedClass = null;

        while(true) {
            System.out.print("분류를 선택해주세요 : ");
            int select = Integer.parseInt(keyInput.readLine());

            if(0 <= select && select < classificationDTOs.size()) {
                selectedClass = classificationDTOs.get(select);
                break;
            }

            else {
                System.out.println(ErrorMessage.OUT_OF_BOUND);
            }
        }

        return selectedClass;
    }

    public ArrayList<Integer> selectOption(ArrayList<DetailsDTO> optionDTOs) throws IOException {
        ArrayList<Integer> selectedOption = new ArrayList<>();

        viewOptionDTOs(optionDTOs);
        System.out.println("등록할 옵션을 모두 선택하세요");
        System.out.println("범위 바깥 값을 입력하거나 모든 옵션을 선택하면 입력이 종료됩니다.");

        while(selectedOption.size() < optionDTOs.size()) {
            System.out.print("입력 : ");
            String curInput = keyInput.readLine();
            if(!curInput.equals("")) {
                int n = Integer.parseInt(curInput);
                if(0 <= n && n < optionDTOs.size()) {
                    selectedOption.add(n);
                }

                else {
                    break;
                }
            }

            else {
                break;
            }
        }

        return selectedOption;
    }

    public MenuDTO setNewMenu(ClassificationDTO selectedClass) throws IOException {
        System.out.println("메뉴를 등록합니다.");
        System.out.print("메뉴 이름 : ");
        String name = keyInput.readLine();
        System.out.print("가격 : ");
        int price = Integer.parseInt(keyInput.readLine());
        System.out.print("수량 : ");
        int stock = Integer.parseInt(keyInput.readLine());

        MenuDTO newMenu = MenuDTO.builder()
                .classification_id(selectedClass.getId())
                .name(name)
                .price(price)
                .stock(stock)
                .build();
        newMenu.setClassification_id(selectedClass.getId());
        newMenu.setName(name);
        newMenu.setPrice(price);
        newMenu.setStock(stock);

        return newMenu;
    }

    public DetailsDTO setNewOption(StoreDTO storeInfo) throws IOException {
        System.out.println("옵션을 등록합니다.");
        System.out.print("옵션명 : ");
        String name = keyInput.readLine();
        System.out.print("추가 금액 : ");
        int price = Integer.parseInt(keyInput.readLine());

        DetailsDTO newOption = DetailsDTO.builder()
                .name(name)
                .price(price)
                .store_id(storeInfo.getId())
                .build();

        return newOption;
    }

    public ClassificationDTO setNewClassification(StoreDTO storeInfo) throws IOException {
        System.out.println("분류를 등록합니다.");
        System.out.print("분류명 : ");
        String name = keyInput.readLine();

        ClassificationDTO newClassification = ClassificationDTO.builder()
                .name(name)
                .store_id(storeInfo.getId())
                .build();


        return newClassification;
    }

    public int getIdx() throws IOException {
        System.out.print("대상을 선택하세요 : ");
        return Integer.parseInt(keyInput.readLine());
    }

    public ArrayList<Integer> getOptionIdxes(ArrayList<DetailsDTO> optionDTOs) throws IOException {
        viewOptionDTOs(optionDTOs);
        ArrayList<Integer> optionIdxes = new ArrayList<>();

        System.out.println("옵션을 선택하세요");
        System.out.println("범위 바깥 값을 입력하거나 모든 옵션을 선택하면 입력이 종료됩니다.");
        while(optionIdxes.size() < optionDTOs.size()) {
            System.out.print("입력 : ");
            optionIdxes.add(Integer.parseInt(keyInput.readLine()));
        }

        return optionIdxes;
    }

    public Pair<String, Integer> getReviewInfo() throws IOException {
        System.out.print("리뷰 내용 입력 : ");
        String contents = keyInput.readLine();
        System.out.print("별점 입력(1 ~ 5) : ");
        int starRank = Integer.parseInt(keyInput.readLine());

        return new Pair<>(contents, starRank);
    }

    public String getDetermination() throws IOException {
        System.out.println("승인 : Y/y, 거절 : N/n");
        System.out.print("입력 : ");
        return keyInput.readLine();
    }

    public int[] getChangeTimeInfo() throws IOException {
        int[] changeTimeInfo = new int[4];
        StringTokenizer st;

        System.out.print("변경할 개점 시간 : ");
        st = new StringTokenizer(keyInput.readLine());
        changeTimeInfo[0] = Integer.parseInt(st.nextToken());
        changeTimeInfo[1] = Integer.parseInt(st.nextToken());

        System.out.print("변경할 폐점 시간 : ");
        st = new StringTokenizer(keyInput.readLine());
        changeTimeInfo[2] = Integer.parseInt(st.nextToken());
        changeTimeInfo[3] = Integer.parseInt(st.nextToken());

        return changeTimeInfo;
    }

    public String[] getStoreInfo() throws IOException {
        String[] result = new String[6];

        System.out.println("[가게 등록]");
        System.out.print("상호명 : ");
        result[0] = keyInput.readLine();
        System.out.print("간단한 가게 소개 : ");
        result[1] = keyInput.readLine();
        System.out.print("주소 : ");
        result[2] = keyInput.readLine();
        System.out.print("가게 전화번호 : ");
        result[3] = keyInput.readLine();
        System.out.print("오픈 시간 : ");
        result[4] = keyInput.readLine();
        System.out.print("닫는 시간 : ");
        result[5] = keyInput.readLine();

        return result;
    }

    public void showOrderCompleteMessage() {
        System.out.println("주문이 정상적으로 등록되었습니다.");
        System.out.println();
    }

    public void showReviewCompleteMessage() {
        System.out.println("리뷰가 등록되었습니다.");
        System.out.println();
    }

    public void showRegistUserCompleteMessage() {
        System.out.println("회원가입이 완료되었습니다.");
        System.out.println();
    }

    public void showAcceptMessage() {
        System.out.println("승인되었습니다.\n");
    }

    public void showRefusalMessage() {
        System.out.println("거절되었습니다.\n");
    }

    public void showSaveMessage() {
        System.out.println("변경사항이 저장되었습니다.");
    }

    public void viewClassificationDTO(ClassificationDTO DTO) {
        System.out.println(DTOToString.classificationDTOToString(DTO));
    }

    public void viewClassificationDTOs(ArrayList<ClassificationDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.classificationDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewOptionDTOs(ArrayList<DetailsDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.optionDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewMenuDTOs(ArrayList<MenuDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.menuDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewMenuDTOs(ArrayList<MenuDTO> DTOs, int startIdx) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + (i + startIdx) + "] " + DTOToString.menuDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewReviewDTOs(ArrayList<ReviewDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.reviewDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewStatisticsDTOs(ArrayList<StatisticsDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.statisticsDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewStoreDTOs(ArrayList<StoreDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.storeDTOToString(DTOs.get(i)) + "\n");
        }
        System.out.println();
    }

    public void viewOrderDTOs(ArrayList<OrdersDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.orderDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewTotalOrderDTOs(ArrayList<TotalOrdersDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.totalOrderDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewUserDTOs(ArrayList<UserDTO> DTOs) {
        for(int i = 0; i < DTOs.size(); i++) {
            System.out.println("[" + i + "] " + DTOToString.userDTOToString(DTOs.get(i)));
        }
        System.out.println();
    }

    public void viewPage(int curPage, int maxPage, int pageWidth) {
        int gap = pageWidth / 2;
        int startPage;

        if(curPage <= gap) {
            startPage = 1;
        } else {
            startPage = curPage - gap;
        }

        if(startPage != 1) {
            System.out.print(1 + " ... ");
        }

        for(int i = startPage; i < startPage + pageWidth && i <= maxPage; i++) {
            if(i == curPage) {
                System.out.print("[" + i + "] ");
            } else {
                System.out.print(i + " ");
            }
        }

        if(pageWidth % 2 == 0) {
            if(maxPage - curPage >= gap) {
                System.out.print("... " + maxPage);
            }
        } else {
            if(maxPage - curPage > gap) {
                System.out.print("... " + maxPage);
            }
        }
        System.out.println();
    }

    public int getNextPage() throws IOException {
        System.out.print("페이지 입력(범위 외 입력 시 종료) : ");
        return Integer.parseInt(keyInput.readLine());
    }

    public Pair<String, Integer> modificationMenuScreen(MenuDTO info) throws IOException {
        final int NAME = 1;
        final int PRICE = 2;
        final int QUIT = 3;

        System.out.println();
        int option;
        String name = info.getName();
        int price = info.getPrice();
        do {
            System.out.println("[수정할 정보 입력]");
            System.out.println("[1] 메뉴명 수정");
            System.out.println("[2] 가격 수정");
            System.out.println("[3] 종료");
            option = Integer.parseInt(keyInput.readLine());

            switch (option) {
                case NAME:
                    name = keyInput.readLine();
                    break;

                case PRICE:
                    price = Integer.parseInt(keyInput.readLine());
                    break;

                case QUIT:
                    break;

                default:
                    System.out.println(ErrorMessage.OUT_OF_BOUND);
                    break;
            }
        } while(option != 3);

        return new Pair<>(name, price);
    }

    public int reviewSelect() throws IOException {
        System.out.println("[1] 페이지 입력");
        System.out.println("[2] 답글 등록");

        return Integer.parseInt(keyInput.readLine());
    }

    public int getReviewIdx() throws IOException {
        System.out.print("답글을 달 리뷰 선택 : ");
        return Integer.parseInt(keyInput.readLine());
    }

    public String getComment() throws IOException {
        System.out.print("답글 내용 입력 : ");
        return keyInput.readLine();
    }
}