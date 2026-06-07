package analyzer.visitors;

import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 未使用代码检测器（Dead Code Elimination）
 *
 * 程序分析中的无用代码消除基础实现。
 * 策略：收集所有 private 成员（方法/字段），再收集所有被引用的名字，
 * 取差集得到"声明但未使用"的成员。
 *
 * 局限：只在单编译单元内分析，不支持跨文件 / 跨模块分析。
 */
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
