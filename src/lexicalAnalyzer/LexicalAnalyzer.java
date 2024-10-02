package lexicalAnalyzer;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

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

    public static void main(String[] args) {
        List<String> fileParts = scanFile("/src/testCases/test.o");

        List<Object[]> stringsWithTokens = Tokenizer.partsToTokens(fileParts);

        for (Object[] sublist : stringsWithTokens) {
            String str = (String) sublist[0];
            Token enumValue = (Token) sublist[1];
            System.out.println("String: '" + str + "', Token type: " + enumValue.getToken());
        }
    }
}