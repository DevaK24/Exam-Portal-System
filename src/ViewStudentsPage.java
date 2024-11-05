import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader; // Ensure this is imported
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewStudentsPage extends JFrame {

    public ViewStudentsPage() {
        setTitle("View Students");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set background color
        getContentPane().setBackground(new Color(240, 248, 255)); // Light AliceBlue color

        // Table to display students
        String[] columnNames = {"Student ID", "Name", "Branch", "Roll Number"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(model);
        loadStudents(model);

        // Customize table appearance
        table.setFillsViewportHeight(true);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setBackground(new Color(255, 255, 255)); // White background for the table
        table.setForeground(new Color(0, 0, 0)); // Black text

        // Customize table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(100, 149, 237)); // Cornflower blue header
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Arial", Font.BOLD, 16));

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Create a back button
        JButton backButton = new JButton("Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setBackground(new Color(100, 149, 237)); // Button color
        backButton.setForeground(Color.WHITE); // Button text color
        backButton.addActionListener(e -> {
            // Code to go back to the previous page
            // For example, dispose the current frame and show the previous frame
            this.dispose(); // Close current frame
            new AdminDashboardPage(); // Assuming you have an AdminDashboardPage class
        });

        // Add the back button at the bottom
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER)); // Align to the right
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadStudents(DefaultTableModel model) {
        // Clear existing rows
        model.setRowCount(0);

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM students");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String branch = rs.getString("branch");
                String rollNumber = rs.getString("roll_number");
                model.addRow(new Object[]{id, name, branch, rollNumber});
            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading students.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewStudentsPage::new);
    }
}
