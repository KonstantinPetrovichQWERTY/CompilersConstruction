package syntaxanalyzer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import syntaxanalyzer.semantic.Type;

import java.util.List;

/**
 * Emits a very basic class that ignores the AST and only prints a placeholder message.
 */
public final class CodeGenerator {

    private static final String ENTRY_METHOD = "main";
    private static final String ENTRY_SIGNATURE = "([Ljava/lang/String;)V";
    private static final String MESSAGE = "CODEGEN NOT IMPLEMENTED";

    public byte[] generate(String className, List<syntaxanalyzer.declarations.Cls> classes, List<Type> types) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        String internalName = className.replace('.', '/');
        writer.visit(Opcodes.V21, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, internalName, null, "java/lang/Object", null);

        emitDefaultConstructor(writer, internalName);
        emitMainMethod(writer, internalName);

        writer.visitEnd();
        return writer.toByteArray();
    }

    private static void emitDefaultConstructor(ClassWriter writer, String owner) {
        MethodVisitor constructor = writer.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
        constructor.visitCode();
        constructor.visitVarInsn(Opcodes.ALOAD, 0);
        constructor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
        constructor.visitInsn(Opcodes.RETURN);
        constructor.visitMaxs(0, 0);
        constructor.visitEnd();
    }

    private static void emitMainMethod(ClassWriter writer, String owner) {
        MethodVisitor main = writer.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, ENTRY_METHOD, ENTRY_SIGNATURE, null, null);
        main.visitCode();
        main.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
        main.visitLdcInsn(MESSAGE);
        main.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
        main.visitInsn(Opcodes.RETURN);
        main.visitMaxs(0, 0);
        main.visitEnd();
    }
}
