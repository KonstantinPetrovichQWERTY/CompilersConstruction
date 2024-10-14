package syntaxAnalyzer.Functions;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;
import syntaxAnalyzer.Statements.IfStatement;
import syntaxAnalyzer.Statements.WhileStatement;

public class ConstructorDeclaration extends Function {

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        while (true) { 
            System.out.println(tokens.get(index).getValue() + " construct");
            if (tokens.get(index).getToken() == TokenType.KEYWORD_END || tokens.get(index).getToken() == TokenType.EOF) {
                return index++;
            }
            
            if (tokens.get(index).getToken() == TokenType.KEYWORD_IF) {
                IfStatement stmt = new IfStatement();
                index = stmt.parse(tokens, index);
            }

            if (tokens.get(index).getToken() == TokenType.KEYWORD_WHILE) {
                WhileStatement stmt = new WhileStatement();
                index = stmt.parse(tokens, index);
            }
            index++;
        }
    }


}
