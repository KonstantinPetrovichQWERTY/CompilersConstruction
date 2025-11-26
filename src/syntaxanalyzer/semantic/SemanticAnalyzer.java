package syntaxanalyzer.semantic;

import syntaxanalyzer.declarations.Assignment;
import syntaxanalyzer.declarations.Block;
import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.declarations.ClsBody;
import syntaxanalyzer.declarations.Constructor;
import syntaxanalyzer.declarations.Declaration;
import syntaxanalyzer.declarations.Expression;
import syntaxanalyzer.declarations.IfStatement;
import syntaxanalyzer.declarations.Method;
import syntaxanalyzer.declarations.Primary;
import syntaxanalyzer.declarations.ReturnStatement;
import syntaxanalyzer.declarations.Variable;
import syntaxanalyzer.declarations.WhileStatement;
import syntaxanalyzer.utils.ExpressionSuffix;
import syntaxanalyzer.utils.ParameterDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Performs a minimal semantic pass:
 * <ul>
 *     <li>detects usage of undeclared variables,</li>
 *     <li>evaluates constant conditions to skip unreachable branches.</li>
 * </ul>
 */
public final class SemanticAnalyzer {

    private static final Set<String> BUILTIN_CLASSES = Set.of("Integer", "Real", "Boolean", "String", "Character", "List");

    public List<Type> analyze(List<Cls> classes) {
        if (classes == null || classes.isEmpty()) {
            throw new SemanticException("Expected at least one class declaration");
        }

        Set<String> classNames = collectClassNames(classes);
        for (Cls cls : classes) {
            analyzeClass(cls, classNames);
        }

        List<Type> results = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            results.add(UnknownType.INSTANCE);
        }
        return List.copyOf(results);
    }

    private Set<String> collectClassNames(List<Cls> classes) {
        Set<String> names = new HashSet<>(BUILTIN_CLASSES);
        for (Cls cls : classes) {
            Objects.requireNonNull(cls, "Class declaration");
            if (cls.getName() != null && !cls.getName().isBlank()) {
                names.add(cls.getName());
            }
        }
        return names;
    }

    private void analyzeClass(Cls cls, Set<String> classNames) {
        ClsBody body = cls.getBody();
        if (body == null) {
            throw new SemanticException("Class " + cls.getName() + " has no body");
        }

        Set<String> fields = body.getVariables().stream()
                .map(Variable::getName)
                .collect(Collectors.toCollection(HashSet::new));
        Scope fieldScope = new Scope(classNames, fields);

        for (Variable field : body.getVariables()) {
            analyzeExpression(field.getExpression(), fieldScope);
        }
        for (Constructor constructor : body.getConstructors()) {
            Scope constructorScope = fieldScope.fork();
            declareParameters(constructorScope, constructor.getParameters());
            analyzeBlock(constructor.getBody(), constructorScope);
        }
        for (Method method : body.getMethods()) {
            Scope methodScope = fieldScope.fork();
            declareParameters(methodScope, method.getParameters());
            analyzeBlock(method.getBody(), methodScope);
        }
    }

    private void declareParameters(Scope scope, List<ParameterDeclaration> params) {
        if (params == null) {
            return;
        }
        for (ParameterDeclaration param : params) {
            scope.declare(param.name());
        }
    }

    private void analyzeBlock(Block block, Scope scope) {
        if (block == null) {
            return;
        }
        for (Declaration declaration : block.getParts()) {
            if (declaration instanceof Variable variable) {
                analyzeExpression(variable.getExpression(), scope);
                scope.declare(variable.getName());
            } else if (declaration instanceof Assignment assignment) {
                scope.requireDeclared(assignment.getName(), assignment.getNameToken());
                analyzeExpression(assignment.getExpression(), scope);
            } else if (declaration instanceof Expression expression) {
                analyzeExpression(expression, scope);
            } else if (declaration instanceof IfStatement ifStatement) {
                analyzeIf(ifStatement, scope);
            } else if (declaration instanceof WhileStatement whileStatement) {
                analyzeWhile(whileStatement, scope);
            } else if (declaration instanceof ReturnStatement returnStatement) {
                if (returnStatement.getValue() != null) {
                    analyzeExpression(returnStatement.getValue(), scope);
                }
            }
        }
    }

    private void analyzeIf(IfStatement statement, Scope scope) {
        analyzeExpression(statement.getCondition(), scope);
        Optional<Boolean> constantCondition = evaluateBoolean(statement.getCondition(), scope.getClassNames());

        if (constantCondition.isPresent()) {
            if (constantCondition.get()) {
                Scope trueScope = scope.fork();
                analyzeBlock(statement.getTrueBlock(), trueScope);
                scope.absorb(trueScope);
            } else if (statement.getFalseBlock() != null) {
                Scope falseScope = scope.fork();
                analyzeBlock(statement.getFalseBlock(), falseScope);
                scope.absorb(falseScope);
            }
            return;
        }

        Scope trueScope = scope.fork();
        analyzeBlock(statement.getTrueBlock(), trueScope);

        Scope falseScope = scope.fork();
        analyzeBlock(statement.getFalseBlock(), falseScope);

        scope.absorb(trueScope);
        scope.absorb(falseScope);
    }

    private void analyzeWhile(WhileStatement statement, Scope scope) {
        analyzeExpression(statement.getCondition(), scope);
        Optional<Boolean> constantCondition = evaluateBoolean(statement.getCondition(), scope.getClassNames());
        if (constantCondition.isPresent() && !constantCondition.get()) {
            return;
        }
        Scope bodyScope = scope.fork();
        analyzeBlock(statement.getBody(), bodyScope);
        scope.absorb(bodyScope);
    }

    private void analyzeExpression(Expression expression, Scope scope) {
        if (expression == null) {
            return;
        }
        checkPrimary(expression.getPrimary(), scope);
        expression.getPrimaryArguments().ifPresent(args -> args.forEach(arg -> analyzeExpression(arg, scope)));
        for (ExpressionSuffix suffix : expression.getSuffixes()) {
            suffix.arguments().ifPresent(args -> args.forEach(arg -> analyzeExpression(arg, scope)));
        }
    }

    private void checkPrimary(Primary primary, Scope scope) {
        if (primary == null) {
            return;
        }
        switch (primary.getPrimaryType()) {
            case Identifier -> {
                String name = primary.getValueToken().getLexeme();
                if (scope.isDeclared(name)) {
                    return;
                }
                if (scope.isClassName(name)) {
                    return;
                }
                throw SemanticException.at("Use of undeclared variable: " + name, primary.getValueToken());
            }
            case This, IntegerLiteral, RealLiteral, BooleanLiteral, StringLiteral -> {
                // Allowed without further checks
            }
            default -> {
            }
        }
    }

    private Optional<Boolean> evaluateBoolean(Expression expression, Set<String> classNames) {
        ConstantEvaluator evaluator = new ConstantEvaluator(classNames);
        Optional<Object> value = evaluator.evaluate(expression);
        if (value.isPresent() && value.get() instanceof Boolean result) {
            return Optional.of(result);
        }
        return Optional.empty();
    }

    private static final class Scope {
        private final Set<String> classNames;
        private final Set<String> declared = new HashSet<>();

        Scope(Set<String> classNames, Set<String> declared) {
            this.classNames = Set.copyOf(classNames);
            if (declared != null) {
                this.declared.addAll(declared);
            }
        }

        Scope fork() {
            return new Scope(classNames, declared);
        }

        void absorb(Scope other) {
            declared.addAll(other.declared);
        }

        void declare(String name) {
            if (name != null && !name.isBlank()) {
                declared.add(name);
            }
        }

        boolean isDeclared(String name) {
            return declared.contains(name);
        }

        void requireDeclared(String name, lexicalanalyzer.Token token) {
            if (!isDeclared(name) && !isClassName(name)) {
                throw SemanticException.at("Use of undeclared variable: " + name, token);
            }
        }

        boolean isClassName(String name) {
            return classNames.contains(name);
        }

        Set<String> getClassNames() {
            return classNames;
        }
    }

    private static final class ConstantEvaluator {
        private final Set<String> classNames;

        ConstantEvaluator(Set<String> classNames) {
            this.classNames = classNames;
        }

        Optional<Object> evaluate(Expression expression) {
            if (expression == null) {
                return Optional.empty();
            }
            Optional<Object> base = evaluatePrimary(expression.getPrimary());
            if (base.isEmpty()) {
                return Optional.empty();
            }
            Object value = base.get();

            if (expression.getPrimaryArguments().isPresent()) {
                Optional<List<Object>> args = evaluateArguments(expression.getPrimaryArguments().get());
                if (args.isEmpty()) {
                    return Optional.empty();
                }
                Optional<Object> callResult = invoke(value, null, args.get());
                if (callResult.isEmpty()) {
                    return Optional.empty();
                }
                value = callResult.get();
            }

            for (ExpressionSuffix suffix : expression.getSuffixes()) {
                if (suffix.arguments().isEmpty()) {
                    return Optional.empty(); // field access is not constant-folded
                }
                Optional<List<Object>> args = evaluateArguments(suffix.arguments().get());
                if (args.isEmpty()) {
                    return Optional.empty();
                }
                Optional<Object> callResult = invoke(value, suffix.identifier().getLexeme(), args.get());
                if (callResult.isEmpty()) {
                    return Optional.empty();
                }
                value = callResult.get();
            }

            return Optional.ofNullable(value);
        }

        private Optional<List<Object>> evaluateArguments(List<Expression> expressions) {
            List<Object> args = new ArrayList<>(expressions.size());
            for (Expression expression : expressions) {
                Optional<Object> arg = evaluate(expression);
                if (arg.isEmpty()) {
                    return Optional.empty();
                }
                args.add(arg.get());
            }
            return Optional.of(args);
        }

        private Optional<Object> evaluatePrimary(Primary primary) {
            if (primary == null) {
                return Optional.empty();
            }
            return switch (primary.getPrimaryType()) {
                case IntegerLiteral -> Optional.of(Integer.parseInt(primary.getValueToken().getLexeme()));
                case RealLiteral -> Optional.of(Double.parseDouble(primary.getValueToken().getLexeme()));
                case BooleanLiteral -> Optional.of(Boolean.parseBoolean(primary.getValueToken().getLexeme()));
                case StringLiteral -> Optional.of(primary.getValueToken().getValue());
                case Identifier, ClassName -> {
                    String name = primary.getValueToken().getLexeme();
                    if (classNames.contains(name)) {
                        yield Optional.of(new ClassRef(name));
                    }
                    yield Optional.empty();
                }
                case This -> Optional.empty();
            };
        }

        private Optional<Object> invoke(Object target, String methodName, List<Object> args) {
            if (target instanceof ClassRef classRef) {
                if (methodName != null) {
                    return Optional.empty();
                }
                return instantiateBuiltin(classRef.name(), args);
            }
            if (methodName == null) {
                return Optional.empty();
            }
            if (target instanceof Integer integer) {
                return invokeInteger(integer, methodName, args);
            }
            if (target instanceof Double real) {
                return invokeReal(real, methodName, args);
            }
            if (target instanceof Boolean bool) {
                return invokeBoolean(bool, methodName, args);
            }
            if (target instanceof String string) {
                return invokeString(string, methodName, args);
            }
            return Optional.empty();
        }

        private Optional<Object> instantiateBuiltin(String name, List<Object> args) {
            String lowered = name.toLowerCase();
            switch (lowered) {
                case "integer" -> {
                    if (args.isEmpty()) {
                        return Optional.of(0);
                    }
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of((int) realValue.doubleValue());
                    }
                    if (arg instanceof Boolean bool) {
                        return Optional.of(bool ? 1 : 0);
                    }
                    return Optional.empty();
                }
                case "real" -> {
                    if (args.isEmpty()) {
                        return Optional.of(0.0d);
                    }
                    Object arg = args.get(0);
                    if (arg instanceof Double realValue) {
                        return Optional.of(realValue);
                    }
                    if (arg instanceof Integer intValue) {
                        return Optional.of(intValue.doubleValue());
                    }
                    if (arg instanceof Boolean bool) {
                        return Optional.of(bool ? 1.0d : 0.0d);
                    }
                    return Optional.empty();
                }
                case "boolean" -> {
                    if (args.isEmpty()) {
                        return Optional.of(Boolean.FALSE);
                    }
                    Object arg = args.get(0);
                    if (arg instanceof Boolean bool) {
                        return Optional.of(bool);
                    }
                    if (arg instanceof Integer intValue) {
                        return Optional.of(intValue != 0);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(realValue != 0.0d);
                    }
                    return Optional.empty();
                }
                case "string" -> {
                    if (args.isEmpty()) {
                        return Optional.of("");
                    }
                    Object arg = args.get(0);
                    if (arg instanceof String stringValue) {
                        return Optional.of(stringValue);
                    }
                    if (arg instanceof Boolean bool) {
                        return Optional.of(Boolean.toString(bool));
                    }
                    if (arg instanceof Integer intValue) {
                        return Optional.of(Integer.toString(intValue));
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(Double.toString(realValue));
                    }
                    return Optional.of(arg.toString());
                }
                case "character" -> {
                    if (args.isEmpty()) {
                        return Optional.of("");
                    }
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(Character.toString((char) intValue.intValue()));
                    }
                    if (arg instanceof String stringValue && !stringValue.isEmpty()) {
                        return Optional.of(Character.toString(stringValue.charAt(0)));
                    }
                    return Optional.empty();
                }
                default -> {
                    return Optional.empty();
                }
            }
        }

        private Optional<Object> invokeInteger(Integer receiver, String methodName, List<Object> args) {
            String lowered = methodName.toLowerCase();
            switch (lowered) {
                case "addone" -> {
                    return Optional.of(receiver + 1);
                }
                case "toreal" -> {
                    return Optional.of(receiver.doubleValue());
                }
                case "toboolean" -> {
                    return Optional.of(receiver != 0);
                }
                case "unaryminus", "unatyminus" -> {
                    return Optional.of(-receiver);
                }
                case "plus" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver + intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver + realValue);
                    }
                    return Optional.empty();
                }
                case "minus" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver - intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver - realValue);
                    }
                    return Optional.empty();
                }
                case "mult" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver * intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver * realValue);
                    }
                    return Optional.empty();
                }
                case "div" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        if (intValue == 0) {
                            return Optional.of(0);
                        }
                        return Optional.of(receiver / intValue);
                    }
                    if (arg instanceof Double realValue) {
                        if (realValue == 0.0d) {
                            return Optional.of(0.0d);
                        }
                        return Optional.of(receiver / realValue);
                    }
                    return Optional.empty();
                }
                case "rem" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        if (intValue == 0) {
                            return Optional.of(0);
                        }
                        return Optional.of(receiver % intValue);
                    }
                    return Optional.empty();
                }
                case "less" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver < intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver < realValue);
                    }
                    return Optional.empty();
                }
                case "lessequal" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver <= intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver <= realValue);
                    }
                    return Optional.empty();
                }
                case "greater" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver > intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver > realValue);
                    }
                    return Optional.empty();
                }
                case "greaterequal" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver >= intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver >= realValue);
                    }
                    return Optional.empty();
                }
                case "equal" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver.equals(intValue));
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver.doubleValue() == realValue);
                    }
                    return Optional.empty();
                }
                case "getmin" -> {
                    return Optional.of(Integer.MIN_VALUE);
                }
                case "getmax" -> {
                    return Optional.of(Integer.MAX_VALUE);
                }
                default -> {
                    return Optional.empty();
                }
            }
        }

        private Optional<Object> invokeReal(Double receiver, String methodName, List<Object> args) {
            String lowered = methodName.toLowerCase();
            switch (lowered) {
                case "toreal" -> {
                    return Optional.of(receiver);
                }
                case "tointeger" -> {
                    return Optional.of(receiver.intValue());
                }
                case "unaryminus", "unatyminus" -> {
                    return Optional.of(-receiver);
                }
                case "plus" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver + intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver + realValue);
                    }
                    return Optional.empty();
                }
                case "minus" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver - intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver - realValue);
                    }
                    return Optional.empty();
                }
                case "mult" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver * intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver * realValue);
                    }
                    return Optional.empty();
                }
                case "div" -> {
                    Object arg = args.get(0);
                    double divisor;
                    if (arg instanceof Integer intValue) {
                        divisor = intValue;
                    } else if (arg instanceof Double realValue) {
                        divisor = realValue;
                    } else {
                        return Optional.empty();
                    }
                    if (divisor == 0.0d) {
                        return Optional.of(0.0d);
                    }
                    return Optional.of(receiver / divisor);
                }
                case "rem" -> {
                    Object arg = args.get(0);
                    double divisor;
                    if (arg instanceof Integer intValue) {
                        divisor = intValue;
                    } else if (arg instanceof Double realValue) {
                        divisor = realValue;
                    } else {
                        return Optional.empty();
                    }
                    if (divisor == 0.0d) {
                        return Optional.of(0.0d);
                    }
                    return Optional.of(receiver % divisor);
                }
                case "less" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver < intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver < realValue);
                    }
                    return Optional.empty();
                }
                case "lessequal" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver <= intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver <= realValue);
                    }
                    return Optional.empty();
                }
                case "greater" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver > intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver > realValue);
                    }
                    return Optional.empty();
                }
                case "greaterequal" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver >= intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(receiver >= realValue);
                    }
                    return Optional.empty();
                }
                case "equal" -> {
                    Object arg = args.get(0);
                    if (arg instanceof Integer intValue) {
                        return Optional.of(receiver.doubleValue() == intValue);
                    }
                    if (arg instanceof Double realValue) {
                        return Optional.of(Double.compare(receiver, realValue) == 0);
                    }
                    return Optional.empty();
                }
                case "getmin" -> {
                    return Optional.of(Double.MIN_VALUE);
                }
                case "getmax" -> {
                    return Optional.of(Double.MAX_VALUE);
                }
                case "getepsilon" -> {
                    return Optional.of(Math.ulp(1.0d));
                }
                default -> {
                    return Optional.empty();
                }
            }
        }

        private Optional<Object> invokeBoolean(Boolean receiver, String methodName, List<Object> args) {
            String lowered = methodName.toLowerCase();
            switch (lowered) {
                case "or" -> {
                    Boolean other = asBoolean(args.get(0));
                    return Optional.of(receiver || other);
                }
                case "and" -> {
                    Boolean other = asBoolean(args.get(0));
                    return Optional.of(receiver && other);
                }
                case "xor" -> {
                    Boolean other = asBoolean(args.get(0));
                    return Optional.of(receiver ^ other);
                }
                case "not" -> {
                    return Optional.of(!receiver);
                }
                case "tointeger" -> {
                    return Optional.of(receiver ? 1 : 0);
                }
                default -> {
                    return Optional.empty();
                }
            }
        }

        private Optional<Object> invokeString(String receiver, String methodName, List<Object> args) {
            String lowered = methodName.toLowerCase();
            if ("plus".equals(lowered)) {
                Object arg = args.get(0);
                return Optional.of(receiver + arg.toString());
            }
            return Optional.empty();
        }

        private Boolean asBoolean(Object value) {
            if (value instanceof Boolean bool) {
                return bool;
            }
            if (value instanceof Integer intValue) {
                return intValue != 0;
            }
            if (value instanceof Double realValue) {
                return realValue != 0.0d;
            }
            if (value instanceof String stringValue) {
                return !stringValue.isEmpty();
            }
            return false;
        }
    }

    private record ClassRef(String name) {}
}
