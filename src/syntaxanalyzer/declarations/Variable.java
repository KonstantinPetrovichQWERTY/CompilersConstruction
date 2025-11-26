package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;

public class Variable extends Declaration {
    private String name;

    private Expression expression;

    public String getName() {
        return name;
    }


    public Expression getExpression() {
        return expression;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        ensureToken(tokens, index, TokenCode.KEYWORD_VAR);
        index++;


        Token identifier = ensureToken(tokens, index, TokenCode.IDENTIFIER);
        name = identifier.getLexeme();
        index++;

        ensureToken(tokens, index, TokenCode.PUNCTUATION_SEMICOLON);
        index++;

        expression = new Expression();
        index = expression.parse(tokens, index);


        return index; // Return the updated index
    }


}
