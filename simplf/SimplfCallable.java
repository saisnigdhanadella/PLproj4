package simplf;

import java.util.List;

interface SimplfCallable {
    int arity();
    Object call(Interpreter interpreter, List<Object> arguments);
}
