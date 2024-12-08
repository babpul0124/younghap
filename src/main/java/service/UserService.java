package service;

import persistence.dao.*;
import persistence.dto.*;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private LoginDAO loginDAO;

    public UserService(LoginDAO loginDAO){
        this.loginDAO = loginDAO;
    }
    // 사용자 로그인
    public UserDTO login(String login_id, String password) {
        // LoginDAO에서 아이디와 비밀번호 목록을 가져옴
        ArrayList<UserDTO> userIdPwList = loginDAO.getIdPwList();

        // 목록을 순회하며 입력받은 아이디와 비밀번호가 일치하는지 확인
        for (UserDTO userDTO : userIdPwList) {
            if (userDTO.getLogin_id().equals(login_id) && userDTO.getPassword().equals(password)) {
                // 로그인 성공
                return userDTO;
            }
        }
        // 로그인 실패 (일치하는 아이디와 비밀번호가 없음)
        return null;
    }
}
