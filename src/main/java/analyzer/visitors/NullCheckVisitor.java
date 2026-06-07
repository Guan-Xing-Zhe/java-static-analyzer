package analyzer.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 空指针风险检测器（Null Safety Analysis）
 *
 * 简单数据流分析：标记方法参数为"可能为空"，
 * 检测对这些参数进行方法调用时的空指针风险。
 *
 * 这是污点传播分析（Taint Analysis）的简化实现：
 * - source: 方法参数（不可信输入）
 * - sink: 方法调用（可能触发 NPE）
 *
 * 注：生产级别需要更完善的流敏感、上下文敏感分析。
 */
public class NullCheckVisitor extends VoidVisitorAdapter<Void> {

    private final List<String> issues = new ArrayList<>();
    private final List<String> nullableOrParams = new ArrayList<>();

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        nullableOrParams.clear();
        md.getParameters().forEach(p -> nullableOrParams.add(p.getNameAsString()));
        super.visit(md, arg);
    }

    @Override
    public void visit(MethodCallExpr mce, Void arg) {
        mce.getScope().ifPresent(scope -> {
            String scopeName = scope.toString();
            boolean isNullable = nullableOrParams.stream().anyMatch(scopeName::contains)
                    || scopeName.startsWith("param")
                    || scopeName.equals("obj")
                    || scopeName.equals("value");
            if (isNullable) {
                int line = mce.getBegin().map(p -> p.line).orElse(0);
                issues.add("Line " + line + ": Possible NPE on method call '" + mce.getNameAsString()
                        + "' from potentially null reference: " + scopeName);
            }
        });
        super.visit(mce, arg);
    }

    public boolean hasIssues() { return !issues.isEmpty(); }
    public List<String> getIssues() { return issues; }
}
