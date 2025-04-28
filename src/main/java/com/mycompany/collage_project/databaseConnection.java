package com.mycompany.collage_project;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class databaseConnection {

    static Connection con;

    public static Connection getConnection() {
        try {
            // تحميل درايفر SQL Server

            // رابط الاتصال بـ SQL Server
            String url = "jdbc:sqlserver://localhost:1433;databaseName=collage;encrypt=false";
            
            // الاتصال بالسيرفر (اسم المستخدم والباسورد)
            con = DriverManager.getConnection(url, "sa", "DBJAVA");
            System.out.println("Connected to SQL Server");
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        return con;
    }
}
