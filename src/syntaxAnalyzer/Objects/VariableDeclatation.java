package syntaxAnalyzer.Objects;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;
import syntaxAnalyzer.Objects.Object;

public class VariableDeclatation extends Object {

    @Override
    public Integer validate(List<Token> tokens, Integer index) {
        while (true) { 
            System.out.println(tokens.get(index).getValue());
            if (tokens.get(index).getToken() == TokenType.KEYWORD_END || tokens.get(index).getToken() == TokenType.EOF) {
                return index++;
            } 
            index++;
        }
    }

    @Override
    public Integer generate(List<Token> tokens, Integer index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generate'");
    }

}
