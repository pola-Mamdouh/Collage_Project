package com.mycompany.collage_project;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;
// import com.formdev.flatlaf.FlatLightLaf; // تم إزالة استيراد FlatLaf
import javax.swing.border.TitledBorder; // Import TitledBorder

public class InsertFrame extends JFrame {

    private JComboBox<String> tableSelector;
    private JTextField idField, firstNameField, lastNameField, phoneField;
    private JTextField courseId, courseName, courseDuration;
    private JTextField departmentName, departmentLocation;
    private JPanel fieldsPanel; // Use one panel to hold all fields

    // Define colors as constants
    private static final Color HEADER_FOREGROUND = new Color(0, 51, 102);
    private static final Color PANEL_BACKGROUND = new Color(255, 255, 255);
    private static final Color BUTTON_BACKGROUND = new Color(0, 102, 204);
    private static final Color BACK_BUTTON_BACKGROUND = new Color(255, 102, 102);

    public InsertFrame() {
        // Set FlatLaf L&F before creating components
        try {
            // UIManager.setLookAndFeel(new FlatLightLaf()); // تم إزالة سطر تعيين FlatLaf
            // يمكنك هنا تعيين شكل ومظهر آخر إذا أردت، أو تركه يستخدم الافتراضي
            // مثال لتعيين الشكل والمظهر الخاص بالنظام:
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) { // تم تغيير نوع الاستثناء ليشمل أي خطأ في تعيين L&F
            ex.printStackTrace();
            // Optionally, fall back to a different L&F or show an error
        }

        setTitle("Insert Form");
        setSize(1000, 600);
        setLayout(new BorderLayout()); // Use BorderLayout for main frame
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Header
        JLabel headerLabel = new JLabel("Insert Data into Database");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Increased font size
        headerLabel.setForeground(HEADER_FOREGROUND);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(headerLabel, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(PANEL_BACKGROUND);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); // Add padding

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); // Consistent insets
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fields fill horizontally

        // Table Selector on the right
        tableSelector = new JComboBox<>(new String[]{"student", "instractor", "course", "department"});
        tableSelector.setPreferredSize(new Dimension(200, 30));
        tableSelector.setBackground(new Color(240, 240, 240));
        tableSelector.setFont(new Font("Arial", Font.PLAIN, 14));

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHEAST; // Anchor to top-right
        formPanel.add(tableSelector, gbc);

        // Fields Panel (centralized)
        fieldsPanel = new JPanel(new GridBagLayout()); // Use GridBagLayout for fieldsPanel
        fieldsPanel.setBackground(PANEL_BACKGROUND);
        fieldsPanel.setBorder(BorderFactory.createTitledBorder("Details")); // Add a border

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0; // Allow fields panel to take extra horizontal space
        gbc.weighty = 1.0; // Allow fields panel to take extra vertical space
        gbc.fill = GridBagConstraints.BOTH; // Fields panel fills both horizontally and vertically
        formPanel.add(fieldsPanel, gbc);


        // Add formPanel to the main layout
        add(formPanel, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); // Add spacing between buttons
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20)); // Add padding
        buttonPanel.setBackground(PANEL_BACKGROUND); // Match background

        JButton insertButton = new JButton("Insert");
        styleButton(insertButton, BUTTON_BACKGROUND);

        JButton backButton = new JButton("Back to Home");
        styleButton(backButton, BACK_BUTTON_BACKGROUND);

        buttonPanel.add(insertButton);
        buttonPanel.add(backButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Initialize fields and update visibility
        createFields();
        updateFieldsVisibility();

        // Action Listeners
        tableSelector.addActionListener(e -> updateFieldsVisibility());
        insertButton.addActionListener(e -> handleInsert());
        backButton.addActionListener(e -> {
            dispose(); // Close InsertFrame
            new CollageFrame().setVisible(true); // Open CollageFrame
        });

        setVisible(true);
    }

    private void createFields() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0; // Text fields take available horizontal space

        // Add fields and their corresponding labels to the fieldsPanel
        // Student/Instructor Fields
        idField = addField(fieldsPanel, gbc, 0, 0, "Id:");
        firstNameField = addField(fieldsPanel, gbc, 0, 1, "First Name:");
        lastNameField = addField(fieldsPanel, gbc, 0, 2, "Last Name:");
        phoneField = addField(fieldsPanel, gbc, 0, 3, "Phone:");

        // Course Fields
        courseId = addField(fieldsPanel, gbc, 0, 0, "Course Id:");
        courseName = addField(fieldsPanel, gbc, 0, 1, "Course Name:");
        courseDuration = addField(fieldsPanel, gbc, 0, 2, "Course Duration:");

        // Department Fields
        departmentName = addField(fieldsPanel, gbc, 0, 0, "Department Name:");
        departmentLocation = addField(fieldsPanel, gbc, 0, 1, "Department Location:");
    }

    private JTextField addField(JPanel panel, GridBagConstraints gbc, int gridx, int gridy, String labelText) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.EAST; // Align label to the east
        gbc.weightx = 0; // Labels don't take extra horizontal space
        panel.add(label, gbc);

        JTextField textField = new JTextField(); // Use default constructor, set size with preferredSize
        textField.setPreferredSize(new Dimension(150, 30)); // Set preferred width and height (مثال: عرض 150، ارتفاع 30)
        gbc.gridx = gridx + 1;
        gbc.gridy = gridy;
        gbc.anchor = GridBagConstraints.WEST; // Align text field to the west
        gbc.weightx = 1.0; // Text field takes extra horizontal space
        panel.add(textField, gbc);
        return textField;
    }


    private void styleButton(JButton button, Color backgroundColor) {
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 25)); // Adjust padding
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    }


    private void updateFieldsVisibility() {
        String selected = (String) tableSelector.getSelectedItem();

        // Hide all fields and their corresponding labels first
        Component[] components = fieldsPanel.getComponents();
        for (Component comp : components) {
            comp.setVisible(false);
        }

        // Show fields and labels based on selected table
        if (selected.equals("student") || selected.equals("instractor")) {
            idField.setVisible(true);
            // Find and show the label for idField
            findAndShowLabel(fieldsPanel, idField);

            firstNameField.setVisible(true);
            findAndShowLabel(fieldsPanel, firstNameField);

            lastNameField.setVisible(true);
            findAndShowLabel(fieldsPanel, lastNameField);

            phoneField.setVisible(true);
            findAndShowLabel(fieldsPanel, phoneField);

            ((TitledBorder) fieldsPanel.getBorder()).setTitle("Student/Instructor Details");

        } else if (selected.equals("course")) {
            courseId.setVisible(true);
            findAndShowLabel(fieldsPanel, courseId);

            courseName.setVisible(true);
            findAndShowLabel(fieldsPanel, courseName);

            courseDuration.setVisible(true);
            findAndShowLabel(fieldsPanel, courseDuration);

            ((TitledBorder) fieldsPanel.getBorder()).setTitle("Course Details");
        } else if (selected.equals("department")) {
            departmentName.setVisible(true);
            findAndShowLabel(fieldsPanel, departmentName);

            departmentLocation.setVisible(true);
            findAndShowLabel(fieldsPanel, departmentLocation);

            ((TitledBorder) fieldsPanel.getBorder()).setTitle("Department Details");
        }

        fieldsPanel.revalidate(); // Revalidate the panel to update layout
        fieldsPanel.repaint(); // Repaint the panel
    }

    // Helper method to find and show the label associated with a text field
    private void findAndShowLabel(JPanel panel, JTextField textField) {
        Component[] components = panel.getComponents();
        for (int i = 0; i < components.length; i++) {
            if (components[i] == textField) {
                // Assuming the label is the component right before the text field in the panel's component list
                if (i > 0 && components[i - 1] instanceof JLabel) {
                    components[i - 1].setVisible(true);
                    break;
                }
            }
        }
    }


    private void handleInsert() {
        String selected = (String) tableSelector.getSelectedItem();
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = databaseConnection.getConnection(); // Assuming databaseConnection is a class you have for managing connections

            if (selected.equals("student")) {
                 // Input validation for student ID
                try {
                    Integer.parseInt(idField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Student ID format. Please enter a number.");
                    return; // Stop further processing
                }
                pstmt = conn.prepareStatement("INSERT INTO student (student_id, first_name, last_name, phone) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, Integer.parseInt(idField.getText()));
                pstmt.setString(2, firstNameField.getText());
                pstmt.setString(3, lastNameField.getText());
                pstmt.setString(4, phoneField.getText());
            } else if (selected.equals("instractor")) {
                 // Input validation for instructor ID
                 try {
                    Integer.parseInt(idField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Instructor ID format. Please enter a number.");
                    return; // Stop further processing
                }
                pstmt = conn.prepareStatement("INSERT INTO instractor (instractor_id, first_name, last_name, phone) VALUES (?, ?, ?, ?)");
                pstmt.setInt(1, Integer.parseInt(idField.getText()));
                pstmt.setString(2, firstNameField.getText());
                pstmt.setString(3, lastNameField.getText());
                pstmt.setString(4, phoneField.getText());
            } else if (selected.equals("course")) {
                 // Input validation for course ID
                 try {
                    Integer.parseInt(courseId.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Invalid Course ID format. Please enter a number.");
                    return; // Stop further processing
                }
                pstmt = conn.prepareStatement("INSERT INTO course (course_id, course_name, duration) VALUES (?, ?, ?)");
                pstmt.setInt(1, Integer.parseInt(courseId.getText()));
                pstmt.setString(2, courseName.getText());
                pstmt.setString(3, courseDuration.getText());
            } else if (selected.equals("department")) {
                 // No specific validation needed for department name/location format beyond basic string
                pstmt = conn.prepareStatement("INSERT INTO department (department_name, location) VALUES (?, ?)");
                pstmt.setString(1, departmentName.getText());
                pstmt.setString(2, departmentLocation.getText());
            } else {
                return; // Should not happen with the current ComboBox options
            }

            int result = pstmt.executeUpdate();

            if (result > 0) {
                JOptionPane.showMessageDialog(this, "Insert successful into " + selected + "!");
                clearFields();
            } else {
                 JOptionPane.showMessageDialog(this, "Insert failed. No rows affected.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
             ex.printStackTrace(); // Print stack trace for debugging
        } catch (Exception ex) {
             JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage());
             ex.printStackTrace(); // Print stack trace for debugging
        } finally {
            // Close resources
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
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
        SwingUtilities.invokeLater(() -> {
            new InsertFrame();
        });
    }
}
