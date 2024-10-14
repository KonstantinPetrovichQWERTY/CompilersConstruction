package syntaxAnalyzer.Statements;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;
import syntaxAnalyzer.Block;
import syntaxAnalyzer.Objects.Expression;
import syntaxAnalyzer.SyntaxAnalyzer;


public class IfStatement extends Statement {
    
    Expression condition;
    Block baseBlock;
    Block elseBlock;
 
    
    public IfStatement () {

    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        
        if (tokens.get(index).getToken() != TokenType.KEYWORD_IF) {
            SyntaxAnalyzer.errorMessage("if stmt 'if'");
        }
        index++;

        if (tokens.get(index).getToken() != TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
            SyntaxAnalyzer.errorMessage("if stmt '('");
        }
        index++;

        Expression tmpCondition = new Expression();
        index = tmpCondition.parse(tokens, index);
        condition = tmpCondition;
        
        if (tokens.get(index).getToken() != TokenType.KEYWORD_THEN) {
            SyntaxAnalyzer.errorMessage("if stmt 'then'");
        }

        Block tmpBaseBlock = new Block();
        index = tmpBaseBlock.parse(tokens, index);
        baseBlock = tmpBaseBlock;
        
        if (tokens.get(index).getToken() == TokenType.KEYWORD_ELSE) {
            Block tmpElseBlock = new Block();
            index++;
            index = tmpElseBlock.parse(tokens, index);
            baseBlock = tmpElseBlock;
        } else if (tokens.get(index).getToken() == TokenType.KEYWORD_END) {
            return index++;
        } else {
            SyntaxAnalyzer.errorMessage("if stmt 'else'");
        }

        return index++;
    }

}
