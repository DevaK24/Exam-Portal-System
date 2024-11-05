import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class CreateTestPage extends JFrame {
    private JTextField txtTestTitle;
    private JTextField txtTimeLimit;

    public CreateTestPage() {
        setTitle("Create Test");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        getContentPane().setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;


        JLabel lblTitle = new JLabel("Create New Test");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(new Color(0, 51, 102));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(20, 0, 20, 0);
        add(lblTitle, gbc);

        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(10, 10, 10, 10);
        JLabel lblTestTitle = new JLabel("Test Title:");
        lblTestTitle.setFont(new Font("Arial", Font.PLAIN, 20));
        add(lblTestTitle, gbc);

        txtTestTitle = new JTextField(20);
        txtTestTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        add(txtTestTitle, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblTimeLimit = new JLabel("Time Limit (minutes):");
        lblTimeLimit.setFont(new Font("Arial", Font.PLAIN, 20));
        add(lblTimeLimit, gbc);

        txtTimeLimit = new JTextField(20);
        txtTimeLimit.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        add(txtTimeLimit, gbc);

        JButton btnSubmit = new JButton("Create Test");
        btnSubmit.setFont(new Font("Arial", Font.BOLD, 20));
        btnSubmit.setBackground(new Color(0, 102, 204));
        btnSubmit.setForeground(Color.WHITE);
        btnSubmit.setFocusPainted(false);
        btnSubmit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                createTest();
            }
        });
        gbc.gridx = 1; gbc.gridy = 3; gbc.insets = new Insets(20, 0, 20, 0);
        add(btnSubmit, gbc);

        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setBackground(new Color(128, 128, 128));
        btnBack.setForeground(Color.WHITE);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminDashboardPage().setVisible(true);
                dispose();
            }
        });
        gbc.gridx = 0; gbc.gridy = 3; gbc.insets = new Insets(20, 0, 20, 0);
        add(btnBack, gbc);

        setVisible(true);
    }

    private void createTest() {
        String title = txtTestTitle.getText().trim();
        String timeLimitStr = txtTimeLimit.getText().trim();

        if (title.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Test title cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int timeLimit;
        try {
            timeLimit = Integer.parseInt(timeLimitStr);
            if (timeLimit <= 0) {
                JOptionPane.showMessageDialog(this, "Time limit must be a positive integer.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Time limit must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement("INSERT INTO tests (title, time_limit) VALUES (?, ?)")) {
            pstmt.setString(1, title);
            pstmt.setInt(2, timeLimit);
            pstmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Test created successfully!");
            clearFields();
            dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error creating test. Check your connection and data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields() {
        txtTestTitle.setText("");
        txtTimeLimit.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(CreateTestPage::new);
    }
}
