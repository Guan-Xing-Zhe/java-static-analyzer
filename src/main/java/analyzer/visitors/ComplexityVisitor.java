package analyzer.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

public class ComplexityVisitor extends VoidVisitorAdapter<Void> {

    private int totalComplexity = 0;
    private int methodCount = 0;

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        int methodComplexity = calculateCyclomaticComplexity(md);
        totalComplexity += methodComplexity;
        methodCount++;
        String name = md.getNameAsString();
        int line = md.getBegin().map(p -> p.line).orElse(0);
        System.out.println("  [Complexity] Method '" + name + "' (line " + line + "): complexity = " + methodComplexity);
        super.visit(md, arg);
    }

    private int calculateCyclomaticComplexity(MethodDeclaration md) {
        int complexity = 1;
        complexity += md.findAll(IfStmt.class).size();
        complexity += md.findAll(WhileStmt.class).size();
        complexity += md.findAll(ForStmt.class).size();
        complexity += md.findAll(ForEachStmt.class).size();
        complexity += md.findAll(ConditionalExpr.class).size();
        for (SwitchStmt switchStmt : md.findAll(SwitchStmt.class)) {
            complexity += switchStmt.getEntries().size();
        }
        for (TryStmt tryStmt : md.findAll(TryStmt.class)) {
            complexity += tryStmt.getCatchClauses().size();
        }
        return complexity;
    }

    public int getTotalComplexity() { return totalComplexity; }
    public int getMethodCount() { return methodCount; }
}
