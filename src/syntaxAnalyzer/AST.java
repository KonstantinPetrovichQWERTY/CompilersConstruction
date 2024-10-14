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
    public Integer parse(List<Token> tokens, Integer index) {
        while(index + 1 < tokens.size()){
            System.out.println(index + " / " + tokens.size());
            Node tempClassDeclaration = new ClassDeclaration();
            index = tempClassDeclaration.parse(tokens, index);
            classDeclarations.addLast(tempClassDeclaration);
            // System.out.println(index + " / " + tokens.size());
        }
        return index;
    }


}
