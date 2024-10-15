package syntaxanalyzer;

import java.util.List;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import syntaxanalyzer.declarations.Cls;

public class SyntaxAnalyzer {
    public static void main(String[] args) {
        List<Token> tokens = LexicalAnalyzer.getTokens("src/test/testOLang/test.o");

        AST rootNode = new AST();
        List<Cls> classes = rootNode.parse(tokens);

        for (Cls cls : classes) {
            System.out.println(cls);
            System.out.println(cls.getName());
            System.out.println(cls.getBaseClass());
        }

    }   

    public static void errorMessage(String err) {
        System.out.println(err);
        System.exit(1);
    }
    
}
