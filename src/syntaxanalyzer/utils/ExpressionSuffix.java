package syntaxanalyzer.declarations;

import lexicalanalyzer.Token;

import java.util.List;

/**
 * @param identifier The method or field name after the '.'
 * @param arguments  Null if it's a field access, or a list of expressions if it's a method call
 */
public record ExpressionSuffix(Token identifier, List<Expression> arguments) {
}
