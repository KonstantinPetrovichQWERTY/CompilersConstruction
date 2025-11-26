package syntaxanalyzer.declarations;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.SyntaxException;
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
        TokenCode code = currentToken.getToken();

        switch (code) {
            case LITERAL_INTEGER -> {
                primaryType = PrimaryType.IntegerLiteral;
                valueToken = currentToken;
            }
            case LITERAL_REAL -> {
                primaryType = PrimaryType.RealLiteral;
                valueToken = currentToken;
            }
            case LITERAL_STRING -> {
                primaryType = PrimaryType.StringLiteral;
                valueToken = currentToken;
            }
            case KEYWORD_TRUE, KEYWORD_FALSE -> {
                primaryType = PrimaryType.BooleanLiteral;
                valueToken = currentToken;
            }
            case KEYWORD_THIS -> {
                primaryType = PrimaryType.This;
                valueToken = currentToken;
            }
            case IDENTIFIER, KEYWORD_LIST -> {
                // Class names are lexically identifiers; keep them as Identifier primary.
                primaryType = PrimaryType.Identifier;
                valueToken = currentToken;
                return consumeGenericPart(tokens, index);
            }
            default -> throw SyntaxException.at(
                    "Unexpected token in primary: " + code,
                    currentToken
            );
        }

        return index;
    }

    public Token getValueToken() {
        return valueToken;
    }

    public PrimaryType getPrimaryType() {
        return primaryType;
    }

    private Integer consumeGenericPart(List<Token> tokens, Integer index) {
        int current = index;
        if (current + 1 < tokens.size()
                && tokens.get(current + 1).getToken() == TokenCode.PUNCTUATION_LEFT_BRACKET) {
            int depth = 1;
            current += 2; // Skip identifier and initial '['
            while (current < tokens.size() && depth > 0) {
                TokenCode code = tokens.get(current).getToken();
                if (code == TokenCode.PUNCTUATION_LEFT_BRACKET) {
                    depth += 1;
                } else if (code == TokenCode.PUNCTUATION_RIGHT_BRACKET) {
                    depth -= 1;
                }
                current += 1;
            }
            if (depth != 0) {
                Token last = current > 0 ? tokens.get(current - 1) : tokens.get(index);
                throw SyntaxException.at("Unclosed generic argument list in primary", last);
            }
            return current - 1; // Position of the closing ']'
        }
        return current;
    }
}
