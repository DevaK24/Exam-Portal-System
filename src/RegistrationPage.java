import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.imageio.ImageIO;
import java.io.IOException;

public class RegistrationPage extends JFrame {

    private Image backgroundImage;
    private final String DB_URL = "jdbc:mysql://localhost:3306/exam";
    private final String USER = "root";
    private final String PASS = "mysqldev224";

    public RegistrationPage() {
        setTitle("Student Registration");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Lenovo\\OneDrive\\Desktop\\DeWatermark.ai_1729779541362.png"));
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Background image could not be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
        }

        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        backgroundPanel.setLayout(null);

        JLabel titleLabel = new JLabel("Student Registration");
        titleLabel.setBounds(450, 50, 600, 60);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
        titleLabel.setForeground(new Color(255, 215, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titleLabel);


        int yPosition = 150;
        Font labelFont = new Font("Arial", Font.BOLD, 20);
        Color labelColor = new Color(0, 25, 255);
        JTextField txtName = createField(backgroundPanel, "Name:", yPosition, labelFont, labelColor);
        JTextField txtRollNumber = createField(backgroundPanel, "Roll Number:", yPosition += 70, labelFont, labelColor);
        JTextField txtUsername = createField(backgroundPanel, "Username:", yPosition += 70, labelFont, labelColor);
        JPasswordField txtPassword = new JPasswordField();
        createFieldWithComponent(backgroundPanel, "Password:", txtPassword, yPosition += 70, labelFont, labelColor);

        JLabel lblBranch = new JLabel("Select Branch:");
        lblBranch.setBounds(450, yPosition += 70, 150, 30);
        lblBranch.setFont(labelFont);
        lblBranch.setForeground(labelColor);
        backgroundPanel.add(lblBranch);

        String[] branches = {"Computer", "Electronics", "Civil"};
        JComboBox<String> branchComboBox = new JComboBox<>(branches);
        branchComboBox.setBounds(600, yPosition, 300, 30);
        branchComboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        backgroundPanel.add(branchComboBox);

        JButton btnRegister = createButton("Register", 570, yPosition += 100, new Color(102, 205, 170));
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String name = txtName.getText();
                String rollNumber = txtRollNumber.getText();
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                String branch = (String) branchComboBox.getSelectedItem();
                try (Connection conn = DriverManager.getConnection(DB_URL, USER, PASS)) {
                    String sql = "INSERT INTO students (name, roll_number, username, password, branch) VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, name);
                    pstmt.setString(2, rollNumber);
                    pstmt.setString(3, username);
                    pstmt.setString(4, password);
                    pstmt.setString(5, branch);
                    pstmt.executeUpdate();
                    JOptionPane.showMessageDialog(null, "Registration successful!");
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(null, "Registration failed: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        backgroundPanel.add(btnRegister);

        // Back Button
        JButton btnBack = createButton("Back", 740, yPosition, Color.RED);
       btnBack.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent e) {
               new IndexPage().setVisible(true);
           }
       });
        backgroundPanel.add(btnBack);

        setContentPane(backgroundPanel);
    }

    private JTextField createField(JPanel panel, String labelText, int yPos, Font labelFont, Color labelColor) {
        JLabel label = new JLabel(labelText);
        label.setBounds(450, yPos, 150, 30);
        label.setFont(labelFont);
        label.setForeground(labelColor);
        panel.add(label);

        JTextField textField = new JTextField();
        textField.setBounds(600, yPos, 300, 30);
        textField.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(textField);

        return textField;
    }

    private void createFieldWithComponent(JPanel panel, String labelText, JComponent component, int yPos, Font labelFont, Color labelColor) {
        JLabel label = new JLabel(labelText);
        label.setBounds(450, yPos, 150, 30);
        label.setFont(labelFont);
        label.setForeground(labelColor);
        panel.add(label);
        component.setBounds(600, yPos, 300, 30);
        component.setFont(new Font("Arial", Font.PLAIN, 18));
        panel.add(component);
    }

    private JButton createButton(String text, int xPos, int yPos, Color bgColor) {
        JButton button = new JButton(text);
        button.setBounds(xPos, yPos, 130, 50);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegistrationPage().setVisible(true));
    }
}
