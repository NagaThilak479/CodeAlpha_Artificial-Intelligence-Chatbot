# CodeAlpha_Artificial-Intelligence-Chatbot
# AI Chatbot Assistant

A beginner-friendly Java Swing chatbot for college and student questions. It combines conversational rules with a small FAQ knowledge base and TF-IDF cosine-similarity matching. No third-party libraries are required.

## Features

- Modern dark Swing chat UI with distinct user/bot bubbles, scrolling history, Clear Chat, and Save Chat.
- Rule-based conversation for greetings, introductions, date/time, mock weather, thanks, and farewells.
- NLP preprocessing: lowercasing, tokenization, stop-word removal, and keyword extraction.
- FAQ search over 55 JSON question/answer pairs using TF-IDF vectors and cosine similarity.
- Confidence shown on matched answers; a graceful fallback when the confidence is low.
- Small context memory (the user's name and last topic).

## Project layout

```
src/
  Main.java
  ChatbotGUI.java
  ChatbotEngine.java
  NLPProcessor.java
  FAQManager.java
data/FAQDataset.json
```

## Run in VS Code

1. Install a JDK (17+ recommended) and the **Extension Pack for Java**.
2. Open this folder in VS Code.
3. Run `Main.java` using the `Run` link above `main`, or use the terminal commands below.

## Run in IntelliJ IDEA

1. Choose **Open** and select this folder.
2. Set the Project SDK to JDK 17 or newer.
3. Mark `src` as a Sources Root if IntelliJ has not done so.
4. Run the `Main` class. Keep the working directory set to the project root so `data/FAQDataset.json` can load.

## Terminal commands (PowerShell)

```powershell
javac -d out src\*.java
java -cp out Main
```

## How the NLP works

`NLPProcessor` changes text to lowercase, splits it into word tokens, removes common stop words (for example, `the`, `is`, and `can`), and keeps the remaining meaningful words as keywords. This makes phrases such as “Can you tell me the library hours?” and “library opening time” more comparable.

## Similarity / ML-style matching

At startup, `FAQManager` reads the JSON dataset. For every FAQ question it builds a TF-IDF vector: term frequency says how important a word is within one question, while inverse document frequency reduces the influence of words common across many questions. The engine creates the same vector for the user's message and uses cosine similarity to find the closest FAQ. A confidence score between 0% and 100% is displayed. Rules get first priority for conversational messages; FAQ matching handles knowledge questions.

## Sample conversation

```
Bot: Hello! I'm your AI Chatbot Assistant. How can I help today?
You: Hi, I'm Priya
Bot: Nice to meet you, Priya! What would you like to know?
You: What are the library timings?
Bot: The library is open from 8:00 AM to 8:00 PM on weekdays and 9:00 AM to 4:00 PM on Saturdays. (Confidence: 91%)
You: What is the weather today?
Bot: Mock weather update: it is pleasant and partly cloudy today. A light jacket should do nicely.
```

`outputs/ai-chatbot-gui-mockup.png` is a visual mockup of the application interface.
