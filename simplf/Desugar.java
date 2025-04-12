package simplf;

import java.util.ArrayList;
import java.util.List;

import simplf.Expr.*;
import simplf.Stmt.*;

public class Desugar implements Expr.Visitor<Expr>, Stmt.Visitor<Stmt> {

    public Desugar() {}

    public List<Stmt> desugar(List<Stmt> stmts) {
        ArrayList<Stmt> ret = new ArrayList<>();
        for (Stmt stmt : stmts) {
            ret.add(stmt.accept(this));
        }
        return ret;
    }

    @Override
    public Stmt visitPrintStmt(Print stmt) {
        return stmt;
    }

    @Override
    public Stmt visitExprStmt(Expression stmt) {
        return new Expression(stmt.expr.accept(this));
    }

    @Override
    public Stmt visitVarStmt(Var stmt) {
        Expr desugaredInit = (stmt.initializer == null) ? null : stmt.initializer.accept(this);
        return new Var(stmt.name, desugaredInit);
    }

    @Override
    public Stmt visitBlockStmt(Block stmt) {
        ArrayList<Stmt> new_statements = new ArrayList<>();
        for (Stmt oldStmt : stmt.statements) {
            new_statements.add(oldStmt.accept(this));
        }
        return new Block(new_statements);
    }

    @Override
    public Stmt visitIfStmt(If stmt) {
        Stmt newElse = (stmt.elseBranch == null) ? null : stmt.elseBranch.accept(this);
        return new If(stmt.cond.accept(this), stmt.thenBranch.accept(this), newElse);
    }

    @Override
    public Stmt visitWhileStmt(While stmt) {
        return new While(stmt.cond.accept(this), stmt.body.accept(this));
    }

    @Override
public Stmt visitForStmt(For stmt) {
    List<Stmt> desugared = new ArrayList<>();
    desugared.add(new Expression(stmt.init.accept(this)));

    List<Stmt> whileBody = new ArrayList<>();
    whileBody.add(stmt.body.accept(this));
    whileBody.add(new Expression(stmt.incr.accept(this)));

    Stmt whileStmt = new While(stmt.cond.accept(this), new Block(whileBody));
    desugared.add(whileStmt);

    return new Block(desugared);
}


    @Override
    public Stmt visitFunctionStmt(Function stmt) {
        ArrayList<Stmt> newBody = new ArrayList<>();
        for (Stmt oldStmt : stmt.body) {
            newBody.add(oldStmt.accept(this));
        }
        return new Function(stmt.name, stmt.params, newBody);
    }

    @Override
    public Expr visitBinary(Binary expr) {
        return new Binary(expr.left.accept(this), expr.op, expr.right.accept(this));
    }

    @Override
    public Expr visitUnary(Unary expr) {
        return new Unary(expr.op, expr.right.accept(this));
    }

    @Override
    public Expr visitLiteral(Literal expr) {
        return expr;
    }

    @Override
    public Expr visitGrouping(Grouping expr) {
        return new Grouping(expr.expression.accept(this));
    }

    @Override
    public Expr visitVarExpr(Variable expr) {
        return expr;
    }

    @Override
    public Expr visitAssignExpr(Assign expr) {
        return new Assign(expr.name, expr.value.accept(this));
    }

    @Override
    public Expr visitLogicalExpr(Logical expr) {
        return new Logical(expr.left.accept(this), expr.op, expr.right.accept(this));
    }

    @Override
    public Expr visitConditionalExpr(Conditional expr) {
        return new Conditional(expr.cond.accept(this),
                expr.thenBranch.accept(this),
                expr.elseBranch.accept(this));
    }

    @Override
    public Expr visitCallExpr(Call expr) {
        ArrayList<Expr> newArgs = new ArrayList<>();
        for (Expr arg : expr.args) {
            newArgs.add(arg.accept(this));
        }
        return new Call(expr.callee.accept(this), expr.paren, newArgs);
    }
}
