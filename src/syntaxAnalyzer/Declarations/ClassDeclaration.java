package syntaxAnalyzer.Declarations;

import java.util.ArrayList;
import java.util.List;
import syntaxAnalyzer.Declarations.Functions.ConstructorDeclaration;
import syntaxAnalyzer.Declarations.Functions.MethodDeclaration;
import syntaxAnalyzer.Declarations.Objects.VariableDeclatation;
import syntaxAnalyzer.Node;

public class ClassDeclaration extends Node {
    List<VariableDeclatation> attributes = new ArrayList<>();
    List<ConstructorDeclaration> constructors = new ArrayList<>();
    List<MethodDeclaration> methods = new ArrayList<>();
    
    public ClassDeclaration() {}

    public ClassDeclaration(List<VariableDeclatation> attributes, List<ConstructorDeclaration> constructors, List<MethodDeclaration> methods) {
        this.attributes = attributes;
        this.constructors = constructors;
        this.methods = methods;
    }

    @Override
    public Boolean validate() {

        for(VariableDeclatation elem : attributes)
        {
            if (!elem.validate() && (!attributes.isEmpty())) { return false;}
        }

        for(ConstructorDeclaration elem : constructors)
        {
            if(!elem.validate() && (!constructors.isEmpty())) { return false;}
        }

        for(MethodDeclaration elem : methods)
        {
            if(!elem.validate() && (!methods.isEmpty())) { return false;}
        }

        System.out.println("Class Declaration validation");
        return true;
    }
    
    // TODO: generate()
    @Override
    public void generate() {
        System.out.println("Class Declaration generation");
    }
    
    public List<VariableDeclatation> getAttributes() {
        return attributes;
    }
    
    public List<ConstructorDeclaration> getConstructors() {
        return constructors;
    }
    
    public List<MethodDeclaration> getMethods() {
        return methods;
    }   
    
    public void addAttribute(VariableDeclatation attribute) {
        attributes.add(attribute);
    }
    
    public void addConstructor(ConstructorDeclaration constructor) {
        constructors.add(constructor);
    }
    
    public void addMethod(MethodDeclaration method) {
        methods.add(method);
    }
}
