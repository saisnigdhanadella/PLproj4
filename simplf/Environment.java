package simplf;

class Environment {
    private final AssocList vars;
    private final Environment enclosing;

    Environment() {
        this.vars = null;
        this.enclosing = null;
    }

    Environment(Environment enclosing) {
        this.vars = null;
        this.enclosing = enclosing;
    }

    Environment(AssocList assocList, Environment enclosing) {
        this.vars = assocList;
        this.enclosing = enclosing;
    }

    Environment define(Token varToken, String name, Object value) {
        AssocList newAssoc = new AssocList(name, value, this.vars);
        return new Environment(newAssoc, this.enclosing);
    }

    void assign(Token name, Object value) {
        for (AssocList node = vars; node != null; node = node.next) {
            if (node.name.equals(name.lexeme)) {
                node.value = value;
                return;
            }
        }
        if (enclosing != null) {
            enclosing.assign(name, value);
            return;
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }

    Object get(Token name) {
        for (AssocList node = vars; node != null; node = node.next) {
            if (node.name.equals(name.lexeme)) {
                return node.value;
            }
        }
        if (enclosing != null) {
            return enclosing.get(name);
        }
        throw new RuntimeError(name, "Undefined variable '" + name.lexeme + "'.");
    }
}
