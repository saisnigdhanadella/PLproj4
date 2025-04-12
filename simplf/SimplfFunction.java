package simplf;

import java.util.List;

class SimplfFunction implements SimplfCallable {
    private final Stmt.Function declaration;
    private final Environment closure;

    SimplfFunction(Stmt.Function declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int arity() {
        return declaration.params.size();
    }

    @Override
    public Object call(Interpreter interpreter, List<Object> args) {
        Environment localEnv = closure;
        for (int i = 0; i < declaration.params.size(); i++) {
            Token param = declaration.params.get(i);
            localEnv = localEnv.define(param, param.lexeme, args.get(i));
        }

        Environment previous = interpreter.environment;
        try {
            interpreter.environment = localEnv;
            for (Stmt stmt : declaration.body) {
                interpreter.execute(stmt);
            }
        } finally {
            interpreter.environment = previous;
        }

        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.name.lexeme + ">";
    }
}
