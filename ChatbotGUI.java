import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.JTextPane;

/** Swing presentation layer. It delegates all chat decisions to ChatbotEngine. */
public class ChatbotGUI extends JFrame {
    private final ChatbotEngine engine;
    private final JTextPane conversation = new JTextPane();
    private final JTextField input = new JTextField();
    private final Color background = new Color(21, 27, 39);
    private final Color panel = new Color(31, 41, 55);
    private final Color userColor = new Color(37, 99, 235);
    private final Color botColor = new Color(55, 65, 81);

    public ChatbotGUI(ChatbotEngine engine) {
        this.engine = engine;
        setTitle("AI Chatbot Assistant");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 650);
        setMinimumSize(new Dimension(560, 460));
        setLocationRelativeTo(null);
        buildInterface();
        addMessage("Bot", "Hello! I'm your AI Chatbot Assistant. Ask me about college life, or simply say hi.", false);
    }

    private void buildInterface() {
        getContentPane().setBackground(background);
        setLayout(new BorderLayout(12, 12));
        ((JPanel) getContentPane()).setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(background);
        JLabel title = new JLabel("AI Chatbot Assistant");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("NLP-powered student support", SwingConstants.RIGHT);
        subtitle.setForeground(new Color(148, 163, 184));
        header.add(title, BorderLayout.WEST); header.add(subtitle, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        conversation.setEditable(false);
        conversation.setBackground(panel);
        conversation.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
        add(new JScrollPane(conversation), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        bottom.setBackground(background);
        input.setFont(new Font("SansSerif", Font.PLAIN, 15));
        input.setBackground(Color.WHITE);
        input.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        input.addActionListener(this::sendMessage);
        JButton send = createButton("Send", userColor);
        send.addActionListener(this::sendMessage);
        JPanel actions = new JPanel(new GridLayout(1, 2, 6, 0));
        actions.setBackground(background);
        JButton clear = createButton("Clear Chat", new Color(75, 85, 99));
        clear.addActionListener(e -> conversation.setText(""));
        JButton save = createButton("Save Chat", new Color(22, 163, 74));
        save.addActionListener(e -> saveHistory());
        actions.add(clear); actions.add(save);
        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setBackground(background); inputRow.add(input, BorderLayout.CENTER); inputRow.add(send, BorderLayout.EAST);
        bottom.add(inputRow, BorderLayout.NORTH); bottom.add(actions, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);
    }

    private JButton createButton(String label, Color color) {
        JButton button = new JButton(label);
        button.setFocusPainted(false); button.setForeground(Color.WHITE); button.setBackground(color);
        button.setFont(new Font("SansSerif", Font.BOLD, 13));
        return button;
    }

    private void sendMessage(ActionEvent ignored) {
        String message = input.getText().trim();
        if (message.isEmpty()) return;
        addMessage("You", message, true);
        input.setText("");
        ChatbotEngine.BotReply response = engine.replyTo(message);
        String suffix = response.isFaqMatch() ? String.format("%nConfidence: %.0f%%", response.getConfidence() * 100) : "";
        addMessage("Bot", response.getText() + suffix, false);
    }

    /** Adds a clearly styled message to the scrolling transcript. */
    private void addMessage(String sender, String message, boolean isUser) {
        StyledDocument doc = conversation.getStyledDocument();
        SimpleAttributeSet style = new SimpleAttributeSet();
        StyleConstants.setForeground(style, Color.WHITE);
        StyleConstants.setBackground(style, isUser ? userColor : botColor);
        StyleConstants.setFontFamily(style, "SansSerif");
        StyleConstants.setFontSize(style, 15);
        StyleConstants.setBold(style, false);
        try {
            doc.insertString(doc.getLength(), sender + ": " + message + "\n\n", style);
            conversation.setCaretPosition(doc.getLength());
        } catch (BadLocationException exception) {
            System.err.println("Could not add chat message: " + exception.getMessage());
        }
    }

    /** Saves transcript in the project folder with a timestamped name. */
    private void saveHistory() {
        try {
            Path folder = Path.of("chat-history");
            Files.createDirectories(folder);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            Files.writeString(folder.resolve("chat-" + timestamp + ".txt"), conversation.getText(), StandardCharsets.UTF_8);
            addMessage("Bot", "Chat history saved in the chat-history folder.", false);
        } catch (IOException exception) {
            addMessage("Bot", "Sorry, I could not save the chat history: " + exception.getMessage(), false);
        }
    }
}
