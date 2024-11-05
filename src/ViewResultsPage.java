import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.SimpleDateFormat;

public class ViewResultsPage extends JFrame {
    private JTable resultsTable;
    private DefaultTableModel tableModel;

    public ViewResultsPage() {
        // Frame properties
        setTitle("Admin View Results");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen mode
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the table model and set column names
        String[] columnNames = {"Student Name", "Test Title", "Score", "Total Questions", "Correct Answers", "Test Date and Time"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultsTable = new JTable(tableModel);
        resultsTable.setFillsViewportHeight(true);
        resultsTable.setRowHeight(30);
        resultsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        resultsTable.setAutoCreateRowSorter(true); // Enable sorting

        // Add a mouse listener for row clicks
        resultsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int row = resultsTable.getSelectedRow();
                if (row != -1) {
                    String studentName = (String) resultsTable.getValueAt(row, 0);
                    String testTitle = (String) resultsTable.getValueAt(row, 1);

                }
            }
        });

        // Add the table to a scroll pane
        JScrollPane scrollPane = new JScrollPane(resultsTable);
        add(scrollPane, BorderLayout.CENTER);

        // Create a header panel with a title and buttons
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180)); // Steel blue
        headerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20)); // Adjust padding and alignment
        JLabel headerLabel = new JLabel("Test Results");
        headerLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Create back and refresh buttons
        JButton backButton = createButton("Back", Color.RED);
        backButton.addActionListener(new BackButtonListener());
        headerPanel.add(backButton);

        JButton refreshButton = createButton("Refresh", Color.GREEN);
        refreshButton.addActionListener(new RefreshButtonListener());
        headerPanel.add(refreshButton);

        // Add header panel to the top
        add(headerPanel, BorderLayout.NORTH);

        // Adjust column widths
        setColumnWidths();

        // Fetch and populate results from the database
        fetchResultsFromDatabase();

        // Set the frame to be visible
        setVisible(true);
    }

    // Method to create a button with standard properties
    private JButton createButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        return button;
    }

    // Method to set column widths
    private void setColumnWidths() {
        TableColumn column;
        for (int i = 0; i < resultsTable.getColumnCount(); i++) {
            column = resultsTable.getColumnModel().getColumn(i);
            if (i == 0 || i == 1) { // Student Name and Test Title
                column.setPreferredWidth(200);
            } else if (i == 4) { // Correct Answers
                column.setPreferredWidth(70);
            } else {
                column.setPreferredWidth(50);
            }
        }
    }

    // Method to fetch results from the database and populate the table
    private void fetchResultsFromDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT s.name AS student_name, t.title AS test_title, r.score, r.total_questions, r.timestamp " +
                             "FROM results r " +
                             "JOIN students s ON r.student_id = s.id " +
                             "JOIN tests t ON r.test_id = t.id");
             ResultSet rs = stmt.executeQuery()) {

            // Clear previous data
            tableModel.setRowCount(0);

            // Loop through the result set and populate the table
            while (rs.next()) {
                String studentName = rs.getString("student_name");
                String testTitle = rs.getString("test_title");
                int score = rs.getInt("score");
                int totalQuestions = rs.getInt("total_questions");
                int correctAnswers = score; // Assuming score is equal to correct answers
                Timestamp timestamp = rs.getTimestamp("timestamp");

                // Add row to the table model
                tableModel.addRow(new Object[]{studentName, testTitle, score, totalQuestions, correctAnswers, formatTimestamp(timestamp)});
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error fetching results from the database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Method to format timestamp for display
    private String formatTimestamp(Timestamp timestamp) {
        return timestamp != null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp) : "N/A";
    }


    // Action listener for the refresh button
    private class RefreshButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            fetchResultsFromDatabase(); // Refresh results
        }
    }

    // Action listener for the back button
    private class BackButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            dispose(); // Close this window
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewResultsPage::new);
    }
}
