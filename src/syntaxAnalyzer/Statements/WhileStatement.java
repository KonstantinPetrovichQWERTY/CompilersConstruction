package syntaxAnalyzer.Statements;

import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;
import syntaxAnalyzer.Block;
import syntaxAnalyzer.Objects.Expression;
import syntaxAnalyzer.SyntaxAnalyzer;

public class WhileStatement extends Statement {
    
    Expression condition;
    Block baseBlock;

    public WhileStatement () {
        
    }

    // class Main is
    //     this() is
    //         var i : Integer(0)
    //         while (i.Less(10)) loop
    //             i.print()
    //             i := i.Plus(1)
    //         end
    //     end
    // end

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        
        if (tokens.get(index).getToken() != TokenType.KEYWORD_WHILE) {
            SyntaxAnalyzer.errorMessage("while stmt 'while'");
        }
        index++;

        if (tokens.get(index).getToken() != TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
            SyntaxAnalyzer.errorMessage("while stmt '('");
        }
        index++;

        Expression tmpCondition = new Expression();
        index = tmpCondition.parse(tokens, index);
        condition = tmpCondition;
        
        if (tokens.get(index).getToken() != TokenType.KEYWORD_LOOP) {
            SyntaxAnalyzer.errorMessage("while stmt 'loop'");
        }

        Block tmpBaseBlock = new Block();
        index = tmpBaseBlock.parse(tokens, index);
        baseBlock = tmpBaseBlock;
        
        if (tokens.get(index).getToken() == TokenType.KEYWORD_END) {
            return index++;
        } else {
            SyntaxAnalyzer.errorMessage("while stmt 'end'");
        }

        return index++;
    }

}
