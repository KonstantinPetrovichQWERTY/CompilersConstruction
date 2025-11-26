package olang.runtime;

import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import syntaxanalyzer.SyntaxAnalyzer;
import syntaxanalyzer.declarations.Assignment;
import syntaxanalyzer.declarations.Block;
import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.declarations.Constructor;
import syntaxanalyzer.declarations.Declaration;
import syntaxanalyzer.declarations.Expression;
import syntaxanalyzer.declarations.IfStatement;
import syntaxanalyzer.declarations.Method;
import syntaxanalyzer.declarations.ReturnStatement;
import syntaxanalyzer.declarations.Variable;
import syntaxanalyzer.declarations.WhileStatement;
import syntaxanalyzer.utils.ExpressionSuffix;
import syntaxanalyzer.utils.ParameterDeclaration;
import syntaxanalyzer.utils.PrimaryType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Simple tree-walking interpreter for the O language.
 */
public final class Interpreter {

    private final Map<String, RuntimeClass> classes = new HashMap<>();
    private final Map<String, ClassValue> builtins = new HashMap<>();

    public Interpreter(List<Cls> classDecls) {
        Objects.requireNonNull(classDecls, "classDecls");
        for (Cls cls : classDecls) {
            RuntimeClass runtimeClass = new RuntimeClass(cls);
            classes.put(runtimeClass.getName(), runtimeClass);
        }

        registerBuiltin(new ClassValue(ClassValue.BuiltinClass.INTEGER, "Integer"));
        registerBuiltin(new ClassValue(ClassValue.BuiltinClass.REAL, "Real"));
        registerBuiltin(new ClassValue(ClassValue.BuiltinClass.BOOLEAN, "Boolean"));
        registerBuiltin(new ClassValue(ClassValue.BuiltinClass.STRING, "String"));
        registerBuiltin(new ClassValue(ClassValue.BuiltinClass.CHARACTER, "Character"));
    }

    private void registerBuiltin(ClassValue value) {
        builtins.put(value.getName(), value);
    }

    public void run(String entryClassName) {
        ClassValue entry = resolveClass(entryClassName);
        construct(entry, List.of());
    }

    private ClassValue resolveClass(String name) {
        ClassValue builtin = builtins.get(name);
        if (builtin != null) {
            return builtin;
        }
        RuntimeClass runtimeClass = classes.get(name);
        if (runtimeClass == null) {
            throw new IllegalStateException("Unknown class: " + name);
        }
        return new ClassValue(name, runtimeClass);
    }

    private InstanceValue construct(ClassValue classValue, List<Value> args) {
        if (classValue.isBuiltin()) {
            throw new IllegalStateException("Cannot construct builtin class through user constructor path");
        }
        RuntimeClass runtimeClass = classValue.getRuntimeClass();
        InstanceValue instance = new InstanceValue(runtimeClass);
        // Initialize fields to null so reads before init do not crash.
        runtimeClass.getFields().forEach(field -> instance.setField(field.getName(), NullValue.INSTANCE));
        ExecutionContext initContext = new ExecutionContext(this, instance);

        // Field initializers
        for (Variable variable : runtimeClass.getFields()) {
            Value value = evaluateExpression(variable.getExpression(), initContext);
            instance.setField(variable.getName(), value);
        }

        // Constructor body
        Constructor constructor = runtimeClass.findConstructor(args.size());
        if (constructor != null) {
            ExecutionContext ctx = new ExecutionContext(this, instance);
            bindParameters(ctx, constructor.getParameters(), args);
            executeBlock(constructor.getBody(), ctx);
        } else if (!runtimeClass.getConstructors().isEmpty() || !args.isEmpty()) {
            throw new IllegalStateException("No matching constructor for " + runtimeClass.getName());
        }
        return instance;
    }

    private Value instantiateBuiltin(ClassValue classValue, List<Value> args) {
        String name = classValue.getName();
        String lowered = name.toLowerCase();
        return switch (lowered) {
            case "integer" -> createInteger(args);
            case "real" -> createReal(args);
            case "boolean" -> createBoolean(args);
            case "string" -> createString(args);
            case "character" -> createCharacter(args);
            default -> throw new IllegalStateException("Unknown builtin class: " + name);
        };
    }

    private Value createInteger(List<Value> args) {
        if (args.isEmpty()) {
            return new IntValue(0);
        }
        Value arg = args.get(0);
        if (arg instanceof IntValue intValue) {
            return new IntValue(intValue.getValue());
        }
        if (arg instanceof RealValue realValue) {
            return new IntValue((int) realValue.getValue());
        }
        if (arg instanceof BoolValue boolValue) {
            return new IntValue(boolValue.getValue() ? 1 : 0);
        }
        throw new IllegalStateException("Unsupported Integer constructor argument");
    }

    private Value createReal(List<Value> args) {
        if (args.isEmpty()) {
            return new RealValue(0.0d);
        }
        Value arg = args.get(0);
        if (arg instanceof RealValue realValue) {
            return new RealValue(realValue.getValue());
        }
        if (arg instanceof IntValue intValue) {
            return new RealValue(intValue.getValue());
        }
        if (arg instanceof BoolValue boolValue) {
            return new RealValue(boolValue.getValue() ? 1d : 0d);
        }
        throw new IllegalStateException("Unsupported Real constructor argument");
    }

    private Value createBoolean(List<Value> args) {
        if (args.isEmpty()) {
            return new BoolValue(false);
        }
        Value arg = args.get(0);
        if (arg instanceof BoolValue boolValue) {
            return new BoolValue(boolValue.getValue());
        }
        if (arg instanceof IntValue intValue) {
            return new BoolValue(intValue.getValue() != 0);
        }
        if (arg instanceof RealValue realValue) {
            return new BoolValue(realValue.getValue() != 0.0d);
        }
        throw new IllegalStateException("Unsupported Boolean constructor argument");
    }

    private Value createString(List<Value> args) {
        if (args.isEmpty()) {
            return new StringValue("");
        }
        Value arg = args.get(0);
        if (arg instanceof StringValue stringValue) {
            return new StringValue(stringValue.getValue());
        }
        if (arg instanceof BoolValue boolValue) {
            return new StringValue(boolValue.display());
        }
        if (arg instanceof IntValue intValue) {
            return new StringValue(intValue.display());
        }
        if (arg instanceof RealValue realValue) {
            return new StringValue(realValue.display());
        }
        return new StringValue(arg.display());
    }

    private Value createCharacter(List<Value> args) {
        if (args.isEmpty()) {
            return new StringValue("");
        }
        Value arg = args.get(0);
        if (arg instanceof IntValue intValue) {
            return new StringValue(Character.toString((char) intValue.getValue()));
        }
        if (arg instanceof StringValue stringValue) {
            String content = stringValue.getValue();
            if (content.isEmpty()) {
                return new StringValue("");
            }
            return new StringValue(Character.toString(content.charAt(0)));
        }
        return new StringValue(arg.display());
    }

    private void bindParameters(ExecutionContext ctx, List<ParameterDeclaration> params, List<Value> args) {
        if (params == null) {
            return;
        }
        for (int i = 0; i < params.size(); i++) {
            ParameterDeclaration param = params.get(i);
            Value argValue = args.size() > i ? args.get(i) : NullValue.INSTANCE;
            ctx.locals().put(param.name(), argValue);
        }
    }

    private void executeBlock(Block block, ExecutionContext ctx) {
        if (block == null) {
            return;
        }
        for (Declaration declaration : block.getParts()) {
            executeDeclaration(declaration, ctx);
        }
    }

    private void executeDeclaration(Declaration declaration, ExecutionContext ctx) {
        if (declaration instanceof Variable variable) {
            Value value = evaluateExpression(variable.getExpression(), ctx);
            ctx.locals().put(variable.getName(), value);
        } else if (declaration instanceof Assignment assignment) {
            Value value = evaluateExpression(assignment.getExpression(), ctx);
            assign(assignment.getName(), value, ctx);
        } else if (declaration instanceof Expression expression) {
            evaluateExpression(expression, ctx);
        } else if (declaration instanceof IfStatement ifStatement) {
            Value condition = evaluateExpression(ifStatement.getCondition(), ctx);
            if (isTruthy(condition)) {
                executeBlock(ifStatement.getTrueBlock(), ctx);
            } else {
                executeBlock(ifStatement.getFalseBlock(), ctx);
            }
        } else if (declaration instanceof WhileStatement whileStatement) {
            while (isTruthy(evaluateExpression(whileStatement.getCondition(), ctx))) {
                executeBlock(whileStatement.getBody(), ctx);
            }
        } else if (declaration instanceof ReturnStatement returnStatement) {
            Value returnValue = returnStatement.getValue() == null
                    ? NullValue.INSTANCE
                    : evaluateExpression(returnStatement.getValue(), ctx);
            throw new ReturnSignal(returnValue);
        }
    }

    private void assign(String name, Value value, ExecutionContext ctx) {
        if (ctx.locals().containsKey(name)) {
            ctx.locals().put(name, value);
            return;
        }
        if (ctx.thisInstance() != null && ctx.thisInstance().hasField(name)) {
            ctx.thisInstance().setField(name, value);
            return;
        }
        ctx.locals().put(name, value);
    }

    private Value evaluateExpression(Expression expression, ExecutionContext ctx) {
        if (expression == null) {
            return NullValue.INSTANCE;
        }
        Value result = evaluatePrimary(expression.getPrimary(), ctx);
        if (expression.getPrimaryArguments().isPresent()) {
            List<Value> args = evaluateArguments(expression.getPrimaryArguments().get(), ctx);
            result = call(result, null, args, ctx);
        }
        for (ExpressionSuffix suffix : expression.getSuffixes()) {
            if (suffix.arguments().isPresent()) {
                List<Value> args = evaluateArguments(suffix.arguments().get(), ctx);
                result = call(result, suffix.identifier().getLexeme(), args, ctx);
            } else {
                result = getMember(result, suffix.identifier().getLexeme(), ctx);
            }
        }
        return result;
    }

    private List<Value> evaluateArguments(List<Expression> expressions, ExecutionContext ctx) {
        List<Value> values = new ArrayList<>(expressions.size());
        for (Expression expr : expressions) {
            values.add(evaluateExpression(expr, ctx));
        }
        return values;
    }

    private Value evaluatePrimary(syntaxanalyzer.declarations.Primary primary, ExecutionContext ctx) {
        if (primary == null) {
            return NullValue.INSTANCE;
        }
        PrimaryType type = primary.getPrimaryType();
        Token valueToken = primary.getValueToken();

        return switch (type) {
            case IntegerLiteral -> new IntValue(Integer.parseInt(valueToken.getLexeme()));
            case RealLiteral -> new RealValue(Double.parseDouble(valueToken.getLexeme()));
            case BooleanLiteral -> new BoolValue(Boolean.parseBoolean(valueToken.getLexeme()));
            case StringLiteral -> new StringValue(valueToken.getValue());
            case This -> ctx.thisInstance() == null ? NullValue.INSTANCE : ctx.thisInstance();
            case Identifier, ClassName -> resolveIdentifier(valueToken.getLexeme(), ctx);
        };
    }

    private Value resolveIdentifier(String name, ExecutionContext ctx) {
        if (ctx.locals().containsKey(name)) {
            return ctx.locals().get(name);
        }
        if (ctx.thisInstance() != null && ctx.thisInstance().hasField(name)) {
            return ctx.thisInstance().getField(name);
        }
        RuntimeClass runtimeClass = classes.get(name);
        if (runtimeClass != null) {
            return new ClassValue(name, runtimeClass);
        }
        ClassValue builtin = builtins.get(name);
        if (builtin != null) {
            return builtin;
        }
        return NullValue.INSTANCE;
    }

    private Value getMember(Value target, String name, ExecutionContext ctx) {
        if (target instanceof InstanceValue instance) {
            if (instance.hasField(name)) {
                return instance.getField(name);
            }
            Method method = instance.getRuntimeClass().findMethod(name, 0);
            if (method != null) {
                return invokeUserMethod(instance, method, List.of());
            }
            return NullValue.INSTANCE;
        }
        // For builtins, treat member access without args as zero-arg method call.
        if (isBuiltinMethod(target, name, 0)) {
            return call(target, name, List.of(), ctx);
        }
        return NullValue.INSTANCE;
    }

    private Value call(Value target, String methodName, List<Value> args, ExecutionContext ctx) {
        if (target instanceof ClassValue classValue) {
            return instantiateBuiltinOrUser(classValue, args);
        }

        if (target instanceof IntValue intValue) {
            return invokeInteger(intValue, methodName, args);
        }
        if (target instanceof RealValue realValue) {
            return invokeReal(realValue, methodName, args);
        }
        if (target instanceof BoolValue boolValue) {
            return invokeBoolean(boolValue, methodName, args);
        }
        if (target instanceof StringValue stringValue) {
            return invokeString(stringValue, methodName, args);
        }
        if (target instanceof InstanceValue instanceValue) {
            RuntimeClass runtimeClass = instanceValue.getRuntimeClass();
            Method method = runtimeClass.findMethod(methodName, args.size());
            if (method == null) {
                throw new IllegalStateException("Method not found: " + runtimeClass.getName() + "." + methodName);
            }
            return invokeUserMethod(instanceValue, method, args);
        }
        return NullValue.INSTANCE;
    }

    private Value instantiateBuiltinOrUser(ClassValue classValue, List<Value> args) {
        if (classValue.isBuiltin()) {
            return instantiateBuiltin(classValue, args);
        }
        return construct(classValue, args);
    }

    private boolean isBuiltinMethod(Value target, String methodName, int argCount) {
        if (target instanceof IntValue || target instanceof RealValue
                || target instanceof BoolValue || target instanceof StringValue) {
            return true;
        }
        return false;
    }

    private Value invokeUserMethod(InstanceValue instance, Method method, List<Value> args) {
        ExecutionContext ctx = new ExecutionContext(this, instance);
        bindParameters(ctx, method.getParameters(), args);
        try {
            executeBlock(method.getBody(), ctx);
        } catch (ReturnSignal returnSignal) {
            return returnSignal.value();
        }
        return NullValue.INSTANCE;
    }

    private Value invokeInteger(IntValue receiver, String methodName, List<Value> args) {
        String name = methodName == null ? "" : methodName;
        String lowered = name.toLowerCase();
        switch (lowered) {
            case "print" -> {
                System.out.println(receiver.display());
                return receiver;
            }
            case "addone" -> {
                return new IntValue(receiver.getValue() + 1);
            }
            case "toreal" -> {
                return new RealValue(receiver.getValue());
            }
            case "toboolean" -> {
                return new BoolValue(receiver.getValue() != 0);
            }
            case "unaryminus", "unatyminus" -> {
                return new IntValue(-receiver.getValue());
            }
            case "plus" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new IntValue(receiver.getValue() + intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new RealValue(receiver.getValue() + realValue.getValue());
                }
            }
            case "minus" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new IntValue(receiver.getValue() - intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new RealValue(receiver.getValue() - realValue.getValue());
                }
            }
            case "mult" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new IntValue(receiver.getValue() * intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new RealValue(receiver.getValue() * realValue.getValue());
                }
            }
            case "div" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new IntValue(intValue.getValue() == 0 ? 0 : receiver.getValue() / intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    double divisor = realValue.getValue();
                    return new RealValue(divisor == 0.0d ? 0.0d : receiver.getValue() / divisor);
                }
            }
            case "rem" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new IntValue(intValue.getValue() == 0 ? 0 : receiver.getValue() % intValue.getValue());
                }
            }
            case "less" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() < intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() < realValue.getValue());
                }
            }
            case "lessequal" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() <= intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() <= realValue.getValue());
                }
            }
            case "greater" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() > intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() > realValue.getValue());
                }
            }
            case "greaterequal" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() >= intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() >= realValue.getValue());
                }
            }
            case "equal" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() == intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() == realValue.getValue());
                }
            }
            case "getmin" -> {
                return new IntValue(Integer.MIN_VALUE);
            }
            case "getmax" -> {
                return new IntValue(Integer.MAX_VALUE);
            }
        }
        throw new IllegalStateException("Unknown Integer method: " + methodName);
    }

    private Value invokeReal(RealValue receiver, String methodName, List<Value> args) {
        String name = methodName == null ? "" : methodName;
        String lowered = name.toLowerCase();
        switch (lowered) {
            case "print" -> {
                System.out.println(receiver.display());
                return receiver;
            }
            case "toreal" -> {
                return receiver;
            }
            case "tointeger" -> {
                return new IntValue((int) receiver.getValue());
            }
            case "unaryminus", "unatyminus" -> {
                return new RealValue(-receiver.getValue());
            }
            case "plus" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new RealValue(receiver.getValue() + intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new RealValue(receiver.getValue() + realValue.getValue());
                }
            }
            case "minus" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new RealValue(receiver.getValue() - intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new RealValue(receiver.getValue() - realValue.getValue());
                }
            }
            case "mult" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new RealValue(receiver.getValue() * intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new RealValue(receiver.getValue() * realValue.getValue());
                }
            }
            case "div" -> {
                Value arg = args.get(0);
                double divisor;
                if (arg instanceof IntValue intValue) {
                    divisor = intValue.getValue();
                } else if (arg instanceof RealValue realValue) {
                    divisor = realValue.getValue();
                } else {
                    divisor = 0.0d;
                }
                return new RealValue(divisor == 0.0d ? 0.0d : receiver.getValue() / divisor);
            }
            case "rem" -> {
                Value arg = args.get(0);
                double divisor;
                if (arg instanceof IntValue intValue) {
                    divisor = intValue.getValue();
                } else if (arg instanceof RealValue realValue) {
                    divisor = realValue.getValue();
                } else {
                    divisor = 0.0d;
                }
                return new RealValue(divisor == 0.0d ? 0.0d : receiver.getValue() % divisor);
            }
            case "less" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() < intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() < realValue.getValue());
                }
            }
            case "lessequal" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() <= intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() <= realValue.getValue());
                }
            }
            case "greater" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() > intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() > realValue.getValue());
                }
            }
            case "greaterequal" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() >= intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() >= realValue.getValue());
                }
            }
            case "equal" -> {
                Value arg = args.get(0);
                if (arg instanceof IntValue intValue) {
                    return new BoolValue(receiver.getValue() == intValue.getValue());
                }
                if (arg instanceof RealValue realValue) {
                    return new BoolValue(receiver.getValue() == realValue.getValue());
                }
            }
            case "getmin" -> {
                return new RealValue(Double.MIN_VALUE);
            }
            case "getmax" -> {
                return new RealValue(Double.MAX_VALUE);
            }
            case "getepsilon" -> {
                return new RealValue(Math.ulp(1.0d));
            }
        }
        throw new IllegalStateException("Unknown Real method: " + methodName);
    }

    private Value invokeBoolean(BoolValue receiver, String methodName, List<Value> args) {
        String name = methodName == null ? "" : methodName;
        String lowered = name.toLowerCase();
        switch (lowered) {
            case "print" -> {
                System.out.println(receiver.display());
                return receiver;
            }
            case "or" -> {
                BoolValue other = asBool(args.get(0));
                return new BoolValue(receiver.getValue() || other.getValue());
            }
            case "and" -> {
                BoolValue other = asBool(args.get(0));
                return new BoolValue(receiver.getValue() && other.getValue());
            }
            case "xor" -> {
                BoolValue other = asBool(args.get(0));
                return new BoolValue(receiver.getValue() ^ other.getValue());
            }
            case "not" -> {
                return new BoolValue(!receiver.getValue());
            }
            case "tointeger" -> {
                return new IntValue(receiver.getValue() ? 1 : 0);
            }
        }
        throw new IllegalStateException("Unknown Boolean method: " + methodName);
    }

    private Value invokeString(StringValue receiver, String methodName, List<Value> args) {
        String name = methodName == null ? "" : methodName;
        String lowered = name.toLowerCase();
        switch (lowered) {
            case "print" -> {
                System.out.println(receiver.display());
                return receiver;
            }
            case "plus" -> {
                Value arg = args.get(0);
                return new StringValue(receiver.getValue() + arg.display());
            }
        }
        throw new IllegalStateException("Unknown String method: " + methodName);
    }

    private BoolValue asBool(Value value) {
        if (value instanceof BoolValue boolValue) {
            return boolValue;
        }
        if (value instanceof IntValue intValue) {
            return new BoolValue(intValue.getValue() != 0);
        }
        if (value instanceof RealValue realValue) {
            return new BoolValue(realValue.getValue() != 0.0d);
        }
        if (value instanceof StringValue stringValue) {
            return new BoolValue(!stringValue.getValue().isEmpty());
        }
        if (value instanceof InstanceValue) {
            return new BoolValue(true);
        }
        return new BoolValue(false);
    }

    private boolean isTruthy(Value value) {
        return asBool(value).getValue();
    }

    /**
     * Entry point used by generated programs to run from a raw source string.
     */
    public static void runSource(String source, String entryClassName) {
        List<Token> tokens = LexicalAnalyzer.getTokensFromString(source);
        List<Cls> classes = SyntaxAnalyzer.analyzeTokens(tokens);
        Interpreter interpreter = new Interpreter(classes);
        interpreter.run(entryClassName);
    }

    private static final class ReturnSignal extends RuntimeException {
        private final Value value;

        ReturnSignal(Value value) {
            this.value = value;
        }

        public Value value() {
            return value;
        }
    }
}
