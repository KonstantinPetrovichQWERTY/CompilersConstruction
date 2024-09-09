import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<Object[]> partsToTokens(List<String> parts){
        List<Object[]> result = new ArrayList<>();

        for (String part : parts){
            Tokens correlatedToken = getToken(part);
            result.add(new Object[]{part, correlatedToken});
        }
        result.add(new Object[]{"", Tokens.EOF});

        result = combineComplexTokens(result);

        return result;
    }
    
    private static List<Object[]> combineComplexTokens(List<Object[]> partsAndTokens){
        List<Object[]> result = new ArrayList<>();

        for(int i=0; i < partsAndTokens.size(); i++){
            
            // tabulation and ":=" handling
            if(
                (i+3 < partsAndTokens.size()) &&
                (partsAndTokens.get(i)[1] == partsAndTokens.get(i+1)[1]) &&
                (partsAndTokens.get(i+1)[1] == partsAndTokens.get(i+2)[1]) &&
                (partsAndTokens.get(i+2)[1] == partsAndTokens.get(i+3)[1]) &&
                (partsAndTokens.get(i+3)[1] == Tokens.PUNCTUATION_SPACE))
            {
                result.add(new Object[]{"\t", Tokens.PUNCTUATION_TABULATION});
                i += 3;
            }
            else if(
                (i+1 < partsAndTokens.size()) &&
                (partsAndTokens.get(i)[1] == Tokens.PUNCTUATION_SEMICOLON) &&
                (partsAndTokens.get(i+1)[1] == Tokens.PUNCTUATION_EQUAL))
            {
                result.add(new Object[]{":=", Tokens.PUNCTUATION_SEMICOLON_EQUAL});
                i += 1;
            }
            else {
                result.add(partsAndTokens.get(i));
            }
        }

        return result;
    }

    private static Tokens getToken(String part) {
        return switch (part) {
            case "class" -> Tokens.KEYWORD_CLASS;
            case "extends" -> Tokens.KEYWORD_EXTENDS;
            case "is" -> Tokens.KEYWORD_IS;
            case "end" -> Tokens.KEYWORD_END;
            case "var" -> Tokens.KEYWORD_VAR;
            case "if" -> Tokens.KEYWORD_IF;
            case "then" -> Tokens.KEYWORD_THEN;
            case "else" -> Tokens.KEYWORD_ELSE;
            case "while" -> Tokens.KEYWORD_WHILE;
            case "loop" -> Tokens.KEYWORD_LOOP;
            case "return" -> Tokens.KEYWORD_RETURN;
            case "method" -> Tokens.KEYWORD_METHOD;
            case "this" -> Tokens.KEYWORD_THIS;
            case "Integer" -> Tokens.KEYWORD_INTEGER;
            case "Real" -> Tokens.KEYWORD_REAL;
            case "String" -> Tokens.KEYWORD_STRING;
            case "Boolean" -> Tokens.KEYWORD_BOOLEAN;
            case "true" -> Tokens.KEYWORD_TRUE;
            case "false" -> Tokens.KEYWORD_FALSE;
            case "Array" -> Tokens.KEYWORD_ARRAY;
            case "List" -> Tokens.KEYWORD_LIST;
            case " " -> Tokens.PUNCTUATION_SPACE;
            case "\n" -> Tokens.PUNCTUATION_LINE_BREAK;
            case "\t" -> Tokens.PUNCTUATION_TABULATION;
            case "\"" -> Tokens.PUNCTUATION_DOUBLE_QUOTE;
            case ":=" -> Tokens.PUNCTUATION_SEMICOLON_EQUAL;
            case ":" -> Tokens.PUNCTUATION_SEMICOLON;
            case "," -> Tokens.PUNCTUATION_COMMA;
            case "=" -> Tokens.PUNCTUATION_EQUAL;
            case "(" -> Tokens.PUNCTUATION_LEFT_PARENTHESIS;
            case ")" -> Tokens.PUNCTUATION_RIGHT_PARENTHESIS;
            case "[" -> Tokens.PUNCTUATION_LEFT_BRACKET;
            case "]" -> Tokens.PUNCTUATION_RIGHT_BRACKET;
            case "." -> Tokens.PUNCTUATION_DOT;
            case "null" -> Tokens.KEYWORD_NULL;
            default -> Tokens.IDENTIFIER;
        };
    }
}
