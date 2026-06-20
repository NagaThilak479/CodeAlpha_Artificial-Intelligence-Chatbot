import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Performs lightweight, dependency-free text preprocessing. */
public class NLPProcessor {
    private final Set<String> stopWords = new HashSet<>(Arrays.asList(
        "a", "an", "the", "is", "are", "am", "was", "were", "to", "of", "in", "on", "at",
        "for", "from", "with", "and", "or", "but", "i", "me", "my", "you", "your", "we", "our",
        "it", "this", "that", "these", "those", "can", "could", "would", "please", "tell", "about",
        "what", "when", "where", "how", "do", "does", "did", "be", "have", "has", "had", "want"
    ));

    /** Converts text to lowercase, tokenizes it, then removes stop words. */
    public List<String> preprocess(String text) {
        List<String> keywords = new ArrayList<>();
        if (text == null) return keywords;
        String cleaned = text.toLowerCase().replaceAll("[^a-z0-9\\s]", " ").trim();
        for (String token : cleaned.split("\\s+")) {
            if (!token.isBlank() && !stopWords.contains(token)) keywords.add(token);
        }
        return keywords;
    }

    /** Returns meaningful words as a human-readable keyword string. */
    public String extractKeywords(String text) {
        return String.join(", ", preprocess(text));
    }
}
