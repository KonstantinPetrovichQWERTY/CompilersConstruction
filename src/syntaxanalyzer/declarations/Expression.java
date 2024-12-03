package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

import java.util.ArrayList;

public class Expression extends Declaration {
    private Primary primary;
    private List<ExpressionSuffix> suffixes;

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        primary = new Primary();
        index = primary.parse(tokens, index);
        index++;

        suffixes = new ArrayList<>();
        if (!(index < tokens.size() && tokens.get(index).getToken() == TokenType.PUNCTUATION_DOT)) {
            return index;
        }
        while (index < tokens.size() && tokens.get(index).getToken() == TokenType.PUNCTUATION_DOT) {
            index++; // Move past '.'

            if (index >= tokens.size()) {
                throw new RuntimeException("Expected identifier after '.', but reached end of tokens");
            }

            Token currentToken = tokens.get(index);
            if (currentToken.getToken() == TokenType.IDENTIFIER) {
                Token identifier = currentToken;
                index++; // Move past identifier

                List<Expression> arguments = null;

                // Check if the next token is '(', indicating a method call
                if (index < tokens.size() && tokens.get(index).getToken() == TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
                    index++; // Move past '('

                    arguments = new ArrayList<>();

                    // Check if the next token is ')', indicating no arguments
                    if (index < tokens.size() && tokens.get(index).getToken() != TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
                        while (true) {
                            Expression argExpr = new Expression();
                            index = argExpr.parse(tokens, index);
                            arguments.add(argExpr);

                            if (index < tokens.size() && tokens.get(index).getToken() == TokenType.PUNCTUATION_COMMA) {
                                index++; // Move past ','
                            } else {
                                break;
                            }
                        }
                    }

                    // Expect ')'
                    if (index < tokens.size() && tokens.get(index).getToken() == TokenType.PUNCTUATION_RIGHT_PARENTHESIS) {
                        index++; // Move past ')'
                    } else {
                        throw new RuntimeException("Expected ')' after arguments in method call, found: " + tokens.get(index).getToken());
                    }
                }

                // Add the parsed suffix (method call or field access)
                ExpressionSuffix suffix = new ExpressionSuffix(identifier, arguments);
                suffixes.add(suffix);
            } else {
                throw new RuntimeException("Expected identifier after '.', found: " + currentToken.getToken());
            }
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
