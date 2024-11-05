import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class StudentLoginPage extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private BufferedImage backgroundImage;

    public StudentLoginPage() {

        setTitle("Student Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Lenovo\\Downloads\\attack-6806140_1280.png")); // Update with your image path
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);

        // Title Label
        JLabel titleLabel = new JLabel("Student Login");
        titleLabel.setBounds(450, 50, 600, 60);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 223, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titleLabel);

        // Username Label and Field
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(450, 150, 150, 30);
        lblUsername.setFont(new Font("Arial", Font.BOLD, 20));
        lblUsername.setForeground(new Color(0, 51, 102));
        backgroundPanel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(600, 150, 300, 30);
        backgroundPanel.add(txtUsername);

        // Password Label and Field
        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(450, 200, 150, 30);
        lblPassword.setFont(new Font("Arial", Font.BOLD, 20));
        lblPassword.setForeground(new Color(0, 51, 102));
        backgroundPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(600, 200, 300, 30);
        backgroundPanel.add(txtPassword);

        // Login Button
        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(600, 270, 130, 50);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setBackground(new Color(102, 205, 170));
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                int studentId = loginStudent(username, password);
                if (studentId != -1) {
                    JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    new StudentPage(username).setVisible(true);
                    dispose(); // Close the login page
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Method to verify login and retrieve student ID
            private int loginStudent(String username, String password) {
                int studentId = -1;
                try {
                    // Database connection
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
                    String sql = "SELECT id FROM students WHERE username = ? AND password = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        studentId = rs.getInt("id");
                    }

                    rs.close();
                    pstmt.close();
                    conn.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                return studentId;
            }
        });
        backgroundPanel.add(btnLogin);

        // Back Button
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(740, 270, 130, 50);
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setBackground(Color.RED);
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new IndexPage().setVisible(true);
                dispose();
            }
        });
        backgroundPanel.add(btnBack);

        setContentPane(backgroundPanel);
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Scale image to fit the panel
            }
        }
    }

    public static void main(String[] args) {
        StudentLoginPage loginPage = new StudentLoginPage();
        loginPage.setVisible(true);
    }
}
