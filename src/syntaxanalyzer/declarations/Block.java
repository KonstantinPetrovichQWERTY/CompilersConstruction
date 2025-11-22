package syntaxanalyzer.declarations;


import java.util.ArrayList;
import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Block extends Declaration{

    List<Declaration> parts = new ArrayList<>();
    
    public List<Declaration> getParts() {
        return parts;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        while (index < tokens.size()) {
            Token currentToken = tokens.get(index);

            // Variable declaration
            if (currentToken.getToken() == TokenType.KEYWORD_VAR) {
                Variable variable = new Variable();
                index = variable.parse(tokens, index);
                parts.add(variable);
            }
            // While statement
            else if (currentToken.getToken() == TokenType.KEYWORD_WHILE) {
                WhileStatement stmt = new WhileStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            // If statement
            else if (currentToken.getToken() == TokenType.KEYWORD_IF) {
                IfStatement stmt = new IfStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            // Return statement
            else if (currentToken.getToken() == TokenType.KEYWORD_RETURN) {
                ReturnStatement stmt = new ReturnStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            // Assignment : Identifier ':=' Expression
            // OR
            // MethodCall : Expression     (Primary here is only ClassName)
            else if (currentToken.getToken() == TokenType.IDENTIFIER) {
                if (tokens.get(index + 1).getToken() == TokenType.PUNCTUATION_SEMICOLON_EQUAL) {
                    Assignment assignment = new Assignment();
                    index = assignment.parse(tokens, index);
                    parts.add(assignment);
                } else if (tokens.get(index + 1).getToken() == TokenType.PUNCTUATION_DOT) {
                    Expression expression = new Expression();
                    index = expression.parse(tokens, index);
                    parts.add(expression);
                } else {
                    throw new RuntimeException("Unexpected token in assignment or method call: " + currentToken.getToken() + " on the " + index);
                }
            } 
            else if (currentToken.getToken() == TokenType.KEYWORD_END) {
                return index;
            } else if (currentToken.getToken() == TokenType.KEYWORD_ELSE) {
                return index;
            } else {
                Expression expression = new Expression();
                index = expression.parse(tokens, index);
                parts.add(expression);
            }

            index += 1;
        }
        throw new RuntimeException("Unexpected end of tokens while parsing class body");
    }
    
}
 
