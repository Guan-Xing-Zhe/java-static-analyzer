/**
 * 调用图构建器（Call Graph）
 *
 * 程序分析基础数据结构，描述方法之间的调用关系。
 * 每个方法为图中节点，调用关系为有向边 A -> B（A 调用 B）。
 *
 * 应用场景：
 * - 理解代码结构、定位入口函数
 * - 变更影响分析（修改 A 会影响哪些调用者）
 * - 内联优化（Inline）决策支持
 */
package analyzer.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;

public class CallGraphVisitor extends VoidVisitorAdapter<Void> {

    private final Map<String, List<String>> callGraph = new LinkedHashMap<>();
    private String currentMethod = "";

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        String prevMethod = currentMethod;
        currentMethod = md.getNameAsString();
        callGraph.putIfAbsent(currentMethod, new ArrayList<>());
        super.visit(md, arg);
        currentMethod = prevMethod;
    }

    @Override
    public void visit(MethodCallExpr mce, Void arg) {
        String calledMethod = mce.getNameAsString();
        if (!currentMethod.isEmpty()) {
            callGraph.computeIfAbsent(currentMethod, k -> new ArrayList<>());
            if (!callGraph.get(currentMethod).contains(calledMethod)) {
                callGraph.get(currentMethod).add(calledMethod);
            }
        }
        super.visit(mce, arg);
    }

    public void printCallGraph(String fileName) {
        System.out.println("[Call Graph] Method call relationships in " + fileName + ":");
        if (callGraph.isEmpty()) {
            System.out.println("  (no methods found)");
            return;
        }
        for (Map.Entry<String, List<String>> entry : callGraph.entrySet()) {
            System.out.println("  " + entry.getKey());
            for (String callee : entry.getValue()) {
                System.out.println("    -> " + callee);
            }
        }
    }
}
