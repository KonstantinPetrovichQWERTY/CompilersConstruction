package syntaxAnalyzer;

import java.util.List;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;
import syntaxAnalyzer.declarations.Cls;
import syntaxAnalyzer.declarations.Constructor;
import syntaxAnalyzer.declarations.Method;
import syntaxAnalyzer.declarations.Variable;

public class SyntaxAnalyzer {
    public static void main(String[] args) {
        List<Token> tokens = LexicalAnalyzer.getTokens("src/test/testOLang/test.o");

        for (Token token : tokens) {
            if(token.getToken() == TokenType.PUNCTUATION_TABULATION)
            {
                System.out.println("String: '" + "\\t" + "', Token type: " + token.getToken());
            }
            else if (token.getToken() == TokenType.PUNCTUATION_LINE_BREAK)
            {
                System.out.println("String: '" + "\\n" + "', Token type: " + token.getToken());
            }
            else {
                System.out.println("String: '" + token.getValue() + "', Token type: " + token.getToken());
            }
        }

        AST rootNode = new AST();
        List<Cls> classes = rootNode.parse(tokens);

        for (Cls cls : classes) {
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
