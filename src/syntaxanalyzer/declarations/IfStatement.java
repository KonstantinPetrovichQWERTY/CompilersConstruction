package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.SyntaxException;

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
        Token current = tokens.get(index);
        if (current.getToken() == TokenCode.KEYWORD_IF) {
            index += 1; // Move past the 'if' keyword
        } else {
            throw SyntaxException.at("Expected 'if' keyword, found: " + current.getToken(), current);
        }

        // Expect '(' for condition
        current = tokens.get(index);
        if (current.getToken() == TokenCode.PUNCTUATION_LEFT_PARENTHESIS) {
            index += 1; // Move past '('

            condition = new Expression();
            index = condition.parse(tokens, index); // Parse condition

            index += 1; // Move past ')'
        } else {
            throw SyntaxException.at("Expected '(', found: " + current.getToken(), current);
        }

        index++;
        // Expect 'then' for true block start
        current = tokens.get(index);
        if (current.getToken() == TokenCode.KEYWORD_THEN) {
            index += 1; // Move past 'then'
        } else {
            throw SyntaxException.at("Expected 'then', found: " + current.getToken(), current);
        }

        // Parse trueBlock (redirect to Block)
        trueBlock = new Block();
        index = trueBlock.parse(tokens, index) + 1; // TODO: Нужен ли тут +1?

        // Expect 'else' for else block start (OPTIONAL)
        current = tokens.get(index);
        if (current.getToken() == TokenCode.KEYWORD_ELSE) {
            index += 1; // Move past 'else'

            // Parse falseBlock (redirect to Block)
            falseBlock = new Block();
            index = falseBlock.parse(tokens, index);

        }

        // Expect 'end' to finish if statement declaration
        current = tokens.get(index);
        if (current.getToken() == TokenCode.KEYWORD_END) {
            return index;
        } else {
            throw SyntaxException.at("Expected 'end', found: " + current.getToken(), current);
        }
    }
}
