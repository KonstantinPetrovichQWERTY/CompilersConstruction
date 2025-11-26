package syntaxanalyzer.declarations;

import java.util.List;

import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;

abstract public class Declaration {
    
        /**
     * Parses the given list of tokens starting from the specified index.
     *
     * @param tokens the list of tokens to parse
     * @param index the starting index for parsing (0 by default)
     * @return the updated index after parsing -- last parsed index
     */
    abstract public Integer parse(List<Token> tokens, Integer index);

    /**
     * this method return the index of the token
     * of the last element of the declaration
     **/
    public Integer parse(List<Token> tokens) {

        return parse(tokens, 0);
    }

    /** Check if the current token on the index position
     * otherwise throw exception.
     * return TOKEN;
     */
    public Token ensureToken(List<Token> tokens, Integer index, TokenCode tokenType) {
        if (tokens.get(index).getToken() == tokenType) {
            index += 1;
        } else {
            throw new RuntimeException(
                    "Expected " +
                    tokenType.toString() +
                    " but found " +
                    tokens.get(index).getToken()
            );
        }
        return tokens.get(index-1);
    }
}
