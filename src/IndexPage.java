import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
public class IndexPage extends JFrame {

    private Image backgroundImage;

    public IndexPage() {

        setTitle("Exam Portal - Index Page");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        try {
            backgroundImage = ImageIO.read(new File("C:\\Users\\Lenovo\\OneDrive\\Desktop\\index2image.jpg"));
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

        JLabel titleLabel = new JLabel("Welcome to Exam Portal");
        titleLabel.setBounds(500, 50, 600, 60);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(new Color(255, 223, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(titleLabel);

        JButton btnRegister = new JButton("Student Registration");
        btnRegister.setBounds(650, 200, 300, 50);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 18));
        btnRegister.setBackground(new Color(29, 214, 151));
        btnRegister.setForeground(Color.WHITE);
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new RegistrationPage().setVisible(true);
                dispose();
            }
        });
        backgroundPanel.add(btnRegister);

        JButton btnStudentLogin = new JButton("Student Login");
        btnStudentLogin.setBounds(650, 280, 300, 50);
        btnStudentLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnStudentLogin.setBackground(new Color(18, 74, 174));
        btnStudentLogin.setForeground(Color.WHITE);
        btnStudentLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StudentLoginPage().setVisible(true);
                dispose();
            }
        });
        backgroundPanel.add(btnStudentLogin);

        JButton btnAdminLogin = new JButton("Admin Login");
        btnAdminLogin.setBounds(650, 360, 300, 50);
        btnAdminLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnAdminLogin.setBackground(new Color(181, 48, 24));
        btnAdminLogin.setForeground(Color.WHITE);
        btnAdminLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminLoginPage().setVisible(true);
                dispose();
            }
        });
        backgroundPanel.add(btnAdminLogin);
        setContentPane(backgroundPanel);
    }

    public static void main(String[] args) {
        IndexPage indexPage = new IndexPage();
        indexPage.setVisible(true);
    }
}





