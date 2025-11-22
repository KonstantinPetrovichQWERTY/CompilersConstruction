package syntaxAnalyzer.declarations;

import java.util.List;
import java.util.Objects;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class Cls extends Declaration {
    String name;
    String baseClass;

    ClsBody body;

    public String getName() {
        return name;
    }

    public String getBaseClass() {
        return baseClass;
    }

    public ClsBody getBody() {
        return body;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.KEYWORD_CLASS) {
            index += 1;
        } else {
            throw new RuntimeException("Expected 'class', found: " + tokens.get(index).getToken());
        }

        if (tokens.get(index).getToken() == TokenType.IDENTIFIER) {
            name = tokens.get(index).getValue();
            index += 1;
        } else {
            throw new RuntimeException("Expected class name (identifier), found: " + tokens.get(index).getToken());
        }

        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.KEYWORD_EXTENDS) {
            index += 1;

            if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.IDENTIFIER){
                baseClass = tokens.get(index).getValue();
                index += 1;
            } else {
                throw new RuntimeException("Expected 'extends', found: " + tokens.get(index).getToken());
            }
        }

        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.KEYWORD_IS) {
            index += 1;
        } else {
            throw new RuntimeException("Expected 'is', found: " + tokens.get(index).getToken());
        }
        
        body = new ClsBody(this);
        index = body.parse(tokens, index);

        if (Objects.requireNonNull(tokens.get(index).getToken()) == TokenType.KEYWORD_END) {
            return index;
        } else {
            throw new RuntimeException("Expected 'end', found: " + tokens.get(index).getToken());
        }
    }
}
