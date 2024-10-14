package syntaxAnalyzer;

import java.util.List;
import lexicalAnalyzer.Token;

abstract public class Node {

    abstract public Integer validate(List<Token> tokens, Integer index);
    abstract public Integer generate(List<Token> tokens, Integer index);
}
