package lexicalanalyzer;

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
    KEYWORD_INTEGER("Integer"),
    KEYWORD_REAL("Real"),
    KEYWORD_STRING("String"),
    KEYWORD_BOOLEAN("Boolean"),
    KEYWORD_TRUE("true"),
    KEYWORD_FALSE("false"),
    KEYWORD_ARRAY("Array"),
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
    LITERAL_INTEGER("literal integer"),
    LITERAL_REAL("literal real"),
    LITERAL_STRING("literal string"),

    // Identifiers
    IDENTIFIER("identifier"),

    // Special tokens
    EOF("eof"), // End of file
    ERROR("error"); // Error token (e.g., unrecognized character)

    private final String lexeme;

    TokenCode(String lexeme) {
        this.lexeme = lexeme;
    }

    public String getLexeme() {
        return this.lexeme;
    }// unrecognized character or malformed token
}
