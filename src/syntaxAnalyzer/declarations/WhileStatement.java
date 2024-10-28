package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenType;

public class WhileStatement extends Declaration {

    private Expression condition;
    private Block body;
    
    
    public Expression getCondition() {
        return condition;
    }
    
    public Block getBody() {
        return body;
    }


    // while (i.Less(10)) loop
    //     i.print()
    //     i := i.Plus(1)
    // end


    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        if (tokens.get(index).getToken() == TokenType.KEYWORD_WHILE) {
            index += 1; // Move past the 'while' keyword
        } else {
            throw new RuntimeException("Expected 'while' keyword, found: " + tokens.get(index).getToken());
        }

        // Expect '(' for condition
        if (tokens.get(index).getToken() == TokenType.PUNCTUATION_LEFT_PARENTHESIS) {
            index += 1; // Move past '('

            condition = new Expression();
            index = condition.parse(tokens, index); // Parse condition
        } else {
            throw new RuntimeException("Expected '(', found: " + tokens.get(index).getToken());
        }

        // Expect 'loop' for true block start
        if (tokens.get(index).getToken() == TokenType.KEYWORD_LOOP) {
            index += 1; // Move past 'loop'
        } else {
            throw new RuntimeException("Expected 'loop', found: " + tokens.get(index).getToken());
        }

        // Parse body (redirect to Block)
        body = new Block();
        index = body.parse(tokens, index) + 1;
        
        // Expect 'end' to finish if statement declaration
        if (tokens.get(index).getToken() == TokenType.KEYWORD_END) {
            return index;
        } else {
            throw new RuntimeException("Expected 'end', found: " + tokens.get(index).getToken());
        }
    }
}
