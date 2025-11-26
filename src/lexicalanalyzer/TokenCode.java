package lexicalanalyzer;

import jakarta.annotation.Nullable;

public enum TokenCode {
    // Keywords
    KEYWORD_CLASS("class"),
    KEYWORD_EXTENDS("extends"), 
    KEYWORD_IS("is"),
    KEYWORD_END("end"),
    KEYWORD_VAR("var"),
    KEYWORD_IF("if"),
    KEYWORD_THEN("then"),
    KEYWORD_ELSE("else"),
    KEYWORD_WHILE("while"),
    KEYWORD_LOOP("loop"),
    KEYWORD_RETURN("return"),
    KEYWORD_METHOD("method"),
    KEYWORD_THIS("this"),
    KEYWORD_TRUE("true"),
    KEYWORD_FALSE("false"),
    KEYWORD_LIST("List"),
    KEYWORD_NULL("null"),

    // Punctuation
    PUNCTUATION_SPACE(" "),
    PUNCTUATION_LINE_BREAK("\n"),
    PUNCTUATION_TABULATION("\t"),
    PUNCTUATION_DOUBLE_QUOTE("\""),
    PUNCTUATION_SEMICOLON(":"),
    PUNCTUATION_COMMA(","),
    PUNCTUATION_EQUAL("="),
    PUNCTUATION_LEFT_PARENTHESIS("("), // (
    PUNCTUATION_RIGHT_PARENTHESIS(")"), // )
    PUNCTUATION_LEFT_BRACKET("["), // [
    PUNCTUATION_RIGHT_BRACKET("]"), // ]
    PUNCTUATION_DOT("."),
    PUNCTUATION_SEMICOLON_EQUAL(":="),

    // Literals
    LITERAL_INTEGER(null),
    LITERAL_REAL(null),
    LITERAL_STRING(null),

    // Identifiers
    IDENTIFIER(null),

    // Special tokens
    EOF(null), // End of file
    ERROR(null); // Error token (e.g., unrecognized character)

    @Nullable
    private final String lexeme;

    TokenCode(String lexeme) {
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return this.lexeme;
    }// unrecognized character or malformed token
}
