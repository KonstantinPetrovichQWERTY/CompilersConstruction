package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;
import syntaxanalyzer.utils.ExpressionSuffix;

import java.util.ArrayList;
import java.util.Optional;

public class Expression extends Declaration {
    private final Primary primary = new Primary();
    private final List<ExpressionSuffix> suffixes = new ArrayList<>();

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        index = primary.parse(tokens, index) + 1;
        while (tokens.get(index).getToken() == TokenType.PUNCTUATION_DOT) {
            index++;
            Token name = ensureToken(tokens, index++, TokenType.IDENTIFIER);
            Optional<List<Expression>> expressions = Optional.empty();
            if (tokens.get(index).getToken() == TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
                index++;
                expressions = Optional.of(new ArrayList<>());
                while (tokens.get(index).getToken() != TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
                    Expression expression = new Expression();
                    index = expression.parse(tokens, index);
                    expressions.get().add(expression);
                    index++;
                    if (tokens.get(index).getToken() == TokenType.PUNCTUATION_COMMA) {
                        index++;
                    }
                }
                index++;
            }

            suffixes.add(new ExpressionSuffix(name, expressions));

        }
        return index-1;
    }

    public Primary getPrimary() {
        return primary;
    }

    public List<ExpressionSuffix> getSuffixes() {
        return suffixes;
    }
}
