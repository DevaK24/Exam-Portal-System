import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class ResultPage extends JFrame {
    private int score;
    private int totalQuestions;
    private int[] selectedAnswers;
    private List<TestPage.Question> questions;
    private String name;
    private String rollnumber;
    private String branch;

    public ResultPage(int score, int totalQuestions, int[] selectedAnswers, List<TestPage.Question> questions, String username) {
        this.score = score;
        this.totalQuestions = totalQuestions;
        this.selectedAnswers = selectedAnswers;
        this.questions = questions;

        // Fetch student details from database
        fetchStudentDetails(username);

        setTitle("Test Results");
        setLayout(new BorderLayout());
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(240, 240, 240)); // Light gray background

        // Score Panel
        JPanel scorePanel = new JPanel();
        JLabel scoreLabel = new JLabel("Score: " + score + " out of " + totalQuestions);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 30));
        scoreLabel.setForeground(new Color(70, 130, 180)); // Steel blue color
        scorePanel.setBackground(getContentPane().getBackground());
        scorePanel.add(scoreLabel);
        add(scorePanel, BorderLayout.NORTH);

        // Feedback Panel
        JPanel feedbackPanel = new JPanel();
        feedbackPanel.setLayout(new BoxLayout(feedbackPanel, BoxLayout.Y_AXIS));
        feedbackPanel.setBackground(Color.WHITE);
        feedbackPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        for (int i = 0; i < questions.size(); i++) {
            TestPage.Question question = questions.get(i);
            feedbackPanel.add(createQuestionFeedbackPanel(question, i));
        }

        JScrollPane scrollPane = new JScrollPane(feedbackPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Summary Panel
        JPanel summaryPanel = new JPanel();
        summaryPanel.setLayout(new BoxLayout(summaryPanel, BoxLayout.Y_AXIS));
        summaryPanel.setBackground(getContentPane().getBackground());

        JLabel percentageLabel = new JLabel("Percentage: " + getPercentage() + "%");
        percentageLabel.setFont(new Font("Arial", Font.BOLD, 30));
        percentageLabel.setForeground(new Color(34, 139, 34)); // Forest green color
        summaryPanel.add(percentageLabel);

        // Display student info
        JLabel studentInfoLabel = new JLabel("Name: " + name + ", Roll No: " + rollnumber + ", Branch: " + branch);
        studentInfoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        summaryPanel.add(studentInfoLabel);

        JButton exportButton = new JButton("Export Results");
        exportButton.setBackground(new Color(100, 149, 237)); // Cornflower blue
        exportButton.setForeground(Color.WHITE);
        exportButton.addActionListener(e -> exportResults());
        summaryPanel.add(exportButton);

        JButton retakeButton = new JButton("Retake Test");
        retakeButton.setBackground(new Color(255, 140, 0)); // Dark orange
        retakeButton.setForeground(Color.WHITE);
        retakeButton.addActionListener(e -> {
            dispose(); // Close current result page
            new StudentLoginPage().setVisible(true); // Launch the login page to retake the test
        });
        summaryPanel.add(retakeButton);

        add(summaryPanel, BorderLayout.SOUTH);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void fetchStudentDetails(String username) {
        String url = "jdbc:mysql://localhost:3306/exam"; // Update with your database URL
        String user = "root"; // Update with your database username
        String password = "mysqldev224"; // Update with your database password

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement("SELECT name, roll_number, branch FROM students WHERE username = ?")) {
            stmt.setString(1, username.trim());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.name = rs.getString("name");
                this.rollnumber = rs.getString("roll_number");
                this.branch = rs.getString("branch");
            } else {
                JOptionPane.showMessageDialog(this, "Student not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching student details: " + e.getMessage());
        }
    }

    private JPanel createQuestionFeedbackPanel(TestPage.Question question, int index) {
        JPanel questionPanel = new JPanel();
        questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
        questionPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        questionPanel.setBackground(index % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
        questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel questionLabel = new JLabel("Q" + (index + 1) + ": " + question.getQuestionText());
        questionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        questionPanel.add(questionLabel);

        char selectedOption = selectedAnswers[index] != -1 ? (char) ('A' + selectedAnswers[index]) : '-';
        String selectedAnswerText = selectedAnswers[index] != -1 ? getOptionText(question, selectedAnswers[index]) : "No answer selected";
        JLabel selectedAnswerLabel = new JLabel("Your Answer: " + selectedOption + " - " + selectedAnswerText);
        selectedAnswerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        selectedAnswerLabel.setForeground(selectedAnswers[index] == -1 ? Color.RED : Color.BLACK);
        questionPanel.add(selectedAnswerLabel);

        String correctAnswerText = getOptionText(question, question.getCorrectOption() - 'A');
        JLabel correctAnswerLabel = new JLabel("Correct Answer: " + question.getCorrectOption() + " - " + correctAnswerText);
        correctAnswerLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        correctAnswerLabel.setForeground(Color.GREEN);
        questionPanel.add(correctAnswerLabel);

        JLabel feedbackLabel = new JLabel();
        feedbackLabel.setFont(new Font("Arial", Font.BOLD, 16));
        if (selectedAnswers[index] != -1 && question.getCorrectOption() == selectedOption) {
            feedbackLabel.setText("Correct!");
            feedbackLabel.setForeground(Color.GREEN);
        } else {
            feedbackLabel.setText("Incorrect.");
            feedbackLabel.setForeground(Color.RED);
        }
        questionPanel.add(feedbackLabel);

        return questionPanel;
    }

    private String getOptionText(TestPage.Question question, int optionIndex) {
        switch (optionIndex) {
            case 0: return question.getOptionA();
            case 1: return question.getOptionB();
            case 2: return question.getOptionC();
            case 3: return question.getOptionD();
            default: return "";
        }
    }

    private double getPercentage() {
        return ((double) score / totalQuestions) * 100;
    }

    private void exportResults() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File("test_results.pdf")); // Default file name

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return; // User canceled the dialog
        }

        File fileToSave = fileChooser.getSelectedFile();

        // Check if the file already exists
        if (fileToSave.exists()) {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "File already exists. Do you want to overwrite it?", "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION);
            if (confirm != JOptionPane.YES_OPTION) {
                return; // User chose not to overwrite
            }
        }

        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
//            contentStream.setFont(PDType1Font.COURIER, 12); // Change font here
            contentStream.beginText();
            contentStream.newLineAtOffset(25, 750);
            contentStream.showText("Test Results");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Name: " + name);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Roll No: " + rollnumber);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Branch: " + branch);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Score: " + score + " out of " + totalQuestions);
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Percentage: " + getPercentage() + "%");
            contentStream.newLineAtOffset(0, -20);
            contentStream.showText("Details: ");
            contentStream.newLineAtOffset(0, -20);

            for (int i = 0; i < questions.size(); i++) {
                TestPage.Question question = questions.get(i);
                contentStream.showText("Q" + (i + 1) + ": " + question.getQuestionText());
                char selectedOption = selectedAnswers[i] != -1 ? (char) ('A' + selectedAnswers[i]) : '-';
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Your Answer: " + selectedOption);
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText("Correct Answer: " + question.getCorrectOption());
                contentStream.newLineAtOffset(0, -20);
                contentStream.showText(""); // Add an empty line for better readability
            }

            contentStream.endText();
            contentStream.close();
            document.save(fileToSave);
            JOptionPane.showMessageDialog(this, "Results exported successfully to " + fileToSave.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error exporting results: " + e.getMessage());
        }
    }

    public static void main(String[] args) {

    }
}
