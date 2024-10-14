package syntaxAnalyzer;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class Block extends Node{

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        while (true) { 
            System.out.println(tokens.get(index).getValue() + " Block");
            if ((tokens.get(index).getToken() == TokenType.KEYWORD_END) || 
            (tokens.get(index).getToken() == TokenType.KEYWORD_ELSE)) {
                return index++;
            } 
            index++;
        }    
    }
}
