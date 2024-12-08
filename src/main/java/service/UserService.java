package service;

import persistence.dao.*;
import persistence.dto.*;

public class UserService {
    private CheckInDAO checkInDao;
    private CheckOutDAO checkOutDao;
    private DormitoryDAO dormitoryDao;
    private LoginDAO loginDao;

    public UserService(CheckInDAO checkInDao, CheckOutDAO checkOutDao, DormitoryDAO dormitoryDao, LoginDAO loginDao) {
        this.checkInDao = checkInDao;
        this.checkOutDao = checkOutDao;
        this.dormitoryDao = dormitoryDao;
        this.loginDao = loginDao;
    }

    public UserDTO login(LoginDTO logInDto) {
        return loginDao.selectLogInUser(logInDto);
    }
}
