import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Application entry point. */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // The application can safely continue with Swing's default look and feel.
            }
            new ChatbotGUI(new ChatbotEngine()).setVisible(true);
        });
    }
}
