package syntaxanalyzer;

import java.util.List;
import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import syntaxanalyzer.declarations.Cls;

public class SyntaxAnalyzer {
    public static void main(String[] args) {
        
        String[] files = {"arrayInitializationAccess.o", "booleanOperations.o", "classDeclarationInstantiation.o", "complexExpression.o", "constructorDeclaration.o", "ifStatment.o", "inheritance.o", "integerOperations.o", "listDeclaration.o", "methodDeclarationCall.o", "methodsOverriding.o", "realOperations.o", "returnStatementExpression.o", "test.o", "varDeclarationAssignment.o", "whileLoop.o"};

        for (String elem : files){
            System.out.println("\n" + elem);

            String p = "src/test/testOLang/" + elem;
            List<Token> tokens = LexicalAnalyzer.getTokens(p);
            
            try {
                AST rootNode = new AST();
                List<Cls> classes = rootNode.parse(tokens); 
            } catch (Exception e) {
                System.out.println("Произошла ошибка: " + e.getMessage());
                continue;
            }
       
            
            // for (Cls cls : classes) {
            //     System.out.println("CLASS " + cls.getName());
            //     System.out.println("BASECLASS " + cls.getBaseClass());

            //     for (Constructor constructor : cls.getBody().getConstructors()) {
            //         System.out.println("CONSTRUCTOR " + constructor.getParameters());
            //     }

            //     for (Variable var : cls.getBody().getVariables()) {
            //         System.out.println("VARIABLE " + var.getName() + " " + var.getExpression().getPrimary().getPrimaryType().toString());
            //     }

            //     for (Method method : cls.getBody().getMethods()) {
            //         System.out.println("METHOD "  + method.getName() + " " + method.getParameters());
            //     }
            // }
        }
    }   

    public static void errorMessage(String err) {
        System.out.println(err);
        System.exit(1);
    }
    
}
