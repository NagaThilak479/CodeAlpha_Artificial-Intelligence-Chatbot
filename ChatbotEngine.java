import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Combines conversation rules, context, and TF-IDF cosine similarity FAQ search. */
public class ChatbotEngine {
    public static class BotReply {
        private final String text;
        private final double confidence;
        private final boolean faqMatch;
        public BotReply(String text, double confidence, boolean faqMatch) {
            this.text = text; this.confidence = confidence; this.faqMatch = faqMatch;
        }
        public String getText() { return text; }
        public double getConfidence() { return confidence; }
        public boolean isFaqMatch() { return faqMatch; }
    }

    private final NLPProcessor nlp = new NLPProcessor();
    private final FAQManager faqManager = new FAQManager("data/FAQDataset.json");
    private final Map<String, Double> idf = new HashMap<>();
    private final List<Map<String, Double>> faqVectors = new ArrayList<>();
    private String userName = "";
    private String lastTopic = "";
    private static final double MATCH_THRESHOLD = 0.18;

    public ChatbotEngine() { buildKnowledgeIndex(); }

    /** Processes one message, retaining a small amount of useful conversation context. */
    public BotReply replyTo(String message) {
        if (message == null || message.isBlank()) return new BotReply("Please type a message and I will do my best to help.", 0, false);
        String lower = message.toLowerCase(Locale.ROOT).trim();
        BotReply ruleReply = handleRules(lower);
        if (ruleReply != null) return ruleReply;
        BotReply faqReply = findBestFAQ(message);
        if (faqReply.confidence >= MATCH_THRESHOLD) {
            lastTopic = nlp.extractKeywords(message);
            return faqReply;
        }
        String context = lastTopic.isBlank() ? "" : " We were last discussing " + lastTopic + ".";
        return new BotReply("I am not fully sure about that yet." + context +
            " Try asking about admissions, courses, fees, library, exams, hostel, scholarships, or campus services.", faqReply.confidence, false);
    }

    private BotReply handleRules(String lower) {
        Matcher nameMatcher = Pattern.compile("(?:my name is|i am|i'm)\\s+([a-z]{2,})", Pattern.CASE_INSENSITIVE).matcher(lower);
        if (nameMatcher.find()) {
            userName = capitalize(nameMatcher.group(1));
            return new BotReply("Nice to meet you, " + userName + "! What would you like to know?", 1, false);
        }
        if (lower.matches(".*\\b(hello|hi|hey|good morning|good evening)\\b.*"))
            return new BotReply("Hello" + (userName.isBlank() ? "" : ", " + userName) + "! I am your AI Chatbot Assistant. How can I help today?", 1, false);
        if (lower.matches(".*\\b(who are you|your name|introduce yourself)\\b.*"))
            return new BotReply("I am an AI Chatbot Assistant for student and college questions. I can also chat about the date, time, and mock weather.", 1, false);
        if (lower.matches(".*\\b(time|current time)\\b.*"))
            return new BotReply("The current local time is " + LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm a")) + ".", 1, false);
        if (lower.matches(".*\\b(date|today's date|day)\\b.*"))
            return new BotReply("Today is " + LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")) + ".", 1, false);
        if (lower.contains("weather") || lower.contains("rain") || lower.contains("temperature"))
            return new BotReply("Mock weather update: it is pleasant and partly cloudy today. A light jacket should do nicely.", 1, false);
        if (lower.matches(".*\\b(thanks|thank you|thankyou)\\b.*"))
            return new BotReply("You are very welcome" + (userName.isBlank() ? "!" : ", " + userName + "!") , 1, false);
        if (lower.matches(".*\\b(bye|goodbye|see you)\\b.*"))
            return new BotReply("Goodbye" + (userName.isBlank() ? "!" : ", " + userName + "!") + " Have a great day.", 1, false);
        return null;
    }

    private void buildKnowledgeIndex() {
        Map<String, Integer> documentFrequency = new HashMap<>();
        for (FAQManager.FAQ faq : faqManager.getFaqs()) {
            Set<String> unique = new HashSet<>(nlp.preprocess(faq.getQuestion()));
            for (String word : unique) documentFrequency.merge(word, 1, Integer::sum);
        }
        int count = faqManager.getFaqs().size();
        for (Map.Entry<String, Integer> entry : documentFrequency.entrySet())
            idf.put(entry.getKey(), Math.log((count + 1.0) / (entry.getValue() + 1.0)) + 1.0);
        for (FAQManager.FAQ faq : faqManager.getFaqs()) faqVectors.add(toVector(nlp.preprocess(faq.getQuestion())));
    }

    private BotReply findBestFAQ(String message) {
        Map<String, Double> userVector = toVector(nlp.preprocess(message));
        double bestScore = 0; int bestIndex = 0;
        for (int i = 0; i < faqVectors.size(); i++) {
            double score = cosineSimilarity(userVector, faqVectors.get(i));
            if (score > bestScore) { bestScore = score; bestIndex = i; }
        }
        return new BotReply(faqManager.getFaqs().get(bestIndex).getAnswer(), bestScore, true);
    }

    private Map<String, Double> toVector(List<String> words) {
        Map<String, Double> vector = new HashMap<>();
        if (words.isEmpty()) return vector;
        for (String word : words) vector.merge(word, 1.0, Double::sum);
        for (String word : new ArrayList<>(vector.keySet())) vector.put(word, (vector.get(word) / words.size()) * idf.getOrDefault(word, 0.0));
        return vector;
    }

    private double cosineSimilarity(Map<String, Double> a, Map<String, Double> b) {
        double dot = 0, aMagnitude = 0, bMagnitude = 0;
        for (Map.Entry<String, Double> entry : a.entrySet()) dot += entry.getValue() * b.getOrDefault(entry.getKey(), 0.0);
        for (double value : a.values()) aMagnitude += value * value;
        for (double value : b.values()) bMagnitude += value * value;
        return (aMagnitude == 0 || bMagnitude == 0) ? 0 : dot / (Math.sqrt(aMagnitude) * Math.sqrt(bMagnitude));
    }

    private String capitalize(String value) { return value.substring(0, 1).toUpperCase() + value.substring(1); }
}
