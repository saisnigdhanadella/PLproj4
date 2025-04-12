package simplf;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

public class Simplf {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: simplf <sourcefile>");
            return;
        }
        runFile(args[0]);
    }

    public static void runFile(String path) throws IOException {
        String source = new String(Files.readAllBytes(Paths.get(path)));
        run(source);
    }

    private static void run(String source) {
        simplf.parser.simplfLexer lexer = new simplf.parser.simplfLexer(CharStreams.fromString(source));
        simplf.parser.simplfParser parser = new simplf.parser.simplfParser(new CommonTokenStream(lexer));
        simplf.parser.simplfParser.ProgramContext tree = parser.program();

        List<Stmt> statements = tree.val;

        Desugar desugarer = new Desugar();
        statements = desugarer.desugar(statements);

        Interpreter interpreter = new Interpreter();
        interpreter.interpret(statements);
    }

    public static void runtimeError(RuntimeError error) {
        System.err.println("[line " + error.token.line + "] RuntimeError: " + error.getMessage());
    }
}
