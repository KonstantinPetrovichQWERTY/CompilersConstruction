package syntaxanalyzer.declarations;

import java.util.List;
import lexicalanalyzer.Token;

public class Cls extends Declaration {
    String name;
    String baseClass;

    ClsBody body;

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
