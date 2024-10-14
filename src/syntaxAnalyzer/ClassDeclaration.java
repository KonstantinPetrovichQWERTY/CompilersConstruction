package syntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;
import syntaxAnalyzer.Functions.ConstructorDeclaration;
import syntaxAnalyzer.Functions.MethodDeclaration;
import syntaxAnalyzer.Objects.VariableDeclatation;

public class ClassDeclaration extends Node {
    String name;
    List<VariableDeclatation> attributes = new ArrayList<>();
    List<ConstructorDeclaration> constructors = new ArrayList<>();
    List<MethodDeclaration> methods = new ArrayList<>();
    
    public ClassDeclaration() {}

    public ClassDeclaration(List<VariableDeclatation> attributes, List<ConstructorDeclaration> constructors, List<MethodDeclaration> methods) {
        this.attributes = attributes;
        this.constructors = constructors;
        this.methods = methods;
    }

    @Override
    public Integer validate(List<Token> tokens, Integer index) {
        List<TokenType> syntax = new ArrayList<>();
        syntax.add(TokenType.KEYWORD_CLASS);
        syntax.add(TokenType.IDENTIFIER);
        syntax.add(TokenType.KEYWORD_IS); // TODO add extends
        
        for (TokenType elem : syntax) {
            System.out.println(tokens.get(index).getValue() + " " + elem);

            if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
                name = tokens.get(index).getValue();
            }

            if(tokens.get(index).getToken() != elem){
                SyntaxAnalyzer.errorMessage("jopa");
            }
            index++;
        }
        
        ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration();
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        VariableDeclatation variableDeclatation = new VariableDeclatation();

        while (true) {
            if (tokens.get(index).getToken() == TokenType.KEYWORD_END || tokens.get(index).getToken() == TokenType.EOF) {
                index++;
                break;
            }

            if (tokens.get(index).getToken() == TokenType.KEYWORD_THIS) {
                index = constructorDeclaration.validate(tokens, index);
                index++;
                continue;
            }

            if (tokens.get(index).getToken() == TokenType.KEYWORD_VAR) {
                index = variableDeclatation.validate(tokens, index);
                index++;
                continue;
            }

            if (tokens.get(index).getToken() == TokenType.KEYWORD_METHOD) {
                index = methodDeclaration.validate(tokens, index);
                index++;
                continue;
            }
        }

        return index++;
    }
    
    // TODO: generate()
    @Override
    public Integer generate(List<Token> tokens, Integer index) {
        System.out.println("Class Declaration generation");
        return 0;
    }
    
}
