package syntaxanalyzer;

import java.util.List;
import java.util.Optional;
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
import syntaxanalyzer.utils.PrimaryType;

public class AstPrinter {

    private static final String INDENT = "  ";

    public static void print(List<Cls> classes) {
        for (Cls cls : classes) {
            printClass(cls, "");
        }
    }

    private static void printClass(Cls cls, String indent) {
        System.out.printf("%sCLASS %s%n", indent, cls.getName());
        System.out.printf("%sBASECLASS %s%n", indent + INDENT, cls.getBaseClass());
        if (cls.getBody() != null) {
            printClassBody(cls.getBody(), indent + INDENT);
        }
    }

    private static void printClassBody(ClsBody body, String indent) {
        System.out.printf("%sVARIABLES%n", indent);
        if (body.getVariables().isEmpty()) {
            System.out.printf("%s<none>%n", indent + INDENT);
        } else {
            for (Variable variable : body.getVariables()) {
                printVariable(variable, indent + INDENT);
            }
        }

        System.out.printf("%sCONSTRUCTORS%n", indent);
        if (body.getConstructors().isEmpty()) {
            System.out.printf("%s<none>%n", indent + INDENT);
        } else {
            for (Constructor constructor : body.getConstructors()) {
                printConstructor(constructor, indent + INDENT);
            }
        }

        System.out.printf("%sMETHODS%n", indent);
        if (body.getMethods().isEmpty()) {
            System.out.printf("%s<none>%n", indent + INDENT);
        } else {
            for (Method method : body.getMethods()) {
                printMethod(method, indent + INDENT);
            }
        }
    }

    private static void printConstructor(Constructor constructor, String indent) {
        System.out.printf("%sCONSTRUCTOR%n", indent);
        printParameters(constructor.getParameters(), indent + INDENT);
        printBlock(constructor.getBody(), indent + INDENT);
    }

    private static void printMethod(Method method, String indent) {
        System.out.printf("%sMETHOD %s%n", indent, method.getName());
        System.out.printf(
                "%sRETURNS %s%n",
                indent + INDENT,
                method.getReturnType() == null ? "<void>" : method.getReturnType()
        );
        printParameters(method.getParameters(), indent + INDENT);
        printBlock(method.getBody(), indent + INDENT);
    }

    private static void printParameters(List<ParameterDeclaration> parameters, String indent) {
        if (parameters == null || parameters.isEmpty()) {
            System.out.printf("%sPARAMETERS []%n", indent);
            return;
        }

        System.out.printf("%sPARAMETERS%n", indent);
        for (ParameterDeclaration parameter : parameters) {
            System.out.printf(
                    "%s- %s: %s%n",
                    indent + INDENT,
                    parameter.name(),
                    parameter.ClassName()
            );
        }
    }

    private static void printBlock(Block block, String indent) {
        if (block == null) {
            System.out.printf("%s<no block>%n", indent);
            return;
        }

        System.out.printf("%sBLOCK%n", indent);
        if (block.getParts().isEmpty()) {
            System.out.printf("%s<empty>%n", indent + INDENT);
            return;
        }

        for (Declaration declaration : block.getParts()) {
            printDeclaration(declaration, indent + INDENT);
        }
    }

    private static void printDeclaration(Declaration declaration, String indent) {
        if (declaration instanceof Variable variable) {
            printVariable(variable, indent);
        } else if (declaration instanceof Assignment assignment) {
            printAssignment(assignment, indent);
        } else if (declaration instanceof IfStatement ifStatement) {
            printIfStatement(ifStatement, indent);
        } else if (declaration instanceof WhileStatement whileStatement) {
            printWhileStatement(whileStatement, indent);
        } else if (declaration instanceof ReturnStatement returnStatement) {
            printReturnStatement(returnStatement, indent);
        } else if (declaration instanceof Expression expression) {
            printExpression(expression, indent);
        } else {
            System.out.printf("%s%s%n", indent, declaration.getClass().getSimpleName());
        }
    }

    private static void printVariable(Variable variable, String indent) {
        System.out.printf("%sVARIABLE %s%n", indent, variable.getName());
        if (variable.getExpression() != null) {
            printExpression(variable.getExpression(), indent + INDENT);
        }
    }

    private static void printAssignment(Assignment assignment, String indent) {
        System.out.printf("%sASSIGNMENT %s%n", indent, assignment.getName());
        printExpression(assignment.getExpression(), indent + INDENT);
    }

    private static void printIfStatement(IfStatement ifStatement, String indent) {
        System.out.printf("%sIF%n", indent);
        System.out.printf("%sCONDITION%n", indent + INDENT);
        printExpression(ifStatement.getCondition(), indent + INDENT + INDENT);
        System.out.printf("%sTHEN%n", indent + INDENT);
        printBlock(ifStatement.getTrueBlock(), indent + INDENT + INDENT);
        if (ifStatement.getFalseBlock() != null) {
            System.out.printf("%sELSE%n", indent + INDENT);
            printBlock(ifStatement.getFalseBlock(), indent + INDENT + INDENT);
        }
    }

    private static void printWhileStatement(WhileStatement whileStatement, String indent) {
        System.out.printf("%sWHILE%n", indent);
        System.out.printf("%sCONDITION%n", indent + INDENT);
        printExpression(whileStatement.getCondition(), indent + INDENT + INDENT);
        System.out.printf("%sBODY%n", indent + INDENT);
        printBlock(whileStatement.getBody(), indent + INDENT + INDENT);
    }

    private static void printReturnStatement(ReturnStatement returnStatement, String indent) {
        System.out.printf("%sRETURN%n", indent);
        if (returnStatement.getValue() != null) {
            printExpression(returnStatement.getValue(), indent + INDENT);
        }
    }

    private static void printExpression(Expression expression, String indent) {
        if (expression == null) {
            System.out.printf("%s<null expression>%n", indent);
            return;
        }

        System.out.printf("%sEXPRESSION%n", indent);
        printPrimary(expression.getPrimary(), indent + INDENT);
        if (!expression.getSuffixes().isEmpty()) {
            System.out.printf("%sSUFFIXES%n", indent + INDENT);
            for (ExpressionSuffix suffix : expression.getSuffixes()) {
                printSuffix(suffix, indent + INDENT + INDENT);
            }
        }
    }

    private static void printPrimary(Primary primary, String indent) {
        PrimaryType primaryType = primary.getPrimaryType();
        String value = primary.getValueToken() == null ? "<unknown>" : primary.getValueToken().getLexeme();
        System.out.printf("%sPRIMARY %s %s%n", indent, primaryType, value);
    }

    private static void printSuffix(ExpressionSuffix suffix, String indent) {
        System.out.printf("%s.%s%n", indent, suffix.identifier().getLexeme());
        Optional<List<Expression>> arguments = suffix.arguments();
        if (arguments.isPresent()) {
            System.out.printf("%sARGUMENTS%n", indent + INDENT);
            for (Expression expression : arguments.get()) {
                printExpression(expression, indent + INDENT + INDENT);
            }
        }
    }
}
