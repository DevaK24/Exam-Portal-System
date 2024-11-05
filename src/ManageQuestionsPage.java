import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class ManageQuestionsPage extends JFrame {
    private JComboBox<String> testsComboBox;
    private JList<String> questionsList;
    private DefaultListModel<String> listModel;
    private JTextField questionTextField, optionAField, optionBField, optionCField, optionDField, correctOptionField;
    private JButton loadQuestionsButton, addQuestionButton, updateQuestionButton, deleteQuestionButton, backButton;
    private ArrayList<Question> questions; // Store the questions in an ArrayList

    public ManageQuestionsPage() {
        setTitle("Manage Questions");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Full-screen mode
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Manage Questions");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // Center Panel
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);

        // Tests Dropdown
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        testsComboBox = new JComboBox<>();
        loadTests(); // Load tests from database
        centerPanel.add(testsComboBox, gbc);

        // Load Questions Button
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridwidth = 1;
        loadQuestionsButton = new JButton("Load Questions");
        loadQuestionsButton.setBackground(new Color(30, 144, 255));
        loadQuestionsButton.setForeground(Color.WHITE);
        loadQuestionsButton.setFont(new Font("Arial", Font.BOLD, 14));
        loadQuestionsButton.addActionListener(new LoadQuestionsListener());
        centerPanel.add(loadQuestionsButton, gbc);

        // Questions List
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 3;
        listModel = new DefaultListModel<>();
        questionsList = new JList<>(listModel);
        questionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        questionsList.addListSelectionListener(e -> loadSelectedQuestion());
        JScrollPane listScrollPane = new JScrollPane(questionsList);
        listScrollPane.setPreferredSize(new Dimension(500, 300));
        listScrollPane.setBorder(BorderFactory.createTitledBorder("Questions List"));
        centerPanel.add(listScrollPane, gbc);

        // Question TextField
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5;
        questionTextField = new JTextField(30);
        questionTextField.setBorder(BorderFactory.createTitledBorder("Question Text"));
        centerPanel.add(questionTextField, gbc);

        // Option TextFields
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 1;
        optionAField = new JTextField(15);
        optionAField.setBorder(BorderFactory.createTitledBorder("Option A"));
        centerPanel.add(optionAField, gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        optionBField = new JTextField(15);
        optionBField.setBorder(BorderFactory.createTitledBorder("Option B"));
        centerPanel.add(optionBField, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        optionCField = new JTextField(15);
        optionCField.setBorder(BorderFactory.createTitledBorder("Option C"));
        centerPanel.add(optionCField, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        optionDField = new JTextField(15);
        optionDField.setBorder(BorderFactory.createTitledBorder("Option D"));
        centerPanel.add(optionDField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        correctOptionField = new JTextField(5);
        correctOptionField.setBorder(BorderFactory.createTitledBorder("Correct Option (A/B/C/D)"));
        centerPanel.add(correctOptionField, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(240, 240, 240));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        add(buttonPanel, BorderLayout.SOUTH);

        addQuestionButton = new JButton("Add Question");
        addQuestionButton.setBackground(new Color(34, 139, 34));
        addQuestionButton.setForeground(Color.WHITE);
        addQuestionButton.setFont(new Font("Arial", Font.BOLD, 14));
        addQuestionButton.addActionListener(new AddQuestionListener());
        buttonPanel.add(addQuestionButton);

        updateQuestionButton = new JButton("Update Question");
        updateQuestionButton.setBackground(new Color(255, 140, 0));
        updateQuestionButton.setForeground(Color.WHITE);
        updateQuestionButton.setFont(new Font("Arial", Font.BOLD, 14));
        updateQuestionButton.addActionListener(new UpdateQuestionListener());
        buttonPanel.add(updateQuestionButton);

        deleteQuestionButton = new JButton("Delete Question");
        deleteQuestionButton.setBackground(new Color(255, 0, 0));
        deleteQuestionButton.setForeground(Color.WHITE);
        deleteQuestionButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteQuestionButton.addActionListener(new DeleteQuestionListener());
        buttonPanel.add(deleteQuestionButton);
        // Inside the constructor after initializing other buttons
        JButton resetButton = new JButton("Reset");
        resetButton.setBackground(Color.LIGHT_GRAY);
        resetButton.setForeground(Color.BLACK);
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.addActionListener(e -> resetFields());
        buttonPanel.add(resetButton);


        backButton = new JButton("Back");
        backButton.setBackground(Color.GRAY);
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.addActionListener(e -> dispose());
        buttonPanel.add(backButton);

        setVisible(true);
    }

    private void loadTests() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT title FROM tests")) {

            while (rs.next()) {
                testsComboBox.addItem(rs.getString("title"));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading tests: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    // Method to clear all input fields
    private void resetFields() {
        questionTextField.setText("");
        optionAField.setText("");
        optionBField.setText("");
        optionCField.setText("");
        optionDField.setText("");
        correctOptionField.setText("");
    }

    private void loadQuestions() {
        listModel.clear();
        questions = new ArrayList<>(); // Clear the previous list of questions
        String selectedTest = (String) testsComboBox.getSelectedItem();

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM questions WHERE test_id = (SELECT id FROM tests WHERE title = ?)")) {

            stmt.setString(1, selectedTest);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String questionText = rs.getString("question_text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                String correctOption = rs.getString("correct_option");
                questions.add(new Question(id, questionText, optionA, optionB, optionC, optionD, correctOption));
                listModel.addElement(questionText); // Display question text in the list
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading questions: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadSelectedQuestion() {
        int selectedIndex = questionsList.getSelectedIndex();
        if (selectedIndex >= 0) {
            Question selectedQuestion = questions.get(selectedIndex);
            questionTextField.setText(selectedQuestion.getQuestionText());
            optionAField.setText(selectedQuestion.getOptionA());
            optionBField.setText(selectedQuestion.getOptionB());
            optionCField.setText(selectedQuestion.getOptionC());
            optionDField.setText(selectedQuestion.getOptionD());
            correctOptionField.setText(selectedQuestion.getCorrectOption());
        }
    }

    private class LoadQuestionsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            loadQuestions(); // Reload questions based on selected test
        }
    }

    private class AddQuestionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String questionText = questionTextField.getText();
            String optionA = optionAField.getText();
            String optionB = optionBField.getText();
            String optionC = optionCField.getText();
            String optionD = optionDField.getText();
            String correctOption = correctOptionField.getText().toUpperCase(); // Ensure it's uppercase
            String selectedTest = (String) testsComboBox.getSelectedItem();

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO questions (test_id, question_text, option_a, option_b, option_c, option_d, correct_option) VALUES ((SELECT id FROM tests WHERE title = ?), ?, ?, ?, ?, ?, ?)")) {

                stmt.setString(1, selectedTest);
                stmt.setString(2, questionText);
                stmt.setString(3, optionA);
                stmt.setString(4, optionB);
                stmt.setString(5, optionC);
                stmt.setString(6, optionD);
                stmt.setString(7, correctOption);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Question added successfully!");
                loadQuestions(); // Refresh the questions list
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Error adding question: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class UpdateQuestionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = questionsList.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Please select a question to update.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Question selectedQuestion = questions.get(selectedIndex);
            String questionText = questionTextField.getText();
            String optionA = optionAField.getText();
            String optionB = optionBField.getText();
            String optionC = optionCField.getText();
            String optionD = optionDField.getText();
            String correctOption = correctOptionField.getText().toUpperCase(); // Ensure it's uppercase
            String selectedTest = (String) testsComboBox.getSelectedItem();

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
                 PreparedStatement stmt = conn.prepareStatement("UPDATE questions SET question_text = ?, option_a = ?, option_b = ?, option_c = ?, option_d = ?, correct_option = ? WHERE id = ?")) {

                stmt.setString(1, questionText);
                stmt.setString(2, optionA);
                stmt.setString(3, optionB);
                stmt.setString(4, optionC);
                stmt.setString(5, optionD);
                stmt.setString(6, correctOption);
                stmt.setInt(7, selectedQuestion.getId());
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Question updated successfully!");
                loadQuestions(); // Refresh the questions list
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Error updating question: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteQuestionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = questionsList.getSelectedIndex();
            if (selectedIndex < 0) {
                JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Please select a question to delete.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Question selectedQuestion = questions.get(selectedIndex);
            int confirm = JOptionPane.showConfirmDialog(ManageQuestionsPage.this, "Are you sure you want to delete this question?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM questions WHERE id = ?")) {

                    stmt.setInt(1, selectedQuestion.getId());
                    stmt.executeUpdate();
                    JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Question deleted successfully!");
                    loadQuestions(); // Refresh the questions list
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(ManageQuestionsPage.this, "Error deleting question: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    // Question class to store question data
    private static class Question {
        private int id;
        private String questionText;
        private String optionA;
        private String optionB;
        private String optionC;
        private String optionD;
        private String correctOption;

        public Question(int id, String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption) {
            this.id = id;
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctOption = correctOption;
        }

        public int getId() {
            return id;
        }

        public String getQuestionText() {
            return questionText;
        }

        public String getOptionA() {
            return optionA;
        }

        public String getOptionB() {
            return optionB;
        }

        public String getOptionC() {
            return optionC;
        }

        public String getOptionD() {
            return optionD;
        }

        public String getCorrectOption() {
            return correctOption;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManageQuestionsPage());
    }
}
