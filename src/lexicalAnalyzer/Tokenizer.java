package lexicalAnalyzer;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<Object[]> partsToTokens(List<String> parts){
        List<Object[]> result = new ArrayList<>();

        for (String part : parts){
            Token correlatedToken = getTokenByString(part);
            result.add(new Object[]{part, correlatedToken});
        }
        result.add(new Object[]{"", new Token(TokenType.EOF)});

        result = combineComplexTokens(result);

        return result;
    }
    
    private static List<Object[]> combineComplexTokens(List<Object[]> partsAndTokens){
        List<Object[]> result = new ArrayList<>();

        for(int i=0; i < partsAndTokens.size(); i++){
            
            // TODO: change existing handlers
            // TODO: add literal converters
            // tabulation and ":=" handling
            if(
                (i+3 < partsAndTokens.size()) &&
                (partsAndTokens.get(i)[1] == partsAndTokens.get(i+1)[1]) &&
                (partsAndTokens.get(i+1)[1] == partsAndTokens.get(i+2)[1]) &&
                (partsAndTokens.get(i+2)[1] == partsAndTokens.get(i+3)[1]) &&
                (partsAndTokens.get(i+3)[1] == TokenType.PUNCTUATION_SPACE))
            {
                result.add(new Object[]{"\t", TokenType.PUNCTUATION_TABULATION});
                i += 3;
            }
            else if(
                (i+1 < partsAndTokens.size()) &&
                (partsAndTokens.get(i)[1] == TokenType.PUNCTUATION_SEMICOLON) &&
                (partsAndTokens.get(i+1)[1] == TokenType.PUNCTUATION_EQUAL))
            {
                result.add(new Object[]{":=", TokenType.PUNCTUATION_SEMICOLON_EQUAL});
                i += 1;
            }
            else {
                result.add(partsAndTokens.get(i));
            }
        }

        return result;
    }

    private static Token getTokenByString(String part) {
        return switch (part) {
            case "class" -> new Token(TokenType.KEYWORD_CLASS);
            case "extends" -> new Token(TokenType.KEYWORD_EXTENDS);
            case "is" -> new Token(TokenType.KEYWORD_IS);
            case "end" -> new Token(TokenType.KEYWORD_END);
            case "var" -> new Token(TokenType.KEYWORD_VAR);
            case "if" -> new Token(TokenType.KEYWORD_IF);
            case "then" -> new Token(TokenType.KEYWORD_THEN);
            case "else" -> new Token(TokenType.KEYWORD_ELSE);
            case "while" -> new Token(TokenType.KEYWORD_WHILE);
            case "loop" -> new Token(TokenType.KEYWORD_LOOP);
            case "return" -> new Token(TokenType.KEYWORD_RETURN);
            case "method" -> new Token(TokenType.KEYWORD_METHOD);
            case "this" -> new Token(TokenType.KEYWORD_THIS);
            case "Integer" -> new Token(TokenType.KEYWORD_INTEGER);
            case "Real" -> new Token(TokenType.KEYWORD_REAL);
            case "String" -> new Token(TokenType.KEYWORD_STRING);
            case "Boolean" -> new Token(TokenType.KEYWORD_BOOLEAN);
            case "true" -> new Token(TokenType.KEYWORD_TRUE);
            case "false" -> new Token(TokenType.KEYWORD_FALSE);
            case "Array" -> new Token(TokenType.KEYWORD_ARRAY);
            case "List" -> new Token(TokenType.KEYWORD_LIST);
            case " " -> new Token(TokenType.PUNCTUATION_SPACE);
            case "\n" -> new Token(TokenType.PUNCTUATION_LINE_BREAK);
            case "\t" -> new Token(TokenType.PUNCTUATION_TABULATION);
            case "\"" -> new Token(TokenType.PUNCTUATION_DOUBLE_QUOTE);
            case ":=" -> new Token(TokenType.PUNCTUATION_SEMICOLON_EQUAL);
            case ":" -> new Token(TokenType.PUNCTUATION_SEMICOLON);
            case "," -> new Token(TokenType.PUNCTUATION_COMMA);
            case "=" -> new Token(TokenType.PUNCTUATION_EQUAL);
            case "(" -> new Token(TokenType.PUNCTUATION_LEFT_PARENTHESIS);
            case ")" -> new Token(TokenType.PUNCTUATION_RIGHT_PARENTHESIS);
            case "[" -> new Token(TokenType.PUNCTUATION_LEFT_BRACKET);
            case "]" -> new Token(TokenType.PUNCTUATION_RIGHT_BRACKET);
            case "." -> new Token(TokenType.PUNCTUATION_DOT);
            case "null" -> new Token(TokenType.KEYWORD_NULL);
            default -> new Token(TokenType.IDENTIFIER);
        };
    }
}
