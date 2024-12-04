package syntaxanalyzer.utils;

import lexicalanalyzer.Token;
import syntaxanalyzer.declarations.Expression;

import java.util.List;
import java.util.Optional;

/**
 * @param identifier The method or field name after the '.'
 * @param arguments  Null if it's a field access, or a list of expressions if it's a method call
 */
public record ExpressionSuffix(Token identifier, Optional<List<Expression>> arguments) {
}
