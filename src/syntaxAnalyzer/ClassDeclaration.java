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
    String baseClass;
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
    public Integer parse(List<Token> tokens, Integer index) {
        List<TokenType> syntax = new ArrayList<>();
        syntax.add(TokenType.KEYWORD_CLASS);
        syntax.add(TokenType.IDENTIFIER);
        syntax.add(TokenType.KEYWORD_IS); // TODO add extends
        
        for (TokenType elem : syntax) {
            System.out.println(tokens.get(index).getValue() + " classDecl " + elem);

            if (tokens.get(index).getToken() == TokenType.IDENTIFIER && elem == TokenType.IDENTIFIER) {
                name = tokens.get(index).getValue();
                index++;
                if (tokens.get(index).getToken() == TokenType.KEYWORD_EXTENDS) {        
                    System.out.println(tokens.get(index).getValue() + " classDecl ");
                    index++;
                    if (tokens.get(index).getToken() == TokenType.IDENTIFIER && elem == TokenType.IDENTIFIER) {
                        System.out.println(tokens.get(index).getValue() + " classDecl ");
                        baseClass = tokens.get(index).getValue();
                        index++;
                    }
                }
                continue;
            }

            if(tokens.get(index).getToken() != elem){
                SyntaxAnalyzer.errorMessage("class is incorrectly declared");
            }
            index++;
        }
        
        while (true) {
            if (tokens.get(index).getToken() == TokenType.KEYWORD_END || tokens.get(index).getToken() == TokenType.EOF) {
                index++;
                break;
            }
            
            if (tokens.get(index).getToken() == TokenType.KEYWORD_THIS) {
                ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration();
                index = constructorDeclaration.parse(tokens, index);
                constructors.add(constructorDeclaration);
                index++;
                continue;
            }
            
            if (tokens.get(index).getToken() == TokenType.KEYWORD_VAR) {
                VariableDeclatation variableDeclatation = new VariableDeclatation();
                index = variableDeclatation.parse(tokens, index);
                attributes.add(variableDeclatation);
                index++;
                continue;
            }
            
            if (tokens.get(index).getToken() == TokenType.KEYWORD_METHOD) {
                MethodDeclaration methodDeclaration = new MethodDeclaration();
                index = methodDeclaration.parse(tokens, index);
                methods.add(methodDeclaration);
                index++;
                continue;
            }
        }

        return index++;
    }
    
}
