import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class StudentPage extends JFrame {
    private JComboBox<String> testComboBox;
    private JButton startTestButton, logoutButton, viewResultsButton, backButton;
    private JLabel welcomeLabel;
    private String rollnumber;

    public StudentPage(String username) {
        // Set full-screen properties
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Student Home Page");

        // Welcome label
        welcomeLabel = new JLabel("Welcome, " + username + "!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(Color.WHITE);

        // Dropdown for selecting a test
        JLabel selectTestLabel = new JLabel("Select a Test:");
        selectTestLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        selectTestLabel.setForeground(Color.WHITE);
        testComboBox = new JComboBox<>();
        loadTests();

        // Buttons
        startTestButton = createStyledButton("Start Test");
        viewResultsButton = createStyledButton("View Previous Results");
        logoutButton = createStyledButton("Logout");
        backButton = createStyledButton("Back");

        // Tooltips
        startTestButton.setToolTipText("Start the selected test");
        viewResultsButton.setToolTipText("View your previous test results");
        logoutButton.setToolTipText("Log out of your account");
        backButton.setToolTipText("Go back to the previous screen");

        // Action Listeners
        startTestButton.addActionListener(e -> startTest(username));
        viewResultsButton.addActionListener(e -> viewResults(username));
        logoutButton.addActionListener(e -> logout());
        backButton.addActionListener(e -> goBack());

        // Layout for main panel
        JPanel mainPanel = createMainPanel();
        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(new Color(255, 255, 255)); // White background for buttons
        button.setForeground(new Color(70, 130, 180)); // Steel blue text
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(new Color(70, 130, 180), 2));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 50)); // Button size
        button.setMargin(new Insets(10, 10, 10, 10)); // Padding for buttons

        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180));
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(new Color(70, 130, 180));
            }
        });

        return button;
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // Set gradient background
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180), 0, getHeight(), new Color(30, 144, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        // Adding components to main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(welcomeLabel, gbc);

        gbc.gridy++;
        gbc.gridwidth = 1;
        mainPanel.add(new JLabel("Select a Test:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(testComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        mainPanel.add(startTestButton, gbc);

        gbc.gridy++;
        mainPanel.add(viewResultsButton, gbc);
        gbc.gridy++;
        mainPanel.add(backButton, gbc);
        gbc.gridy++;
        mainPanel.add(logoutButton, gbc);

        return mainPanel;
    }

    private void loadTests() {
        String query = "SELECT title FROM tests";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                testComboBox.addItem(rs.getString("title"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tests. Please try again later.");
        }
    }

    private void viewResults(String username) {
        String query = "SELECT t.title, r.score, r.total_questions, r.timestamp " +
                "FROM results r JOIN tests t ON r.test_id = t.id " +
                "JOIN students s ON r.student_id = s.id WHERE s.username = ?";
        StringBuilder resultsMessage = new StringBuilder("Previous Results:\n");
        boolean hasResults = false; // Flag to check if we have results

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                hasResults = true; // We found at least one result
                resultsMessage.append("Test: ").append(rs.getString("title"))
                        .append("\nScore: ").append(rs.getInt("score"))
                        .append("/").append(rs.getInt("total_questions"))
                        .append("\nDate: ").append(rs.getTimestamp("timestamp"))
                        .append("\n\n");
            }

            if (!hasResults) {
                resultsMessage.append("No previous results found.");
            }

            // Create a JTextArea to display the results
            JTextArea textArea = new JTextArea(20, 40); // Adjust dimensions as necessary
            textArea.setText(resultsMessage.toString());
            textArea.setEditable(false); // Make it read-only
            textArea.setLineWrap(true); // Enable line wrapping
            textArea.setWrapStyleWord(true); // Wrap at word boundaries

            // Create a JScrollPane to make the JTextArea scrollable
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

            // Show the results in a dialog
            JOptionPane resultsDialog = new JOptionPane(scrollPane, JOptionPane.INFORMATION_MESSAGE);
            JDialog dialog = resultsDialog.createDialog(this, "Previous Results");
            resultsDialog.setOptionType(JOptionPane.DEFAULT_OPTION);

            dialog.setVisible(true);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error retrieving results. Please try again later.");
        }
    }






    private void startTest(String username) {
        String selectedTest = (String) testComboBox.getSelectedItem();
        if (selectedTest != null) {
            new TestPage(username, selectedTest);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Please select a test.");
        }
    }

    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Logout Confirmation", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dispose();
            JOptionPane.showMessageDialog(this, "Logged out successfully.");
            new StudentLoginPage().setVisible(true);
        }
    }

    private void goBack() {
        dispose();
        new IndexPage().setVisible(true); // Assuming IndexPage is the previous screen
    }

    public static void main(String[] args) {
        new StudentPage("Student Name");
    }
}
