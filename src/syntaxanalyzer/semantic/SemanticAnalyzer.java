package syntaxanalyzer.semantic;

import syntaxanalyzer.declarations.Cls;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Type checking entry point. For now it only confirms that the AST contains at least one class
 * and returns a placeholder {@link UnknownType} for every declaration.
 */
public final class SemanticAnalyzer {

    public List<Type> analyze(List<Cls> classes) {
        if (classes == null || classes.isEmpty()) {
            throw new SemanticException("Expected at least one class declaration");
        }

        List<Type> results = new ArrayList<>();
        for (Cls cls : classes) {
            Objects.requireNonNull(cls, "Class declaration");
            results.add(UnknownType.INSTANCE);
        }
        return List.copyOf(results);
    }
}
