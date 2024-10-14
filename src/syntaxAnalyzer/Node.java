package syntaxAnalyzer;

import java.util.List;
import lexicalAnalyzer.Token;

abstract public class Node {

    abstract public Integer parse(List<Token> tokens, Integer index);
}
