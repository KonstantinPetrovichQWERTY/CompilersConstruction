# Compilers Construction

## Setting Up Vala Syntax Highlighting in VS Code

To enable syntax highlighting for Vala in Visual Studio Code, follow these steps:

1. Add the following configuration to your `.vscode/settings.json` file:

    ```json
    {
      "files.associations": {
        "*.o": "vala",
        "*.io": "vala"
      }
    }
    ```

2. Install the Vala syntax highlighting extension in VS Code.

Once this is set up, files with `.o` and `.io` extensions will be nicely highlighted with Vala syntax colors in your editor.