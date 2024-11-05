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

public class AdminLoginPage extends JFrame {

    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private BufferedImage backgroundImage;

    public AdminLoginPage() {
        // Frame properties
        setTitle("Admin Login");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen mode
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Lenovo\\Downloads\\DeWatermark.ai_1729786023462.png")); // Update with your image path
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null); // Using absolute positioning

        JLabel titleLabel = new JLabel("Admin Login");
        titleLabel.setBounds(450, 150, 600, 60);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(217, 13, 7)); // Dark Green
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titleLabel);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setBounds(500, 300, 150, 30);
        lblUsername.setFont(new Font("Arial", Font.BOLD, 20));
        lblUsername.setForeground(new Color(0, 102, 0)); // Dark Green
        backgroundPanel.add(lblUsername);

        txtUsername = new JTextField();
        txtUsername.setBounds(670, 300, 300, 30);
        backgroundPanel.add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setBounds(500, 350, 150, 30);
        lblPassword.setFont(new Font("Arial", Font.BOLD, 20));
        lblPassword.setForeground(new Color(0, 102, 0)); // Dark Green
        backgroundPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        txtPassword.setBounds(670, 350, 300, 30);
        backgroundPanel.add(txtPassword);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(670, 410, 130, 50);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setBackground(new Color(53, 211, 18)); // Medium Aquamarine
        btnLogin.setForeground(Color.WHITE);
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                if (loginAdmin(username, password)) {
                    JOptionPane.showMessageDialog(null, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    new AdminDashboardPage().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            private boolean loginAdmin(String username, String password) {
                try {
                    // Database connection
                    Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224"); // Update with your database credentials
                    String sql = "SELECT * FROM admins WHERE username = ? AND password = ?";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, username);
                    pstmt.setString(2, password);
                    ResultSet rs = pstmt.executeQuery();

                    boolean exists = rs.next();

                    rs.close();
                    pstmt.close();
                    conn.close();
                    return exists;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Database connection error.", "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
        });
        backgroundPanel.add(btnLogin);

        // Back Button
        JButton btnBack = new JButton("Back");
        btnBack.setBounds(840, 410, 130, 50);
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
        AdminLoginPage loginPage = new AdminLoginPage();
        loginPage.setVisible(true);
    }
}
