package syntaxAnalyzer.declarations;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class Expression extends Declaration {
    private Cls cls;

    private Token exprToken;

    public Expression(Cls cls) {
        this.cls = cls;
    }

    public Cls getCls() {
        return cls;
    }

    public Token getExprToken() {
        return exprToken;
    }

    public Object getExprValue() {
        return exprToken.getValue();
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        Token currentToken = tokens.get(index);
        Token keywordToken;
        Token literalToken;
        
        // Value initialization (e.g. Integer, Real, String)
        if ((currentToken.getToken() == TokenType.KEYWORD_INTEGER) || 
            (currentToken.getToken() == TokenType.KEYWORD_STRING) ||
            (currentToken.getToken() == TokenType.KEYWORD_REAL)
        ) {
            keywordToken = currentToken;
            index += 1;
            currentToken = tokens.get(index);
            
            // Expect '(' for the initial value
            if (tokens.get(index).getToken() == TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
                index += 1; // Move past '('
                currentToken = tokens.get(index);
            } else {
                throw new RuntimeException("Expected '(', found: " + tokens.get(index).getToken());
            }
            
            // Store value in exprToken. And check types 
            if (currentToken.getToken().name().startsWith("LITERAL_")) {
                literalToken = currentToken;
                
                checkKeywordLiteral(keywordToken, literalToken);
                this.exprToken = literalToken;
                
                index += 1;
                currentToken = tokens.get(index);
            } 
            else {
                throw new RuntimeException("Expected literal for initial value, found: " + tokens.get(index).getToken());
            }
            
            // Expect ')' for the initial value
            if (tokens.get(index).getToken() == TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
                index += 1; // Move past ')'
            } else {
                throw new RuntimeException("Expected ')', found: " + tokens.get(index).getToken());
            }
        }
        else {
            throw new RuntimeException("Unexpected token while parsing expression");
        }
        
        return index;
    }

    
    private void checkKeywordLiteral(Token keywordToken, Token literalToken) {
        // If not equals -> raise an error
        if (!(((keywordToken.getToken() == TokenType.KEYWORD_INTEGER) &&
            (literalToken.getToken() == TokenType.LITERAL_INTEGER)) || (
            (keywordToken.getToken() == TokenType.KEYWORD_REAL) &&
            (literalToken.getToken() == TokenType.LITERAL_REAL)) || (
            (keywordToken.getToken() == TokenType.KEYWORD_STRING) &&
            (literalToken.getToken() == TokenType.LITERAL_STRING)))) {
                throw new RuntimeException("Different Keyword and Value types");
           }
    }
}
