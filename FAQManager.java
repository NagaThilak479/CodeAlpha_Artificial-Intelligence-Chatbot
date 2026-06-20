import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Loads and stores FAQ records from the bundled JSON file. */
public class FAQManager {
    public static class FAQ {
        private final String question;
        private final String answer;

        public FAQ(String question, String answer) {
            this.question = question;
            this.answer = answer;
        }
        public String getQuestion() { return question; }
        public String getAnswer() { return answer; }
    }

    private final List<FAQ> faqs = new ArrayList<>();
    // Dataset strings intentionally avoid embedded quotes; this small parser keeps the project library-free.
    private static final Pattern FAQ_PATTERN = Pattern.compile(
        "\\{\\s*\\\"question\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"\\s*,\\s*\\\"answer\\\"\\s*:\\s*\\\"([^\\\"]*)\\\"\\s*}");

    public FAQManager(String datasetPath) {
        load(datasetPath);
    }

    public void load(String datasetPath) {
        faqs.clear();
        try {
            String json = Files.readString(Path.of(datasetPath), StandardCharsets.UTF_8);
            Matcher matcher = FAQ_PATTERN.matcher(json);
            while (matcher.find()) faqs.add(new FAQ(matcher.group(1), matcher.group(2)));
            if (faqs.isEmpty()) throw new IOException("No valid FAQ entries found.");
        } catch (IOException exception) {
            System.err.println("Unable to load FAQ dataset: " + exception.getMessage());
            addFallbackFAQs();
        }
    }

    public List<FAQ> getFaqs() { return Collections.unmodifiableList(faqs); }

    private void addFallbackFAQs() {
        faqs.add(new FAQ("What are library timings?", "The library is open from 8:00 AM to 8:00 PM on weekdays."));
        faqs.add(new FAQ("How do I contact admissions?", "You can contact the admissions office by email or visit the administration building."));
    }
}
