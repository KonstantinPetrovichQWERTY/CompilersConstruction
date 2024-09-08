import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LexicalAnalyzer {

    private static List<String> separateFileToArrayList(String filePath){
        List<String> parts = new ArrayList<>();
        List<Character> separators = List.of(',', '.', '!', '?', ' ', '(', ')', '\n', '{', '}', '[', ']', '\t');

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
            e.printStackTrace();
        }

        return parts;
    }

    private static List<Tokens> partsToTokens(List<String> parts){
        List<Tokens> result = new ArrayList<>();

        for (String part : parts){
            switch (part) {
                case "var": 
                    result.add(Tokens.ABSTRACT);
                    break;
                case ":":
                    System.out.println("It's a banana.");
                    break;
                default:
                    result.add(Tokens.AND);
                    break;
            }
        }

        return result;
    }
    
    public static void main(String[] args) {
        List<String> fileParts = separateFileToArrayList("testCases/booleanOperations.o");
        
        for (String part : fileParts) {
            System.out.println(part);
        }

        List<Tokens> tokens = partsToTokens(fileParts);

        for (Tokens token : tokens) {
            System.out.println(token);
        }
    }
}