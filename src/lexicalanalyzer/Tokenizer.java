package lexicalanalyzer;

import java.util.ArrayList;
import java.util.List;

public class Tokenizer {

    public static List<Token> partsToTokens(List<LexicalFragment> fragments, Token.Span eofSpan) {
        List<Token> tokens = new ArrayList<>();
        for (LexicalFragment fragment : fragments) {
            tokens.add(getTokenByFragment(fragment));
        }
        tokens.add(new Token(TokenCode.EOF, eofSpan));
        return combineComplexTokens(tokens);
    }

    private static List<Token> combineComplexTokens(List<Token> tokens) {
        List<Token> result = new ArrayList<>();
        int index = 0;

        while (index < tokens.size()) {
            Token current = tokens.get(index);

            if (isTabSequence(tokens, index)) {
                result.add(new Token(TokenCode.PUNCTUATION_TABULATION, current.getSpan()));
                index += 4;
                continue;
            }

            if (isAssignmentSequence(tokens, index)) {
                result.add(new Token(TokenCode.PUNCTUATION_SEMICOLON_EQUAL, current.getSpan()));
                index += 2;
                continue;
            }

            if (isRealLiteralSequence(tokens, index)) {
                Token integerToken = tokens.get(index);
                Token dotToken = tokens.get(index + 1);
                Token fractionalToken = tokens.get(index + 2);
                String literal = integerToken.getLexeme() + dotToken.getLexeme() + fractionalToken.getLexeme();
                result.add(new RealLiteralToken(Double.parseDouble(literal), integerToken.getSpan()));
                index += 3;
                continue;
            }

            if (current.getToken() == TokenCode.PUNCTUATION_DOUBLE_QUOTE) {
                Token.Span literalSpan = current.getSpan();
                int j = index + 1;
                StringBuilder literal = new StringBuilder();

                while (j < tokens.size()) {
                    Token candidate = tokens.get(j);
                    if (candidate.getToken() == TokenCode.EOF) {
                        result.add(new ErrorToken("String does not end", literalSpan));
                        return result;
                    }
                    if (candidate.getToken() == TokenCode.PUNCTUATION_DOUBLE_QUOTE) {
                        result.add(new StringLiteralToken(literal.toString(), literalSpan));
                        index = j + 1;
                        break;
                    }
                    literal.append(candidate.getLexeme());
                    j++;
                }

                if (index != j + 1) {
                    result.add(new ErrorToken("String does not end", literalSpan));
                    return result;
                }

                continue;
            }

            result.add(current);
            index++;
        }

        return result;
    }

    private static boolean isTabSequence(List<Token> tokens, int index) {
        if (index + 3 >= tokens.size()) {
            return false;
        }
        return tokens.get(index).getToken() == TokenCode.PUNCTUATION_SPACE
                && tokens.get(index + 1).getToken() == TokenCode.PUNCTUATION_SPACE
                && tokens.get(index + 2).getToken() == TokenCode.PUNCTUATION_SPACE
                && tokens.get(index + 3).getToken() == TokenCode.PUNCTUATION_SPACE;
    }

    private static boolean isAssignmentSequence(List<Token> tokens, int index) {
        return index + 1 < tokens.size()
                && tokens.get(index).getToken() == TokenCode.PUNCTUATION_SEMICOLON
                && tokens.get(index + 1).getToken() == TokenCode.PUNCTUATION_EQUAL;
    }

    private static boolean isRealLiteralSequence(List<Token> tokens, int index) {
        return index + 2 < tokens.size()
                && tokens.get(index).getToken() == TokenCode.LITERAL_INTEGER
                && tokens.get(index + 1).getToken() == TokenCode.PUNCTUATION_DOT
                && tokens.get(index + 2).getToken() == TokenCode.LITERAL_INTEGER;
    }

    private static Token getTokenByFragment(LexicalFragment fragment) {
        String word = fragment.getContent();
        Token.Span span = fragment.getSpan();

        if (word.matches("\\d+")) {
            return new IntegerLiteralToken(Integer.parseInt(word), span);
        }

        return switch (word) {
            case "class" -> new Token(TokenCode.KEYWORD_CLASS, span);
            case "extends" -> new Token(TokenCode.KEYWORD_EXTENDS, span);
            case "is" -> new Token(TokenCode.KEYWORD_IS, span);
            case "end" -> new Token(TokenCode.KEYWORD_END, span);
            case "var" -> new Token(TokenCode.KEYWORD_VAR, span);
            case "if" -> new Token(TokenCode.KEYWORD_IF, span);
            case "then" -> new Token(TokenCode.KEYWORD_THEN, span);
            case "else" -> new Token(TokenCode.KEYWORD_ELSE, span);
            case "while" -> new Token(TokenCode.KEYWORD_WHILE, span);
            case "loop" -> new Token(TokenCode.KEYWORD_LOOP, span);
            case "return" -> new Token(TokenCode.KEYWORD_RETURN, span);
            case "method" -> new Token(TokenCode.KEYWORD_METHOD, span);
            case "this" -> new Token(TokenCode.KEYWORD_THIS, span);
            case "true" -> new Token(TokenCode.KEYWORD_TRUE, span);
            case "false" -> new Token(TokenCode.KEYWORD_FALSE, span);
            case "List" -> new Token(TokenCode.KEYWORD_LIST, span);
            case " " -> new Token(TokenCode.PUNCTUATION_SPACE, span);
            case "\n" -> new Token(TokenCode.PUNCTUATION_LINE_BREAK, span);
            case "\t" -> new Token(TokenCode.PUNCTUATION_TABULATION, span);
            case "\"" -> new Token(TokenCode.PUNCTUATION_DOUBLE_QUOTE, span);
            case ":=" -> new Token(TokenCode.PUNCTUATION_SEMICOLON_EQUAL, span);
            case ":" -> new Token(TokenCode.PUNCTUATION_SEMICOLON, span);
            case "," -> new Token(TokenCode.PUNCTUATION_COMMA, span);
            case "=" -> new Token(TokenCode.PUNCTUATION_EQUAL, span);
            case "(" -> new Token(TokenCode.PUNCTUATION_LEFT_PARENTHESIS, span);
            case ")" -> new Token(TokenCode.PUNCTUATION_RIGHT_PARENTHESIS, span);
            case "[" -> new Token(TokenCode.PUNCTUATION_LEFT_BRACKET, span);
            case "]" -> new Token(TokenCode.PUNCTUATION_RIGHT_BRACKET, span);
            case "." -> new Token(TokenCode.PUNCTUATION_DOT, span);
            case "null" -> new Token(TokenCode.KEYWORD_NULL, span);
            default -> new IdentifierToken(word, span);
        };
    }
}
