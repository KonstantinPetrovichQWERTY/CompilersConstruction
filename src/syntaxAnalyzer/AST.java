package syntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;
import lexicalAnalyzer.Token;

public class AST extends Node {

    List<Node> classDeclarations = new ArrayList<>();

    // TODO: RootNode constructor
    public AST() {
    }

    @Override
    public Integer validate(List<Token> tokens, Integer index) {
        while(index + 1 < tokens.size()){
            System.out.println(index + " / " + tokens.size());
            Node tempClassDeclaration = new ClassDeclaration();
            index = tempClassDeclaration.validate(tokens, index);
            // System.out.println(index + " / " + tokens.size());
        }
        return index;
    }

    @Override
    public Integer generate(List<Token> tokens, Integer index) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generate'");
    }

}
