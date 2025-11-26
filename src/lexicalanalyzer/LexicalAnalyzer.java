package lexicalanalyzer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class LexicalAnalyzer {

    public static List<Token> getTokens(String filePath) {
        try (Stream<String> lines = Files.lines(Path.of(filePath))) {
            return getTokensFromLineStream(lines);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read file: " + filePath, e);
        }
    }

    public static List<Token> getTokensFromString(String content) {
        try (BufferedReader reader = new BufferedReader(new StringReader(content))) {
            return getTokensFromLineStream(reader.lines());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read provided string content", e);
        }
    }

    public static List<Token> getTokensFromLines(List<String> lines) {
        return getTokensFromLineStream(lines.stream());
    }

    private static List<Token> getTokensFromLineStream(Stream<String> lines) {
        ScanResult scanResult = scanLines(lines);
        return Tokenizer.partsToTokens(scanResult.fragments(), scanResult.eofSpan());
    }

    private static ScanResult scanLines(Stream<String> lines) {
        List<LexicalFragment> fragments = new ArrayList<>();
        AtomicInteger lastLine = new AtomicInteger(0);
        AtomicInteger currentLine = new AtomicInteger(1);

        lines.forEach(line -> processLine(line, currentLine.getAndIncrement(), fragments, lastLine));

        int finalLine = lastLine.get();
        Token.Span eofSpan = new Token.Span(finalLine + 1, 1);
        return new ScanResult(fragments, eofSpan);
    }

    private static void processLine(String line, int lineNumber, List<LexicalFragment> fragments,
                                    AtomicInteger lastLine) {
        StringBuilder currentPart = new StringBuilder();
        int column = 1;
        int partStart = 1;
        boolean insideString = false;

        for (int index = 0; index < line.length(); ) {
            char ch = line.charAt(index);

            if (!insideString && ch == '/' && index + 1 < line.length() && line.charAt(index + 1) == '/') {
                flushCurrentPart(fragments, currentPart, lineNumber, partStart);
                column = line.length() + 1;
                break;
            }

            if (isSeparator(ch)) {
                flushCurrentPart(fragments, currentPart, lineNumber, partStart);
                if (ch != '\r') {
                    fragments.add(new LexicalFragment(String.valueOf(ch), new Token.Span(lineNumber, column)));
                }
                if (ch == '"') {
                    insideString = !insideString;
                }
                column++;
                index++;
                partStart = column;
                continue;
            }

            if (currentPart.isEmpty()) {
                partStart = column;
            }
            currentPart.append(ch);
            column++;
            index++;
        }

        flushCurrentPart(fragments, currentPart, lineNumber, partStart);
        fragments.add(new LexicalFragment("\n", new Token.Span(lineNumber, column)));
        lastLine.set(lineNumber);
    }

    private static void flushCurrentPart(List<LexicalFragment> fragments, StringBuilder currentPart,
                                         int lineNumber, int startColumn) {
        if (currentPart.length() > 0) {
            fragments.add(new LexicalFragment(currentPart.toString(), new Token.Span(lineNumber, startColumn)));
            currentPart.setLength(0);
        }
    }

    private static boolean isSeparator(char ch) {
        return ch == ' ' || ch == ',' || ch == '.' || ch == '"' || ch == '(' || ch == ')' ||
               ch == '[' || ch == ']' || ch == '\t' || ch == ';' || ch == ':' || ch == '=' || ch == '\r';
    }

    private record ScanResult(List<LexicalFragment> fragments, Token.Span eofSpan) {}

    public static void main(String[] args) {
        List<Token> tokens = getTokens("src/test/testOLang/methodsOverriding.o");

        for (Token token : tokens) {
            if (token.getToken() == TokenCode.PUNCTUATION_TABULATION) {
                System.out.println("String: '" + "\\t" + "', Token type: " + token.getToken());
            } else if (token.getToken() == TokenCode.PUNCTUATION_LINE_BREAK) {
                System.out.println("String: '" + "\\n" + "', Token type: " + token.getToken());
            } else {
                System.out.println("String: '" + token.getLexeme() + "', Token type: " + token.getToken());
            }
        }
    }
}
