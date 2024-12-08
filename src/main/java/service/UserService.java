package service;

import persistence.dao.*;
import persistence.dto.*;

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

    // 아이디와 비밀번호를 저장하는 메서드
    public boolean registerUser(String loginId, String password) {
        try {
            // 아이디 중복 검사 (중복된 아이디가 있는지 확인)
            if (isIdExists(loginId)) {
                return false;  // 아이디가 이미 존재하면 등록 실패
            }
            loginDAO.saveIdPw(loginId, password);
            return true;  // 등록 성공
        } catch (Exception e) {
            e.printStackTrace();
            return false;  // 예외가 발생하면 등록 실패
        }
    }

    // 사용자 아이디가 이미 존재하는지 확인하는 메서드
    private boolean isIdExists(String loginId) {
        ArrayList<LoginDTO> userIdPwList = loginDAO.getIdPwList();
        for (LoginDTO user : userIdPwList) {
            if (user.getLoginId().equals(loginId)) {
                return true;  // 아이디가 존재하면 true 반환
            }
        }
        return false;  // 아이디가 존재하지 않으면 false 반환
    }

    // 아이디와 비밀번호로 사용자를 인증하는 메서드
    public boolean validateUser(String loginId, String password) {
        ArrayList<LoginDTO> userIdPwList = loginDAO.getIdPwList();
        for (LoginDTO user : userIdPwList) {
            if (user.getLoginId().equals(loginId) && user.getPassword().equals(password)) {
                return true;  // 아이디와 비밀번호가 일치하면 인증 성공
            }
        }
        return false;  // 아이디와 비밀번호가 일치하지 않으면 인증 실패
    }
}
