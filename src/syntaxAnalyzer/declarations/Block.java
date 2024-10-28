package syntaxanalyzer.declarations;


import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Block extends Declaration{

    List<Declaration> parts;
    
    public List<Declaration> getParts() {
        return parts;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        while (index < tokens.size()) {
            Token currentToken = tokens.get(index);

            if (currentToken.getToken() == TokenType.KEYWORD_VAR) {
                Variable variable = new Variable();
                index = variable.parse(tokens, index);
                parts.add(variable);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_WHILE) {
                WhileStatement stmt = new WhileStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_IF) {
                IfStatement stmt = new IfStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_RETURN) {
                ReturnStatement stmt = new ReturnStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            else if (currentToken.getToken() == TokenType.IDENTIFIER) {
                Expression expression = new Expression();
                index = expression.parse(tokens, index);
                parts.add(expression);
            } // TODO: consider the option to add calls
            else if (currentToken.getToken() == TokenType.KEYWORD_END) {
                return index;
            }
            else {
                throw new RuntimeException("Unexpected token in class body: " + currentToken.getToken() + " on the " + index);
            }

            index += 1;
        }
        throw new RuntimeException("Unexpected end of tokens while parsing class body");
    }
    
}
