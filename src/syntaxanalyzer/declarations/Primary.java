package syntaxanalyzer.declarations;

import lexicalanalyzer.TokenType;
import lexicalanalyzer.Token;
import syntaxanalyzer.utils.PrimaryType;

import java.util.List;

public class Primary extends Declaration {

    private PrimaryType primaryType;

    /**
     * Contain the only parsed Token.
     * By this token you could get a corresponding value
     * **/
    private Token valueToken;

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        Token currentToken = tokens.get(index);

        if (currentToken.getToken() == TokenType.LITERAL_INTEGER) {
            primaryType = PrimaryType.IntegerLiteral;
            valueToken = currentToken;
        }

        else if (currentToken.getToken() == TokenType.LITERAL_STRING) {
            primaryType = PrimaryType.StringLiteral;
            valueToken = currentToken;
        }

        else if (currentToken.getToken() == TokenType.LITERAL_REAL) {
            primaryType = PrimaryType.RealLiteral;
            valueToken = currentToken;
        }

        else if (currentToken.getToken() == TokenType.IDENTIFIER) {
            primaryType = PrimaryType.ClassName;
            valueToken = currentToken;
        }

        else if (currentToken.getToken() == TokenType.KEYWORD_THIS)
        {
            primaryType = PrimaryType.This;
            valueToken = currentToken;
        }


        return index;
    }

    public Token getValueToken() {
        return valueToken;
    }

    public PrimaryType getPrimaryType() {
        return primaryType;
    }
}
