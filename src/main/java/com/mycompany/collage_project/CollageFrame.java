package com.mycompany.collage_project;

import java.awt.*;
import javax.swing.*;

public class CollageFrame extends JFrame {

    private JButton insertButton;
    private JButton updateButton;
    private JLabel welcomeLabel;
    private JLabel logoLabel;

    public CollageFrame() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Collage App");
        setSize(900, 700);  // تكبير الحجم الكلي للنوافذ
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // لوحة العنوان (Top Panel)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(10, 25, 74)); // أزرق غامق رايق
        topPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));  // تكبير الحواف

        // تحميل صورة اللوغو
        ImageIcon originalIcon = new ImageIcon(getClass().getResource("/com/mycompany/collage_project/modern_logo.jpg"));
        Image img = originalIcon.getImage();
        Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);  // تكبير اللوغو
        ImageIcon resizedIcon = new ImageIcon(resizedImg);
        logoLabel = new JLabel(resizedIcon);

        // نص الترحيب
        welcomeLabel = new JLabel("Welcome to our App");
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));  // تكبير حجم الخط
        welcomeLabel.setForeground(new Color(230, 230, 250)); // لون خط ناعم (Lavender color)
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        topPanel.add(logoLabel, BorderLayout.WEST);
        topPanel.add(welcomeLabel, BorderLayout.CENTER);

        // لوحة الأزرار (Button Panel)
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 248, 255)); // لون خلفية ناعم جداً (AliceBlue)
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 60, 50));  // تكبير الفواصل بين الأزرار

        insertButton = new JButton("Insert");
        styleButton(insertButton, new Color(0, 102, 204)); // أزرق فاتح للأزرار

        updateButton = new JButton("Update & Delete & Search");
        styleButton(updateButton, new Color(0, 102, 204));

        insertButton.addActionListener(e -> openInsertFrame());
        updateButton.addActionListener(e -> openUpdateFrame());

        buttonPanel.add(insertButton);
        buttonPanel.add(updateButton);

        // إضافة كل البانلز
        add(topPanel, BorderLayout.NORTH);

        // إضافة وصف بين البار والأزرار
        JLabel descriptionLabel = new JLabel("<html><div style='text-align:center; font-size:18px; font-family:Segoe UI; color:#2F4F4F;'>"
                + "This application helps you manage full database including insert, update, delete, and search functionality. "
                + "You can perform operations on various data records seamlessly."
                + "</div></html>");
        descriptionLabel.setBackground(new Color(240, 248, 255));
        descriptionLabel.setOpaque(true);
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        add(descriptionLabel, BorderLayout.CENTER);  // وضع الوصف في المنتصف بين البار والأزرار

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void styleButton(JButton button, Color backgroundColor) {
        button.setPreferredSize(new Dimension(300, 70));  // تكبير الأزرار
        button.setFont(new Font("Segoe UI", Font.PLAIN, 24));  // تكبير حجم الخط
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE); // لون الخط أبيض
        button.setFocusPainted(false); // إزالة إطار التركيز
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));  // تكبير الحدود
        button.setCursor(new Cursor(Cursor.HAND_CURSOR)); // اليد بدل السهم
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
