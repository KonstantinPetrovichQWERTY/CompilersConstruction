import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

    private static List<String> separateFileToArrayList(String filePath){
        List<String> parts = new ArrayList<>();
        List<Character> separators = List.of(',', '.', '"', ' ', '(', ')', '\n', '[', ']', '\t');

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder currentPart = new StringBuilder();
            int currentChar;

            while ((currentChar = reader.read()) != -1) {
                char ch = (char) currentChar;

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
            }

        } catch (IOException e) {
            System.out.println("IOException");
        }

        return parts;
    }

    public static void main(String[] args) {
        List<String> fileParts = separateFileToArrayList("D:/Innopolis/ucheba/compilers/CompilersConstruction/lexicalAnalyzer/src/testCases/complexExpression.o");
        
        // for (String part : fileParts) {
        //     System.out.println(part);
        // }

        List<Object[]> stringsWithTokens = Tokenizer.partsToTokens(fileParts);

        // for (Object[] sublist : stringsWithTokens) {
        //     String str = (String) sublist[0];
        //     Tokens enumValue = (Tokens) sublist[1];
        //     System.out.println("String: " + str + ", Enum: " + enumValue);
        // }
    }
}