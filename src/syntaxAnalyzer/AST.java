package syntaxAnalyzer;

import java.util.ArrayList;
import java.util.List;
import syntaxAnalyzer.Declarations.ClassDeclaration;

public class AST extends Node {

    List<Node> classDeclarations = new ArrayList<>();
    
    // TODO: RootNode constructor
    public AST() {
    }

    void addClassDeclaration(ClassDeclaration classDeclarationNode) {
        classDeclarationNode.validate();
        classDeclarationNode.generate();

        classDeclarations.add(classDeclarationNode);
    }

    @Override
    public Boolean validate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'validate'");
    }

    @Override
    public void generate() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generate'");
    }

}
