import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class EditTestPage extends JFrame {
    private JComboBox<String> testComboBox;
    private JTextField titleField;
    private JTextField timeLimitField;

    public EditTestPage() {
        setTitle("Edit Test");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full screen
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout()); // Using GridBagLayout for better positioning

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 20, 20, 20); // Adding padding

        // Set a gradient background
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, Color.decode("#F0F4F8"), 0, getHeight(), Color.decode("#C7D2D8"));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());
        add(backgroundPanel);

        // Set constraints for the background panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        backgroundPanel.add(new JLabel("Select Test:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7; // Width of combo box
        testComboBox = new JComboBox<>();
        loadTests();
        testComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                loadSelectedTestDetails();
            }
        });
        backgroundPanel.add(testComboBox, gbc);

        // Text field for test title
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        backgroundPanel.add(new JLabel("Test Title:"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        titleField = new JTextField();
        titleField.setFont(new Font("Arial", Font.PLAIN, 16));
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#007BFF"), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Add padding inside the text field
        backgroundPanel.add(titleField, gbc);

        // Text field for time limit
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.3;
        backgroundPanel.add(new JLabel("Time Limit (minutes):"), gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 0.7;
        timeLimitField = new JTextField();
        timeLimitField.setFont(new Font("Arial", Font.PLAIN, 16));
        timeLimitField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.decode("#007BFF"), 1),
                BorderFactory.createEmptyBorder(5, 5, 5, 5))); // Add padding inside the text field
        backgroundPanel.add(timeLimitField, gbc);

        // Save button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        JButton saveButton = new JButton("Save Changes");
        styleButton(saveButton, Color.decode("#28A745")); // Green color
        saveButton.addActionListener(new SaveButtonListener());
        backgroundPanel.add(saveButton, gbc);

        // Delete button
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0.5;
        JButton deleteButton = new JButton("Delete Test");
        styleButton(deleteButton, Color.decode("#DC3545")); // Red color
        deleteButton.addActionListener(new DeleteButtonListener());
        backgroundPanel.add(deleteButton, gbc);

        // Back button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2; // Span across both columns
        JButton backButton = new JButton("Back");
        styleButton(backButton, Color.decode("#6C757D")); // Gray color
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AdminDashboardPage().setVisible(true);
                dispose(); // Close the page to go back
            }
        });
        backgroundPanel.add(backButton, gbc);

        // Set main panel background color
        backgroundPanel.setBackground(new Color(240, 244, 248));

        setVisible(true);
    }

    private void styleButton(JButton button, Color color) {
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); // Padding inside buttons
        button.setFocusPainted(false); // Remove focus border
    }

    private void loadTests() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement stmt = conn.prepareStatement("SELECT id, title FROM tests");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                testComboBox.addItem(rs.getString("id") + ": " + rs.getString("title"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tests.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedTestDetails() {
        String selectedItem = (String) testComboBox.getSelectedItem();
        if (selectedItem != null) {
            int testId = Integer.parseInt(selectedItem.split(":")[0]);

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
                 PreparedStatement stmt = conn.prepareStatement("SELECT title, time_limit FROM tests WHERE id = ?")) {

                stmt.setInt(1, testId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    titleField.setText(rs.getString("title"));
                    timeLimitField.setText(String.valueOf(rs.getInt("time_limit")));
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error loading test details.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class SaveButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedItem = (String) testComboBox.getSelectedItem();
            if (selectedItem == null) return;
            int testId = Integer.parseInt(selectedItem.split(":")[0]);
            String title = titleField.getText();
            int timeLimit;

            try {
                timeLimit = Integer.parseInt(timeLimitField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(EditTestPage.this, "Please enter a valid number for time limit.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE tests SET title = ?, time_limit = ? WHERE id = ?")) {

                stmt.setString(1, title);
                stmt.setInt(2, timeLimit);
                stmt.setInt(3, testId);
                stmt.executeUpdate();

                JOptionPane.showMessageDialog(EditTestPage.this, "Test updated successfully!");
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(EditTestPage.this, "Error updating test.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String selectedItem = (String) testComboBox.getSelectedItem();
            if (selectedItem == null) return;
            int testId = Integer.parseInt(selectedItem.split(":")[0]);

            int confirm = JOptionPane.showConfirmDialog(EditTestPage.this, "Are you sure you want to delete this test?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM tests WHERE id = ?")) {

                    stmt.setInt(1, testId);
                    stmt.executeUpdate();

                    JOptionPane.showMessageDialog(EditTestPage.this, "Test deleted successfully!");
                    dispose();
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(EditTestPage.this, "Error deleting test.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new EditTestPage());
    }
}
