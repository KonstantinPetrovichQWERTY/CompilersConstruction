# Compilers Construction

This project is an implementation of the F24 Innopolis Compilers Course.

Our assigment is:

> **Source language:** Object-Oriented  
> **Implementation:** Java  
> **Lexer and parser:** hand-written  
> **Bytecode generation:** ASM  
> **Target platform:** JVM  

## Development Setup

1. Ensure you have Ubuntu **or** use **Windows Subsystem for Linux** from Windows:

```bash
wsl
```

2. Install Java 21 SDK:

```bash
sudo apt update
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

## Setting Up Vala Syntax Highlighting in VS Code

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
