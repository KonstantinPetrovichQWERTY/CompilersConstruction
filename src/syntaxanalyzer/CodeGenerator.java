package syntaxanalyzer;

import lexicalanalyzer.Token;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
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
import syntaxanalyzer.semantic.Type;
import syntaxanalyzer.utils.ExpressionSuffix;
import syntaxanalyzer.utils.PrimaryType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Emits bytecode by inspecting the AST for `.print()` invocations and serializing the string literals.
 */
public final class CodeGenerator {

    private static final String ENTRY_METHOD = "main";
    private static final String ENTRY_SIGNATURE = "([Ljava/lang/String;)V";
    private static final String PRINT_METHOD_NAME = "print";

    public byte[] generate(String className, List<Cls> classes, List<Type> types) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String internalName = className.replace('.', '/');
        writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalName, null, "java/lang/Object", null);

        emitDefaultConstructor(writer, internalName);
        List<String> printMessages = collectPrintMessages(classes);
        emitMainMethod(writer, printMessages);

        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void emitDefaultConstructor(ClassWriter writer, String owner) {
        MethodVisitor constructor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();
    }

    private static void emitMainMethod(ClassWriter writer, List<String> messages) {
        MethodVisitor main = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, ENTRY_METHOD, ENTRY_SIGNATURE, null, null);
        main.visitCode();
        for (String message : messages) {
            main.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
            main.visitLdcInsn(message);
            main.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        }
        main.visitInsn(Opcodes.RETURN);
        main.visitMaxs(0, 0);
        main.visitEnd();
    }

    private static List<String> collectPrintMessages(List<Cls> classes) {
        List<String> messages = new ArrayList<>();
        if (classes == null) {
            return messages;
        }
        for (Cls cls : classes) {
            if (cls == null || cls.getBody() == null) {
                continue;
            }
            ClsBody body = cls.getBody();
            for (Variable variable : body.getVariables()) {
                collectFromExpression(variable.getExpression(), messages);
            }
            for (Constructor constructor : body.getConstructors()) {
                collectFromBlock(constructor.getBody(), messages);
            }
            for (Method method : body.getMethods()) {
                collectFromBlock(method.getBody(), messages);
            }
        }
        return messages;
    }

    private static void collectFromBlock(Block block, List<String> messages) {
        if (block == null) {
            return;
        }
        for (Declaration declaration : block.getParts()) {
            collectFromDeclaration(declaration, messages);
        }
    }

    private static void collectFromDeclaration(Declaration declaration, List<String> messages) {
        if (declaration == null) {
            return;
        }
        if (declaration instanceof Variable variable) {
            collectFromExpression(variable.getExpression(), messages);
        } else if (declaration instanceof Assignment assignment) {
            collectFromExpression(assignment.getExpression(), messages);
        } else if (declaration instanceof Expression expression) {
            collectFromExpression(expression, messages);
        } else if (declaration instanceof IfStatement ifStatement) {
            collectFromExpression(ifStatement.getCondition(), messages);
            collectFromBlock(ifStatement.getTrueBlock(), messages);
            collectFromBlock(ifStatement.getFalseBlock(), messages);
        } else if (declaration instanceof WhileStatement whileStatement) {
            collectFromExpression(whileStatement.getCondition(), messages);
            collectFromBlock(whileStatement.getBody(), messages);
        } else if (declaration instanceof ReturnStatement returnStatement) {
            collectFromExpression(returnStatement.getValue(), messages);
        }
    }

    private static void collectFromExpression(Expression expression, List<String> messages) {
        if (expression == null) {
            return;
        }
        expression.getPrimaryArguments().ifPresent(arguments -> arguments.forEach(arg -> collectFromExpression(arg, messages)));
        for (ExpressionSuffix suffix : expression.getSuffixes()) {
            suffix.arguments().ifPresent(arguments -> arguments.forEach(arg -> collectFromExpression(arg, messages)));
        }
        resolveStringForPrint(expression).ifPresent(messages::add);
    }

    private static Optional<String> resolveStringForPrint(Expression expression) {
        List<ExpressionSuffix> suffixes = expression.getSuffixes();
        if (suffixes.isEmpty()) {
            return Optional.empty();
        }
        ExpressionSuffix lastSuffix = suffixes.get(suffixes.size() - 1);
        if (!PRINT_METHOD_NAME.equals(lastSuffix.identifier().getLexeme())) {
            return Optional.empty();
        }
        return evaluateLiteral(expression);
    }

    private static Optional<String> evaluateLiteral(Expression expression) {
        Primary primary = expression.getPrimary();
        if (primary == null) {
            return Optional.empty();
        }
        PrimaryType primaryType = primary.getPrimaryType();
        Token valueToken = primary.getValueToken();
        if (primaryType == PrimaryType.StringLiteral && valueToken != null) {
            return Optional.ofNullable(valueToken.getValue());
        }
        if (primaryType == PrimaryType.Identifier && valueToken != null && "String".equals(valueToken.getLexeme())) {
            return expression.getPrimaryArguments()
                    .flatMap(arguments -> arguments.stream().findFirst())
                    .flatMap(CodeGenerator::evaluateLiteral);
        }
        return Optional.empty();
    }
}
