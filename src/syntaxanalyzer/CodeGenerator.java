package syntaxanalyzer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.semantic.Type;

import java.util.List;

/**
 * Emits a tiny bootstrap class that forwards execution to the interpreter with the program source embedded.
 *
 * Think of {@code main.o}:
 * <pre>
 * class Main is
 *     this() is
 *         var i : Integer(0)
 *         while (i.Less(10)) loop
 *             i.print()
 *             i := i.Plus(1)
 *         end
 *     end
 * end
 * </pre>
 *
 * Instead of translating the whole loop into JVM bytecode, we store the exact text above inside the generated
 * {@code main_o.class}. The generated {@code public static void main(String[] args)} simply calls
 * {@code Interpreter.runSource("<source>", "Main")}. The interpreter reuses the parser to rebuild the AST at runtime
 * and performs the {@code while} loop by walking the AST. These bootstrap classes are intentionally tiny—two methods:
 * <ul>
 *     <li>a no-op default constructor (required by the JVM),</li>
 *     <li>{@code main}, which pushes the source and entry class name onto the stack and invokes the interpreter.</li>
 * </ul>
 */
public final class CodeGenerator {

    private static final String ENTRY_METHOD = "main";
    private static final String ENTRY_SIGNATURE = "([Ljava/lang/String;)V";

    public byte[] generate(String generatedClassName, String entryPointClassName,
                           String sourceCode, List<Cls> classes, List<Type> types) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String internalName = generatedClassName.replace('.', '/');
        writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalName, null, "java/lang/Object", null);

        emitDefaultConstructor(writer);
        emitMainMethod(writer, sourceCode, entryPointClassName);

        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void emitDefaultConstructor(ClassWriter writer) {
        MethodVisitor constructor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();
    }

    private static void emitMainMethod(ClassWriter writer, String sourceCode, String entryClass) {
        MethodVisitor main = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, ENTRY_METHOD, ENTRY_SIGNATURE, null, null);
        main.visitCode();
        // Stack: [] -> ["<program text>"]
        main.visitLdcInsn(sourceCode);
        // Stack: ["<program text>"] -> ["<program text>", "Main"]
        main.visitLdcInsn(entryClass);
        // Call the interpreter: Interpreter.runSource(source, entryClass)
        main.visitMethodInsn(Opcodes.INVOKESTATIC, "olang/runtime/Interpreter", "runSource",
                "(Ljava/lang/String;Ljava/lang/String;)V", false);
        main.visitInsn(Opcodes.RETURN);
        main.visitMaxs(0, 0);
        main.visitEnd();
    }
}
