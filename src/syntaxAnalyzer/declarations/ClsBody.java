package syntaxAnalyzer.declarations;

import java.util.ArrayList;
import java.util.Map;
import java.util.Hashtable;
import java.util.List;
import lexicalAnalyzer.Token;
import lexicalAnalyzer.TokenType;

public class ClsBody extends Declaration {
    private Cls cls;

    List<Constructor> constructors = new ArrayList<>();
    List<Method> methods = new ArrayList<>();
    List<Variable> variables = new ArrayList<>();

    Map<String, Token> variablesPairs = new Hashtable<>();

    public ClsBody(Cls cls) {
        this.cls = cls;
    }

    public Cls getCls() {
        return cls;
    }

    public List<Variable> getVariables() {
        return variables;
    }

    public Map<String, Token> getVariablesPairs() {
        return variablesPairs;
    }
    
    public Token getVariableTokenByName(String name) {
        return variablesPairs.get(name);
    }
    
    public List<Variable> addVariable(Variable var) {
        variables.add(var);

        String name = var.getName();
        Token tkn = var.getExpression().getExprToken();
        variablesPairs.put(name, tkn);

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
                Variable variable = new Variable(cls);
                index = variable.parse(tokens, index);
                addVariable(variable);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_THIS) {
                Constructor constructor = new Constructor(cls);
                index = constructor.parse(tokens, index);
                constructors.add(constructor);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_METHOD) {
                Method method = new Method(cls);
                index = method.parse(tokens, index);
                methods.add(method);
            }
            else if (currentToken.getToken() == TokenType.KEYWORD_END) {
                return index;
            }
            else {
                throw new RuntimeException("Unexpected token in class body: " + currentToken.getToken() + " : " + currentToken.getValue() + " on the " + index);
            }
        }
        throw new RuntimeException("Unexpected end of tokens while parsing class body");
    }
}
