package syntaxAnalyzer;

import java.util.List;
import lexicalAnalyzer.LexicalAnalyzer;
import lexicalAnalyzer.Token;
import syntaxAnalyzer.Declarations.ClassDeclaration;

public class SynataxAnalyzer {
    // private String fileName;
    // List<Token> tokens;

    // public SynataxAnalyzer(String fileName, List<Token> tokens) {
    //     this.fileName = fileName;
    //     this.tokens = tokens;
    // }

    public static void main(String[] args) {
        List<Token> tokensList = LexicalAnalyzer.getTokens("D:/Innopolis/ucheba/compilers/CompilersConstruction/src/test/testOLang/methodsOverriding.o");
        
        AST rootNode = new AST();
        while (true) {
            ClassDeclaration classDeclarationNode = new ClassDeclaration();
            rootNode.addClassDeclaration(classDeclarationNode);
        }
    }

    public static void errorMessage(String err) {
        System.out.println(err);
        System.exit(1);
    }
    
}
