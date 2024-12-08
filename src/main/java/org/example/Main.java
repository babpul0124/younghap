package org.example;

import java.sql.*;
import java.time.LocalDateTime;

public class Main {
    public static void main(String args[]){

        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{
            //Class.forName("com.mysql.jdbc.Driver"); //java 7이후 생략 가능
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost/domitory?characterEncoding=utf8&serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true";
            //String url = "jdbc:mysql://localhost/dormitory";
            conn = DriverManager.getConnection(url, "root", "jinchen0807!");



            String query = "SELECT * FROM domitory.distance";

            stmt = conn.createStatement();
            rs = stmt.executeQuery(query);

            String preQuery = "INSERT INTO domitory.user (region_name, distance_score) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(preQuery);
            pstmt.setString(1, ("응가의 나라"));
            pstmt.setInt(2, 101);
            pstmt.executeUpdate();

            while(rs.next()) {
                String region_name = rs.getString("region_name");
                String distance_score = rs.getString("distance_score");
                System.out.printf("%s | %s\n", region_name,distance_score);
                System.out.println("-------------------------------------");
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch(SQLException e){
            System.out.println("error : " + e);
        }  finally{
            try{
                if(conn != null && !rs.isClosed()){
                    rs.close();
                }
                if(conn != null && !stmt.isClosed()){
                    stmt.close();
                }
                if(conn != null && !conn.isClosed()){
                    conn.close();
                }
            }
            catch(SQLException e){
                e.printStackTrace();
            }
        }
    }
}