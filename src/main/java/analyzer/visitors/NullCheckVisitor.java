package analyzer.visitors;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.ArrayList;
import java.util.List;

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
