package syntaxanalyzer;

import java.util.List;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.declarations.Constructor;
import syntaxanalyzer.declarations.Method;
import syntaxanalyzer.declarations.Variable;

public class SyntaxAnalyzer {
    public static void main(String[] args) {
        List<Token> tokens = LexicalAnalyzer.getTokens("src/test/testOLang/test.o");

        AST rootNode = new AST();
        List<Cls> classes = rootNode.parse(tokens);

        for (Cls cls : classes) {
            System.out.println("CLASS " + cls.getName());
            System.out.println("BASECLASS " + cls.getBaseClass());

            for (Constructor constructor : cls.getBody().getConstructors()) {
                System.out.println("CONSTRUCTOR " + constructor.getParameters());
            }

            for (Variable var : cls.getBody().getVariables()) {
                System.out.println("VARIABLE " + var.getName() + " " + var.getExpression());
            }

            for (Method method : cls.getBody().getMethods()) {
                System.out.println("METHOD "  + method.getName());
            }
        }
    }   

    public static void errorMessage(String err) {
        System.out.println(err);
        System.exit(1);
    }
    
}
