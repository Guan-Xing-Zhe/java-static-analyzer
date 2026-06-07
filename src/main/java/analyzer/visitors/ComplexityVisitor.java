package analyzer.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

/**
 * 圈复杂度计算器（Cyclomatic Complexity）
 *
 * 圈复杂度 = 1 + 控制流分支数，衡量方法的逻辑复杂程度。
 * 理论来源：Thomas McCabe 于 1976 年提出的软件度量指标。
 *
 * 计数规则：
 * - if/else if/else 每个条件 +1
 * - while/for/foreach 每个循环 +1
 * - && / || 每个逻辑运算符 +1（通过 ConditionalExpr）
 * - switch 每个 case +1
 * - catch 每个异常处理器 +1
 *
 * 业界参考：
 * - 1-10: 简单，低风险
 * - 11-20: 中等，建议关注
 * - 21-50: 复杂，建议重构
 * - 50+: 极复杂，必须重构
 */
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

    /** 遍历 AST 统计所有控制流节点的数量 */
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
