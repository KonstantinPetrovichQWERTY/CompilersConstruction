# Compilers Construction

This project is an implementation of the F24 Innopolis Compilers Course.

Our assigment is:

> **Source language:** Object-Oriented  
> **Implementation:** Java  
> **Lexer and parser:** hand-written  
> **Bytecode generation:** ASM  
> **Target platform:** JVM  

## Setup

1. Ensure you have Ubuntu **or** use **Windows Subsystem for Linux** from Windows:

```bash
wsl
```

2. Install Gradle build tool and Java 21 SDK:

```bash
sudo apt update
sudo snap install gradle --classic
sudo apt install openjdk-21-jdk
java --version
# openjdk 21.0.8 2025-07-15
# OpenJDK Runtime Environment (build 21.0.8+9-Ubuntu-0ubuntu122.04.1)
# OpenJDK 64-Bit Server VM (build 21.0.8+9-Ubuntu-0ubuntu122.04.1, mixed mode, sharing)
```

3. Clone the repository:

```bash
git clone https://github.com/KonstantinPetrovichQWERTY/CompilersConstruction/
cd CompilersConstruction
```

4. Install the Red Hat Java Language Server and related tools for VS Code:
[vscode-java-pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)

## Setting Up Vala Syntax Highlighting in VS Code for Project O source files

To enable syntax highlighting for Vala in Visual Studio Code:

1. Add the following configuration to your `.vscode/settings.json` file:

```json
{
  "files.associations": {
    "*.o": "vala",
    "*.io": "vala"
  }
}
```

2. Install the [Vala syntax highlighting](https://marketplace.visualstudio.com/items?itemName=prince781.vala) extension in VS Code.

After this setup, files with `.o` and `.io` extensions will use Vala syntax highlighting in the editor.

## Usage

Build the Project
```bash
./gradlew build
```

Install the CLI script

```bash
./gradlew installDist 
```
Tokenize source to inspect raw lexer output:
```bash
./o tokenize test/smoke/helloWorld.o
# 1. KEYWORD_CLASS class
# 2. IDENTIFIER HelloWorld
# ...
# filter out whitespace-like tokens
./o tokenize test/smoke/helloWorld.o --filter
```
Run it via the bundled `o` script:
```bash
 ./o parse test/smoke/helloWorld.o
# CLASS HelloWorld
# BASECLASS null
# CONSTRUCTOR []
```


Available commands:

- `./o tokenize <file> [--filter]` – run only the lexer and print numbered tokens (optionally skipping whitespace).
- `./o parse <file>` – lexical + syntax analysis, then prints the AST.
- `./o build <file>` – compile file and provide `.class` file.
- `./o run <file>` – compile and run.

## Runtime architecture (how the code works)

1. **Entry point – `syntaxanalyzer.OCompiler.main`**  
   The `o` CLI calls this class with the command (`run`, `build`, etc.) and the target `.o` file.

2. **Lexing and parsing**  
   `LexicalAnalyzer` converts the source into tokens, the recursive-descent parser (`SyntaxAnalyzer`) produces an AST (`syntaxanalyzer.declarations.*`), and `SemanticAnalyzer` currently only ensures the file is not empty (it returns `UnknownType` placeholders; we do not yet have a full static type system).

3. **Bytecode generation (ASM bootstrap)**  
   `src/syntaxanalyzer/CodeGenerator.java` uses ASM to emit a minimal JVM class that embeds the original source code as a string literal. Its `main` method invokes `olang.runtime.Interpreter.runSource(source, entryClass)`—ASM is now used only to create this wrapper class, not to lower every AST node.

4. **Runtime/interpreter**  
   `olang.runtime.Interpreter` re-parses the embedded source (reusing the lexer/parser), builds runtime class metadata, and interprets the AST. It implements value objects for Integer/Real/Boolean/String literals plus user-defined instances, handles assignments, loops, method dispatch, and prints. When expressions call `.print()`, the interpreter calls `System.out.println` on the underlying value.

### Type system status
- No static type checker yet: `SemanticAnalyzer` returns `UnknownType`. All actual behavior is resolved dynamically in the interpreter. Adding a real type system is left for future work.

### Error reporting flow
- Before lexing/parsing, `SyntaxException.setCurrentSource(filePath)` is called. Every token carries a `Token.Span` (line number + column). When the parser encounters an unexpected token, it calls `SyntaxException.at(message, token)`, which uses the stored span to produce the nicely formatted `path:line:column` message seen in the CLI output.  
- Interpreter/runtime mismatches raise plain Java exceptions (e.g., `IllegalStateException` when a method is missing). Parser errors therefore stay user-friendly thanks to the span data, while interpreter errors surface as stack traces pointing to the runtime code.

## Testing
To run the full Gradle build and unit tests:

```bash
./gradlew build test --info
```
