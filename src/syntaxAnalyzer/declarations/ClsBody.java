package syntaxAnalyzer.declarations;

import java.util.ArrayList;
import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class ClsBody extends Declaration {
    List<Constructor> constructors = new ArrayList<>();
    List<Method> methods = new ArrayList<>();
    List<Variable> variables = new ArrayList<>();

    public List<Variable> getVariables() {
        return variables;
    }

    public List<Method> getMethods() {
        return methods;
    }

    public List<Constructor> getConstructors() {
        return constructors;
    }

    @Override
    public Integer parse(List<Token> tokens, Integer index) {
        while (index < tokens.size()) {
            Token currentToken = tokens.get(index);

            if (currentToken.getToken() == TokenType.KEYWORD_VAR) {
                Variable variable = new Variable();
                index = variable.parse(tokens, index);
                variables.add(variable);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_THIS) {
                Constructor constructor = new Constructor();
                index = constructor.parse(tokens, index);
                constructors.add(constructor);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_METHOD) {
                Method method = new Method();
                index = method.parse(tokens, index);
                methods.add(method);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_END) {
                return index;
            }
            else {
                throw new RuntimeException("Unexpected token in class body: " + currentToken.getToken() + " on the " + index);
            }

            index += 1;
        }
        throw new RuntimeException("Unexpected end of tokens while parsing class body");
    }
}
