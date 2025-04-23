
package com.mycompany.collage_project;
import com.mycompany.collage_project.CollageJFram;

public class Collage_Project {
    public static void main(String[] args) {
        databaseConnection.Open(); // فتح الاتصال
        new CollageJFram().setVisible(true); // اظهار الواجهة
    }
}
