package syntaxanalyzer;

import java.util.List;
import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.declarations.Constructor;
import syntaxanalyzer.declarations.Method;
import syntaxanalyzer.declarations.Variable;

public class AstPrinter {

    public static void print(List<Cls> classes) {
        for (Cls cls : classes) {
            System.out.println("CLASS " + cls.getName());
            System.out.println("BASECLASS " + cls.getBaseClass());

            for (Constructor constructor : cls.getBody().getConstructors()) {
                System.out.println("CONSTRUCTOR " + constructor.getParameters());
            }

            for (Variable var : cls.getBody().getVariables()) {
                System.out.println(
                        "VARIABLE " + var.getName() + " " + var.getExpression()
                                .getPrimary()
                                .getPrimaryType()
                                .toString()
                );
            }

            for (Method method : cls.getBody().getMethods()) {
                System.out.println("METHOD " + method.getName() + " " + method.getParameters());
            }
        }
    }
}
