package analyzer.visitors;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;
import java.util.stream.Collectors;

public class UnusedCodeVisitor extends VoidVisitorAdapter<Void> {

    private final Set<String> privateMembers = new LinkedHashSet<>();
    private final Set<String> usedNames = new HashSet<>();
    private final List<String> unusedMembers = new ArrayList<>();

    @Override
    public void visit(FieldDeclaration fd, Void arg) {
        if (fd.isPrivate()) {
            fd.getVariables().forEach(v ->
                privateMembers.add(v.getNameAsString()));
        }
        super.visit(fd, arg);
    }

    @Override
    public void visit(MethodDeclaration md, Void arg) {
        if (md.isPrivate()) {
            privateMembers.add(md.getNameAsString());
        }
        super.visit(md, arg);
    }

    @Override
    public void visit(NameExpr ne, Void arg) {
        usedNames.add(ne.getNameAsString());
        super.visit(ne, arg);
    }

    @Override
    public void visit(MethodCallExpr mce, Void arg) {
        // Unscoped calls such as `analyze(input)` are MethodCallExpr, not NameExpr,
        // so the method name has to be recorded here as well.
        usedNames.add(mce.getNameAsString());
        super.visit(mce, arg);
    }

    public boolean hasUnusedMembers() {
        unusedMembers.clear();
        for (String member : privateMembers) {
            if (!usedNames.contains(member)) {
                unusedMembers.add(member);
            }
        }
        return !unusedMembers.isEmpty();
    }

    public List<String> getUnusedMembers() {
        return unusedMembers;
    }
}
