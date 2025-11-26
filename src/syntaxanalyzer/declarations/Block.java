package syntaxanalyzer.declarations;


import java.util.ArrayList;
import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.SyntaxException;

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
            if (currentToken.getToken() == TokenCode.KEYWORD_VAR) {
                Variable variable = new Variable();
                index = variable.parse(tokens, index);
                parts.add(variable);
            }
            // While statement
            else if (currentToken.getToken() == TokenCode.KEYWORD_WHILE) {
                WhileStatement stmt = new WhileStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            // If statement
            else if (currentToken.getToken() == TokenCode.KEYWORD_IF) {
                IfStatement stmt = new IfStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            // Return statement
            else if (currentToken.getToken() == TokenCode.KEYWORD_RETURN) {
                ReturnStatement stmt = new ReturnStatement();
                index = stmt.parse(tokens, index);
                parts.add(stmt);
            }
            // Assignment : Identifier ':=' Expression
            // OR
            // Expression
            else if (currentToken.getToken() == TokenCode.IDENTIFIER) {
                if (tokens.get(index + 1).getToken() == TokenCode.PUNCTUATION_SEMICOLON_EQUAL) {
                    Assignment assignment = new Assignment();
                    index = assignment.parse(tokens, index);
                    parts.add(assignment);
                } else {
                    Expression expression = new Expression();
                    index = expression.parse(tokens, index);
                    parts.add(expression);
                }
            } 
            else if (currentToken.getToken() == TokenCode.KEYWORD_END) {
                return index - 1; // Last KEYWORAD_END is not a part of current BLOCK
            } else if (currentToken.getToken() == TokenCode.KEYWORD_ELSE) {
                return index - 1; // Last KEYWORAD_ELSE is not a part of current BLOCK
            }

            index += 1;
        }
        Token last = index > 0 ? tokens.get(index - 1) : null;
        throw SyntaxException.at("Unexpected end of tokens while parsing class body", last);
    }
    
}
 
