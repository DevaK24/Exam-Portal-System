import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AdminDashboardPage extends JFrame {

    public AdminDashboardPage() {
        setTitle("Admin Dashboard");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen mode
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Set a gradient background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180), 0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };

        backgroundPanel.setLayout(new BorderLayout());

        // Create a custom panel for holding buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for better placement
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30)); // Add padding
        buttonPanel.setOpaque(false); // Make it transparent to see background

        // Create GridBag constraints for button arrangement
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL; // Fill horizontally
        gbc.insets = new Insets(20, 20, 20, 20); // Add space between buttons
        gbc.weightx = 1.0; // Equal weight for buttons

        // Create buttons for various functionalities with icons
        JButton btnCreateTest = createButton("Create Test");
        JButton btnViewTest = createButton("View Tests");
        JButton btnEditTest = createButton("Edit Test");
        JButton btnManageQuestions = createButton("Manage Questions");
        JButton btnViewStudents = createButton("View Students");
        JButton btnDeleteStudent = createButton("Delete Student");
        JButton btnViewResults = createButton("View Results");
        JButton btnLogout = createButton("Logout");

        // Add action listeners for buttons with basic error handling
        btnCreateTest.addActionListener(e -> openPage(new CreateTestPage()));
        btnViewTest.addActionListener(e -> openPage(new ViewTestsPage()));
        btnEditTest.addActionListener(e -> openPage(new EditTestPage()));
        btnManageQuestions.addActionListener(e -> openPage(new ManageQuestionsPage()));
        btnViewStudents.addActionListener(e -> openPage(new ViewStudentsPage()));
        btnDeleteStudent.addActionListener(e -> openPage(new DeleteStudentPage()));
        btnViewResults.addActionListener(e -> openPage(new ViewResultsPage()));

        btnLogout.addActionListener(e -> {
            dispose(); // Close the dashboard
            new IndexPage().setVisible(true); // Open the login page
        });

        // Add buttons to the panel using GridBagLayout
        addButtonsToPanel(buttonPanel, gbc, btnCreateTest, btnViewTest, btnEditTest, btnManageQuestions, btnViewStudents, btnDeleteStudent, btnViewResults, btnLogout);

        // Add button panel to the center of the frame
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Add a header panel with title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(50, 100, 150)); // Darker blue background
        JLabel headerLabel = new JLabel("Admin Dashboard");
        headerLabel.setFont(new Font("Serif", Font.BOLD, 40));
        headerLabel.setForeground(Color.WHITE);
        headerPanel.add(headerLabel);

        // Add header panel to the top of the frame
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // Add the background panel to the frame
        add(backgroundPanel);

        // Set the frame to be visible
        setVisible(true);
    }

    // Helper method to create rounded buttons with consistent styling
    private JButton createButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15); // Round edges
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(new Color(60, 179, 113)); // Medium Sea Green
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false); // No border
        button.setOpaque(false); // Make the button background transparent
        button.setPreferredSize(new Dimension(200, 50)); // Set a preferred size for better spacing

        // Load and set icon

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(46, 139, 87)); // Darker green on hover
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 179, 113)); // Original color
            }
        });

        return button;
    }

    // Method to add buttons to the panel
    private void addButtonsToPanel(JPanel panel, GridBagConstraints gbc, JButton... buttons) {
        int index = 0;
        for (JButton button : buttons) {
            gbc.gridx = index % 2; // Alternate between column 0 and 1
            gbc.gridy = index / 2; // Move to the next row
            panel.add(button, gbc);
            index++;
        }
    }

    // Open new page with error handling
    private void openPage(JFrame page) {
        try {
            page.setVisible(true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error opening page: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        // Create and display the admin dashboard
        SwingUtilities.invokeLater(AdminDashboardPage::new);
    }
}
