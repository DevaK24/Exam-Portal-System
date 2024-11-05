import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewTestsPage extends JFrame {
    private JTable table;
    private DefaultTableModel model;

    public ViewTestsPage() {
        // Frame properties
        setTitle("View Tests");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table to display tests
        String[] columnNames = {"Test ID", "Test Title", "Time Limit (minutes)"};
        model = new DefaultTableModel(columnNames, 0);
        table = new JTable(model);
        loadTests();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button to view questions of selected test
        JButton viewQuestionsButton = new JButton("View Questions");
        viewQuestionsButton.setFont(new Font("Arial", Font.BOLD, 16));
        viewQuestionsButton.setBackground(new Color(70, 130, 180)); // Steel blue
        viewQuestionsButton.setForeground(Color.WHITE);
        add(viewQuestionsButton, BorderLayout.SOUTH);

        // Back button to return to the previous page
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        add(backButton, BorderLayout.NORTH);

        // Add action listener for buttons
        viewQuestionsButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    int testId = (int) model.getValueAt(selectedRow, 0);
                    new ViewQuestionsPage(testId);
                } else {
                    JOptionPane.showMessageDialog(ViewTestsPage.this, "Please select a test to view questions.", "No Test Selected", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
                // Optionally, return to the main page or previous page
            }
        });

        setVisible(true);
    }

    private void loadTests() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tests");

            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                int timeLimit = rs.getInt("time_limit");
                model.addRow(new Object[]{id, title, timeLimit});
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tests.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class ViewQuestionsPage extends JFrame {

    public ViewQuestionsPage(int testId) {
        // Frame properties
        setTitle("View Questions for Test ID: " + testId);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Table to display questions with correct answer column
        String[] columnNames = {"Question ID", "Question Text", "Option A", "Option B", "Option C", "Option D", "Correct Answer"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        loadQuestions(testId, model);

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Back button to return to the tests page
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 16));
        backButton.setBackground(Color.RED);
        backButton.setForeground(Color.WHITE);
        add(backButton, BorderLayout.NORTH);

        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close the current window
            }
        });

        setVisible(true);
    }

    private void loadQuestions(int testId, DefaultTableModel model) {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM questions WHERE test_id = " + testId);

            while (rs.next()) {
                int questionId = rs.getInt("id");
                String questionText = rs.getString("question_text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                String correctOption = rs.getString("correct_option"); // Fetch correct answer

                model.addRow(new Object[]{questionId, questionText, optionA, optionB, optionC, optionD, correctOption});
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading questions.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
