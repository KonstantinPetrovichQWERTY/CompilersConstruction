package syntaxanalyzer;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import syntaxanalyzer.declarations.Cls;
import syntaxanalyzer.semantic.Type;

import java.util.List;

/**
 * Emits a tiny bootstrap class that forwards execution to the interpreter with the program source embedded.
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
        main.visitLdcInsn(sourceCode);
        main.visitLdcInsn(entryClass);
        main.visitMethodInsn(Opcodes.INVOKESTATIC, "olang/runtime/Interpreter", "runSource",
                "(Ljava/lang/String;Ljava/lang/String;)V", false);
        main.visitInsn(Opcodes.RETURN);
        main.visitMaxs(0, 0);
        main.visitEnd();
    }
}
