import lexicalanalyzer.LexicalAnalyzer;
import lexicalanalyzer.Token;
import lexicalanalyzer.TokenCode;
import org.junit.jupiter.api.Test;
import syntaxanalyzer.AST;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class ParserSmokeTests {

    private static final Path SMOKE_DIRECTORY = Paths.get("test", "smoke");

    @Test
    public void smokeFilesParseWithoutError() throws IOException {
        for (Path source : loadSmokeSources()) {
            assertDoesNotThrow(() -> {
                List<Token> tokens = LexicalAnalyzer.getTokens(source.toString());
                new AST().parse(tokens);
            }, "Parser failed for " + source);
        }
    }

    @Test
    public void smokeFilesProduceNoErrorTokens() throws IOException {
        for (Path source : loadSmokeSources()) {
            List<Token> tokens = LexicalAnalyzer.getTokens(source.toString());
            assertFalse(tokens.stream().anyMatch(token -> token.getToken() == TokenCode.ERROR),
                    "Lexer reported an error token for " + source);
        }
    }

    @Test
    public void stringInputTracksSpan() {
        List<Token> tokens = LexicalAnalyzer.getTokensFromString("var x : Integer");
        Token colon = tokens.stream()
                .filter(token -> token.getToken() == TokenCode.PUNCTUATION_SEMICOLON)
                .findFirst()
                .orElseThrow();

        assertEquals(1, colon.getSpan().lineNum());
        assertEquals(7, colon.getSpan().posBegin());
    }

    private static List<Path> loadSmokeSources() throws IOException {
        try (Stream<Path> stream = Files.list(SMOKE_DIRECTORY)) {
            return stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".o"))
                    .sorted()
                    .collect(Collectors.toList());
        }
    }
}
