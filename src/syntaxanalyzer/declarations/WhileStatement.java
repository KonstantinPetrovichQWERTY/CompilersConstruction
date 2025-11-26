package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import syntaxanalyzer.SyntaxException;

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
        Token current = tokens.get(index);
        if (current.getToken() == TokenCode.KEYWORD_WHILE) {
            index += 1; // Move past the 'while' keyword
        } else {
            throw SyntaxException.at("Expected 'while' keyword, found: " + current.getToken(), current);
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

        // Consume trailing ')' and expect 'loop'
        current = tokens.get(index);
        if (current.getToken() == TokenCode.PUNCTUATION_RIGHT_PARENTHESIS) {
            index += 1;
        }

        current = tokens.get(index);
        if (current.getToken() == TokenCode.KEYWORD_LOOP) {
            index += 1; // Move past 'loop'
        } else {
            throw SyntaxException.at("Expected 'loop', found: " + current.getToken(), current);
        }

        // Parse body (redirect to Block)
        body = new Block();
        index = body.parse(tokens, index) + 1; // TODO: Нужен ли +1?
        
        // Expect 'end' to finish if statement declaration
        current = tokens.get(index);
        if (current.getToken() == TokenCode.KEYWORD_END) {
            return index;
        } else {
            throw SyntaxException.at("Expected 'end', found: " + current.getToken(), current);
        }
    }
}
