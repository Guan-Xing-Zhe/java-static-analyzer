# Java Static Analyzer

A static analysis tool for Java source code that detects code quality issues using program analysis techniques.

Built on top of [JavaParser](https://github.com/javaparser/javaparser), demonstrating practical application of AST traversal, control flow analysis, and code metrics computation.

## Analysis Capabilities

| Analysis | Description |
|---|---|
| **Cyclomatic Complexity** | Measures method complexity based on control flow structures (if, while, for, switch, catch) |
| **Unused Code Detection** | Identifies private methods and fields that are never referenced within the compilation unit |
| **Null Safety Warnings** | Flags potential null pointer dereferences on method parameters |
| **Call Graph Construction** | Builds a directed call graph showing method invocation relationships |

## Build & Run

```bash
# Build with Maven
mvn clean package -q

# Analyze a single file
java -jar target/java-static-analyzer-1.0.0.jar src/main/java/analyzer/Main.java

# Analyze an entire project
java -jar target/java-static-analyzer-1.0.0.jar /path/to/project/src
```

## Sample Output

Running the analyzer on its own `Main.java`:

```
========== Analyzing: src/main/java/analyzer/Main.java ==========
  [Complexity] Method 'main' (line 16): complexity = 3
  [Complexity] Method 'analyze' (line 33): complexity = 8
[Complexity] Total cyclomatic complexity: 11 (methods: 2, avg: 5.5)
[Unused Code] No unused private members detected.
[Null Safety] Potential null pointer issues:
  - Line 35: Possible NPE on method call 'toFile' from possibly null reference: filePath
[Call Graph] Method call relationships in src/main/java/analyzer/Main.java:
  main
    -> println
    -> exit
    -> get
```

## Project Structure

```
src/main/java/analyzer/
├── Main.java                       # CLI entry point
├── visitors/
│   ├── ComplexityVisitor.java      # Cyclomatic complexity calculator
│   ├── UnusedCodeVisitor.java      # Unused private member detector
│   ├── NullCheckVisitor.java       # Null dereference warning
│   └── CallGraphVisitor.java       # Call graph builder
```
