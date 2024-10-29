package syntaxanalyzer.declarations;

import lexicalanalyzer.Token;

import java.util.List;

public class ExpressionSuffix {
    private Token identifier; // The method or field name after the '.'
    private List<Expression> arguments; // Null if it's a field access, or a list of expressions if it's a method call

    public ExpressionSuffix(Token identifier, List<Expression> arguments) {
        this.identifier = identifier;
        this.arguments = arguments;
    }

    public Token getIdentifier() {
        return identifier;
    }

    public List<Expression> getArguments() {
        return arguments;
    }
}
