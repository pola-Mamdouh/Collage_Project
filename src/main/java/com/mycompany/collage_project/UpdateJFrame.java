package com.mycompany.collage_project;

import com.mycompany.collage_project.CollageFrame;
import com.mycompany.collage_project.databaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;
import java.util.List; // Added import for List
import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class UpdateJFrame extends JFrame {
    private Connection connection;
    private JTable resultTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> tableComboBox, searchColumnComboBox;
    private JTextField searchTextField;
    private JPanel editPanel;
    private Map<String, JTextField> editFieldsMap = new HashMap<>();
    private JButton saveButton, deleteButton;

    public UpdateJFrame() {
        try {
            connection = databaseConnection.getConnection();
        } catch (Exception e) {
            showError("DB Connection Failed: " + e.getMessage()); // Added error details
            // Consider exiting or disabling functionality if connection fails
        }

        setTitle("Update / Delete / Search");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(5, 5)); // Added gaps between components

        // Top Panel
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT)); // Align left
        tableComboBox = new JComboBox<>(new String[]{"student", "instractor", "course", "department"});
        searchColumnComboBox = new JComboBox<>();
        searchTextField = new JTextField(15); // Slightly wider search field
        JButton searchButton = new JButton("Search");

        topPanel.add(new JLabel("Table:"));
        topPanel.add(tableComboBox);
        topPanel.add(new JLabel("Search Column:")); // Changed label for clarity
        topPanel.add(searchColumnComboBox);
        topPanel.add(new JLabel("Search Text:"));   // Added label for clarity
        topPanel.add(searchTextField);
        topPanel.add(searchButton);
        add(topPanel, BorderLayout.NORTH);

        // Table Center
        tableModel = new DefaultTableModel();
        resultTable = new JTable(tableModel);
        resultTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection
        add(new JScrollPane(resultTable), BorderLayout.CENTER);

        // Right Panel (Editing)
        editPanel = new JPanel();
        editPanel.setLayout(new BoxLayout(editPanel, BoxLayout.Y_AXIS));
        // Wrap editPanel in a JScrollPane for potentially long forms
        JScrollPane editScrollPane = new JScrollPane(editPanel);
        editScrollPane.setPreferredSize(new Dimension(200, 0)); // Give it a preferred width
        add(editScrollPane, BorderLayout.EAST);


        // Bottom Panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Center buttons
        saveButton = new JButton("Save Changes"); // More descriptive button text
        deleteButton = new JButton("Delete Selected"); // More descriptive button text
        JButton backButton = new JButton("Back");
        bottomPanel.add(saveButton);
        bottomPanel.add(deleteButton);
        bottomPanel.add(backButton);
        add(bottomPanel, BorderLayout.SOUTH);

        setListeners(searchButton, backButton);

        // Initialize state
        updateSearchColumns(); // Populate columns for the default table
        disableEditArea();
    }

    private void setListeners(JButton searchButton, JButton backButton) {
        tableComboBox.addActionListener(e -> {
            updateSearchColumns();
            clearTableAndEditArea(); // Combined clearing
        });

        searchButton.addActionListener(e -> searchDatabase());

        // Handle Enter key press in search text field
        searchTextField.addActionListener(e -> searchDatabase());

        resultTable.getSelectionModel().addListSelectionListener(e -> {
            // Check if selection is finished and a row is actually selected
            if (!e.getValueIsAdjusting() && resultTable.getSelectedRow() != -1) {
                fillEditFields();
            } else if (resultTable.getSelectedRow() == -1) {
                // If selection is cleared, disable edit area
                disableEditArea();
            }
        });

        saveButton.addActionListener(e -> updateRecord());
        deleteButton.addActionListener(e -> deleteRecord());

        backButton.addActionListener(e -> {
            // Properly close resources if needed, e.g., DB connection if frame manages it
            // if (connection != null) { try { connection.close(); } catch (SQLException ex) {} }
            dispose(); // Close this window
            // Assuming CollageFrame is the main menu or previous screen
            SwingUtilities.invokeLater(() -> new CollageFrame().setVisible(true)); // Ensure GUI updates on EDT
        });
    }

    private void updateSearchColumns() {
        String selectedTable = (String) tableComboBox.getSelectedItem();
        if (selectedTable == null) return; // Avoid error if called before initialization

        searchColumnComboBox.removeAllItems(); // Clear existing items
        String[] columns = {}; // Use an array for columns

        // Use switch statement for better readability
        switch (selectedTable) {
            case "student":
                // Assuming common columns for student/instructor
                columns = new String[]{"student_id", "first_name", "last_name", "Phone", "email", "D_ID"}; // Example extra columns
                break;
            case "instractor": // Corrected spelling if needed in DB
                columns = new String[]{"instractor_id", "first_name", "last_name", "Phone", "email", "salary"}; // Example extra columns
                break;
            case "course":
                columns = new String[]{"course_id", "course_name", "duration", "D_ID", "instractor_id"};
                break;
            case "department":
                // Note: head_instructor_id will be hidden later, but might be useful for searching
                columns = new String[]{"D_ID", "department_name", "location", "Head_ID"}; // Assuming D_ID is the primary key
                break;
            default:
                // Handle unexpected table selection if necessary
                break;
        }

        // Add columns to the JComboBox
        for (String col : columns) {
             // We allow searching by any column, even if it's hidden later
             // Or optionally: if (!col.equalsIgnoreCase("head_instructor_id")) {
                 searchColumnComboBox.addItem(col);
             // }
        }
    }

    // No need for addColumns helper method anymore

    private void searchDatabase() {
        clearTableAndEditArea(); // Clear previous results and edit fields

        String selectedTable = (String) tableComboBox.getSelectedItem();
        String selectedColumn = (String) searchColumnComboBox.getSelectedItem();
        String searchText = searchTextField.getText().trim(); // Trim whitespace

        if (selectedTable == null || selectedColumn == null) {
            showError("Please select a table and search column.");
            return;
        }

        // Basic validation for search text if needed
        // if (searchText.isEmpty()) {
        //     showError("Please enter search text.");
        //     return;
        // }

        // Use try-with-resources for PreparedStatement and ResultSet
        String sql = "SELECT * FROM " + selectedTable + " WHERE " + selectedColumn + " LIKE ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, "%" + searchText + "%");

            try (ResultSet rs = ps.executeQuery()) {
                ResultSetMetaData meta = rs.getMetaData();
                int colCount = meta.getColumnCount();

                Vector<String> headers = new Vector<>();
                List<Integer> columnIndicesToShow = new ArrayList<>(); // Keep track of indices to show

                for (int i = 1; i <= colCount; i++) {
                    String colName = meta.getColumnName(i);
                    // *** This is where head_instructor_id is excluded from the table headers ***
                    if (!colName.equalsIgnoreCase("head_instructor_id")) {
                        headers.add(colName);
                        columnIndicesToShow.add(i); // Add the original index (1-based)
                    }
                }
                tableModel.setColumnIdentifiers(headers); // Set headers in the table model

                // Populate table rows
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    // Iterate only through the indices we decided to show
                    for (int index : columnIndicesToShow) {
                         // *** This ensures data from head_instructor_id is not added to the row vector ***
                        row.add(rs.getObject(index));
                    }
                    tableModel.addRow(row);
                }

                if (tableModel.getRowCount() == 0) {
                    showMessage("No records found matching your criteria.");
                }

            } // ResultSet is automatically closed here
        } catch (SQLException ex) {
            showError("Database Search Failed: " + ex.getMessage());
            ex.printStackTrace(); // Log the full stack trace for debugging
        } catch (Exception ex) {
             showError("An unexpected error occurred during search: " + ex.getMessage());
             ex.printStackTrace();
        }
    }


    private void fillEditFields() {
        editPanel.removeAll(); // Clear previous fields
        editFieldsMap.clear(); // Clear the map

        int selectedRow = resultTable.getSelectedRow();
        // Double-check if a row is still selected (might be cleared by search)
        if (selectedRow == -1) {
            disableEditArea();
            return;
        }

        int colCount = tableModel.getColumnCount(); // Number of columns *in the table model*

        // Get the assumed primary key value (first column in the *table model*)
        // It's safer to get the ID column name dynamically if possible,
        // but assuming it's always the first visible column here.
        String idColumnName = tableModel.getColumnName(0);
        Object idValue = tableModel.getValueAt(selectedRow, 0);

        // Add a non-editable field for the ID
        JTextField idField = new JTextField(idValue.toString());
        idField.setEditable(false); // Make ID field read-only
        idField.setBackground(Color.LIGHT_GRAY); // Visual cue for read-only
        editPanel.add(new JLabel(idColumnName + " (ID):"));
        editPanel.add(idField);

        // Add editable fields for other columns (starting from index 1)
        // *** Since head_instructor_id is not in tableModel, it won't appear here ***
        for (int i = 1; i < colCount; i++) {
            String colName = tableModel.getColumnName(i);
            Object value = tableModel.getValueAt(selectedRow, i);
            JTextField field = new JTextField(value != null ? value.toString() : ""); // Handle null values

            editPanel.add(new JLabel(colName + ":")); // Add colon for clarity
            editPanel.add(field);
            editFieldsMap.put(colName, field); // Map column name to its text field
        }

        saveButton.setEnabled(true);
        deleteButton.setEnabled(true);
        editPanel.revalidate();
        editPanel.repaint();
    }

    private void updateRecord() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a record to update.");
            return;
        }

        try {
            String table = (String) tableComboBox.getSelectedItem();
            // Get ID column name and value from the *table model*
            String idColumn = tableModel.getColumnName(0);
            Object idValue = tableModel.getValueAt(selectedRow, 0);

            // Basic validation (e.g., check if required fields are filled)
            for (Map.Entry<String, JTextField> entry : editFieldsMap.entrySet()) {
                 if (entry.getValue().getText().trim().isEmpty()) {
                     // Optional: Add logic here to check if the column allows NULLs
                     // For now, just show a warning or prevent update
                     // showError("Field '" + entry.getKey() + "' cannot be empty.");
                     // return;
                 }
            }


            // Build the SQL query dynamically
            StringBuilder sql = new StringBuilder("UPDATE ").append(table).append(" SET ");
            List<Object> values = new ArrayList<>(); // To hold values for PreparedStatement

            // Append "column = ?" for each editable field
            for (String colName : editFieldsMap.keySet()) {
                sql.append(colName).append(" = ?, ");
                values.add(editFieldsMap.get(colName).getText()); // Get text from JTextField
            }

            // Remove the trailing ", "
            if (!editFieldsMap.isEmpty()) {
                sql.delete(sql.length() - 2, sql.length());
            } else {
                showMessage("No fields available to update."); // Should not happen if save is enabled
                return;
            }


            // Add the WHERE clause
            sql.append(" WHERE ").append(idColumn).append(" = ?");
            values.add(idValue); // Add the ID value last

            // Execute the update using PreparedStatement
            try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
                // Set parameters for the PreparedStatement
                for (int i = 0; i < values.size(); i++) {
                    // Basic type handling (can be improved)
                    Object value = values.get(i);
                    // You might need more sophisticated type conversion based on DB schema
                     ps.setObject(i + 1, value);
                }

                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    showMessage("Update Successful!");
                    // Refresh the table view to show the updated data
                    int modelRow = resultTable.convertRowIndexToModel(selectedRow); // Get model index before refreshing
                    searchDatabase(); // Re-run the search to refresh
                    // Try to re-select the updated row (might change position if sorted/filtered)
                    // This is tricky, might be better to just clear selection or select based on ID after refresh
                    // For simplicity, we are just refreshing the search.

                    // Optionally clear selection and disable edit area after successful update
                    // resultTable.clearSelection();
                    // disableEditArea();

                } else {
                    showError("Update Failed: Record not found or no changes made.");
                }
            } // PreparedStatement closed automatically

        } catch (SQLException ex) {
            showError("Database Update Failed: " + ex.getMessage());
            ex.printStackTrace(); // Log for debugging
        } catch (Exception ex) {
             showError("An unexpected error occurred during update: " + ex.getMessage());
             ex.printStackTrace();
        }
    }

    private void deleteRecord() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Please select a record to delete.");
            return;
        }

        // Confirmation dialog
        int confirmation = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete the selected record?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirmation != JOptionPane.YES_OPTION) {
            return; // User cancelled
        }

        try {
            String table = (String) tableComboBox.getSelectedItem();
            // Get ID column name and value from the *table model*
            String idColumn = tableModel.getColumnName(0);
            Object idValue = tableModel.getValueAt(selectedRow, 0);

            String sql = "DELETE FROM " + table + " WHERE " + idColumn + " = ?";

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setObject(1, idValue); // Set the ID parameter
                int rowsAffected = ps.executeUpdate();

                if (rowsAffected > 0) {
                    showMessage("Delete Successful!");
                    // Refresh the table view after deletion
                    searchDatabase(); // Re-run search, the deleted row will disappear
                    // Edit area will be disabled by searchDatabase -> clearTableAndEditArea
                } else {
                    showError("Delete Failed: Record not found.");
                }
            } // PreparedStatement closed automatically

        } catch (SQLException ex) {
            // Handle potential foreign key constraint violations
            if (ex.getMessage().toLowerCase().contains("foreign key constraint")) {
                 showError("Delete Failed: This record is referenced by other data (foreign key constraint).");
            } else {
                 showError("Database Delete Failed: " + ex.getMessage());
            }
            ex.printStackTrace(); // Log for debugging
        } catch (Exception ex) {
             showError("An unexpected error occurred during deletion: " + ex.getMessage());
             ex.printStackTrace();
        }
    }

    // Disables the editing panel and buttons
    private void disableEditArea() {
        editPanel.removeAll(); // Remove all components (labels, fields)
        editFieldsMap.clear();  // Clear the field map
        saveButton.setEnabled(false); // Disable save button
        deleteButton.setEnabled(false); // Disable delete button
        editPanel.revalidate(); // Update layout
        editPanel.repaint();    // Repaint the panel
    }

    // Clears table data and also disables the edit area
    private void clearTableAndEditArea() {
        tableModel.setRowCount(0); // Clear rows
        tableModel.setColumnCount(0); // Clear columns/headers
        disableEditArea(); // Disable editing components
    }

    // Utility method to show informational messages
    private void showMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    // Utility method to show error messages
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // Main method for standalone testing
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // Optional: Set Look and Feel for better appearance
            try {
                 UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                 System.err.println("Failed to set system Look and Feel.");
            }
            new UpdateJFrame().setVisible(true);
        });
    }
}