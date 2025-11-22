package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class IfStatement extends Declaration {

    private Expression condition;
    private Block trueBlock;
    private Block falseBlock;
    
    
    public Expression getCondition() {
        return condition;
    }
    
    public Block getFalseBlock() {
        return falseBlock;
    }

    public Block getTrueBlock() {
        return trueBlock;
    }

    // if (trueCondition) then
    //     String("test if true right").print()
    // else
    //     String("test if true wrong").print()
    // end

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.KEYWORD_IF) {
            index += 1; // Move past the 'if' keyword
        } else {
            throw new RuntimeException("Expected 'if' keyword, found: " + tokens.get(index).getToken());
        }

        // Expect '(' for condition
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
            index += 1; // Move past '('

            condition = new Expression();
            index = condition.parse(tokens, index); // Parse condition

            index += 1; // Move past ')'
        } else {
            throw new RuntimeException("Expected '(', found: " + tokens.get(index).getToken());
        }

        index++;
        // Expect 'then' for true block start
        if (tokens.get(index).getToken() == TokenType.KEYWORD_THEN) {
            index += 1; // Move past 'then'
        } else {
            throw new RuntimeException("Expected 'then', found: " + tokens.get(index).getToken());
        }

        // Parse trueBlock (redirect to Block)
        trueBlock = new Block();
        index = trueBlock.parse(tokens, index) + 1; // TODO: Нужен ли тут +1?

        // Expect 'else' for else block start (OPTIONAL)
        if (tokens.get(index).getToken() == TokenType.KEYWORD_ELSE) {
            index += 1; // Move past 'else'

            // Parse falseBlock (redirect to Block)
            falseBlock = new Block();
            index = falseBlock.parse(tokens, index);

        }

        // Expect 'end' to finish if statement declaration
        if (tokens.get(index).getToken() == TokenType.KEYWORD_END) {
            return index;
        } else {
            throw new RuntimeException("Expected 'end', found: " + tokens.get(index).getToken());
        }
    }
}
