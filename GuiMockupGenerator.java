import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/** Generates a portable visual mockup of the Swing interface for the project deliverables. */
public class GuiMockupGenerator {
    public static void main(String[] args) throws Exception {
        BufferedImage image = new BufferedImage(900, 700, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setColor(new Color(21, 27, 39)); g.fillRect(0, 0, 900, 700);
        g.setFont(new Font("SansSerif", Font.BOLD, 28)); g.setColor(Color.WHITE); g.drawString("AI Chatbot Assistant", 42, 58);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16)); g.setColor(new Color(148, 163, 184)); g.drawString("NLP-powered student support", 620, 56);
        g.setColor(new Color(31, 41, 55)); g.fillRoundRect(35, 88, 830, 480, 16, 16);
        bubble(g, 57, 115, 575, 72, new Color(55, 65, 81), "Bot: Hello! I'm your AI Chatbot Assistant.", "Ask me about college life, or simply say hi.");
        bubble(g, 337, 212, 485, 52, new Color(37, 99, 235), "You: What are the library timings?", "");
        bubble(g, 57, 289, 695, 92, new Color(55, 65, 81), "Bot: The library is open from 8:00 AM to 8:00 PM", "on weekdays and 9:00 AM to 4:00 PM on Saturdays.   Confidence: 89%");
        bubble(g, 337, 406, 330, 52, new Color(37, 99, 235), "You: What is the weather today?", "");
        bubble(g, 57, 483, 680, 52, new Color(55, 65, 81), "Bot: Mock weather update: pleasant and partly cloudy today.", "");
        g.setColor(Color.WHITE); g.fillRoundRect(35, 590, 640, 48, 10, 10);
        g.setFont(new Font("SansSerif", Font.PLAIN, 16)); g.setColor(new Color(100, 116, 139)); g.drawString("Type your message...", 53, 621);
        button(g, 690, 590, 175, 48, new Color(37, 99, 235), "Send");
        button(g, 35, 650, 175, 35, new Color(75, 85, 99), "Clear Chat");
        button(g, 220, 650, 175, 35, new Color(22, 163, 74), "Save Chat");
        g.dispose();
        new File("outputs").mkdirs();
        ImageIO.write(image, "png", new File("outputs/gui-mockup.png"));
    }

    private static void bubble(Graphics2D g, int x, int y, int width, int height, Color color, String first, String second) {
        g.setColor(color); g.fillRoundRect(x, y, width, height, 14, 14);
        g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.PLAIN, 16)); g.drawString(first, x + 15, y + 29);
        if (!second.isBlank()) g.drawString(second, x + 15, y + 53);
    }

    private static void button(Graphics2D g, int x, int y, int width, int height, Color color, String text) {
        g.setColor(color); g.fillRoundRect(x, y, width, height, 9, 9);
        g.setColor(Color.WHITE); g.setFont(new Font("SansSerif", Font.BOLD, 14));
        g.drawString(text, x + (width - g.getFontMetrics().stringWidth(text)) / 2, y + height / 2 + 5);
    }
}
