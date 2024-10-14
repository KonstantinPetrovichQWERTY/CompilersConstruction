package syntaxAnalyzer.Functions;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class ConstructorDeclaration extends Function {

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

    // TODO: generate()
    @Override
    public Integer generate(List<Token> tokens, Integer index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

}
