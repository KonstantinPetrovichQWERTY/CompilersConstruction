package syntaxanalyzer.declarations;

import java.util.List;
import java.util.Objects;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class Cls extends Declaration {
    String name;
    String baseClass;

    ClsBody body;

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.KEYWORD_CLASS) {
            index += 1;
        } else {
            throw new RuntimeException("Unexpected token: " + tokens.get(index).getToken());
        }

        if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
            name = tokens.get(index).getToken().getValue();
            index += 1;
        } else {
            throw new RuntimeException("Unexpected token: " + tokens.get(index).getToken());
        }

        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.KEYWORD_EXTENDS) {
            index += 1;

            if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.IDENTIFIER){
                baseClass = tokens.get(index).getToken().getValue();
                index += 1;
            } else {
                throw new RuntimeException("Unexpected token: " + tokens.get(index).getToken());
            }
        }

        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.KEYWORD_IS) {
            body = new ClsBody();
            index = body.parse(tokens, index);
        }

        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.KEYWORD_END) {
            index += 1;
        }

        return index;
    }
}
