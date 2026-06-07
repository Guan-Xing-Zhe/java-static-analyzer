package analyzer.ai;

import java.util.List;

public class AIReviewEnhancer {

    private final String apiKey;
    private final boolean realMode;

    public AIReviewEnhancer() {
        this.apiKey = System.getenv("OPENAI_API_KEY");
        this.realMode = (apiKey != null && !apiKey.isEmpty());
    }

    public String review(String fileName, int complexity, int methodCount,
                         List<String> unusedMembers, List<String> nullIssues,
                         String summary) {
        if (realMode) {
            return callLLM(fileName, complexity, methodCount, unusedMembers, nullIssues, summary);
        }
        return mockReview(fileName, complexity, methodCount, unusedMembers, nullIssues, summary);
    }

    private String mockReview(String fileName, int complexity, int methodCount,
                               List<String> unused, List<String> nullIssues,
                               String summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("--- [AI Enhanced Review] ---").append("\n");

        double avg = methodCount > 0 ? (double) complexity / methodCount : 0;
        if (avg > 5) {
            sb.append("- High complexity (avg ").append(String.format("%.1f", avg))
              .append(" per method). Consider refactoring.\n");
        } else {
            sb.append("- Complexity is well-managed (avg ").append(String.format("%.1f", avg))
              .append(" per method).\n");
        }

        if (!unused.isEmpty()) {
            sb.append("- Unused private members: ").append(unused.size())
              .append(". Removing dead code reduces maintenance overhead.\n");
        }

        if (!nullIssues.isEmpty()) {
            sb.append("- Null safety alerts: ").append(nullIssues.size()).append("\n");
            for (String issue : nullIssues) {
                sb.append("  * ").append(issue).append("\n");
            }
        }

        if (summary != null && !summary.isEmpty()) {
            sb.append("- ").append(summary).append("\n");
        }

        sb.append("--- End of AI Review ---");
        return sb.toString();
    }

    private String callLLM(String fileName, int complexity, int methodCount,
                            List<String> unused, List<String> nullIssues,
                            String summary) {
        try {
            String prompt = buildPrompt(fileName, complexity, methodCount, unused, nullIssues, summary);
            return callOpenAI(prompt);
        } catch (Exception e) {
            return "[AI Review Error] " + e.getMessage();
        }
    }

    private String buildPrompt(String fileName, int complexity, int methodCount,
                                List<String> unused, List<String> nullIssues,
                                String summary) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a senior Java code reviewer. Analyze these static analysis results ")
          .append("and provide concise, actionable improvement suggestions.\n\n");
        sb.append("File: ").append(fileName).append("\n");
        sb.append("- Cyclomatic complexity: ").append(complexity)
          .append(" (methods: ").append(methodCount).append(")\n");
        if (!unused.isEmpty()) {
            sb.append("- Unused private members: ").append(String.join(", ", unused)).append("\n");
        }
        if (!nullIssues.isEmpty()) {
            sb.append("- Null safety issues: ").append(String.join("; ", nullIssues)).append("\n");
        }
        if (summary != null && !summary.isEmpty()) {
            sb.append("- ").append(summary).append("\n");
        }
        sb.append("\nProvide specific code improvement suggestions.");
        return sb.toString();
    }

    private String callOpenAI(String prompt) throws Exception {
        java.net.URL url = new java.net.URL("https://api.openai.com/v1/chat/completions");
        java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + apiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        String escaped = prompt.replace("\\", "\\\\")
                               .replace("\"", "\\\"")
                               .replace("\n", "\\n")
                               .replace("\t", "\\t");
        String body = "{\"model\":\"gpt-3.5-turbo\",\"messages\":[{\"role\":\"user\",\"content\":\""
                    + escaped + "\"}],\"temperature\":0.3}";

        conn.getOutputStream().write(body.getBytes("utf-8"));

        java.io.BufferedReader reader = new java.io.BufferedReader(
                new java.io.InputStreamReader(conn.getInputStream(), "utf-8"));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        String resp = response.toString();
        int start = resp.indexOf("\"content\":\"");
        if (start > 0) {
            start += 11;
            int end = resp.indexOf("\"", start);
            return resp.substring(start, end).replace("\\n", "\n");
        }
        return resp;
    }
}
