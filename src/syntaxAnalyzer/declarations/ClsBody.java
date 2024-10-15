package syntaxanalyzer.declarations;

import java.util.ArrayList;
import java.util.List;
import lexicalanalyzer.Token;

public class ClsBody extends Declaration {
    List<Constructor> constructors = new ArrayList<>();
    List<Method> methods = new ArrayList<>();
    List<Variable> variables = new ArrayList<>();


    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        return index;
//        throw new UnsupportedOperationException("Not supported yet.");
    }

}
