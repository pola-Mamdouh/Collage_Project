package com.mycompany.collage_project;

import java.awt.Image;
import javax.swing.*;

public class CollageFrame extends JFrame {

    private JButton jButton1;
    private JButton jButton2;
    private JLabel jLabel1;
    private JLabel backgroundLabel;

    public CollageFrame() {
        initComponents();
    }

    private void initComponents() {
        // إعدادات الفريم الأساسية
        setTitle("Collage App");
        setSize(400, 300);
        setLayout(null); // بدون Layout Manager علشان تتحكم بحرية
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // الخلفية
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/mycompany/collage_project/modern_logo.jpg"));

// Resize الصورة حسب حجم الفريم
        Image img = originalIcon.getImage();
        Image resizedImg = img.getScaledInstance(400, 300, Image.SCALE_SMOOTH); // نفس مقاس الفريم
        ImageIcon resizedIcon = new ImageIcon(resizedImg);

        backgroundLabel = new JLabel(resizedIcon);
        backgroundLabel.setBounds(0, 0, 30, 40);
        add(backgroundLabel);

        // الليبل الرئيسي
        jLabel1 = new JLabel("Welcome to our app");
        jLabel1.setBounds(120, 20, 200, 30);
        add(jLabel1);

        // زر Insert
        jButton1 = new JButton("Insert");
        jButton1.setBounds(80, 100, 100, 30);
        jButton1.addActionListener(e -> openInsertFrame());
        add(jButton1);

        // زر Update & Delete & Search
        jButton2 = new JButton("Update & Delete & Search");
        jButton2.setBounds(190, 100, 180, 30);
        jButton2.addActionListener(e -> openUpdateFrame());
        add(jButton2);

        // خلي الخلفية آخر حاجة تتحط علشان تبقى ورا الباقي
        getContentPane().add(backgroundLabel);
        backgroundLabel.setLocation(0, 0);
    }

    private void openInsertFrame() {
        InsertFrame insertFrame = new InsertFrame();
        insertFrame.setVisible(true);
        this.setVisible(false);
    }

    private void openUpdateFrame() {
        UpdateJFrame updateFrame = new UpdateJFrame();
        updateFrame.setVisible(true);
        this.setVisible(false);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CollageFrame().setVisible(true);
        });
    }
}
