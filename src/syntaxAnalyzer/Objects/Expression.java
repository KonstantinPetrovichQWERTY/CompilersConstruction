package syntaxAnalyzer.Objects;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;
import syntaxAnalyzer.Node;


public class Expression extends Node{

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        while (true) { 
            System.out.println(tokens.get(index).getValue() + " expression");
            if ((tokens.get(index).getToken() == TokenType.KEYWORD_END) || (tokens.get(index).getToken() == TokenType.EOF) ||
            (tokens.get(index).getToken() == TokenType.KEYWORD_THEN) || (tokens.get(index).getToken() == TokenType.KEYWORD_LOOP)) {
                return index++;
            } 
            index++;
        }
    }

}
