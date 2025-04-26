package com.mycompany.collage_project;

import com.mycompany.collage_project.CollageFrame;

public class Collage_Project {
    public static void main(String[] args) {
        // محاولة فتح الاتصال بقاعدة البيانات
        if (databaseConnection.getConnection() != null) {
            // إذا تم الاتصال بنجاح، فتح الواجهة
            new CollageFrame().setVisible(true);
        } else {
            // إذا فشل الاتصال، إظهار رسالة خطأ
            System.out.println("Failed to connect to the database. Please check the connection settings.");
        }
    }
}
