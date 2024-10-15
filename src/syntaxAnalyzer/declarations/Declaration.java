package syntaxanalyzer.declarations;

import java.util.List;

import lexicalanalyzer.Token;

abstract public class Declaration {
    
        /**
     * Parses the given list of tokens starting from the specified index.
     *
     * @param tokens the list of tokens to parse
     * @param index the starting index for parsing (0 by default)
     * @return the updated index after parsing -- last parsed index
     */
    abstract public Integer parse(List<Token> tokens, Integer index);


    public Integer parse(List<Token> tokens) {
        return parse(tokens, 0);
    }
}
