# Java Static Analyzer

A static analysis tool for Java source code that detects code quality issues using program analysis techniques.

Built on top of [JavaParser](https://github.com/javaparser/javaparser), demonstrating practical application of AST traversal, control flow analysis, and code metrics computation.

基于 JavaParser 的 Java 静态分析工具，演示程序分析中的抽象语法树遍历、控制流复杂度度量、
未使用代码检测、空指针安全警告及调用图构建等关键技术。

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

```
========== Analyzing: src/example/Calculator.java ==========
  [Complexity] Method 'add' (line 5): complexity = 1
  [Complexity] Method 'evaluate' (line 10): complexity = 7
[Complexity] Total cyclomatic complexity: 8 (methods: 2, avg: 4.0)
[Unused Code] No unused private members detected.
[Null Safety] Potential null pointer issues:
  - Line 12: Possible NPE on method call 'equals' from possibly null reference: input
[Call Graph] Method call relationships:
  evaluate
    -> add
    -> subtract
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