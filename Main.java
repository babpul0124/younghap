package org.example;

import persistence.dao.DBConnection;
import service.LoginService;

public class Main
{
    public static void main(String[] args)
    {
        LoginService loginService = new LoginService();

        try
        {
            DBConnection.getConnection();
            System.out.println("DB 연결 완료");

           loginService.login();

            DBConnection.closeConnection();
            System.out.println("DB 연결 종료");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
