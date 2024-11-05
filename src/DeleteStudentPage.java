import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class DeleteStudentPage extends JFrame {
    private JTextField txtStudentID;

    public DeleteStudentPage() {
        setTitle("Delete Student");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());

        // Background color
        getContentPane().setBackground(new Color(245, 245, 245));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title label
        JLabel lblTitle = new JLabel("Delete Student Record");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(new Color(139, 0, 0));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(lblTitle, gbc);

        // Student ID label and field
        gbc.gridwidth = 1;
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblStudentID = new JLabel("Student ID:");
        lblStudentID.setFont(new Font("Arial", Font.PLAIN, 20));
        add(lblStudentID, gbc);

        txtStudentID = new JTextField(15);
        txtStudentID.setFont(new Font("Arial", Font.PLAIN, 18));
        gbc.gridx = 1;
        add(txtStudentID, gbc);

        // Delete button
        JButton btnDelete = new JButton("Delete Student");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 20));
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteStudent();
            }
        });
        gbc.gridx = 1; gbc.gridy = 2;
        add(btnDelete, gbc);

        // Back button
        JButton btnBack = new JButton("Back");
        btnBack.setFont(new Font("Arial", Font.BOLD, 18));
        btnBack.setBackground(new Color(128, 128, 128));
        btnBack.setForeground(Color.WHITE);
        btnBack.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close this window to go back
            }
        });
        gbc.gridx = 0; gbc.gridy = 2;
        add(btnBack, gbc);

        setVisible(true);
    }

    private void deleteStudent() {
        String studentIDText = txtStudentID.getText().trim();

        // Validate input
        if (studentIDText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a student ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int id;
        try {
            id = Integer.parseInt(studentIDText);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Student ID must be a number.", "Validation Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM students WHERE id = ?")) {

            pstmt.setInt(1, id);
            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                txtStudentID.setText(""); // Clear input field
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error deleting student. Check your connection and data.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(DeleteStudentPage::new);
    }
}
