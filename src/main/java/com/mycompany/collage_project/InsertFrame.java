package com.mycompany.collage_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class InsertFrame extends JFrame {

    private JComboBox<String> tableSelector;
    private JTextField idField, firstNameField, lastNameField, phoneField;
    private JTextField courseId, courseName, courseDuration;
    private JTextField departmentName, departmentLocation;
    private JPanel studentInstructorPanel, coursePanel, departmentPanel;

    public InsertFrame() {
        setTitle("Insert Form");
        setSize(600, 400);
        setLayout(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Table Selector
        tableSelector = new JComboBox<>(new String[]{"student", "instractor", "course", "department"});
        tableSelector.setBounds(300, 20, 150, 25);
        add(tableSelector);

        // Student/Instructor Panel
        studentInstructorPanel = new JPanel(null);
        studentInstructorPanel.setBounds(30, 60, 270, 150); // مكانه ثابت على اليسار

        JLabel idLabel = new JLabel("Id:");
        idLabel.setBounds(0, 0, 80, 25);
        studentInstructorPanel.add(idLabel);
        idField = new JTextField();
        idField.setBounds(90, 0, 150, 25);
        studentInstructorPanel.add(idField);

        JLabel firstNameLabel = new JLabel("first_name:");
        firstNameLabel.setBounds(0, 40, 80, 25);
        studentInstructorPanel.add(firstNameLabel);
        firstNameField = new JTextField();
        firstNameField.setBounds(90, 40, 150, 25);
        studentInstructorPanel.add(firstNameField);

        JLabel lastNameLabel = new JLabel("last_name:");
        lastNameLabel.setBounds(0, 80, 80, 25);
        studentInstructorPanel.add(lastNameLabel);
        lastNameField = new JTextField();
        lastNameField.setBounds(90, 80, 150, 25);
        studentInstructorPanel.add(lastNameField);

        JLabel phoneLabel = new JLabel("phone:");
        phoneLabel.setBounds(0, 120, 80, 25);
        studentInstructorPanel.add(phoneLabel);
        phoneField = new JTextField();
        phoneField.setBounds(90, 120, 150, 25);
        studentInstructorPanel.add(phoneField);

        add(studentInstructorPanel);

        // Course Panel
        coursePanel = new JPanel(null);
        coursePanel.setBounds(30, 60, 270, 150); // مكانه نفس مكان studentInstructorPanel

        JLabel courseIdLabel = new JLabel("id:");
        courseIdLabel.setBounds(0, 0, 80, 25);
        coursePanel.add(courseIdLabel);
        courseId = new JTextField();
        courseId.setBounds(90, 0, 150, 25);
        coursePanel.add(courseId);

        JLabel courseNameLabel = new JLabel("course_name:");
        courseNameLabel.setBounds(0, 40, 100, 25);
        coursePanel.add(courseNameLabel);
        courseName = new JTextField();
        courseName.setBounds(90, 40, 150, 25);
        coursePanel.add(courseName);

        JLabel courseDurationLabel = new JLabel("duration:");
        courseDurationLabel.setBounds(0, 80, 80, 25);
        coursePanel.add(courseDurationLabel);
        courseDuration = new JTextField();
        courseDuration.setBounds(90, 80, 150, 25);
        coursePanel.add(courseDuration);

        add(coursePanel);

        // Department Panel
        departmentPanel = new JPanel(null);
        departmentPanel.setBounds(30, 60, 270, 150); // نفس المكان برضو

        JLabel departmentNameLabel = new JLabel("department name:");
        departmentNameLabel.setBounds(0, 0, 120, 25);
        departmentPanel.add(departmentNameLabel);
        departmentName = new JTextField();
        departmentName.setBounds(130, 0, 130, 25);
        departmentPanel.add(departmentName);

        JLabel departmentLocationLabel = new JLabel("location:");
        departmentLocationLabel.setBounds(0, 40, 100, 25);
        departmentPanel.add(departmentLocationLabel);
        departmentLocation = new JTextField();
        departmentLocation.setBounds(130, 40, 130, 25);
        departmentPanel.add(departmentLocation);

        add(departmentPanel);

        // Buttons
        JButton insertButton = new JButton("Insert");
        insertButton.setBounds(250, 300, 100, 30);
        add(insertButton);

        JButton backButton = new JButton("Back to Home");
        backButton.setBounds(450, 300, 120, 30);
        add(backButton);

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // يغلق InsertFrame
                new CollageJFram().setVisible(true); // يفتح CollageJFrame
            }
        });

        // Action Listeners
        tableSelector.addActionListener(e -> updateFieldsVisibility());
        insertButton.addActionListener(e -> handleInsert());

        updateFieldsVisibility(); // Initial visibility
        setVisible(true);
    }

    private void updateFieldsVisibility() {
        String selected = (String) tableSelector.getSelectedItem();
        boolean isStudentOrInstructor = selected.equals("student") || selected.equals("instractor");
        boolean isCourse = selected.equals("course");
        boolean isDepartment = selected.equals("department");

        studentInstructorPanel.setVisible(isStudentOrInstructor);
        coursePanel.setVisible(isCourse);
        departmentPanel.setVisible(isDepartment);
    }

    private void handleInsert() {
        String selected = (String) tableSelector.getSelectedItem();

        try (Connection conn = databaseConnection.getConnection()) {
            PreparedStatement pstmt;
            if (selected.equals("student")) {
                pstmt = conn.prepareStatement("INSERT INTO student (student_id, first_name, last_name, phone) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, Integer.parseInt(idField.getText()));
                pstmt.setString(2, firstNameField.getText());
                pstmt.setString(3, lastNameField.getText());
                pstmt.setString(4, phoneField.getText());
            } else if (selected.equals("instractor")) {
                pstmt = conn.prepareStatement("INSERT INTO instractor (instractor_id, first_name, last_name, phone) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, Integer.parseInt(idField.getText()));
                pstmt.setString(2, firstNameField.getText());
                pstmt.setString(3, lastNameField.getText());
                pstmt.setString(4, phoneField.getText());
            } else if (selected.equals("course")) {
                pstmt = conn.prepareStatement("INSERT INTO course (course_id, course_name, duration) VALUES (?, ?, ?)");
                pstmt.setInt(1, Integer.parseInt(courseId.getText()));
                pstmt.setString(2, courseName.getText());
                pstmt.setString(3, courseDuration.getText());
            } else if (selected.equals("department")) {
                pstmt = conn.prepareStatement("INSERT INTO department (department_name, location) VALUES (?, ?)");
                pstmt.setString(1, departmentName.getText());
                pstmt.setString(2, departmentLocation.getText());
            } else {
                return;
            }

            int result = pstmt.executeUpdate();
            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Insert successful into " + selected + "!");
                clearFields();
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Insert failed: " + ex.getMessage());
        }
    }

    private void clearFields() {
        idField.setText("");
        firstNameField.setText("");
        lastNameField.setText("");
        phoneField.setText("");
        courseId.setText("");
        courseName.setText("");
        courseDuration.setText("");
        departmentName.setText("");
        departmentLocation.setText("");
    }

    public static void main(String[] args) {
        new InsertFrame();
    }
}
