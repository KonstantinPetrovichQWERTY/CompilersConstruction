package lexicalanalyzer;
import java.util.ArrayList;
import java.util.List;

public class Tokenizer {
    public static List<Token> partsToTokens(List<String> parts){
        List<Token> result = new ArrayList<>();

        for (String part : parts){
            Token correlatedToken = getTokenByString(part);
            result.add(correlatedToken);
        }
        result.add(new Token(TokenType.EOF));

        result = combineComplexTokens(result);

        return result;
    }
    
    private static List<Token> combineComplexTokens(List<Token> partsAndTokens){
        List<Token> result = new ArrayList<>();

        for(int i=0; i < partsAndTokens.size(); i++){
            
            // tabulation case handling
            if((i+3 < partsAndTokens.size()) && (partsAndTokens.get(i).getToken() == TokenType.PUNCTUATION_SPACE))
            {
                Token tokenClass1 = partsAndTokens.get(i+1);
                Token tokenClass2 = partsAndTokens.get(i+2);
                Token tokenClass3 = partsAndTokens.get(i+3);

                if((tokenClass1.getToken() == TokenType.PUNCTUATION_SPACE) &&
                (tokenClass2.getToken() == TokenType.PUNCTUATION_SPACE) &&
                (tokenClass3.getToken() == TokenType.PUNCTUATION_SPACE))
                {
                    result.add(new Token(TokenType.PUNCTUATION_TABULATION));
                    i += 3;
                    continue;
                }
            }

            // ":=" case handling
            if((i+1 < partsAndTokens.size()) && (partsAndTokens.get(i).getToken() == TokenType.PUNCTUATION_SEMICOLON))
            {
                Token tokenClass1 = partsAndTokens.get(i+1);

                if(tokenClass1.getToken() == TokenType.PUNCTUATION_EQUAL)
                {
                    result.add(new Token(TokenType.PUNCTUATION_SEMICOLON_EQUAL));
                    i += 1;
                    continue;
                }
            }
            
            // Real number literals handling
            if((i+2 < partsAndTokens.size()) && (partsAndTokens.get(i).getToken() == TokenType.LITERAL_INTEGER))
            {
                
                Token tokenClass = partsAndTokens.get(i);
                Token tokenClass1 = partsAndTokens.get(i+1);
                Token tokenClass2 = partsAndTokens.get(i+2);
                
                String value = tokenClass.getValue();
                String value1 = tokenClass1.getValue();
                String value2 = tokenClass2.getValue();

                if ((tokenClass1.getToken() == TokenType.PUNCTUATION_DOT) &&
                (tokenClass2.getToken() == TokenType.LITERAL_INTEGER))
                {
                    String concatenatedString = value + value1 + value2;
                    result.add(new RealLiteralToken(Double.parseDouble(concatenatedString)));
                    i += 2;
                    continue;
                }
            }

            // String literals handling
            if(partsAndTokens.get(i).getToken() == TokenType.PUNCTUATION_DOUBLE_QUOTE)
            {
                int j = i + 1;

                String resValue = "";

                while (j < partsAndTokens.size())
                {
                    Token tempTokenClass = partsAndTokens.get(j);
                    String tempValue = tempTokenClass.getValue();

                    j += 1;

                    if (tempTokenClass.getToken() == TokenType.EOF)
                    {
                        result.add(new ErrorToken("String does not end"));
                        return result;
                    }
                    else if(tempTokenClass.getToken() == TokenType.PUNCTUATION_DOUBLE_QUOTE){
                        break;
                    }
                    else {
                        resValue += tempValue;
                    }
                }
                result.add(new StringLiteralToken(resValue));
                i = j;
            }
            
            result.add(partsAndTokens.get(i));
        }

        return result;
    }

    private static Token getTokenByString(String part) {
        // Integer literal handler
        if (part.matches("\\d+"))
        {
            return new IntegerLiteralToken(Integer.parseInt(part));
        }

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
            default -> new IdentifierToken(part);
        };
    }
}
