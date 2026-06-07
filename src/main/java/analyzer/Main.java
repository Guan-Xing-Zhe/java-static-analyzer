package analyzer;

import analyzer.visitors.*;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Usage: java -jar java-static-analyzer.jar <path-to-java-file-or-directory>");
            System.exit(1);
        }

        Path input = Paths.get(args[0]);
        if (Files.isDirectory(input)) {
            try (Stream<Path> files = Files.walk(input)) {
                files.filter(p -> p.toString().endsWith(".java"))
                     .forEach(Main::analyze);
            }
        } else {
            analyze(input);
        }
    }

    private static void analyze(Path filePath) {
        try {
            CompilationUnit cu = StaticJavaParser.parse(filePath.toFile());

            String fileName = filePath.toString();
            System.out.println("\n========== Analyzing: " + fileName + " ==========");

            // Cyclomatic complexity
            ComplexityVisitor complexityVisitor = new ComplexityVisitor();
            complexityVisitor.visit(cu, null);
            int totalComplexity = complexityVisitor.getTotalComplexity();
            int methodCount = complexityVisitor.getMethodCount();
            System.out.println("[Complexity] Total cyclomatic complexity: " + totalComplexity
                    + " (methods: " + methodCount
                    + ", avg: " + (methodCount > 0 ? String.format("%.1f", (double) totalComplexity / methodCount) : "N/A")
                    + ")");

            // Unused private code detection
            UnusedCodeVisitor unusedVisitor = new UnusedCodeVisitor();
            unusedVisitor.visit(cu, null);
            if (unusedVisitor.hasUnusedMembers()) {
                System.out.println("[Unused Code] Found potential unused members:");
                for (String member : unusedVisitor.getUnusedMembers()) {
                    System.out.println("  - " + member);
                }
            } else {
                System.out.println("[Unused Code] No unused private members detected.");
            }

            // Null check analysis
            NullCheckVisitor nullCheckVisitor = new NullCheckVisitor();
            nullCheckVisitor.visit(cu, null);
            if (nullCheckVisitor.hasIssues()) {
                System.out.println("[Null Safety] Potential null pointer issues:");
                for (String issue : nullCheckVisitor.getIssues()) {
                    System.out.println("  - " + issue);
                }
            } else {
                System.out.println("[Null Safety] No obvious null safety issues detected.");
            }

            // Call graph
            CallGraphVisitor callGraphVisitor = new CallGraphVisitor();
            callGraphVisitor.visit(cu, null);
                        callGraphVisitor.printCallGraph(fileName);

            // AI-enhanced code review
            analyzer.ai.AIReviewEnhancer aiEnhancer = new analyzer.ai.AIReviewEnhancer();
            String aiReview = aiEnhancer.review(
                fileName, totalComplexity, methodCount,
                unusedVisitor.getUnusedMembers(),
                nullCheckVisitor.getIssues(),
                "Total methods: " + methodCount
            );
            System.out.println(aiReview);

        } catch (IOException e) {
            System.err.println("Error reading file: " + filePath + " - " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error parsing file: " + filePath + " - " + e.getMessage());
        }
    }
}
