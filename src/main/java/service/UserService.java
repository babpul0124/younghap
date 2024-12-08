package service;

import persistence.dao.LoginDAO;
import persistence.dto.LoginDTO;
import persistence.dto.UserDTO;

import java.util.ArrayList;

public class UserService {

    private final LoginDAO loginDAO;

    public UserService(LoginDAO loginDAO) {
        this.loginDAO = loginDAO;
    }

    // 아이디와 비밀번호 목록을 가져오는 메서드
    public ArrayList<LoginDTO> getUserIdPwList() {
        return loginDAO.getIdPwList();
    }

    // 아이디와 비밀번호로 사용자를 인증하는 메서드
    public String validateUser(String loginId, String password) {
        ArrayList<LoginDTO> userIdPwList = loginDAO.getIdPwList();
        for (LoginDTO user : userIdPwList) {
            if (user.getLoginId().equals(loginId) && user.getPassword().equals(password)) {
                // 인증에 성공한 경우 userRole을 통해 역할 구분
                UserDTO userDTO = getUserByLoginId(loginId);  // 로그인 ID로 UserDTO 가져오기
                if (userDTO != null) {
                    // userRole이 "학생"이면 학생, "관리자"이면 관리자
                    if ("학생".equals(userDTO.getUserRole())) {
                        return "학생";  // 학생일 경우
                    } else if ("관리자".equals(userDTO.getUserRole())) {
                        return "관리자";  // 관리자일 경우
                    }
                }
            }
        }
        return "인증 실패";  // 인증 실패 시
    }

    // 로그인 ID로 UserDTO를 가져오는 메서드
    private UserDTO getUserByLoginId(String loginId) {
        // 로그인 ID로 UserDTO 정보를 가져오기 위한 방법
        ArrayList<LoginDTO> userIdPwList = loginDAO.getIdPwList(); // 로그인 아이디와 비밀번호 리스트
        for (LoginDTO loginDTO : userIdPwList) {
            if (loginDTO.getLoginId().equals(loginId)) {
                UserDTO userDTO = new UserDTO();
                userDTO.setLoginId(loginDTO.getLoginId());
                // 실제 userRole을 데이터베이스에서 가져오도록 수정 필요
                userDTO.setUserRole("학생");  // 여기서는 예시로 "학생"을 설정
                return userDTO;
            }
        }
        return null;  // 로그인 ID가 없으면 null 반환
    }
}
