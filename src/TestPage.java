import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TestPage extends JFrame {
    private JTextArea questionArea;
    private JRadioButton[] options;
    private ButtonGroup optionGroup;
    private JButton nextButton;
    private JButton submitButton;
    private JButton backButton;
    private JLabel timerLabel;
    private Timer timer;
    private int questionIndex = 0;
    private String username;
    private String testTitle;
    private int[] selectedAnswers;
    private List<Question> questions;
    private int timeRemaining;


    public TestPage(String username, String testTitle) {
        this.username = username;
        this.testTitle = testTitle;

        setTitle("Test: " + testTitle);
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Setup question area
        questionArea = new JTextArea();
        questionArea.setEditable(false);
        questionArea.setFont(new Font("Arial", Font.PLAIN, 24));
        questionArea.setBackground(new Color(240, 240, 240));
        questionArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        questionArea.setLineWrap(true);
        questionArea.setWrapStyleWord(true);

        // Timer label (Top-right alignment)
        timerLabel = new JLabel("Time Remaining: 00:00");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setForeground(Color.RED);
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        timerPanel.add(timerLabel);

        // Combine timerPanel and questionArea into topPanel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(timerPanel, BorderLayout.EAST);
        topPanel.add(new JScrollPane(questionArea), BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Options panel
        JPanel optionsPanel = new JPanel(new GridLayout(0, 1, 0, 10)); // 10 pixels of vertical spacing
        optionsPanel.setBackground(Color.WHITE);
        options = new JRadioButton[4];
        optionGroup = new ButtonGroup();

        for (int i = 0; i < options.length; i++) {
            options[i] = new JRadioButton();
            options[i].setFont(new Font("Arial", Font.PLAIN, 18));
            options[i].setBackground(Color.WHITE);
            options[i].setForeground(Color.BLACK);
            optionsPanel.add(options[i]);
            optionGroup.add(options[i]);
        }
        add(optionsPanel, BorderLayout.CENTER);

        // Button panel
        backButton = new JButton("Back");
        backButton.setBackground(new Color(255, 223, 186));
        backButton.addActionListener(e -> previousQuestion());

        nextButton = new JButton("Next");
        nextButton.setBackground(new Color(186, 255, 223));
        nextButton.addActionListener(e -> nextQuestion());

        submitButton = new JButton("Submit");
        submitButton.setEnabled(false);
        submitButton.setBackground(new Color(186, 186, 255));
        submitButton.addActionListener(e -> submitTest());

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);
        buttonPanel.add(submitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        loadQuestions();
        startTestTimer();
        loadQuestion();

        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Load questions from the database
    private void loadQuestions() {
        questions = new ArrayList<>();
        String query = "SELECT * FROM questions WHERE test_id = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, getTestId(testTitle));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String questionText = rs.getString("question_text");
                String optionA = rs.getString("option_a");
                String optionB = rs.getString("option_b");
                String optionC = rs.getString("option_c");
                String optionD = rs.getString("option_d");
                char correctOption = rs.getString("correct_option").charAt(0);
                questions.add(new Question(questionText, optionA, optionB, optionC, optionD, correctOption));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading questions.");
        }
        selectedAnswers = new int[questions.size()];
    }

    // Load the current question
    private void loadQuestion() {
        if (questionIndex < questions.size()) {
            Question question = questions.get(questionIndex);
            questionArea.setText((questionIndex + 1) + ". " + question.getQuestionText());
            options[0].setText("A. " + question.getOptionA());
            options[1].setText("B. " + question.getOptionB());
            options[2].setText("C. " + question.getOptionC());
            options[3].setText("D. " + question.getOptionD());
            optionGroup.clearSelection();
        } else {
            nextButton.setEnabled(false);
            submitButton.setEnabled(true);
        }
    }

    // Load next question
    private void nextQuestion() {
        if (optionGroup.getSelection() != null) {
            selectedAnswers[questionIndex] = getSelectedOption();
            questionIndex++;
            loadQuestion();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an option.");
        }
    }



    // Submit the test
    private void submitTest() {
        int score = calculateScore();
        int resultId = saveResult(score);
        saveAnswers(resultId);
        new ResultPage(score, questions.size(), selectedAnswers, questions, username);
        dispose();
    }


    // Get selected option index (0 - 3)
    private int getSelectedOption() {
        for (int i = 0; i < options.length; i++) {
            if (options[i].isSelected()) {
                return i;
            }
        }
        return -1;
    }

    // Calculate score
    private int calculateScore() {
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            if (selectedAnswers[i] != -1) {
                if (question.getCorrectOption() == (char) ('A' + selectedAnswers[i])) {
                    score++;
                }
            }
        }
        return score;
    }

    private void previousQuestion() {
        if (questionIndex > 0) {
            selectedAnswers[questionIndex] = getSelectedOption();
            questionIndex--;
            loadQuestion();
        } else {
            JOptionPane.showMessageDialog(this, "This is the first question.");
        }
    }

    // Save results
    private int saveResult(int score) {
        String query = "INSERT INTO results (student_id, test_id, score, total_questions) VALUES (?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, getStudentId(username));
            pstmt.setInt(2, getTestId(testTitle));
            pstmt.setInt(3, score);
            pstmt.setInt(4, questions.size());
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error saving results: " + ex.getMessage());
        }
        return -1;
    }

    // Save answers to the student_answers table
    private void saveAnswers(int resultId) {
        String query = "INSERT INTO student_answers (result_id, question_id, selected_answer) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            for (int i = 0; i < questions.size(); i++) {
                pstmt.setInt(1, resultId);
                pstmt.setInt(2, i + 1); // Assuming question IDs are sequential, replace with actual method if different
                pstmt.setString(3, String.valueOf((char) ('A' + selectedAnswers[i])));
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Timer for the entire test duration
    private void startTestTimer() {
        timeRemaining = getTestTime() * 60;
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    updateTimerLabel();
                } else {
                    timer.cancel();
                    submitTest();
                }
            }
        }, 1000, 1000);
    }

    private void updateTimerLabel() {
        int minutes = timeRemaining / 60;
        int seconds = timeRemaining % 60;
        timerLabel.setText(String.format("Time Remaining: %02d:%02d", minutes, seconds));
    }

    private int getStudentId(String username) {
        String query = "SELECT id FROM students WHERE username = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private int getTestId(String testTitle) {
        String query = "SELECT id FROM tests WHERE title = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, testTitle);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return -1;
    }

    private int getTestTime() {
        String query = "SELECT time_limit FROM tests WHERE title = ?";
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/exam", "root", "mysqldev224");
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, testTitle);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("time_limit"); // This should be in minutes
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 60;
    }

    // Question class (inner class)
    class Question {
        private String questionText, optionA, optionB, optionC, optionD;
        private char correctOption;

        public Question(String questionText, String optionA, String optionB, String optionC, String optionD, char correctOption) {
            this.questionText = questionText;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctOption = correctOption;
        }

        public String getQuestionText() { return questionText; }
        public String getOptionA() { return optionA; }
        public String getOptionB() { return optionB; }
        public String getOptionC() { return optionC; }
        public String getOptionD() { return optionD; }
        public char getCorrectOption() { return correctOption; }
    }
}
