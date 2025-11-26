package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.SyntaxException;
import syntaxanalyzer.utils.ExpressionSuffix;
import syntaxanalyzer.utils.PrimaryType;

import java.util.ArrayList;
import java.util.Optional;

public class Expression extends Declaration {
    private final Primary primary = new Primary();
    private Optional<List<Expression>> primaryArguments = Optional.empty();
    private final List<ExpressionSuffix> suffixes = new ArrayList<>();

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        int current = primary.parse(tokens, index) + 1;

        if (current < tokens.size()
                && tokens.get(current).getToken() == TokenCode.PUNCTUATION_LEFT_PARENTHESIS) {
            if (allowsCall(primary.getPrimaryType())) {
                List<Expression> args = new ArrayList<>();
                current = parseArgumentList(tokens, current, args) + 1; // move past ')'
                primaryArguments = Optional.of(args);
            } else {
                throw SyntaxException.at("Literals cannot be followed by an argument list", tokens.get(current));
            }
        }

        while (current < tokens.size()
                && tokens.get(current).getToken() == TokenCode.PUNCTUATION_DOT) {
            current++;
            Token name = ensureToken(tokens, current++, TokenCode.IDENTIFIER);
            Optional<List<Expression>> expressions = Optional.empty();
            if (current < tokens.size()
                    && tokens.get(current).getToken() == TokenCode.PUNCTUATION_LEFT_PARENTHESIS) {
                List<Expression> args = new ArrayList<>();
                current = parseArgumentList(tokens, current, args) + 1; // move past ')'
                expressions = Optional.of(args);
            }

            suffixes.add(new ExpressionSuffix(name, expressions));
        }
        return current - 1;
    }

    private boolean allowsCall(PrimaryType type) {
        return type == PrimaryType.Identifier || type == PrimaryType.This || type == PrimaryType.ClassName;
    }

    private int parseArgumentList(List<Token> tokens, int index, List<Expression> output) {
        ensureToken(tokens, index++, TokenCode.PUNCTUATION_LEFT_PARENTHESIS);
        if (tokens.get(index).getToken() == TokenCode.PUNCTUATION_RIGHT_PARENTHESIS) {
            return index; // empty argument list, return position of ')'
        }

        while (index < tokens.size()) {
            Expression expression = new Expression();
            index = expression.parse(tokens, index);
            output.add(expression);
            index++;
            TokenCode separator = tokens.get(index).getToken();
            if (separator == TokenCode.PUNCTUATION_COMMA) {
                index++;
                continue;
            }
            ensureToken(tokens, index, TokenCode.PUNCTUATION_RIGHT_PARENTHESIS);
            return index;
        }
        Token last = index > 0 ? tokens.get(index - 1) : tokens.get(0);
        throw SyntaxException.at("Unclosed argument list", last);
    }

    public Primary getPrimary() {
        return primary;
    }

    public Optional<List<Expression>> getPrimaryArguments() {
        return primaryArguments;
    }

    public List<ExpressionSuffix> getSuffixes() {
        return suffixes;
    }
}
