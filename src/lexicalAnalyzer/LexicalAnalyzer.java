package lexicalAnalyzer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

    // private String fileName;
    // public LexicalAnalyzer(String fileName) {
    //     this.fileName = fileName;
    // }
    
    private static List<String> scanFile(String filePath){
        List<String> parts = new ArrayList<>();
        List<Character> separators = List.of(',', '=', '.', '"', ' ', '(', ')', '\0', '\n', '[', ']', '\t', ';', ':');

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder currentPart = new StringBuilder();
            String currentString;

            while ((currentString = reader.readLine()) != null) {
                if (currentString.startsWith("//")){
                    continue;
                }

                char[] charList = currentString.toCharArray();
                for (char ch: charList){
                    if (separators.contains(ch)) {
                        if (currentPart.length() > 0) {
                            parts.add(currentPart.toString());
                            currentPart.setLength(0);
                        }
                        parts.add(String.valueOf(ch));
                    } else {
                        currentPart.append(ch);
                    }
                }
                if (currentPart.length() > 0) {
                    parts.add(currentPart.toString());
                    currentPart.setLength(0);
                }
                parts.add("\n");
            }
            
        } catch (IOException e) {
            System.out.println("IOException");
        }

        return parts;
    }


    public static List<Token> getTokens(String filePath) {
        List<String> fileParts = scanFile(filePath);
        
        List<Token> tokens = Tokenizer.partsToTokens(fileParts);

        return tokens;
    }

    public static void main(String[] args) {
        List<String> fileParts = scanFile("src/test/testOLang/methodsOverriding.o");
        
        List<Token> tokens = Tokenizer.partsToTokens(fileParts);

        for (Token token : tokens) {
            if(token.getToken() == TokenType.PUNCTUATION_TABULATION)
            {
                System.out.println("String: '" + "\\t" + "', Token type: " + token.getToken());
            }
            else if (token.getToken() == TokenType.PUNCTUATION_LINE_BREAK)
            {
                System.out.println("String: '" + "\\n" + "', Token type: " + token.getToken());
            }
            else {
                System.out.println("String: '" + token.getValue() + "', Token type: " + token.getToken());
            }
        }
    }
}
